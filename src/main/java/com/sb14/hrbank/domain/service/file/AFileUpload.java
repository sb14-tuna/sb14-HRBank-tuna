package com.sb14.hrbank.domain.service.file;

import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public abstract class AFileUpload implements IFileUpload {

    @Override
    public MetaFile uploadFile(MultipartFile file, FileCategory fileCategory) {
        if(Objects.isNull(file) || file.isEmpty()){
            throw new RuntimeException("FILE_EMPTY");
        }

        if (Objects.isNull(fileCategory)) {
            throw new RuntimeException("CATEGORY_NULL");
        }

        try{
            String originalFileName = file.getOriginalFilename();

            Long fileSize = file.getSize();
            String fileType = file.getContentType();
            String fileName = getFileName(fileCategory, originalFileName);
            String filePathUrl = getFullPath(fileName);
            log.info("filePathUrl ----------- {}", filePathUrl);

            String key = storeFile(file, filePathUrl);
            log.info("storedFile ----------- {}", key);


            return MetaFile.init(fileName, fileSize, fileType, fileCategory, key);
        }catch (IOException e){
            throw new RuntimeException("FILE_STORE_FAILED");
        }
    }

    @Override
    public void deleteFile(String filePath) {
        if(Objects.isNull(filePath)) throw new RuntimeException("FILEPATH IS NULL");
        try{
            delete(filePath);
        }catch (IOException e){
            log.warn("============FILE DELETE FAILED=============");
            //throw new RuntimeException("");
        }
    }

    @Override
    public Resource loadFile(String key) {
        if(Objects.isNull(key) || key.isBlank()){
            log.warn("=========== 인자로 들어온 키 값이 이상함, key = {}", key);
            throw new HrBankException(HrBankExceptionType.FILE_KEY_INVALID, "파일 key 값이 유효하지 않습니다.");
        }

        try{
            return loadPhysicalFile(key);
        }catch (IOException e){
            log.warn("파일 키로 물리파일로 불러오는 중에 문제가 발생했습니다. {}", key, e);
            throw new HrBankException(HrBankExceptionType.FILE_LOAD_FAILED, "물리 파일로 불러오는 중에 문제가 발생했습니다.");
        }
    }

    private String getFileName(FileCategory fileCategory, String originalFileName){
        String extension = extractExt(originalFileName);
        return fileCategory.getField() + "_" + UUID.randomUUID() + "." + extension;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    abstract String getFullPath(String fileName);
    abstract String storeFile(MultipartFile file, String filePathUrl) throws IOException;
    abstract void delete(String filePath) throws IOException;
    protected abstract Resource loadPhysicalFile(String key) throws IOException;
}
