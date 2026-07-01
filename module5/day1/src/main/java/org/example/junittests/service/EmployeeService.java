package org.example.junittests.service;

import org.example.junittests.dto.EmployeeRequestDto;
import org.example.junittests.dto.EmployeeResponseDto;
import org.example.junittests.dto.EmployeeUpdateDto;
import org.example.junittests.model.Employee;

import java.util.List;

public interface EmployeeService {
    EmployeeResponseDto save(EmployeeRequestDto e);
    EmployeeResponseDto update(EmployeeUpdateDto e);
    void delete(int id);
    EmployeeResponseDto findbyid(int id);
    List<EmployeeResponseDto> findAll();
}
