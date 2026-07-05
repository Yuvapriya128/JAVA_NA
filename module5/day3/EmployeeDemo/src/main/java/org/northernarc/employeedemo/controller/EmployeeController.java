package org.northernarc.employeedemo.controller;

import org.northernarc.employeedemo.dto.EmployeeRequestDTO;
import org.northernarc.employeedemo.dto.EmployeeResponseDTO;
import org.northernarc.employeedemo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:63342")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@Valid @RequestBody EmployeeRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(requestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable int id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/salary-sort")
    public ResponseEntity<List<EmployeeResponseDTO>> sortEmployees() {
        return ResponseEntity.ok(employeeService.sortBySalary());
    }

    @GetMapping("/salary-rsort")
    public ResponseEntity<List<EmployeeResponseDTO>> reverseSortEmployees() {
        return ResponseEntity.ok(employeeService.sortBySalaryDesc());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(@PathVariable int id, @Valid @RequestBody EmployeeRequestDTO requestDTO) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable int id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee deleted successfully");
    }
}
