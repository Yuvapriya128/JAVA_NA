package org.example.springdatajpademo.ProjectEmplyoee.repository;

import org.example.springdatajpademo.ProjectEmplyoee.dto.EmployeeResponseDTO;
import org.example.springdatajpademo.ProjectEmplyoee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

//for meaning loggings
@Repository
public interface EmployeeRepo extends JpaRepository<Employee,Long> {
    @Query("""
select new org.example.springdatajpademo.ProjectEmplyoee.dto.EmployeeResponseDTO(e.id,e.name,e.designation,e.department) from Employee e where e.department= :department
""")
    List<EmployeeResponseDTO> getAllEmpByCustom(@Param("department") String dept);

    @Query("""
select e from Employee e join fetch e.project
""")
    List<Employee> getEmpByDep();

}
