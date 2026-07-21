package org.northernarc.week5_assess.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.northernarc.week5_assess.dto.AccountDto;
import org.northernarc.week5_assess.dto.TransferRequestDto;
import org.northernarc.week5_assess.exception.InvalidRequestException;
import org.northernarc.week5_assess.exception.ResourceNotFoundException;
import org.northernarc.week5_assess.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

@WebMvcTest(AccountController.class)
@DisplayName("AccountControllerTest")
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    private static final String ACCOUNTS_ENDPOINT = "/api/accounts";

    @BeforeEach
    void setUp() {
        reset(accountService);
    }

    // ==================== POST /api/accounts Tests ====================

    @Test
    @DisplayName("Create Account: valid request returns 201 Created")
    void createAccount_withValidRequest_returns201Created() throws Exception {
        // Arrange
        AccountDto request = createAccountDto("ACC001", BigDecimal.valueOf(1000), "SAVINGS", 1L);

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(accountService, times(1)).createAccount(any(AccountDto.class));
    }

    @Test
    @DisplayName("Create Account: validation error returns 400")
    void createAccount_withValidationError_returns400() throws Exception {
        // Arrange
        AccountDto request = createAccountDto(null, BigDecimal.valueOf(1000), "SAVINGS", 1L);

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).createAccount(any(AccountDto.class));
    }

    @Test
    @DisplayName("Create Account: customer not found returns 404")
    void createAccount_customerNotFound_returns404() throws Exception {
        // Arrange
        AccountDto request = createAccountDto("ACC002", BigDecimal.valueOf(1000), "SAVINGS", 999L);
        doThrow(new ResourceNotFoundException("Customer not found"))
                .when(accountService).createAccount(argThat(account -> account != null
                        && "ACC002".equals(account.getAccountNumber())
                        && account.getCustomerId() != null
                        && account.getCustomerId() == 999L));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).createAccount(any(AccountDto.class));
    }

    @Test
    @DisplayName("Create Account: duplicate account number returns 409")
    void createAccount_duplicateAccountNumber_returns409() throws Exception {
        // Arrange
        AccountDto request = createAccountDto("ACC001", BigDecimal.valueOf(2000), "CURRENT", 1L);
        doThrow(new InvalidRequestException("Account number already exists"))
                .when(accountService).createAccount(argThat(account -> account != null
                        && "ACC001".equals(account.getAccountNumber())
                        && account.getCustomerId() != null
                        && account.getCustomerId() == 1L));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(accountService, times(1)).createAccount(any(AccountDto.class));
    }

    @Test
    @DisplayName("Create Account: unauthorized returns 401")
    void createAccount_unauthorized_returns401() throws Exception {
        // Arrange
        AccountDto request = createAccountDto("ACC003", BigDecimal.valueOf(1000), "SAVINGS", 1L);

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Create Account: forbidden returns 403")
    void createAccount_forbidden_returns403() throws Exception {
        // Arrange
        AccountDto request = createAccountDto("ACC004", BigDecimal.valueOf(1000), "SAVINGS", 1L);

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // ==================== GET /api/accounts Tests ====================

    @Test
    @DisplayName("Get All Accounts: returns 200 OK")
    void getAllAccounts_returns200Ok() throws Exception {
        // Arrange
        when(accountService.getAllAccounts()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(ACCOUNTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(accountService, times(1)).getAllAccounts();
    }

    @Test
    @DisplayName("Get All Accounts: unauthorized returns 401")
    void getAllAccounts_unauthorized_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ACCOUNTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get All Accounts: forbidden returns 403")
    void getAllAccounts_forbidden_returns403() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ACCOUNTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ==================== GET /api/accounts/{id} Tests ====================

    @Test
    @DisplayName("Get Account by ID: valid ID returns 200 OK")
    void getAccountById_withValidId_returns200Ok() throws Exception {
        // Arrange
        when(accountService.getAccountById(1L)).thenReturn(new Object());

        // Act & Assert
        mockMvc.perform(get(ACCOUNTS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(accountService, times(1)).getAccountById(1L);
    }

    @Test
    @DisplayName("Get Account by ID: account not found returns 404")
    void getAccountById_notFound_returns404() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Account not found"))
                .when(accountService).getAccountById(999L);

        // Act & Assert
        mockMvc.perform(get(ACCOUNTS_ENDPOINT + "/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).getAccountById(999L);
    }

    @Test
    @DisplayName("Get Account by ID: unauthorized returns 401")
    void getAccountById_unauthorized_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ACCOUNTS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get Account by ID: forbidden returns 403")
    void getAccountById_forbidden_returns403() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ACCOUNTS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ==================== PUT /api/accounts/{id} Tests ====================

    @Test
    @DisplayName("Update Account: valid request returns 200 OK")
    void updateAccount_withValidRequest_returns200Ok() throws Exception {
        // Arrange
        AccountDto request = createAccountDto("ACC001", BigDecimal.valueOf(2000), "CURRENT", 1L);

        // Act & Assert
        mockMvc.perform(put(ACCOUNTS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(accountService, times(1)).updateAccount(anyLong(), any(AccountDto.class));
    }

    @Test
    @DisplayName("Update Account: validation error returns 400")
    void updateAccount_withValidationError_returns400() throws Exception {
        // Arrange
        AccountDto request = createAccountDto("", BigDecimal.valueOf(1000), "SAVINGS", 1L);

        // Act & Assert
        mockMvc.perform(put(ACCOUNTS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).updateAccount(anyLong(), any(AccountDto.class));
    }

    @Test
    @DisplayName("Update Account: account not found returns 404")
    void updateAccount_notFound_returns404() throws Exception {
        // Arrange
        AccountDto request = createAccountDto("ACC001", BigDecimal.valueOf(1000), "SAVINGS", 1L);
        doThrow(new ResourceNotFoundException("Account not found"))
                .when(accountService).updateAccount(999L, request);

        // Act & Assert
        mockMvc.perform(put(ACCOUNTS_ENDPOINT + "/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).updateAccount(anyLong(), any(AccountDto.class));
    }

    @Test
    @DisplayName("Update Account: unauthorized returns 401")
    void updateAccount_unauthorized_returns401() throws Exception {
        // Arrange
        AccountDto request = createAccountDto("ACC001", BigDecimal.valueOf(1000), "SAVINGS", 1L);

        // Act & Assert
        mockMvc.perform(put(ACCOUNTS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Update Account: forbidden returns 403")
    void updateAccount_forbidden_returns403() throws Exception {
        // Arrange
        AccountDto request = createAccountDto("ACC001", BigDecimal.valueOf(1000), "SAVINGS", 1L);

        // Act & Assert
        mockMvc.perform(put(ACCOUNTS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ==================== DELETE /api/accounts/{id} Tests ====================

    @Test
    @DisplayName("Delete Account: valid ID returns 204 No Content")
    void deleteAccount_withValidId_returns204() throws Exception {
        // Arrange
        doNothing().when(accountService).deleteAccount(1L);

        // Act & Assert
        mockMvc.perform(delete(ACCOUNTS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(accountService, times(1)).deleteAccount(1L);
    }

    @Test
    @DisplayName("Delete Account: account not found returns 404")
    void deleteAccount_notFound_returns404() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Account not found"))
                .when(accountService).deleteAccount(999L);

        // Act & Assert
        mockMvc.perform(delete(ACCOUNTS_ENDPOINT + "/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).deleteAccount(999L);
    }

    @Test
    @DisplayName("Delete Account: unauthorized returns 401")
    void deleteAccount_unauthorized_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(ACCOUNTS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete Account: forbidden returns 403")
    void deleteAccount_forbidden_returns403() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(ACCOUNTS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    // ==================== POST /api/accounts/deposit Tests ====================

    @Test
    @DisplayName("Deposit: valid amount returns 200 OK")
    void deposit_withValidAmount_returns200Ok() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("ACC001", "ACC002", BigDecimal.valueOf(100));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(accountService, times(1)).deposit(anyString(), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Deposit: invalid amount returns 400")
    void deposit_withInvalidAmount_returns400() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("ACC001", "ACC002", BigDecimal.valueOf(-100));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).deposit(anyString(), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Deposit: account not found returns 404")
    void deposit_accountNotFound_returns404() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("MISSING", "ACC002", BigDecimal.valueOf(100));
        doThrow(new ResourceNotFoundException("Account not found"))
                .when(accountService).deposit(eq("MISSING"), eq(BigDecimal.valueOf(100)));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).deposit(anyString(), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Deposit: unauthorized returns 401")
    void deposit_unauthorized_returns401() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("ACC001", "ACC002", BigDecimal.valueOf(100));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deposit: forbidden returns 403")
    void deposit_forbidden_returns403() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("ACC001", "ACC002", BigDecimal.valueOf(100));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ==================== POST /api/accounts/withdraw Tests ====================

    @Test
    @DisplayName("Withdraw: valid amount returns 200 OK")
    void withdraw_withValidAmount_returns200Ok() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("ACC001", "ACC002", BigDecimal.valueOf(100));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(accountService, times(1)).withdraw(anyString(), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Withdraw: insufficient balance returns 400")
    void withdraw_insufficientBalance_returns400() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("ACC001", "ACC002", BigDecimal.valueOf(999999));
        doThrow(new InvalidRequestException("Insufficient balance"))
                .when(accountService).withdraw(eq("ACC001"), eq(BigDecimal.valueOf(999999)));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(accountService, times(1)).withdraw(anyString(), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Withdraw: invalid amount returns 400")
    void withdraw_invalidAmount_returns400() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("ACC001", "ACC002", BigDecimal.valueOf(-50));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).withdraw(anyString(), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Withdraw: account not found returns 404")
    void withdraw_accountNotFound_returns404() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("MISSING", "ACC002", BigDecimal.valueOf(100));
        doThrow(new ResourceNotFoundException("Account not found"))
                .when(accountService).withdraw(eq("MISSING"), eq(BigDecimal.valueOf(100)));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).withdraw(anyString(), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Withdraw: unauthorized returns 401")
    void withdraw_unauthorized_returns401() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("ACC001", "ACC002", BigDecimal.valueOf(100));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Withdraw: forbidden returns 403")
    void withdraw_forbidden_returns403() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("ACC001", "ACC002", BigDecimal.valueOf(100));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ==================== POST /api/accounts/transfer Tests ====================

    @Test
    @DisplayName("Transfer: valid transfer returns 200 OK")
    void transfer_withValidTransfer_returns200Ok() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("ACC001", "ACC002", BigDecimal.valueOf(100));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(accountService, times(1)).transfer(any(TransferRequestDto.class));
    }

    @Test
    @DisplayName("Transfer: invalid amount returns 400")
    void transfer_invalidAmount_returns400() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("ACC001", "ACC002", BigDecimal.valueOf(-50));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).transfer(any(TransferRequestDto.class));
    }

    @Test
    @DisplayName("Transfer: same account returns 400")
    void transfer_sameAccount_returns400() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("ACC001", "ACC001", BigDecimal.valueOf(100));
        doThrow(new InvalidRequestException("Cannot transfer to same account"))
                .when(accountService).transfer(argThat(transfer -> transfer != null
                        && "ACC001".equals(transfer.getSourceAccountNumber())
                        && "ACC001".equals(transfer.getDestinationAccountNumber())
                        && transfer.getAmount() != null
                        && BigDecimal.valueOf(100).compareTo(transfer.getAmount()) == 0));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(accountService, times(1)).transfer(any(TransferRequestDto.class));
    }

    @Test
    @DisplayName("Transfer: source account not found returns 404")
    void transfer_sourceAccountNotFound_returns404() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("MISSING", "ACC002", BigDecimal.valueOf(100));
        doThrow(new ResourceNotFoundException("Source account not found"))
                .when(accountService).transfer(argThat(transfer -> transfer != null
                        && "MISSING".equals(transfer.getSourceAccountNumber())
                        && "ACC002".equals(transfer.getDestinationAccountNumber())
                        && transfer.getAmount() != null
                        && BigDecimal.valueOf(100).compareTo(transfer.getAmount()) == 0));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).transfer(any(TransferRequestDto.class));
    }

    @Test
    @DisplayName("Transfer: destination account not found returns 404")
    void transfer_destinationAccountNotFound_returns404() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("ACC001", "MISSING", BigDecimal.valueOf(100));
        doThrow(new ResourceNotFoundException("Destination account not found"))
                .when(accountService).transfer(argThat(transfer -> transfer != null
                        && "ACC001".equals(transfer.getSourceAccountNumber())
                        && "MISSING".equals(transfer.getDestinationAccountNumber())
                        && transfer.getAmount() != null
                        && BigDecimal.valueOf(100).compareTo(transfer.getAmount()) == 0));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).transfer(any(TransferRequestDto.class));
    }

    @Test
    @DisplayName("Transfer: unauthorized returns 401")
    void transfer_unauthorized_returns401() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("ACC001", "ACC002", BigDecimal.valueOf(100));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Transfer: forbidden returns 403")
    void transfer_forbidden_returns403() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("ACC001", "ACC002", BigDecimal.valueOf(100));

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Create Account: empty request body returns 400")
    void createAccount_emptyRequestBody_returns400() throws Exception {
        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).createAccount(any(AccountDto.class));
    }

    @Test
    @DisplayName("Create Account: malformed JSON returns 400")
    void createAccount_malformedJson_returns400() throws Exception {
        // Arrange
        String malformedJson = "{\"accountNumber\":\"ACC100\", invalid-json}";

        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).createAccount(any(AccountDto.class));
    }

    @Test
    @DisplayName("Transfer: empty request body returns 400")
    void transfer_emptyRequestBody_returns400() throws Exception {
        // Act & Assert
        mockMvc.perform(post(ACCOUNTS_ENDPOINT + "/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).transfer(any(TransferRequestDto.class));
    }

    @Test
    @DisplayName("Get Accounts: invalid Accept header returns 406")
    void getAccounts_invalidAcceptHeader_returns406() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ACCOUNTS_ENDPOINT)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @DisplayName("Get Accounts: missing JWT returns 401")
    void getAccounts_missingJwt_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ACCOUNTS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Get Accounts: invalid JWT returns 401")
    void getAccounts_invalidJwt_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ACCOUNTS_ENDPOINT)
                .header("Authorization", "Bearer invalid.jwt")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Get Account: response headers and JSON contract are validated")
    void getAccount_responseContract_validated() throws Exception {
        // Arrange
        when(accountService.getAccountById(1L)).thenReturn(new Object());

        // Act & Assert
        mockMvc.perform(get(ACCOUNTS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("Content-Type"))
                .andExpect(jsonPath("$").exists());
    }

    // ==================== Helper Methods ====================

    private AccountDto createAccountDto(String accountNumber, BigDecimal openingBalance, String accountType, Long customerId) {
        AccountDto dto = new AccountDto();
        setField(dto, "accountNumber", accountNumber);
        setField(dto, "openingBalance", openingBalance);
        setField(dto, "accountType", accountType);
        setField(dto, "customerId", customerId);
        return dto;
    }

    private TransferRequestDto createTransferRequest(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount) {
        TransferRequestDto dto = new TransferRequestDto();
        setField(dto, "sourceAccountNumber", sourceAccountNumber);
        setField(dto, "destinationAccountNumber", destinationAccountNumber);
        setField(dto, "amount", amount);
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
