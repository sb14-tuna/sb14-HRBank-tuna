package com.sb14.hrbank.web.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sb14.hrbank.domain.entity.employeehistory.ChangeLogSortField;
import com.sb14.hrbank.domain.entity.employeehistory.EmployeeChangeHistoryType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.hibernate.query.SortDirection;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.springframework.util.StringUtils.hasText;

public record ChangeLogSearchRequest(
        String employeeNumber,
        EmployeeChangeHistoryType type,
        String memo,
        String ipAddress,
        LocalDateTime atFrom,
        LocalDateTime atTo,

        @Positive(message = "idAfter는 1 이상이어야 함")
        Long idAfter,
        String cursor,

        @Min(value = 1, message = "size는 1 이상이어야 함")
        Integer size,
        ChangeLogSortField sortField,
        SortDirection sortDirection
) {
    public ChangeLogSearchRequest {
        // 페이지 크기 : 기본값 10
        size = size == null ? 10 : size;

        // 정렬 필드 : 기본값 at
        sortField = sortField == null ? ChangeLogSortField.AT : sortField;

        // 정렬 방향 : 기본값 desc
        sortDirection = sortDirection == null ? SortDirection.DESCENDING : sortDirection;
    }

    // 시작일시 검색 범위 체크
    @JsonIgnore
    @AssertTrue(message = "종료일시는 시작일시보다 이를 수 없음")
    public boolean isAtRangeValid() {
        if(Objects.isNull(atFrom) || Objects.isNull(atTo)) {
            return true;
        }

        return !atFrom.isAfter(atTo);
    }

    // 커서와 idAfter는 둘다 없거나 둘다 있어야 함
    @JsonIgnore
    @AssertTrue(message = "cursor와 idAfter는 둘다 없거나 둘다 있어야 함")
    public boolean isCursorPairValid() {
        boolean hasCursor = hasText(cursor);
        boolean hasIdAfter = Objects.nonNull(idAfter);

        return hasCursor == hasIdAfter;
    }
}
