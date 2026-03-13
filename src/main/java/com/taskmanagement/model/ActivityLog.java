package com.taskmanagement.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * ActivityLog entity for tracking user actions and system events.
 * 
 * Stores information about what action was performed, by whom, on what entity,
 * and when it happened. Can also store additional details about what changed.
 * 
 * @author TaskFlow System
 */
public class ActivityLog {
    
    private Long id;
    private ActionType action;           // Use enum for type safety
    private String entityType;           // "TASK", "SUBTASK", "USER"
    private Long entityId;               // ID of the affected entity
    private String entityName;           // Name/Title of the affected entity (for display)
    private User user;                   // User who performed the action
    private String details;              // Additional details: changes, old->new values, etc.
    private LocalDateTime timestamp;     // When the action occurred
    
    // For future use: before/after state tracking
    private String beforeState;          // Optional: JSON serialization of previous state
    private String afterState;           // Optional: JSON serialization of new state

    /**
     * Default constructor - sets timestamp to now
     */
    public ActivityLog() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor with basic info (no details)
     */
    public ActivityLog(ActionType action, String entityType, Long entityId, String entityName, User user) {
        this();
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityName = entityName;
        this.user = user;
    }

    /**
     * Constructor with details
     */
    public ActivityLog(ActionType action, String entityType, Long entityId, String entityName, User user, String details) {
        this(action, entityType, entityId, entityName, user);
        this.details = details;
    }

    /**
     * Constructor with before/after state (for comprehensive change tracking)
     */
    public ActivityLog(ActionType action, String entityType, Long entityId, String entityName, 
                      User user, String details, String beforeState, String afterState) {
        this(action, entityType, entityId, entityName, user, details);
        this.beforeState = beforeState;
        this.afterState = afterState;
    }

    // ===== GETTERS & SETTERS =====
    
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public ActionType getAction() { 
        return action; 
    }
    
    public void setAction(ActionType action) { 
        this.action = action; 
    }
    
    /**
     * Get action as string (for database storage/compatibility)
     */
    public String getActionString() {
        return action != null ? action.name() : null;
    }

    public String getEntityType() { 
        return entityType; 
    }
    
    public void setEntityType(String entityType) { 
        this.entityType = entityType; 
    }

    public Long getEntityId() { 
        return entityId; 
    }
    
    public void setEntityId(Long entityId) { 
        this.entityId = entityId; 
    }

    public String getEntityName() { 
        return entityName; 
    }
    
    public void setEntityName(String entityName) { 
        this.entityName = entityName; 
    }

    public User getUser() { 
        return user; 
    }
    
    public void setUser(User user) { 
        this.user = user; 
    }

    public String getDetails() { 
        return details; 
    }
    
    public void setDetails(String details) { 
        this.details = details; 
    }

    public LocalDateTime getTimestamp() { 
        return timestamp; 
    }
    
    public void setTimestamp(LocalDateTime timestamp) { 
        this.timestamp = timestamp; 
    }

    public String getBeforeState() { 
        return beforeState; 
    }
    
    public void setBeforeState(String beforeState) { 
        this.beforeState = beforeState; 
    }

    public String getAfterState() { 
        return afterState; 
    }
    
    public void setAfterState(String afterState) { 
        this.afterState = afterState; 
    }

    /**
     * Get action category (for UI grouping/filtering)
     */
    public String getActionCategory() {
        return action != null ? action.getCategory() : "SYSTEM";
    }

    /**
     * Get human-readable action name
     */
    public String getActionDisplayName() {
        return action != null ? action.getDisplayName() : "Unknown";
    }

    /**
     * Get formatted timestamp for display
     */
    public String getFormattedTimestamp() {
        if (timestamp == null) return "";
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // ===== OBJECT METHODS =====

    @Override
    public String toString() {
        return String.format("[%s] %s - %s '%s' by %s%s",
                getFormattedTimestamp(),
                getActionDisplayName(),
                entityType,
                entityName,
                user != null ? user.getUsername() : "System",
                details != null ? " (" + details + ")" : ""
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
