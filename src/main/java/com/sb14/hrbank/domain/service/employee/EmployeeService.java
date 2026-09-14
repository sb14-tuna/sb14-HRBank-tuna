package com.sb14.hrbank.domain.service.employee;

import com.sb14.hrbank.web.controller.dto.*;
import org.springframework.web.multipart.MultipartFile;


public interface EmployeeService {
    EmployeeDto createEmployee(EmployeeCreateRequest createRequest, MultipartFile profile);
    EmployeeDto findById(Long employeeId);
    CursorPageResponseEmployeeDto findAll(EmployeeSearchRequest querySearchRequest);
    EmployeeDto updateEmployee(Long employeeId, EmployeeUpdateRequest updateRequest, MultipartFile profile);
    void deleteEmployee(Long employeeId);
}
