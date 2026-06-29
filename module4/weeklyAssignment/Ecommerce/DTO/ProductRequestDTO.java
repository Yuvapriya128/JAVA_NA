package org.example.springdatajpademo.Ecommerce.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {

    @NotBlank(message = "Name is empty")
    private String name;

    @NotBlank(message = "Brand is empty")
    private String brand;

    @NotBlank(message = "Category is empty")
    private String category;

    @Positive(message = "Cost must be greater than 0")
    private double cost;
}