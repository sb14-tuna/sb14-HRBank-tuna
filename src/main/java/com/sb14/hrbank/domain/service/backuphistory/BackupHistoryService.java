package com.sb14.hrbank.domain.service.backuphistory;

import com.sb14.hrbank.domain.entity.backuphistory.BackupHistory;
import com.sb14.hrbank.domain.entity.backuphistory.BackupState;

public interface BackupHistoryService {
    BackupHistory createBackupHistory(String worker);
    BackupHistory findLatestBackupHistoryByState(BackupState state);
}
