package org.example.loanemimgmt;

import org.example.loanemimgmt.enums.EmiStatus;
import org.example.loanemimgmt.enums.LoanStatus;
import org.example.loanemimgmt.enums.LoanType;
import org.example.loanemimgmt.enums.UserRole;
import org.example.loanemimgmt.model.Customer;
import org.example.loanemimgmt.model.EmiPayment;
import org.example.loanemimgmt.model.EmiSchedule;
import org.example.loanemimgmt.model.Loan;
import org.example.loanemimgmt.repository.CustomerRepository;
import org.example.loanemimgmt.repository.EmiPaymentRepository;
import org.example.loanemimgmt.repository.EmiScheduleRepository;
import org.example.loanemimgmt.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LoanEmiMgmtExhaustiveTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private EmiScheduleRepository emiScheduleRepository;

    @Autowired
    private EmiPaymentRepository emiPaymentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        customer = Customer.builder()
                .customerName("Renjitha")
                .email("renjitha@gmail.com")
                .password(passwordEncoder.encode("Password@123"))
                .phoneNumber("9876543210")
                .city("Chennai")
                .creditScore(780)
                .role(UserRole.USER)
                .build();
        customer = customerRepository.save(customer);

        loan = Loan.builder()
                .loanType(LoanType.PERSONAL)
                .principalAmount(new BigDecimal("500000.00"))
                .annualInterestRate(new BigDecimal("12.00"))
                .tenureMonths(24)
                .emiAmount(new BigDecimal("23536.74"))
                .disbursementDate(LocalDate.now())
                .loanStatus(LoanStatus.ON_PROGRESS)
                .customer(customer)
                .build();
        customer.getLoans().add(loan);
        loan = loanRepository.save(loan);

        emiSchedule = EmiSchedule.builder()
                .installmentNumber(1)
                .dueDate(LocalDate.now().plusMonths(1))
                .amountDue(new BigDecimal("23536.74"))
                .principalComponent(new BigDecimal("18536.74"))
                .interestComponent(new BigDecimal("5000.00"))
                .amountPaid(BigDecimal.ZERO)
                .status(EmiStatus.PENDING)
                .daysPastDue(0)
                .penaltyAmount(BigDecimal.ZERO)
                .loan(loan)
                .build();
        loan.getEmiSchedules().add(emiSchedule);
        emiSchedule = emiScheduleRepository.save(emiSchedule);

        emiPayment = EmiPayment.builder()
                .amount(new BigDecimal("10000.00"))
                .paymentMode("UPI")
                .paymentDate(LocalDate.now())
                .referenceNumber("PAY123456")
                .emiSchedule(emiSchedule)
                .build();
        emiSchedule.getEmiPayments().add(emiPayment);
        emiPayment = emiPaymentRepository.save(emiPayment);
    }

    @Nested
    @DisplayName("Task 1 : Entity Mapping Tests")
    class EntityMappingTests {

        @Test
        @DisplayName("Customer should contain loan")
        void testCustomerLoanMapping() {
            Customer savedCustomer = customerRepository.findById(customer.getCustomerId()).orElseThrow();
            assertThat(savedCustomer).isNotNull();
            assertThat(savedCustomer.getLoans()).hasSize(1);
        }

        @Test
        @DisplayName("Loan should reference customer")
        void testLoanCustomerMapping() {
            Loan savedLoan = loanRepository.findById(loan.getLoanId()).orElseThrow();
            assertThat(savedLoan.getCustomer()).isNotNull();
            assertThat(savedLoan.getCustomer().getCustomerName()).isEqualTo("Renjitha");
        }

        @Test
        @DisplayName("Loan should contain EMI schedules")
        void testLoanEmiMapping() {
            Loan savedLoan = loanRepository.findById(loan.getLoanId()).orElseThrow();
            assertThat(savedLoan.getEmiSchedules()).hasSize(1);
        }

        @Test
        @DisplayName("EMI should belong to Loan")
        void testEmiLoanMapping() {
            EmiSchedule emi = emiScheduleRepository.findById(emiSchedule.getEmiId()).orElseThrow();
            assertThat(emi.getLoan()).isNotNull();
            assertThat(emi.getLoan().getLoanType()).isEqualTo(LoanType.PERSONAL);
        }

        @Test
        @DisplayName("EMI should contain payments")
        void testEmiPaymentMapping() {
            EmiSchedule emi = emiScheduleRepository.findById(emiSchedule.getEmiId()).orElseThrow();
            assertThat(emi.getEmiPayments()).hasSize(1);
        }

        @Test
        @DisplayName("Payment should reference EMI")
        void testPaymentMapping() {
            EmiPayment payment = emiPaymentRepository.findById(emiPayment.getPaymentId()).orElseThrow();
            assertThat(payment.getEmiSchedule()).isNotNull();
            assertThat(payment.getEmiSchedule().getInstallmentNumber()).isEqualTo(1);
        }

        @Test
        @DisplayName("Cascade delete customer")
        void testCascadeDeleteCustomer() {
            customerRepository.delete(customer);
            customerRepository.flush();
            assertThat(loanRepository.findAll()).isEmpty();
            assertThat(emiScheduleRepository.findAll()).isEmpty();
        }

        @Test
        @DisplayName("Cascade delete loan")
        void testCascadeDeleteLoan() {
            customer.getLoans().remove(loan);
            Loan savedLoan = loanRepository.findById(loan.getLoanId()).orElseThrow();
            loanRepository.delete(savedLoan);
          //  loanRepository.flush();
            assertThat(loanRepository.findById(loan.getLoanId())).isEmpty();
            assertThat(emiScheduleRepository.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Task 2 : Bean Validation Tests")
    class BeanValidationTests {

        @Test
        @WithMockUser(roles = "MANAGER")
        @DisplayName("Customer name cannot be blank")
        void testBlankCustomerName() throws Exception {
            String request = """
                    {
                      "customerName":"",
                      "email":"abc@gmail.com",
                      "password":"Password@123",
                      "phoneNumber":"9876543211",
                      "city":"Chennai",
                      "creditScore":750
                    }
                    """;
            mockMvc.perform(post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "MANAGER")
        @DisplayName("Email should be valid")
        void testInvalidEmail() throws Exception {
            String request = """
                    {
                      "customerName":"Raj",
                      "email":"abcgmail.com",
                      "password":"Password@123",
                      "phoneNumber":"9876543211",
                      "city":"Chennai",
                      "creditScore":750
                    }
                    """;
            mockMvc.perform(post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "MANAGER")
        @DisplayName("Password should not be blank")
        void testBlankPassword() throws Exception {
            String request = """
                    {
                      "customerName":"Raj",
                      "email":"abc@gmail.com",
                      "password":"",
                      "phoneNumber":"9876543211",
                      "city":"Chennai",
                      "creditScore":750
                    }
                    """;
            mockMvc.perform(post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "MANAGER")
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
            mockMvc.perform(post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "MANAGER")
        @DisplayName("City cannot be blank")
        void testBlankCity() throws Exception {
            String request = """
                    {
                      "customerName":"Raj",
                      "email":"abc@gmail.com",
                      "password":"Password@123",
                      "phoneNumber":"9876543211",
                      "city":"",
                      "creditScore":750
                    }
                    """;
            mockMvc.perform(post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "MANAGER")
        @DisplayName("Credit score cannot be below 300")
        void testLowCreditScore() throws Exception {
            String request = """
                    {
                      "customerName":"Raj",
                      "email":"abc@gmail.com",
                      "password":"Password@123",
                      "phoneNumber":"9876543211",
                      "city":"Chennai",
                      "creditScore":200
                    }
                    """;
            mockMvc.perform(post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Principal amount cannot be zero or negative")
        void testZeroPrincipal() {
            Loan invalid = Loan.builder()
                    .loanType(LoanType.PERSONAL)
                    .principalAmount(BigDecimal.ZERO)
                    .annualInterestRate(new BigDecimal("10.00"))
                    .tenureMonths(24)
                    .customer(customer)
                    .build();
            assertThatThrownBy(() -> loanRepository.saveAndFlush(invalid)).isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Interest rate cannot be negative")
        void testNegativeInterestRate() {
            Loan invalid = Loan.builder()
                    .loanType(LoanType.PERSONAL)
                    .principalAmount(new BigDecimal("100000.00"))
                    .annualInterestRate(new BigDecimal("-5.00"))
                    .tenureMonths(24)
                    .customer(customer)
                    .build();
            assertThatThrownBy(() -> loanRepository.saveAndFlush(invalid)).isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Tenure should be greater than zero")
        void testZeroTenure() {
            Loan invalid = Loan.builder()
                    .loanType(LoanType.PERSONAL)
                    .principalAmount(new BigDecimal("100000.00"))
                    .annualInterestRate(new BigDecimal("10.00"))
                    .tenureMonths(0)
                    .customer(customer)
                    .build();
            assertThatThrownBy(() -> loanRepository.saveAndFlush(invalid)).isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Loan type cannot be null")
        void testBlankLoanType() {
            Loan invalid = Loan.builder()
                    .loanType(null)
                    .principalAmount(new BigDecimal("100000.00"))
                    .annualInterestRate(new BigDecimal("10.00"))
                    .tenureMonths(24)
                    .customer(customer)
                    .build();
            assertThatThrownBy(() -> loanRepository.saveAndFlush(invalid)).isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Principal amount cannot be negative")
        void testNegativePrincipal() {
            Loan invalid = Loan.builder()
                    .loanType(LoanType.PERSONAL)
                    .principalAmount(new BigDecimal("-100000.00"))
                    .annualInterestRate(new BigDecimal("10.00"))
                    .tenureMonths(24)
                    .customer(customer)
                    .build();
            assertThatThrownBy(() -> loanRepository.saveAndFlush(invalid)).isInstanceOf(Exception.class);
        }

        @Test
        @WithMockUser(roles = "MANAGER")
        @DisplayName("Valid customer request should pass")
        void testValidCustomerRequest() throws Exception {
            String request = """
                    {
                      "customerName":"Arun",
                      "email":"arun@gmail.com",
                      "password":"Password@123",
                      "phoneNumber":"9876543212",
                      "city":"Madurai",
                      "creditScore":780
                    }
                    """;
            mockMvc.perform(post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Task 3 : Spring Data JPA Derived Query Tests")
    class DerivedQueryTests {

        @Test
        @DisplayName("Find loans by loan type")
        void testFindByLoanType() {
            Page<Loan> loans = loanRepository.findByLoanType(LoanType.PERSONAL, PageRequest.of(0, 10));
            assertThat(loans.getContent()).isNotEmpty();
            assertThat(loans.getContent().get(0).getLoanType()).isEqualTo(LoanType.PERSONAL);
        }

        @Test
        @DisplayName("Find loans by customer city")
        void testFindByCustomerCity() {
            Page<Loan> loans = loanRepository.findByCustomerCityIgnoreCase("Chennai", PageRequest.of(0, 10));
            assertThat(loans.getContent()).hasSize(1);
            assertThat(loans.getContent().get(0).getCustomer().getCity()).isEqualTo("Chennai");
        }

        @Test
        @DisplayName("Find loans by ON_PROGRESS status")
        void testFindByLoanStatus() {
            Page<Loan> loans = loanRepository.findByLoanStatus(LoanStatus.ON_PROGRESS, PageRequest.of(0, 10));
            assertThat(loans.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Find loans having principal greater than amount")
        void testFindByPrincipalGreaterThan() {
            Page<Loan> loans = loanRepository.findByPrincipalAmountGreaterThanEqual(
                    new BigDecimal("100000.00"), PageRequest.of(0, 10));
            assertThat(loans.getContent()).hasSize(1);
            assertThat(loans.getContent().get(0).getPrincipalAmount())
                    .isGreaterThanOrEqualTo(new BigDecimal("100000.00"));
        }

        @Test
        @DisplayName("Find customer using email")
        void testFindCustomerByEmail() {
            Customer foundCustomer = customerRepository.findByEmailIgnoreCase("renjitha@gmail.com").orElseThrow();
            assertThat(foundCustomer).isNotNull();
            assertThat(foundCustomer.getCustomerName()).isEqualTo("Renjitha");
        }

        @Test
        @DisplayName("Find customer using phone number")
        void testFindCustomerByPhone() {
            List<Customer> customers = customerRepository.findByCityIgnoreCase("Chennai");
            assertThat(customers).isNotEmpty();
            assertThat(customers.get(0).getCity()).isEqualTo("Chennai");
        }

        @Test
        @DisplayName("Find customers by city")
        void testFindCustomersByCity() {
            List<Customer> customers = customerRepository.findByCityIgnoreCase("Chennai");
            assertThat(customers).hasSize(1);
        }

        @Test
        @DisplayName("Find customers with credit score greater than")
        void testFindCustomersByCreditScore() {
            List<Customer> customers = customerRepository.findByCityIgnoreCase("Chennai");
            assertThat(customers).isNotEmpty();
            assertThat(customers.get(0).getCreditScore()).isGreaterThan(700);
        }

        @Test
        @DisplayName("Find pending EMI schedules")
        void testFindPendingEmis() {
            List<EmiSchedule> emis = emiScheduleRepository.findByStatusAndDueDateBefore(EmiStatus.PENDING, LocalDate.now().plusMonths(2));
            assertThat(emis).hasSize(1);
            assertThat(emis.get(0).getAmountPaid()).isEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Find overdue EMI schedules")
        void testFindOverdueEmis() {
            emiSchedule.setStatus(EmiStatus.OVERDUE);
            emiSchedule.setDueDate(LocalDate.now().minusDays(1));
            emiScheduleRepository.save(emiSchedule);

            List<EmiSchedule> overdue = emiScheduleRepository.findByStatusAndDueDateBefore(EmiStatus.OVERDUE, LocalDate.now().plusDays(1));
            assertThat(overdue).hasSize(1);
        }

        @Test
        @DisplayName("Find EMI schedules by due date before today")
        void testFindByDueDateBefore() {
            emiSchedule.setDueDate(LocalDate.now().minusDays(5));
            emiScheduleRepository.save(emiSchedule);

            List<EmiSchedule> result = emiScheduleRepository.findByStatusAndDueDateBefore(EmiStatus.PENDING, LocalDate.now());
            assertThat(result).isNotEmpty();
        }

        @Test
        @DisplayName("Find EMI payment by reference number")
        void testFindPaymentByReferenceNumber() {
            List<EmiPayment> payments = emiPaymentRepository.findLatestPayment(PageRequest.of(0, 1));
            assertThat(payments).isNotEmpty();
            assertThat(payments.get(0).getReferenceNumber()).isEqualTo("PAY123456");
        }
    }

    @Nested
    @DisplayName("Task 4 : Complex JPQL Query Tests")
    class JpqlQueryTests {

        @Test
        @DisplayName("JPQL : Customers having overdue EMI")
        void testCustomersWithOverdueEmi() {
            emiSchedule.setStatus(EmiStatus.OVERDUE);
            emiSchedule.setDaysPastDue(10);
            emiScheduleRepository.save(emiSchedule);

            List<Customer> customers = customerRepository.findCustomersWithOverdueEmis();
            assertThat(customers).isNotEmpty();
            assertThat(customers.get(0).getCustomerName()).isEqualTo("Renjitha");
        }

        @Test
        @DisplayName("JPQL : Highest overdue EMI")
        void testHighestOverdueEmi() {
            emiSchedule.setStatus(EmiStatus.OVERDUE);
            emiSchedule.setPenaltyAmount(new BigDecimal("2500.00"));
            emiScheduleRepository.save(emiSchedule);

            List<EmiSchedule> emis = emiScheduleRepository.findHighestOverdueAmount(PageRequest.of(0, 1));
            assertThat(emis).isNotEmpty();
            assertThat(emis.get(0).getPenaltyAmount()).isEqualTo(new BigDecimal("2500.00"));
        }

        @Test
        @DisplayName("JPQL : Total EMI collection by city")
        void testTotalCollectionByCity() {
            emiSchedule.setAmountPaid(new BigDecimal("23536.74"));
            emiSchedule.setStatus(EmiStatus.PAID);
            emiScheduleRepository.save(emiSchedule);

            List<Object[]> result = emiPaymentRepository.getTotalEmiCollectionByCityRows();
            assertThat(result).isNotEmpty();
            Object[] row = result.get(0);
            assertThat(row[0]).isEqualTo("Chennai");
            assertThat(((Number) row[1]).doubleValue()).isGreaterThan(0);
        }

        @Test
        @DisplayName("JPQL : Latest EMI payment")
        void testLatestPayment() {
            List<EmiPayment> payments = emiPaymentRepository.findLatestPayment(PageRequest.of(0, 1));
            assertThat(payments).hasSize(1);
            assertThat(payments.get(0).getReferenceNumber()).isEqualTo("PAY123456");
        }

        @Test
        @DisplayName("JPQL : Loans having zero overdue EMI")
        void testLoansWithZeroOverdue() {
            List<Loan> loans = loanRepository.findLoansWithZeroOverdueEmis();
            assertThat(loans).hasSize(1);
            assertThat(loans.get(0).getLoanStatus()).isEqualTo(LoanStatus.ON_PROGRESS);
        }

        @Test
        @DisplayName("JPQL : Top defaulters")
        void testTopDefaulters() {
            emiSchedule.setStatus(EmiStatus.OVERDUE);
            emiSchedule.setPenaltyAmount(new BigDecimal("3500.00"));
            emiSchedule.setDaysPastDue(40);
            emiScheduleRepository.save(emiSchedule);

            List<Object[]> defaulters = customerRepository.findTopDefaulterRows(PageRequest.of(0, 1));
            assertThat(defaulters).isNotEmpty();
            assertThat(defaulters.get(0)[1]).isEqualTo("Renjitha");
        }

        @Test
        @DisplayName("JPQL : Find active loans")
        void testFindActiveLoans() {
            Page<Loan> loans = loanRepository.findByLoanStatus(LoanStatus.ON_PROGRESS, PageRequest.of(0, 10));
            assertThat(loans.getContent()).hasSize(1);
            assertThat(loans.getContent().get(0).getLoanStatus()).isEqualTo(LoanStatus.ON_PROGRESS);
        }

        @Test
        @DisplayName("JPQL : Count loans per city")
        void testLoanCountPerCity() {
            long activeLoans = loanRepository.countByLoanStatus(LoanStatus.ON_PROGRESS);
            assertThat(activeLoans).isGreaterThanOrEqualTo(1L);
        }

        @Test
        @DisplayName("JPQL : Average interest rate")
        void testAverageInterestRate() {
            BigDecimal avg = loanRepository.getAverageInterestRate();
            assertThat(avg).isEqualByComparingTo(new BigDecimal("12.00"));
        }

        @Test
        @DisplayName("JPQL : Highest outstanding loan")
        void testHighestOutstandingLoan() {
            List<BigDecimal> outstandings = emiScheduleRepository.findHighestOutstandingLoan(PageRequest.of(0, 1));
            assertThat(outstandings).isNotEmpty();
            assertThat(outstandings.get(0)).isGreaterThan(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("JPQL : Total penalty collected")
        void testPenaltyCollection() {
            emiSchedule.setPenaltyAmount(new BigDecimal("500.00"));
            emiScheduleRepository.save(emiSchedule);

            BigDecimal total = emiScheduleRepository.getTotalPenaltyCollected();
            assertThat(total).isGreaterThanOrEqualTo(new BigDecimal("500.00"));
        }

        @Test
        @DisplayName("JPQL : Total EMI collected")
        void testTotalEmiCollected() {
            BigDecimal total = emiPaymentRepository.getTotalEmiCollected();
            assertThat(total).isGreaterThan(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("Task 5 : JPQL Modifying Query Tests")
    class JpqlUpdateTests {

        @Test
        @DisplayName("Increase interest rate for PERSONAL loans")
        void testIncreaseInterestRate() {
            int updated = loanRepository.reviseAnnualInterestRateByLoanTypes(
                    List.of(LoanType.PERSONAL),
                    new BigDecimal("15.00")
            );

            assertThat(updated).isEqualTo(1);
            loanRepository.flush();

            Loan updatedLoan = loanRepository.findById(loan.getLoanId()).orElseThrow();
            assertThat(updatedLoan.getAnnualInterestRate()).isEqualTo(new BigDecimal("15.00"));
        }

        @Test
        @DisplayName("Update should not affect other loan types")
        void testUpdateSpecificLoanTypeOnly() {
            Loan vehicleLoan = Loan.builder()
                    .loanType(LoanType.VEHICLE)
                    .principalAmount(new BigDecimal("800000.00"))
                    .annualInterestRate(new BigDecimal("11.00"))
                    .tenureMonths(60)
                    .loanStatus(LoanStatus.ON_PROGRESS)
                    .disbursementDate(LocalDate.now())
                    .customer(customer)
                    .build();
            loanRepository.save(vehicleLoan);

            loanRepository.reviseAnnualInterestRateByLoanTypes(
                    List.of(LoanType.PERSONAL),
                    new BigDecimal("14.50")
            );

            Loan personal = loanRepository.findById(loan.getLoanId()).orElseThrow();
            Loan vehicle = loanRepository.findById(vehicleLoan.getLoanId()).orElseThrow();

            assertThat(personal.getAnnualInterestRate()).isEqualTo(new BigDecimal("14.50"));
            assertThat(vehicle.getAnnualInterestRate()).isEqualTo(new BigDecimal("11.00"));
        }

        @Test
        @DisplayName("Updating non-existing loan type returns zero")
        void testUpdateInvalidLoanType() {
            int updated = loanRepository.reviseAnnualInterestRateByLoanTypes(
                    List.of(LoanType.GOLD),
                    new BigDecimal("20.00")
            );
            assertThat(updated).isZero();
        }
    }

    @Nested
    @DisplayName("Task 6 : Pagination & Sorting Tests")
    class PaginationTests {

        @BeforeEach
        void createLoans() {
            for (int i = 1; i <= 10; i++) {
                Loan l = Loan.builder()
                        .loanType(LoanType.PERSONAL)
                        .principalAmount(new BigDecimal(100000L * i))
                        .annualInterestRate(new BigDecimal("10.00"))
                        .tenureMonths(24)
                        .emiAmount(new BigDecimal("9000.00"))
                        .disbursementDate(LocalDate.now())
                        .loanStatus(LoanStatus.ON_PROGRESS)
                        .customer(customer)
                        .build();
                loanRepository.save(l);
            }
        }

        @Test
        @DisplayName("Page 0 Size 5")
        void testFirstPage() {
            Page<Loan> page = loanRepository.findAll(
                    PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "principalAmount"))
            );
            assertThat(page.getContent()).hasSize(5);
        }

        @Test
        @DisplayName("Second Page")
        void testSecondPage() {
            Page<Loan> page = loanRepository.findAll(
                    PageRequest.of(1, 5, Sort.by(Sort.Direction.DESC, "principalAmount"))
            );
            assertThat(page.getContent()).hasSize(5);
        }

        @Test
        @DisplayName("Verify descending sorting")
        void testSortingDescending() {
            Page<Loan> page = loanRepository.findAll(
                    PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "principalAmount"))
            );
            List<Loan> loans = page.getContent();
            assertThat(loans.get(0).getPrincipalAmount())
                    .isGreaterThan(loans.get(1).getPrincipalAmount());
        }

        @Test
        @DisplayName("Verify ascending sorting")
        void testSortingAscending() {
            Page<Loan> page = loanRepository.findAll(
                    PageRequest.of(0, 10, Sort.by("principalAmount"))
            );
            List<Loan> loans = page.getContent();
            assertThat(loans.get(0).getPrincipalAmount())
                    .isLessThan(loans.get(1).getPrincipalAmount());
        }

        @Test
        @DisplayName("Verify total pages")
        void testTotalPages() {
            Page<Loan> page = loanRepository.findAll(PageRequest.of(0, 5));
            assertThat(page.getTotalPages()).isGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("Verify total elements")
        void testTotalElements() {
            Page<Loan> page = loanRepository.findAll(PageRequest.of(0, 5));
            assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(10);
        }

        @Test
        @DisplayName("Empty page")
        void testEmptyPage() {
            Page<Loan> page = loanRepository.findAll(PageRequest.of(50, 5));
            assertThat(page.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Loan Creation Business Tests")
    class LoanCreationTests {

        @Test
        @DisplayName("Create Business Loan Successfully")
        void testCreateLoan() {
            Loan newLoan = Loan.builder()
                    .loanType(LoanType.BUSINESS)
                    .principalAmount(new BigDecimal("800000.00"))
                    .annualInterestRate(new BigDecimal("13.50"))
                    .tenureMonths(48)
                    .loanStatus(LoanStatus.ON_PROGRESS)
                    .disbursementDate(LocalDate.now())
                    .customer(customer)
                    .build();
            Loan saved = loanRepository.save(newLoan);

            assertThat(saved.getLoanId()).isNotNull();
            assertThat(saved.getLoanType()).isEqualTo(LoanType.BUSINESS);
        }

        @Test
        @DisplayName("Principal Amount must be greater than zero")
        void testNegativePrincipalRejected() {
            Loan newLoan = Loan.builder()
                    .loanType(LoanType.PERSONAL)
                    .principalAmount(new BigDecimal("-1000.00"))
                    .annualInterestRate(new BigDecimal("12.00"))
                    .tenureMonths(24)
                    .customer(customer)
                    .build();

            assertThatThrownBy(() -> loanRepository.saveAndFlush(newLoan))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Zero Principal Amount")
        void testZeroPrincipalRejected() {
            Loan newLoan = Loan.builder()
                    .loanType(LoanType.PERSONAL)
                    .principalAmount(BigDecimal.ZERO)
                    .annualInterestRate(new BigDecimal("12.00"))
                    .tenureMonths(24)
                    .customer(customer)
                    .build();

            assertThatThrownBy(() -> loanRepository.saveAndFlush(newLoan))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Loan must belong to customer")
        void testLoanCustomerRelation() {
            Loan saved = loanRepository.findById(loan.getLoanId()).orElseThrow();
            assertThat(saved.getCustomer()).isNotNull();
        }

        @Test
        @DisplayName("Loan Status should be ON_PROGRESS after creation")
        void testLoanStatus() {
            Loan saved = loanRepository.findById(loan.getLoanId()).orElseThrow();
            assertThat(saved.getLoanStatus()).isEqualTo(LoanStatus.ON_PROGRESS);
        }
    }

    @Nested
    @DisplayName("EMI Generation Tests")
    class EmiGenerationTests {

        @Test
        @DisplayName("Loan should have EMI schedule")
        void testEmiGenerated() {
            List<EmiSchedule> emis = emiScheduleRepository.findByStatusAndDueDateBefore(
                    EmiStatus.PENDING, LocalDate.now().plusDays(32));
            assertThat(emis).isNotEmpty();
        }

        @Test
        @DisplayName("EMI installment number starts from 1")
        void testInstallmentNumber() {
            EmiSchedule emi = emiScheduleRepository.findById(emiSchedule.getEmiId()).orElseThrow();
            assertThat(emi.getInstallmentNumber()).isEqualTo(1);
        }

        @Test
        @DisplayName("EMI due date should not be null")
        void testDueDateGenerated() {
            EmiSchedule emi = emiScheduleRepository.findById(emiSchedule.getEmiId()).orElseThrow();
            assertThat(emi.getDueDate()).isNotNull();
        }

        @Test
        @DisplayName("EMI amount should be positive")
        void testEmiAmountPositive() {
            EmiSchedule emi = emiScheduleRepository.findById(emiSchedule.getEmiId()).orElseThrow();
            assertThat(emi.getAmountDue()).isGreaterThan(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Initial Amount Paid should be zero")
        void testInitialAmountPaid() {
            EmiSchedule emi = emiScheduleRepository.findById(emiSchedule.getEmiId()).orElseThrow();
            assertThat(emi.getAmountPaid()).isEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Initial EMI status should be PENDING")
        void testInitialStatus() {
            EmiSchedule emi = emiScheduleRepository.findById(emiSchedule.getEmiId()).orElseThrow();
            assertThat(emi.getStatus()).isEqualTo(EmiStatus.PENDING);
        }

        @Test
        @DisplayName("Initial Penalty should be zero")
        void testPenaltyInitiallyZero() {
            EmiSchedule emi = emiScheduleRepository.findById(emiSchedule.getEmiId()).orElseThrow();
            assertThat(emi.getPenaltyAmount()).isEqualTo(BigDecimal.ZERO);
        }
    }

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
            double expectedEmi = (principal * monthlyRate * Math.pow(1 + monthlyRate, months))
                    / (Math.pow(1 + monthlyRate, months) - 1);

            assertThat(loan.getEmiAmount().doubleValue())
                    .isCloseTo(expectedEmi, within(2.0));
        }
    }

    @Nested
    @DisplayName("EMI Payment Business Tests")
    class EmiPaymentTests {

        @Test
        @DisplayName("Partial Payment")
        void testPartialPayment() {
            emiSchedule.setAmountPaid(new BigDecimal("10000.00"));
            emiSchedule.setStatus(EmiStatus.PENDING);
            emiScheduleRepository.save(emiSchedule);

            EmiSchedule saved = emiScheduleRepository.findById(emiSchedule.getEmiId()).orElseThrow();
            assertThat(saved.getAmountPaid()).isEqualTo(new BigDecimal("10000.00"));
            assertThat(saved.getStatus()).isEqualTo(EmiStatus.PENDING);
        }

        @Test
        @DisplayName("Full EMI Payment")
        void testFullPayment() {
            emiSchedule.setAmountPaid(emiSchedule.getAmountDue());
            emiSchedule.setStatus(EmiStatus.PAID);
            emiSchedule.setPaymentDate(LocalDate.now());
            emiScheduleRepository.save(emiSchedule);

            EmiSchedule saved = emiScheduleRepository.findById(emiSchedule.getEmiId()).orElseThrow();
            assertThat(saved.getStatus()).isEqualTo(EmiStatus.PAID);
            assertThat(saved.getAmountPaid()).isEqualTo(saved.getAmountDue());
        }

        @Test
        @DisplayName("Reject Over Payment")
        void testOverPaymentRejected() {
            BigDecimal payment = emiSchedule.getAmountDue().add(new BigDecimal("5000"));
            assertThat(payment).isGreaterThan(emiSchedule.getAmountDue());
        }

        @Test
        @DisplayName("Reject Negative Payment")
        void testNegativePayment() {
            BigDecimal payment = new BigDecimal("-500");
            assertThat(payment).isNegative();
        }

        @Test
        @DisplayName("Zero Payment")
        void testZeroPayment() {
            emiSchedule.setAmountPaid(BigDecimal.ZERO);
            emiScheduleRepository.save(emiSchedule);

            EmiSchedule saved = emiScheduleRepository.findById(emiSchedule.getEmiId()).orElseThrow();
            assertThat(saved.getStatus()).isEqualTo(EmiStatus.PENDING);
        }

        @Test
        @DisplayName("Payment Date Stored")
        void testPaymentDateStored() {
            LocalDate today = LocalDate.now();
            emiSchedule.setPaymentDate(today);
            emiSchedule.setStatus(EmiStatus.PAID);
            emiSchedule.setAmountPaid(emiSchedule.getAmountDue());
            emiScheduleRepository.save(emiSchedule);

            assertThat(emiScheduleRepository.findById(emiSchedule.getEmiId()).orElseThrow().getPaymentDate())
                    .isEqualTo(today);
        }
    }

    @Nested
    @DisplayName("Penalty Calculation Tests")
    class PenaltyTests {

        @Test
        @DisplayName("No penalty before due date")
        void testNoPenalty() {
            emiSchedule.setDueDate(LocalDate.now().plusDays(5));
            emiSchedule.setPenaltyAmount(BigDecimal.ZERO);
            emiScheduleRepository.save(emiSchedule);

            assertThat(emiScheduleRepository.findById(emiSchedule.getEmiId()).orElseThrow().getPenaltyAmount())
                    .isEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Penalty after due date")
        void testPenaltyCalculation() {
            emiSchedule.setStatus(EmiStatus.OVERDUE);
            emiSchedule.setDueDate(LocalDate.now().minusDays(5));
            emiSchedule.setDaysPastDue(5);

            BigDecimal penalty = emiSchedule.getAmountDue().multiply(new BigDecimal("0.02"))
                    .add(new BigDecimal("50").multiply(new BigDecimal("5")));
            emiSchedule.setPenaltyAmount(penalty);
            emiScheduleRepository.save(emiSchedule);

            assertThat(emiScheduleRepository.findById(emiSchedule.getEmiId()).orElseThrow().getPenaltyAmount())
                    .isEqualTo(penalty);
        }

        @Test
        @DisplayName("Days Past Due")
        void testDPD() {
            emiSchedule.setStatus(EmiStatus.OVERDUE);
            emiSchedule.setDaysPastDue(15);
            emiScheduleRepository.save(emiSchedule);

            assertThat(emiScheduleRepository.findById(emiSchedule.getEmiId()).orElseThrow().getDaysPastDue())
                    .isEqualTo(15);
        }

        @Test
        @DisplayName("Penalty Increases Daily")
        void testPenaltyIncrease() {
            BigDecimal day1 = emiSchedule.getAmountDue().multiply(new BigDecimal("0.02"))
                    .add(new BigDecimal("50"));
            BigDecimal day5 = emiSchedule.getAmountDue().multiply(new BigDecimal("0.02"))
                    .add(new BigDecimal("250"));
            assertThat(day5).isGreaterThan(day1);
        }
    }

    @Nested
    @DisplayName("Loan Closure Tests")
    class LoanClosureTests {

        @Test
        @DisplayName("Loan closes after final EMI")
        void testLoanClosure() {
            emiSchedule.setStatus(EmiStatus.PAID);
            emiSchedule.setAmountPaid(emiSchedule.getAmountDue());
            emiScheduleRepository.save(emiSchedule);

            loan.setLoanStatus(LoanStatus.CLOSED);
            loanRepository.save(loan);

            Loan saved = loanRepository.findById(loan.getLoanId()).orElseThrow();
            assertThat(saved.getLoanStatus()).isEqualTo(LoanStatus.CLOSED);
        }

        @Test
        @DisplayName("Closed Loan cannot accept payment")
        void testClosedLoanPaymentRejected() {
            loan.setLoanStatus(LoanStatus.CLOSED);
            loanRepository.save(loan);

            Loan saved = loanRepository.findById(loan.getLoanId()).orElseThrow();
            assertThat(saved.getLoanStatus()).isEqualTo(LoanStatus.CLOSED);
        }

        @Test
        @DisplayName("Loan remains ON_PROGRESS if EMI pending")
        void testLoanStillActive() {
            emiSchedule.setStatus(EmiStatus.PENDING);
            emiScheduleRepository.save(emiSchedule);

            loan.setLoanStatus(LoanStatus.ON_PROGRESS);
            loanRepository.save(loan);

            assertThat(loanRepository.findById(loan.getLoanId()).orElseThrow().getLoanStatus())
                    .isEqualTo(LoanStatus.ON_PROGRESS);
        }

        @Test
        @DisplayName("Outstanding Principal Positive")
        void testOutstandingPrincipal() {
            BigDecimal outstanding = emiSchedule.getPrincipalComponent();
            assertThat(outstanding).isPositive();
        }
    }

    @Nested
    @DisplayName("Task 8 : JWT Authentication Tests")
    class JwtAuthenticationTests {

        @Test
        @DisplayName("Valid Login")
        void testSuccessfulLogin() throws Exception {
            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"renjitha@gmail.com\",\"password\":\"Password@123\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists());
        }

        @Test
        @DisplayName("Invalid Password")
        void testWrongPassword() throws Exception {
            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"renjitha@gmail.com\",\"password\":\"wrongpassword\"}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Unknown Email")
        void testUnknownUser() throws Exception {
            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"unknown@gmail.com\",\"password\":\"Password@123\"}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Access API without JWT")
        void testWithoutToken() throws Exception {
            mockMvc.perform(get("/loans"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Invalid JWT")
        void testInvalidJwt() throws Exception {
            mockMvc.perform(get("/loans")
                            .header("Authorization", "Bearer invalid.token"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Expired JWT")
        void testExpiredJwt() throws Exception {
            mockMvc.perform(get("/loans")
                            .header("Authorization", "Bearer expired.jwt.token"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Task 9 : Authorization Tests")
    class AuthorizationTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN can delete loan")
        void testAdminAccess() throws Exception {
            mockMvc.perform(delete("/customers/" + customer.getCustomerId()))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER cannot delete loan")
        void testUserForbidden() throws Exception {
            mockMvc.perform(delete("/customers/" + customer.getCustomerId()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "MANAGER")
        @DisplayName("MANAGER can update loan")
        void testManagerUpdate() throws Exception {
            mockMvc.perform(get("/loans/dashboard"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER can view loans")
        void testUserViewLoan() throws Exception {
            mockMvc.perform(get("/loans"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN can access dashboard")
        void testDashboardAccess() throws Exception {
            mockMvc.perform(get("/dashboard"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER cannot access admin dashboard")
        void testDashboardForbidden() throws Exception {
            mockMvc.perform(get("/dashboard"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Task 10 : Exception Handling Tests")
    class ExceptionTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Loan Not Found")
        void testLoanNotFound() throws Exception {
            mockMvc.perform(post("/loans/999999/approve"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Customer Not Found")
        void testCustomerNotFound() throws Exception {
            mockMvc.perform(get("/customers/999999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "MANAGER")
        @DisplayName("Validation Failure")
        void testValidationException() throws Exception {
            String json = """
                    {
                        "customerName":"",
                        "email":"badmail",
                        "password":"",
                        "phoneNumber":"1",
                        "city":""
                    }
                    """;
            mockMvc.perform(post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Delete Missing Loan")
        void testDeleteMissingLoan() throws Exception {
            mockMvc.perform(delete("/customers/10000"))
                    .andExpect(status().isNotFound());
        }
    }
}
