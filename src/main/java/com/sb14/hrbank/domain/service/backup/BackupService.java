package com.sb14.hrbank.domain.service.backup;

import com.sb14.hrbank.domain.entity.backuphistory.BackupHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

public interface BackupService {
    BackupHistory startBackup(String worker);
}
