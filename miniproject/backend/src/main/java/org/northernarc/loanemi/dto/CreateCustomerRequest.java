package org.northernarc.loanemi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCustomerRequest {

    @NotBlank
    private String customerName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @Pattern(regexp = "\\d{10}", message = "phoneNumber must have exactly 10 digits")
    private String phoneNumber;

    @NotBlank
    private String city;

    @Min(0)
    @Max(900)
    private Integer creditScore;

    private String role;

}
