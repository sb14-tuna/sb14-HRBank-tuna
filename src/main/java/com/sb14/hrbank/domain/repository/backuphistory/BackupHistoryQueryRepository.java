package com.sb14.hrbank.domain.repository.backuphistory;

import com.sb14.hrbank.domain.entity.backuphistory.BackupHistory;
import com.sb14.hrbank.domain.service.backuphistory.BackupSearchCondition;
import java.util.List;

public interface BackupHistoryQueryRepository {
    List<BackupHistory> findAllBackupHistoryByCondition(BackupSearchCondition condition);
    long countByCondition(BackupSearchCondition condition);
}
