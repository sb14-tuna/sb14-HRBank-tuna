package com.sb14.hrbank.domain.service.file;

import com.sb14.hrbank.domain.entity.backuphistory.BackupHistory;
import com.sb14.hrbank.domain.entity.employeehistory.EmployeeChangeHistory;
import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import org.springframework.web.multipart.MultipartFile;
import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import com.sb14.hrbank.domain.repository.FileRepository;


import com.sb14.hrbank.domain.repository.backuphistory.BackupHistoryRepository;
import com.sb14.hrbank.domain.repository.employeehistory.EmployeeHistoryRepository;
import com.sb14.hrbank.domain.service.backuphistory.BackupHistoryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
    파일 서비스를 작업 중에 이걸 독립 서비스로 두는게맞나 싶음
    이 요구사항은 일단 메인주체가 아닌 서브 주체로 호출되고 저장 api(직원, 백업로그)를 통해 호출되는데 단독 서비스가 아니라 생각해서
    추가로 서비스를 두면 서비스에서 서비스를 호출하는게 도메인서비스가 아닌 유효 비즈니스 서비스를 호출한다는게 아닌거같아서 파일 저장 유틸 클래스로 변경

    근데 다시 또 생각해보면 서비스가 독립 api 를 담당하는게 아니여도 파일 저장 실페에 따른 처리를 담당해야하기에 서비스를 두는게 맞는거같음
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    private final FileUpload fileUpload;

    private final BackupHistoryService backupHistoryService;
    private final BackupHistoryRepository backupHistoryRepository;
    private final EmployeeHistoryRepository employeeHistoryRepository;
    private final FileGenerator fileGenerator;

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



    /*
        책임 안나누고 작업해보기

        요청 흐름
            1. 백업 요청이 들어온다 ( 시스템 배치프로세스 또는 관리자의 요청 )
            2. 백업 생성 메서드(트랜잭션 시작)
            3. 히스토리 서비스(새로운 트랜잭션 생성)의 생성 메서드 호출
            4. 백업 파일 생성 판단
                4-1. 생성이 불필요하다
                4-2. 생성이 필요하다
            5-1. 생성이 불필요할 경우
                - 히스토리 서비스의 상태변경 메서드(새로운 트랜잭션)를 호출한다
                - 메서드 종료
            5-2. 생성이 필요할 경우
                - 파일 생성 유틸클래스에서 생성 메서드를 호출한다
                    - 트라이캐치 구문 내에 작성한다
                    - 생성이 정상 동작했으면 히스토리 서비스의 상태를 종료로 변경하고 메서드 종료
                    - 생성이 비정상적으로 끝났으면 파일생성클래스에서 예외를 던지게하고 이를 잡아 상태를 실패로 변경하고 에러로그 파일 생성 로직을 호출한다,
                        - 에러로그 파일 생성도 실패하면? ㅁ?ㄹ


       추가
        생성이 필요없다 판단 방법(스킵)
            1. 직원 수정 이력 관리 테이블에서 마지막 수정시간을 가져온다.
            2. 그 시간을 기준으로 데이터 백업 이력을 탐색한다.
                2-1. 상태가 백업 완료됨을 필터링
                2-2. 시작시간을 기준으로 마지막 수정시간과 비교해서 레코드를 추출 (시작시간과 종료시간 사이에 수정이 발생할 수 있기에)
                2-3. 레코드가 없다면 생성이 필요하다 판단

        트랜잭션 분리
            1. 파일 생성이 실패하더라도 이력은 남아야한다
            2. 트랜잭션 구분
                2-1. 백업요청 -> 파일 생성 -> 디비에 메타파-일 저장과 실제 파일 업로드를 트랜잭션 1
                2-2. 히스토리생성 및 상태 변경 -> 트랜잭션 2
            todo : csv write 중에 실패 시 복구 프로세스 나중에 생각해보기
     */

    @Override
    @Transactional
    public BackupHistory startBackup(String worker){
        // 트랜잭션 2
        BackupHistory backupHistory = entityManager.merge(backupHistoryService.createBackupHistory(worker));    // 새 트랜잭션에서 관리하던 객체를 이전 트랜잭션의 영속성 컨텍스트로 관리하고 싶음

        // 백업판단 ( 직원 수정 서비스에서 메서드를 정의하고 가져올까? -> 따로 실패 시나리오가 떠오르지않음. 그냥 레코드가 아예없거나 디비 통신 예외밖에 없음. 바로 리포지토리 호출하는 것으로 결정)
        EmployeeChangeHistory employeeHistory = employeeHistoryRepository.findTopByOrderByUpdatedAtDesc()
            .orElseThrow( () -> new NoSuchElementException("아예 레코드 값이 없음"));

        LocalDateTime updatedAt = employeeHistory.getUpdatedAt();
        Instant instant = updatedAt
            .atZone(ZoneId.of("Asia/Seoul"))
            .toInstant();
        try{
            // 백업이 필요없다면
            if(backupHistoryRepository.existsCompleteBackupHistoryByStartedAt(instant)){
                backupHistory.skipBackup();
                return backupHistory;
            }
        }catch (DataAccessException e){
            log.error(" 백업 히스토리 백업 판단 중 예외 발생 ");
            // 추가상황 : 요구사항에서는 백업 생성 중 오류가 터지면 그 파일을 삭제하고 에러로그파일 생성 및 히스토리 상태변경이였지만 그 전 과정에서 디비 예외가터지면
            // 오토 커밋이라 이미 히스토리는 들어가있음. 이걸 삭제해야함
            // 이렇게까지 안해도되나? web -> domain -> common
            // 삭제 트랜잭션을 넣고 싶었음. (이벤트 발행 해보기 ------------ todo : 나중에 )
            throw new RuntimeException(" 서버 내부 오류 발생 ");
        }

        // 백업 해야될 경우
        byte[] bytesToFile = null;
        FileCategory category = FileCategory.BACKUP_CSV;
        try{
            bytesToFile = fileGenerator.createCsvFile();
            backupHistory.completeBackup();
        }catch (Exception e){       // csv 생성중 예외 설정은 생각해보자
            log.error(" 백업 파일 생성 중 오류 발생 ");
            // 복구 로직
            // 앞서 생성된 csvfile 은 지워야하는데 제너레티어 내부에서 가능할까
            bytesToFile = fileGenerator.createErrorLogFile(worker, e.getMessage());
            // 에러로그파일 생성 실패는 아직 생각하지말자
            category = FileCategory.ERROR_LOG;
            backupHistory.failBackup();
        }

        // 실제 파일에서 디비 저장 및 필드 연결
        try{
            MetaFile metaFile = fileUpload.uploadFile(bytesToFile, category);
            fileRepository.save(metaFile);
            backupHistory.attachMetaFile(metaFile);
            return backupHistory;
        }catch (HrBankException e){
            // 복구서비스
            throw new HrBankException(HrBankExceptionType.FILE_UPLOAD_FAILED, "파일 업로드 실패");
        }
    }
}
