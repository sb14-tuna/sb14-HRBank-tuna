package com.sb14.hrbank.domain.service.department;

public interface IDepartmentService {
    DepartmentDto createDepartment(DepartmentCreateRequest createRequest);
    DepartmentDto updateDepartment(Long departmentId, DepartmentUpdateRequest updateRequest);
    void deleteDepartment(Long departmentId);
}
