package com.sb14.hrbank.web.controller;

import com.sb14.hrbank.domain.entity.department.Department;
import com.sb14.hrbank.domain.entity.employee.Employee;
import com.sb14.hrbank.domain.service.department.DepartmentServiceImpl;
import com.sb14.hrbank.web.controller.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")
public class DepartmentApiController {
    private final DepartmentServiceImpl departmentService;

    @PostMapping
    public ResponseEntity<DepartmentDto> create(
            @Valid @RequestBody DepartmentCreateRequest request
    ) {
        DepartmentDto createResult = departmentService.createDepartment(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(createResult);
    }


    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> findById(
            @PathVariable Long id
    ) {
        DepartmentDto result = departmentService.findDepartmentById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseDepartmentDto> findAll(
            @Valid @ModelAttribute DepartmentQueryRequest querySearchRequest
    ) {
        CursorPageResponseDepartmentDto querySearchResult = departmentService.findAll(
                querySearchRequest.toCondition()
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(querySearchResult);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<DepartmentDto> update(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentUpdateRequest updateRequest
    ) {
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
