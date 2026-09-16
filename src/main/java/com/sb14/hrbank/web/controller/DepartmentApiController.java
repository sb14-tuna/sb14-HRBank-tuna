package com.sb14.hrbank.web.controller;

import com.sb14.hrbank.domain.service.department.DepartmentSearchCondition;
import com.sb14.hrbank.domain.service.department.DepartmentServiceImpl;
import com.sb14.hrbank.web.controller.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
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
            @RequestParam(required = false)
            String nameOrDescription,

            @RequestParam(required = false)
            @Positive(message = "idAfter는 1 이상이어야 합니다.")
            Long idAfter,

            @RequestParam(required = false)
            String cursor,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "size는 1 이상이어야 합니다.")
            Integer size,

            @RequestParam(defaultValue = "establishedDate")
            @Pattern(
                    regexp = "^(name|establishedDate)$",
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
        DepartmentSearchCondition condition = DepartmentSearchCondition.of(
                nameOrDescription,
                idAfter,
                cursor,
                size,
                sortField,
                sortDirection
        );

        CursorPageResponseDepartmentDto querySearchResult = departmentService.findAll(condition);

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
