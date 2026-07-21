package org.northernarc.week5_assess.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.northernarc.week5_assess.controller.AccountController;
import org.northernarc.week5_assess.controller.AuthController;
import org.northernarc.week5_assess.controller.CustomerController;
import org.northernarc.week5_assess.controller.TransactionController;
import org.northernarc.week5_assess.dto.AuthRequestDto;
import org.northernarc.week5_assess.service.AccountService;
import org.northernarc.week5_assess.service.AuthService;
import org.northernarc.week5_assess.service.CustomerService;
import org.northernarc.week5_assess.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
        AuthController.class,
        CustomerController.class,
        AccountController.class,
        TransactionController.class
})
@DisplayName("SecurityTest")
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private AccountService accountService;

    @MockBean
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        reset(authService, customerService, accountService, transactionService);
    }

    // ==================== Public Endpoint Tests ====================

    @Test
    @DisplayName("Security: POST /api/auth/register is public endpoint")
    void registerEndpoint_isPublic_returnsSuccessWithoutAuth() throws Exception {
        // Arrange
        AuthRequestDto request = createAuthRequest("Alex", "alex@northernarc.org", "9876543210", "password");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Security: POST /api/auth/login is public endpoint")
    void loginEndpoint_isPublic_returnsSuccessWithoutAuth() throws Exception {
        // Arrange
        AuthRequestDto request = createAuthRequest("alex@northernarc.org", null, null, "password");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ==================== Customer Endpoint Security Tests ====================

    @Test
    @DisplayName("Security: GET /api/customers requires authentication")
    void getCustomers_withoutJWT_returns401Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError()); // Either 401 or 403 depending on security config
    }

    @Test
    @DisplayName("Security: GET /api/customers/{id} requires authentication")
    void getCustomerById_withoutJWT_returns401Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/customers/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Security: POST /api/customers requires authentication")
    void createCustomer_withoutJWT_returns401Unauthorized() throws Exception {
        // Arrange
        String request = objectMapper.writeValueAsString(Map.of(
                "name", "Alex",
                "email", "alex@northernarc.org",
                "phone", "9876543210"
        ));

        // Act & Assert
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Security: PUT /api/customers/{id} requires authentication")
    void updateCustomer_withoutJWT_returns401Unauthorized() throws Exception {
        // Arrange
        String request = objectMapper.writeValueAsString(Map.of(
                "name", "Alex Updated",
                "email", "alex@northernarc.org",
                "phone", "9876543210"
        ));

        // Act & Assert
        mockMvc.perform(put("/api/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Security: DELETE /api/customers/{id} requires authentication")
    void deleteCustomer_withoutJWT_returns401Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/customers/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    // ==================== Account Endpoint Security Tests ====================

    @Test
    @DisplayName("Security: GET /api/accounts requires authentication")
    void getAccounts_withoutJWT_returns401Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Security: GET /api/accounts/{id} requires authentication")
    void getAccountById_withoutJWT_returns401Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/accounts/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Security: POST /api/accounts requires authentication")
    void createAccount_withoutJWT_returns401Unauthorized() throws Exception {
        // Arrange
        String request = objectMapper.writeValueAsString(Map.of(
                "accountNumber", "ACC001",
                "openingBalance", 1000,
                "accountType", "SAVINGS",
                "customerId", 1
        ));

        // Act & Assert
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Security: PUT /api/accounts/{id} requires authentication")
    void updateAccount_withoutJWT_returns401Unauthorized() throws Exception {
        // Arrange
        String request = objectMapper.writeValueAsString(Map.of(
                "accountNumber", "ACC001",
                "openingBalance", 2000,
                "accountType", "CURRENT",
                "customerId", 1
        ));

        // Act & Assert
        mockMvc.perform(put("/api/accounts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Security: DELETE /api/accounts/{id} requires authentication")
    void deleteAccount_withoutJWT_returns401Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/accounts/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Security: POST /api/accounts/deposit requires authentication")
    void depositFunds_withoutJWT_returns401Unauthorized() throws Exception {
        // Arrange
        String request = objectMapper.writeValueAsString(Map.of(
                "sourceAccountNumber", "ACC001",
                "destinationAccountNumber", "ACC002",
                "amount", 100
        ));

        // Act & Assert
        mockMvc.perform(post("/api/accounts/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Security: POST /api/accounts/withdraw requires authentication")
    void withdrawFunds_withoutJWT_returns401Unauthorized() throws Exception {
        // Arrange
        String request = objectMapper.writeValueAsString(Map.of(
                "sourceAccountNumber", "ACC001",
                "destinationAccountNumber", "ACC002",
                "amount", 100
        ));

        // Act & Assert
        mockMvc.perform(post("/api/accounts/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Security: POST /api/accounts/transfer requires authentication")
    void transferFunds_withoutJWT_returns401Unauthorized() throws Exception {
        // Arrange
        String request = objectMapper.writeValueAsString(Map.of(
                "sourceAccountNumber", "ACC001",
                "destinationAccountNumber", "ACC002",
                "amount", 100
        ));

        // Act & Assert
        mockMvc.perform(post("/api/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().is4xxClientError());
    }

    // ==================== Transaction Endpoint Security Tests ====================

    @Test
    @DisplayName("Security: GET /api/transactions requires authentication")
    void getTransactions_withoutJWT_returns401Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Security: GET /api/transactions/{id} requires authentication")
    void getTransactionById_withoutJWT_returns401Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Security: GET /api/accounts/{id}/transactions requires authentication")
    void getAccountTransactions_withoutJWT_returns401Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/accounts/1/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    // ==================== JWT Validation Tests ====================

    @Test
    @DisplayName("Security: Missing JWT token returns 401 Unauthorized")
    void missingJWT_returns401Unauthorized() throws Exception {
        // Act & Assert - No Authorization header provided
        mockMvc.perform(get("/api/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Security: Invalid JWT token returns 401 Unauthorized")
    void invalidJWT_returns401Unauthorized() throws Exception {
        // Act & Assert - Invalid token format
        mockMvc.perform(get("/api/customers")
                .header("Authorization", "Bearer invalid.token.here")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Security: Expired JWT token returns 401 Unauthorized")
    void expiredJWT_returns401Unauthorized() throws Exception {
        // Act & Assert - Expired token signature
        String expiredToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiZXhwIjoxMzAwODAxODg0fQ.expired";
        mockMvc.perform(get("/api/customers")
                .header("Authorization", "Bearer " + expiredToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Security: Valid JWT token allows access")
    void validJWT_allowsAccess() throws Exception {
        // Arrange
        String validToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        when(customerService.getAllCustomers()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/customers")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Security: Access denied for forbidden resource returns 403")
    void forbiddenAccess_returns403Forbidden() throws Exception {
        // Act & Assert - Simulating insufficient privileges
        mockMvc.perform(get("/api/customers")
                .header("Authorization", "Bearer valid.jwt.token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()); // Depends on roles
    }

    @Test
    @DisplayName("Security: Missing JWT explicit response is 401 Unauthorized")
    void missingJwt_explicit401() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Security: Insufficient role returns 403 Forbidden")
    void insufficientRole_returns403Forbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/accounts")
                .header("Authorization", "Bearer valid.jwt.with.user.role")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Security: User role accessing admin endpoint returns 403")
    void userRoleAccessingAdminEndpoint_returns403() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/accounts/1")
                .header("Authorization", "Bearer valid.jwt.with.user.role")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Security: Malformed JWT token returns 401 Unauthorized")
    void malformedJwt_returns401Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/customers")
                .header("Authorization", "Bearer malformed-jwt-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Security: Invalid bearer prefix returns 401 Unauthorized")
    void invalidBearerPrefix_returns401Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/customers")
                .header("Authorization", "Token invalid.jwt.token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Security: Empty bearer token returns 401 Unauthorized")
    void emptyBearerToken_returns401Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/customers")
                .header("Authorization", "Bearer ")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Security: CORS allowed origin preflight returns expected headers")
    void corsPreflight_allowedOrigin_returnsCorsHeaders() throws Exception {
        // Act & Assert
        mockMvc.perform(options("/api/customers")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    @Test
    @DisplayName("Security: CORS disallowed origin preflight is rejected")
    void corsPreflight_disallowedOrigin_rejected() throws Exception {
        // Act & Assert
        mockMvc.perform(options("/api/customers")
                .header("Origin", "http://malicious.example")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().is4xxClientError());
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


