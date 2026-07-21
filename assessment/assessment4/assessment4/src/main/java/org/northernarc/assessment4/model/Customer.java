package org.northernarc.assessment4.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
   @Column(name = "customer_id", unique = true, nullable = false)
    private Long customerId;

    @Column(name = "customer_name", nullable = false)
    @NotBlank(message = "Customer name is required")
    private String customerName;

    @Column(name = "email", unique = true, nullable = false)
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    // Legacy schema compatibility: non-null credit_score exists in DB.
    @Column(name = "credit_score", nullable = false)
    private Integer creditScore = 750;

    @Column(name = "branch")
    private String branch;

    // Legacy schema compatibility: DB still has NOT NULL city column.
    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "password", unique = true)
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    List<Account> accounts = new ArrayList<>();

    public void setRole(String role) {
        this.role = role == null || role.isBlank() ? Role.USER : Role.valueOf(role.toUpperCase());
    }

    public void setBranch(String branch) {
        this.branch = branch;
        this.city = branch;
    }

    public void setCity(String city) {
        this.city = city;
        if (this.branch == null || this.branch.isBlank()) {
            this.branch = city;
        }
    }

    @PrePersist
    @PreUpdate
    private void syncCityAndBranch() {
        if (creditScore == null || creditScore < 300 || creditScore > 900) {
            creditScore = 750;
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            phoneNumber = "9" + String.format("%09d", Math.abs((int) (System.nanoTime() % 1_000_000_000L)));
        }
        if ((city == null || city.isBlank()) && branch != null && !branch.isBlank()) {
            city = branch;
        }
        if ((branch == null || branch.isBlank()) && city != null && !city.isBlank()) {
            branch = city;
        }
    }
}