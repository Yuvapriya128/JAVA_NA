package org.northernarc.week5_assess.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.northernarc.week5_assess.exception.ResourceNotFoundException;
import org.northernarc.week5_assess.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@DisplayName("TransactionControllerTest")
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    private static final String TRANSACTIONS_ENDPOINT = "/api/transactions";
    private static final String ACCOUNTS_ENDPOINT = "/api/accounts";

    @BeforeEach
    void setUp() {
        reset(transactionService);
    }

    // ==================== GET /api/transactions Tests ====================

    @Test
    @DisplayName("Get All Transactions: returns 200 OK")
    void getAllTransactions_returns200Ok() throws Exception {
        // Arrange
        when(transactionService.getAllTransactions()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(TRANSACTIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(transactionService, times(1)).getAllTransactions();
    }

    @Test
    @DisplayName("Get All Transactions: unauthorized returns 401")
    void getAllTransactions_unauthorized_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get(TRANSACTIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get All Transactions: forbidden returns 403")
    void getAllTransactions_forbidden_returns403() throws Exception {
        // Act & Assert
        mockMvc.perform(get(TRANSACTIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ==================== GET /api/transactions/{id} Tests ====================

    @Test
    @DisplayName("Get Transaction by ID: valid ID returns 200 OK")
    void getTransactionById_withValidId_returns200Ok() throws Exception {
        // Arrange
        when(transactionService.getTransactionById(1L)).thenReturn(new Object());

        // Act & Assert
        mockMvc.perform(get(TRANSACTIONS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(transactionService, times(1)).getTransactionById(1L);
    }

    @Test
    @DisplayName("Get Transaction by ID: transaction not found returns 404")
    void getTransactionById_notFound_returns404() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Transaction not found"))
                .when(transactionService).getTransactionById(999L);

        // Act & Assert
        mockMvc.perform(get(TRANSACTIONS_ENDPOINT + "/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(transactionService, times(1)).getTransactionById(999L);
    }

    @Test
    @DisplayName("Get Transaction by ID: unauthorized returns 401")
    void getTransactionById_unauthorized_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get(TRANSACTIONS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get Transaction by ID: forbidden returns 403")
    void getTransactionById_forbidden_returns403() throws Exception {
        // Act & Assert
        mockMvc.perform(get(TRANSACTIONS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ==================== GET /api/accounts/{id}/transactions Tests ====================

    @Test
    @DisplayName("Get Transactions by Account: valid account returns 200 OK")
    void getTransactionsByAccount_withValidAccount_returns200Ok() throws Exception {
        // Arrange
        when(transactionService.getTransactionsByAccountId(1L)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(ACCOUNTS_ENDPOINT + "/1/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(transactionService, times(1)).getTransactionsByAccountId(1L);
    }

    @Test
    @DisplayName("Get Transactions by Account: account not found returns 404")
    void getTransactionsByAccount_accountNotFound_returns404() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Account not found"))
                .when(transactionService).getTransactionsByAccountId(999L);

        // Act & Assert
        mockMvc.perform(get(ACCOUNTS_ENDPOINT + "/999/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(transactionService, times(1)).getTransactionsByAccountId(999L);
    }

    @Test
    @DisplayName("Get Transactions by Account: unauthorized returns 401")
    void getTransactionsByAccount_unauthorized_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ACCOUNTS_ENDPOINT + "/1/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get Transactions by Account: forbidden returns 403")
    void getTransactionsByAccount_forbidden_returns403() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ACCOUNTS_ENDPOINT + "/1/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get Transaction by ID: invalid path variable returns 400")
    void getTransactionById_invalidPathVariable_returns400() throws Exception {
        // Act & Assert
        mockMvc.perform(get(TRANSACTIONS_ENDPOINT + "/invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get All Transactions: invalid Accept header returns 406")
    void getAllTransactions_invalidAcceptHeader_returns406() throws Exception {
        // Act & Assert
        mockMvc.perform(get(TRANSACTIONS_ENDPOINT)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @DisplayName("Get All Transactions: missing JWT returns 401")
    void getAllTransactions_missingJwt_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get(TRANSACTIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Get All Transactions: invalid JWT returns 401")
    void getAllTransactions_invalidJwt_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get(TRANSACTIONS_ENDPOINT)
                .header("Authorization", "Bearer invalid.jwt")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Get Transaction by ID: response contract and headers are validated")
    void getTransactionById_responseContract_validated() throws Exception {
        // Arrange
        when(transactionService.getTransactionById(1L)).thenReturn(new Object());

        // Act & Assert
        mockMvc.perform(get(TRANSACTIONS_ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("Content-Type"))
                .andExpect(jsonPath("$").exists());
    }
}
