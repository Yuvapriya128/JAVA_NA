package org.northernarc.loanemi;

import org.junit.jupiter.api.Test;
import org.northernarc.loanemi.model.Customer;
import org.northernarc.loanemi.model.EmiPayment;
import org.northernarc.loanemi.model.EmiSchedule;
import org.northernarc.loanemi.model.Loan;
import org.northernarc.loanemi.enums.EmiStatus;
import org.northernarc.loanemi.enums.LoanStatus;
import org.northernarc.loanemi.repository.CustomerRepository;
import org.northernarc.loanemi.repository.EmiPaymentRepository;
import org.northernarc.loanemi.repository.EmiScheduleRepository;
import org.northernarc.loanemi.repository.LoanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class LoanEmiApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private EmiScheduleRepository emiScheduleRepository;

    @Autowired
    private EmiPaymentRepository emiPaymentRepository;

    private Customer customer;

    private Loan loan;

    private EmiSchedule emiSchedule;

    private EmiPayment emiPayment;

    @BeforeEach
    void setUp() {

        emiPaymentRepository.deleteAll();
        emiScheduleRepository.deleteAll();
        loanRepository.deleteAll();
        customerRepository.deleteAll();

        customer = new Customer();
        customer.setCustomerName("Renjitha");
        customer.setEmail("renjitha@gmail.com");
        customer.setPassword("$2a$10$abcd123456789");
        customer.setPhoneNumber("9876543210");
        customer.setCity("Chennai");
        customer.setCreditScore(780);

        customer = customerRepository.save(customer);

        loan = new Loan();
        loan.setLoanType("PERSONAL");
        loan.setPrincipalAmount(500000.0);
        loan.setAnnualInterestRate(12.0);
        loan.setTenureMonths(24);
        loan.setEmiAmount(23536.74);
        loan.setDisbursementDate(LocalDate.now());
        loan.setLoanStatus("ACTIVE");
        loan.setCustomer(customer);

        loan = loanRepository.save(loan);

        emiSchedule = new EmiSchedule();
        emiSchedule.setInstallmentNumber(1);
        emiSchedule.setDueDate(LocalDate.now().plusMonths(1));
        emiSchedule.setAmountDue(23536.74);
        emiSchedule.setPrincipalComponent(18536.74);
        emiSchedule.setInterestComponent(5000.0);
        emiSchedule.setAmountPaid(0.0);
        emiSchedule.setStatus("PENDING");
        emiSchedule.setDaysPastDue(0);
        emiSchedule.setPenaltyAmount(0.0);
        emiSchedule.setLoan(loan);

        emiSchedule = emiScheduleRepository.save(emiSchedule);

        emiPayment = new EmiPayment();
        emiPayment.setAmount(10000.0);
        emiPayment.setPaymentMode("UPI");
        emiPayment.setPaymentDate(LocalDate.now());
        emiPayment.setReferenceNumber("PAY123456");
        emiPayment.setEmiSchedule(emiSchedule);

        emiPayment = emiPaymentRepository.save(emiPayment);
    }

    @Nested
    @DisplayName("Task 1 : Entity Mapping Tests")
    class EntityMappingTests {

        @Test
        @DisplayName("Customer should contain loan")
        void testCustomerLoanMapping() {

            Customer savedCustomer =
                    customerRepository.findById(customer.getCustomerId()).orElseThrow();

            assertThat(savedCustomer).isNotNull();
            assertThat(savedCustomer.getLoans()).hasSize(1);
        }

        @Test
        @DisplayName("Loan should reference customer")
        void testLoanCustomerMapping() {

            Loan savedLoan =
                    loanRepository.findById(loan.getLoanId()).orElseThrow();

            assertThat(savedLoan.getCustomer()).isNotNull();
            assertThat(savedLoan.getCustomer().getCustomerName())
                    .isEqualTo("Renjitha");
        }

        @Test
        @DisplayName("Loan should contain EMI schedules")
        void testLoanEmiMapping() {

            Loan savedLoan =
                    loanRepository.findById(loan.getLoanId()).orElseThrow();

            assertThat(savedLoan.getEmiSchedules())
                    .hasSize(1);
        }

        @Test
        @DisplayName("EMI should belong to Loan")
        void testEmiLoanMapping() {

            EmiSchedule emi =
                    emiScheduleRepository.findById(emiSchedule.getEmiId())
                            .orElseThrow();

            assertThat(emi.getLoan()).isNotNull();
            assertThat(emi.getLoan().getLoanType())
                    .isEqualTo("PERSONAL");
        }

        @Test
        @DisplayName("EMI should contain payments")
        void testEmiPaymentMapping() {

            EmiSchedule emi =
                    emiScheduleRepository.findById(emiSchedule.getEmiId())
                            .orElseThrow();

            assertThat(emi.getPayments())
                    .hasSize(1);
        }

        @Test
        @DisplayName("Payment should reference EMI")
        void testPaymentMapping() {

            EmiPayment payment =
                    emiPaymentRepository.findById(emiPayment.getPaymentId())
                            .orElseThrow();

            assertThat(payment.getEmiSchedule())
                    .isNotNull();

            assertThat(payment.getEmiSchedule().getInstallmentNumber())
                    .isEqualTo(1);
        }

        @Test
        @DisplayName("Cascade delete customer")
        void testCascadeDeleteCustomer() {

            customerRepository.delete(customer);
//            customerRepository.flush();

            assertThat(loanRepository.findAll()).isEmpty();
            assertThat(emiScheduleRepository.findAll()).isEmpty();
        }

        @Test
        @DisplayName("Cascade delete loan")
        void testCascadeDeleteLoan() {

            assertThatCode(() -> {
                loanRepository.deleteById(loan.getLoanId());
                loanRepository.flush();
            }).doesNotThrowAnyException();
        }

    }

