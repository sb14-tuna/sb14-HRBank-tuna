package com.sb14.hrbank.web.controller.dto;

import com.sb14.hrbank.domain.entity.employee.Employee;
import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CursorPageResponseEmployeeDto {
    List<EmployeeDto> content;
    String nextCursor;
    Long nextIdAfter;
    Integer size;
    Long totalElements;
    Boolean hasNext;

    public static CursorPageResponseEmployeeDto from(
            List<Employee> searchedEmployees,
            int requestedSize,
            String sortField,
            long totalElements
    ) {
        // dto 조합에 필요한 연산은 여기서 수행
        // * nextCursor, nextIdAfter 계산 등
        boolean hasNext = searchedEmployees.size() > requestedSize;
        List<Employee> pageEmployees = hasNext
                ? searchedEmployees.subList(0, requestedSize)
                : searchedEmployees;

        String nextCursor = null;
        Long nextIdAfter = null;

        if (hasNext) {
            Employee lastEmployee = pageEmployees.get(pageEmployees.size() - 1);
            nextCursor = getCursor(lastEmployee, sortField);
            nextIdAfter = lastEmployee.getId();
        }

        List<EmployeeDto> content = pageEmployees.stream()
                .map(EmployeeDto::from)
                .toList();

        return new CursorPageResponseEmployeeDto(
                content,
                nextCursor,
                nextIdAfter,
                content.size(),
                totalElements,
                hasNext
        );
    }

    // 직원 객체, 정렬 필드로 커서 값 반환
    private static String getCursor(Employee employee, String sortField) {
        return switch (sortField) {
            case "name" -> employee.getName();
            case "employeeNumber" -> employee.getEmployeeNumber();
            case "hireDate" -> employee.getHireDate().toString();
            default -> throw new HrBankException(
                    HrBankExceptionType.ILLEGAL_SORT_FIELD,
                    sortField
            );
        };
    }
}
