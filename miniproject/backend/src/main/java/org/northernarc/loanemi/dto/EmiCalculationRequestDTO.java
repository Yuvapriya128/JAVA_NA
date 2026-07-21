package org.northernarc.loanemi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for calculating EMI without creating a loan.
 * Contains the required parameters for EMI calculation.
 */
public class EmiCalculationRequestDTO {

    @NotNull(message = "Principal amount cannot be null")
    @Positive(message = "Principal amount must be greater than zero")
    private Double principalAmount;

    @NotNull(message = "Annual interest rate cannot be null")
    @Positive(message = "Annual interest rate must be greater than zero")
    private Double annualInterestRate;

    @NotNull(message = "Tenure months cannot be null")
    @Positive(message = "Tenure must be greater than zero")
    private Integer tenureMonths;

    // No-arg constructor
    public EmiCalculationRequestDTO() {
    }

    // Constructor with all fields
    public EmiCalculationRequestDTO(Double principalAmount, Double annualInterestRate, Integer tenureMonths) {
        this.principalAmount = principalAmount;
        this.annualInterestRate = annualInterestRate;
        this.tenureMonths = tenureMonths;
    }

    // Getters and Setters
    public Double getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(Double principalAmount) {
        this.principalAmount = principalAmount;
    }

    public Double getAnnualInterestRate() {
        return annualInterestRate;
    }

    public void setAnnualInterestRate(Double annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    @Override
    public String toString() {
        return "EmiCalculationRequestDTO{" +
                "principalAmount=" + principalAmount +
                ", annualInterestRate=" + annualInterestRate +
                ", tenureMonths=" + tenureMonths +
                '}';
    }
}