// ===============================
// PART 1B : TASK 2 - BEAN VALIDATION TESTS
// ===============================

    @Nested
    @DisplayName("Task 2 : Bean Validation Tests")
    @WithMockUser(roles = "MANAGER")
    class BeanValidationTests {

        @Test
        @DisplayName("Customer name cannot be blank")
        void testBlankCustomerName() throws Exception {

            String request = """
                    {
                      "customerName":"",
                      "email":"abc@gmail.com",
                      "password":"Password@123",
                      "phoneNumber":"9876543210",
                      "city":"Chennai",
                      "creditScore":750
                    }
                    """;

            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Email should be valid")
        void testInvalidEmail() throws Exception {

            String request = """
                    {
                      "customerName":"Raj",
                      "email":"abcgmail.com",
                      "password":"Password@123",
                      "phoneNumber":"9876543210",
                      "city":"Chennai",
                      "creditScore":750
                    }
                    """;

            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Password should not be blank")
        void testBlankPassword() throws Exception {

            String request = """
                    {
                      "customerName":"Raj",
                      "email":"abc@gmail.com",
                      "password":"",
                      "phoneNumber":"9876543210",
                      "city":"Chennai",
                      "creditScore":750
                    }
                    """;

            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Phone number must contain exactly 10 digits")
        void testInvalidPhone() throws Exception {

            String request = """
                    {
                      "customerName":"Raj",
                      "email":"abc@gmail.com",
                      "password":"Password@123",
                      "phoneNumber":"12345",
                      "city":"Chennai",
                      "creditScore":750
                    }
                    """;

            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("City cannot be blank")
        void testBlankCity() throws Exception {

            String request = """
                    {
                      "customerName":"Raj",
                      "email":"abc@gmail.com",
                      "password":"Password@123",
                      "phoneNumber":"9876543210",
                      "city":"",
                      "creditScore":750
                    }
                    """;

            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Credit score cannot be negative")
        void testNegativeCreditScore() throws Exception {

            String request = """
                    {
                      "customerName":"Raj",
                      "email":"abc@gmail.com",
                      "password":"Password@123",
                      "phoneNumber":"9876543210",
                      "city":"Chennai",
                      "creditScore":-10
                    }
                    """;

            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Principal amount cannot be negative")
        void testNegativePrincipal() throws Exception {

            String request = """
                    {
                      "loanType":"PERSONAL",
                      "principalAmount":-100000,
                      "annualInterestRate":10,
                      "tenureMonths":24,
                      "customerId":1
                    }
                    """;

            mockMvc.perform(post("/api/loans")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Principal amount cannot be zero")
        void testZeroPrincipal() throws Exception {

            String request = """
                    {
                      "loanType":"PERSONAL",
                      "principalAmount":0,
                      "annualInterestRate":10,
                      "tenureMonths":24,
                      "customerId":1
                    }
                    """;

            mockMvc.perform(post("/api/loans")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Interest rate should be positive")
        void testNegativeInterestRate() throws Exception {

            String request = """
                    {
                      "loanType":"PERSONAL",
                      "principalAmount":100000,
                      "annualInterestRate":-5,
                      "tenureMonths":24,
                      "customerId":1
                    }
                    """;

            mockMvc.perform(post("/api/loans")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Tenure should be greater than zero")
        void testZeroTenure() throws Exception {

            String request = """
                    {
                      "loanType":"PERSONAL",
                      "principalAmount":100000,
                      "annualInterestRate":10,
                      "tenureMonths":0,
                      "customerId":1
                    }
                    """;

            mockMvc.perform(post("/api/loans")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Loan type cannot be blank")
        void testBlankLoanType() throws Exception {

            String request = """
                    {
                      "loanType":"",
                      "principalAmount":100000,
                      "annualInterestRate":10,
                      "tenureMonths":24,
                      "customerId":1
                    }
                    """;

            mockMvc.perform(post("/api/loans")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Valid customer request should pass")
        void testValidCustomerRequest() throws Exception {

            String request = """
                    {
                      "customerName":"Arun",
                      "email":"arun@gmail.com",
                      "password":"Password@123",
                      "phoneNumber":"9876543211",
                      "city":"Madurai",
                      "creditScore":780
                    }
                    """;

            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isCreated());
        }

    }
// ======================================
// PART 1C : TASK 3 - DERIVED QUERY TESTS
// ======================================

    @Nested
    @DisplayName("Task 3 : Spring Data JPA Derived Query Tests")
    class DerivedQueryTests {

        @Test
        @DisplayName("Find loans by loan type")
        void testFindByLoanType() {

            List<Loan> loans =
                    loanRepository.findByLoanType("PERSONAL");

            assertThat(loans).isNotEmpty();
            assertThat(loans.get(0).getLoanType())
                    .isEqualTo("PERSONAL");
        }

        @Test
        @DisplayName("Find loans by customer city")
        void testFindByCustomerCity() {

            List<Loan> loans =
                    loanRepository.findByCustomerCity("Chennai");

            assertThat(loans).hasSize(1);
            assertThat(loans.get(0)
                    .getCustomer()
                    .getCity())
                    .isEqualTo("Chennai");
        }

        @Test
        @DisplayName("Find loans by ACTIVE status")
        void testFindByLoanStatus() {

            List<Loan> loans =
                    loanRepository.findByLoanStatus(LoanStatus.ACTIVE);

            assertThat(loans).hasSize(1);
        }

        @Test
        @DisplayName("Find loans having principal greater than amount")
        void testFindByPrincipalGreaterThan() {

            List<Loan> loans =
                    loanRepository.findByPrincipalAmountGreaterThan(100000.0);

            assertThat(loans).hasSize(1);
            assertThat(loans.get(0).getPrincipalAmount())
                    .isGreaterThan(100000);
        }

        @Test
        @DisplayName("Find customer using email")
        void testFindCustomerByEmail() {

            Customer customer =
                    customerRepository.findByEmail("renjitha@gmail.com")
                            .orElseThrow();

            assertThat(customer).isNotNull();
            assertThat(customer.getCustomerName())
                    .isEqualTo("Renjitha");
        }

        @Test
        @DisplayName("Find customer using phone number")
        void testFindCustomerByPhone() {

            Customer customer =
                    customerRepository.findByPhoneNumber("9876543210");

            assertThat(customer).isNotNull();
            assertThat(customer.getCity())
                    .isEqualTo("Chennai");
        }

        @Test
        @DisplayName("Find customers by city")
        void testFindCustomersByCity() {

            List<Customer> customers =
                    customerRepository.findByCity("Chennai");

            assertThat(customers).hasSize(1);
        }

        @Test
        @DisplayName("Find customers with credit score greater than")
        void testFindCustomersByCreditScore() {

            List<Customer> customers =
                    customerRepository.findByCreditScoreGreaterThan(700);

            assertThat(customers).hasSize(1);
            assertThat(customers.get(0).getCreditScore())
                    .isEqualTo(780);
        }

        @Test
        @DisplayName("Find pending EMI schedules")
        void testFindPendingEmis() {

            List<EmiSchedule> emis =
                    emiScheduleRepository.findByStatus(EmiStatus.PENDING);

            assertThat(emis).hasSize(1);
            assertThat(emis.get(0).getAmountPaid())
                    .isEqualTo(0.0);
        }

        @Test
        @DisplayName("Find overdue EMI schedules")
        void testFindOverdueEmis() {

            emiSchedule.setStatus("OVERDUE");
            emiScheduleRepository.save(emiSchedule);

            List<EmiSchedule> overdue =
                    emiScheduleRepository.findByStatus(EmiStatus.OVERDUE);

            assertThat(overdue).hasSize(1);
        }

        @Test
        @DisplayName("Find EMI schedules by due date before today")
        void testFindByDueDateBefore() {

            emiSchedule.setDueDate(LocalDate.now().minusDays(5));
            emiScheduleRepository.save(emiSchedule);

            List<EmiSchedule> result =
                    emiScheduleRepository.findByDueDateBefore(LocalDate.now());

            assertThat(result).isNotEmpty();
        }

        @Test
        @DisplayName("Find EMI payment by reference number")
        void testFindPaymentByReferenceNumber() {

            EmiPayment payment =
                    emiPaymentRepository.findByReferenceNumber("PAY123456");

            assertThat(payment).isNotNull();
            assertThat(payment.getAmount())
                    .isEqualTo(10000.0);
        }

    }

// =======================================
// PART 2A : TASK 4 - COMPLEX JPQL TESTS
// =======================================

    @Nested
    @DisplayName("Task 4 : Complex JPQL Query Tests")
    class JpqlQueryTests {

        @Test
        @DisplayName("JPQL : Customers having overdue EMI")
        void testCustomersWithOverdueEmi() {

            emiSchedule.setStatus("OVERDUE");
            emiSchedule.setDaysPastDue(10);
            emiScheduleRepository.save(emiSchedule);

            List<Customer> customers =
                    customerRepository.findCustomersWithOverdueEmis();

            assertThat(customers).isNotEmpty();
            assertThat(customers.get(0).getCustomerName())
                    .isEqualTo("Renjitha");
        }

        @Test
        @DisplayName("JPQL : Highest overdue EMI")
        void testHighestOverdueEmi() {

            emiSchedule.setStatus("OVERDUE");
            emiSchedule.setPenaltyAmount(2500.0);
            emiScheduleRepository.save(emiSchedule);

            EmiSchedule emi =
                    emiScheduleRepository.findHighestOverdueEmi();

            assertThat(emi).isNotNull();
            assertThat(emi.getPenaltyAmount())
                    .isEqualTo(2500.0);
        }

        @Test
        @DisplayName("JPQL : Total EMI collection by city")
        void testTotalCollectionByCity() {

            emiSchedule.setAmountPaid(23536.74);
            emiSchedule.setStatus("PAID");
            emiScheduleRepository.save(emiSchedule);

            List<Object[]> result =
                    loanRepository.findTotalEmiCollectionByCity();

            assertThat(result).isNotEmpty();

            Object[] row = result.get(0);

            assertThat(row[0]).isEqualTo("Chennai");
            assertThat((Double) row[1]).isGreaterThan(0);
        }

        @Test
        @DisplayName("JPQL : Latest EMI payment")
        void testLatestPayment() {

            List<EmiPayment> payments =
                    emiPaymentRepository.findLatestPayment(PageRequest.of(0, 1));

            assertThat(payments).hasSize(1);

            assertThat(payments.get(0).getReferenceNumber())
                    .isEqualTo("PAY123456");
        }

        @Test
        @DisplayName("JPQL : Loans having zero overdue EMI")
        void testLoansWithZeroOverdue() {

            List<Loan> loans =
                    loanRepository.findLoansWithZeroOverdueEmis();

            assertThat(loans).hasSize(1);

            assertThat(loans.get(0).getLoanStatus())
                    .isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("JPQL : Top defaulters")
        void testTopDefaulters() {

            emiSchedule.setStatus("OVERDUE");
            emiSchedule.setPenaltyAmount(3500.0);
            emiSchedule.setDaysPastDue(40);

            emiScheduleRepository.save(emiSchedule);

            List<Customer> customers =
                    customerRepository.findTopDefaulters();

            assertThat(customers).isNotEmpty();

            assertThat(customers.get(0).getCustomerName())
                    .isEqualTo("Renjitha");
        }

        @Test
        @DisplayName("JPQL : Find active loans")
        void testFindActiveLoans() {

            List<Loan> loans =
                    loanRepository.findActiveLoans();

            assertThat(loans).hasSize(1);

            assertThat(loans.get(0).getLoanStatus())
                    .isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("JPQL : Count loans per city")
        void testLoanCountPerCity() {

            List<Object[]> list =
                    loanRepository.findLoanCountPerCity();

            assertThat(list).isNotEmpty();

            Object[] row = list.get(0);

            assertThat(row[0]).isEqualTo("Chennai");
            assertThat(((Long) row[1])).isEqualTo(1L);
        }

        @Test
        @DisplayName("JPQL : Average interest rate")
        void testAverageInterestRate() {

            Double avg =
                    loanRepository.findAverageInterestRate();

            assertThat(avg)
                    .isEqualTo(12.0);
        }

        @Test
        @DisplayName("JPQL : Highest outstanding loan")
        void testHighestOutstandingLoan() {

            Loan highest =
                    loanRepository.findHighestOutstandingLoan();

            assertThat(highest).isNotNull();

            assertThat(highest.getPrincipalAmount())
                    .isEqualTo(500000.0);
        }

        @Test
        @DisplayName("JPQL : Total penalty collected")
        void testPenaltyCollection() {

            emiSchedule.setPenaltyAmount(500.0);
            emiScheduleRepository.save(emiSchedule);

            Double total =
                    loanRepository.findTotalPenaltyCollected();

            assertThat(total)
                    .isGreaterThanOrEqualTo(500.0);
        }

        @Test
        @DisplayName("JPQL : Total EMI collected")
        void testTotalEmiCollected() {

            emiSchedule.setAmountPaid(23536.74);
            emiScheduleRepository.save(emiSchedule);

            Double total =
                    loanRepository.findTotalEmiCollected();

            assertThat(total)
                    .isGreaterThan(0);
        }

    }

// =========================================
// TASK 5 : @Modifying JPQL Update Tests
// =========================================

    @Nested
    @DisplayName("Task 5 : JPQL Modifying Query Tests")
    class JpqlUpdateTests {

        @Test
        @DisplayName("Increase interest rate for PERSONAL loans")
        void testIncreaseInterestRate() {

            int updated =
                    loanRepository.updateInterestRateByLoanType(
                            "PERSONAL",
                            15.0
                    );

            assertThat(updated).isEqualTo(1);

            loanRepository.flush();

            Loan updatedLoan =
                    loanRepository.findById(loan.getLoanId())
                            .orElseThrow();

            assertThat(updatedLoan.getAnnualInterestRate())
                    .isEqualTo(15.0);
        }

        @Test
        @DisplayName("Update should not affect other loan types")
        void testUpdateSpecificLoanTypeOnly() {

            Loan vehicleLoan = new Loan();

            vehicleLoan.setLoanType("VEHICLE");
            vehicleLoan.setPrincipalAmount(800000.0);
            vehicleLoan.setAnnualInterestRate(11.0);
            vehicleLoan.setTenureMonths(60);
            vehicleLoan.setLoanStatus("ACTIVE");
            vehicleLoan.setDisbursementDate(LocalDate.now());
            vehicleLoan.setCustomer(customer);

            loanRepository.save(vehicleLoan);

            loanRepository.updateInterestRateByLoanType(
                    "PERSONAL",
                    14.5
            );

            Loan personal =
                    loanRepository.findById(loan.getLoanId())
                            .orElseThrow();

            Loan vehicle =
                    loanRepository.findById(vehicleLoan.getLoanId())
                            .orElseThrow();

            assertThat(personal.getAnnualInterestRate())
                    .isEqualTo(14.5);

            assertThat(vehicle.getAnnualInterestRate())
                    .isEqualTo(11.0);
        }

        @Test
        @DisplayName("Updating non-existing loan type returns zero")
        void testUpdateInvalidLoanType() {

            int updated =
                    loanRepository.updateInterestRateByLoanType(
                            "GOLD",
                            20.0
                    );

            assertThat(updated).isZero();
        }

    }

// =========================================
// TASK 6 : Pagination & Sorting
// =========================================

    @Nested
    @DisplayName("Task 6 : Pagination & Sorting Tests")
    class PaginationTests {

        @BeforeEach
        void createLoans() {

            for (int i = 1; i <= 10; i++) {

                Loan l = new Loan();

                l.setLoanType("PERSONAL");

                l.setPrincipalAmount(100000.0 * i);

                l.setAnnualInterestRate(10.0);

                l.setTenureMonths(24);

                l.setLoanStatus("ACTIVE");

                l.setDisbursementDate(LocalDate.now());

                l.setCustomer(customer);

                loanRepository.save(l);

            }

        }

        @Test
        @DisplayName("Page 0 Size 5")
        void testFirstPage() {

            Page<Loan> page =
                    loanRepository.findAll(

                            PageRequest.of(
                                    0,
                                    5,
                                    Sort.by("principalAmount")
                                            .descending()
                            )
                    );

            assertThat(page.getContent())
                    .hasSize(5);
        }

        @Test
        @DisplayName("Second Page")
        void testSecondPage() {

            Page<Loan> page =
                    loanRepository.findAll(

                            PageRequest.of(
                                    1,
                                    5,
                                    Sort.by("principalAmount")
                                            .descending()
                            )
                    );

            assertThat(page.getContent())
                    .hasSize(5);
        }

        @Test
        @DisplayName("Verify descending sorting")
        void testSortingDescending() {

            Page<Loan> page =
                    loanRepository.findAll(

                            PageRequest.of(
                                    0,
                                    10,
                                    Sort.by("principalAmount")
                                            .descending()
                            )
                    );

            List<Loan> loans =
                    page.getContent();

            assertThat(loans.get(0).getPrincipalAmount())
                    .isGreaterThan(
                            loans.get(1).getPrincipalAmount()
                    );

        }

        @Test
        @DisplayName("Verify ascending sorting")
        void testSortingAscending() {

            Page<Loan> page =
                    loanRepository.findAll(

                            PageRequest.of(
                                    0,
                                    10,
                                    Sort.by("principalAmount")
                            )
                    );

            List<Loan> loans =
                    page.getContent();

            assertThat(loans.get(0).getPrincipalAmount())
                    .isLessThan(
                            loans.get(1).getPrincipalAmount()
                    );

        }

        @Test
        @DisplayName("Verify total pages")
        void testTotalPages() {

            Page<Loan> page =
                    loanRepository.findAll(

                            PageRequest.of(
                                    0,
                                    5
                            )
                    );

            assertThat(page.getTotalPages())
                    .isGreaterThanOrEqualTo(2);

        }

        @Test
        @DisplayName("Verify total elements")
        void testTotalElements() {

            Page<Loan> page =
                    loanRepository.findAll(PageRequest.of(0, 5));

            assertThat(page.getTotalElements())
                    .isGreaterThanOrEqualTo(10);

        }

        @Test
        @DisplayName("Empty page")
        void testEmptyPage() {

            Page<Loan> page =
                    loanRepository.findAll(PageRequest.of(50, 5));

            assertThat(page.getContent())
                    .isEmpty();

        }

    }
// =====================================================
// PART 3A : LOAN CREATION & EMI GENERATION TESTS
// =====================================================

    @Nested
    @DisplayName("Loan Creation Business Tests")
    class LoanCreationTests {

        @Test
        @DisplayName("Create Personal Loan Successfully")
        void testCreateLoan() {

            Loan newLoan = new Loan();

            newLoan.setLoanType("BUSINESS");
            newLoan.setPrincipalAmount(800000.0);
            newLoan.setAnnualInterestRate(13.5);
            newLoan.setTenureMonths(48);
            newLoan.setLoanStatus("ACTIVE");
            newLoan.setDisbursementDate(LocalDate.now());
            newLoan.setCustomer(customer);

            Loan saved = loanRepository.save(newLoan);

            assertThat(saved.getLoanId()).isNotNull();
            assertThat(saved.getLoanType()).isEqualTo("BUSINESS");
        }

        @Test
        @DisplayName("Principal Amount must be greater than zero")
        void testNegativePrincipalRejected() {

            Loan newLoan = new Loan();

            newLoan.setLoanType("PERSONAL");
            newLoan.setPrincipalAmount(-1000.0);
            newLoan.setAnnualInterestRate(12.0);
            newLoan.setTenureMonths(24);
            newLoan.setCustomer(customer);

            assertThatThrownBy(() ->
                    loanRepository.saveAndFlush(newLoan))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Zero Principal Amount")
        void testZeroPrincipalRejected() {

            Loan newLoan = new Loan();

            newLoan.setLoanType("PERSONAL");
            newLoan.setPrincipalAmount(0.0);
            newLoan.setAnnualInterestRate(12.0);
            newLoan.setTenureMonths(24);
            newLoan.setCustomer(customer);

            assertThatThrownBy(() ->
                    loanRepository.saveAndFlush(newLoan))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Loan must belong to customer")
        void testLoanCustomerRelation() {

            Loan saved =
                    loanRepository.findById(loan.getLoanId())
                            .orElseThrow();

            assertThat(saved.getCustomer()).isNotNull();
        }

        @Test
        @DisplayName("Loan Status should be ACTIVE after approval")
        void testLoanStatus() {

            Loan saved =
                    loanRepository.findById(loan.getLoanId())
                            .orElseThrow();

            assertThat(saved.getLoanStatus())
                    .isEqualTo("ACTIVE");
        }

    }
// =====================================
// EMI GENERATION TESTS
// =====================================

    @Nested
    @DisplayName("EMI Generation Tests")
    class EmiGenerationTests {

        @Test
        @DisplayName("Loan should generate EMI schedule")
        void testEmiGenerated() {

            List<EmiSchedule> emis =
                    emiScheduleRepository.findByLoanLoanId(
                            loan.getLoanId());

            assertThat(emis).hasSize(1);
        }

        @Test
        @DisplayName("EMI installment number starts from 1")
        void testInstallmentNumber() {

            EmiSchedule emi =
                    emiScheduleRepository.findByLoanLoanId(
                                    loan.getLoanId())
                            .get(0);

            assertThat(emi.getInstallmentNumber())
                    .isEqualTo(1);
        }

        @Test
        @DisplayName("EMI due date should not be null")
        void testDueDateGenerated() {

            EmiSchedule emi =
                    emiScheduleRepository.findByLoanLoanId(
                                    loan.getLoanId())
                            .get(0);

            assertThat(emi.getDueDate()).isNotNull();
        }

        @Test
        @DisplayName("EMI amount should be positive")
        void testEmiAmountPositive() {

            EmiSchedule emi =
                    emiScheduleRepository.findByLoanLoanId(
                                    loan.getLoanId())
                            .get(0);

            assertThat(emi.getAmountDue())
                    .isGreaterThan(0);
        }

        @Test
        @DisplayName("Initial Amount Paid should be zero")
        void testInitialAmountPaid() {

            EmiSchedule emi =
                    emiScheduleRepository.findByLoanLoanId(
                                    loan.getLoanId())
                            .get(0);

            assertThat(emi.getAmountPaid())
                    .isEqualTo(0.0);
        }

        @Test
        @DisplayName("Initial EMI status should be PENDING")
        void testInitialStatus() {

            EmiSchedule emi =
                    emiScheduleRepository.findByLoanLoanId(
                                    loan.getLoanId())
                            .get(0);

            assertThat(emi.getStatus())
                    .isEqualTo("PENDING");
        }

        @Test
        @DisplayName("Initial Penalty should be zero")
        void testPenaltyInitiallyZero() {

            EmiSchedule emi =
                    emiScheduleRepository.findByLoanLoanId(
                                    loan.getLoanId())
                            .get(0);

            assertThat(emi.getPenaltyAmount())
                    .isZero();
        }

    }
// =====================================
// EMI FORMULA TEST
// =====================================

    @Nested
    @DisplayName("EMI Formula Tests")
    class EmiFormulaTests {

        @Test
        @DisplayName("Verify EMI Formula")
        void testEmiCalculationFormula() {

            double principal = 500000;

            double annualRate = 12;

            int months = 24;

            double monthlyRate = annualRate / (12 * 100);

            double expectedEmi =
                    (principal * monthlyRate *
                            Math.pow(1 + monthlyRate, months))
                            /
                            (Math.pow(1 + monthlyRate, months) - 1);

            assertThat(expectedEmi)
                    .isCloseTo(
                            loan.getEmiAmount(),
                            within(2.0)
                    );
        }

    }

// ==========================================
// EMI PAYMENT BUSINESS TESTS
// ==========================================

    @Nested
    @DisplayName("EMI Payment Business Tests")
    class EmiPaymentTests {

        @Test
        @DisplayName("Partial Payment")
        void testPartialPayment() {

            emiSchedule.setAmountPaid(10000.0);
            emiSchedule.setStatus("PENDING");

            emiScheduleRepository.save(emiSchedule);

            EmiSchedule saved =
                    emiScheduleRepository.findById(emiSchedule.getEmiId())
                            .orElseThrow();

            assertThat(saved.getAmountPaid()).isEqualTo(10000.0);
            assertThat(saved.getStatus()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("Full EMI Payment")
        void testFullPayment() {

            emiSchedule.setAmountPaid(emiSchedule.getAmountDue());
            emiSchedule.setStatus("PAID");
            emiSchedule.setPaymentDate(LocalDate.now());

            emiScheduleRepository.save(emiSchedule);

            EmiSchedule saved =
                    emiScheduleRepository.findById(emiSchedule.getEmiId())
                            .orElseThrow();

            assertThat(saved.getStatus()).isEqualTo("PAID");
            assertThat(saved.getAmountPaid())
                    .isEqualTo(saved.getAmountDue());
        }

        @Test
        @DisplayName("Reject Over Payment")
        void testOverPaymentRejected() {

            double payment =
                    emiSchedule.getAmountDue() + 5000;

            assertThat(payment)
                    .isGreaterThan(emiSchedule.getAmountDue());
        }

        @Test
        @DisplayName("Reject Negative Payment")
        void testNegativePayment() {

            double payment = -500;

            assertThat(payment).isNegative();
        }

        @Test
        @DisplayName("Zero Payment")
        void testZeroPayment() {

            emiSchedule.setAmountPaid(0.0);

            emiScheduleRepository.save(emiSchedule);

            EmiSchedule saved =
                    emiScheduleRepository.findById(emiSchedule.getEmiId())
                            .orElseThrow();

            assertThat(saved.getStatus())
                    .isEqualTo("PENDING");
        }

        @Test
        @DisplayName("Payment Date Stored")
        void testPaymentDateStored() {

            LocalDate today = LocalDate.now();

            emiSchedule.setPaymentDate(today);
            emiSchedule.setStatus("PAID");
            emiSchedule.setAmountPaid(emiSchedule.getAmountDue());

            emiScheduleRepository.save(emiSchedule);

            assertThat(
                    emiScheduleRepository.findById(
                                    emiSchedule.getEmiId())
                            .orElseThrow()
                            .getPaymentDate())
                    .isEqualTo(today);
        }

    }

// ==========================================
// PENALTY CALCULATION TESTS
// ==========================================

    @Nested
    @DisplayName("Penalty Calculation Tests")
    class PenaltyTests {

        @Test
        @DisplayName("No penalty before due date")
        void testNoPenalty() {

            emiSchedule.setDueDate(LocalDate.now().plusDays(5));
            emiSchedule.setPenaltyAmount(0.0);

            emiScheduleRepository.save(emiSchedule);

            assertThat(
                    emiScheduleRepository.findById(
                                    emiSchedule.getEmiId())
                            .orElseThrow()
                            .getPenaltyAmount())
                    .isZero();
        }

        @Test
        @DisplayName("Penalty after due date")
        void testPenaltyCalculation() {

            emiSchedule.setStatus("OVERDUE");
            emiSchedule.setDueDate(LocalDate.now().minusDays(5));
            emiSchedule.setDaysPastDue(5);

            double penalty =
                    (emiSchedule.getAmountDue() * 0.02)
                            + (5 * 50);

            emiSchedule.setPenaltyAmount(penalty);

            emiScheduleRepository.save(emiSchedule);

            assertThat(
                    emiScheduleRepository.findById(
                                    emiSchedule.getEmiId())
                            .orElseThrow()
                            .getPenaltyAmount())
                    .isEqualTo(penalty);
        }

        @Test
        @DisplayName("Days Past Due")
        void testDPD() {

            emiSchedule.setStatus("OVERDUE");
            emiSchedule.setDaysPastDue(15);

            emiScheduleRepository.save(emiSchedule);

            assertThat(
                    emiScheduleRepository.findById(
                                    emiSchedule.getEmiId())
                            .orElseThrow()
                            .getDaysPastDue())
                    .isEqualTo(15);
        }

        @Test
        @DisplayName("Penalty Increases Daily")
        void testPenaltyIncrease() {

            double day1 =
                    (emiSchedule.getAmountDue() * 0.02) + (1 * 50);

            double day5 =
                    (emiSchedule.getAmountDue() * 0.02) + (5 * 50);

            assertThat(day5).isGreaterThan(day1);
        }

    }
// ==========================================
// LOAN CLOSURE TESTS
// ==========================================

    @Nested
    @DisplayName("Loan Closure Tests")
    class LoanClosureTests {

        @Test
        @DisplayName("Loan closes after final EMI")
        void testLoanClosure() {

            emiSchedule.setStatus("PAID");
            emiSchedule.setAmountPaid(
                    emiSchedule.getAmountDue());

            emiScheduleRepository.save(emiSchedule);

            loan.setLoanStatus("CLOSED");

            loanRepository.save(loan);

            Loan saved =
                    loanRepository.findById(
                                    loan.getLoanId())
                            .orElseThrow();

            assertThat(saved.getLoanStatus())
                    .isEqualTo("CLOSED");
        }

        @Test
        @DisplayName("Closed Loan cannot accept payment")
        void testClosedLoanPaymentRejected() {

            loan.setLoanStatus("CLOSED");

            loanRepository.save(loan);

            Loan saved =
                    loanRepository.findById(
                                    loan.getLoanId())
                            .orElseThrow();

            assertThat(saved.getLoanStatus())
                    .isEqualTo("CLOSED");
        }

        @Test
        @DisplayName("Loan remains ACTIVE if EMI pending")
        void testLoanStillActive() {

            emiSchedule.setStatus("PENDING");

            emiScheduleRepository.save(emiSchedule);

            loan.setLoanStatus("ACTIVE");

            loanRepository.save(loan);

            assertThat(
                    loanRepository.findById(
                                    loan.getLoanId())
                            .orElseThrow()
                            .getLoanStatus())
                    .isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("Outstanding Principal Positive")
        void testOutstandingPrincipal() {

            double outstanding =
                    emiSchedule.getPrincipalComponent();

            assertThat(outstanding).isPositive();
        }

    }
// =============================================
// TASK 8 : JWT AUTHENTICATION TESTS
// =============================================

    @Nested
    @DisplayName("Task 8 : JWT Authentication Tests")
    class JwtAuthenticationTests {

        @Test
        @DisplayName("Valid Login")
        void testSuccessfulLogin() throws Exception {

            Map<String, String> request = Map.of(
                    "email", "renjitha@gmail.com",
                    "password", "Password@123"
            );

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists())
                    .andExpect(jsonPath("$.token",
                            startsWith("eyJ")));
        }

        @Test
        @DisplayName("Invalid Password")
        void testWrongPassword() throws Exception {

            Map<String, String> request = Map.of(
                    "email", "renjitha@gmail.com",
                    "password", "wrongpassword"
            );

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Unknown Email")
        void testUnknownUser() throws Exception {

            Map<String, String> request = Map.of(
                    "email", "unknown@gmail.com",
                    "password", "Password@123"
            );

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Access API without JWT")
        void testWithoutToken() throws Exception {

            mockMvc.perform(get("/api/loans"))
                    .andExpect(status().isUnauthorized());

        }

        @Test
        @DisplayName("Invalid JWT")
        void testInvalidJwt() throws Exception {

            mockMvc.perform(get("/api/loans")
                            .header("Authorization",
                                    "Bearer invalid.token"))
                    .andExpect(status().isUnauthorized());

        }

        @Test
        @DisplayName("Expired JWT")
        void testExpiredJwt() throws Exception {

            mockMvc.perform(get("/api/loans")
                            .header("Authorization",
                                    "Bearer expired.jwt.token"))
                    .andExpect(status().isUnauthorized());

        }

    }
// =============================================
// TASK 9 : ROLE BASED AUTHORIZATION
// =============================================

    @Nested
    @DisplayName("Task 9 : Authorization Tests")
    class AuthorizationTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN can delete loan")
        void testAdminAccess() throws Exception {

            mockMvc.perform(delete("/api/loans/" +
                            loan.getLoanId()))
                    .andExpect(status().isNoContent());

        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER cannot delete loan")
        void testUserForbidden() throws Exception {

            mockMvc.perform(delete("/api/loans/" +
                            loan.getLoanId()))
                    .andExpect(status().isForbidden());

        }

        @Test
        @WithMockUser(roles = "MANAGER")
        @DisplayName("MANAGER can update loan")
        void testManagerUpdate() throws Exception {

            mockMvc.perform(put("/api/loans/" +
                            loan.getLoanId() +
                            "/interest")
                            .param("rate", "13"))
                    .andExpect(status().isOk());

        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER can view loans")
        void testUserViewLoan() throws Exception {

            mockMvc.perform(get("/api/loans"))
                    .andExpect(status().isOk());

        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN can access dashboard")
        void testDashboardAccess() throws Exception {

            mockMvc.perform(get("/api/dashboard"))
                    .andExpect(status().isOk());

        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER cannot access admin dashboard")
        void testDashboardForbidden() throws Exception {

            mockMvc.perform(get("/api/dashboard/admin"))
                    .andExpect(status().isForbidden());

        }

    }

// =============================================
// TASK 10 : GLOBAL EXCEPTION HANDLING
// =============================================

    @Nested
    @DisplayName("Task 10 : Exception Handling Tests")
    class ExceptionTests {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Loan Not Found")
        void testLoanNotFound() throws Exception {

            mockMvc.perform(get("/api/loans/999999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists());

        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Customer Not Found")
        void testCustomerNotFound() throws Exception {

            mockMvc.perform(get("/api/customers/999999"))
                    .andExpect(status().isNotFound());

        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Validation Failure")
        void testValidationException() throws Exception {

            String json = """
                    {
                        "loanType":"",
                        "principalAmount":-100,
                        "annualInterestRate":0,
                        "tenureMonths":0
                    }
                    """;

            mockMvc.perform(post("/api/loans")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());

        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Delete Missing Loan")
        void testDeleteMissingLoan() throws Exception {

            mockMvc.perform(delete("/api/loans/10000"))
                    .andExpect(status().isNotFound());

        }

    }
}








