package org.northernarc.loanemi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.northernarc.loanemi.enums.LoanType;

public class CreateLoanRequest {

    @NotNull
    private LoanType loanType;

    @Positive
    private Double principalAmount;

    @Positive
    private Double annualInterestRate;

    @Positive
    private Integer tenureMonths;

    @NotNull
    private Long customerId;

    /**
     * Optional: Link this loan to an approved application.
     * If provided, the application will be marked as LOAN_CREATED.
     */
    private Long applicationId;

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType == null ? null : LoanType.valueOf(loanType.trim().toUpperCase());
    }

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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
}
