package com.sb14.hrbank.domain.service.department;

import com.sb14.hrbank.domain.entity.department.Department;
import com.sb14.hrbank.domain.repository.IDepartmentRepository;
import com.sb14.hrbank.domain.repository.IEmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements IDepartmentService{
    private final IDepartmentRepository departmentRepository;
    private final IEmployeeRepository employeeRepository;

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
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 부서: " + departmentId));

        if (!department.getName().equals(updateRequest.getName())
        && departmentRepository.existsByName(updateRequest.getName())) {
            throw new IllegalArgumentException("이미 존재하는 부서명입니다.: " + updateRequest.getName());
        }
        department.updateDepartmentInfo(updateRequest.getName(), updateRequest.getDescription(), updateRequest.getEstablishedDate());
        return DepartmentDto.from(department);
    }
    @Override
    @Transactional
    public void deleteDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 부서: " + departmentId));

        if (employeeRepository.existsByDepartmentId(departmentId)) {
            throw new IllegalArgumentException("소속된 직원이 있어서 삭제할 수 없습니다.: " + departmentId);
        }
        departmentRepository.delete(department);
    }
}
