package com.sb14.hrbank.domain.repository.employee;

import com.sb14.hrbank.domain.entity.employee.Employee;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeQueryRepository {
    boolean existsByEmail(String email);

    /*
        모든 부서 정보가 아닌 부서 이름만을 가져오는거도 성능과 관련이 있을까
        OOM 조심하라는데 일단 ㄱ
     */
    @Query(value = """
                select
                  e.employee_id       as id,
                  e.employee_number   as employeeNumber,
                  e.employee_name     as name,
                  e.employee_email    as email,
                  d.department_name   as departmentName,
                  e.employee_position as position,
                  e.employee_hiredate as hireDate,
                  e.employee_status   as status
                        from employees as e
                        join departments as d
                        on e.department_id = d.department_id
            """, nativeQuery = true)
    List<EmployeeCsvForm> selectEmployeeInfoCsvForm();


    record EmployeeCsvForm(
        Long id,
        String employeeNumber,
        String name,
        String email,
        String departmentName,
        String position,
        LocalDate hireDate,
        String status
    ){

    }
}
