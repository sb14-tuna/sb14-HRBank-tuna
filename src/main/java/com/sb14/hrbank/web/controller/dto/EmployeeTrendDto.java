package com.sb14.hrbank.web.controller.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EmployeeTrendDto {
    LocalDate date;         // 집계 구간 시작일
    Long count;             // 직원수
    Long change;            // 증감수
    Double changeRate;      // 증감률
}
