package org.northernarc.loanemi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.northernarc.loanemi.dto.EmiCalculationRequestDTO;
import org.northernarc.loanemi.dto.EmiCalculationResponseDTO;
import org.northernarc.loanemi.repository.CustomerRepository;
import org.northernarc.loanemi.repository.EmiPaymentRepository;
import org.northernarc.loanemi.repository.EmiScheduleRepository;
import org.northernarc.loanemi.repository.LoanRepository;
import org.northernarc.loanemi.serviceimpl.LoanServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EMI calculation functionality.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EMI Calculation Tests")
public class EmiCalculationServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EmiScheduleRepository emiScheduleRepository;

    @Mock
    private EmiPaymentRepository emiPaymentRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    private EmiCalculationRequestDTO calculationRequest;

    @BeforeEach
    void setUp() {
        calculationRequest = new EmiCalculationRequestDTO();
    }

    @Test
    @DisplayName("Calculate EMI with standard monthly compound interest formula")
    void testCalculateEmiStandard() {
        // Arrange
        calculationRequest.setPrincipalAmount(1000000.0);
        calculationRequest.setAnnualInterestRate(7.5);
        calculationRequest.setTenureMonths(240);

        // Act
        EmiCalculationResponseDTO response = loanService.calculateEmi(calculationRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getEmiAmount() > 0);
        assertTrue(response.getTotalInterest() > 0);
        assertTrue(response.getTotalPayment() > 0);
        
        // Total payment should be EMI * tenure
        assertEquals(response.getEmiAmount() * 240, response.getTotalPayment(), 0.01);
        
        // Total interest should be total payment - principal
        assertEquals(response.getTotalPayment() - 1000000, response.getTotalInterest(), 0.01);
    }

    @Test
    @DisplayName("Calculate EMI with zero interest rate")
    void testCalculateEmiWithZeroInterest() {
        // Arrange
        calculationRequest.setPrincipalAmount(500000.0);
        calculationRequest.setAnnualInterestRate(0.0);
        calculationRequest.setTenureMonths(60);

        // Act
        EmiCalculationResponseDTO response = loanService.calculateEmi(calculationRequest);

        // Assert
        assertNotNull(response);
        // EMI should be principal / tenure when interest is zero
        assertEquals(500000.0 / 60, response.getEmiAmount(), 0.01);
        assertEquals(500000.0, response.getTotalPayment(), 0.01);
        assertEquals(0.0, response.getTotalInterest(), 0.01);
    }

    @Test
    @DisplayName("Calculate EMI with high interest rate vs low interest rate")
    void testCalculateEmiWithHighInterestRate() {
        // Arrange
        calculationRequest.setPrincipalAmount(1000000.0);
        calculationRequest.setAnnualInterestRate(15.0);
        calculationRequest.setTenureMonths(60);

        // Act
        EmiCalculationResponseDTO response = loanService.calculateEmi(calculationRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getEmiAmount() > 0);
        assertTrue(response.getTotalInterest() > 0);
        assertTrue(response.getTotalPayment() > 1000000);
    }

    @Test
    @DisplayName("Calculate EMI with short tenure")
    void testCalculateEmiWithShortTenure() {
        // Arrange
        calculationRequest.setPrincipalAmount(100000.0);
        calculationRequest.setAnnualInterestRate(10.0);
        calculationRequest.setTenureMonths(12);

        // Act
        EmiCalculationResponseDTO response = loanService.calculateEmi(calculationRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getEmiAmount() > 0);
        assertEquals(response.getEmiAmount() * 12, response.getTotalPayment(), 0.01);
    }

    @Test
    @DisplayName("Calculate EMI with long tenure")
    void testCalculateEmiWithLongTenure() {
        // Arrange
        calculationRequest.setPrincipalAmount(3000000.0);
        calculationRequest.setAnnualInterestRate(6.5);
        calculationRequest.setTenureMonths(360);

        // Act
        EmiCalculationResponseDTO response = loanService.calculateEmi(calculationRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getEmiAmount() > 0);
        assertTrue(response.getTotalInterest() > 0);
        assertEquals(response.getEmiAmount() * 360, response.getTotalPayment(), 0.01);
    }

    @Test
    @DisplayName("EMI calculation returns consistent results")
    void testEmiCalculationConsistency() {
        // Arrange
        calculationRequest.setPrincipalAmount(1000000.0);
        calculationRequest.setAnnualInterestRate(7.5);
        calculationRequest.setTenureMonths(240);

        // Act - Calculate multiple times
        EmiCalculationResponseDTO response1 = loanService.calculateEmi(calculationRequest);
        EmiCalculationResponseDTO response2 = loanService.calculateEmi(calculationRequest);
        EmiCalculationResponseDTO response3 = loanService.calculateEmi(calculationRequest);

        // Assert - Results should be identical
        assertEquals(response1.getEmiAmount(), response2.getEmiAmount());
        assertEquals(response2.getEmiAmount(), response3.getEmiAmount());
        assertEquals(response1.getTotalInterest(), response2.getTotalInterest());
        assertEquals(response1.getTotalPayment(), response2.getTotalPayment());
    }

    @Test
    @DisplayName("EMI Request DTO validation")
    void testEmiCalculationRequestDTOValidation() {
        // Test that DTO accepts valid values
        EmiCalculationRequestDTO request = new EmiCalculationRequestDTO();
        request.setPrincipalAmount(100000.0);
        request.setAnnualInterestRate(10.0);
        request.setTenureMonths(60);

        assertNotNull(request.getPrincipalAmount());
        assertNotNull(request.getAnnualInterestRate());
        assertNotNull(request.getTenureMonths());
    }

    @Test
    @DisplayName("EMI Response DTO structure")
    void testEmiCalculationResponseDTOStructure() {
        // Arrange & Act
        EmiCalculationResponseDTO response = new EmiCalculationResponseDTO(10000.0, 50000.0, 150000.0);

        // Assert
        assertEquals(10000.0, response.getEmiAmount());
        assertEquals(50000.0, response.getTotalInterest());
        assertEquals(150000.0, response.getTotalPayment());
    }

    @Test
    @DisplayName("Calculate EMI matches expected mathematical formula")
    void testEmiCalculationFormula() {
        // EMI Formula: (P * R * (1+R)^N) / ((1+R)^N - 1)
        // Where: P = Principal, R = Monthly Rate, N = Tenure in months
        
        // Arrange
        double principal = 1000000.0;
        double annualRate = 7.5;
        int tenureMonths = 240;
        double monthlyRate = annualRate / (12 * 100);
        
        calculationRequest.setPrincipalAmount(principal);
        calculationRequest.setAnnualInterestRate(annualRate);
        calculationRequest.setTenureMonths(tenureMonths);

        // Act
        EmiCalculationResponseDTO response = loanService.calculateEmi(calculationRequest);

        // Assert - Calculate expected EMI manually
        double factor = Math.pow(1 + monthlyRate, tenureMonths);
        double expectedEmi = (principal * monthlyRate * factor) / (factor - 1);
        
        assertEquals(expectedEmi, response.getEmiAmount(), 0.01);
    }
}
