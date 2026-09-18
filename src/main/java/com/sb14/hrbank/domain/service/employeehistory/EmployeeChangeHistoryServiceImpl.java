package com.sb14.hrbank.domain.service.employeehistory;

import com.sb14.hrbank.domain.entity.employeehistory.EmployeeChangeHistory;
import com.sb14.hrbank.domain.repository.EmployeeChangeHistoryRepository;
import com.sb14.hrbank.web.controller.dto.ChangeLogDetailDto;
import com.sb14.hrbank.web.controller.dto.ChangeLogSearchRequest;
import com.sb14.hrbank.web.controller.dto.CursorPageResponseChangeLogDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeChangeHistoryServiceImpl implements EmployeeChangeHistoryService {
    private final EmployeeChangeHistoryRepository changeHistoryRepository;

    @Override
    public CursorPageResponseChangeLogDto findAll(ChangeLogSearchRequest request) {
        List<EmployeeChangeHistory> changeHistories = changeHistoryRepository.findAllByCondition(request);
        boolean hasNextPage = changeHistories.size() > request.size();

        if (hasNextPage) {
            changeHistories.remove(changeHistories.size() - 1);
        }

        String nextCursor = null;
        Long nextIdAfter = null;

        if (hasNextPage) {
            EmployeeChangeHistory lastChangeHistory = changeHistories.get(changeHistories.size() - 1);

            nextCursor = switch (request.sortField()) {
                case IP_ADDRESS -> lastChangeHistory.getIpAddress();
                case AT -> lastChangeHistory.getUpdatedAt().toString();
            };

            nextIdAfter = lastChangeHistory.getId();
        }

        Long totalElements = changeHistoryRepository.countByCondition(request);

        return CursorPageResponseChangeLogDto.from(
                changeHistories,
                nextCursor,
                nextIdAfter,
                request.size(),
                totalElements,
                hasNextPage
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ChangeLogDetailDto findDetailById(Long id) {
        EmployeeChangeHistory changeHistory = changeHistoryRepository.findDetailById(id)
                .orElseThrow(() -> new IllegalArgumentException("변경 이력을 찾을 수 없습니다."));

        return ChangeLogDetailDto.from(changeHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public Long count(LocalDateTime fromDate, LocalDateTime toDate) {
        return changeHistoryRepository.countByDateRange(fromDate, toDate);
    }


}

