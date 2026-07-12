package org.example.springdatajpademo.Ecommerce.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemRequestDTO {
    @NotNull(message = "Order is required")
    private Integer orderId;

    @NotNull(message = "Product is required")
    private Integer productId;

    @Min(value = 1, message = "Quantity must be atleast 1")
    private int quantity;

    @PositiveOrZero(message = "Unit price must be 0 or greater")
    private Double unitPrice;
}
