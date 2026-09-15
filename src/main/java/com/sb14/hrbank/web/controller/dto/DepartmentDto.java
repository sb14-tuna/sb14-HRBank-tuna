package com.sb14.hrbank.web.controller.dto;

import com.sb14.hrbank.domain.entity.department.Department;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DepartmentDto {
    Long id;
    String name;
    String description;
    LocalDate establishedDate;
    Long employeeCount;

    public static DepartmentDto from(
            Department department,
            long employeeCount
    ) {
        return new DepartmentDto(
                department.getId(),
                department.getName(),
                department.getDescription(),
                department.getEstablishedDate(),
                employeeCount
        );
    }

    public static DepartmentDto from(
            Department department
    ) {
        return from(department, 0L);
    }
}
