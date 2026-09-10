package com.sb14.hrbank.domain.service.file;

import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import com.sb14.hrbank.domain.repository.IFileRepository;

import com.sb14.hrbank.web.exception.HrBankException;
import com.sb14.hrbank.web.exception.HrBankExceptionType;
import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/*
    파일 서비스를 작업 중에 이걸 독립 서비스로 두는게맞나 싶음
    이 요구사항은 일단 메인주체가 아닌 서브 주체로 호출되고 저장 api(직원, 백업로그)를 통해 호출되는데 단독 서비스가 아니라 생각해서
    추가로 서비스를 두면 서비스에서 서비스를 호출하는게 도메인서비스가 아닌 유효 비즈니스 서비스를 호출한다는게 아닌거같아서 파일 저장 유틸 클래스로 변경

    근데 다시 또 생각해보면 서비스가 독립 api 를 담당하는게 아니여도 파일 저장 실페에 따른 처리를 담당해야하기에 서비스를 두는게 맞는거같음
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements IFileService{
    private final IFileRepository fileRepository;
    private final IFileUpload fileUpload;

    /*
        타 서비스에서 호출로 실행됨
        - 직원 등록 수정 요구사항에서 호출
        - 백업 서비스에서 호출

        - {메타 정보}는 데이터베이스에, {실제 파일}은 로컬 디스크에 저장합니다.
     */
    @Transactional
    @Override
    public MetaFile createFile(MultipartFile file, FileCategory fileCategory) {
        MetaFile metaFile = null;

        try{
            metaFile = fileUpload.uploadFile(file, fileCategory);
            return fileRepository.save(metaFile);
        }catch (DataAccessException e){
            if(Objects.nonNull(metaFile)){
                fileUpload.deleteFile(metaFile.getFilePath());
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
            fileUpload.deleteFile(fileToDelete.getFilePath());
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

        Resource resource = fileUpload.loadFile(metaFile.getFilePath());

        return FileDownload.of(metaFile, resource);
    }
}
