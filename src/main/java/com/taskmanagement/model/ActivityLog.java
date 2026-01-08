package com.taskmanagement.model;

import java.time.LocalDateTime;
import java.util.Objects;

//Represents activity log entry for tracking task, project, and user changes

public class ActivityLog {
    private Long id;
    private String action; // e.g., "TASK_CREATED", "TASK_UPDATED", "TASK_ASSIGNED", "COMMENT_ADDED"
    private String entityType; // "TASK", "PROJECT", "COMMENT", "USER"
    private Long entityId;
    private String entityName;
    private User user; // User who performed the action
    private String details; // Additional details about the change
    private LocalDateTime timestamp;

    public ActivityLog() {
        this.timestamp = LocalDateTime.now();
    }

    public ActivityLog(String action, String entityType, Long entityId, String entityName, User user) {
        this();
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityName = entityName;
        this.user = user;
    }

    public ActivityLog(String action, String entityType, Long entityId, String entityName, User user, String details) {
        this(action, entityType, entityId, entityName, user);
        this.details = details;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] %s %s '%s' by %s%s",
                timestamp.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                action,
                entityType,
                entityName,
                user != null ? user.getUsername() : "System",
                details != null ? " - " + details : ""
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActivityLog that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
