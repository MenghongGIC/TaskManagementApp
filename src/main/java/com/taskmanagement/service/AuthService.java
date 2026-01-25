package com.taskmanagement.service;

import com.taskmanagement.model.User;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.utils.CurrentUser;

/**
 * Authentication service that delegates to UserService
 * Provides a unified interface for all authentication operations
 */
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;

    public AuthService() {
        this.userRepository = new UserRepository();
        this.userService = new UserService(userRepository);
    }

    /**
     * Authenticate a user with username and password
     * 
     * @param username the username
     * @param password the password
     * @return the authenticated user
     */
    public User login(String username, String password) {
        return userService.login(username, password);
    }

    /**
     * Logout the current user
     */
    public void logout() {
        userService.logout();
    }
    
    /**
     * Register a new user account
     * 
     * @param username the username
     * @param email the email address
     * @param password the password
     * @return the newly created user
     */
    public User register(String username, String email, String password) {
        return userService.register(username, email, password);
    }

    /**
     * Check if a user is currently logged in
     * 
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return CurrentUser.isLoggedIn();
    }

    /**
     * Get the currently logged-in user
     * 
     * @return the current user, or null if not logged in
     */
    public User getCurrentUser() {
        return CurrentUser.getInstance();
    }

    /**
     * Check if the current user has admin role
     * 
     * @return true if current user is admin, false otherwise
     */
    public boolean isAdmin() {
        return CurrentUser.isAdmin();
    }

    /**
     * Change password for the current user
     * 
     * @param oldPassword the current password
     * @param newPassword the new password
     * @throws IllegalStateException if no user is logged in
     * @throws IllegalArgumentException if password validation fails
     */
    public void changePassword(String oldPassword, String newPassword) {
        User user = CurrentUser.getInstance();
        if (user == null) {
            throw new IllegalStateException("No user logged in");
        }
        userService.changePassword(user, oldPassword, newPassword);
    }

    /**
     * Update the profile of the current user
     * 
     * @param updatedUser the user with updated information
     * @throws IllegalStateException if no user is logged in
     */
    public void updateProfile(User updatedUser) {
        User current = CurrentUser.getInstance();
        if (current == null) {
            throw new IllegalStateException("No user logged in");
        }
        updatedUser.setId(current.getId());
        userService.updateProfile(updatedUser);
    }
}
