package com.sb14.hrbank.web.controller.dto;

import com.sb14.hrbank.domain.service.backuphistory.BackupHistoryInfo;
import java.util.List;

public record CursorPageResponseBackupDto(
    List<BackupDto> content,            // 내부 객체드은 불변이 아니라 고민
    String nextCursor,
    Long nextIdAfter,
    int size,
    long totalElements,
    boolean hasNext
) {
    static public CursorPageResponseBackupDto from(BackupHistoryInfo backupHistoryInfo){
        List<BackupDto> backupDtoList = backupHistoryInfo.backupHistories()
            .stream()
            .map(BackupDto::from)
            .toList();

        return new CursorPageResponseBackupDto(
            backupDtoList,
            backupHistoryInfo.nextCursor(),
            backupHistoryInfo.nextIdAfter(),
            backupHistoryInfo.size(),
            backupHistoryInfo.totalElements(),
            backupHistoryInfo.hasNext()
        );
    }
}
