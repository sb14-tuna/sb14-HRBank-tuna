package com.sb14.hrbank.web.controller.dto;

import com.sb14.hrbank.domain.entity.backuphistory.BackupHistory;
import com.sb14.hrbank.domain.entity.backuphistory.BackupState;
import java.time.Instant;
import java.util.Objects;

public record BackupDto(
    Long id,
    String worker,
    Instant startedAt,
    Instant endedAt,
    BackupState status,
    Long fileId
) {
    public static BackupDto from(BackupHistory backupHistory){
        if(Objects.isNull(backupHistory)) return null;

        Long fileId = Objects.isNull(backupHistory.getMetaFile()) ? null : backupHistory.getMetaFile().getId();

        return new BackupDto(
            backupHistory.getId(),
            backupHistory.getWorker(),
            backupHistory.getStartedAt(),
            backupHistory.getEndedAt(),
            backupHistory.getState(),
            fileId         // 널포인터 익셉션 발생 - 기존 : backupHistory.getMetaFile().getId();
        );
    }
}
