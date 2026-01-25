package com.taskmanagement.utils;

import java.time.LocalDateTime;

import com.taskmanagement.model.Role;
import com.taskmanagement.model.User;

/**
 * Global holder for the currently logged-in user
 * Provides thread-safe singleton access to current user and permission delegation
 */
public class CurrentUser {

    private static volatile User instance;

    private CurrentUser() {
        // Static utility class
    }

    /**
     * Set the current user
     * 
     * @param user the user to set as current
     */
    public static void set(User user) {
        instance = user;
    }

    /**
     * Get the currently logged-in user
     * 
     * @return the current user, or null if not logged in
     */
    public static User getInstance() {
        return instance;
    }

    /**
     * Check if a user is logged in
     * 
     * @return true if user is logged in
     */
    public static boolean isLoggedIn() {
        return instance != null;
    }

    /**
     * Get the current user's ID
     * 
     * @return the user ID, or -1 if not logged in
     */
    public static long getId() {
        if (instance != null) {
            Long id = instance.getId();
            return id != null ? id : -1L;
        }
        return -1L;
    }

    /**
     * Get the current user's email
     * 
     * @return the email, or empty string if not logged in
     */
    public static String getEmail() {
        return instance != null ? instance.getEmail() : "";
    }

    /**
     * Get the current user's username
     * 
     * @return the username, or "Guest" if not logged in
     */
    public static String getUsername() {
        return instance != null ? instance.getUsername() : "Guest";
    }

    /**
     * Get the current user's role display name
     * 
     * @return the role name, or "Guest" if not logged in
     */
    public static String getRoleName() {
        return instance != null ? instance.getRoleDisplayName() : "Guest";
    }

    /**
     * Check if current user has admin role
     * 
     * @return true if user is admin
     */
    public static boolean isAdmin() {
        return instance != null && instance.getRole() == Role.ADMIN;
    }

    /**
     * Check if current user has user role
     * 
     * @return true if user has user role
     */
    public static boolean isUser() {
        return instance != null && instance.getRole() == Role.USER;
    }

    /**
     * Check if current user is guest (not logged in or has user role)
     * 
     * @return true if user is guest
     */
    public static boolean isGuest() {
        return instance == null || instance.getRole() == Role.USER;
    }

    // ===== Permission Delegation Methods =====
    /**
     * Delegate permission checks to the current user's role
     * 
     * @return true if current user/role can perform the action
     */
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

    /**
     * Get the safe role for permission checks (defaults to USER if not logged in)
     * 
     * @return the current user's role, or USER role if not logged in
     */
    private static Role safeRole() {
        return instance != null ? instance.getRole() : Role.USER;
    }

    /**
     * Logout by clearing the current user
     */
    public static void clear() {
        instance = null;
    }

    /**
     * Update the last login timestamp for the current user
     * 
     * @param user the user to update (typically the current user)
     */
    public static void updateLastLogin(User user) {
        if (instance != null) {
            instance.setLastLogin(LocalDateTime.now());
        }
    }
}