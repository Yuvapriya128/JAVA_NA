package org.example.springdatajpademo.Ecommerce.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateDTO {
    @NotNull(message = "Order id is required")
    private Integer id;

    @NotNull(message = "Customer is required")
    private Integer customerId;

    private String status;

    @PositiveOrZero(message = "Total amount must be 0 or greater")
    private Double totalAmount;

    private String paymentMethod;

    private String paymentStatus;
}
