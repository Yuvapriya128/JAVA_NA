package org.northernarc.week5_assess.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.northernarc.week5_assess.controller.AccountController;
import org.northernarc.week5_assess.controller.CustomerController;
import org.northernarc.week5_assess.controller.TransactionController;
import org.northernarc.week5_assess.service.AccountService;
import org.northernarc.week5_assess.service.CustomerService;
import org.northernarc.week5_assess.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import jakarta.validation.ConstraintViolationException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
        GlobalExceptionHandler.class,
        CustomerController.class,
        AccountController.class,
        TransactionController.class
})
@DisplayName("GlobalExceptionHandlerTest")
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private AccountService accountService;

    @MockBean
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        reset(customerService, accountService, transactionService);
    }

    @AfterEach
    void tearDown() {
        reset(customerService, accountService, transactionService);
    }

    // ==================== ResourceNotFoundException Tests ====================

    @Test
    @DisplayName("Exception Handler: CustomerNotFoundException returns 404 Not Found")
    void customerNotFound_returns404() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Customer not found with id: 999"))
                .when(customerService).getCustomerById(999L);

        // Act & Assert
        mockMvc.perform(get("/api/customers/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Customer not found with id: 999"))
                .andExpect(jsonPath("$.path").value("/api/customers/999"));

        verify(customerService, times(1)).getCustomerById(999L);
        verify(accountService, never()).getAccountById(anyLong());
    }

    @Test
    @DisplayName("Exception Handler: AccountNotFoundException returns 404 Not Found")
    void accountNotFound_returns404() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Account not found with id: 999"))
                .when(accountService).getAccountById(999L);

        // Act & Assert
        mockMvc.perform(get("/api/accounts/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/accounts/999"));
    }

    @Test
    @DisplayName("Exception Handler: TransactionNotFoundException returns 404 Not Found")
    void transactionNotFound_returns404() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Transaction not found with id: 999"))
                .when(transactionService).getTransactionById(999L);

        // Act & Assert
        mockMvc.perform(get("/api/transactions/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/transactions/999"));
    }

    // ==================== InvalidRequestException Tests ====================

    @Test
    @DisplayName("Exception Handler: DuplicateEmailException returns 409 Conflict")
    void duplicateEmail_returns409() throws Exception {
        // Arrange
        doThrow(new InvalidRequestException("Email already exists: alex@northernarc.org"))
                .when(customerService)
                .createCustomer(argThat(customer -> customer != null
                        && "Alex".equals(customer.getName())
                        && "alex@northernarc.org".equals(customer.getEmail())
                        && "9876543210".equals(customer.getPhone())));

        // Act & Assert
        String createCustomerRequest = objectMapper.writeValueAsString(Map.of(
                "name", "Alex",
                "email", "alex@northernarc.org",
                "phone", "9876543210"
        ));

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createCustomerRequest))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Email already exists: alex@northernarc.org"))
                .andExpect(jsonPath("$.path").value("/api/customers"));

        verify(customerService, times(1)).createCustomer(any());
        verify(transactionService, never()).getTransactionById(anyLong());
    }

    @Test
    @DisplayName("Exception Handler: ValidationException returns 400 Bad Request")
    void validationError_returns400() throws Exception {
        // Arrange - Send invalid data
        String invalidRequest = objectMapper.writeValueAsString(Map.of(
                "name", "",
                "email", "invalid-email",
                "phone", "123"
        ));

        // Act & Assert
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/customers"));
    }

    @Test
    @DisplayName("Exception Handler: DuplicateAccountNumberException returns 409 Conflict")
    void duplicateAccountNumber_returns409() throws Exception {
        // Arrange
        doThrow(new InvalidRequestException("Account number already exists: ACC001"))
                .when(accountService)
                .createAccount(argThat(account -> account != null
                        && "ACC001".equals(account.getAccountNumber())
                        && BigDecimal.valueOf(1000).compareTo(account.getOpeningBalance()) == 0
                        && account.getCustomerId() != null
                        && account.getCustomerId() == 1L));

        // Act & Assert
        String createAccountRequest = objectMapper.writeValueAsString(Map.of(
                "accountNumber", "ACC001",
                "openingBalance", 1000,
                "accountType", "SAVINGS",
                "customerId", 1
        ));

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAccountRequest))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Account number already exists: ACC001"))
                .andExpect(jsonPath("$.path").value("/api/accounts"));
    }

    @Test
    @DisplayName("Exception Handler: InvalidAmountException returns 400 Bad Request")
    void invalidAmount_returns400() throws Exception {
        // Arrange
        doThrow(new InvalidRequestException("Amount must be positive"))
                .when(accountService).deposit(eq("ACC001"), eq(BigDecimal.valueOf(-100)));

        // Act & Assert
        String depositRequest = objectMapper.writeValueAsString(Map.of(
                "sourceAccountNumber", "ACC001",
                "destinationAccountNumber", "ACC002",
                "amount", -100
        ));

        mockMvc.perform(post("/api/accounts/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(depositRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Amount must be positive"))
                .andExpect(jsonPath("$.path").value("/api/accounts/deposit"));
    }

    @Test
    @DisplayName("Exception Handler: InsufficientBalanceException returns 400 Bad Request")
    void insufficientBalance_returns400() throws Exception {
        // Arrange
        doThrow(new InvalidRequestException("Insufficient balance in account"))
                .when(accountService).withdraw(eq("ACC001"), eq(BigDecimal.valueOf(999999)));

        // Act & Assert
        String withdrawRequest = objectMapper.writeValueAsString(Map.of(
                "sourceAccountNumber", "ACC001",
                "destinationAccountNumber", "ACC002",
                "amount", 999999
        ));

        mockMvc.perform(post("/api/accounts/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(withdrawRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Insufficient balance in account"))
                .andExpect(jsonPath("$.path").value("/api/accounts/withdraw"));
    }

    // ==================== UnauthorizedException Tests ====================

    @Test
    @DisplayName("Exception Handler: UnauthorizedException returns 401 Unauthorized")
    void unauthorized_returns401() throws Exception {
        // Arrange
        doThrow(new UnauthorizedException("Invalid credentials"))
                .when(customerService).getCustomerById(1L);

        // Act & Assert
        mockMvc.perform(get("/api/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer invalid.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid credentials"))
                .andExpect(jsonPath("$.path").value("/api/customers/1"));

        verify(customerService, times(1)).getCustomerById(1L);
        verify(accountService, never()).createAccount(any());
    }

    // ==================== AccessDeniedException Tests ====================

    @Test
    @DisplayName("Exception Handler: AccessDeniedException returns 403 Forbidden")
    void forbidden_returns403() throws Exception {
        // Arrange
        doThrow(new UnauthorizedException("Access denied"))
                .when(customerService).deleteCustomer(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/customers/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Access denied"))
                .andExpect(jsonPath("$.path").value("/api/customers/1"));
    }

    // ==================== Error Response Structure Tests ====================

    @Test
    @DisplayName("Exception Handler: Error response contains timestamp")
    void errorResponse_containsTimestamp() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Test error"))
                .when(customerService).getCustomerById(999L);

        // Act & Assert
        mockMvc.perform(get("/api/customers/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    @DisplayName("Exception Handler: Error response contains status code")
    void errorResponse_containsStatusCode() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Test error"))
                .when(customerService).getCustomerById(999L);

        // Act & Assert
        mockMvc.perform(get("/api/customers/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Exception Handler: Error response contains error type")
    void errorResponse_containsErrorType() throws Exception {
        // Arrange
        doThrow(new InvalidRequestException("Test error"))
                .when(accountService).createAccount(argThat(account -> account != null
                        && account.getAccountNumber() == null
                        && account.getOpeningBalance() == null
                        && account.getCustomerId() == null));

        // Act & Assert
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Exception Handler: Error response contains message")
    void errorResponse_containsMessage() throws Exception {
        // Arrange
        doThrow(new InvalidRequestException("Custom error message"))
                .when(customerService).createCustomer(argThat(customer -> customer != null
                        && customer.getName() == null
                        && customer.getEmail() == null
                        && customer.getPhone() == null));

        // Act & Assert
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("Custom error message"));
    }

    @Test
    @DisplayName("Exception Handler: Error response contains path")
    void errorResponse_containsPath() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Test error"))
                .when(transactionService).getTransactionById(999L);

        // Act & Assert
        mockMvc.perform(get("/api/transactions/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.path").exists())
                .andExpect(jsonPath("$.path").value("/api/transactions/999"));
    }

    @Test
    @DisplayName("Exception Handler: Error response content type is application/json")
    void errorResponse_contentTypeIsJson() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Test error"))
                .when(customerService).getCustomerById(999L);

        // Act & Assert
        mockMvc.perform(get("/api/customers/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Exception Handler: RuntimeException returns 500 Internal Server Error")
    void runtimeException_returns500() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Unexpected failure"))
                .when(customerService).getCustomerById(123L);

        // Act & Assert
        mockMvc.perform(get("/api/customers/123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/customers/123"));
    }

    @Test
    @DisplayName("Exception Handler: MethodArgumentNotValidException returns 400 with field validation payload")
    void methodArgumentNotValid_returns400() throws Exception {
        // Arrange
        String invalidRequest = objectMapper.writeValueAsString(Map.of(
                "name", "",
                "email", "invalid",
                "phone", "12"
        ));

        // Act & Assert
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/customers"));
    }

    @Test
    @DisplayName("Exception Handler: ValidationError payload contains fieldErrors, field names, and messages")
    void validationError_containsFieldErrorsAndRejectedFields() throws Exception {
        // Arrange
        String invalidRequest = objectMapper.writeValueAsString(Map.of(
                "name", "",
                "email", "invalid-email",
                "phone", "123"
        ));

        // Act & Assert
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/customers"))
                .andExpect(jsonPath("$.fieldErrors").exists())
                .andExpect(jsonPath("$.fieldErrors[0].field").exists())
                .andExpect(jsonPath("$.fieldErrors[0].message").exists());
    }

    @Test
    @DisplayName("Exception Handler: ConstraintViolationException returns 400 Bad Request")
    void constraintViolationException_returns400() throws Exception {
        // Arrange
        doThrow(new ConstraintViolationException("Constraint failed", Collections.emptySet()))
                .when(accountService).createAccount(argThat(account -> account != null
                        && account.getAccountNumber() == null
                        && account.getOpeningBalance() == null
                        && account.getCustomerId() == null));

        // Act & Assert
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/accounts"));
    }

    @Test
    @DisplayName("Exception Handler: HttpMessageNotReadableException returns 400 Bad Request")
    void httpMessageNotReadable_returns400() throws Exception {
        // Arrange
        String malformedJson = "{\"name\":\"Alex\", invalid-json}";

        // Act & Assert
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/customers"));
    }

    @Test
    @DisplayName("Exception Handler: MethodArgumentTypeMismatchException returns 400 Bad Request")
    void methodArgumentTypeMismatch_returns400() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/customers/not-a-number")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/customers/not-a-number"));
    }

    @Test
    @DisplayName("Exception Handler: AuthenticationException returns 401 Unauthorized")
    void authenticationException_returns401() throws Exception {
        // Arrange
        AuthenticationException authException = new BadCredentialsException("Bad credentials");
        doThrow(authException).when(customerService).getCustomerById(55L);

        // Act & Assert
        mockMvc.perform(get("/api/customers/55")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/customers/55"));
    }

    @Test
    @DisplayName("Exception Handler: AccessDeniedException returns 403 Forbidden")
    void accessDeniedException_returns403() throws Exception {
        // Arrange
        doThrow(new AccessDeniedException("Not allowed"))
                .when(customerService).deleteCustomer(77L);

        // Act & Assert
        mockMvc.perform(delete("/api/customers/77")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/customers/77"));
    }

}
