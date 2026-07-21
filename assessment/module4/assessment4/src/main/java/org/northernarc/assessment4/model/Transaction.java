package org.northernarc.assessment4.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "transaction_id", unique = true, nullable = false)
    private Long transactionId;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
//    @JoinColumn(name = "account_number", nullable = false)
//    @JsonBackReference
//    private Account account;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "account_number", nullable = false)
    @JsonBackReference("account-transactions")
    private Account account;

}
