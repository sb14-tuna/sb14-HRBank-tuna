package com.sb14.hrbank.domain.repository.employee;

import com.sb14.hrbank.domain.entity.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeQueryRepository {
    boolean existsByEmail(String email);
}
