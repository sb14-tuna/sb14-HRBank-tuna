package com.sb14.hrbank.web.controller;

import com.sb14.hrbank.domain.entity.backuphistory.BackupHistory;
import com.sb14.hrbank.domain.entity.backuphistory.BackupState;
import com.sb14.hrbank.domain.service.backuphistory.BackupHistoryService;
import com.sb14.hrbank.domain.service.file.FileService;
import com.sb14.hrbank.web.controller.dto.BackupDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/backups")
public class BackupApiController {
    private final FileService fileService;
    private final BackupHistoryService backupHistoryService;

    @PostMapping
    public ResponseEntity<BackupDto> startBackup(
        HttpServletRequest request
    ){
        String worker = request.getRemoteAddr();
        BackupHistory backupHistory = fileService.startBackup(worker);
        BackupDto response = BackupDto.from(backupHistory);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/latest")
    public ResponseEntity<BackupDto> latestBackupInfoByState(
        @RequestParam(defaultValue = "COMPLETED") BackupState status
    ){
        BackupHistory backupHistory = backupHistoryService.findLatestBackupHistoryByState(status);
        BackupDto response = BackupDto.from(backupHistory);

        return ResponseEntity.ok(response);
    }
}
