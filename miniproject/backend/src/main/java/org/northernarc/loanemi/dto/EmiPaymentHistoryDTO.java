package org.northernarc.loanemi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class EmiPaymentHistoryDTO {
    
    @JsonProperty("emiId")
    private Long emiId;
    
    @JsonProperty("amountPaid")
    private Double amountPaid;
    
    @JsonProperty("paymentMode")
    private String paymentMode;
    
    @JsonProperty("referenceNumber")
    private String referenceNumber;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("penalty")
    private Double penalty;
    
    @JsonProperty("daysPastDue")
    private Integer daysPastDue;
    
    @JsonProperty("paymentDate")
    private LocalDate paymentDate;
    
    @JsonProperty("customerId")
    private Long customerId;
    
    @JsonProperty("customerName")
    private String customerName;

    // Constructor without customer fields (backward compatibility)
    public EmiPaymentHistoryDTO(Long emiId, Double amountPaid, String paymentMode, 
                                 String referenceNumber, String status, Double penalty, 
                                 Integer daysPastDue, LocalDate paymentDate) {
        this.emiId = emiId;
        this.amountPaid = amountPaid;
        this.paymentMode = paymentMode;
        this.referenceNumber = referenceNumber;
        this.status = status;
        this.penalty = penalty;
        this.daysPastDue = daysPastDue;
        this.paymentDate = paymentDate;
        this.customerId = null;
        this.customerName = null;
    }

    // Full constructor with customer fields
    public EmiPaymentHistoryDTO(Long emiId, Double amountPaid, String paymentMode, 
                                 String referenceNumber, String status, Double penalty, 
                                 Integer daysPastDue, LocalDate paymentDate,
                                 Long customerId, String customerName) {
        this.emiId = emiId;
        this.amountPaid = amountPaid;
        this.paymentMode = paymentMode;
        this.referenceNumber = referenceNumber;
        this.status = status;
        this.penalty = penalty;
        this.daysPastDue = daysPastDue;
        this.paymentDate = paymentDate;
        this.customerId = customerId;
        this.customerName = customerName;
    }
}
