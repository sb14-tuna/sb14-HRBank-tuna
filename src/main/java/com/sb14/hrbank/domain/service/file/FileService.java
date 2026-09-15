package com.sb14.hrbank.domain.service.file;

import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import java.nio.file.Path;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    MetaFile createFile(MultipartFile file, FileCategory fileCategory);
    void deleteFile(Long id);
    FileDownload getFileDownload(Long id);
    MetaFile completeFile(Path filePath, FileCategory fileCategory);
}
