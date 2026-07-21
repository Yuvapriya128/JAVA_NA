package org.northernarc.loanemi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.northernarc.loanemi.dto.NotificationDTO;
import org.northernarc.loanemi.enums.NotificationType;
import org.northernarc.loanemi.exception.CustomerNotFoundException;
import org.northernarc.loanemi.model.Customer;
import org.northernarc.loanemi.model.Notification;
import org.northernarc.loanemi.repository.CustomerRepository;
import org.northernarc.loanemi.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Notification management APIs")
public class NotificationController {
    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationRepository notificationRepository;
    private final CustomerRepository customerRepository;

    public NotificationController(NotificationRepository notificationRepository, 
                                   CustomerRepository customerRepository) {
        this.notificationRepository = notificationRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get notifications")
    public Page<NotificationDTO> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            @RequestParam(required = false) String type,
            Authentication authentication) {
        log.info("Get notifications requested unreadOnly={} type={} for email={}", unreadOnly, type, authentication.getName());
        
        Customer customer = customerRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Notification> notifications;
        if (type != null && !type.isBlank()) {
            try {
                NotificationType notificationType = NotificationType.valueOf(type.toUpperCase());
                notifications = notificationRepository.findByCustomerAndTypeOrderByTimestampDesc(customer, notificationType, pageable);
            } catch (IllegalArgumentException e) {
                notifications = unreadOnly 
                        ? notificationRepository.findByCustomerAndIsReadFalseOrderByTimestampDesc(customer, pageable)
                        : notificationRepository.findByCustomerOrderByTimestampDesc(customer, pageable);
            }
        } else {
            notifications = unreadOnly 
                    ? notificationRepository.findByCustomerAndIsReadFalseOrderByTimestampDesc(customer, pageable)
                    : notificationRepository.findByCustomerOrderByTimestampDesc(customer, pageable);
        }
        
        return notifications.map(this::mapToDTO);
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get unread notification count")
    public Map<String, Long> getUnreadCount(Authentication authentication) {
        log.info("Get unread count requested for email={}", authentication.getName());
        
        Customer customer = customerRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        
        long unreadCount = notificationRepository.countUnreadByCustomer(customer);
        return Map.of("unreadCount", unreadCount);
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Mark notification as read")
    public NotificationDTO markAsRead(@PathVariable Long id, Authentication authentication) {
        log.info("Mark notification {} as read for email={}", id, authentication.getName());
        
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
        
        // Verify ownership
        if (!notification.getCustomer().getEmail().equals(authentication.getName())) {
            throw new IllegalAccessError("Not authorized to modify this notification");
        }
        
        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);
        return mapToDTO(saved);
    }

    @PutMapping("/mark-all-read")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Mark all notifications as read")
    public Map<String, Integer> markAllAsRead(Authentication authentication) {
        log.info("Mark all notifications as read for email={}", authentication.getName());
        
        Customer customer = customerRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        
        int count = notificationRepository.markAllAsReadByCustomer(customer);
        return Map.of("markedAsRead", count);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Delete notification")
    public void deleteNotification(@PathVariable Long id, Authentication authentication) {
        log.info("Delete notification {} for email={}", id, authentication.getName());
        
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
        
        // Verify ownership
        if (!notification.getCustomer().getEmail().equals(authentication.getName())) {
            throw new IllegalAccessError("Not authorized to delete this notification");
        }
        
        notificationRepository.delete(notification);
    }

    private NotificationDTO mapToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setType(notification.getType().name());
        dto.setMessage(notification.getMessage());
        dto.setTimestamp(notification.getTimestamp());
        dto.setRead(notification.isRead());
        return dto;
    }
}
