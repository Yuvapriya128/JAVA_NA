package org.northernarc.loanemi.dto;

import org.northernarc.loanemi.enums.ApplicationEventType;

import java.time.LocalDateTime;

/**
 * DTO for application timeline events.
 */
public class ApplicationTimelineDTO {
    private Long id;
    private ApplicationEventType eventType;
    private String eventDisplayName;
    private LocalDateTime eventAt;
    private String actorRole;
    private String actorName;
    private String message;
    private String previousStatus;
    private String newStatus;

    public ApplicationTimelineDTO() {
    }

    public ApplicationTimelineDTO(Long id, ApplicationEventType eventType, LocalDateTime eventAt,
                                   String actorRole, String actorName, String message, 
                                   String previousStatus, String newStatus) {
        this.id = id;
        this.eventType = eventType;
        this.eventDisplayName = eventType != null ? eventType.getDisplayName() : null;
        this.eventAt = eventAt;
        this.actorRole = actorRole;
        this.actorName = actorName;
        this.message = message;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationEventType getEventType() {
        return eventType;
    }

    public void setEventType(ApplicationEventType eventType) {
        this.eventType = eventType;
        this.eventDisplayName = eventType != null ? eventType.getDisplayName() : null;
    }

    public String getEventDisplayName() {
        return eventDisplayName;
    }

    public void setEventDisplayName(String eventDisplayName) {
        this.eventDisplayName = eventDisplayName;
    }

    public LocalDateTime getEventAt() {
        return eventAt;
    }

    public void setEventAt(LocalDateTime eventAt) {
        this.eventAt = eventAt;
    }

    public String getActorRole() {
        return actorRole;
    }

    public void setActorRole(String actorRole) {
        this.actorRole = actorRole;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
}
