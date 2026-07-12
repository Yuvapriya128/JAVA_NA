package org.example.springdatajpademo.Ecommerce.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Email is required")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Password is required")
    private String password;

    @Pattern(regexp = "^$|^\\+?[1-9]\\d{1,14}$", message = "Phone number must be valid or empty")
    private String phoneNumber;

    public CustomerRequestDTO(String name, String email, String address, String password) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.password = password;
        this.phoneNumber = null;
    }
}
