package com.sb14.hrbank.domain.service.file.fileupload;

import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileUpload {
    MetaFile uploadFile(MultipartFile file, FileCategory fileCategory);
    MetaFile uploadFile(byte[] bytesToFile, FileCategory fileCategory);

    String createFilePath(FileCategory category);
    void appendFile(byte[] chunk, String filePath);

    MetaFile completeFile(String filePath, FileCategory category);


    void deleteFile(String filePath);
    Resource loadFile(String key);
}
