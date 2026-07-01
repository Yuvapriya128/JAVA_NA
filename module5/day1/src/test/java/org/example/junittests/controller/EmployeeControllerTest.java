package org.example.junittests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.junittests.dto.EmployeeRequestDto;
import org.example.junittests.dto.EmployeeResponseDto;
import org.example.junittests.dto.EmployeeUpdateDto;
import org.example.junittests.exception.EmployeeNotFound;
import org.example.junittests.exception.GlobalExceptionHandler;
import org.example.junittests.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest
@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class EmployeeControllerTest {

//    fake client
    @Autowired
    MockMvc mockMvc;

//    converting json to java
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    EmployeeService employeeService;

    @WithMockUser
    @Test
    void testGetEmployeeSuccess() throws Exception{
        EmployeeResponseDto emp = new EmployeeResponseDto(
                1, "yuva", 100000);

        when(employeeService.findbyid(1)).thenReturn(emp);

        mockMvc.perform(
                get("/api/junit/employees/1")
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
         .andExpect(jsonPath("$.id").value(1))
         .andExpect(jsonPath("$.name").value("yuva"))
         .andExpect(jsonPath("$.salary").value(100000));



    }
/*
* .requestMatchers("/api/junit/**").authenticated()
* without @MockUser
* it will return 401 -> unauthorized
*
*
*
*
* */


    @WithMockUser
    @Test
    void testGetEmployeeFailure() throws Exception{
        when(employeeService.findbyid(2)).thenThrow(new EmployeeNotFound("Employee not found"));

        mockMvc.perform(
                        get("/api/junit/employees/2")
                ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Employee not found"))
                .andExpect(jsonPath("$.status").value(404));

    }

    @WithMockUser
    @Test
    void testDeleteEmployee() throws Exception{
       doNothing().when(employeeService).delete(1);
        mockMvc.perform(
                        delete("/api/junit/employees/1")
                ).andExpect(status().isNoContent());

    }

    @WithMockUser
    @Test
    void testPostEmployee() throws Exception{
        EmployeeRequestDto e = new EmployeeRequestDto("yuva", 100000);
        EmployeeResponseDto em=new EmployeeResponseDto(1, "yuva", 100000);

        when(employeeService.save(e)
        ).thenReturn(em);

        mockMvc.perform(post("/api/junit/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(e))
        ).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

    }

    @WithMockUser
    @Test
    void testPutEmployee() throws Exception{
        EmployeeUpdateDto e = new EmployeeUpdateDto(null, "yuva", 100000);
        EmployeeResponseDto em=new EmployeeResponseDto(1, "yuva", 100000);

        when(employeeService.update(any(EmployeeUpdateDto.class))
        ).thenReturn(em);

        mockMvc.perform(
                put("/api/junit/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(e))
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

    }




}
