package com.sb14.hrbank.application;

import com.sb14.hrbank.application.dto.EmployeeTrendCondition;
import com.sb14.hrbank.application.dto.EmployeeTrendResult;
import com.sb14.hrbank.domain.entity.employee.EmployeeHireDateCount;
import com.sb14.hrbank.domain.repository.employee.EmployeeRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmployeeTrendService {
    private final EmployeeRepository employeeRepository;

    public List<EmployeeTrendResult> getEmployeeTrend(LocalDate from, LocalDate to, String unit){
        log.info(" ====== {},{},{}", from,to , unit);
        EmployeeTrendCondition condition = resolveCondition(from, to, unit);

        long previousCount = employeeRepository.countHiredBeforeDate(condition.from());

        List<EmployeeHireDateCount> periodCounts = employeeRepository
            .findEmployeeCountByPeriod(condition.from(), condition.to(), unit);

        List<EmployeeTrendResult> result = calculateTrendResults(previousCount, periodCounts);

        log.info("===== 트렌드 추이 결과 -> {}", result);
        return result;
    }

    private List<EmployeeTrendResult> calculateTrendResults(
        long previousCount,
        List<EmployeeHireDateCount> periodCounts
    ) {
        List<EmployeeTrendResult> result = new ArrayList<>();

        for (EmployeeHireDateCount period : periodCounts) {
            long change = period.getCount();
            long currentCount = previousCount + change;

            double changeRate = previousCount == 0
                ? 0.0
                : (double) change / previousCount * 100;

            result.add(new EmployeeTrendResult(
                period.getHireDate(),
                currentCount,
                change,
                Math.round(changeRate * 10) / 10.0
            ));

            previousCount = currentCount;
        }

        return result;
    }

    private EmployeeTrendCondition resolveCondition(LocalDate from, LocalDate to, String unit){
        LocalDate resolvedTo =
            Objects.nonNull(to) ? to : LocalDate.now();

        LocalDate resolvedFrom =
            Objects.nonNull(from)
            ? from
            : switch (unit) {
                case "day" -> resolvedTo.minusDays(12);
                case "week" -> resolvedTo.minusWeeks(12);
                case "month" -> resolvedTo.minusMonths(12);
                case "quarter" -> resolvedTo.minusMonths(36);
                case "year" -> resolvedTo.minusYears(12);
                default -> resolvedTo.minusMonths(12);
            };

        return new EmployeeTrendCondition(resolvedFrom, resolvedTo, unit);
    }
}
