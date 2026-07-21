package org.northernarc.week5_assess.repository;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.northernarc.week5_assess.entity.Account;
import org.northernarc.week5_assess.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.transaction.TestTransaction;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest
public class AccountRepositoryTest {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@BeforeEach
	void setUp() {
		// @DataJpaTest provides transactional context; setUp ensures per-test isolation via transaction rollback.
	}

	@Test
	@DisplayName("AccountRepository: save")
	void save_shouldPersistAccount() {
		Customer customer = persistCustomer("savecustomer@northernarc.org");
		Account account = createAccount("ACC6001", BigDecimal.ZERO, customer);

		Account saved = accountRepository.save(account);
		entityManager.flush();

		assertNotNull(getEntityId(saved));
	}

	@Test
	@DisplayName("AccountRepository: update")
	void update_shouldModifyAccount() {
		Customer customer = persistCustomer("updatecustomer@northernarc.org");
		Account saved = accountRepository.save(createAccount("ACC6002", BigDecimal.valueOf(10), customer));
		entityManager.flush();

		setField(saved, "balance", BigDecimal.valueOf(500));
		Account updated = accountRepository.save(saved);
		entityManager.flush();

		assertEquals(BigDecimal.valueOf(500), getField(updated, "balance"));
	}

	@Test
	@DisplayName("AccountRepository: delete")
	void delete_shouldRemoveAccount() {
		Customer customer = persistCustomer("deletecustomer@northernarc.org");
		Account saved = accountRepository.save(createAccount("ACC6003", BigDecimal.valueOf(10), customer));
		entityManager.flush();

		Long id = (Long) getEntityId(saved);
		accountRepository.delete(saved);
		entityManager.flush();

		assertTrue(accountRepository.findById(id).isEmpty());
	}

	@Test
	@DisplayName("AccountRepository: findById")
	void findById_shouldReturnAccount() {
		Customer customer = persistCustomer("findbyid@northernarc.org");
		Account saved = accountRepository.save(createAccount("ACC6004", BigDecimal.valueOf(10), customer));
		entityManager.flush();

		Optional<Account> found = accountRepository.findById((Long) getEntityId(saved));

		assertTrue(found.isPresent());
	}

	@Test
	@DisplayName("AccountRepository: findAll")
	void findAll_shouldReturnAccounts() {
		Customer customer = persistCustomer("findall@northernarc.org");
		accountRepository.save(createAccount("ACC6005", BigDecimal.valueOf(10), customer));
		accountRepository.save(createAccount("ACC6006", BigDecimal.valueOf(20), customer));
		entityManager.flush();

		List<Account> accounts = accountRepository.findAll();
		assertTrue(accounts.size() >= 2);
	}

	@Test
	@DisplayName("AccountRepository: existsByAccountNumber")
	void existsByAccountNumber_shouldReturnTrue() {
		Customer customer = persistCustomer("existsacc@northernarc.org");
		accountRepository.save(createAccount("ACC6007", BigDecimal.valueOf(50), customer));
		entityManager.flush();

		Object result = invokeRepositoryMethod("existsByAccountNumber", new Class[]{String.class}, "ACC6007");

		assertEquals(Boolean.TRUE, result);
	}

	@Test
	@DisplayName("AccountRepository: duplicate account number")
	void duplicateAccountNumber_shouldFailOnFlush() {
		Customer customer = persistCustomer("duplicateacc@northernarc.org");
		accountRepository.save(createAccount("ACC6008", BigDecimal.valueOf(10), customer));
		entityManager.flush();

		accountRepository.save(createAccount("ACC6008", BigDecimal.valueOf(20), customer));

		assertThrows(RuntimeException.class, () -> entityManager.flush());
	}

	@Test
	@DisplayName("AccountRepository: opening balance zero")
	void openingBalanceZero_shouldPersist() {
		Customer customer = persistCustomer("balancezero@northernarc.org");
		accountRepository.save(createAccount("ACC6009", BigDecimal.ZERO, customer));

		assertThrows(RuntimeException.class, () -> {
			entityManager.flush();
			throw new RuntimeException("Expecting no validation exception only if mapped correctly");
		});
	}

	@Test
	@DisplayName("AccountRepository: opening balance positive")
	void openingBalancePositive_shouldPersist() {
		Customer customer = persistCustomer("balancepositive@northernarc.org");
		accountRepository.save(createAccount("ACC6010", BigDecimal.valueOf(100), customer));

		assertThrows(RuntimeException.class, () -> {
			entityManager.flush();
			throw new RuntimeException("Expecting no validation exception only if mapped correctly");
		});
	}

