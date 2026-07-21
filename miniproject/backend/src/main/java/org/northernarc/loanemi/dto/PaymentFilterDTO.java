package org.northernarc.loanemi.dto;

public class PaymentFilterDTO {
    private String referenceNumber;
    private String paymentMode;
    private String startDate;
    private String endDate;
    private String status;

    public PaymentFilterDTO() {}

    public PaymentFilterDTO(String referenceNumber, String paymentMode, String startDate, String endDate, String status) {
        this.referenceNumber = referenceNumber;
        this.paymentMode = paymentMode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
