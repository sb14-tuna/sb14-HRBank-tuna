package com.sb14.hrbank.web.controller.dto;

import com.sb14.hrbank.web.controller.dto.employee.management.EmployeeDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CursorPageResponseEmployeeDto {
    List<EmployeeDto> content;
    String nextCursor;
    Long nextIdAfter;
    Integer size;
    Long totalElements;
    Boolean hasNext;

    public static CursorPageResponseEmployeeDto from(
            List<EmployeeDto> content,
            String nextCursor,
            Long nextIdAfter,
            long totalElements,
            boolean hasNext
    ) {
        return new CursorPageResponseEmployeeDto(
                content,
                nextCursor,
                nextIdAfter,
                content.size(),
                totalElements,
                hasNext
        );
    }
}
