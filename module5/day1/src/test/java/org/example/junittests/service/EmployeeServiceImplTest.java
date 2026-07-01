package org.example.junittests.service;

import org.example.junittests.dto.EmployeeRequestDto;
import org.example.junittests.dto.EmployeeResponseDto;
import org.example.junittests.dto.EmployeeUpdateDto;
import org.example.junittests.exception.EmployeeNotFound;
import org.example.junittests.model.Employee;
import org.example.junittests.repo.EmployeeRepo;
import org.example.junittests.serviceImpl.EmployeeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EmployeeServiceImplTest {

	@MockBean
	private EmployeeRepo employeeRepo;

	@Autowired
	private EmployeeServiceImpl employeeService;

	@Test
	@DisplayName("save employee")
	void saveCheck() {
		EmployeeRequestDto request = new EmployeeRequestDto("yuva", 100000);
		Employee savedEntity = new Employee();
		savedEntity.setId(1);
		savedEntity.setName("yuva");
		savedEntity.setSalary(100000);

		when(employeeRepo.save(any(Employee.class))).thenReturn(savedEntity);

		EmployeeResponseDto response = employeeService.save(request);

		Assertions.assertEquals(1, response.getId());
		Assertions.assertEquals("yuva", response.getName());
		Assertions.assertEquals(100000, response.getSalary());

		ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
		verify(employeeRepo, times(1)).save(captor.capture());
		Assertions.assertEquals("yuva", captor.getValue().getName());
		Assertions.assertEquals(100000, captor.getValue().getSalary());
	}

	@Test
	@DisplayName("update employee success")
	void updateCheckSuccess() {
		EmployeeUpdateDto updateDto = new EmployeeUpdateDto(1, "priya", 120000);
		Employee existing = new Employee();
		existing.setId(1);
		existing.setName("yuva");
		existing.setSalary(100000);

		when(employeeRepo.findById(1)).thenReturn(Optional.of(existing));
		when(employeeRepo.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

		EmployeeResponseDto response = employeeService.update(updateDto);

		Assertions.assertEquals(1, response.getId());
		Assertions.assertEquals("priya", response.getName());
		Assertions.assertEquals(120000, response.getSalary());
		verify(employeeRepo, times(1)).findById(1);
		verify(employeeRepo, times(1)).save(existing);
	}

	@Test
	@DisplayName("update employee not found")
	void updateCheckNotFound() {
		EmployeeUpdateDto updateDto = new EmployeeUpdateDto(99, "priya", 120000);
		when(employeeRepo.findById(99)).thenReturn(Optional.empty());

		EmployeeNotFound ex = Assertions.assertThrows(EmployeeNotFound.class, () -> employeeService.update(updateDto));
		Assertions.assertEquals("Employee not found", ex.getMessage());
		verify(employeeRepo, never()).save(any(Employee.class));
	}

	@Test
	@DisplayName("delete employee success")
	void deleteCheckSuccess() {
		Employee existing = new Employee();
		existing.setId(1);
		existing.setName("yuva");
		existing.setSalary(100000);

		when(employeeRepo.findById(1)).thenReturn(Optional.of(existing));
		doNothing().when(employeeRepo).delete(existing);

		employeeService.delete(1);

		verify(employeeRepo, times(1)).findById(1);
		verify(employeeRepo, times(1)).delete(existing);
	}

	@Test
	@DisplayName("delete employee not found")
	void deleteCheckNotFound() {
		when(employeeRepo.findById(5)).thenReturn(Optional.empty());

		EmployeeNotFound ex = Assertions.assertThrows(EmployeeNotFound.class, () -> employeeService.delete(5));
		Assertions.assertEquals("Employee not found", ex.getMessage());
		verify(employeeRepo, never()).delete(any(Employee.class));
	}

	@Test
	@DisplayName("find employee by id success")
	void findByIdSuccess() {
		Employee existing = new Employee();
		existing.setId(1);
		existing.setName("yuva");
		existing.setSalary(100000);

		when(employeeRepo.findById(1)).thenReturn(Optional.of(existing));

		EmployeeResponseDto response = employeeService.findbyid(1);

		Assertions.assertEquals(1, response.getId());
		Assertions.assertEquals("yuva", response.getName());
		Assertions.assertEquals(100000, response.getSalary());
		verify(employeeRepo, times(1)).findById(1);
	}

	@Test
	@DisplayName("find employee by id not found")
	void findByIdNotFound() {
		when(employeeRepo.findById(10)).thenReturn(Optional.empty());

		EmployeeNotFound ex = Assertions.assertThrows(EmployeeNotFound.class, () -> employeeService.findbyid(10));
		Assertions.assertEquals("Employee not found", ex.getMessage());
	}

	@Test
	@DisplayName("find all employees sorted by name")
	void findAllSortedByName() {
		Employee e1 = new Employee();
		e1.setId(1);
		e1.setName("zara");
		e1.setSalary(90000);

		Employee e2 = new Employee();
		e2.setId(2);
		e2.setName("anu");
		e2.setSalary(80000);

		Employee e3 = new Employee();
		e3.setId(3);
		e3.setName("mohan");
		e3.setSalary(85000);

		when(employeeRepo.findAll()).thenReturn(List.of(e1, e2, e3));

		List<EmployeeResponseDto> responses = employeeService.findAll();

		Assertions.assertEquals(3, responses.size());
		Assertions.assertEquals("anu", responses.get(0).getName());
		Assertions.assertEquals("mohan", responses.get(1).getName());
		Assertions.assertEquals("zara", responses.get(2).getName());
		verify(employeeRepo, times(1)).findAll();
	}
}
