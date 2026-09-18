package com.sb14.hrbank.application.dto;

import java.time.LocalDate;

public record EmployeeTrendCondition(
    LocalDate from,
    LocalDate to,
    String unit
) {

}
