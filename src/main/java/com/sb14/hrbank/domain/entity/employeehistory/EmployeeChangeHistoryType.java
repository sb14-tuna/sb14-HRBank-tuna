package com.sb14.hrbank.domain.entity.employeehistory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum EmployeeChangeHistoryType {
    CREATED, UPDATED, DELETED
}
