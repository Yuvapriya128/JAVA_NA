package org.example.springdatajpademo.Ecommerce.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @OneToMany(mappedBy = "product",cascade = CascadeType.PERSIST)
    @JsonManagedReference("product-orderitem")
    private List<OrderItem> orderItems;



}
