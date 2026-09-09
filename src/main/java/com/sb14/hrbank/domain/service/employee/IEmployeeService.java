package com.sb14.hrbank.domain.service.employee;

import com.sb14.hrbank.web.controller.dto.EmployeeCreateRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeDto;
import com.sb14.hrbank.web.controller.dto.EmployeeUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IEmployeeService {
    EmployeeDto createEmployee(EmployeeCreateRequest createRequest, MultipartFile profile);
    EmployeeDto findById(Long employeeId);
    EmployeeDto updateEmployee(Long employeeId, EmployeeUpdateRequest updateRequest);
    void deleteEmployee(Long employeeId);
}
