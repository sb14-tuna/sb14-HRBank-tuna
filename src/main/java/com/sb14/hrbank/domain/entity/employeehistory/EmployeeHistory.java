package com.sb14.hrbank.domain.entity.employeehistory;

import com.sb14.hrbank.domain.entity.employee.Employee;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class EmployeeHistory {
    @Id
    Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    Employee employee;
}
