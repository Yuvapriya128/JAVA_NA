package org.example.springdatajpademo.ProjectEmplyoee.service;

import org.example.springdatajpademo.ProjectEmplyoee.dto.EmployeeRequestDTO;
import org.example.springdatajpademo.ProjectEmplyoee.dto.EmployeeResponseDTO;
import org.example.springdatajpademo.ProjectEmplyoee.dto.EmployeeUpdateDTO;
import org.example.springdatajpademo.ProjectEmplyoee.exceptions.EmpNotFound;
import org.example.springdatajpademo.ProjectEmplyoee.model.Employee;
import org.example.springdatajpademo.ProjectEmplyoee.repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService{
    @Autowired
    private EmployeeRepo employeeRepo;

//    sort by name (Sort.by(Sort.Direction.ASC,"properties"))
//    pagination

    public List<EmployeeResponseDTO> getAllSorted(){
        return employeeRepo.findAll(
                Sort.by("name").descending()
                .and(
                        Sort.by(Sort.Direction.DESC,"department"))
        ).stream()
                .map((e)->mapToResponseDTO(e)).toList();
    }
    public List<EmployeeResponseDTO> getAllByPage(int pageno,int pagesize){
        return employeeRepo.findAll(PageRequest.of(pageno,pagesize))
                .stream().map((e)->mapToResponseDTO(e)).toList();
    }

//    custom query
    @Override
    public List<EmployeeResponseDTO> getAllEmpByCustom(String dept){
        return employeeRepo.getAllEmpByCustom(dept);
    }

    @Override
    public List<Employee> getEmpByDept(){
        return employeeRepo.getEmpByDep();
    }

    @Override
    @Transactional
    public int updateEmailByName(String name, String email) {
        return employeeRepo.updateEmailByName(name,email);
    }

    @Override
    public List<EmployeeResponseDTO> getAllEmployees() {
        return  employeeRepo.findAll(Sort.by(Sort.Direction.ASC,"name")).stream().map((e)->mapToResponseDTO(e)).toList();

    }

    private EmployeeResponseDTO mapToResponseDTO(Employee e){
        return new EmployeeResponseDTO(e.getId(),e.getName(),e.getDesignation(),e.getDepartment(),e.getEmail());
    }
    private  Employee mapToEntity(EmployeeRequestDTO dto){
        Employee employee = new Employee();

        employee.setName(dto.getName());
        employee.setDesignation(dto.getDesignation());
        employee.setDepartment(dto.getDepartment());
        employee.setEmail(dto.getEmail());
    return employee;
    }


    @Override
    public EmployeeResponseDTO saveEmployee(EmployeeRequestDTO edto) {
        Employee e= employeeRepo.save(mapToEntity(edto));
        return mapToResponseDTO(e);
    }


    @Override
    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee tempemp= employeeRepo.findById(id).orElseThrow(()-> new EmpNotFound("Employee not found"));
        return mapToResponseDTO(tempemp);
    }

    @Override
    public EmployeeResponseDTO updateEmployee(Long id,EmployeeUpdateDTO employee) {
        Employee tempemp =employeeRepo.findById(id).orElseThrow(()-> new EmpNotFound("Employee not found"));

        tempemp.setDepartment(employee.getDepartment());
        tempemp.setName(employee.getName());
        tempemp.setDesignation(employee.getDesignation());
        tempemp.setEmail(employee.getEmail());

        return mapToResponseDTO(employeeRepo.save(tempemp));

    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepo.deleteById(id);

    }
}
