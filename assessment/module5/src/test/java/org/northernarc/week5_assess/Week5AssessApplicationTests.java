package org.northernarc.week5_assess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.northernarc.week5_assess.controller.AccountController;
import org.northernarc.week5_assess.controller.AuthController;
import org.northernarc.week5_assess.controller.CustomerController;
import org.northernarc.week5_assess.controller.TransactionController;
import org.northernarc.week5_assess.repository.AccountRepository;
import org.northernarc.week5_assess.repository.CustomerRepository;
import org.northernarc.week5_assess.repository.TransactionRepository;
import org.northernarc.week5_assess.service.AccountService;
import org.northernarc.week5_assess.service.AuthService;
import org.northernarc.week5_assess.service.CustomerService;
import org.northernarc.week5_assess.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Week5AssessApplicationTests")
class Week5AssessApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AuthController authController;

    @Autowired
    private CustomerController customerController;

    @Autowired
    private AccountController accountController;

    @Autowired
    private TransactionController transactionController;

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        // Lifecycle hook kept explicit for consistent suite structure.
    }

    @Test
    @DisplayName("Application: Spring context loads successfully")
    void contextLoads() {
        // Assert context is loaded
        assertNotNull(applicationContext);
    }

    @Test
    @DisplayName("Application: Application starts successfully")
    void applicationStartsSuccessfully() {
        // Assert application context exists and is running
        assertNotNull(applicationContext);
        assertTrue(((ConfigurableApplicationContext) applicationContext).isActive());
    }

    @Test
    @DisplayName("Application: All controllers are instantiated")
    void allControllersInstantiated() {
        assertNotNull(authController);
        assertNotNull(customerController);
        assertNotNull(accountController);
        assertNotNull(transactionController);
    }

    @Test
    @DisplayName("Application: All services are instantiated")
    void allServicesInstantiated() {
        assertNotNull(authService);
        assertNotNull(customerService);
        assertNotNull(accountService);
        assertNotNull(transactionService);
    }

    @Test
    @DisplayName("Application: All repositories are instantiated")
    void allRepositoriesInstantiated() {
        assertNotNull(customerRepository);
        assertNotNull(accountRepository);
        assertNotNull(transactionRepository);
    }
}
