package org.northernarc.loanemi.dto;

import jakarta.validation.constraints.Positive;

/**
 * DTO for updating a pending loan application.
 */
public class UpdateApplicationRequestDTO {

    @Positive(message = "Principal amount must be positive")
    private Double principalAmount;

    @Positive(message = "Tenure must be positive")
    private Integer tenureMonths;

    @Positive(message = "Interest rate must be positive")
    private Double annualInterestRate;

    public UpdateApplicationRequestDTO() {
    }

    public UpdateApplicationRequestDTO(Double principalAmount, Integer tenureMonths, Double annualInterestRate) {
        this.principalAmount = principalAmount;
        this.tenureMonths = tenureMonths;
        this.annualInterestRate = annualInterestRate;
    }

    public Double getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(Double principalAmount) {
        this.principalAmount = principalAmount;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public Double getAnnualInterestRate() {
        return annualInterestRate;
    }

    public void setAnnualInterestRate(Double annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }
}
