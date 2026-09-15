package com.sb14.hrbank.domain.service.file;

import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import com.sb14.hrbank.util.FileUtils;
import java.nio.file.Path;
import org.springframework.web.multipart.MultipartFile;
import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import com.sb14.hrbank.domain.repository.FileRepository;


import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
    파일 서비스 :
        실제 파일과 메타파일의 저장·조회·삭제를 관리
        업로드 실패 시 그에 따른 예외처리를 담당
            아직 실패 시나리오 모름
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    private final FileUtils fileUtils;


    @Transactional
    @Override
    public MetaFile createFile(MultipartFile file, FileCategory fileCategory) {
        MetaFile metaFile = null;

        try{
            metaFile = fileUtils.uploadFile(file, fileCategory);
            return fileRepository.save(metaFile);
        }catch (DataAccessException e){
            if(Objects.nonNull(metaFile)){
                fileUtils.deleteFile(metaFile.getFilePath());
            }

            throw new RuntimeException("DB STORE FAILED");
        }
    }

    @Transactional
    @Override
    public void deleteFile(Long fileId) {
        MetaFile fileToDelete = null;
        try {
            log.info("============= 디스크 파일 삭제 시작 =================");
            fileToDelete = fileRepository.findById(fileId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 파일: " + fileId));
            fileUtils.deleteFile(fileToDelete.getFilePath());
        } catch (NoSuchElementException e) {
            log.warn("삭제하려는 파일이 존재하지 않음 - 일단 작동엔 문제 없으니 넘어간다");
        } catch (DataAccessException e) {
            throw new RuntimeException("파일 삭제 실패");
        }
    }

    @Override
    public FileDownload getFileDownload(Long id){
        MetaFile metaFile = fileRepository.findById(id)
            .orElseThrow(() -> new HrBankException(HrBankExceptionType.FILE_NOT_FOUND, "자세한 정보?"));

        Resource resource = fileUtils.loadFile(metaFile.getFilePath());

        return FileDownload.of(metaFile, resource);
    }

    @Override
    public MetaFile completeFile(Path filePath, FileCategory fileCategory) {
        MetaFile metaFile = fileUtils.completeFile(filePath, fileCategory);

        return fileRepository.save(metaFile);
    }
}
