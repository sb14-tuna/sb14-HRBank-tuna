package com.sb14.hrbank.scheduler;

import com.sb14.hrbank.domain.service.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class BackupFileScheduler {
    private final FileService fileService;

    @Scheduled(cron = "${backup.schedule}")
    public void batchBackupFile(){
        log.info(" ===== 백업 스케줄러 시작 ===== ");
        String worker = "system";
        fileService.startBackup(worker);
        log.info(" ===== 백업 스케줄러 종료 ===== ");
    }
}
