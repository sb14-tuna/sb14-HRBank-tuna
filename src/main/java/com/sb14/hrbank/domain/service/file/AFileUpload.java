package com.sb14.hrbank.domain.service.file;

import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
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
            log.error("============FILE DELETE FAILED=============");
            //throw new RuntimeException("");
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
}
