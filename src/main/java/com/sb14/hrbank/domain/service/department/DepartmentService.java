package com.sb14.hrbank.domain.service.department;

import com.sb14.hrbank.web.controller.dto.DepartmentCreateRequest;
import com.sb14.hrbank.web.controller.dto.DepartmentDto;
import com.sb14.hrbank.web.controller.dto.DepartmentUpdateRequest;

public interface DepartmentService {
    DepartmentDto createDepartment(DepartmentCreateRequest createRequest);
    DepartmentDto updateDepartment(Long departmentId, DepartmentUpdateRequest updateRequest);
    void deleteDepartment(Long departmentId);
}
