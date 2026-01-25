package com.taskmanagement.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.taskmanagement.model.ActivityLog;
import com.taskmanagement.model.User;
import com.taskmanagement.utils.CurrentUser;

public class ActivityLogService {

    private static final List<ActivityLog> activities = new ArrayList<>();

    // log activity with details
    public static void logActivity(String action, String entityType, Long entityId, String entityName, String details) {
        User actor = CurrentUser.getInstance();
        ActivityLog log = new ActivityLog(action, entityType, entityId, entityName, actor, details);
        activities.add(log);
    }

    // log activity without details
    public static void logActivity(String action, String entityType, Long entityId, String entityName) {
        logActivity(action, entityType, entityId, entityName, null);
    }

    // log task creation
    public static void logTaskCreated(Long taskId, String taskTitle) {
        logActivity("TASK_CREATED", "TASK", taskId, taskTitle);
    }

    // log task update
    public static void logTaskUpdated(Long taskId, String taskTitle, String changes) {
        logActivity("TASK_UPDATED", "TASK", taskId, taskTitle, changes);
    }

    // log task status change
    public static void logTaskStatusChanged(Long taskId, String taskTitle, String oldStatus, String newStatus) {
        logActivity("STATUS_CHANGED", "TASK", taskId, taskTitle, 
                String.format("%s -> %s", oldStatus, newStatus));
    }

    // log task assignment
    public static void logTaskAssigned(Long taskId, String taskTitle, String assigneeName) {
        logActivity("TASK_ASSIGNED", "TASK", taskId, taskTitle, "Assigned to: " + assigneeName);
    }

    // log task deletion
    public static void logTaskDeleted(Long taskId, String taskTitle) {
        logActivity("TASK_DELETED", "TASK", taskId, taskTitle);
    }

    public static void logProjectCreated(Long projectId, String projectName) {
        logActivity("PROJECT_CREATED", "PROJECT", projectId, projectName);
    }

    // log project updated
    public static void logProjectUpdated(Long projectId, String projectName, String changes) {
        logActivity("PROJECT_UPDATED", "PROJECT", projectId, projectName, changes);
    }

    // log project deleted
    public static void logProjectDeleted(Long projectId, String projectName) {
        logActivity("PROJECT_DELETED", "PROJECT", projectId, projectName);
    }

    // log for all activities
    public static List<ActivityLog> getAllActivities() {
        return new ArrayList<>(activities);
    }

    // recent activities
    public static List<ActivityLog> getRecentActivities(int count) {
        return activities.stream().skip(Math.max(0, activities.size() - count)) .collect(Collectors.toList());
    }

    // for checking activities by action
    public static List<ActivityLog> getActivitiesByAction(String action) {
        return activities.stream().filter(a -> action.equals(a.getAction())).collect(Collectors.toList());
    }

    // get activities by entity type
    public static List<ActivityLog> getActivitiesByEntity(String entityType) {
        return activities.stream().filter(a -> entityType.equals(a.getEntityType())).collect(Collectors.toList());
    }

    // get activities by user
    public static List<ActivityLog> getActivitiesByActor(Long userId) {
        return activities.stream().filter(a -> a.getUser() != null && a.getUser().getId().equals(userId)).collect(Collectors.toList());
    }

    // get history for specific entity
    public static List<ActivityLog> getEntityHistory(String entityType, Long entityId) {
        return activities.stream().filter(a -> entityType.equals(a.getEntityType()) && entityId.equals(a.getEntityId())).collect(Collectors.toList());
    }

    // clear all activities
    public static void clearActivities() {
        activities.clear();
    }

    // activity count
    public static int getActivityCount() {
        return activities.size();
    }
}
