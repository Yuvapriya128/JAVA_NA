package org.northernarc.employeedemo.serviceimpl;

import org.northernarc.employeedemo.dto.EmployeeRequestDTO;
import org.northernarc.employeedemo.dto.EmployeeResponseDTO;
import org.northernarc.employeedemo.exception.EmployeeNotFound;
import org.northernarc.employeedemo.model.Employee;
import org.northernarc.employeedemo.repository.EmployeeRepo;
import org.northernarc.employeedemo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepo employeeRepo;

    @Override
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO) {
        Employee employee = mapToEntity(requestDTO);
        Employee savedEmployee = employeeRepo.save(employee);
        return mapToResponseDTO(savedEmployee);
    }

    @Override
    public EmployeeResponseDTO getEmployeeById(int id) {
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new EmployeeNotFound("Employee not found with id: " + id));
        return mapToResponseDTO(employee);
    }

    @Override
    public List<EmployeeResponseDTO> getAllEmployees() {
        List<Employee> employees = employeeRepo.findAll();
        return employees.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    @Override
    public EmployeeResponseDTO updateEmployee(int id, EmployeeRequestDTO requestDTO) {

        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new EmployeeNotFound("Employee not found with id: " + id));

        employee.setName(requestDTO.getName());
        employee.setSalary(requestDTO.getSalary());

        Employee updatedEmployee = employeeRepo.save(employee);

        return mapToResponseDTO(updatedEmployee);
    }

    @Override
    public void deleteEmployee(int id) {
        if (!employeeRepo.existsById(id)) {
            throw new EmployeeNotFound("Employee not found with id: " + id);
        }
        employeeRepo.deleteById(id);
    }

    @Override
    public List<EmployeeResponseDTO> sortBySalary() {
        return employeeRepo.findAll().stream()
                .sorted(Comparator.comparingDouble(Employee::getSalary))
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeResponseDTO> sortBySalaryDesc() {
        return employeeRepo.findAll().stream()
                .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private Employee mapToEntity(EmployeeRequestDTO requestDTO) {
        Employee employee = new Employee();
        employee.setName(requestDTO.getName());
        employee.setSalary(requestDTO.getSalary());
        return employee;
    }

    private EmployeeResponseDTO mapToResponseDTO(Employee employee) {
        return new EmployeeResponseDTO(employee.getId(), employee.getName(), employee.getSalary());
    }
}
