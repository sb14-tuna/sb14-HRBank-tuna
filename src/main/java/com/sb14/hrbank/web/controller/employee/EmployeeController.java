package com.sb14.hrbank.web.controller.employee;

import com.sb14.hrbank.application.EmployeeTrendService;
import com.sb14.hrbank.application.dto.EmployeeTrendResult;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeTrendService employeeTrendService;

    @GetMapping("/stats/trend")
    public ResponseEntity<List<EmployeeTrendResult>> getEmployeeTrend(
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate to,

        @RequestParam(defaultValue = "month")
        @Pattern(
            regexp = "^(day|week|month|quarter|year)$",
            message = "집계 단위는 day, week, month, quarter, year 중 하나여야 합니다."
        )
        String unit
    ) {
        return ResponseEntity.ok(
            employeeTrendService.getEmployeeTrend(from, to, unit)
        );
    }
}
