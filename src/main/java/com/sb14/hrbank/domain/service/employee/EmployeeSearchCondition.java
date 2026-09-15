package com.sb14.hrbank.domain.service.employee;

import com.sb14.hrbank.domain.entity.employee.EmployeeStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EmployeeSearchCondition {
    String nameOrEmail;
    String employeeNumber;
    String departmentName;
    String position;
    LocalDate hireDateFrom;
    LocalDate hireDateTo;
    EmployeeStatus status;
    Long idAfter;
    String cursor;
    Integer size;
    String sortField;
    String sortDirection;
}
