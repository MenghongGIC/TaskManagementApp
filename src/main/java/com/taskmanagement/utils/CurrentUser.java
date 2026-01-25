package com.taskmanagement.utils;

import java.time.LocalDateTime;

import com.taskmanagement.model.Role;
import com.taskmanagement.model.User;
public class CurrentUser {

    private static volatile User instance;

    private CurrentUser() {}

    public static void set(User user) {
        instance = user;
    }
    public static User getInstance() {
        return instance;
    }

    public static boolean isLoggedIn() {
        return instance != null;
    }
    public static long getId() {
        if (instance != null) {
            Long id = instance.getId();
            return id != null ? id : -1L;
        }
        return -1L;
    }

    public static String getEmail(){
        return instance != null ? instance.getEmail() : "";
    }
    public static String getUsername() {
        return instance != null ? instance.getUsername() : "Guest";
    }
    public static String getRoleName() {
        return instance != null ? instance.getRoleDisplayName() : "Guest";
    }
    public static boolean isAdmin() {
        return instance != null && instance.getRole() == Role.ADMIN;
    }

    public static boolean isUser() {
        return instance != null && instance.getRole() == Role.USER;
    }

    public static boolean isGuest() {
        return instance == null || instance.getRole() == Role.USER;
    }

    public static boolean canViewOwnTasks()         { return safeRole().ViewOwnTasks(); }
    public static boolean canCompleteOwnTasks()     { return safeRole().CompleteOwnTasks(); }
    public static boolean canViewAllTasks()         { return safeRole().ViewAllTasks(); }
    public static boolean canAssignTasks()          { return safeRole().AssignTasks(); }
    public static boolean canCreateProjects()       { return safeRole().CreateProjects(); }
    public static boolean canDeleteProject()        { return safeRole().DeleteProject(); }
    public static boolean canManageTeams()          { return safeRole().ManageTeams(); }
    public static boolean canManageUsers()          { return safeRole().ManageUsers(); }
    public static boolean hasFullAccess()           { return safeRole().FullAccess(); }
    public static boolean canRegisterOrLogin()      { return safeRole().RegisterOrLogin(); }

    private static Role safeRole() {
        return instance != null ? instance.getRole() : Role.USER;
    }
    public static void clear() {
        instance = null;
    }
    public static void updateLastLogin(User user) {
        if (instance != null) {
            instance.setLastLogin(LocalDateTime.now());
        }
    }
}