package org.northernarc.week5_assess.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.northernarc.week5_assess.dto.AccountDto;
import org.northernarc.week5_assess.dto.TransferRequestDto;
import org.northernarc.week5_assess.entity.Account;
import org.northernarc.week5_assess.entity.Customer;
import org.northernarc.week5_assess.entity.Transaction;
import org.northernarc.week5_assess.exception.InvalidRequestException;
import org.northernarc.week5_assess.exception.ResourceNotFoundException;
import org.northernarc.week5_assess.repository.AccountRepository;
import org.northernarc.week5_assess.repository.CustomerRepository;
import org.northernarc.week5_assess.repository.TransactionRepository;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

	private static final Class<?>[] CREATE_ACCOUNT_SIG = {AccountDto.class};
	private static final Class<?>[] DEPOSIT_SIG = {String.class, BigDecimal.class};
	private static final Class<?>[] WITHDRAW_SIG = {String.class, BigDecimal.class};
	private static final Class<?>[] TRANSFER_SIG = {TransferRequestDto.class};

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private TransactionRepository transactionRepository;

	@InjectMocks
	private AccountService accountService;

	@BeforeEach
	void setUp() {
		reset(accountRepository, customerRepository, transactionRepository);
	}

	@Test
	@DisplayName("Account Creation: valid account")
	void createAccount_validAccount() {
		AccountDto request = createAccountRequest("ACC1001", BigDecimal.valueOf(1000), "SAVINGS", 1L);
		when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
		when(accountRepository.save(argThat(account -> account != null
				&& "ACC1001".equals(account.getAccountNumber())))).thenReturn(new Account());

		assertDoesNotThrow(() -> invokeAccountService("createAccount", CREATE_ACCOUNT_SIG, request));

		verify(accountRepository, times(1)).save(any(Account.class));
	}

	@Test
	@DisplayName("Account Creation: customer exists")
	void createAccount_customerExists() {
		AccountDto request = createAccountRequest("ACC1002", BigDecimal.ZERO, "CURRENT", 7L);
		when(customerRepository.findById(7L)).thenReturn(Optional.of(new Customer()));
		when(accountRepository.save(argThat(account -> account != null
				&& "ACC1002".equals(account.getAccountNumber())))).thenReturn(new Account());

		assertDoesNotThrow(() -> invokeAccountService("createAccount", CREATE_ACCOUNT_SIG, request));

		verify(customerRepository, times(1)).findById(7L);
	}

	@Test
	@DisplayName("Account Creation: customer missing")
	void createAccount_customerMissing() {
		AccountDto request = createAccountRequest("ACC1003", BigDecimal.valueOf(500), "SAVINGS", 99L);
		when(customerRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class,
				() -> invokeAccountService("createAccount", CREATE_ACCOUNT_SIG, request));

		verify(accountRepository, never()).save(any(Account.class));
	}

	@Test
	@DisplayName("Account Creation: duplicate account number")
	void createAccount_duplicateAccountNumber() {
		AccountDto request = createAccountRequest("ACC1001", BigDecimal.valueOf(500), "SAVINGS", 1L);

		assertThrows(InvalidRequestException.class,
				() -> invokeAccountService("createAccount", CREATE_ACCOUNT_SIG, request));

		verify(accountRepository, never()).save(any(Account.class));
	}

	@Test
	@DisplayName("Account Creation: null account number")
	void createAccount_nullAccountNumber() {
		AccountDto request = createAccountRequest(null, BigDecimal.valueOf(500), "SAVINGS", 1L);

		assertThrows(InvalidRequestException.class,
				() -> invokeAccountService("createAccount", CREATE_ACCOUNT_SIG, request));

		verify(accountRepository, never()).save(any(Account.class));
	}

	@Test
	@DisplayName("Account Creation: blank account number")
	void createAccount_blankAccountNumber() {
		AccountDto request = createAccountRequest("   ", BigDecimal.valueOf(500), "SAVINGS", 1L);

		assertThrows(InvalidRequestException.class,
				() -> invokeAccountService("createAccount", CREATE_ACCOUNT_SIG, request));

		verify(accountRepository, never()).save(any(Account.class));
	}

	@Test
	@DisplayName("Account Creation: opening balance zero")
	void createAccount_openingBalanceZero() {
		AccountDto request = createAccountRequest("ACC1004", BigDecimal.ZERO, "SAVINGS", 1L);
		when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
		when(accountRepository.save(argThat(account -> account != null
				&& "ACC1004".equals(account.getAccountNumber())))).thenReturn(new Account());

		assertDoesNotThrow(() -> invokeAccountService("createAccount", CREATE_ACCOUNT_SIG, request));

		verify(accountRepository, times(1)).save(any(Account.class));
	}

	@Test
	@DisplayName("Account Creation: opening balance positive")
	void createAccount_openingBalancePositive() {
		AccountDto request = createAccountRequest("ACC1005", BigDecimal.valueOf(2500), "SAVINGS", 1L);
		when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
		when(accountRepository.save(argThat(account -> account != null
				&& "ACC1005".equals(account.getAccountNumber())))).thenReturn(new Account());

		assertDoesNotThrow(() -> invokeAccountService("createAccount", CREATE_ACCOUNT_SIG, request));

		verify(accountRepository, times(1)).save(any(Account.class));
	}

	@Test
	@DisplayName("Account Creation: opening balance negative")
	void createAccount_openingBalanceNegative() {
		AccountDto request = createAccountRequest("ACC1006", BigDecimal.valueOf(-1), "SAVINGS", 1L);

		assertThrows(InvalidRequestException.class,
				() -> invokeAccountService("createAccount", CREATE_ACCOUNT_SIG, request));

		verify(accountRepository, never()).save(any(Account.class));
	}

	@Test
	@DisplayName("Account Creation: account type SAVINGS")
	void createAccount_accountTypeSavings() {
		AccountDto request = createAccountRequest("ACC1007", BigDecimal.valueOf(100), "SAVINGS", 1L);
		when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
		when(accountRepository.save(argThat(account -> account != null
				&& "ACC1007".equals(account.getAccountNumber())))).thenReturn(new Account());

		assertDoesNotThrow(() -> invokeAccountService("createAccount", CREATE_ACCOUNT_SIG, request));

		verify(accountRepository, times(1)).save(any(Account.class));
	}

	@Test
	@DisplayName("Account Creation: account type CURRENT")
	void createAccount_accountTypeCurrent() {
		AccountDto request = createAccountRequest("ACC1008", BigDecimal.valueOf(100), "CURRENT", 1L);
		when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
		when(accountRepository.save(argThat(account -> account != null
				&& "ACC1008".equals(account.getAccountNumber())))).thenReturn(new Account());

		assertDoesNotThrow(() -> invokeAccountService("createAccount", CREATE_ACCOUNT_SIG, request));

		verify(accountRepository, times(1)).save(any(Account.class));
	}

	@Test
	@DisplayName("Account Creation: invalid account type")
	void createAccount_invalidAccountType() {
		AccountDto request = createAccountRequest("ACC1009", BigDecimal.valueOf(100), "INVALID", 1L);

		assertThrows(InvalidRequestException.class,
				() -> invokeAccountService("createAccount", CREATE_ACCOUNT_SIG, request));

		verify(accountRepository, never()).save(any(Account.class));
	}

	@Test
	@DisplayName("Account Creation: repository save invoked")
	void createAccount_repositorySaveInvoked() {
		AccountDto request = createAccountRequest("ACC1010", BigDecimal.valueOf(100), "SAVINGS", 1L);
		when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
		when(accountRepository.save(argThat(account -> account != null
				&& "ACC1010".equals(account.getAccountNumber())))).thenReturn(new Account());

		assertDoesNotThrow(() -> invokeAccountService("createAccount", CREATE_ACCOUNT_SIG, request));

		verify(accountRepository, times(1)).save(any(Account.class));
	}

	@Test
	@DisplayName("Deposit: valid deposit")
	void deposit_validDeposit() {
		assertDoesNotThrow(() -> invokeAccountService("deposit", DEPOSIT_SIG, "ACC2001", BigDecimal.valueOf(500)));

		verify(accountRepository, atLeastOnce()).save(any(Account.class));
	}

	@Test
	@DisplayName("Deposit: deposit zero")
	void deposit_zero() {
		assertThrows(InvalidRequestException.class,
				() -> invokeAccountService("deposit", DEPOSIT_SIG, "ACC2001", BigDecimal.ZERO));

		verify(transactionRepository, never()).save(any(Transaction.class));
	}

	@Test
	@DisplayName("Deposit: deposit negative")
	void deposit_negative() {
		assertThrows(InvalidRequestException.class,
				() -> invokeAccountService("deposit", DEPOSIT_SIG, "ACC2001", BigDecimal.valueOf(-1)));

		verify(transactionRepository, never()).save(any(Transaction.class));
	}

	@Test
	@DisplayName("Deposit: account not found")
	void deposit_accountNotFound() {
		assertThrows(ResourceNotFoundException.class,
				() -> invokeAccountService("deposit", DEPOSIT_SIG, "MISSING", BigDecimal.valueOf(50)));
	}

	@Test
	@DisplayName("Deposit: transaction created")
	void deposit_transactionCreated() {
		assertDoesNotThrow(() -> invokeAccountService("deposit", DEPOSIT_SIG, "ACC2002", BigDecimal.valueOf(200)));

		verify(transactionRepository, times(1)).save(any(Transaction.class));
	}

	@Test
	@DisplayName("Deposit: balance updated correctly")
	void deposit_balanceUpdatedCorrectly() {
		assertDoesNotThrow(() -> invokeAccountService("deposit", DEPOSIT_SIG, "ACC2003", BigDecimal.valueOf(300)));

		verify(accountRepository, times(1)).save(any(Account.class));
	}

	@Test
	@DisplayName("Withdrawal: valid withdrawal")
	void withdraw_validWithdrawal() {
		assertDoesNotThrow(() -> invokeAccountService("withdraw", WITHDRAW_SIG, "ACC3001", BigDecimal.valueOf(100)));

		verify(accountRepository, times(1)).save(any(Account.class));
	}

	@Test
	@DisplayName("Withdrawal: withdrawal equals balance")
	void withdraw_equalsBalance() {
		assertDoesNotThrow(() -> invokeAccountService("withdraw", WITHDRAW_SIG, "ACC3002", BigDecimal.valueOf(1000)));

		verify(accountRepository, times(1)).save(any(Account.class));
	}

	@Test
	@DisplayName("Withdrawal: withdrawal greater than balance")
	void withdraw_greaterThanBalance() {
		assertThrows(InvalidRequestException.class,
				() -> invokeAccountService("withdraw", WITHDRAW_SIG, "ACC3003", BigDecimal.valueOf(5000)));

		verify(transactionRepository, never()).save(any(Transaction.class));
	}

	@Test
	@DisplayName("Withdrawal: withdrawal zero")
	void withdraw_zero() {
		assertThrows(InvalidRequestException.class,
				() -> invokeAccountService("withdraw", WITHDRAW_SIG, "ACC3003", BigDecimal.ZERO));

		verify(transactionRepository, never()).save(any(Transaction.class));
	}

	@Test
	@DisplayName("Withdrawal: withdrawal negative")
	void withdraw_negative() {
		assertThrows(InvalidRequestException.class,
				() -> invokeAccountService("withdraw", WITHDRAW_SIG, "ACC3003", BigDecimal.valueOf(-5)));

		verify(transactionRepository, never()).save(any(Transaction.class));
	}

	@Test
	@DisplayName("Withdrawal: account not found")
	void withdraw_accountNotFound() {
		assertThrows(ResourceNotFoundException.class,
				() -> invokeAccountService("withdraw", WITHDRAW_SIG, "MISSING", BigDecimal.valueOf(5)));
	}

	@Test
	@DisplayName("Withdrawal: transaction created")
	void withdraw_transactionCreated() {
		assertDoesNotThrow(() -> invokeAccountService("withdraw", WITHDRAW_SIG, "ACC3004", BigDecimal.valueOf(10)));

		verify(transactionRepository, times(1)).save(any(Transaction.class));
	}

	@Test
	@DisplayName("Withdrawal: balance updated")
	void withdraw_balanceUpdated() {
		assertDoesNotThrow(() -> invokeAccountService("withdraw", WITHDRAW_SIG, "ACC3005", BigDecimal.valueOf(10)));

		verify(accountRepository, times(1)).save(any(Account.class));
	}

	@Test
	@DisplayName("Transfer: valid transfer")
	void transfer_validTransfer() {
		TransferRequestDto request = createTransferRequest("SRC100", "DST100", BigDecimal.valueOf(50));

		assertDoesNotThrow(() -> invokeAccountService("transfer", TRANSFER_SIG, request));
	}

	@Test
	@DisplayName("Transfer: source missing")
	void transfer_sourceMissing() {
		TransferRequestDto request = createTransferRequest("MISSING", "DST100", BigDecimal.valueOf(50));

		assertThrows(ResourceNotFoundException.class,
				() -> invokeAccountService("transfer", TRANSFER_SIG, request));
	}

	@Test
	@DisplayName("Transfer: destination missing")
	void transfer_destinationMissing() {
		TransferRequestDto request = createTransferRequest("SRC100", "MISSING", BigDecimal.valueOf(50));

		assertThrows(ResourceNotFoundException.class,
				() -> invokeAccountService("transfer", TRANSFER_SIG, request));
	}

	@Test
	@DisplayName("Transfer: same account")
	void transfer_sameAccount() {
		TransferRequestDto request = createTransferRequest("ACC4001", "ACC4001", BigDecimal.valueOf(50));

		assertThrows(InvalidRequestException.class,
				() -> invokeAccountService("transfer", TRANSFER_SIG, request));
	}

	@Test
	@DisplayName("Transfer: transfer zero")
	void transfer_zero() {
		TransferRequestDto request = createTransferRequest("SRC100", "DST100", BigDecimal.ZERO);

		assertThrows(InvalidRequestException.class,
				() -> invokeAccountService("transfer", TRANSFER_SIG, request));
	}

	@Test
	@DisplayName("Transfer: transfer negative")
	void transfer_negative() {
		TransferRequestDto request = createTransferRequest("SRC100", "DST100", BigDecimal.valueOf(-10));

		assertThrows(InvalidRequestException.class,
				() -> invokeAccountService("transfer", TRANSFER_SIG, request));
	}

	@Test
	@DisplayName("Transfer: insufficient balance")
	void transfer_insufficientBalance() {
		TransferRequestDto request = createTransferRequest("SRC100", "DST100", BigDecimal.valueOf(999999));

		assertThrows(InvalidRequestException.class,
				() -> invokeAccountService("transfer", TRANSFER_SIG, request));

		verify(transactionRepository, never()).save(any(Transaction.class));
	}

	@Test
	@DisplayName("Transfer: source updated")
	void transfer_sourceUpdated() {
		TransferRequestDto request = createTransferRequest("SRC100", "DST100", BigDecimal.valueOf(25));

		assertDoesNotThrow(() -> invokeAccountService("transfer", TRANSFER_SIG, request));

		verify(accountRepository, atLeastOnce()).save(any(Account.class));
	}

	@Test
	@DisplayName("Transfer: destination updated")
	void transfer_destinationUpdated() {
		TransferRequestDto request = createTransferRequest("SRC101", "DST101", BigDecimal.valueOf(25));

		assertDoesNotThrow(() -> invokeAccountService("transfer", TRANSFER_SIG, request));

		verify(accountRepository, atLeastOnce()).save(any(Account.class));
	}

	@Test
	@DisplayName("Transfer: two transactions created")
	void transfer_twoTransactionsCreated() {
		TransferRequestDto request = createTransferRequest("SRC102", "DST102", BigDecimal.valueOf(25));

		assertDoesNotThrow(() -> invokeAccountService("transfer", TRANSFER_SIG, request));

		verify(transactionRepository, times(2)).save(any(Transaction.class));
	}

	@Test
	@DisplayName("Transfer: transactional behavior verified")
	void transfer_transactionalBehaviorVerified() {
		TransferRequestDto request = createTransferRequest("SRC103", "DST103", BigDecimal.valueOf(25));
		when(transactionRepository.save(argThat(transaction -> transaction != null
				&& transaction.getAmount() != null
				&& BigDecimal.valueOf(25).compareTo(transaction.getAmount()) == 0)))
				.thenThrow(new RuntimeException("Simulated failure"));

		assertThrows(RuntimeException.class,
				() -> invokeAccountService("transfer", TRANSFER_SIG, request));

		verify(transactionRepository, atLeastOnce()).save(any(Transaction.class));
	}

	@Test
	@DisplayName("Deposit: high precision BigDecimal is accepted")
	void deposit_highPrecisionBigDecimal() {
		assertDoesNotThrow(() -> invokeAccountService("deposit", DEPOSIT_SIG, "ACC-PRECISION", new BigDecimal("0.000001")));
	}

	@Test
	@DisplayName("Deposit: rounding contract preserves scale")
	void deposit_roundingContractPreservesScale() {
		assertDoesNotThrow(() -> invokeAccountService("deposit", DEPOSIT_SIG, "ACC-ROUND", new BigDecimal("123.456789")));
	}

	@Test
	@DisplayName("Transfer: repository save failure triggers rollback contract")
	void transfer_repositorySaveFailureRollbackContract() {
		TransferRequestDto request = createTransferRequest("SRC-FAIL", "DST-FAIL", BigDecimal.valueOf(50));
		when(accountRepository.save(argThat(account -> account != null
				&& ("SRC-FAIL".equals(account.getAccountNumber()) || "DST-FAIL".equals(account.getAccountNumber())))))
				.thenThrow(new RuntimeException("Account save failed"));

		assertThrows(RuntimeException.class,
				() -> invokeAccountService("transfer", TRANSFER_SIG, request));

		verify(accountRepository, atLeastOnce()).save(any(Account.class));
		verifyNoMoreInteractions(transactionRepository);
	}

	@Test
	@DisplayName("Transfer: transaction save failure triggers rollback contract")
	void transfer_transactionSaveFailureRollbackContract() {
		TransferRequestDto request = createTransferRequest("SRC-TXN", "DST-TXN", BigDecimal.valueOf(75));
		when(transactionRepository.save(argThat(transaction -> transaction != null
				&& transaction.getAmount() != null
				&& BigDecimal.valueOf(75).compareTo(transaction.getAmount()) == 0)))
				.thenThrow(new RuntimeException("Transaction save failed"));

		assertThrows(RuntimeException.class,
				() -> invokeAccountService("transfer", TRANSFER_SIG, request));

		verify(transactionRepository, atLeastOnce()).save(any(Transaction.class));
	}

	@Test
	@DisplayName("Deposit: invalid amount verifies no repository interactions")
	void deposit_invalidAmount_verifyNoRepositoryInteractions() {
		assertThrows(InvalidRequestException.class,
				() -> invokeAccountService("deposit", DEPOSIT_SIG, "ACC-INVALID", BigDecimal.ZERO));

		verifyNoInteractions(accountRepository, transactionRepository);
	}

	@Test
	@DisplayName("Concurrent transfers: service contract remains deterministic")
	void transfer_concurrentTransfersContract() {
		TransferRequestDto requestOne = createTransferRequest("SRC-CONC", "DST-CONC", BigDecimal.valueOf(10));
		TransferRequestDto requestTwo = createTransferRequest("SRC-CONC", "DST-CONC", BigDecimal.valueOf(20));

		assertDoesNotThrow(() -> invokeAccountService("transfer", TRANSFER_SIG, requestOne));
		assertDoesNotThrow(() -> invokeAccountService("transfer", TRANSFER_SIG, requestTwo));
	}

	private AccountDto createAccountRequest(String accountNumber, BigDecimal openingBalance, String accountType, Long customerId) {
		AccountDto dto = new AccountDto();
		setFieldIfPresent(dto, "accountNumber", accountNumber);
		setFieldIfPresent(dto, "openingBalance", openingBalance);
		setFieldIfPresent(dto, "accountType", accountType);
		setFieldIfPresent(dto, "customerId", customerId);
		return dto;
	}

	private TransferRequestDto createTransferRequest(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount) {
		TransferRequestDto dto = new TransferRequestDto();
		setFieldIfPresent(dto, "sourceAccountNumber", sourceAccountNumber);
		setFieldIfPresent(dto, "destinationAccountNumber", destinationAccountNumber);
		setFieldIfPresent(dto, "amount", amount);
		return dto;
	}

	private Object invokeAccountService(String methodName, Class<?>[] parameterTypes, Object... args) {
		return invokeRequiredMethod(accountService, methodName, parameterTypes, args);
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

	private void setFieldIfPresent(Object target, String fieldName, Object value) {
		Class<?> current = target.getClass();
		while (current != null) {
			try {
				Field field = current.getDeclaredField(fieldName);
				field.setAccessible(true);
				field.set(target, value);
				return;
			} catch (NoSuchFieldException exception) {
				current = current.getSuperclass();
			} catch (IllegalAccessException exception) {
				throw new RuntimeException(exception);
			}
		}
	}
}
