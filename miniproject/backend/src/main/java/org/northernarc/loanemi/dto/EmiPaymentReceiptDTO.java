package org.northernarc.loanemi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.northernarc.loanemi.enums.EmiStatus;
import org.northernarc.loanemi.enums.LoanType;
import org.northernarc.loanemi.enums.PaymentMode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmiPaymentReceiptDTO {
    private String receiptNumber;
    private LocalDateTime generatedAt;
    
    // EMI Details
    private Long emiId;
    private Integer installmentNumber;
    private LocalDate dueDate;
    private Double amountDue;
    private Double principalComponent;
    private Double interestComponent;
    private Double penaltyAmount;
    private EmiStatus emiStatus;
    
    // Payment Details
    private Double amountPaid;
    private LocalDate paymentDate;
    private PaymentMode paymentMode;
    private String referenceNumber;
    
    // Loan Details
    private Long loanId;
    private LoanType loanType;
    private Double loanPrincipal;
    private Double loanInterestRate;
    private Integer loanTenureMonths;
    private LocalDate disbursementDate;
    
    // Customer Details
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerCity;
}
