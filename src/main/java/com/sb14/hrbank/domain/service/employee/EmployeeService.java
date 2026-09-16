package com.sb14.hrbank.domain.service.employee;

import com.sb14.hrbank.domain.entity.employee.EmployeeStatus;
import com.sb14.hrbank.web.controller.dto.*;
import com.sb14.hrbank.web.controller.dto.EmployeeCreateRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeDto;
import com.sb14.hrbank.web.controller.dto.EmployeeUpdateRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeCountRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeDistributionDto;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;


public interface EmployeeService {

    EmployeeDto createEmployee(EmployeeCreateRequest createRequest, MultipartFile profile, String ipAddress);

    EmployeeDto findById(Long employeeId);
    CursorPageResponseEmployeeDto findAll(EmployeeSearchCondition request);

    EmployeeDto updateEmployee(Long employeeId, EmployeeUpdateRequest updateRequest, MultipartFile profile, String ipAddress);

    void deleteEmployee(Long employeeId, String ipAddress);

    List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, EmployeeStatus status);
    long countEmployeeByStatusAndDateRange(EmployeeCountRequest queryCountRequest);
    List<EmployeeTrendDto> getEmployeeTrend(String unit);
}
