package org.northernarc.loanemi.dto;

public class LoanFilterDTO {
    private String loanType;
    private String loanStatus;
    private Double minInterestRate;
    private Double maxInterestRate;
    private Double minPrincipal;
    private Double maxPrincipal;
    private Integer tenure;

    public LoanFilterDTO() {}

    public LoanFilterDTO(String loanType, String loanStatus, Double minInterestRate, Double maxInterestRate,
                         Double minPrincipal, Double maxPrincipal, Integer tenure) {
        this.loanType = loanType;
        this.loanStatus = loanStatus;
        this.minInterestRate = minInterestRate;
        this.maxInterestRate = maxInterestRate;
        this.minPrincipal = minPrincipal;
        this.maxPrincipal = maxPrincipal;
        this.tenure = tenure;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }

    public Double getMinInterestRate() {
        return minInterestRate;
    }

    public void setMinInterestRate(Double minInterestRate) {
        this.minInterestRate = minInterestRate;
    }

    public Double getMaxInterestRate() {
        return maxInterestRate;
    }

    public void setMaxInterestRate(Double maxInterestRate) {
        this.maxInterestRate = maxInterestRate;
    }

    public Double getMinPrincipal() {
        return minPrincipal;
    }

    public void setMinPrincipal(Double minPrincipal) {
        this.minPrincipal = minPrincipal;
    }

    public Double getMaxPrincipal() {
        return maxPrincipal;
    }

    public void setMaxPrincipal(Double maxPrincipal) {
        this.maxPrincipal = maxPrincipal;
    }

    public Integer getTenure() {
        return tenure;
    }

    public void setTenure(Integer tenure) {
        this.tenure = tenure;
    }
}
