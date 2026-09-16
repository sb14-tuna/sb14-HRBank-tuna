package com.sb14.hrbank.domain.repository.backuphistory;

import static com.sb14.hrbank.domain.entity.backuphistory.QBackupHistory.backupHistory;
import static org.springframework.util.StringUtils.hasText;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sb14.hrbank.domain.entity.backuphistory.BackupHistory;
import com.sb14.hrbank.domain.entity.backuphistory.BackupState;
import com.sb14.hrbank.domain.service.backuphistory.BackupSearchCondition;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class BackupHistoryQueryRepositoryImpl implements BackupHistoryQueryRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<BackupHistory> findAllBackupHistoryByCondition(BackupSearchCondition condition) {
        return queryFactory
            .select(backupHistory)
            .from(backupHistory)
            .leftJoin(backupHistory.metaFile).fetchJoin()  // n+1 방지? - fetch 해옴
            .where(
                workerContains(condition.worker()),
                statusEquals(condition.status()),
                startedAtBetween(condition.startedAtFrom(), condition.startedAtTo()),
                cursorCondition(
                    condition.cursor(),
                    condition.idAfter(),
                    condition.sortField(),
                    isDescending(condition.sortDirection())
                )
            )
            .orderBy(
                primarySortBySortField(
                    condition.sortField(),
                    isDescending(condition.sortDirection())
                ),
                secondarySortById(
                    isDescending(condition.sortDirection())
                )
            )
            .limit(condition.size() + 1)
            .fetch();
    }

    @Override
    public long countByCondition(BackupSearchCondition condition) {
        Long totalElements = queryFactory
            .select(backupHistory.count())
            .from(backupHistory)
            .where(
                workerContains(condition.worker()),
                statusEquals(condition.status()),
                startedAtBetween(
                    condition.startedAtFrom(),
                    condition.startedAtTo()
                )
            )
            .fetchOne();

        return Objects.nonNull(totalElements)
            ? totalElements
            : 0;
    }



    // 조건절
    private BooleanExpression workerContains(String worker) {
        return hasText(worker)
            ? backupHistory.worker.containsIgnoreCase(worker)    // where like
            : null;
    }

    private BooleanExpression statusEquals(BackupState status) {
        return Objects.nonNull(status)
            ? backupHistory.state.eq(status)    // where =
            : null;
    }

    private BooleanExpression startedAtBetween(Instant from, Instant to) {
        if(Objects.isNull(from) || Objects.isNull(to)) return null;

        return backupHistory.startedAt.between(from, to);
    }

    private boolean isDescending(String sortDirection) {
        return sortDirection.equals("DESC");
    }

    private OrderSpecifier<?> primarySortBySortField(String sortField, boolean isDescending) {
        return switch (sortField) {
            case "startedAt" -> isDescending
                ? backupHistory.startedAt.desc()
                : backupHistory.startedAt.asc();

            case "endedAt" -> isDescending
                ? backupHistory.endedAt.desc()
                : backupHistory.endedAt.asc();

            case "status" -> isDescending
                ? backupHistory.state.desc()
                : backupHistory.state.asc();

            default -> throw new RuntimeException("정렬 필드가 아닌 필드");
        };
    }

    private OrderSpecifier<Long> secondarySortById(boolean isDescending) {
        return isDescending
            ? backupHistory.id.desc()
            : backupHistory.id.asc();
    }


    // 커서
    private BooleanExpression cursorCondition(
        String cursor,          // cursor = "김민수"
        Long idAfter,
        String sortField,       // sortField = "name"
        boolean isDescending
    ) {
        if (!hasText(cursor) && Objects.isNull(idAfter)) return null;   // 첫번째 페이지일때

        return switch (sortField) {
            case "startedAt" -> cursorCondition(
                backupHistory.startedAt,
                Instant.parse(cursor),
                idAfter,
                isDescending
            );

            case "endedAt" -> cursorCondition(
                backupHistory.endedAt,
                Instant.parse(cursor),
                idAfter,
                isDescending
            );

            case "status" -> cursorCondition(
                backupHistory.state,
                BackupState.valueOf(cursor),
                idAfter,
                isDescending
            );
            default -> throw new RuntimeException("정렬 필드가 아닌 필드");
        };
    }

    // 결정된 컬럼을 실제로 비교한다
    private <T extends Comparable<?>> BooleanExpression cursorCondition(    // "T는 비교가능한 타입이어야 한다"
        ComparableExpression<T> backupColumn,   // 비교할 DB 컬럼 (employee.name)
        T cursorValue,                      // 이전 페이지 마지막 정렬값 ("홍길동")
        Long idAfter,                       // 이전 페이지 마지막 직원 ID (10)
        boolean isDescending
    ) {
        if (isDescending) { // 내림차순
            return backupColumn.lt(cursorValue) // lt = less than
                .or(
                    backupColumn
                        .eq(cursorValue)
                        .and(backupHistory.id.lt(idAfter))   // 2차 정렬 id로
                );
        }
        /* SQL로 하면:
         *   WHERE employee_name > '홍길동'
                OR (
                    employee_name = '홍길동'
                    AND employee_id > 10
                )
        */

        // 오름차순
        return backupColumn.gt(cursorValue) // gt = greater than
            .or(
                backupColumn
                    .eq(cursorValue)
                    .and(backupHistory.id.gt(idAfter))   // 2차 정렬 id로
            );
    }
}
