package com.sb14.hrbank.web.controller;

import com.sb14.hrbank.domain.service.employee.EmployeeService;
import com.sb14.hrbank.web.controller.dto.EmployeeCreateRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeDto;
import com.sb14.hrbank.web.controller.dto.EmployeeUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


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
    public ResponseEntity<EmployeeDto> delete(
            @PathVariable Long id
    ) {
        employeeService.deleteEmployee(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(null);
    }
}
