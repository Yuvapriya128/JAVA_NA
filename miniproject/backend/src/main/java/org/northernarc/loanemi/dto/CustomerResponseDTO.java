package org.northernarc.loanemi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {
    private Long customerId;
    private String customerName;
    private String email;
    private String phoneNumber;
    private String city;
    private Integer creditScore;
    private String role;
    private boolean active;
}
