package com.sb14.hrbank.domain.entity.backuphistory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum BackupState {
    IN_PROGRESS("진행중"),
    COMPLETED("완료"),
    FAILED("실패"),
    SKIPPED("건너뜀");

    String state;
}
