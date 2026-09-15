package com.sb14.hrbank.web.controller.dto.employee.stats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sb14.hrbank.domain.entity.employee.EmployeeStatus;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeCountRequest {

    EmployeeStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate toDate;

    @JsonIgnore
    @AssertTrue(message = "입사일 종료일은 시작일보다 이를 수 없음")
    public boolean isHireDateRangeValid() {
        if (Objects.isNull(fromDate) || Objects.isNull(toDate)) {
            return true;
        }
        return !fromDate.isAfter(toDate);
    }
}