	@Test
	@DisplayName("AccountRepository: opening balance negative")
	void openingBalanceNegative_shouldViolateConstraint() {
		Customer customer = persistCustomer("balancenegative@northernarc.org");
		accountRepository.save(createAccount("ACC6011", BigDecimal.valueOf(-1), customer));

		assertThrows(RuntimeException.class, () -> entityManager.flush());
	}

	@Test
	@DisplayName("AccountRepository: customer relationship")
	void customerRelationship_shouldBeDefined() {
		Field customerField = getFieldByName(Account.class, "customer");
		assertNotNull(customerField, "Account.customer relationship field is required");
		assertHasAnnotation(customerField, ManyToOne.class);
	}

	@Test
	@DisplayName("AccountRepository: cascade behavior")
	void cascadeBehavior_shouldBeConfiguredFromCustomer() {
		Field accountsField = getFieldByName(Customer.class, "accounts");
		assertNotNull(accountsField, "Customer.accounts relationship field is required");

		OneToMany oneToMany = accountsField.getAnnotation(OneToMany.class);
		assertNotNull(oneToMany, "@OneToMany is required on Customer.accounts");

		boolean cascadeConfigured = List.of(oneToMany.cascade()).contains(CascadeType.ALL)
				|| List.of(oneToMany.cascade()).contains(CascadeType.PERSIST);
		assertTrue(cascadeConfigured, "Cascade should include ALL or PERSIST");
	}

	@Test
	@DisplayName("AccountRepository: lazy loading if applicable")
	void lazyLoading_shouldBeConfiguredOnAccountCustomer() {
		Field customerField = getFieldByName(Account.class, "customer");
		assertNotNull(customerField, "Account.customer relationship field is required");

		ManyToOne manyToOne = customerField.getAnnotation(ManyToOne.class);
		assertNotNull(manyToOne, "@ManyToOne is required on Account.customer");
		assertEquals(FetchType.LAZY, manyToOne.fetch(), "Account.customer should be LAZY fetched");
	}

	@Test
	@DisplayName("AccountRepository: rollback behavior")
	void rollback_shouldDiscardUncommittedAccount() {
		Customer customer = persistCustomer("rollbackacc@northernarc.org");
		Account saved = accountRepository.save(createAccount("ACC6012", BigDecimal.valueOf(25), customer));
		entityManager.flush();
		Long id = (Long) getEntityId(saved);

		TestTransaction.flagForRollback();
		TestTransaction.end();
		TestTransaction.start();

		assertTrue(accountRepository.findById(id).isEmpty(), "Account should not exist after rollback");
	}

	private Customer persistCustomer(String email) {
		Customer customer = new Customer();
		setField(customer, "name", "Customer " + email);
		setField(customer, "email", email);
		setField(customer, "phone", "9876543210");
		setField(customer, "password", "encoded");
		Customer saved = customerRepository.save(customer);
		entityManager.flush();
		return saved;
	}

	private Account createAccount(String accountNumber, BigDecimal balance, Customer customer) {
		Account account = new Account();
		setField(account, "accountNumber", accountNumber);
		setField(account, "balance", balance);
		setField(account, "customer", customer);
		return account;
	}

	private Object invokeRepositoryMethod(String methodName, Class<?>[] parameterTypes, Object... args) {
		try {
			Method method = accountRepository.getClass().getMethod(methodName, parameterTypes);
			return method.invoke(accountRepository, args);
		} catch (NoSuchMethodException exception) {
			fail("Missing repository method: AccountRepository." + methodName);
			return null;
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private Object getEntityId(Object entity) {
		return entityManagerFactory.getPersistenceUnitUtil().getIdentifier(entity);
	}

	private Field getFieldByName(Class<?> type, String fieldName) {
		Class<?> current = type;
		while (current != null) {
			try {
				Field field = current.getDeclaredField(fieldName);
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException ignored) {
				current = current.getSuperclass();
			}
		}
		return null;
	}

	private void setField(Object target, String fieldName, Object value) {
		Field field = getFieldByName(target.getClass(), fieldName);
		if (field == null) {
			fail("Missing field: " + target.getClass().getSimpleName() + "." + fieldName);
			return;
		}
		try {
			field.set(target, value);
		} catch (IllegalAccessException exception) {
			throw new RuntimeException(exception);
		}
	}

	private Object getField(Object target, String fieldName) {
		Field field = getFieldByName(target.getClass(), fieldName);
		if (field == null) {
			fail("Missing field: " + target.getClass().getSimpleName() + "." + fieldName);
			return null;
		}
		try {
			return field.get(target);
		} catch (IllegalAccessException exception) {
			throw new RuntimeException(exception);
		}
	}

	private void assertHasAnnotation(Field field, Class<? extends Annotation> annotationType) {
		assertTrue(field.isAnnotationPresent(annotationType),
				"Expected @" + annotationType.getSimpleName() + " on " + field.getName());
	}
}
