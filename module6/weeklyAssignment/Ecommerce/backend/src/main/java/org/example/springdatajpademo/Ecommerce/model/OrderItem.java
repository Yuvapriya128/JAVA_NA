package org.example.springdatajpademo.Ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ecom_orderitem")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderitem_id")
    @JsonProperty("orderitem_id")
    private Integer id;

    @Min(value = 1, message = "Quantity must be atleast 1")
    private int quantity;

    @PositiveOrZero(message = "unitPrice must be 0 or greater")
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice = 0.0;

    @PositiveOrZero(message = "subtotal must be 0 or greater")
    @Column(name = "subtotal", nullable = false)
    private Double subtotal = 0.0;

    @ManyToOne
    @JsonBackReference("product-orderitem")
    @NotNull(message = "Product is required,Add Product")
    private Product product;

    @ManyToOne
    @JsonBackReference("order-orderitem")
    @NotNull(message = "Order is required")
    private Order order;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void calculateSubtotal() {
        if (this.quantity > 0 && this.unitPrice != null) {
            this.subtotal = this.quantity * this.unitPrice;
        }
    }
}
