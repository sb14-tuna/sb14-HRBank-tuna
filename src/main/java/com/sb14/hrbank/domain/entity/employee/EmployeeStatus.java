package com.sb14.hrbank.domain.entity.employee;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum EmployeeStatus {
    ACTIVE,     // 재직중
    ON_LEAVE,   // 휴직중
    RESIGNED  // 퇴사
}
