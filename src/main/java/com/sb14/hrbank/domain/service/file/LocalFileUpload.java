package com.sb14.hrbank.domain.service.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class LocalFileUpload extends AFileUpload{
    private final String fileDir;

    public LocalFileUpload(@Value("${file.upload-dir}") String fileDir) {
        this.fileDir = fileDir;
    }

    @Override
    String getFullPath(String fileName) {
        return Path.of(fileDir)
            .resolve(fileName)
            .normalize()
            .toString();
    }

    @Override
    String storeFile(MultipartFile file, String filePathUrl) throws IOException {
        Path key = Path.of(filePathUrl).toAbsolutePath();

        Files.createDirectories(key.getParent());
        file.transferTo(key);

        return key.toString();
    }

    @Override
    void delete(String filePath) throws IOException {
        Files.deleteIfExists(Path.of(filePath));
    }
}
