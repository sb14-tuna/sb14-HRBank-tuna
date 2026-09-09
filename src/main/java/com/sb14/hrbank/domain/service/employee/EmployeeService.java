package com.sb14.hrbank.domain.service.employee;

import com.sb14.hrbank.domain.entity.department.Department;
import com.sb14.hrbank.domain.entity.employee.Employee;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import com.sb14.hrbank.domain.repository.IDepartmentRepository;
import com.sb14.hrbank.domain.repository.IEmployeeRepository;
import com.sb14.hrbank.web.controller.dto.EmployeeCreateRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeDto;
import com.sb14.hrbank.web.controller.dto.EmployeeUpdateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService implements IEmployeeService {

    private final IEmployeeRepository employeeRepository;
    private final IDepartmentRepository departmentRepository;

    @Override
    @Transactional
    public EmployeeDto createEmployee(
            EmployeeCreateRequest createRequest,
            MultipartFile profile
    ) {
        if (employeeRepository.existsByEmail(createRequest.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + createRequest.getEmail());
        }

        Department department = departmentRepository.findById(createRequest.getDepartmentId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 부서입니다: "+ createRequest.getDepartmentId()));

        MetaFile profileImage = null;

        Employee employee = Employee.init(
                createRequest.getName(),
                createRequest.getEmail(),
                createRequest.getPosition(),
                createRequest.getHireDate(),
                department,
                profileImage
        );

        Employee createdEmployee = employeeRepository.save(employee);

        // todo: 생성 이력 히스토리 테이블에 적재

        return EmployeeDto.from(createdEmployee);
    }

    @Override
    public EmployeeDto findById(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 직원: " + employeeId));    // 404

        return EmployeeDto.from(employee);
    }

    @Override
    public EmployeeDto updateEmployee(
            Long employeeId,
            EmployeeUpdateRequest updateRequest
    ) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 직원: " + employeeId));

        // 이메일 변경 됐을 때만 중복 검사
        if (!employee.getEmail().equals(updateRequest.getEmail())
            && employeeRepository.existsByEmail(updateRequest.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일: " + updateRequest.getEmail());
        }

        // 부서 Id로 실제 부서 객체 불러오기
        Department department = departmentRepository.findById(updateRequest.getDepartmentId())
                .orElseThrow(() -> new NoSuchElementException( "존재하지 않는 부서: " + updateRequest.getDepartmentId()));

        employee.update(
                updateRequest.getName(),
                updateRequest.getEmail(),
                updateRequest.getPosition(),
                updateRequest.getHireDate(),
                updateRequest.getStatus(),
                department
        );

        // todo: 업데이트 이력 히스토리 테이블에 적재

        return EmployeeDto.from(employee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 직원: " + employeeId));    // 404

        // todo: 삭제 이력 히스토리 테이블에 적재

        employeeRepository.delete(employee);
    }
}
