package com.sb14.hrbank.web.controller;

import com.sb14.hrbank.domain.entity.employee.EmployeeHireDateCount;
import com.sb14.hrbank.domain.entity.employee.EmployeeStatus;
import com.sb14.hrbank.domain.service.employee.EmployeeSearchCondition;
import com.sb14.hrbank.domain.service.employee.EmployeeService;
import com.sb14.hrbank.web.controller.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeCreateRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeDto;
import com.sb14.hrbank.web.controller.dto.EmployeeUpdateRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeCountRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeDistributionDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeApiController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeDto> create(
            @Valid @RequestPart("employee") EmployeeCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile,
            HttpServletRequest httpRequest
    ) {
        String ipAddress = httpRequest.getRemoteAddr();

        EmployeeDto createResult = employeeService.createEmployee(request, profile, ipAddress);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(createResult);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> findById(
            @PathVariable Long id
    ) {
        EmployeeDto findResult = employeeService.findById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(findResult);
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseEmployeeDto> findAll(
            @RequestParam(required = false)
            String nameOrEmail,

            @RequestParam(required = false)
            String employeeNumber,

            @RequestParam(required = false)
            String departmentName,

            @RequestParam(required = false)
            String position,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hireDateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hireDateTo,

            @RequestParam(required = false)
            EmployeeStatus status,

            @RequestParam(required = false)
            @Positive(message = "idAfter는 1 이상이어야 합니다.")
            Long idAfter,

            @RequestParam(required = false)
            String cursor,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "size는 1 이상이어야 합니다.")
            Integer size,

            @RequestParam(defaultValue = "name")
            @Pattern(
                    regexp = "^(name|employeeNumber|hireDate)$",
                    message = "지원하지 않는 정렬 필드입니다."
            )
            String sortField,

            @RequestParam(defaultValue = "asc")
            @Pattern(
                    regexp = "^(asc|desc)$",
                    message = "지원하지 않는 정렬 방향입니다."
            )
            String sortDirection
    ) {
        EmployeeSearchCondition condition =
                EmployeeSearchCondition.of(
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

        CursorPageResponseEmployeeDto findAllResult = employeeService.findAll(condition);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(findAllResult);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeDto> update(
            @PathVariable Long id,
            @Valid @RequestPart("employee") EmployeeUpdateRequest updateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile,
            HttpServletRequest httpRequest
    ) {
        String ipAddress = httpRequest.getRemoteAddr();

        EmployeeDto updateResult = employeeService.updateEmployee(id, updateRequest, profile, ipAddress);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updateResult);
    }

    @DeleteMapping("/{id}")
   public ResponseEntity<Void> delete(
            @PathVariable Long id,
            HttpServletRequest httpRequest
    ) {
        String ipAddress = httpRequest.getRemoteAddr();

        employeeService.deleteEmployee(id, ipAddress);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    //@GetMapping("/stats/trend")
    public ResponseEntity<List<EmployeeTrendDto>> getEmployeeTrend(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @RequestParam(defaultValue = "month")
            @Pattern(
                    regexp = "^(day|week|month|quarter|year)$",
                    message = "집계 단위는 day, week, month, quarter, year 중 하나여야 합니다."
            )
            String unit
    ) {
        List<EmployeeTrendDto> employeeHireDateCounts = employeeService.getEmployeeTrend(unit);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(employeeHireDateCounts);
    }

    @GetMapping("/stats/distribution")
    public ResponseEntity<List<EmployeeDistributionDto>> getDistribution(
            @RequestParam(defaultValue = "department") String groupBy,
            @RequestParam(defaultValue = "ACTIVE") EmployeeStatus status
    ) {
        List<EmployeeDistributionDto> result = employeeService.getEmployeeDistribution(groupBy, status);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getEmployeeCount(
            @Valid @ModelAttribute EmployeeCountRequest countRequest
    ) {
        Long employeeCount = employeeService.countEmployeeByStatusAndDateRange(countRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(employeeCount);
    }
}
