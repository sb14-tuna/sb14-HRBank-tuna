package com.sb14.hrbank.web.controller.dto;

import com.sb14.hrbank.domain.entity.employee.EmployeeGroupCount;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EmployeeDistributionDto {
    String groupKey;
    Long count;
    double percentage;

    public static List<EmployeeDistributionDto> from(
            List<EmployeeGroupCount> groupCounts
    ) {
        // 총 직원수
        long totalCount = groupCounts.stream()
                .mapToLong(EmployeeGroupCount::getCount)
                .sum();

        if (totalCount == 0) return List.of();

        // 퍼센티지 계산
        return groupCounts.stream()
                .map(groupCount -> of(
                        groupCount.getGroupKey(),
                        groupCount.getCount(),
                        calculatePercentage(
                                groupCount.getCount(),
                                totalCount
                        )
                ))
                .toList();
    }

    private static double calculatePercentage(long count, long totalCount) {
        return Math.round(count * 1000.0 / totalCount) / 10.0;
    }

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
