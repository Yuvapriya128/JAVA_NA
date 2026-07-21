package org.northernarc.loanemi.dto;

import java.time.LocalDate;

public class EmiScheduleDTO {
    private Long emiId;
    private Long loanId;
    private Integer emiNumber;
    private LocalDate emiDate;
    private Double principalComponent;
    private Double interestComponent;
    private Double totalEmi;
    private Double amountPaid;
    private String status;
    private Double penaltyAmount;
    private Integer daysPastDue;

    public EmiScheduleDTO() {}

    public EmiScheduleDTO(Long emiId, Long loanId, Integer emiNumber, LocalDate emiDate,
                         Double principalComponent, Double interestComponent, Double totalEmi,
                         Double amountPaid, String status, Double penaltyAmount, Integer daysPastDue) {
        this.emiId = emiId;
        this.loanId = loanId;
        this.emiNumber = emiNumber;
        this.emiDate = emiDate;
        this.principalComponent = principalComponent;
        this.interestComponent = interestComponent;
        this.totalEmi = totalEmi;
        this.amountPaid = amountPaid;
        this.status = status;
        this.penaltyAmount = penaltyAmount;
        this.daysPastDue = daysPastDue;
    }

    public Long getEmiId() {
        return emiId;
    }

    public void setEmiId(Long emiId) {
        this.emiId = emiId;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public Integer getEmiNumber() {
        return emiNumber;
    }

    public void setEmiNumber(Integer emiNumber) {
        this.emiNumber = emiNumber;
    }

    public LocalDate getEmiDate() {
        return emiDate;
    }

    public void setEmiDate(LocalDate emiDate) {
        this.emiDate = emiDate;
    }

    public Double getPrincipalComponent() {
        return principalComponent;
    }

    public void setPrincipalComponent(Double principalComponent) {
        this.principalComponent = principalComponent;
    }

    public Double getInterestComponent() {
        return interestComponent;
    }

    public void setInterestComponent(Double interestComponent) {
        this.interestComponent = interestComponent;
    }

    public Double getTotalEmi() {
        return totalEmi;
    }

    public void setTotalEmi(Double totalEmi) {
        this.totalEmi = totalEmi;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setPenaltyAmount(Double penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    public Integer getDaysPastDue() {
        return daysPastDue;
    }

    public void setDaysPastDue(Integer daysPastDue) {
        this.daysPastDue = daysPastDue;
    }
}
