package com.sb14.hrbank.util;

import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
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
public class LocalFileUtils extends AbstractFileUtils {
    private final String fileDir;

    public LocalFileUtils(@Value("${file.upload-dir}") String fileDir) {
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
    String storeFile(byte[] bytes, String filePathUrl) throws IOException {
        Path key = Path.of(filePathUrl).toAbsolutePath();

        Files.createDirectories(key.getParent());
        Files.write(key, bytes);

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

    /*
        백업 파일 생성 전용
     */
    @Override
    public Path createCsvFile(FileCategory fileCategory) {
        String extension = fileCategory.equals(FileCategory.BACKUP_CSV) ? "csv" : "log";
        String fileName = fileCategory.getField() + "_" + UUID.randomUUID() + "." + extension;

        return Path.of(getFullPath(fileName));
    }

    @Override
    public void appendFile(byte[] chunk, String filePath) {
        try{
            Path path = Path.of(filePath).toAbsolutePath();
            Files.createDirectories(path.getParent());

            // 덮어쓰기가 아닌 이어쓰기
            Files.write(path, chunk, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }catch (IOException e){
            throw new HrBankException(HrBankExceptionType.FILE_UPLOAD_FAILED, "백업 파일 이어쓰기 중 오류가 발생했습니다.");
        }
    }

    @Override
    public MetaFile completeFile(Path path, FileCategory fileCategory) {
        try {
            String fileName = path.getFileName().toString();
            long fileSize = Files.size(path);
            String fileType = fileCategory.equals(FileCategory.BACKUP_CSV) ? "text/csv" : "text/plain";

            return MetaFile.init(fileName, fileSize, fileType, fileCategory, path.toString());
        } catch (IOException e) {
            throw new HrBankException(HrBankExceptionType.FILE_UPLOAD_FAILED, "백업 파일 완료 처리에 실패했습니다.");
        }
    }
}
