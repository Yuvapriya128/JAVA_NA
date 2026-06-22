package org.example.springdatajpademo.Ecommerce.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ecom_customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    @JsonProperty("customer_id")
    private Integer id;

    @NotBlank(message = "customer name is required")
    private String name;
    @Email(message = "invalid email format")
    private String email;
    @NotBlank(message = "address is required")
    private String address;

    @OneToMany(mappedBy = "customer",cascade = CascadeType.PERSIST)
    @JsonManagedReference("customer-order")
    private List<Order> orders;

}
