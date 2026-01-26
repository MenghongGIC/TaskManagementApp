    package com.taskmanagement.service;

import java.time.LocalDateTime;

import com.taskmanagement.model.Role;
import com.taskmanagement.model.User;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.utils.CurrentUser;

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

    public void logout() {
        CurrentUser.clear();
    }

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

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username.trim()) != null;
    }

    public User getCurrentUser() {
        return CurrentUser.getInstance();
    }
    
    public java.util.List<User> getAllUsers() {
        if (!CurrentUser.isAdmin()) {
            throw new SecurityException(ERR_ADMIN_VIEW_ONLY);
        }
        return userRepository.findAll();
    }
}