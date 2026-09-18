package com.sb14.hrbank.domain.entity.backuphistory;

import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "backup_histories")
@Getter
public class BackupHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "backup_history_seq")
    @SequenceGenerator(
        name = "backup_history_seq",
        sequenceName = "backup_histories_seq",
        allocationSize = 1
    )
    @Column(name = "backup_id")
    Long id;

    @Column(name = "backup_worker", nullable = false)
    String worker;

    @Column(name = "backup_started_at", nullable = false)
    Instant startedAt;

    @Column(name = "backup_ended_at", nullable = true)      // null 값은 현재 진행 중인 것
    Instant endedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "backup_state", nullable = false)
    BackupState state;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", unique = true)
    MetaFile metaFile;

    static public BackupHistory init(String worker){
        return BackupHistory.builder()
            .worker(worker)
            .state(BackupState.IN_PROGRESS)      // 초기값은 항상 진행 중
            .build();
    }

    public void startBackup(){
        validateUpdateState();
        this.startedAt = Instant.now();
    }

    public void completeBackup(){
        validateUpdateState();

        this.endedAt = Instant.now();
        this.state = BackupState.COMPLETED;
    }

    public void failBackup(){
        validateUpdateState();

        this.endedAt = Instant.now();
        this.state = BackupState.FAILED;
    }

    public void skipBackup(){
        validateUpdateState();

        this.endedAt = this.startedAt;
        this.state = BackupState.SKIPPED;       // 백업 기록이 필요없다 판단된 경우
    }

    public void attachMetaFile(MetaFile metaFile){
        this.metaFile = metaFile;
    }

    private void validateUpdateState(){
        if(!this.state.equals(BackupState.IN_PROGRESS)){
            throw new HrBankException(HrBankExceptionType.BACKUP_STATE_INVALID_CHANGE,
                "backup state cant change : " + this.state);
        }
    }
}
