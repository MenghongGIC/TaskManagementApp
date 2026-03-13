package com.taskmanagement.service;

import java.time.LocalDateTime;
import java.util.List;

import com.taskmanagement.model.ActionType;
import com.taskmanagement.model.ActivityLog;
import com.taskmanagement.model.User;
import com.taskmanagement.repository.ActivityLogRepository;
import com.taskmanagement.utils.CurrentUser;

/**
 * Service for managing activity logs.
 * 
 * This service handles logging all user actions and system events.
 * It provides a clean API for controllers and services to log activities
 * without needing to know about database details.
 * 
 * Best practices:
 * - Call logging methods immediately after actions succeed
 * - Use specific log methods (e.g., logTaskCreated) rather than generic logActivity
 * - Include meaningful details about what changed
 * - Don't log redundant actions (e.g., don't log if the save fails)
 * 
 * @author TaskFlow System
 */
public class ActivityLogService {

    private static final ActivityLogRepository repository = new ActivityLogRepository();

    // ===== TASK ACTION LOGGING =====

    /**
     * Log when a task is created
     */
    public static void logTaskCreated(Long taskId, String taskTitle, String priority, Long assigneeId) {
        String details = String.format("Priority: %s%s",
                priority,
                assigneeId != null ? ", Assigned to ID: " + assigneeId : "");
        logActivity(ActionType.TASK_CREATED, "TASK", taskId, taskTitle, details);
    }

    /**
     * Log when a task is updated
     * Include what changed in the details parameter
     */
    public static void logTaskUpdated(Long taskId, String taskTitle, String changes) {
        if (changes == null || changes.isEmpty()) {
            changes = "Task details modified";
        }
        logActivity(ActionType.TASK_UPDATED, "TASK", taskId, taskTitle, changes);
    }

    /**
     * Log when a task status changes
     */
    public static void logTaskStatusChanged(Long taskId, String taskTitle, String oldStatus, String newStatus) {
        String details = String.format("%s → %s", oldStatus, newStatus);
        logActivity(ActionType.TASK_STATUS_CHANGED, "TASK", taskId, taskTitle, details);
    }

    /**
     * Log when a task is assigned to someone
     */
    public static void logTaskAssigned(Long taskId, String taskTitle, String assigneeName) {
        String details = "Assigned to: " + assigneeName;
        logActivity(ActionType.TASK_ASSIGNED, "TASK", taskId, taskTitle, details);
    }

    /**
     * Log when a task is reassigned from one user to another
     */
    public static void logTaskReassigned(Long taskId, String taskTitle, String previousAssignee, String newAssignee) {
        String details = String.format("%s → %s", previousAssignee, newAssignee);
        logActivity(ActionType.TASK_REASSIGNED, "TASK", taskId, taskTitle, details);
    }

    /**
     * Log when a task is unassigned
     */
    public static void logTaskUnassigned(Long taskId, String taskTitle, String previousAssignee) {
        String details = "Unassigned from: " + previousAssignee;
        logActivity(ActionType.TASK_UNASSIGNED, "TASK", taskId, taskTitle, details);
    }

    /**
     * Log when a task priority changes
     */
    public static void logTaskPriorityChanged(Long taskId, String taskTitle, String oldPriority, String newPriority) {
        String details = String.format("%s → %s", oldPriority, newPriority);
        logActivity(ActionType.TASK_PRIORITY_CHANGED, "TASK", taskId, taskTitle, details);
    }

    /**
     * Log when a task due date is changed
     */
    public static void logTaskDueDateChanged(Long taskId, String taskTitle, String oldDate, String newDate) {
        String details = String.format("%s → %s", oldDate, newDate);
        logActivity(ActionType.TASK_DUE_DATE_CHANGED, "TASK", taskId, taskTitle, details);
    }

    /**
     * Log when a task is deleted
     */
    public static void logTaskDeleted(Long taskId, String taskTitle) {
        logActivity(ActionType.TASK_DELETED, "TASK", taskId, taskTitle);
    }

    /**
     * Log when a comment is added to a task
     */
    public static void logCommentAdded(Long taskId, String taskTitle, String commentPreview) {
        String details = "Comment: " + (commentPreview != null ? commentPreview : "");
        logActivity(ActionType.TASK_COMMENT_ADDED, "TASK", taskId, taskTitle, details);
    }

