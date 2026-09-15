package com.sb14.hrbank.web.controller.dto.employee.stats;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EmployeeDistributionDto {
    String groupKey;
    Long count;
    double percentage;

    public static EmployeeDistributionDto of (
            String groupKey,
            long count,
            double percentage
    ) {
        return new EmployeeDistributionDto(
                groupKey,
                count,
                percentage
        );
    }
}
