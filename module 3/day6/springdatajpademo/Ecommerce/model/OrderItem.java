package org.example.springdatajpademo.Ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Min(value =1,message = "Quantity must be atleast 1")
    private int quantity;

    @ManyToOne
    @JsonBackReference("product-orderitem")
    @NotNull(message = "Product is required,Add Product")
    private Product product;

    @ManyToOne
    @JsonBackReference("order-orderitem")
    @NotNull(message = "Order is required")
    private Order order;

}
