package com.sb14.hrbank.web.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EmployeeCreateRequest {
    @NotBlank
    String name;

    @NotBlank
    @Email
    String email;

    @NotNull
    Long departmentId;

    @NotBlank
    String position;

    @NotNull
    LocalDate hireDate;

    String memo;
}
