package org.northernarc.employeedemo.service;

import org.northernarc.employeedemo.dto.EmployeeRequestDTO;
import org.northernarc.employeedemo.dto.EmployeeResponseDTO;
import java.util.List;

public interface EmployeeService {
    EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO);

    EmployeeResponseDTO getEmployeeById(int id);

    List<EmployeeResponseDTO> getAllEmployees();

    EmployeeResponseDTO updateEmployee(int id, EmployeeRequestDTO requestDTO);

    void deleteEmployee(int id);

    List<EmployeeResponseDTO> sortBySalary();
    List<EmployeeResponseDTO> sortBySalaryDesc();
}
