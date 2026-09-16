package com.sb14.hrbank.util;

import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import java.nio.file.Path;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileUtils {
    MetaFile uploadFile(MultipartFile file, FileCategory fileCategory);
    MetaFile uploadFile(byte[] bytesToFile, FileCategory fileCategory);

    Path createCsvFile(FileCategory category);
    void appendFile(byte[] chunk, String filePath);

    MetaFile completeFile(Path filePath, FileCategory category);


    void deleteFile(String filePath);
    Resource loadFile(String key);

    Path writeFile(byte[] bytes, FileCategory category);
}
