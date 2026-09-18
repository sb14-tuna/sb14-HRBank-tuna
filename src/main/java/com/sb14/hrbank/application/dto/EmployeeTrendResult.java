package com.sb14.hrbank.application.dto;

import java.time.LocalDate;

public record EmployeeTrendResult(
    LocalDate date,
    long count,
    long change,
    Double changeRate
) {

}
