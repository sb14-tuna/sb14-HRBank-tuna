package com.sb14.hrbank.web.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DepartmentCreateRequest {
    @NotBlank
    String name;

    String description;

    @NotNull
    LocalDate establishedDate;
}
