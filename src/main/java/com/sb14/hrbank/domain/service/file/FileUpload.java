package com.sb14.hrbank.domain.service.file;

import com.sb14.hrbank.domain.entity.file.FileCategory;
import com.sb14.hrbank.domain.entity.file.MetaFile;
import org.springframework.web.multipart.MultipartFile;

public interface FileUpload {
    MetaFile uploadFile(MultipartFile file, FileCategory fileCategory);
}
