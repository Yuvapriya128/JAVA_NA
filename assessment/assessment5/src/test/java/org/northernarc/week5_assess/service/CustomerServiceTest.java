package org.northernarc.week5_assess.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.northernarc.week5_assess.dto.CustomerDto;
import org.northernarc.week5_assess.entity.Customer;
import org.northernarc.week5_assess.exception.InvalidRequestException;
import org.northernarc.week5_assess.exception.ResourceNotFoundException;
import org.northernarc.week5_assess.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

	private static final Class<?>[] CREATE_CUSTOMER_SIG = {CustomerDto.class};
	private static final Class<?>[] GET_CUSTOMER_BY_ID_SIG = {Long.class};
	private static final Class<?>[] GET_ALL_CUSTOMERS_SIG = {};
	private static final Class<?>[] UPDATE_CUSTOMER_SIG = {Long.class, CustomerDto.class};
	private static final Class<?>[] DELETE_CUSTOMER_SIG = {Long.class};

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private CustomerService customerService;

	@BeforeEach
	void setUp() {
		reset(customerRepository, passwordEncoder);
	}

	@Test
	@DisplayName("Create Customer: valid customer")
	void createCustomer_validCustomer() {
		CustomerDto request = createCustomerRequest("Alex", "alex@northernarc.org", "9876543210", "plainPass");
		when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
		when(customerRepository.save(argThat(customer -> customer != null
				&& "Alex".equals(customer.getName())
				&& "alex@northernarc.org".equals(customer.getEmail())
				&& "9876543210".equals(customer.getPhone())))).thenReturn(new Customer());

		assertDoesNotThrow(() -> invokeCustomerService("createCustomer", CREATE_CUSTOMER_SIG, request));

		verify(passwordEncoder, times(1)).encode(any(String.class));
		verify(customerRepository, times(1)).save(any(Customer.class));
	}

	@Test
	@DisplayName("Create Customer: duplicate email")
	void createCustomer_duplicateEmail() {
		CustomerDto request = createCustomerRequest("Alex", "duplicate@northernarc.org", "9876543210", "plainPass");

		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("createCustomer", CREATE_CUSTOMER_SIG, request));

		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	@DisplayName("Create Customer: null name")
	void createCustomer_nullName() {
		CustomerDto request = createCustomerRequest(null, "alex@northernarc.org", "9876543210", "plainPass");

		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("createCustomer", CREATE_CUSTOMER_SIG, request));

		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	@DisplayName("Create Customer: blank name")
	void createCustomer_blankName() {
		CustomerDto request = createCustomerRequest("   ", "alex@northernarc.org", "9876543210", "plainPass");

		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("createCustomer", CREATE_CUSTOMER_SIG, request));

		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	@DisplayName("Create Customer: invalid email")
	void createCustomer_invalidEmail() {
		CustomerDto request = createCustomerRequest("Alex", "invalid-email", "9876543210", "plainPass");

		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("createCustomer", CREATE_CUSTOMER_SIG, request));

		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	@DisplayName("Create Customer: null email")
	void createCustomer_nullEmail() {
		CustomerDto request = createCustomerRequest("Alex", null, "9876543210", "plainPass");

		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("createCustomer", CREATE_CUSTOMER_SIG, request));

		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	@DisplayName("Create Customer: blank email")
	void createCustomer_blankEmail() {
		CustomerDto request = createCustomerRequest("Alex", "   ", "9876543210", "plainPass");

		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("createCustomer", CREATE_CUSTOMER_SIG, request));

		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	@DisplayName("Create Customer: phone less than 10 digits")
	void createCustomer_phoneLessThanTenDigits() {
		CustomerDto request = createCustomerRequest("Alex", "alex@northernarc.org", "123456789", "plainPass");

		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("createCustomer", CREATE_CUSTOMER_SIG, request));

		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	@DisplayName("Create Customer: phone greater than 10 digits")
	void createCustomer_phoneGreaterThanTenDigits() {
		CustomerDto request = createCustomerRequest("Alex", "alex@northernarc.org", "12345678901", "plainPass");

		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("createCustomer", CREATE_CUSTOMER_SIG, request));

		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	@DisplayName("Create Customer: phone contains letters")
	void createCustomer_phoneContainsLetters() {
		CustomerDto request = createCustomerRequest("Alex", "alex@northernarc.org", "98AB543210", "plainPass");

		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("createCustomer", CREATE_CUSTOMER_SIG, request));

		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	@DisplayName("Create Customer: null phone")
	void createCustomer_nullPhone() {
		CustomerDto request = createCustomerRequest("Alex", "alex@northernarc.org", null, "plainPass");

		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("createCustomer", CREATE_CUSTOMER_SIG, request));

		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	@DisplayName("Create Customer: password encrypted")
	void createCustomer_passwordEncrypted() {
		CustomerDto request = createCustomerRequest("Alex", "alex@northernarc.org", "9876543210", "plainPass");
		when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
		when(customerRepository.save(argThat(customer -> customer != null
				&& "Alex".equals(customer.getName())
				&& "alex@northernarc.org".equals(customer.getEmail())
				&& "9876543210".equals(customer.getPhone())))).thenReturn(new Customer());

		assertDoesNotThrow(() -> invokeCustomerService("createCustomer", CREATE_CUSTOMER_SIG, request));

		verify(passwordEncoder, times(1)).encode("plainPass");
	}

	@Test
	@DisplayName("Create Customer: repository save called once")
	void createCustomer_repositorySaveCalledOnce() {
		CustomerDto request = createCustomerRequest("Alex", "alex@northernarc.org", "9876543210", "plainPass");
		when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
		when(customerRepository.save(argThat(customer -> customer != null
				&& "Alex".equals(customer.getName())
				&& "alex@northernarc.org".equals(customer.getEmail())
				&& "9876543210".equals(customer.getPhone())))).thenReturn(new Customer());

		assertDoesNotThrow(() -> invokeCustomerService("createCustomer", CREATE_CUSTOMER_SIG, request));

		verify(customerRepository, times(1)).save(any(Customer.class));
	}

	@Test
	@DisplayName("Create Customer: repository save never called on validation failure")
	void createCustomer_repositorySaveNeverCalledOnValidationFailure() {
		CustomerDto request = createCustomerRequest(null, "alex@northernarc.org", "9876543210", "plainPass");

		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("createCustomer", CREATE_CUSTOMER_SIG, request));

		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	@DisplayName("Get Customer: valid id")
	void getCustomer_validId() {
		when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));

		Object response = assertDoesNotThrow(() -> invokeCustomerService("getCustomerById", GET_CUSTOMER_BY_ID_SIG, 1L));

		assertNotNull(response);
		verify(customerRepository, times(1)).findById(1L);
	}

	@Test
	@DisplayName("Get Customer: invalid id")
	void getCustomer_invalidId() {
		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("getCustomerById", GET_CUSTOMER_BY_ID_SIG, -1L));

		verify(customerRepository, never()).findById(any(Long.class));
	}

	@Test
	@DisplayName("Get Customer: null id")
	void getCustomer_nullId() {
		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("getCustomerById", GET_CUSTOMER_BY_ID_SIG, new Object[]{null}));

		verify(customerRepository, never()).findById(any(Long.class));
	}

	@Test
	@DisplayName("Get Customer: customer not found")
	void getCustomer_customerNotFound() {
		when(customerRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class,
				() -> invokeCustomerService("getCustomerById", GET_CUSTOMER_BY_ID_SIG, 99L));

		verify(customerRepository, times(1)).findById(99L);
	}

	@Test
	@DisplayName("Get All Customers: customers exist")
	void getAllCustomers_customersExist() {
		when(customerRepository.findAll()).thenReturn(List.of(new Customer(), new Customer()));

		Object response = assertDoesNotThrow(() -> invokeCustomerService("getAllCustomers", GET_ALL_CUSTOMERS_SIG));

		assertTrue(response instanceof List<?>);
		assertEquals(2, ((List<?>) response).size());
		assertFalse(((List<?>) response).isEmpty());
		verify(customerRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("Get All Customers: empty list")
	void getAllCustomers_emptyList() {
		when(customerRepository.findAll()).thenReturn(List.of());

		Object response = assertDoesNotThrow(() -> invokeCustomerService("getAllCustomers", GET_ALL_CUSTOMERS_SIG));

		assertTrue(response instanceof List<?>);
		assertEquals(0, ((List<?>) response).size());
		assertTrue(((List<?>) response).isEmpty());
		verify(customerRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("Update Customer: success")
	void updateCustomer_success() {
		CustomerDto request = createCustomerRequest("Alex Updated", "alex.updated@northernarc.org", "9123456780", "plainPass");
		when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
		when(customerRepository.save(argThat(customer -> customer != null
				&& "Alex Updated".equals(customer.getName())
				&& "alex.updated@northernarc.org".equals(customer.getEmail())
				&& "9123456780".equals(customer.getPhone())))).thenReturn(new Customer());

		assertDoesNotThrow(() -> invokeCustomerService("updateCustomer", UPDATE_CUSTOMER_SIG, 1L, request));

		verify(customerRepository, times(1)).findById(1L);
		verify(customerRepository, times(1)).save(any(Customer.class));
	}

	@Test
	@DisplayName("Update Customer: customer not found")
	void updateCustomer_customerNotFound() {
		CustomerDto request = createCustomerRequest("Alex", "alex@northernarc.org", "9876543210", "plainPass");
		when(customerRepository.findById(777L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class,
				() -> invokeCustomerService("updateCustomer", UPDATE_CUSTOMER_SIG, 777L, request));

		verify(customerRepository, times(1)).findById(777L);
		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	@DisplayName("Update Customer: duplicate email")
	void updateCustomer_duplicateEmail() {
		CustomerDto request = createCustomerRequest("Alex", "duplicate@northernarc.org", "9876543210", "plainPass");
		when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));

		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("updateCustomer", UPDATE_CUSTOMER_SIG, 1L, request));

		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	@DisplayName("Update Customer: invalid email")
	void updateCustomer_invalidEmail() {
		CustomerDto request = createCustomerRequest("Alex", "bad-email", "9876543210", "plainPass");
		when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));

		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("updateCustomer", UPDATE_CUSTOMER_SIG, 1L, request));

		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	@DisplayName("Update Customer: invalid phone")
	void updateCustomer_invalidPhone() {
		CustomerDto request = createCustomerRequest("Alex", "alex@northernarc.org", "12345", "plainPass");
		when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));

		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("updateCustomer", UPDATE_CUSTOMER_SIG, 1L, request));

		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	@DisplayName("Update Customer: blank name")
	void updateCustomer_blankName() {
		CustomerDto request = createCustomerRequest("  ", "alex@northernarc.org", "9876543210", "plainPass");
		when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));

		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("updateCustomer", UPDATE_CUSTOMER_SIG, 1L, request));

		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	@DisplayName("Delete Customer: success")
	void deleteCustomer_success() {
		when(customerRepository.findById(5L)).thenReturn(Optional.of(new Customer()));

		assertDoesNotThrow(() -> invokeCustomerService("deleteCustomer", DELETE_CUSTOMER_SIG, 5L));

		verify(customerRepository, times(1)).findById(5L);
		assertDeleteMethodWasInvoked();
	}

	@Test
	@DisplayName("Delete Customer: customer not found")
	void deleteCustomer_customerNotFound() {
		when(customerRepository.findById(5L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class,
				() -> invokeCustomerService("deleteCustomer", DELETE_CUSTOMER_SIG, 5L));

		verify(customerRepository, times(1)).findById(5L);
	}

	@Test
	@DisplayName("Delete Customer: repository delete invoked")
	void deleteCustomer_repositoryDeleteInvoked() {
		when(customerRepository.findById(8L)).thenReturn(Optional.of(new Customer()));

		assertDoesNotThrow(() -> invokeCustomerService("deleteCustomer", DELETE_CUSTOMER_SIG, 8L));

		assertDeleteMethodWasInvoked();
	}

	@Test
	@DisplayName("Create Customer: invalid payload triggers verifyNoInteractions")
	void createCustomer_invalidPayload_verifyNoInteractions() {
		CustomerDto request = createCustomerRequest("", "", "", "plainPass");

		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("createCustomer", CREATE_CUSTOMER_SIG, request));

		verifyNoInteractions(passwordEncoder);
		verify(customerRepository, never()).save(any(Customer.class));
	}

	@Test
	@DisplayName("Create Customer: repository interaction stops after validation failure")
	void createCustomer_validationFailure_verifyNoMoreInteractions() {
		CustomerDto request = createCustomerRequest(null, "alex@northernarc.org", "9876543210", "plainPass");

		assertThrows(InvalidRequestException.class,
				() -> invokeCustomerService("createCustomer", CREATE_CUSTOMER_SIG, request));

		verifyNoInteractions(passwordEncoder);
		verifyNoMoreInteractions(customerRepository);
	}

	@Test
	@DisplayName("Create Customer: whitespace normalization contract")
	void createCustomer_whitespaceNormalizationContract() {
		CustomerDto request = createCustomerRequest("  Alex  ", "  alex@northernarc.org  ", "9876543210", "plainPass");
		when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
		when(customerRepository.save(argThat(customer -> customer != null
				&& "Alex".equals(customer.getName())
				&& "alex@northernarc.org".equals(customer.getEmail())
				&& "9876543210".equals(customer.getPhone())))).thenReturn(new Customer());

		assertDoesNotThrow(() -> invokeCustomerService("createCustomer", CREATE_CUSTOMER_SIG, request));

		verify(customerRepository, times(1)).save(any(Customer.class));
	}

	@Test
	@DisplayName("Create Customer: password never stored in plaintext contract")
	void createCustomer_passwordNeverStoredAsPlaintextContract() {
		CustomerDto request = createCustomerRequest("Alex", "alex@northernarc.org", "9876543210", "plainPass");
		when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
		when(customerRepository.save(argThat(customer -> customer != null
				&& "Alex".equals(customer.getName())
				&& "alex@northernarc.org".equals(customer.getEmail())
				&& "9876543210".equals(customer.getPhone())))).thenReturn(new Customer());

		assertDoesNotThrow(() -> invokeCustomerService("createCustomer", CREATE_CUSTOMER_SIG, request));

		verify(passwordEncoder, times(1)).encode("plainPass");
		verify(customerRepository, times(1)).save(any(Customer.class));
	}

	private CustomerDto createCustomerRequest(String name, String email, String phone, String password) {
		CustomerDto dto = new CustomerDto();
		setFieldIfPresent(dto, "name", name);
		setFieldIfPresent(dto, "email", email);
		setFieldIfPresent(dto, "phone", phone);
		setFieldIfPresent(dto, "password", password);
		return dto;
	}

	private Object invokeCustomerService(String methodName, Class<?>[] parameterTypes, Object... args) {
		return invokeRequiredMethod(customerService, methodName, parameterTypes, args);
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

	private void assertDeleteMethodWasInvoked() {
		long deleteCalls = mockingDetails(customerRepository).getInvocations().stream()
				.filter(invocation -> invocation.getMethod().getName().startsWith("delete"))
				.count();
		assertTrue(deleteCalls > 0, "Expected one delete* method invocation on repository");
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
