package com.sb14.hrbank.domain.service.employee;

import com.sb14.hrbank.domain.entity.employee.EmployeeStatus;
import com.sb14.hrbank.web.controller.dto.*;
import com.sb14.hrbank.web.controller.dto.employee.management.EmployeeCreateRequest;
import com.sb14.hrbank.web.controller.dto.employee.management.EmployeeDto;
import com.sb14.hrbank.web.controller.dto.employee.management.EmployeeQueryRequest;
import com.sb14.hrbank.web.controller.dto.employee.management.EmployeeUpdateRequest;
import com.sb14.hrbank.web.controller.dto.employee.stats.EmployeeCountRequest;
import com.sb14.hrbank.web.controller.dto.employee.stats.EmployeeDistributionDto;
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
