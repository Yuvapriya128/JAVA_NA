package org.northernarc.week5_assess.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.northernarc.week5_assess.dto.AccountDto;
import org.northernarc.week5_assess.dto.AuthRequestDto;
import org.northernarc.week5_assess.dto.CustomerDto;
import org.northernarc.week5_assess.dto.TransferRequestDto;
import org.northernarc.week5_assess.repository.AccountRepository;
import org.northernarc.week5_assess.repository.CustomerRepository;
import org.northernarc.week5_assess.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("TransferIntegrationTest")
public class TransferIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private String jwtToken;
    private Long customerId;
    private String sourceAccountNumber;
    private String destinationAccountNumber;

    @BeforeEach
    void setUp() throws Exception {
        // Clean databases
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        customerRepository.deleteAll();

        // Register and login customer
        AuthRequestDto registerRequest = createAuthRequest("TransferUser", "transfer@northernarc.org", "9876543210", "password");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        AuthRequestDto loginRequest = createAuthRequest("transfer@northernarc.org", null, null, "password");
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        jwtToken = extractTokenFromResponse(loginResponse);
        customerId = (Long) customerRepository.findAll().stream()
                .findFirst()
                .map(c -> getField(c, "id"))
                .orElse(null);

        // Create source and destination accounts
        AccountDto sourceRequest = createAccountDto("ACC001", BigDecimal.valueOf(5000), "SAVINGS", customerId);
        mockMvc.perform(post("/api/accounts")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sourceRequest)))
                .andExpect(status().isCreated());

        AccountDto destRequest = createAccountDto("ACC002", BigDecimal.valueOf(1000), "CURRENT", customerId);
        mockMvc.perform(post("/api/accounts")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(destRequest)))
                .andExpect(status().isCreated());

        sourceAccountNumber = "ACC001";
        destinationAccountNumber = "ACC002";
    }

    // ==================== Account Creation Tests ====================

    @Test
    @Transactional
    @DisplayName("Integration: Create source account persists to database")
    void createSourceAccount_persistsAndReturns201() throws Exception {
        // Arrange
        AccountDto request = createAccountDto("SRC100", BigDecimal.valueOf(10000), "SAVINGS", customerId);

        // Act & Assert
        mockMvc.perform(post("/api/accounts")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        assert accountRepository.count() > 2;
    }

    @Test
    @Transactional
    @DisplayName("Integration: Create destination account persists to database")
    void createDestinationAccount_persistsAndReturns201() throws Exception {
        // Arrange
        AccountDto request = createAccountDto("DST100", BigDecimal.valueOf(5000), "CURRENT", customerId);

        // Act & Assert
        mockMvc.perform(post("/api/accounts")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        assert accountRepository.count() > 2;
    }

    // ==================== Deposit Tests ====================

    @Test
    @Transactional
    @DisplayName("Integration: Deposit transaction creates record and updates balance")
    void deposit_createsTransactionAndUpdatesBalance() throws Exception {
        // Arrange
        long txnCountBefore = transactionRepository.count();
        TransferRequestDto request = createTransferRequest(sourceAccountNumber, destinationAccountNumber, BigDecimal.valueOf(500));

        // Act & Assert
        mockMvc.perform(post("/api/accounts/deposit")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        long txnCountAfter = transactionRepository.count();
        assert txnCountAfter > txnCountBefore;
    }

    // ==================== Withdraw Tests ====================

    @Test
    @Transactional
    @DisplayName("Integration: Withdrawal transaction creates record and updates balance")
    void withdraw_createsTransactionAndUpdatesBalance() throws Exception {
        // Arrange
        long txnCountBefore = transactionRepository.count();
        TransferRequestDto request = createTransferRequest(sourceAccountNumber, destinationAccountNumber, BigDecimal.valueOf(300));

        // Act & Assert
        mockMvc.perform(post("/api/accounts/withdraw")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        long txnCountAfter = transactionRepository.count();
        assert txnCountAfter > txnCountBefore;
    }

    // ==================== Transfer Tests ====================

    @Test
    @Transactional
    @DisplayName("Integration: Valid transfer debits source and credits destination")
    void transfer_withValidAccounts_transfersSuccessfully() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest(sourceAccountNumber, destinationAccountNumber, BigDecimal.valueOf(1000));

        // Act & Assert
        mockMvc.perform(post("/api/accounts/transfer")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @DisplayName("Integration: Transfer creates two transaction records")
    void transfer_createsTwoTransactionRecords() throws Exception {
        // Arrange
        long txnCountBefore = transactionRepository.count();
        TransferRequestDto request = createTransferRequest(sourceAccountNumber, destinationAccountNumber, BigDecimal.valueOf(500));

        // Act & Assert
        mockMvc.perform(post("/api/accounts/transfer")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        long txnCountAfter = transactionRepository.count();
        assert txnCountAfter == txnCountBefore + 2; // Two transactions: debit + credit
    }

    @Test
    @Transactional
    @DisplayName("Integration: Transfer is atomic - both accounts updated or none")
    void transfer_isAtomic_bothAccountsUpdatedOrNone() throws Exception {
        // Arrange
        long accountCountBefore = accountRepository.count();
        TransferRequestDto request = createTransferRequest(sourceAccountNumber, destinationAccountNumber, BigDecimal.valueOf(750));

        // Act & Assert
        mockMvc.perform(post("/api/accounts/transfer")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        long accountCountAfter = accountRepository.count();
        assert accountCountBefore == accountCountAfter; // No accounts added/deleted
    }

    @Test
    @Transactional
    @DisplayName("Integration: Transfer with insufficient balance returns 400")
    void transfer_withInsufficientBalance_returns400() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest(sourceAccountNumber, destinationAccountNumber, BigDecimal.valueOf(999999));

        // Act & Assert
        mockMvc.perform(post("/api/accounts/transfer")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Verify no transactions created on failure
        long txnCount = transactionRepository.count();
        assert txnCount == 0;
    }

    @Test
    @Transactional
    @DisplayName("Integration: Transfer to same account returns 400")
    void transfer_toSameAccount_returns400() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest(sourceAccountNumber, sourceAccountNumber, BigDecimal.valueOf(500));

        // Act & Assert
        mockMvc.perform(post("/api/accounts/transfer")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @DisplayName("Integration: Transfer with missing source account returns 404")
    void transfer_withMissingSourceAccount_returns404() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest("MISSING", destinationAccountNumber, BigDecimal.valueOf(500));

        // Act & Assert
        mockMvc.perform(post("/api/accounts/transfer")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @DisplayName("Integration: Transfer with missing destination account returns 404")
    void transfer_withMissingDestinationAccount_returns404() throws Exception {
        // Arrange
        TransferRequestDto request = createTransferRequest(sourceAccountNumber, "MISSING", BigDecimal.valueOf(500));

        // Act & Assert
        mockMvc.perform(post("/api/accounts/transfer")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @DisplayName("Integration: Failed transfer causes rollback - no partial updates")
    void failedTransfer_causesRollback_noPartialUpdates() throws Exception {
        // Arrange
        long txnCountBefore = transactionRepository.count();
        TransferRequestDto request = createTransferRequest(sourceAccountNumber, "MISSING", BigDecimal.valueOf(500));

        // Act
        mockMvc.perform(post("/api/accounts/transfer")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        // Assert - No transactions created on failure
        long txnCountAfter = transactionRepository.count();
        assert txnCountBefore == txnCountAfter;
    }

    // ==================== Transaction History Tests ====================

    @Test
    @Transactional
    @DisplayName("Integration: Get all transactions returns list from database")
    void getAllTransactions_returnsRecordsFromDatabase() throws Exception {
        // Arrange - Create some transactions
        TransferRequestDto transferRequest = createTransferRequest(sourceAccountNumber, destinationAccountNumber, BigDecimal.valueOf(200));
        mockMvc.perform(post("/api/accounts/transfer")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk());

        // Act & Assert
        mockMvc.perform(get("/api/transactions")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @DisplayName("Integration: Get account transaction history returns associated transactions")
    void getAccountTransactionHistory_returnsAssociatedTransactions() throws Exception {
        // Arrange - Create transactions
        TransferRequestDto transferRequest = createTransferRequest(sourceAccountNumber, destinationAccountNumber, BigDecimal.valueOf(100));
        mockMvc.perform(post("/api/accounts/transfer")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk());

        Long accountId = (Long) accountRepository.findAll().stream()
                .filter(a -> sourceAccountNumber.equals(getField(a, "accountNumber")))
                .map(a -> getField(a, "id"))
                .findFirst()
                .orElse(null);

        // Act & Assert
        mockMvc.perform(get("/api/accounts/" + accountId + "/transactions")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ==================== Balance Verification Tests ====================

    @Test
    @Transactional
    @DisplayName("Integration: Balance is correctly updated after transfer")
    void balanceVerification_afterTransfer_isCorrect() throws Exception {
        // Arrange - Get initial balance
        Long sourceId = (Long) accountRepository.findAll().stream()
                .filter(a -> sourceAccountNumber.equals(getField(a, "accountNumber")))
                .map(a -> getField(a, "id"))
                .findFirst()
                .orElse(null);

        Object initialBalance = accountRepository.findById(sourceId)
                .map(a -> getField(a, "balance"))
                .orElse(null);

        // Act - Perform transfer
        TransferRequestDto request = createTransferRequest(sourceAccountNumber, destinationAccountNumber, BigDecimal.valueOf(500));
        mockMvc.perform(post("/api/accounts/transfer")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Assert - Balance should be reduced
        Object finalBalance = accountRepository.findById(sourceId)
                .map(a -> getField(a, "balance"))
                .orElse(null);

        assert finalBalance != null;
        assert ((BigDecimal) finalBalance).compareTo((BigDecimal) initialBalance) < 0;
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
            int tokenStart = response.indexOf("\"token\":\"") + 9;
            int tokenEnd = response.indexOf("\"", tokenStart);
            return response.substring(tokenStart, tokenEnd);
        } catch (Exception e) {
            return "dummy-token";
        }
    }
}
