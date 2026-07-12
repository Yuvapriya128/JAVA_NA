package org.example.springdatajpademo.Ecommerce.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ecom_product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("product_id")
    private Integer id;

    @NotBlank(message = "Name is empty")
    private String name;

    @NotBlank(message = "brand is empty")
    private String brand;

    @NotBlank(message = "category is empty")
    private String category;

    @Positive(message = "cost greater than 0")
    private double cost;

    private String description;

    @PositiveOrZero(message = "stock must be 0 or greater")
    @Column(name = "stock", nullable = false)
    private Integer stock = 0;

    @PositiveOrZero(message = "minimum_stock must be 0 or greater")
    @Column(name = "minimum_stock", nullable = false)
    private Integer minimumStock = 10;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    @JsonManagedReference("product-orderitem")
    private List<OrderItem> orderItems;

}
