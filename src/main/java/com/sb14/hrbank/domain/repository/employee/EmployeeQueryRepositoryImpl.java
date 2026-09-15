package com.sb14.hrbank.domain.repository.employee;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sb14.hrbank.domain.entity.employee.Employee;
import com.sb14.hrbank.domain.entity.employee.EmployeeGroupCount;
import com.sb14.hrbank.domain.entity.employee.EmployeeStatus;
import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import com.sb14.hrbank.domain.service.employee.EmployeeSearchCondition;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

import static com.sb14.hrbank.domain.entity.employee.QEmployee.employee;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class EmployeeQueryRepositoryImpl implements EmployeeQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Employee> findAllByCondition(EmployeeSearchCondition condition) {
        return queryFactory
                .select(employee)
                .from(employee)
                .join(employee.department).fetchJoin()  // n+1 방지? - fetch 해옴
                .leftJoin(employee.profileImage).fetchJoin()    // 프로필 없는 직원도 join (필수 아니었음)
                .where(
                        // 각 employee row의 컬럼 값이 request에 들어온 값과 같으면 통과
                        nameOrEmailContains(condition.getNameOrEmail()),
                        employeeNumberContains(condition.getEmployeeNumber()),
                        departmentNameContains(condition.getDepartmentName()),
                        positionContains(condition.getPosition()),
                        hireDateGreaterThanOrEqual(condition.getHireDateFrom()),
                        hireDateLessThanOrEqual(condition.getHireDateTo()),
                        statusEquals(condition.getStatus()),
                        cursorCondition(
                                condition.getCursor(),
                                condition.getIdAfter(),
                                condition.getSortField(),
                                isDescending(condition.getSortDirection())
                        )
                )
                .orderBy(
                        // 1차 정렬: 사용자가 선택한 정렬 필드로
                        primarySortBySortField(
                                condition.getSortField(),
                                isDescending(condition.getSortDirection())
                        ),
                        // 2차 정렬: 직원 Id로
                        secondarySortById(
                                isDescending(condition.getSortDirection())
                        )
                )
                .limit(condition.getSize() + 1)
                .fetch();
    }


    @Override
    public long countByCondition(EmployeeSearchCondition condition) {
        Long totalElements = queryFactory
                .select(employee.count())
                .from(employee)
                .join(employee.department)
                .where(
                        nameOrEmailContains(condition.getNameOrEmail()),
                        employeeNumberContains(condition.getEmployeeNumber()),
                        departmentNameContains(condition.getDepartmentName()),
                        positionContains(condition.getPosition()),
                        hireDateGreaterThanOrEqual(condition.getHireDateFrom()),
                        hireDateLessThanOrEqual(condition.getHireDateTo()),
                        statusEquals(condition.getStatus())
                )
                .fetchOne();
        return (Objects.nonNull(totalElements))
                ? totalElements
                : 0;
    }

    @Override
    public List<EmployeeGroupCount> findByEmployeeDistribution(
            String groupBy,
            EmployeeStatus status) {
        StringExpression groupingColumn = groupColumnByGroupBy(groupBy);
        NumberExpression<Long> countColumn = employee.count();

        List<Tuple> groupedEmployees = queryFactory
                .select(groupingColumn, countColumn)
                .from(employee)
                .join(employee.department)
                .where(statusEquals(status))
                .groupBy(groupingColumn)
                .orderBy(countColumn.desc(), groupingColumn.asc())
                .fetch();

        return groupedEmployees.stream()
                .map(tuple ->
                        EmployeeGroupCount.of(
                                tuple.get(groupingColumn),
                                Objects.requireNonNullElse(
                                        tuple.get(countColumn),
                                        0L
                                )
                        )
                )
                .toList();
    }

    @Override
    public long countEmployeesByStatusAndDateRange(EmployeeStatus status, LocalDate fromDate, LocalDate toDate) {
        Long employeeCount = queryFactory
                .select(employee.count())
                .from(employee)
                .where(
                        statusEquals(status),
                        hireDateGreaterThanOrEqual(fromDate),
                        hireDateLessThanOrEqual(toDate)
                )
                .fetchOne();
        return (Objects.nonNull(employeeCount)) ? employeeCount : 0;
    }

    /* where 절 */
    private BooleanExpression nameOrEmailContains(String keyword) {
        return hasText(keyword)
                ? employee.name.containsIgnoreCase(keyword)
                            .or(employee.email.containsIgnoreCase(keyword))
                : null;
    }
    private BooleanExpression employeeNumberContains(String employeeNumber) {
        return hasText(employeeNumber)
                ? employee.employeeNumber.containsIgnoreCase(employeeNumber)
                : null;
    }
    private BooleanExpression departmentNameContains(String departmentName) {
        return hasText(departmentName)
                ? employee.department.name.containsIgnoreCase(departmentName)
                : null;
    }
    private BooleanExpression positionContains(String position) {
        return hasText(position)
                ? employee.position.containsIgnoreCase(position)
                : null;
    }
    private BooleanExpression hireDateGreaterThanOrEqual(LocalDate hireDateFrom) {
        return Objects.nonNull(hireDateFrom)
                ? employee.hireDate.goe(hireDateFrom)
                : null;
    }
    private BooleanExpression hireDateLessThanOrEqual(LocalDate hireDateTo) {
        return Objects.nonNull(hireDateTo)
                ? employee.hireDate.loe(hireDateTo)
                : null;
    }
    private BooleanExpression statusEquals(EmployeeStatus status) {
        return Objects.nonNull(status)
                ? employee.status.eq(status)
                : null;
    }


    /* 정렬 */
    private boolean isDescending(String sortDirection) {    // = is내림차순
        return sortDirection.equals("desc");
    }
    private StringExpression groupColumnByGroupBy(String groupBy) {
        return switch (groupBy) {
            case "department" -> employee.department.name;
            case "position" -> employee.position;
            default -> throw new HrBankException(
                    HrBankExceptionType.ILLEGAL_DISTRIBUTION_GROUP,
                    groupBy
            );
        };
    }
    // 1차 정렬 - 사용자가 선택한 거에 대해 정렬
    private OrderSpecifier<?> primarySortBySortField(String sortField, boolean isDescending) {
        return switch (sortField) {
            case "name" ->
                isDescending
                        ? employee.name.desc()
                        : employee.name.asc();
            case "employeeNumber" ->
                isDescending
                        ? employee.employeeNumber.desc()
                        : employee.employeeNumber.asc();
            case "hireDate" ->
                isDescending
                        ? employee.hireDate.desc()
                        : employee.hireDate.asc();
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
                ? employee.id.desc()
                : employee.id.asc();
    }



    /* 커서 */
    // 어떤 컬럼으로 비교할지 결정한다
    private BooleanExpression cursorCondition(
            String cursor,          // cursor = "김민수"
            Long idAfter,
            String sortField,       // sortField = "name"
            boolean isDescending
    ) {
        if (!hasText(cursor) && Objects.isNull(idAfter)) return null;   // 첫번째 페이지일때

        return switch (sortField) {
            // sortField 따라 비교할 컬럼 지정
            case "name" ->
                cursorCondition(
                        employee.name,              // employee의 name 컬럼 지정
                        cursor,
                        idAfter,
                        isDescending
                );
            case "employeeNumber" ->
                cursorCondition(
                        employee.employeeNumber,    // employee의 employeeNumber 컬럼 지정
                        cursor,
                        idAfter,
                        isDescending
                );
            case "hireDate" -> {
                LocalDate cursorDate = null;
                try {
                    cursorDate = LocalDate.parse(cursor);   // String으로 LocalDate가 들어오기 때문에 parse 먼저
                    yield cursorCondition(
                            employee.hireDate,      // employee의 hireDate 컬럼 지정
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

    // 결정된 컬럼을 실제로 비교한다
    private <T extends Comparable<?>> BooleanExpression cursorCondition(    // "T는 비교가능한 타입이어야 한다"
            ComparableExpression<T> employeeColumn,   // 비교할 DB 컬럼 (employee.name)
            T cursorValue,                      // 이전 페이지 마지막 정렬값 ("홍길동")
            Long idAfter,                       // 이전 페이지 마지막 직원 ID (10)
            boolean isDescending
    ) {
        // 프론트가 idAfter 안 보내면 cursorValue만 가지고 정렬한다 -- cursor랑 idAfter 같이 받아야 하는데, 프론트는 cursor만 준다 (프론트 이슈)
        if (Objects.isNull(idAfter)) {
            return isDescending
                    ? employeeColumn.lt(cursorValue)
                    : employeeColumn.gt(cursorValue);
        }

        return isDescending
                ? employeeColumn
                    .lt(cursorValue) // lt = less than (내림차순)
                    .or(
                            employeeColumn
                                    .eq(cursorValue)
                                    .and(employee.id.lt(idAfter))   // 2차 정렬 id로
                    )
                : employeeColumn
                    .gt(cursorValue) // gt = greater than (오름차순)
                    .or(
                            employeeColumn
                                    .eq(cursorValue)
                                    .and(employee.id.gt(idAfter))   // 2차 정렬 id로
                    );

    }

}
