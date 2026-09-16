package com.sb14.hrbank.web.controller;

import com.sb14.hrbank.domain.service.employeehistory.EmployeeChangeHistoryService;
import com.sb14.hrbank.web.controller.dto.ChangeLogDetailDto;
import com.sb14.hrbank.web.controller.dto.ChangeLogSearchRequest;
import com.sb14.hrbank.web.controller.dto.CursorPageResponseChangeLogDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/change-logs")
public class EmployeeChangeHistoryApiController {
    private final EmployeeChangeHistoryService changeHistoryService;

    @GetMapping
    public ResponseEntity<CursorPageResponseChangeLogDto> findAll(@Valid @ModelAttribute ChangeLogSearchRequest request) {
        CursorPageResponseChangeLogDto response = changeHistoryService.findAll(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChangeLogDetailDto> findDetailById(@PathVariable Long id) {
        ChangeLogDetailDto response = changeHistoryService.findDetailById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate
            ) {
        LocalDateTime now = LocalDateTime.now();

        fromDate = fromDate == null ? now.minusDays(7) : fromDate;
        toDate = toDate == null ? now : toDate;

        Long count = changeHistoryService.count(fromDate, toDate);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(count);
    }
}
