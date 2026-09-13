package com.sb14.hrbank.domain.repository.backuphistory;

import com.sb14.hrbank.domain.entity.backuphistory.BackupHistory;
import com.sb14.hrbank.domain.entity.backuphistory.BackupState;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/*
    2-1. 상태가 백업 완료됨을 필터링
    2-2. 시작시간을 기준으로 마지막 수정시간과 비교해서 레코드를 추출 (시작시간과 종료시간 사이에 수정이 발생할 수 있기에)
    2-3. 레코드가 없다면 생성이 필요하다 판단
 */
@Repository
public interface BackupHistoryRepository extends JpaRepository<BackupHistory, Long> {
    @Query(value = """
                SELECT EXISTS (
                   SELECT 1
                   FROM backup_histories
                   WHERE backup_state = 'COMPLETED'
                     AND backup_started_at > :updatedAt
               )
            """, nativeQuery = true)
    boolean existsCompleteBackupHistoryByStartedAt(
        @Param("updatedAt") Instant updatedAt
    );


    Optional<BackupHistory> findTopByStateOrderByStartedAtDesc(BackupState state);
}
