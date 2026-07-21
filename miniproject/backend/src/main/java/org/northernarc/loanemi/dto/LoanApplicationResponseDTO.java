package org.northernarc.loanemi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplicationResponseDTO {

    private Long applicationId;
    private String loanType;
    private String status;
    private Long customerId;
    private LocalDateTime appliedAt;
    private String message;
}
