package com.sb14.hrbank.domain.repository;

import com.sb14.hrbank.domain.entity.employeehistory.EmployeeChangeHistory;
import com.sb14.hrbank.web.controller.dto.ChangeLogSearchRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmployeeChangeHistoryQueryRepository {
    List<EmployeeChangeHistory> findAllByCondition(ChangeLogSearchRequest request);
    Long countByCondition(ChangeLogSearchRequest request);
    Optional<EmployeeChangeHistory> findDetailById(Long id);
    Long countByDateRange(LocalDateTime fromDate, LocalDateTime toDate);
}
