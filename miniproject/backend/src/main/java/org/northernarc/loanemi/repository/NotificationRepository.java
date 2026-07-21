package org.northernarc.loanemi.repository;

import org.northernarc.loanemi.enums.NotificationType;
import org.northernarc.loanemi.model.Customer;
import org.northernarc.loanemi.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    Page<Notification> findByCustomerOrderByTimestampDesc(Customer customer, Pageable pageable);
    
    Page<Notification> findByCustomerAndIsReadFalseOrderByTimestampDesc(Customer customer, Pageable pageable);
    
    Page<Notification> findByCustomerAndTypeOrderByTimestampDesc(Customer customer, NotificationType type, Pageable pageable);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.customer = :customer AND n.isRead = false")
    long countUnreadByCustomer(@Param("customer") Customer customer);
    
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.customer = :customer")
    int markAllAsReadByCustomer(@Param("customer") Customer customer);
    
    List<Notification> findByCustomerAndRelatedEntityTypeAndRelatedEntityIdOrderByTimestampDesc(
            Customer customer, String relatedEntityType, Long relatedEntityId);
}
