package com.sb14.hrbank.domain.repository.employee;

import com.sb14.hrbank.domain.entity.employee.Employee;
import com.sb14.hrbank.domain.entity.employee.EmployeeGroupCount;
import com.sb14.hrbank.domain.entity.employee.EmployeeStatus;
import com.sb14.hrbank.domain.service.employee.EmployeeSearchCondition;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeQueryRepository {
    // 검색 조건을 주면: 조건에 맞는 List<Employee> 반환
    List<Employee> findPageByCondition(EmployeeSearchCondition condition);

    // 직원 Id를 주면: Employee 반환
    Optional<Employee> findByIdWithDetails(Long employeeId);

    // 검색 조건을 주면: 조건에 맞는 총 직원 수 반환
    long countByCondition(EmployeeSearchCondition condition);

    // 분포 그룹, 직원 상태를 주면: 직원 상태에 대해 필터링 한 List<항목, 수> 반환
    List<EmployeeGroupCount> findDistribution(
            String groupBy,
            EmployeeStatus status
    );

    // 상태 + 기간 안에 입사한 직원 수 반환
    long countByStatusAndHireDateRange(
            EmployeeStatus status,
            LocalDate fromDate,
            LocalDate toDate
    );
}
