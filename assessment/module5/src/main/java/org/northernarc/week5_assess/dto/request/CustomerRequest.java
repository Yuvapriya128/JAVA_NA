package org.northernarc.week5_assess.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {
    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @Pattern(regexp = "\\d{10}")
    private String phone;
}

