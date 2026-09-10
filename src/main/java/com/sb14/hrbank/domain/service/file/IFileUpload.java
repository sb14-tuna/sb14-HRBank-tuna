package com.sb14.hrbank.domain.service.file;

import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IFileUpload {
    MetaFile uploadFile(MultipartFile file, FileCategory fileCategory);
    void deleteFile(String filePath);
    Resource loadFile(String key);
}