    /**
     * Log when a comment is edited
     */
    public static void logCommentEdited(Long taskId, String taskTitle, String commentPreview) {
        String details = "Comment updated: " + (commentPreview != null ? commentPreview : "");
        logActivity(ActionType.TASK_COMMENT_EDITED, "TASK", taskId, taskTitle, details);
    }

    /**
     * Log when a comment is deleted
     */
    public static void logCommentDeleted(Long taskId, String taskTitle) {
        logActivity(ActionType.TASK_COMMENT_DELETED, "TASK", taskId, taskTitle);
    }

    /**
     * Log when a label is added to a task
     */
    public static void logLabelAdded(Long taskId, String taskTitle, String labelName) {
        String details = "Label added: " + labelName;
        logActivity(ActionType.TASK_LABEL_ADDED, "TASK", taskId, taskTitle, details);
    }

    /**
     * Log when a label is removed from a task
     */
    public static void logLabelRemoved(Long taskId, String taskTitle, String labelName) {
        String details = "Label removed: " + labelName;
        logActivity(ActionType.TASK_LABEL_REMOVED, "TASK", taskId, taskTitle, details);
    }

    // ===== SUBTASK/CHECKLIST ACTION LOGGING =====

    /**
     * Log when a subtask is created
     */
    public static void logSubtaskCreated(Long subtaskId, String subtaskTitle, Long parentTaskId) {
        String details = "Parent Task ID: " + parentTaskId;
        logActivity(ActionType.SUBTASK_CREATED, "SUBTASK", subtaskId, subtaskTitle, details);
    }

    /**
     * Log when a subtask is marked as complete
     */
    public static void logSubtaskCompleted(Long subtaskId, String subtaskTitle) {
        logActivity(ActionType.SUBTASK_COMPLETED, "SUBTASK", subtaskId, subtaskTitle);
    }

    /**
     * Log when a subtask is marked as incomplete
     */
    public static void logSubtaskIncomplete(Long subtaskId, String subtaskTitle) {
        logActivity(ActionType.SUBTASK_INCOMPLETE, "SUBTASK", subtaskId, subtaskTitle);
    }

    /**
     * Log when a subtask is deleted
     */
    public static void logSubtaskDeleted(Long subtaskId, String subtaskTitle) {
        logActivity(ActionType.SUBTASK_DELETED, "SUBTASK", subtaskId, subtaskTitle);
    }

    /**
     * Log when a subtask is updated
     */
    public static void logSubtaskUpdated(Long subtaskId, String subtaskTitle, String changes) {
        if (changes == null || changes.isEmpty()) {
            changes = "Subtask modified";
        }
        logActivity(ActionType.SUBTASK_UPDATED, "SUBTASK", subtaskId, subtaskTitle, changes);
    }

    // ===== USER ACTION LOGGING =====

    /**
     * Log when a user logs in
     * Call this in AuthService after successful login
     */
    public static void logUserLogin(Long userId, String username) {
        logActivity(ActionType.USER_LOGGED_IN, "USER", userId, username);
    }

    /**
     * Log when a user logs out
     * Call this in AuthService during logout
     */
    public static void logUserLogout(Long userId, String username) {
        logActivity(ActionType.USER_LOGGED_OUT, "USER", userId, username);
    }

    /**
     * Log when a user is created (by admin)
     */
    public static void logUserCreated(Long userId, String username, String email, String role) {
        String details = String.format("Email: %s, Role: %s", email, role);
        logActivity(ActionType.USER_CREATED, "USER", userId, username, details);
    }

    /**
     * Log when a user is updated
     */
    public static void logUserUpdated(Long userId, String username, String changes) {
        if (changes == null || changes.isEmpty()) {
            changes = "User profile updated";
        }
        logActivity(ActionType.USER_UPDATED, "USER", userId, username, changes);
    }

    /**
     * Log when a user is deleted
     */
    public static void logUserDeleted(Long userId, String username) {
        logActivity(ActionType.USER_DELETED, "USER", userId, username);
    }

    /**
     * Log when a user role is changed
     */
    public static void logUserRoleChanged(Long userId, String username, String oldRole, String newRole) {
        String details = String.format("%s → %s", oldRole, newRole);
        logActivity(ActionType.USER_ROLE_CHANGED, "USER", userId, username, details);
    }

    // ===== BATCH OPERATION LOGGING =====

