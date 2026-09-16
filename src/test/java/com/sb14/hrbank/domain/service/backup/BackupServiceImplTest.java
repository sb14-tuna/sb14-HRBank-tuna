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

    @Test
    void 성능_확인() throws InterruptedException{
        String worker = "system";

        System.out.println("====== PID >>>>>>>>>>> " + ProcessHandle.current().pid());

        Thread.sleep(20_000);
        backupService.startBackup(worker);
        Thread.sleep(60_000);
    }
}