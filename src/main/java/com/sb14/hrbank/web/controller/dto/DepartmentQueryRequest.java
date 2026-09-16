package com.sb14.hrbank.web.controller.dto;

import com.sb14.hrbank.domain.service.department.DepartmentSearchCondition;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepartmentQueryRequest {
    String nameOrDescription;   // 자유입력

    @Positive(message = "idAfter는 1 이상이어야 함")
    Long idAfter;
    String cursor;

    @NotNull
    @Min(value = 1, message = "size는 1 이상이어야 함")
    Integer size = 10;

    @Pattern(
            regexp = "^(name|establishedDate)$",
            message = "지원하지 않는 정렬 필드"
    )
    String sortField = "establishedDate";

    @Pattern(
            regexp = "^(asc|desc)$",
            message = "지원하지 않는 정렬 방향"
    )
    String sortDirection = "asc";


    public DepartmentSearchCondition toCondition() {
        return new DepartmentSearchCondition(
                nameOrDescription,
                idAfter,
                cursor,
                size,
                sortField,
                sortDirection
        );
    }
}
