package com.taskmanagement.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.taskmanagement.model.ActivityLog;
import com.taskmanagement.model.User;
import com.taskmanagement.utils.CurrentUser;

public class ActivityLogService {

    // Activity Action Constants
    private static final String ACTION_TASK_CREATED = "TASK_CREATED";
    private static final String ACTION_TASK_UPDATED = "TASK_UPDATED";
    private static final String ACTION_TASK_DELETED = "TASK_DELETED";
    private static final String ACTION_STATUS_CHANGED = "STATUS_CHANGED";
    private static final String ACTION_TASK_ASSIGNED = "TASK_ASSIGNED";
    private static final String ACTION_PROJECT_CREATED = "PROJECT_CREATED";
    private static final String ACTION_PROJECT_UPDATED = "PROJECT_UPDATED";
    private static final String ACTION_PROJECT_DELETED = "PROJECT_DELETED";
    
    // Entity Type Constants
    private static final String ENTITY_TASK = "TASK";
    private static final String ENTITY_PROJECT = "PROJECT";
    
    // Detail Message Templates
    private static final String DETAIL_STATUS_CHANGE = "%s -> %s";
    private static final String DETAIL_ASSIGNED_TO = "Assigned to: ";

    private static final List<ActivityLog> activities = new ArrayList<>();

    public static void logActivity(String action, String entityType, Long entityId, String entityName, String details) {
        User actor = CurrentUser.getInstance();
        ActivityLog log = new ActivityLog(action, entityType, entityId, entityName, actor, details);
        activities.add(log);
    }

    public static void logActivity(String action, String entityType, Long entityId, String entityName) {
        logActivity(action, entityType, entityId, entityName, null);
    }

    public static void logTaskCreated(Long taskId, String taskTitle) {
        logActivity(ACTION_TASK_CREATED, ENTITY_TASK, taskId, taskTitle);
    }

    public static void logTaskUpdated(Long taskId, String taskTitle, String changes) {
        logActivity(ACTION_TASK_UPDATED, ENTITY_TASK, taskId, taskTitle, changes);
    }

    public static void logTaskStatusChanged(Long taskId, String taskTitle, String oldStatus, String newStatus) {
        logActivity(ACTION_STATUS_CHANGED, ENTITY_TASK, taskId, taskTitle, 
                String.format(DETAIL_STATUS_CHANGE, oldStatus, newStatus));
    }

    public static void logTaskAssigned(Long taskId, String taskTitle, String assigneeName) {
        logActivity(ACTION_TASK_ASSIGNED, ENTITY_TASK, taskId, taskTitle, DETAIL_ASSIGNED_TO + assigneeName);
    }

    public static void logTaskDeleted(Long taskId, String taskTitle) {
        logActivity(ACTION_TASK_DELETED, ENTITY_TASK, taskId, taskTitle);
    }

    public static void logProjectCreated(Long projectId, String projectName) {
        logActivity(ACTION_PROJECT_CREATED, ENTITY_PROJECT, projectId, projectName);
    }

    public static void logProjectUpdated(Long projectId, String projectName, String changes) {
        logActivity(ACTION_PROJECT_UPDATED, ENTITY_PROJECT, projectId, projectName, changes);
    }

    public static void logProjectDeleted(Long projectId, String projectName) {
        logActivity(ACTION_PROJECT_DELETED, ENTITY_PROJECT, projectId, projectName);
    }

    public static List<ActivityLog> getAllActivities() {
        return new ArrayList<>(activities);
    }

    public static List<ActivityLog> getRecentActivities(int count) {
        return activities.stream()
                .skip(Math.max(0, activities.size() - count))
                .collect(Collectors.toList());
    }

    public static List<ActivityLog> getActivitiesByAction(String action) {
        return activities.stream()
                .filter(a -> action.equals(a.getAction()))
                .collect(Collectors.toList());
    }
    public static List<ActivityLog> getActivitiesByEntity(String entityType) {
        return activities.stream()
                .filter(a -> entityType.equals(a.getEntityType()))
                .collect(Collectors.toList());
    }
    public static List<ActivityLog> getActivitiesByActor(Long userId) {
        return activities.stream()
                .filter(a -> a.getUser() != null && a.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }
    public static List<ActivityLog> getEntityHistory(String entityType, Long entityId) {
        return activities.stream()
                .filter(a -> entityType.equals(a.getEntityType()) && entityId.equals(a.getEntityId()))
                .collect(Collectors.toList());
    }
    public static void clearActivities() {
        activities.clear();
    }
    public static int getActivityCount() {
        return activities.size();
    }
}
