package com.sb14.hrbank.domain.service.department;


import com.sb14.hrbank.domain.entity.department.Department;
import com.sb14.hrbank.domain.repository.DepartmentRepository;
import com.sb14.hrbank.domain.repository.EmployeeRepository;
import com.sb14.hrbank.web.controller.dto.DepartmentCreateRequest;
import com.sb14.hrbank.web.controller.dto.DepartmentDto;
import com.sb14.hrbank.web.controller.dto.DepartmentUpdateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public DepartmentDto createDepartment(DepartmentCreateRequest createRequest) {
        if (departmentRepository.existsByName(createRequest.getName())) {
            throw new IllegalArgumentException("이미 존재하는 부서명입니다: " + createRequest.getName());
        }
        Department department = Department.init(
                createRequest.getName(),
                createRequest.getDescription(),
                createRequest.getEstablishedDate()
        );
        Department createdDepartment = departmentRepository.save(department);
        return DepartmentDto.from(createdDepartment);
    }
    @Override
    @Transactional
    public DepartmentDto updateDepartment(Long departmentId, DepartmentUpdateRequest updateRequest) {
        Department department = departmentRepository.findById(departmentId).orElseThrow(() -> new NoSuchElementException("존재 하지 않는 부서: " + departmentId));

        if (!department.getName().equals(updateRequest.getName()) && departmentRepository.existsByName(updateRequest.getName())) {
            throw new IllegalArgumentException("이미 존재하는 부서명입니다.: " + updateRequest.getName());
        }
        department.updateDepartmentInfo(updateRequest.getName(), updateRequest.getDescription(),updateRequest.getEstablishedDate());
        return DepartmentDto.from(department);
    }
    @Override
    @Transactional
    public void deleteDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 부서: " + departmentId));

    }
}
