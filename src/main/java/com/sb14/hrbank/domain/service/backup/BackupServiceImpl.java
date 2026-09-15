package com.sb14.hrbank.domain.service.backup;

import com.sb14.hrbank.domain.entity.backuphistory.BackupHistory;
import com.sb14.hrbank.domain.entity.employeehistory.EmployeeChangeHistory;
import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import com.sb14.hrbank.domain.repository.EmployeeRepository;
import com.sb14.hrbank.domain.repository.EmployeeRepository.EmployeeCsvForm;
import com.sb14.hrbank.domain.repository.FileRepository;
import com.sb14.hrbank.domain.repository.backuphistory.BackupHistoryRepository;
import com.sb14.hrbank.domain.repository.employeehistory.EmployeeHistoryRepository;
import com.sb14.hrbank.domain.service.backuphistory.BackupHistoryService;
import com.sb14.hrbank.domain.service.file.fileupload.FileUpload;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
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
    private final EmployeeRepository employeeRepository;
    private final BackupHistoryRepository backupHistoryRepository;
    private final EmployeeHistoryRepository employeeHistoryRepository;
    private final FileGenerator fileGenerator;
    private final FileUpload fileUpload;
    private final FileRepository fileRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public BackupHistory startBackup(String worker){
        // 새로운 트랜잭션
        BackupHistory backupHistory = entityManager.merge(backupHistoryService.createBackupHistory(worker));    // 새 트랜잭션에서 관리하던 객체를 이전 트랜잭션의 영속성 컨텍스트로 관리하고 싶음

        EmployeeChangeHistory employeeHistory = employeeHistoryRepository.findTopByOrderByUpdatedAtDesc()
            .orElseThrow( () -> new NoSuchElementException("직원 변경 레코드 값이 없음"));

        try{
            // 백업이 필요없다면
            if(validateSkipBackup(employeeHistory)){
                backupHistory.skipBackup();
                return backupHistory;
            }
        }catch (DataAccessException e){
            log.error(" 백업 히스토리 백업 판단 중 예외 발생 ");
            // 오토커밋된거 삭제
            backupHistoryRepository.delete(backupHistory);

            // todo : 이벤트 발행(디비 예외시)
            throw new RuntimeException(" 서버 내부 오류 발생 ");
        }
        // 파일서비스 - 생성 파일 업로드(로컬) + 파일 디비 저장 만 담당하도록 변경
        // 새로이 백업서비스 -> 파일생성 파일 서비스를 호출하게 하려는데
        // 코드가 추상적이게 바뀜. 덮어쓰기 구조라 기존 crud + create 만 있었는데
        // append beginBackup 등 이런게 생겨서
        //      - ( 한번에 저장이 아닌 일부만 저장하게 하려니까 - 페이징으로 )
        // 암튼 이런거때문에
        //      예외 터지면 에러로그 저장하려니까 로직이 너무 보기힘드네요
        // 이거


        // 백업 해야될 경우
        Long chunkSize = 5000L;
        Long idAfter = 0L;
        FileCategory category = FileCategory.BACKUP_CSV;
        boolean firstPage = true;
        byte[] bytesToFile = null;
        String filePath = fileUpload.createFilePath(category);
        try{
            while(true){
                List<EmployeeCsvForm> employeeCsvForms = employeeRepository.selectEmployeeInfoCsvFormPage(idAfter, chunkSize);
                if(employeeCsvForms.isEmpty()) break;

                bytesToFile = fileGenerator.createCsvFile(employeeCsvForms, firstPage);
                firstPage = false;
                idAfter = employeeCsvForms.get(employeeCsvForms.size() - 1).id();
                // 파일 서비스 호출
                fileUpload.appendFile(bytesToFile, filePath);
            }

            // 완성되면
            MetaFile metaFile = fileUpload.completeFile(filePath, category);

            fileRepository.save(metaFile);
            backupHistory.attachMetaFile(metaFile);
            backupHistory.completeBackup();
        }catch (Exception e){
            log.error(" 백업 파일 생성 중 오류 발생 ", e);

            bytesToFile = fileGenerator.createErrorLogFile(worker, e.getMessage());
            category = FileCategory.ERROR_LOG;
            MetaFile metaFile = fileUpload.uploadFile(bytesToFile, category);

            fileRepository.save(metaFile);
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
        return updatedAt
            .atZone(ZoneId.of("Asia/Seoul"))
            .toInstant();
    }
}
