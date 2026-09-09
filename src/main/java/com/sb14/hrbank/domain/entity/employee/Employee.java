package com.sb14.hrbank.domain.entity.employee;

import com.sb14.hrbank.domain.entity.department.Department;
import com.sb14.hrbank.domain.entity.file.MetaFile;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(
            name = "employee_seq",
            sequenceName = "employee_seq",
            allocationSize = 1
    )
    @Column(name = "employee_id")
    Long id;

    @Column(name = "employee_name", nullable = false)
    String name;

    @Column(
            name = "employee_email",
            unique = true,
            nullable = false
    )
    String email;

    // 랜덤 10자, 자동부여, 수정 불가능
    @Column(
            name = "employee_number",
            unique = true,
            nullable = false,
            updatable = false
    )
    String employeeNumber = String.valueOf(
            ThreadLocalRandom.current()
                    .nextLong(1_000_000_000L, 10_000_000_000L)
    );

    @Column(
            name = "employee_position",
            nullable = false,
            length = 10
    )
    String position;

    @Column(name = "employee_hiredate", nullable = false)
    LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_status", nullable = false)
    EmployeeStatus status;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    Department department;

    @OneToOne
    @JoinColumn(name = "file_id", nullable = true)
    MetaFile profileImage;


    public static Employee init(
            String name,
            String email,
            String position,
            LocalDate hireDate,
            EmployeeStatus status,
            Department department,
            MetaFile profileImage
    ) {
        return Employee.builder()
                .name(name)
                .email(email)
                .position(position)
                .hireDate(hireDate)
                .status(status)
                .department(department)
                .profileImage(profileImage)
                .build();
    }
}
