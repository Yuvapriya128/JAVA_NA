package org.example.springdatajpademo.Ecommerce.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponseDTO {
    private Integer id;

    private Integer orderId;

    private Integer productId;

    private String productName;

    private int quantity;

    private double productCost;

    private double totalPrice;

    private Double unitPrice;

    private Double subtotal;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
