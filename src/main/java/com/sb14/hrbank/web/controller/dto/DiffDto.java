package com.sb14.hrbank.web.controller.dto;

import com.sb14.hrbank.domain.entity.employeehistory.EmployeeChangeDetail;

public record DiffDto(
        String propertyName,
        String before,
        String after
) {
    public static DiffDto from(EmployeeChangeDetail changeDetail) {
        return new DiffDto(
                changeDetail.getPropertyName(),
                changeDetail.getBeforeValue(),
                changeDetail.getAfterValue()
        );
    }
}
