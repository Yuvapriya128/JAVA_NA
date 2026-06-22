package org.example.springdatajpademo.Ecommerce.DTO;

import lombok.Data;

@Data
public class OrderRequestDTO {
    private Integer customerId;
    private Integer productId;
    private Integer orderId;
    private int quantity;
}
