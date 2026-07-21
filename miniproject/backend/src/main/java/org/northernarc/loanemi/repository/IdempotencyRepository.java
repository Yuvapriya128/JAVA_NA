package org.northernarc.loanemi.repository;

import org.northernarc.loanemi.model.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord, Long> {
    
    Optional<IdempotencyRecord> findByIdempotencyKey(String idempotencyKey);
    
    boolean existsByIdempotencyKey(String idempotencyKey);
    
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
