package org.example.springdatajpademo.Ecommerce.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.springdatajpademo.Ecommerce.model.UserRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCustomerRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Role is required")
    private UserRole role;

    @Pattern(regexp = "^$|^\\+?[1-9]\\d{1,14}$", message = "Phone number must be valid or empty")
    private String phoneNumber;
}