    /**
     * Log when multiple tasks are updated in bulk
     */
    public static void logBulkStatusUpdate(int taskCount, String oldStatus, String newStatus) {
        String details = String.format("%d tasks: %s → %s", taskCount, oldStatus, newStatus);
        logActivity(ActionType.BULK_TASK_STATUS_UPDATE, "TASK", null, "Bulk Operations", details);
    }

    /**
     * Log when multiple tasks are deleted
     */
    public static void logBulkTaskDelete(int taskCount) {
        String details = String.format("%d tasks deleted", taskCount);
        logActivity(ActionType.BULK_TASK_DELETE, "TASK", null, "Bulk Delete", details);
    }

    // ===== CORE LOGGING METHOD =====

    /**
     * Core logging method - all other logging methods call this
     * Validates inputs and creates activity log entry
     */
    private static void logActivity(ActionType action, String entityType, Long entityId, 
                                   String entityName, String details) {
        try {
            // Validation
            if (action == null || entityType == null || entityName == null) {
                System.err.println("ActivityLogService: Invalid logging parameters - skipping log");
                return;
            }

            User actor = CurrentUser.getInstance();
            ActivityLog log = new ActivityLog(action, entityType, entityId, entityName, actor, details);
            
            // Save to database
            repository.save(log);
            
        } catch (Exception e) {
            // Don't let logging errors crash the application
            System.err.println("Failed to save activity log: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Overload without details
     */
    private static void logActivity(ActionType action, String entityType, Long entityId, String entityName) {
        logActivity(action, entityType, entityId, entityName, null);
    }

    // ===== QUERY METHODS =====

    /**
     * Get all activity logs, ordered by most recent
     */
    public static List<ActivityLog> getAllActivities() {
        return repository.findAll();
    }

    /**
     * Get recent activities (limited count)
     */
    public static List<ActivityLog> getRecentActivities(int count) {
        return repository.findRecent(Math.max(1, count)); // Prevent zero or negative counts
    }

    /**
     * Get activities for a specific action type
     */
    public static List<ActivityLog> getActivitiesByAction(ActionType action) {
        if (action == null) return List.of();
        return repository.findByAction(action.name());
    }

    /**
     * Get all activities for a specific entity (task, subtask, or user)
     * FIX: Previously was calling findByAction instead of findByEntity
     */
    public static List<ActivityLog> getActivitiesByEntity(String entityType, Long entityId) {
        if (entityType == null || entityId == null) return List.of();
        return repository.findByEntity(entityType, entityId);
    }

    /**
     * Get all activities performed by a specific user
     */
    public static List<ActivityLog> getActivitiesByUser(Long userId) {
        if (userId == null) return List.of();
        return repository.findByUserId(userId);
    }

    /**
     * Get activity history for a specific task (all changes to that task)
     */
    public static List<ActivityLog> getTaskHistory(Long taskId) {
        return getActivitiesByEntity("TASK", taskId);
    }

    /**
     * Get activity history for a specific user (all their actions)
     * Same as getActivitiesByUser
     */
    public static List<ActivityLog> getUserHistory(Long userId) {
        return getActivitiesByUser(userId);
    }

    /**
     * Clear all activities (use cautiously - typically only for testing or cleanup)
     */
    public static void clearAllActivities() {
        repository.deleteAll();
    }

    /**
     * BACKWARD COMPATIBILITY: Old method name - use clearAllActivities instead
     */
    public static void clearActivities() {
        clearAllActivities();
    }

    /**
     * BACKWARD COMPATIBILITY: Old method that gets entity history
     * Use getTaskHistory(taskId) or getActivitiesByEntity(entityType, entityId) instead
     */
    public static List<ActivityLog> getEntityHistory(String entityType, Long entityId) {
        return getActivitiesByEntity(entityType, entityId);
    }

    /**
     * BACKWARD COMPATIBILITY: Old overload of logTaskCreated with 2 args
     * Use logTaskCreated(taskId, taskTitle, priority, assigneeId) for full logging
     */
    public static void logTaskCreated(Long taskId, String taskTitle) {
        logTaskCreated(taskId, taskTitle, null, null);
    }

    /**
     * Get count of total activities (useful for pagination)
     */
    public static int getActivityCount() {
        return repository.findAll().size();
    }

    /**
     * Convenience method to filter activities by category
     * e.g., "TASK", "USER", "SUBTASK"
     */
    public static List<ActivityLog> getActivitiesByCategory(String category) {
        // This would need implementation in repository
        // For now, get all activities and filter client-side
        List<ActivityLog> activities = getAllActivities();
        return activities;
    }
}
