package org.example.springdatajpademo.ProjectEmplyoee.service;

import org.example.springdatajpademo.ProjectEmplyoee.exceptions.EmpNotFound;
import org.example.springdatajpademo.ProjectEmplyoee.model.Employee;
import org.example.springdatajpademo.ProjectEmplyoee.repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService{
    @Autowired
    private EmployeeRepo employeeRepo;

    @Override
    public Employee saveEmployee(Employee employee) {
        return employeeRepo.save(employee);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepo.findAll();
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepo.findById(id).orElseThrow(()-> new EmpNotFound("Employee not found"));
    }

    @Override
    public Employee updateEmployee(Long id, Employee employee) {
        Employee temp=employeeRepo.findById(id).orElseThrow(()-> new EmpNotFound("Employee not found"));
        temp.setName(employee.getName());
        temp.setDesignation(employee.getDesignation());
        temp.setDepartment(employee.getDepartment());
        temp.setEmail(employee.getEmail());
        return employeeRepo.save(temp);
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepo.deleteById(id);

    }
}
