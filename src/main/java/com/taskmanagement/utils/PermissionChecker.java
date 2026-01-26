package com.taskmanagement.utils;

import com.taskmanagement.model.Project;
import com.taskmanagement.model.Task;

public class PermissionChecker {

    private PermissionChecker(){}

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
