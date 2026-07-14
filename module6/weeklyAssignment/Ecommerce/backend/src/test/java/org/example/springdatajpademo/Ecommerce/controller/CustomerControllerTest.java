package org.example.springdatajpademo.Ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerResponseDTO;
import org.example.springdatajpademo.Ecommerce.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomerControllerTest {

    private MockMvc mockMvc;
    private CustomerService customerService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        CustomerController controller = new CustomerController();
        customerService = mock(CustomerService.class);
        ReflectionTestUtils.setField(controller, "customerService", customerService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void findAll_returnsCustomerList() throws Exception {
        CustomerResponseDTO customer = new CustomerResponseDTO(1, "Yuva", "yuva@example.com", "Chennai", null);
        when(customerService.getAllCustomers()).thenReturn(List.of(customer));

        mockMvc.perform(get("/api/ecom/customer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Yuva"))
                .andExpect(jsonPath("$[0].email").value("yuva@example.com"));
    }

    @Test
    void save_createsCustomer() throws Exception {
        CustomerRequestDTO request = new CustomerRequestDTO(
                "Yuva",
                "yuva@example.com",
                "Chennai",
                "secret123"
        );

        CustomerResponseDTO response = new CustomerResponseDTO(2, "Yuva", "yuva@example.com", "Chennai", null);
        when(customerService.saveCustomer(any(CustomerRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/ecom/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Yuva"));
    }
}
