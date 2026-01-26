package com.taskmanagement.service;

import com.taskmanagement.model.User;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.utils.CurrentUser;

public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;

    public AuthService() {
        this.userRepository = new UserRepository();
        this.userService = new UserService(userRepository);
    }

    public User login(String username, String password) {
        return userService.login(username, password);
    }

    public void logout() {
        userService.logout();
    }
    public User register(String username, String email, String password) {
        return userService.register(username, email, password);
    }

    public boolean isLoggedIn() {
        return CurrentUser.isLoggedIn();
    }

    public User getCurrentUser() {
        return CurrentUser.getInstance();
    }
    public boolean isAdmin() {
        return CurrentUser.isAdmin();
    }

    public void changePassword(String oldPassword, String newPassword) {
        User user = CurrentUser.getInstance();
        if (user == null) {
            throw new IllegalStateException("No user logged in");
        }
        userService.changePassword(user, oldPassword, newPassword);
    }
    public void updateProfile(User updatedUser) {
        User current = CurrentUser.getInstance();
        if (current == null) {
            throw new IllegalStateException("No user logged in");
        }
        updatedUser.setId(current.getId());
        userService.updateProfile(updatedUser);
    }
}
