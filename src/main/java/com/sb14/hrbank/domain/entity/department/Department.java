package com.sb14.hrbank.domain.entity.department;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "departments")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "department_seq")
    @SequenceGenerator(
            name = "department_seq",
            sequenceName = "department_seq",
            allocationSize = 1
    )
    @Column(name = "department_id")
    Long id;

    @Column(name = "department_name", nullable = false, unique = true)
    String name;

    @Column(name = "department_description")
    String description;

    @Column(name = "established_date", nullable = false)
    LocalDate establishedDate;

    public static Department init(String name, String description, LocalDate establishedDate) {
        return Department.builder()
                .name(name)
                .description(description)
                .establishedDate(establishedDate)
                .build();
    }
    public void updateDepartmentInfo(String name, String description, LocalDate establishedDate) {
        this.name = name;
        this.description = description;
        this.establishedDate = establishedDate;
    }
}
