package com.taskmanagement.utils;

import com.taskmanagement.model.Project;
import com.taskmanagement.model.Task;

/**
 * Utility class for permission and access control checks across model classes.
 * Provides methods to verify user permissions for projects and tasks based on ownership and role.
 */
public class PermissionChecker {

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with static methods only.
     */
    private PermissionChecker() {
        // Utility class, no instantiation
    }

    // ============ Project Permission Methods ============

    /**
     * Check if current user can edit a project.
     * Allowed if: user is admin, or user created the project.
     *
     * @param project the project to check
     * @return true if current user can edit the project
     */
    public static boolean canEditProject(Project project) {
        if (!CurrentUser.isLoggedIn()) {
            return false;
        }
        if (CurrentUser.isAdmin()) {
            return true;
        }
        if (project.getCreatedBy() != null && project.getCreatedBy().getId().equals(CurrentUser.getId())) {
            return true;
        }
        return false;
    }

    /**
     * Check if current user can delete a project.
     * Allowed if: user is admin, or user created the project.
     *
     * @param project the project to check
     * @return true if current user can delete the project
     */
    public static boolean canDeleteProject(Project project) {
        if (!CurrentUser.isLoggedIn()) {
            return false;
        }
        if (CurrentUser.isAdmin()) {
            return true;
        }
        if (project.getCreatedBy() != null && project.getCreatedBy().getId().equals(CurrentUser.getId())) {
            return true;
        }
        return false;
    }

    // ============ Task Permission Methods ============

    /**
     * Check if current user can edit a task.
     * Allowed if: user is admin, user created the task, user is assigned to the task,
     * or user can edit the task's project.
     *
     * @param task the task to check
     * @return true if current user can edit the task
     */
    public static boolean canEditTask(Task task) {
        if (!CurrentUser.isLoggedIn()) {
            return false;
        }
        if (CurrentUser.isAdmin()) {
            return true;
        }
        if (task.getCreatedBy() != null && task.getCreatedBy().getId().equals(CurrentUser.getId())) {
            return true;
        }
        if (task.getAssignee() != null && task.getAssignee().getId().equals(CurrentUser.getId())) {
            return true;
        }
        return task.getProject() != null && canEditProject(task.getProject());
    }

    /**
     * Check if current user can delete a task.
     * Allowed if: user is admin, user created the task, or user can delete the task's project.
     *
     * @param task the task to check
     * @return true if current user can delete the task
     */
    public static boolean canDeleteTask(Task task) {
        if (!CurrentUser.isLoggedIn()) {
            return false;
        }
        if (CurrentUser.isAdmin()) {
            return true;
        }
        if (task.getCreatedBy() != null && task.getCreatedBy().getId().equals(CurrentUser.getId())) {
            return true;
        }
        return task.getProject() != null && canDeleteProject(task.getProject());
    }
}
