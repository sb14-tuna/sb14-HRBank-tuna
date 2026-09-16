package com.sb14.hrbank.domain.service.imagefile;

import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import com.sb14.hrbank.domain.service.file.FileService;
import com.sb14.hrbank.util.FileUtils;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ImageFileService {
    private final FileUtils fileUtils;
    private final FileService fileService;

    public MetaFile uploadProfileImage(MultipartFile multipartFile){

        MetaFile metaFile = null;
        try{
            metaFile = fileUtils.uploadFile(multipartFile, FileCategory.PROFILE_IMAGE);
            return fileService.save(metaFile);
        }catch (DataAccessException e){
            if(Objects.nonNull(metaFile)){
                fileUtils.deleteFile(metaFile.getFilePath());
            }

            throw new RuntimeException("DB STORE FAILED");
        }
    }
}
