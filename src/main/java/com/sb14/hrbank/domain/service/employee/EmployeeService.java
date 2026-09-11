package com.sb14.hrbank.domain.service.employee;

import com.sb14.hrbank.web.controller.dto.EmployeeCreateRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeDto;
import com.sb14.hrbank.web.controller.dto.EmployeeUpdateRequest;
import org.springframework.web.multipart.MultipartFile;


public interface EmployeeService {
    EmployeeDto createEmployee(EmployeeCreateRequest createRequest, MultipartFile profile);
    EmployeeDto findById(Long employeeId);
    EmployeeDto updateEmployee(Long employeeId, EmployeeUpdateRequest updateRequest, MultipartFile profile);
    void deleteEmployee(Long employeeId);
}
