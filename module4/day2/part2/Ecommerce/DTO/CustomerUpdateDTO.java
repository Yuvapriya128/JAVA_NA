package org.example.springdatajpademo.Ecommerce.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerUpdateDTO {

    private Integer id;

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Email is required")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;
}
