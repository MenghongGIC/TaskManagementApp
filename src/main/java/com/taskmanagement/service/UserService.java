    package com.taskmanagement.service;

import java.time.LocalDateTime;

import com.taskmanagement.model.Role;
import com.taskmanagement.model.User;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.utils.CurrentUser;

/**
 * Service for managing user operations including registration, login, and profile updates
 */
public class UserService {

    // Error Messages
    private static final String ERR_USERNAME_REQUIRED = "Username is required";
    private static final String ERR_PASSWORD_TOO_SHORT = "Password must be at least 8 characters";
    private static final String ERR_USERNAME_EXISTS = "Username already exists";
    private static final String ERR_USERNAME_PASSWORD_REQUIRED = "Username and password are required";
    private static final String ERR_INVALID_CREDENTIALS = "Invalid username or password";
    private static final String ERR_USER_NOT_FOUND = "User not found";
    private static final String ERR_INVALID_USER_DATA = "Invalid user data";
    private static final String ERR_WRONG_PASSWORD = "Current password is incorrect";
    private static final String ERR_ADMIN_ONLY = "Only admins can change user roles";
    private static final String ERR_ADMIN_VIEW_ONLY = "Only admins can view all users";

    // Constraints
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Register a new user account
     * 
     * @param username the username (required, must be unique)
     * @param email the email address
     * @param password the password (minimum 8 characters)
     * @return the newly created user
     * @throws IllegalArgumentException if validation fails
     */
    public User register(String username, String email, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException(ERR_USERNAME_REQUIRED);
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(ERR_PASSWORD_TOO_SHORT);
        }

        if (userRepository.findByUsername(username.trim()) != null) {
            throw new IllegalArgumentException(ERR_USERNAME_EXISTS);
        }

        User newUser = new User();
        newUser.setUsername(username.trim());
        newUser.setEmail(email != null ? email.trim() : null);
        newUser.setPasswordHash(password);
        newUser.setRole(Role.USER);
        newUser.setCreatedAt(LocalDateTime.now());

        return userRepository.save(newUser);
    }

    /**
     * Authenticate a user with username and password
     * Updates last login timestamp and sets as current user
     * 
     * @param username the username
     * @param password the password
     * @return the authenticated user
     * @throws IllegalArgumentException if credentials are invalid
     */
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null) {
            throw new IllegalArgumentException(ERR_USERNAME_PASSWORD_REQUIRED);
        }

        User user = userRepository.findByUsername(username.trim());
        if (user == null || !password.equals(user.getPasswordHash())) {
            throw new IllegalArgumentException(ERR_INVALID_CREDENTIALS);
        }
        userRepository.updateLastLogin(user);
        user = userRepository.findById(user.getId());
        CurrentUser.set(user);
        CurrentUser.updateLastLogin(user);
        
        return user;
    }

    /**
     * Logout the current user
     */
    public void logout() {
        CurrentUser.clear();
    }

    /**
     * Update user profile information
     * 
     * @param updatedUser the user with updated information
     * @return the updated user
     * @throws IllegalArgumentException if user data is invalid
     */
    public User updateProfile(User updatedUser) {
        if (updatedUser == null || updatedUser.getId() == null) {
            throw new IllegalArgumentException(ERR_INVALID_USER_DATA);
        }

        User existing = userRepository.findById(updatedUser.getId());
        if (existing == null) {
            throw new IllegalArgumentException(ERR_USER_NOT_FOUND);
        }
        existing.setEmail(updatedUser.getEmail());
        return userRepository.save(existing);
    }

    /**
     * Change password for a user
     * 
     * @param user the user whose password to change
     * @param oldPassword the current password (must match)
     * @param newPassword the new password (minimum 8 characters)
     * @throws IllegalArgumentException if passwords don't meet requirements
     */
    public void changePassword(User user, String oldPassword, String newPassword) {
        if (!oldPassword.equals(user.getPasswordHash())) {
            throw new IllegalArgumentException(ERR_WRONG_PASSWORD);
        }
        if (newPassword == null || newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(ERR_PASSWORD_TOO_SHORT);
        }
        user.setPasswordHash(newPassword);
        userRepository.save(user);
    }

    /**
     * Update a user's role (admin only operation)
     * 
     * @param userId the ID of the user to update
     * @param newRole the new role to assign
     * @return the updated user
     * @throws SecurityException if not an admin
     * @throws IllegalArgumentException if user not found
     */
    public User updateUserRole(Long userId, Role newRole) {
        if (!CurrentUser.isAdmin()) {
            throw new SecurityException(ERR_ADMIN_ONLY);
        }
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException(ERR_USER_NOT_FOUND);
        }
        user.setRole(newRole);
        return userRepository.save(user);
    }

    /**
     * Check if a username is already taken
     * 
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username.trim()) != null;
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
     * Get all users in the system (admin only)
     * 
     * @return list of all users
     * @throws SecurityException if not an admin
     */
    public java.util.List<User> getAllUsers() {
        if (!CurrentUser.isAdmin()) {
            throw new SecurityException(ERR_ADMIN_VIEW_ONLY);
        }
        return userRepository.findAll();
    }
}