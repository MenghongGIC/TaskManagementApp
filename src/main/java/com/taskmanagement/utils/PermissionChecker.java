package com.taskmanagement.utils;

import com.taskmanagement.model.Task;

public class PermissionChecker {

    private PermissionChecker(){}

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
        return false;
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
        return false;
    }
}
