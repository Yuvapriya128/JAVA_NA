package org.example.junittests.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.example.junittests.dto.EmployeeRequestDto;
import org.example.junittests.dto.EmployeeResponseDto;
import org.example.junittests.dto.EmployeeUpdateDto;
import org.example.junittests.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/junit")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/employees")
    public ResponseEntity<?> saveEmployee(
            @Valid @RequestBody EmployeeRequestDto requestDto,
            @RequestParam(defaultValue = "false") boolean redirect,
            UriComponentsBuilder uriBuilder) {

        EmployeeResponseDto response = employeeService.save(requestDto);
        URI location = uriBuilder.path("/api/junit/employees/{id}").buildAndExpand(response.getId()).toUri();

        if (redirect) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER).location(location).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(response);
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<EmployeeResponseDto> updateEmployee(
            @PathVariable @Positive(message = "Id must be greater than 0") int id,
            @Valid @RequestBody EmployeeUpdateDto updateDto) {
        updateDto.setId(id);
        EmployeeResponseDto response = employeeService.update(updateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable @Positive(message = "Id must be greater than 0") int id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(
            @PathVariable @Positive(message = "Id must be greater than 0") int id,
            @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {

        EmployeeResponseDto response = employeeService.findbyid(id);
        String etag = buildWeakEtag(response.getId() + ":" + response.getName() + ":" + response.getSalary());

        if (etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(etag).build();
        }

        return ResponseEntity.ok().eTag(etag).body(response);
    }

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees(
            @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {

        List<EmployeeResponseDto> employees = employeeService.findAll();
        String etag = buildWeakEtag(employees.toString());

        if (etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(etag).build();
        }

        return ResponseEntity.ok().eTag(etag).body(employees);
    }

    private String buildWeakEtag(String source) {
        String digest = DigestUtils.md5DigestAsHex(source.getBytes(StandardCharsets.UTF_8));
        return "W/\"" + digest + "\"";
    }
}
