package com.sb14.hrbank.web.controller.dto;

import com.sb14.hrbank.domain.entity.backuphistory.BackupState;
import com.sb14.hrbank.domain.service.backuphistory.BackupSearchCondition;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BackupSearchRequestDto {
    String worker;
    BackupState status;
    Instant startedAtFrom;
    Instant startedAtTo;

    @Positive(message = "idAfter는 1 이상이어야 함")
    Long idAfter;
    String cursor;

    @NotNull
    @Min(value = 1, message = "size는 1 이상이어야 함")
    Integer size = 10;

    @Pattern(
        regexp = "^(startedAt|endedAt|status)$",
        message = "지원하지 않는 정렬 필드"
    )
    String sortField = "startedAt";

    @Pattern(
        regexp = "^(ASC|DESC)$",
        message = "지원하지 않는 정렬 방향"
    )
    String sortDirection = "DESC";

    public BackupSearchCondition toBackupSearchCondition(){
        return new BackupSearchCondition(
            worker,
            status,
            startedAtFrom,
            startedAtTo,
            idAfter,
            cursor,
            size,
            sortField,
            sortDirection
        );
    }
}
