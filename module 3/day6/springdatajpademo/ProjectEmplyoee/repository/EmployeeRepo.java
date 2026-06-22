package org.example.springdatajpademo.ProjectEmplyoee.repository;

import org.example.springdatajpademo.ProjectEmplyoee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepo extends JpaRepository<Employee,Long> {

}
