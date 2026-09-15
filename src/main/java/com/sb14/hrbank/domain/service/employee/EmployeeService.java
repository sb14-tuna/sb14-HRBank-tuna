package com.sb14.hrbank.domain.service.employee;

import com.sb14.hrbank.domain.entity.employee.EmployeeStatus;
import com.sb14.hrbank.web.controller.dto.*;
import com.sb14.hrbank.web.controller.dto.EmployeeCreateRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeDto;
import com.sb14.hrbank.web.controller.dto.EmployeeUpdateRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeCountRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeDistributionDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface EmployeeService {
    EmployeeDto createEmployee(EmployeeCreateRequest createRequest, MultipartFile profile);
    EmployeeDto findById(Long employeeId);
    CursorPageResponseEmployeeDto findAll(EmployeeSearchCondition request);
    EmployeeDto updateEmployee(Long employeeId, EmployeeUpdateRequest updateRequest, MultipartFile profile);
    void deleteEmployee(Long employeeId);
    List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, EmployeeStatus status);
    long countEmployeeByStatusAndDateRange(EmployeeCountRequest queryCountRequest);
}
