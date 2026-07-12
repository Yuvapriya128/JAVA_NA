package org.example.springdatajpademo.ProjectEmplyoee.service;

import org.example.springdatajpademo.ProjectEmplyoee.dto.EmployeeRequestDTO;
import org.example.springdatajpademo.ProjectEmplyoee.dto.EmployeeResponseDTO;
import org.example.springdatajpademo.ProjectEmplyoee.dto.EmployeeUpdateDTO;
import org.example.springdatajpademo.ProjectEmplyoee.model.Employee;

import java.util.List;

public interface EmployeeService {
    EmployeeResponseDTO saveEmployee(EmployeeRequestDTO employee);
    List<EmployeeResponseDTO>  getAllEmployees();
    EmployeeResponseDTO getEmployeeById(Long id);
    EmployeeResponseDTO updateEmployee(Long id, EmployeeUpdateDTO employee);
    void deleteEmployee(Long id);
    public List<EmployeeResponseDTO> getAllSorted();
    public List<EmployeeResponseDTO> getAllByPage(int pageno,int pagesize);
    public List<EmployeeResponseDTO> getAllEmpByCustom(String dept);
    public List<Employee> getEmpByDept();

//    custom query
    int updateEmailByName(String name, String email);
}
