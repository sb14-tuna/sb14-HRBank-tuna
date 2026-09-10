package com.sb14.hrbank.web.controller.dto;

import com.sb14.hrbank.domain.entity.employee.Employee;
import com.sb14.hrbank.domain.entity.employee.EmployeeStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EmployeeDto {
    Long id;
    String name;
    String email;
    String employeeNumber;
    Long departmentId;
    String departmentName;
    String position;
    LocalDate hireDate;
    EmployeeStatus status;
    Long profileImageId;

    public static EmployeeDto from(Employee employee) {
        return new EmployeeDto(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getEmployeeNumber(),
                employee.getDepartment().getId(),
                employee.getDepartment().getName(),
                employee.getPosition(),
                employee.getHireDate(),
                employee.getStatus(),
                Objects.isNull(employee.getProfileImage())
                    ? null
                    : employee.getProfileImage().getId()
        );
    }
}
