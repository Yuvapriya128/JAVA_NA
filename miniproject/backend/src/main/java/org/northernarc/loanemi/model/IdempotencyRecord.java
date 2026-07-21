package org.northernarc.loanemi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity to store idempotency keys for preventing duplicate operations.
 */
@Entity
@Table(name = "loan_idempotency_key", indexes = {
    @Index(name = "idx_idempotency_key", columnList = "idempotencyKey", unique = true)
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdempotencyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String idempotencyKey;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String operation;

    @Column(length = 64)
    private String payloadHash;

    @Column
    private Long resultEntityId;

    @Lob
    @Column
    private String resultJson;

    @Column(nullable = false)
    private Integer httpStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime expiresAt;

    public IdempotencyRecord(String idempotencyKey, String userId, String operation, 
                             String payloadHash, Long resultEntityId, String resultJson, Integer httpStatus) {
        this.idempotencyKey = idempotencyKey;
        this.userId = userId;
        this.operation = operation;
        this.payloadHash = payloadHash;
        this.resultEntityId = resultEntityId;
        this.resultJson = resultJson;
        this.httpStatus = httpStatus;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusHours(24); // 24 hour expiry
    }
}
