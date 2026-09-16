package com.sb14.hrbank.web.controller.dto;

import com.sb14.hrbank.domain.entity.employeehistory.EmployeeChangeHistory;

import java.util.List;

public record CursorPageResponseChangeLogDto(
        List<ChangeLogDto> content,
        String nextCursor,
        Long nextIdAfter,
        int size,
        Long totalElements,
        boolean hasNext
) {
    public static CursorPageResponseChangeLogDto from(
            List<EmployeeChangeHistory> changeHistories,
            String nextCursor,
            Long nextIdAfter,
            int size,
            Long totalElements,
            boolean hasNext
    ) {
        return new CursorPageResponseChangeLogDto(
                changeHistories.stream()
                        .map(ChangeLogDto::from)
                        .toList(),
                nextCursor,
                nextIdAfter,
                size,
                totalElements,
                hasNext
        );
    }
}
