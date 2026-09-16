package com.sb14.hrbank.domain.entity.employee;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EmployeeHireDateCount {
    LocalDate hireDate;
    long count;

    public static EmployeeHireDateCount of(LocalDate hireDate, long count) {
        return new EmployeeHireDateCount(hireDate, count);
    }
}
