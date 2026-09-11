package com.sb14.hrbank.web.controller;

import com.sb14.hrbank.domain.entity.department.Department;
import com.sb14.hrbank.domain.entity.employee.Employee;
import com.sb14.hrbank.domain.service.department.DepartmentServiceImpl;
import com.sb14.hrbank.web.controller.dto.DepartmentCreateRequest;
import com.sb14.hrbank.web.controller.dto.DepartmentDto;
import com.sb14.hrbank.web.controller.dto.DepartmentUpdateRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeDto;
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
@RequestMapping("/api/department")
public class DepartmentApiController {
    private final DepartmentServiceImpl departmentService;

    @PostMapping
    public ResponseEntity<DepartmentDto> create(
            @Valid @RequestPart("department")DepartmentCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
            ) {
        DepartmentDto createResult = departmentService.createDepartment(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(createResult);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<DepartmentDto> update(
            @PathVariable Long id,
            @Valid @RequestPart DepartmentUpdateRequest updateRequest) {
        DepartmentDto updateResult = departmentService.updateDepartment(id, updateRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updateResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DepartmentDto> delete(
            @PathVariable Long id
    ) {
        departmentService.deleteDepartment(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(null);
    }
}
