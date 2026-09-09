package com.sb14.hrbank.domain.entity.employee;

import com.sb14.hrbank.domain.entity.department.Department;
import com.sb14.hrbank.domain.entity.employeehistory.EmployeeHistory;
import com.sb14.hrbank.domain.entity.file.File;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "employees")
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) //
    @Column(name = "employee_id")
    Long id;

    @Column(name = "employee_name")
    String name;

    @Column(name = "employee_email")
    String email;

    @Column(name = "employee_number")
    String employeeNumber;

    @Column(name = "employee_position")
    String position;

    @Column(name = "employee_hiredate")
    LocalDate hireDate;


    //EmployeeStatus status;        // todo : 이넘 만들기

    @ManyToOne
    @JoinColumn(name = "department_id")
    Department department;

    @OneToOne
    @JoinColumn(name = "file_id")
    File profileImage;
}
