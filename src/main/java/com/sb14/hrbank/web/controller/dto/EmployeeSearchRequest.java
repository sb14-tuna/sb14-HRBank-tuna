package com.sb14.hrbank.web.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sb14.hrbank.domain.entity.employee.EmployeeStatus;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import static org.springframework.util.StringUtils.hasText;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class EmployeeSearchRequest {

    // 자유 입력란
    String nameOrEmail;
    String employeeNumber;
    String departmentName;
    String position;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate hireDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate hireDateTo;

    EmployeeStatus status;

    @Positive(message = "idAfter는 1 이상이어야 함")
    Long idAfter;
    String cursor;  // 이전 페이지  마지막 직원의 정렬 필드값

    @NotNull
    @Min(value = 1, message = "size는 1 이상이어야 함")
    Integer size = 10;  // 최대 페이지 크기 뭐로 할지? 일단 10

    @Pattern(
            regexp = "^(name|employeeNumber|hireDate)$",
            message = "지원하지 않는 정렬 필드"
    )
    String sortField = "name";

    @Pattern(
            regexp = "^(asc|desc)$",
            message = "지원하지 않는 정렬 방향"
    )
    String sortDirection = "asc";



    // 입사일 검색 범위 체크
    @JsonIgnore
    @AssertTrue(message = "입사일 종료일은 시작일보다 이를 수 없음")
    public boolean isHireDateRangeValid() {
        if (Objects.isNull(hireDateFrom) || Objects.isNull(hireDateTo)) {
            return true;
        }
        return !hireDateFrom.isAfter(hireDateTo);
    }

    // 커서와 idAfter는 둘다 없거나 둘다 있어야 함
    @JsonIgnore
    @AssertTrue(message = "cursor와 idAfter는 둘다 없거나 둘다 있어야 함")
    public boolean isCursorPairValid() {
        boolean hasCursor = hasText(cursor);
        boolean hasIdAfter = Objects.nonNull(idAfter);

        return hasCursor == hasIdAfter;
    }

    @JsonIgnore
    @AssertTrue(message = "입사일 cursor는 yyyy-MM-dd 형식이어야 함")
    public boolean isCursorFormatValid() {
        if (!hasText(cursor) || !"hireDate".equals(sortField)) {
            return true;
        }

        try {
            LocalDate.parse(cursor);
            return true;
        } catch (DateTimeParseException exception) {
            return false;
        }
    }
}
