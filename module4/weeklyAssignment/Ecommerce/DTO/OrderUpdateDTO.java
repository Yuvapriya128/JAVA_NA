package org.example.springdatajpademo.Ecommerce.DTO;

import jakarta.validation.constraints.NotNull;
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
}
