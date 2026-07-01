package org.example.loanemimgmt.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.loanemimgmt.enums.UserRole;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String customerName;

    @NotBlank
    @Email
    @Size(max = 120)
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Size(min = 6, max = 120)
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Size(min = 10, max = 15)
    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String city;

    @NotNull
    @Min(300)
    @Max(900)
    @Builder.Default
    @Column(nullable = false)
    private Integer creditScore = 300;

    @NotNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Builder.Default
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Loan> loans = new ArrayList<>();

    public void addLoan(Loan loan) {
        if (loan == null || loans.contains(loan)) {
            return;
        }
        loans.add(loan);
        loan.setCustomer(this);
    }

    public void removeLoan(Loan loan) {
        if (loan == null) {
            return;
        }
        loans.remove(loan);
    }
}
