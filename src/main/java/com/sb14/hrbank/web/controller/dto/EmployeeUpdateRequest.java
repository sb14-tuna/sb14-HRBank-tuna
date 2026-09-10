package com.sb14.hrbank.web.controller.dto;

import com.sb14.hrbank.domain.entity.employee.EmployeeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EmployeeUpdateRequest {

    @NotBlank
    String name;

    @NotBlank
    String email;

    @NotNull
    Long departmentId;

    @NotBlank
    String position;

    @NotNull
    LocalDate hireDate;

    @NotNull
    EmployeeStatus status;

    String memo;
}
