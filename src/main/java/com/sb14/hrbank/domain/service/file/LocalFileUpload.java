package com.sb14.hrbank.domain.service.file;

import com.sb14.hrbank.web.exception.HrBankException;
import com.sb14.hrbank.web.exception.HrBankExceptionType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/*
    로컬저장 구현체 또는 S3 발생하는 예외는 IOException 으로 통일
    추상클래스에서 공통 파일 익셉션으로 잡아 처리하도록 구현
 */
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

    @Override
    protected Resource loadPhysicalFile(String key) throws IOException{
        Path path = Path.of(key).toAbsolutePath();

        if (!Files.isRegularFile(path)) {
            throw new NoSuchFileException(path.toString());
        }

        return new FileSystemResource(path);
    }
}
