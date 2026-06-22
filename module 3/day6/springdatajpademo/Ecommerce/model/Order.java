package org.example.springdatajpademo.Ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ecom_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    @JsonProperty("order_id")
    private Integer id;


    @ManyToOne
    @JsonBackReference("customer-order")
    @NotNull(message = "customer is required")
    private Customer customer;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    @JsonManagedReference("order-orderitem")
    private List<OrderItem> orderItems;

}
