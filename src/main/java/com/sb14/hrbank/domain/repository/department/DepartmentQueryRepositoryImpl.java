package com.sb14.hrbank.domain.repository.department;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sb14.hrbank.domain.entity.department.Department;
import com.sb14.hrbank.domain.entity.department.DepartmentWithEmployeeCount;
import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import com.sb14.hrbank.domain.service.department.DepartmentSearchCondition;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.sb14.hrbank.domain.entity.department.QDepartment.department;
import static com.sb14.hrbank.domain.entity.employee.QEmployee.employee;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class DepartmentQueryRepositoryImpl implements DepartmentQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<DepartmentWithEmployeeCount> findPageByCondition(DepartmentSearchCondition condition) {
        boolean descending = isDescending(condition.getSortDirection());
        NumberExpression<Long> employeeCount = employee.id.count();

        List<Tuple> results = queryFactory
                .select(department, employeeCount)
                .from(department)
                .leftJoin(employee)
                .on(employee.department.eq(department))
                .where(
                        nameOrDescriptionContains(condition.getNameOrDescription()),
                        cursorCondition(
                                condition.getCursor(),
                                condition.getIdAfter(),
                                condition.getSortField(),
                                descending
                        )
                )
                .groupBy(department)
                .orderBy(
                        // 1차 정렬: 사용자가 선택한 정렬 필드로
                        primarySortBySortField(
                                condition.getSortField(),
                                descending
                        ),
                        // 2차 정렬: 부서 id로
                        secondarySortById(descending)
                )
                .limit(condition.getSize() + 1)
                .fetch();

        return results.stream()
                .map(tuple ->
                        DepartmentWithEmployeeCount.of(
                                Objects.requireNonNull(tuple.get(department)),
                                Objects.requireNonNullElse(tuple.get(employeeCount), 0L)
                        ))
                .toList();
    }

    @Override
    public Optional<DepartmentWithEmployeeCount> findByIdWithEmployeeCount(Long departmentId) {
        NumberExpression<Long> employeeCount = employee.id.count();

        Tuple result = queryFactory
                .select(department, employeeCount)
                .from(department)
                .leftJoin(employee)
                .on(employee.department.eq(department))
                .where(department.id.eq(departmentId))
                .groupBy(department)
                .fetchOne();

        if (Objects.isNull(result)) {
            return Optional.empty();
        }

        return Optional.of(
                DepartmentWithEmployeeCount.of(
                        Objects.requireNonNull(result.get(department)),
                        Objects.requireNonNullElse(result.get(employeeCount), 0L)
                )
        );
    }

    @Override
    public long countByCondition(DepartmentSearchCondition condition) {
        Long totalElements = queryFactory
                .select(department.count())
                .from(department)
                .where(nameOrDescriptionContains(condition.getNameOrDescription()))
                .fetchOne();

        return Objects.requireNonNullElse(totalElements, 0L);
    }


// ===================================================================================================

    /* where 절 */
    private BooleanExpression nameOrDescriptionContains(String keyword) {
        return hasText(keyword)
                ? department.name.containsIgnoreCase(keyword)
                  .or(department.description.containsIgnoreCase(keyword))
                : null;
    }

    /* 정렬 */
    private boolean isDescending(String sortDirection) {
        return sortDirection.equals("desc");
    }
    // 1차 정렬 - 사용자가 선택한 것에 대해서
    private OrderSpecifier<?> primarySortBySortField(String sortField, boolean isDescending) {
        return switch (sortField) {
            case "name" ->
                isDescending
                        ? department.name.desc()
                        : department.name.asc();
            case "establishedDate" ->
                isDescending
                        ? department.establishedDate.desc()
                        : department.establishedDate.asc();
            default ->
                throw new HrBankException(
                        HrBankExceptionType.ILLEGAL_SORT_FIELD,
                        sortField
                );
        };
    }
    // 2차 정렬 - 그 안에서 한번 더 id로 정렬
    private OrderSpecifier<?> secondarySortById(boolean isDescending) {
        return isDescending
                ? department.id.desc()
                : department.id.asc();
    }



    /* 커서 */
    private BooleanExpression cursorCondition(
            String cursor,
            Long idAfter,
            String sortField,
            boolean isDescending
    ) {
        if (!hasText(cursor) && Objects.isNull(idAfter)) return null;   // 첫번째 페이지일때

        return switch (sortField) {
            // sortField 따라 비교할 컬럼 지정한다
            case "name" ->
                cursorCondition(
                        department.name,    // department의 name 컬럼 지정
                        cursor,
                        idAfter,
                        isDescending
                );
            case "establishedDate" -> {
                LocalDate cursorDate = null;
                try {
                    cursorDate = LocalDate.parse(cursor);   // String으로 LocalDate가 들어오기 때문에 파싱 작업해주기
                    yield cursorCondition(
                            department.establishedDate,
                            cursorDate,
                            idAfter,
                            isDescending
                    );
                } catch (DateTimeParseException e) {
                    throw new HrBankException(
                            HrBankExceptionType.ILLEGAL_DATE_FORMAT,
                            cursor
                    );
                }
            }
            default ->
                throw new HrBankException(
                        HrBankExceptionType.ILLEGAL_SORT_FIELD,
                        sortField
                );
        };
    }

    // 실제 비교
    private <T extends Comparable<?>> BooleanExpression cursorCondition(
            ComparableExpression<T> departmentColumn,
            T cursorValue,
            Long idAfter,
            boolean isDescending
    ) {
        if (Objects.isNull(idAfter)) {
            return isDescending
                    ? departmentColumn.lt(cursorValue)
                    : departmentColumn.gt(cursorValue);
        }

        return isDescending
                ? departmentColumn
                  .lt(cursorValue)
                  .or(
                          departmentColumn
                          .eq(cursorValue)
                          .and(department.id.lt(idAfter))
                  )
                : departmentColumn
                  .gt(cursorValue)
                   .or(
                           departmentColumn
                                   .eq(cursorValue)
                                   .and(department.id.gt(idAfter))
                   );
    }
}
