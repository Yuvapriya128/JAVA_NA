package org.example.springdatajpademo.Ecommerce.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {

    private Integer id;
    private String name;
    private String brand;
    private String category;
    private double cost;

    private String description;
    private Integer stock;
    private Integer minimumStock;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
