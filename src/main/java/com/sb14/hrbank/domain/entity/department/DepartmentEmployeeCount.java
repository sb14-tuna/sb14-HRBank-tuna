package com.sb14.hrbank.domain.entity.department;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DepartmentEmployeeCount {
    Long departmentId;
    Long employeeCount;

    public static DepartmentEmployeeCount of(
            Long departmentId,
            Long employeeCount
    ) {
        return new DepartmentEmployeeCount(
                departmentId,
                employeeCount
        );
    }
}
