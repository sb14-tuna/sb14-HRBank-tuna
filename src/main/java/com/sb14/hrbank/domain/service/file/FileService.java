package com.sb14.hrbank.domain.service.file;

import com.sb14.hrbank.domain.entity.backuphistory.BackupHistory;
import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import com.sb14.hrbank.web.controller.dto.BackupDto;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    MetaFile createFile(MultipartFile file, FileCategory fileCategory);
    void deleteFile(Long id);
    FileDownload getFileDownload(Long id);
    BackupHistory startBackup(String worker);
}
