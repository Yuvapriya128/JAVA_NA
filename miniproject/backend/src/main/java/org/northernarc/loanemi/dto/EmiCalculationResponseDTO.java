package org.northernarc.loanemi.dto;

/**
 * DTO for EMI calculation response.
 * Returns calculated EMI values without persisting any data.
 */
public class EmiCalculationResponseDTO {

    private Double emiAmount;
    private Double totalInterest;
    private Double totalPayment;

    // No-arg constructor
    public EmiCalculationResponseDTO() {
    }

    // Constructor with all fields
    public EmiCalculationResponseDTO(Double emiAmount, Double totalInterest, Double totalPayment) {
        this.emiAmount = emiAmount;
        this.totalInterest = totalInterest;
        this.totalPayment = totalPayment;
    }

    // Getters and Setters
    public Double getEmiAmount() {
        return emiAmount;
    }

    public void setEmiAmount(Double emiAmount) {
        this.emiAmount = emiAmount;
    }

    public Double getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(Double totalInterest) {
        this.totalInterest = totalInterest;
    }

    public Double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(Double totalPayment) {
        this.totalPayment = totalPayment;
    }

    @Override
    public String toString() {
        return "EmiCalculationResponseDTO{" +
                "emiAmount=" + emiAmount +
                ", totalInterest=" + totalInterest +
                ", totalPayment=" + totalPayment +
                '}';
    }
}
