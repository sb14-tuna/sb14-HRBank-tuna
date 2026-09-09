package com.sb14.hrbank.domain.service.file;

import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    MetaFile createFile(MultipartFile file, FileCategory fileCategory);
}
