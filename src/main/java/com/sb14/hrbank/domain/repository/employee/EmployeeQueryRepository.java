package com.sb14.hrbank.domain.repository.employee;

import com.sb14.hrbank.domain.entity.employee.Employee;
import com.sb14.hrbank.domain.entity.employee.EmployeeGroupCount;
import com.sb14.hrbank.domain.entity.employee.EmployeeStatus;
import com.sb14.hrbank.domain.service.employee.EmployeeSearchCondition;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeQueryRepository {
    List<Employee> findAllByCondition(EmployeeSearchCondition condition);

    long countByCondition(EmployeeSearchCondition condition);

    List<EmployeeGroupCount> findByEmployeeDistribution(
            String groupBy,
            EmployeeStatus status
    );

    long countEmployeesByStatusAndDateRange(
            EmployeeStatus status,
            LocalDate fromDate,
            LocalDate toDate
    );
}
