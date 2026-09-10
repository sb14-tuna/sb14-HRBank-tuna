package com.sb14.hrbank.domain.entity.employee;

import com.sb14.hrbank.domain.entity.department.Department;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "employees")
@Getter
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
            updatable = false,
            length = 10
    )
    String employeeNumber;


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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    Department department;


    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "file_id", nullable = true)
    MetaFile profileImage;


    // public 정적 메소드만을 통한 객체 생성
    public static Employee init(
            String name,
            String email,
            String position,
            LocalDate hireDate,
            Department department,
            MetaFile profileImage
    ) {
        return Employee.builder()
                .name(name)
                .email(email)
                .employeeNumber(generateEmployeeNumber())
                .position(position)
                .status(EmployeeStatus.ACTIVE)
                .hireDate(hireDate)
                .department(department)
                .profileImage(profileImage)
                .build();
    }

    public void update(
            String name,
            String email,
            String position,
            LocalDate hireDate,
            EmployeeStatus status,
            Department department,
            MetaFile profileImage
    ) {
        this.name = name;
        this.email = email;
        this.position = position;
        this.hireDate = hireDate;
        this.status = status;
        this.department = department;
        this.profileImage = profileImage;
    }

    public void softDelete() {  // 이렇게 해도 되는지???
        this.status = EmployeeStatus.DELETED;
    }

    private static String generateEmployeeNumber() {
        return String.valueOf(
                ThreadLocalRandom.current()
                        .nextLong(1_000_000_000L, 10_000_000_000L)
        );
    }


}
