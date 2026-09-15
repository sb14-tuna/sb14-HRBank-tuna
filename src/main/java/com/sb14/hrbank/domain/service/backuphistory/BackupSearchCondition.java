package com.sb14.hrbank.domain.service.backuphistory;

import com.sb14.hrbank.domain.entity.backuphistory.BackupState;
import java.time.Instant;

public record BackupSearchCondition(
    String worker,
    BackupState status,
    Instant startedAtFrom,
    Instant startedAtTo,
    Long idAfter,
    String cursor,
    int size,
    String sortField,
    String sortDirection
) {

}
