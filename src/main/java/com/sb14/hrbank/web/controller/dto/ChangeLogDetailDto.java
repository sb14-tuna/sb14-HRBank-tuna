package com.sb14.hrbank.web.controller.dto;

import com.sb14.hrbank.domain.entity.employee.Employee;
import com.sb14.hrbank.domain.entity.employeehistory.EmployeeChangeHistory;
import com.sb14.hrbank.domain.entity.employeehistory.EmployeeChangeHistoryType;

import java.time.LocalDateTime;
import java.util.List;

public record ChangeLogDetailDto(
        Long id,
        EmployeeChangeHistoryType type,
        String employeeNumber,
        String memo,
        String ipAddress,
        LocalDateTime at,
        String employeeName,
        Long profileImageId,
        List<DiffDto> diffs
) {
    public static ChangeLogDetailDto from(EmployeeChangeHistory changeHistory) {
        Employee employee = changeHistory.getEmployee();
        Long profileImageId = employee.getProfileImage() == null ? null : employee.getProfileImage().getId();

        return new ChangeLogDetailDto(
                changeHistory.getId(),
                changeHistory.getType(),
                changeHistory.getEmployee().getEmployeeNumber(),
                changeHistory.getMemo(),
                changeHistory.getIpAddress(),
                changeHistory.getUpdatedAt(),
                employee.getName(),
                profileImageId,
                changeHistory.getDiffs().stream()
                        .map(DiffDto::from)
                        .toList()
        );
    }
}
