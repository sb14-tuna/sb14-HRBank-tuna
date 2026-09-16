package com.sb14.hrbank.web.controller.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CursorPageResponseDepartmentDto {
    List<DepartmentDto> content;
    String nextCursor;
    Long nextIdAfter;
    Integer size;
    Long totalElements;
    Boolean hasNext;

    public static CursorPageResponseDepartmentDto from(
            List<DepartmentDto> content,
            String nextCursor,
            Long nextIdAfter,
            long totalElements,
            boolean hasNext
    ) {
        return new CursorPageResponseDepartmentDto(
                content, nextCursor, nextIdAfter, content.size(), totalElements, hasNext
        );
    }
}
