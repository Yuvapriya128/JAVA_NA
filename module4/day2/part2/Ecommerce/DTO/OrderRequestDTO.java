package org.example.springdatajpademo.Ecommerce.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {

    // null means create new order
    private Integer orderId;

    @NotNull(message = "Customer is required")
    private Integer customerId;

    @NotNull(message = "Product is required")
    private Integer productId;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    private String status;
}