package com.taskmanagement.model;

/**
 * Enum for all possible activity log action types.
 * This provides type safety and makes the system more extensible.
 * 
 * Organized by category:
 * - TASK_*: Task-related actions
 * - SUBTASK_*: Subtask/Checklist-related actions
 * - USER_*: User-related actions
 * 
 * This can be easily extended with new action types without modifying existing code.
 */
public enum ActionType {
    
    // ===== TASK ACTIONS =====
    TASK_CREATED("Task Created"),
    TASK_UPDATED("Task Updated"),
    TASK_DELETED("Task Deleted"),
    TASK_STATUS_CHANGED("Task Status Changed"),
    TASK_ASSIGNED("Task Assigned"),
    TASK_REASSIGNED("Task Reassigned"),
    TASK_UNASSIGNED("Task Unassigned"),
    TASK_PRIORITY_CHANGED("Task Priority Changed"),
    TASK_DUE_DATE_CHANGED("Task Due Date Changed"),
    TASK_COMMENT_ADDED("Comment Added"),
    TASK_COMMENT_EDITED("Comment Edited"),
    TASK_COMMENT_DELETED("Comment Deleted"),
    TASK_LABEL_ADDED("Label Added"),
    TASK_LABEL_REMOVED("Label Removed"),
    
    // ===== SUBTASK/CHECKLIST ACTIONS =====
    SUBTASK_CREATED("Subtask Created"),
    SUBTASK_COMPLETED("Subtask Completed"),
    SUBTASK_INCOMPLETE("Subtask Marked Incomplete"),
    SUBTASK_DELETED("Subtask Deleted"),
    SUBTASK_UPDATED("Subtask Updated"),
    
    // ===== USER ACTIONS =====
    USER_LOGGED_IN("User Logged In"),
    USER_LOGGED_OUT("User Logged Out"),
    USER_CREATED("User Created"),
    USER_UPDATED("User Updated"),
    USER_DELETED("User Deleted"),
    USER_ROLE_CHANGED("User Role Changed"),
    
    // ===== BATCH ACTIONS =====
    BULK_TASK_STATUS_UPDATE("Bulk Status Update"),
    BULK_TASK_DELETE("Bulk Delete"),
    
    // ===== SYSTEM ACTIONS =====
    SYSTEM_NOTE("System Note");
    
    private final String displayName;
    
    ActionType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get ActionType from string for database compatibility
     */
    public static ActionType fromString(String value) {
        try {
            return ActionType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SYSTEM_NOTE;
        }
    }
    
    /**
     * Get the category of this action (TASK, SUBTASK, USER, BATCH, SYSTEM)
     */
    public String getCategory() {
        String name = this.name();
        if (name.startsWith("TASK_")) return "TASK";
        if (name.startsWith("SUBTASK_")) return "SUBTASK";
        if (name.startsWith("USER_")) return "USER";
        if (name.startsWith("BULK_")) return "BATCH";
        return "SYSTEM";
    }
}
