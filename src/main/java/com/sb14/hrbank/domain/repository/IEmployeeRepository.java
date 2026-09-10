package com.sb14.hrbank.domain.repository;

import com.sb14.hrbank.domain.entity.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmail(String email);
}
