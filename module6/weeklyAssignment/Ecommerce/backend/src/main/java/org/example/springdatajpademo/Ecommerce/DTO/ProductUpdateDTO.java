package org.example.springdatajpademo.Ecommerce.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateDTO {
    private Integer id;

    @NotBlank(message = "Name is empty")
    private String name;

    @NotBlank(message = "Brand is empty")
    private String brand;

    @NotBlank(message = "Category is empty")
    private String category;

    @Positive(message = "Cost must be greater than 0")
    private double cost;

    private String description;

    @PositiveOrZero(message = "Stock must be 0 or greater")
    private Integer stock;

    @PositiveOrZero(message = "Minimum stock must be 0 or greater")
    private Integer minimumStock;

    private Boolean active;
}
