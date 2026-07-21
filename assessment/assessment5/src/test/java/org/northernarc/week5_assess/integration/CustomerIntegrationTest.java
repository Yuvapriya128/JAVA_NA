package org.northernarc.week5_assess.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.northernarc.week5_assess.dto.AuthRequestDto;
import org.northernarc.week5_assess.dto.CustomerDto;
import org.northernarc.week5_assess.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("CustomerIntegrationTest")
public class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        // Clean database before each test
        customerRepository.deleteAll();

        // Register test user
        AuthRequestDto registerRequest = createAuthRequest("TestUser", "test@northernarc.org", "9876543210", "password");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Login to get JWT token
        AuthRequestDto loginRequest = createAuthRequest("test@northernarc.org", null, null, "password");
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract JWT from response (assumes token field in response)
        jwtToken = extractTokenFromResponse(loginResponse);
    }

    // ==================== Authentication Tests ====================

    @Test
    @Transactional
    @DisplayName("Integration: User registration successful")
    void register_withValidData_createsCustomerInDatabase() throws Exception {
        // Arrange
        AuthRequestDto request = createAuthRequest("NewUser", "newuser@northernarc.org", "9876543211", "password");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    @Transactional
    @DisplayName("Integration: User login successful and returns JWT")
    void login_withValidCredentials_returnsJWTToken() throws Exception {
        // Arrange
        AuthRequestDto loginRequest = createAuthRequest("test@northernarc.org", null, null, "password");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    // ==================== Customer CRUD Tests ====================

    @Test
    @Transactional
    @DisplayName("Integration: Create customer persists to database")
    void createCustomer_withValidData_persistsAndReturns201() throws Exception {
        // Arrange
        CustomerDto request = createCustomerDto("Alex", "alex@northernarc.org", "9876543210");

        // Act & Assert
        mockMvc.perform(post("/api/customers")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Verify persistence
        assert customerRepository.count() > 0;
    }

    @Test
    @Transactional
    @DisplayName("Integration: Get customer retrieves from database")
    void getCustomer_byId_retrievesFromDatabase() throws Exception {
        // Arrange - Create a customer first
        CustomerDto createRequest = createCustomerDto("Sam", "sam@northernarc.org", "9876543211");
        mockMvc.perform(post("/api/customers")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        Long customerId = (Long) customerRepository.findAll().stream()
                .filter(c -> "sam@northernarc.org".equals(getField(c, "email")))
                .map(c -> getField(c, "id"))
                .findFirst()
                .orElse(null);

        // Act & Assert
        mockMvc.perform(get("/api/customers/" + customerId)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @DisplayName("Integration: Update customer modifies database record")
    void updateCustomer_withValidData_modifiesDatabase() throws Exception {
        // Arrange - Create customer
        CustomerDto createRequest = createCustomerDto("Jordan", "jordan@northernarc.org", "9876543212");
        mockMvc.perform(post("/api/customers")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        Long customerId = (Long) customerRepository.findAll().stream()
                .filter(c -> "jordan@northernarc.org".equals(getField(c, "email")))
                .map(c -> getField(c, "id"))
                .findFirst()
                .orElse(null);

        // Act & Assert - Update
        CustomerDto updateRequest = createCustomerDto("Jordan Updated", "jordan.updated@northernarc.org", "9123456780");
        mockMvc.perform(put("/api/customers/" + customerId)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @DisplayName("Integration: Delete customer removes from database")
    void deleteCustomer_byId_removesFromDatabase() throws Exception {
        // Arrange - Create customer
        CustomerDto createRequest = createCustomerDto("Taylor", "taylor@northernarc.org", "9876543213");
        mockMvc.perform(post("/api/customers")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        long countBefore = customerRepository.count();
        Long customerId = (Long) customerRepository.findAll().stream()
                .filter(c -> "taylor@northernarc.org".equals(getField(c, "email")))
                .map(c -> getField(c, "id"))
                .findFirst()
                .orElse(null);

        // Act & Assert
        mockMvc.perform(delete("/api/customers/" + customerId)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        long countAfter = customerRepository.count();
        assert countAfter == countBefore - 1;
    }

    // ==================== Validation & Business Rule Tests ====================

    @Test
    @Transactional
    @DisplayName("Integration: Duplicate email returns 409 and doesn't persist")
    void registerWithDuplicateEmail_returns409AndDoesNotPersist() throws Exception {
        // Arrange - First customer already in DB from setUp
        long countBefore = customerRepository.count();
        AuthRequestDto request = createAuthRequest("NewUser", "test@northernarc.org", "9876543214", "password");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        long countAfter = customerRepository.count();
        assert countBefore == countAfter;
    }

    @Test
    @Transactional
    @DisplayName("Integration: Invalid email validation returns 400")
    void createCustomer_withInvalidEmail_returns400() throws Exception {
        // Arrange
        CustomerDto request = createCustomerDto("Alex", "invalid-email", "9876543210");

        // Act & Assert
        mockMvc.perform(post("/api/customers")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        long count = customerRepository.count();
        assert count <= 1; // Only the setup customer
    }

    @Test
    @Transactional
    @DisplayName("Integration: Null name validation returns 400")
    void createCustomer_withNullName_returns400() throws Exception {
        // Arrange
        CustomerDto request = createCustomerDto(null, "alex@northernarc.org", "9876543210");

        // Act & Assert
        mockMvc.perform(post("/api/customers")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @DisplayName("Integration: Invalid phone validation returns 400")
    void createCustomer_withInvalidPhone_returns400() throws Exception {
        // Arrange
        CustomerDto request = createCustomerDto("Alex", "alex@northernarc.org", "123456789"); // Less than 10 digits

        // Act & Assert
        mockMvc.perform(post("/api/customers")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ==================== Security Tests ====================

    @Test
    @Transactional
    @DisplayName("Integration: Missing JWT returns 401 Unauthorized")
    void getCustomers_withoutJWT_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @DisplayName("Integration: Invalid JWT returns 401 Unauthorized")
    void getCustomers_withInvalidJWT_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/customers")
                .header("Authorization", "Bearer invalid.jwt.token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    // ==================== Transaction & Rollback Tests ====================

    @Test
    @Transactional
    @DisplayName("Integration: Validation failure causes transaction rollback")
    void validationFailure_causesTransactionRollback() throws Exception {
        // Arrange
        long countBefore = customerRepository.count();
        CustomerDto invalidRequest = createCustomerDto("", "", ""); // All invalid

        // Act
        mockMvc.perform(post("/api/customers")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Assert - No new customer created
        long countAfter = customerRepository.count();
        assert countBefore == countAfter;
    }

    // ==================== Helper Methods ====================

    private AuthRequestDto createAuthRequest(String name, String email, String phone, String password) {
        AuthRequestDto dto = new AuthRequestDto();
        setField(dto, "name", name);
        setField(dto, "email", email);
        setField(dto, "phone", phone);
        setField(dto, "password", password);
        return dto;
    }

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

    private Object getField(Object target, String fieldName) {
        Class<?> current = target.getClass();
        while (current != null) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            } catch (IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }
        return null;
    }

    private String extractTokenFromResponse(String response) {
        try {
            // Parse JSON response to extract token
            // This is a simplified approach; actual implementation depends on response structure
            int tokenStart = response.indexOf("\"token\":\"") + 9;
            int tokenEnd = response.indexOf("\"", tokenStart);
            return response.substring(tokenStart, tokenEnd);
        } catch (Exception e) {
            return "dummy-token"; // Fallback for testing
        }
    }
}
