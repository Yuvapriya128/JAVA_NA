package org.northernarc.week5_assess.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.northernarc.week5_assess.entity.Account;
import org.northernarc.week5_assess.entity.Transaction;
import org.northernarc.week5_assess.exception.InvalidRequestException;
import org.northernarc.week5_assess.exception.ResourceNotFoundException;
import org.northernarc.week5_assess.repository.AccountRepository;
import org.northernarc.week5_assess.repository.TransactionRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

	private static final Class<?>[] GET_TRANSACTION_BY_ID_SIG = {Long.class};
	private static final Class<?>[] GET_ALL_TRANSACTIONS_SIG = {};
	private static final Class<?>[] GET_TRANSACTIONS_BY_ACCOUNT_SIG = {String.class};
	private static final Class<?>[] GET_TRANSACTIONS_BY_ACCOUNT_ID_SIG = {Long.class};

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private AccountRepository accountRepository;

	@InjectMocks
	private TransactionService transactionService;

	@BeforeEach
	void setUp() {
		reset(transactionRepository, accountRepository);
	}

	@Test
	@DisplayName("Get Transaction: valid id")
	void getTransaction_validId() {
		when(transactionRepository.findById(1L)).thenReturn(Optional.of(new Transaction()));

		Object response = assertDoesNotThrow(
				() -> invokeTransactionService("getTransactionById", GET_TRANSACTION_BY_ID_SIG, 1L));

		assertNotNull(response);
		verify(transactionRepository, times(1)).findById(1L);
	}

	@Test
	@DisplayName("Get Transaction: invalid id")
	void getTransaction_invalidId() {
		assertThrows(InvalidRequestException.class,
				() -> invokeTransactionService("getTransactionById", GET_TRANSACTION_BY_ID_SIG, -1L));

		verify(transactionRepository, never()).findById(any(Long.class));
	}

	@Test
	@DisplayName("Get Transaction: null id")
	void getTransaction_nullId() {
		assertThrows(InvalidRequestException.class,
				() -> invokeTransactionService("getTransactionById", GET_TRANSACTION_BY_ID_SIG, new Object[]{null}));

		verify(transactionRepository, never()).findById(any(Long.class));
	}

	@Test
	@DisplayName("Get All Transactions: records exist")
	void getAllTransactions_recordsExist() {
		when(transactionRepository.findAll()).thenReturn(List.of(new Transaction(), new Transaction()));

		Object response = assertDoesNotThrow(
				() -> invokeTransactionService("getAllTransactions", GET_ALL_TRANSACTIONS_SIG));

		assertTrue(response instanceof List<?>);
		verify(transactionRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("Get All Transactions: empty list")
	void getAllTransactions_emptyList() {
		when(transactionRepository.findAll()).thenReturn(List.of());

		Object response = assertDoesNotThrow(
				() -> invokeTransactionService("getAllTransactions", GET_ALL_TRANSACTIONS_SIG));

		assertTrue(response instanceof List<?>);
		assertTrue(((List<?>) response).isEmpty());
		verify(transactionRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("Get Transactions By Account: valid account")
	void getTransactionsByAccount_validAccount() {
		when(accountRepository.findById(1L)).thenReturn(Optional.of(new Account()));

		assertDoesNotThrow(() -> invokeTransactionService(
				"getTransactionsByAccount", GET_TRANSACTIONS_BY_ACCOUNT_SIG, "ACC5001"));

		verify(transactionRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("Get Transactions By Account: invalid account")
	void getTransactionsByAccount_invalidAccount() {
		assertThrows(ResourceNotFoundException.class,
				() -> invokeTransactionService("getTransactionsByAccount", GET_TRANSACTIONS_BY_ACCOUNT_SIG, "MISSING"));
	}

	@Test
	@DisplayName("Get Transactions By Account: empty transaction history")
	void getTransactionsByAccount_emptyTransactionHistory() {
		when(accountRepository.findById(1L)).thenReturn(Optional.of(new Account()));
		when(transactionRepository.findAll()).thenReturn(List.of());

		Object response = assertDoesNotThrow(
				() -> invokeTransactionService("getTransactionsByAccount", GET_TRANSACTIONS_BY_ACCOUNT_SIG, "ACC5001"));

		assertTrue(response instanceof List<?>);
		assertTrue(((List<?>) response).isEmpty());
	}

	@Test
	@DisplayName("Get Transactions By Account Id: null account id")
	void getTransactionsByAccountId_nullId() {
		assertThrows(InvalidRequestException.class,
				() -> invokeTransactionService("getTransactionsByAccountId", GET_TRANSACTIONS_BY_ACCOUNT_ID_SIG, new Object[]{null}));

		verify(transactionRepository, never()).findByAccountId(any(Long.class));
	}

	@Test
	@DisplayName("Get Transactions By Account Id: negative account id")
	void getTransactionsByAccountId_negativeId() {
		assertThrows(InvalidRequestException.class,
				() -> invokeTransactionService("getTransactionsByAccountId", GET_TRANSACTIONS_BY_ACCOUNT_ID_SIG, -1L));

		verify(transactionRepository, never()).findByAccountId(any(Long.class));
	}

	@Test
	@DisplayName("Get Transactions By Account Id: zero account id")
	void getTransactionsByAccountId_zeroId() {
		assertThrows(InvalidRequestException.class,
				() -> invokeTransactionService("getTransactionsByAccountId", GET_TRANSACTIONS_BY_ACCOUNT_ID_SIG, 0L));

		verify(transactionRepository, never()).findByAccountId(any(Long.class));
	}

	@Test
	@DisplayName("Get All Transactions: repository exception")
	void getAllTransactions_repositoryException() {
		when(transactionRepository.findAll()).thenThrow(new RuntimeException("Repository failure"));

		assertThrows(RuntimeException.class,
				() -> invokeTransactionService("getAllTransactions", GET_ALL_TRANSACTIONS_SIG));

		verify(transactionRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("Get Transactions By Account: empty transaction history")
	void getTransactionsByAccount_emptyHistory() {
		when(accountRepository.findById(1L)).thenReturn(Optional.of(new Account()));
		when(transactionRepository.findAll()).thenReturn(List.of());

		Object response = assertDoesNotThrow(() -> invokeTransactionService(
				"getTransactionsByAccount", GET_TRANSACTIONS_BY_ACCOUNT_SIG, "ACC5002"));

		assertTrue(response instanceof List<?>);
		assertTrue(((List<?>) response).isEmpty());
		verify(transactionRepository, times(1)).findAll();
	}

	private Object invokeTransactionService(String methodName, Class<?>[] parameterTypes, Object... args) {
		return invokeRequiredMethod(transactionService, methodName, parameterTypes, args);
	}

	private Object invokeRequiredMethod(Object target, String methodName, Class<?>[] parameterTypes, Object... args) {
		try {
			Method method = target.getClass().getDeclaredMethod(methodName, parameterTypes);
			method.setAccessible(true);
			return method.invoke(target, args);
		} catch (NoSuchMethodException exception) {
			fail("Missing service method: " + target.getClass().getSimpleName() + "." + methodName);
			return null;
		} catch (InvocationTargetException exception) {
			Throwable cause = exception.getTargetException();
			if (cause instanceof RuntimeException runtimeException) {
				throw runtimeException;
			}
			if (cause instanceof Error error) {
				throw error;
			}
			throw new RuntimeException(cause);
		} catch (IllegalAccessException exception) {
			throw new RuntimeException(exception);
		}
	}
}
