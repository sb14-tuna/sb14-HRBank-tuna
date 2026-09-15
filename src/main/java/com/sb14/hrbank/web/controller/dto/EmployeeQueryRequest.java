package com.sb14.hrbank.web.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sb14.hrbank.domain.entity.employee.EmployeeStatus;
import com.sb14.hrbank.domain.service.employee.EmployeeSearchCondition;
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
public class EmployeeQueryRequest {

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

//    Request URL
//    http://localhost:8080/api/employees?employeeNumber=&size=10&sortField=hireDate&sortDirection=desc&cursor=2023-01-17

    // 프론트는 cursor만 보내기도 함
    // idAfter만 있을 수는 없음
    @JsonIgnore
    @AssertTrue(message = "idAfter를 사용하려면 cursor가 필요함")
    public boolean isCursorValid() {
        return Objects.isNull(idAfter) || hasText(cursor);
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

    public EmployeeSearchCondition toCondition() {
        return new EmployeeSearchCondition(
                nameOrEmail,
                employeeNumber,
                departmentName,
                position,
                hireDateFrom,
                hireDateTo,
                status,
                idAfter,
                cursor,
                size,
                sortField,
                sortDirection
        );
    }
}
