package com.sb14.hrbank.domain.entity.department;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DepartmentWithEmployeeCount {
    Department department;
    Long employeeCount;

    public static DepartmentWithEmployeeCount of(
            Department department,
            Long employeeCount
    ) {
        return new DepartmentWithEmployeeCount(
                department,
                employeeCount
        );
    }
}
