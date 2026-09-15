package com.sb14.hrbank.web.controller;

import com.sb14.hrbank.domain.entity.employee.EmployeeStatus;
import com.sb14.hrbank.domain.service.employee.EmployeeService;
import com.sb14.hrbank.web.controller.dto.*;
import com.sb14.hrbank.web.controller.dto.employee.management.EmployeeCreateRequest;
import com.sb14.hrbank.web.controller.dto.employee.management.EmployeeDto;
import com.sb14.hrbank.web.controller.dto.employee.management.EmployeeQueryRequest;
import com.sb14.hrbank.web.controller.dto.employee.management.EmployeeUpdateRequest;
import com.sb14.hrbank.web.controller.dto.employee.stats.EmployeeCountRequest;
import com.sb14.hrbank.web.controller.dto.employee.stats.EmployeeDistributionDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        EmployeeDto createResult = employeeService.createEmployee(request, profile);
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
            @Valid @ModelAttribute EmployeeQueryRequest querySearchRequest
    ) {
        CursorPageResponseEmployeeDto querySearchResult = employeeService.findAll(
                querySearchRequest.toCondition()
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(querySearchResult);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeDto> update(
            @PathVariable Long id,
            @Valid @RequestPart("employee") EmployeeUpdateRequest updateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        EmployeeDto updateResult = employeeService.updateEmployee(id, updateRequest, profile);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updateResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        employeeService.deleteEmployee(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
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
