package com.sb14.hrbank.web.controller.dto;

import com.sb14.hrbank.domain.entity.employeehistory.EmployeeChangeHistory;
import com.sb14.hrbank.domain.entity.employeehistory.EmployeeChangeHistoryType;

import java.time.LocalDateTime;

public record ChangeLogDto(
        Long id,
        EmployeeChangeHistoryType type,
        String employeeNumber,
        String memo,
        String ipAddress,
        LocalDateTime at
) {
    public static ChangeLogDto from(EmployeeChangeHistory changeHistory) {
        return new ChangeLogDto(
                changeHistory.getId(),
                changeHistory.getType(),
                changeHistory.getEmployee().getEmployeeNumber(),
                changeHistory.getMemo(),
                changeHistory.getIpAddress(),
                changeHistory.getUpdatedAt()
        );
    }
}
