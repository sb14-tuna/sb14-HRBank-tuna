package com.sb14.hrbank.web.controller.dto;

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
        return new ChangeLogDetailDto(
                changeHistory.getId(),
                changeHistory.getType(),
                changeHistory.getEmployee().getEmployeeNumber(),
                changeHistory.getMemo(),
                changeHistory.getIpAddress(),
                changeHistory.getUpdatedAt(),
                changeHistory.getEmployee().getName(),
                changeHistory.getEmployee().getProfileImage() == null ? null : changeHistory.getEmployee().getProfileImage().getId(),
                changeHistory.getDiffs().stream()
                        .map(DiffDto::from)
                        .toList()
        );
    }
}
