package com.sb14.hrbank.domain.service.file;

import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import com.sb14.hrbank.domain.service.file.fileupload.FileUpload;
import org.springframework.web.multipart.MultipartFile;
import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import com.sb14.hrbank.domain.repository.FileRepository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    private final FileUpload fileUpload;

    @PersistenceContext
    private EntityManager entityManager;        // 영속성 관리를 위함

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

    @Override
    public MetaFile completeBackupFile(String filePath, FileCategory category) {
        MetaFile metaFile = fileUpload.completeFile(filePath, category);
        return fileRepository.save(metaFile);
    }

    @Override
    public MetaFile createErrorLogFile(byte[] bytes, FileCategory category) {
        MetaFile metaFile = fileUpload.uploadFile(bytes, category);
        return fileRepository.save(metaFile);
    }

    @Override
    public String beginFile(FileCategory category) {
        return fileUpload.createFilePath(category);
    }

    @Override
    public void appendFile(byte[] chunk, String filePath) {
        fileUpload.appendFile(chunk, filePath);
    }
/*
    1. 레코드 데이터를 나눠서 가져온다
    2. 그만큼 데이터를 넣는다
    3. 10번쨰 까지 그건 좀어려운거같은데 요구사항에서 실패하면 삭ㅈ하라하새ㅓ...
    근데 이어쓰기가 실패요구사항이 있어서 실패하면 에러로그를 남기고 그 csv 파일을 삭제하라고
    좀많이 단순

    1. 테이블을 나눠서 임시저장
    2. 그 테이블을 읽어서 파일저장
 */
}
