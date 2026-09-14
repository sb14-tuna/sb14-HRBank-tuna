package com.sb14.hrbank.domain.repository;

import com.sb14.hrbank.domain.entity.employee.Employee;
import com.sb14.hrbank.web.controller.dto.EmployeeSearchRequest;

import java.util.List;

public interface EmployeeQueryRepository {
    List<Employee> findAllByCondition(EmployeeSearchRequest request);
    long countByCondition(EmployeeSearchRequest request);
}
