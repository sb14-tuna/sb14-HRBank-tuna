package com.sb14.hrbank.domain.service.backup;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackupServiceImplTest {
    @Autowired private BackupService backupService;

    @Test
    void 베베베베() {
        String worker = "system";
        backupService.startBackup(worker);
    }
}