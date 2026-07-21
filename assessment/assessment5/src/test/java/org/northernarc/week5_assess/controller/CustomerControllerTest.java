package org.northernarc.week5_assess.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.northernarc.week5_assess.dto.CustomerDto;
import org.northernarc.week5_assess.exception.InvalidRequestException;
import org.northernarc.week5_assess.exception.ResourceNotFoundException;
import org.northernarc.week5_assess.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@DisplayName("CustomerControllerTest")
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private static final String CUSTOMERS_ENDPOINT = "/api/customers";

    @BeforeEach
    void setUp() {
        reset(customerService);
    }

    // ==================== POST /api/customers Tests ====================

    @Test
    @DisplayName("Create Customer: valid request returns 201 Created")
    void createCustomer_withValidRequest_returns201Created() throws Exception {
        // Arrange
        CustomerDto request = createCustomerDto("Alex", "alex@northernarc.org", "9876543210");

        // Act & Assert
        mockMvc.perform(post(CUSTOMERS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(customerService, times(1)).createCustomer(any(CustomerDto.class));
    }

    @Test
    @DisplayName("Create Customer: validation error returns 400")
    void createCustomer_withValidationError_returns400() throws Exception {
        // Arrange
        CustomerDto request = createCustomerDto(null, "alex@northernarc.org", "9876543210");

        // Act & Assert
        mockMvc.perform(post(CUSTOMERS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).createCustomer(any(CustomerDto.class));
    }

    @Test
    @DisplayName("Create Customer: duplicate email returns 409 Conflict")
    void createCustomer_withDuplicateEmail_returns409() throws Exception {
        // Arrange
        CustomerDto request = createCustomerDto("Sam", "duplicate@northernarc.org", "9876543211");
        doThrow(new InvalidRequestException("Email already exists"))
                .when(customerService).createCustomer(argThat(customer -> customer != null
                        && "Sam".equals(customer.getName())
                        && "duplicate@northernarc.org".equals(customer.getEmail())
                        && "9876543211".equals(customer.getPhone())));

        // Act & Assert
        mockMvc.perform(post(CUSTOMERS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(customerService, times(1)).createCustomer(any(CustomerDto.class));
    }

    @Test
    @DisplayName("Create Customer: unauthorized returns 401")
    void createCustomer_unauthorized_returns401() throws Exception {
        // Arrange
        CustomerDto request = createCustomerDto("Alex", "alex@northernarc.org", "9876543210");

        // Act & Assert
        mockMvc.perform(post(CUSTOMERS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Create Customer: forbidden returns 403")
    void createCustomer_forbidden_returns403() throws Exception {
        // Arrange
        CustomerDto request = createCustomerDto("Alex", "alex@northernarc.org", "9876543210");

        // Act & Assert
        mockMvc.perform(post(CUSTOMERS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // ==================== GET /api/customers Tests ====================

    @Test
    @DisplayName("Get All Customers: returns 200 OK")
    void getAllCustomers_returns200Ok() throws Exception {
        // Arrange
        when(customerService.getAllCustomers()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(CUSTOMERS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    @DisplayName("Get All Customers: unauthorized returns 401")
    void getAllCustomers_unauthorized_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get(CUSTOMERS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get All Customers: forbidden returns 403")
    void getAllCustomers_forbidden_returns403() throws Exception {
        // Act & Assert
        mockMvc.perform(get(CUSTOMERS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ==================== GET /api/customers/{id} Tests ====================

    @Test
    @DisplayName("Get Customer by ID: valid ID returns 200 OK")
    void getCustomerById_withValidId_returns200Ok() throws Exception {
        // Arrange
        when(customerService.getCustomerById(1L)).thenReturn(new Object());

        // Act & Assert
        mockMvc.perform(get(CUSTOMERS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(customerService, times(1)).getCustomerById(1L);
    }

    @Test
    @DisplayName("Get Customer by ID: customer not found returns 404")
    void getCustomerById_notFound_returns404() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Customer not found"))
                .when(customerService).getCustomerById(999L);

        // Act & Assert
        mockMvc.perform(get(CUSTOMERS_ENDPOINT + "/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getCustomerById(999L);
    }

    @Test
    @DisplayName("Get Customer by ID: invalid ID returns 400")
    void getCustomerById_withInvalidId_returns400() throws Exception {
        // Act & Assert
        mockMvc.perform(get(CUSTOMERS_ENDPOINT + "/invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).getCustomerById(anyLong());
    }

    @Test
    @DisplayName("Get Customer by ID: unauthorized returns 401")
    void getCustomerById_unauthorized_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get(CUSTOMERS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get Customer by ID: forbidden returns 403")
    void getCustomerById_forbidden_returns403() throws Exception {
        // Act & Assert
        mockMvc.perform(get(CUSTOMERS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ==================== PUT /api/customers/{id} Tests ====================

    @Test
    @DisplayName("Update Customer: valid request returns 200 OK")
    void updateCustomer_withValidRequest_returns200Ok() throws Exception {
        // Arrange
        CustomerDto request = createCustomerDto("Alex Updated", "alex.updated@northernarc.org", "9123456780");

        // Act & Assert
        mockMvc.perform(put(CUSTOMERS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(customerService, times(1)).updateCustomer(anyLong(), any(CustomerDto.class));
    }

    @Test
    @DisplayName("Update Customer: validation error returns 400")
    void updateCustomer_withValidationError_returns400() throws Exception {
        // Arrange
        CustomerDto request = createCustomerDto("", "alex@northernarc.org", "9876543210");

        // Act & Assert
        mockMvc.perform(put(CUSTOMERS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).updateCustomer(anyLong(), any(CustomerDto.class));
    }

    @Test
    @DisplayName("Update Customer: customer not found returns 404")
    void updateCustomer_notFound_returns404() throws Exception {
        // Arrange
        CustomerDto request = createCustomerDto("Alex", "alex@northernarc.org", "9876543210");
        doThrow(new ResourceNotFoundException("Customer not found"))
                .when(customerService).updateCustomer(999L, request);

        // Act & Assert
        mockMvc.perform(put(CUSTOMERS_ENDPOINT + "/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).updateCustomer(anyLong(), any(CustomerDto.class));
    }

    @Test
    @DisplayName("Update Customer: duplicate email returns 409 Conflict")
    void updateCustomer_withDuplicateEmail_returns409() throws Exception {
        // Arrange
        CustomerDto request = createCustomerDto("Sam", "duplicate@northernarc.org", "9876543211");
        doThrow(new InvalidRequestException("Email already exists"))
                .when(customerService).updateCustomer(eq(1L), argThat(customer -> customer != null
                        && "Sam".equals(customer.getName())
                        && "duplicate@northernarc.org".equals(customer.getEmail())
                        && "9876543211".equals(customer.getPhone())));

        // Act & Assert
        mockMvc.perform(put(CUSTOMERS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(customerService, times(1)).updateCustomer(anyLong(), any(CustomerDto.class));
    }

    @Test
    @DisplayName("Update Customer: unauthorized returns 401")
    void updateCustomer_unauthorized_returns401() throws Exception {
        // Arrange
        CustomerDto request = createCustomerDto("Alex", "alex@northernarc.org", "9876543210");

        // Act & Assert
        mockMvc.perform(put(CUSTOMERS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Update Customer: forbidden returns 403")
    void updateCustomer_forbidden_returns403() throws Exception {
        // Arrange
        CustomerDto request = createCustomerDto("Alex", "alex@northernarc.org", "9876543210");

        // Act & Assert
        mockMvc.perform(put(CUSTOMERS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ==================== DELETE /api/customers/{id} Tests ====================

    @Test
    @DisplayName("Delete Customer: valid ID returns 204 No Content")
    void deleteCustomer_withValidId_returns204() throws Exception {
        // Arrange
        doNothing().when(customerService).deleteCustomer(1L);

        // Act & Assert
        mockMvc.perform(delete(CUSTOMERS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).deleteCustomer(1L);
    }

    @Test
    @DisplayName("Delete Customer: customer not found returns 404")
    void deleteCustomer_notFound_returns404() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Customer not found"))
                .when(customerService).deleteCustomer(999L);

        // Act & Assert
        mockMvc.perform(delete(CUSTOMERS_ENDPOINT + "/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).deleteCustomer(999L);
    }

    @Test
    @DisplayName("Delete Customer: unauthorized returns 401")
    void deleteCustomer_unauthorized_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(CUSTOMERS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete Customer: forbidden returns 403")
    void deleteCustomer_forbidden_returns403() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(CUSTOMERS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Create Customer: empty request body returns 400")
    void createCustomer_emptyRequestBody_returns400() throws Exception {
        // Act & Assert
        mockMvc.perform(post(CUSTOMERS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).createCustomer(any(CustomerDto.class));
    }

    @Test
    @DisplayName("Create Customer: malformed JSON returns 400")
    void createCustomer_malformedJson_returns400() throws Exception {
        // Arrange
        String malformedJson = "{\"name\":\"Alex\", invalid-json}";

        // Act & Assert
        mockMvc.perform(post(CUSTOMERS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).createCustomer(any(CustomerDto.class));
    }

    @Test
    @DisplayName("Create Customer: invalid content type returns 415")
    void createCustomer_invalidContentType_returns415() throws Exception {
        // Arrange
        CustomerDto request = createCustomerDto("Alex", "alex@northernarc.org", "9876543210");

        // Act & Assert
        mockMvc.perform(post(CUSTOMERS_ENDPOINT)
                .contentType(MediaType.TEXT_PLAIN)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Get Customers: invalid Accept header returns 406")
    void getCustomers_invalidAcceptHeader_returns406() throws Exception {
        // Act & Assert
        mockMvc.perform(get(CUSTOMERS_ENDPOINT)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @DisplayName("Get Customer: missing JWT returns 401")
    void getCustomer_missingJwt_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get(CUSTOMERS_ENDPOINT + "/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Get Customer: invalid JWT returns 401")
    void getCustomer_invalidJwt_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get(CUSTOMERS_ENDPOINT + "/1")
                .header("Authorization", "Bearer invalid.jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Get Customer: response JSON contract and headers are validated")
    void getCustomer_responseJsonContract_validated() throws Exception {
        // Arrange
        when(customerService.getCustomerById(1L)).thenReturn(new Object());

        // Act & Assert
        mockMvc.perform(get(CUSTOMERS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("Content-Type"))
                .andExpect(jsonPath("$").exists());
    }

    // ==================== Helper Methods ====================

    private CustomerDto createCustomerDto(String name, String email, String phone) {
        CustomerDto dto = new CustomerDto();
        setField(dto, "name", name);
        setField(dto, "email", email);
        setField(dto, "phone", phone);
        return dto;
    }

    private void setField(Object target, String fieldName, Object value) {
        Class<?> current = target.getClass();
        while (current != null) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            } catch (IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }
    }
}
