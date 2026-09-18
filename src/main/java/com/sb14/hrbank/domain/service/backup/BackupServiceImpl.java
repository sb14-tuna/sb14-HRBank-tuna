package com.sb14.hrbank.domain.service.backup;

import com.sb14.hrbank.domain.entity.backuphistory.BackupHistory;
import com.sb14.hrbank.domain.entity.employeehistory.EmployeeChangeHistory;
import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import com.sb14.hrbank.domain.repository.backuphistory.BackupHistoryRepository;
import com.sb14.hrbank.domain.repository.employeehistory.EmployeeHistoryRepository;
import com.sb14.hrbank.domain.service.backuphistory.BackupHistoryService;
import com.sb14.hrbank.domain.service.file.FileService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class BackupServiceImpl implements BackupService {
    private final BackupHistoryService backupHistoryService;
    private final FileService fileService;
    private final BackupHistoryRepository backupHistoryRepository;
    private final EmployeeHistoryRepository employeeHistoryRepository;
    private final BackupFileSaver backupFileSaver;

    @PersistenceContext
    private EntityManager entityManager;

    /*
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
     */
    @Override
    @Transactional
    public BackupHistory startBackup(String worker){
        EmployeeChangeHistory employeeHistory = employeeHistoryRepository.findTopByOrderByUpdatedAtDesc()
            .orElseThrow( () -> new NoSuchElementException("직원 변경 레코드 값이 없음"));

        // 트랜잭션2
        BackupHistory backupHistory = entityManager.merge(backupHistoryService.createBackupHistory(worker));    // 새 트랜잭션에서 관리하던 객체를 이전 트랜잭션의 영속성 컨텍스트로 관리하고 싶음


        try{
            if(validateSkipBackup(employeeHistory)){
                backupHistory.skipBackup();
                return backupHistory;
            }
        }catch (DataAccessException e){
            log.error(" 백업 히스토리 백업 판단 중 디비 예외 발생 ");
            backupHistoryRepository.delete(backupHistory);

            // todo : 이벤트 발행(디비 예외시)
            throw e;
        }

        // 백업 진행
        try{
            Path csvFilePath = backupFileSaver.saveCsvFile();
            MetaFile metaFile = fileService.completeFile(csvFilePath, FileCategory.BACKUP_CSV);
            backupHistory.attachMetaFile(metaFile);
            backupHistory.completeBackup();
        }catch (Exception e){
            Path errorLogFilePath = backupFileSaver.saveErrorLogFile(worker, e.getMessage());
            MetaFile metaFile = fileService.completeFile(errorLogFilePath, FileCategory.ERROR_LOG);
            backupHistory.attachMetaFile(metaFile);
            backupHistory.failBackup();
        }

        return backupHistory;
    }

    private boolean validateSkipBackup(EmployeeChangeHistory employeeHistory) {
        Instant employeeUpdatedAt = localDateToInstant(employeeHistory.getUpdatedAt());

        return backupHistoryRepository.existsCompleteBackupHistoryByStartedAt(employeeUpdatedAt);
    }

    private Instant localDateToInstant(LocalDateTime updatedAt){
        return updatedAt.toInstant(ZoneOffset.UTC);
    }
}
