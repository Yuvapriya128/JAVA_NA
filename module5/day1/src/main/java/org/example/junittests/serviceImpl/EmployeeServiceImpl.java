package org.example.junittests.serviceImpl;

import org.example.junittests.dto.EmployeeRequestDto;
import org.example.junittests.dto.EmployeeResponseDto;
import org.example.junittests.dto.EmployeeUpdateDto;
import org.example.junittests.exception.EmployeeNotFound;
import org.example.junittests.model.Employee;
import org.example.junittests.repo.EmployeeRepo;
import org.example.junittests.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService{
    private final EmployeeRepo employeeRepo;

    public EmployeeServiceImpl(EmployeeRepo employeeRepo) {
        this.employeeRepo = employeeRepo;
    }
    private EmployeeRequestDto employeeRequestDto;

    private EmployeeResponseDto employeeResponseDto;


    private EmployeeUpdateDto employeeUpdateDto;

    private Employee mapToEntity(EmployeeRequestDto e) {
        Employee employee = new Employee();
        employee.setName(e.getName());
        employee.setSalary(e.getSalary());
        return employee;
    }

    private EmployeeResponseDto mapToResponse(Employee e){
        employeeResponseDto=new EmployeeResponseDto(
                e.getId(),
                e.getName(),
                e.getSalary()
        );
        return employeeResponseDto;
    }

    @Override
    public EmployeeResponseDto save(EmployeeRequestDto e) {

        return mapToResponse(employeeRepo.save(mapToEntity(e)));
    }

    @Override
    public EmployeeResponseDto update(EmployeeUpdateDto e) {
        Employee er=employeeRepo.findById(e.getId()).orElseThrow(() -> new EmployeeNotFound("Employee not found"));
        er.setName(e.getName());
        er.setSalary(e.getSalary());
        return mapToResponse(employeeRepo.save(er));
    }

    @Override
    public void delete(int id) {
        Employee er=employeeRepo.findById(id).orElseThrow(() -> new EmployeeNotFound("Employee not found"));
        employeeRepo.delete(er);
    }

    @Override
    public EmployeeResponseDto findbyid(int id) {
        Employee er=employeeRepo.findById(id).orElseThrow(() -> new EmployeeNotFound("Employee not found"));
        return mapToResponse(er);
    }

    @Override
    public List<EmployeeResponseDto> findAll() {
        return employeeRepo.findAll()
                .stream()
                .sorted((e1, e2) -> e1.getName().compareTo(e2.getName()))
                .map(this::mapToResponse)
                .toList();
    }
}
