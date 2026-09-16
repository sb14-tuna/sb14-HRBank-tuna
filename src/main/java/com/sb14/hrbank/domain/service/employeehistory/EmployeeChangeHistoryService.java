package com.sb14.hrbank.domain.service.employeehistory;

import com.sb14.hrbank.web.controller.dto.ChangeLogDetailDto;
import com.sb14.hrbank.web.controller.dto.ChangeLogSearchRequest;
import com.sb14.hrbank.web.controller.dto.CursorPageResponseChangeLogDto;

import java.time.LocalDateTime;

public interface EmployeeChangeHistoryService {
    // 기존 수정 이력 조회
    CursorPageResponseChangeLogDto findAll(ChangeLogSearchRequest request);

    // 수정 이력 상세 조회
    ChangeLogDetailDto findDetailById(Long id);

    // 수정 이력 건수 조회
    Long count(LocalDateTime fromDate, LocalDateTime toDate);
}
