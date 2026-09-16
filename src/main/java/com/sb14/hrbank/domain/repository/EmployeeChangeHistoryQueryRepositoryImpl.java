package com.sb14.hrbank.domain.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sb14.hrbank.domain.entity.employeehistory.ChangeLogSortField;
import com.sb14.hrbank.domain.entity.employeehistory.EmployeeChangeHistory;
import com.sb14.hrbank.domain.entity.employeehistory.EmployeeChangeHistoryType;
import com.sb14.hrbank.web.controller.dto.ChangeLogSearchRequest;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.SortDirection;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.sb14.hrbank.domain.entity.employee.QEmployee.employee;
import static com.sb14.hrbank.domain.entity.employeehistory.QEmployeeChangeDetail.employeeChangeDetail;
import static com.sb14.hrbank.domain.entity.employeehistory.QEmployeeChangeHistory.employeeChangeHistory;
import static com.sb14.hrbank.domain.entity.metafile.QMetaFile.metaFile;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class EmployeeChangeHistoryQueryRepositoryImpl implements EmployeeChangeHistoryQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<EmployeeChangeHistory> findAllByCondition(ChangeLogSearchRequest request) {
        return queryFactory
                .selectFrom(employeeChangeHistory)
                .join(employeeChangeHistory.employee, employee).fetchJoin()
                .where(
                        employeeNumberContains(request.employeeNumber()),
                        typeEquals(request.type()),
                        memoContains(request.memo()),
                        ipAddressContains(request.ipAddress()),
                        atGreaterThanOrEqual(request.atFrom()),
                        atLessThanOrEqual(request.atTo()),
                        cursorCondition(
                                request.cursor(),
                                request.idAfter(),
                                request.sortField(),
                                isDescending(request.sortDirection())
                        )
                )
                .orderBy(
                        primarySortBySortField(
                                request.sortField(),
                                isDescending(request.sortDirection())
                        ),
                        secondarySortById(
                                isDescending(request.sortDirection())
                        )
                )
                .limit(request.size() + 1)
                .fetch();
    }

    @Override
    public Long countByCondition(ChangeLogSearchRequest request) {
        Long totalElements = queryFactory
                .select(employeeChangeHistory.count())
                .from(employeeChangeHistory)
                .join(employeeChangeHistory.employee, employee)
                .where(
                        employeeNumberContains(request.employeeNumber()),
                        typeEquals(request.type()),
                        memoContains(request.memo()),
                        ipAddressContains(request.ipAddress()),
                        atGreaterThanOrEqual(request.atFrom()),
                        atLessThanOrEqual(request.atTo())
                )
                .fetchOne();

        return Objects.nonNull(totalElements) ? totalElements : 0;
    }

    @Override
    public Optional<EmployeeChangeHistory> findDetailById(Long id) {
        EmployeeChangeHistory result = queryFactory
                .selectFrom(employeeChangeHistory)
                // 수정 이력 -> 직원
                .join(employeeChangeHistory.employee, employee)
                .fetchJoin()
                // 직원 -> 프로필 이미지
                .leftJoin(employee.profileImage, metaFile)
                .fetchJoin()
                // 수정 이력 -> 수정 이력 상세
                .leftJoin(employeeChangeHistory.diffs, employeeChangeDetail)
                .fetchJoin()
                // 요청 이력 ID
                .where(employeeChangeHistory.id.eq(id))
                // Join으로 인한 History 중복 제거
                .distinct()
                // 하나만 조회
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Long countByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        Long count = queryFactory
                .select(employeeChangeHistory.count())
                .from(employeeChangeHistory)
                .where(
                        employeeChangeHistory.updatedAt.goe(fromDate),
                        employeeChangeHistory.updatedAt.loe(toDate)
                )
                .fetchOne();

        return Objects.nonNull(count) ? count : 0L;
    }

    /* where절 */
    private BooleanExpression employeeNumberContains(String employeeNumber) {
        return hasText(employeeNumber) ? employee.employeeNumber.containsIgnoreCase(employeeNumber) : null;
    }

    private BooleanExpression typeEquals(EmployeeChangeHistoryType type) {
        return Objects.nonNull(type) ? employeeChangeHistory.type.eq(type) : null;
    }

    private BooleanExpression memoContains(String memo) {
        return hasText(memo) ? employeeChangeHistory.memo.containsIgnoreCase(memo) : null;
    }

    private BooleanExpression ipAddressContains(String ipAddress) {
        return hasText(ipAddress) ? employeeChangeHistory.ipAddress.containsIgnoreCase(ipAddress) : null;
    }

    private BooleanExpression atGreaterThanOrEqual(LocalDateTime atFrom) {
        return Objects.nonNull(atFrom) ? employeeChangeHistory.updatedAt.goe(atFrom) : null;
    }

    private BooleanExpression atLessThanOrEqual(LocalDateTime atTo) {
        return Objects.nonNull(atTo) ? employeeChangeHistory.updatedAt.loe(atTo) : null;
    }

    /* 정렬 */
    private boolean isDescending(SortDirection sortDirection) {
        return sortDirection == SortDirection.DESCENDING;
    }

    /* 1차 정렬 */
    private OrderSpecifier<?> primarySortBySortField(ChangeLogSortField sortField, boolean isDescending) {
        return switch (sortField) {
            case IP_ADDRESS ->
                isDescending ? employeeChangeHistory.ipAddress.desc() : employeeChangeHistory.ipAddress.asc();

            case AT ->
                isDescending ? employeeChangeHistory.updatedAt.desc() : employeeChangeHistory.updatedAt.asc();
        };
    }

    /* 2차 정렬 */
    private OrderSpecifier<?> secondarySortById(boolean isDescending) {
        return isDescending ? employeeChangeHistory.id.desc() : employeeChangeHistory.id.asc();
    }

    /* Cursor */
    private BooleanExpression cursorCondition(String cursor, Long idAfter, ChangeLogSortField sortField, boolean isDescending) {
        if (!hasText(cursor) && Objects.isNull(idAfter)) {
            return null;
        }

        return switch (sortField) {
            case IP_ADDRESS ->
                cursorCondition(
                        employeeChangeHistory.ipAddress,
                        cursor,
                        idAfter,
                        isDescending
                );

            case AT -> {
                LocalDateTime cursorDateTime;

                try {
                    cursorDateTime = LocalDateTime.parse(cursor);

                    yield cursorCondition(
                            employeeChangeHistory.updatedAt,
                            cursorDateTime,
                            idAfter,
                            isDescending
                    );
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Cursor 형식이 올바르지 않습니다.");
                }
            }
        };
    }

    // 결정된 컬럼을 실제 비교
    private <T extends Comparable<?>> BooleanExpression cursorCondition(
            ComparableExpression<T> employeeChangeHistoryColumn,
            T cursorValue,
            Long idAfter,
            boolean isDescending
    ) {
        // 내림차순
        if (isDescending) {
            return employeeChangeHistoryColumn.lt(cursorValue)
                    .or(
                            employeeChangeHistoryColumn
                                    .eq(cursorValue)
                                    .and(employeeChangeHistory.id.lt(idAfter))
                    );
        }

        // 오름차순
        return employeeChangeHistoryColumn.gt(cursorValue)
                .or(
                        employeeChangeHistoryColumn
                                .eq(cursorValue)
                                .and(employeeChangeHistory.id.gt(idAfter))
                );
    }
}
