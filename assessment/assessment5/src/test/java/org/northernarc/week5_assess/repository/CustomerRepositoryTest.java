package org.northernarc.week5_assess.repository;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OneToMany;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.northernarc.week5_assess.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.transaction.TestTransaction;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest
public class CustomerRepositoryTest {

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
	@DisplayName("CustomerRepository: save")
	void save_shouldPersistCustomer() {
		Customer customer = createCustomer("Alex", "alex@northernarc.org", "9876543210");

		Customer saved = customerRepository.save(customer);
		entityManager.flush();
		entityManager.clear();

		Object id = getEntityId(saved);
		assertNotNull(id, "Saved entity should have generated identifier");
	}

	@Test
	@DisplayName("CustomerRepository: update")
	void update_shouldModifyCustomer() {
		Customer saved = customerRepository.save(createCustomer("Alex", "alex1@northernarc.org", "9876543210"));
		entityManager.flush();

		setField(saved, "name", "Alex Updated");
		Customer updated = customerRepository.save(saved);
		entityManager.flush();
		entityManager.clear();

		assertEquals("Alex Updated", getField(updated, "name"));
	}

	@Test
	@DisplayName("CustomerRepository: delete")
	void delete_shouldRemoveCustomer() {
		Customer saved = customerRepository.save(createCustomer("Alex", "alex2@northernarc.org", "9876543210"));
		entityManager.flush();

		Object id = getEntityId(saved);
		customerRepository.delete(saved);
		entityManager.flush();

		Optional<Customer> deleted = customerRepository.findById((Long) id);
		assertTrue(deleted.isEmpty());
	}

	@Test
	@DisplayName("CustomerRepository: findById")
	void findById_shouldReturnCustomer() {
		Customer saved = customerRepository.save(createCustomer("Alex", "alex3@northernarc.org", "9876543210"));
		entityManager.flush();

		Optional<Customer> found = customerRepository.findById((Long) getEntityId(saved));

		assertTrue(found.isPresent());
	}

	@Test
	@DisplayName("CustomerRepository: findAll")
	void findAll_shouldReturnAllCustomers() {
		customerRepository.save(createCustomer("Alex", "alex4@northernarc.org", "9876543210"));
		customerRepository.save(createCustomer("Sam", "sam4@northernarc.org", "9876543211"));
		entityManager.flush();

		List<Customer> customers = customerRepository.findAll();

		assertTrue(customers.size() >= 2);
	}

	@Test
	@DisplayName("CustomerRepository: findByEmail")
	void findByEmail_shouldReturnCustomer() {
		customerRepository.save(createCustomer("Alex", "lookup@northernarc.org", "9876543210"));
		entityManager.flush();

		Object result = invokeRepositoryMethod("findByEmail", new Class[]{String.class}, "lookup@northernarc.org");

		assertNotNull(result, "findByEmail should return Optional<Customer> or Customer");
	}

	@Test
	@DisplayName("CustomerRepository: existsByEmail")
	void existsByEmail_shouldReturnTrueWhenExists() {
		customerRepository.save(createCustomer("Alex", "exists@northernarc.org", "9876543210"));
		entityManager.flush();

		Object result = invokeRepositoryMethod("existsByEmail", new Class[]{String.class}, "exists@northernarc.org");

		assertEquals(Boolean.TRUE, result);
	}

	@Test
	@DisplayName("CustomerRepository: duplicate email constraint")
	void duplicateEmail_shouldFailOnFlush() {
		customerRepository.save(createCustomer("Alex", "dup@northernarc.org", "9876543210"));
		entityManager.flush();

		customerRepository.save(createCustomer("Sam", "dup@northernarc.org", "9876543211"));

		assertThrows(RuntimeException.class, () -> entityManager.flush());
	}

	@Test
	@DisplayName("CustomerRepository: null name")
	void nullName_shouldViolateConstraint() {
		customerRepository.save(createCustomer(null, "nullname@northernarc.org", "9876543210"));

		assertThrows(RuntimeException.class, () -> entityManager.flush());
	}

	@Test
	@DisplayName("CustomerRepository: invalid email")
	void invalidEmail_shouldViolateConstraint() {
		customerRepository.save(createCustomer("Alex", "invalid-email", "9876543210"));

		assertThrows(RuntimeException.class, () -> entityManager.flush());
	}

	@Test
	@DisplayName("CustomerRepository: invalid phone")
	void invalidPhone_shouldViolateConstraint() {
		customerRepository.save(createCustomer("Alex", "valid@northernarc.org", "98AB543210"));

		assertThrows(RuntimeException.class, () -> entityManager.flush());
	}

	@Test
	@DisplayName("CustomerRepository: transaction rollback")
	void rollback_shouldDiscardUncommittedCustomer() {
		Customer saved = customerRepository.save(createCustomer("Rollback", "rollback@northernarc.org", "9876543210"));
		entityManager.flush();
		Long id = (Long) getEntityId(saved);

		TestTransaction.flagForRollback();
		TestTransaction.end();
		TestTransaction.start();

		assertTrue(customerRepository.findById(id).isEmpty(), "Customer should not exist after rollback");
	}

	@Test
	@DisplayName("CustomerRepository: entity relationships")
	void entityRelationships_shouldDefineCustomerToAccounts() {
		Field relationField = getFieldByName(Customer.class, "accounts");
		assertNotNull(relationField, "Customer.accounts relationship field is required");
		assertHasAnnotation(relationField, OneToMany.class);
	}

	private Customer createCustomer(String name, String email, String phone) {
		Customer customer = new Customer();
		setField(customer, "name", name);
		setField(customer, "email", email);
		setField(customer, "phone", phone);
		setField(customer, "password", "encodedPassword");
		return customer;
	}

	private Object invokeRepositoryMethod(String methodName, Class<?>[] parameterTypes, Object... args) {
		try {
			Method method = customerRepository.getClass().getMethod(methodName, parameterTypes);
			return method.invoke(customerRepository, args);
		} catch (NoSuchMethodException exception) {
			fail("Missing repository method: CustomerRepository." + methodName);
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
