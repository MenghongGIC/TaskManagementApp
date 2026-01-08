package com.taskmanagement.controller;

import com.taskmanagement.model.User;
import com.taskmanagement.service.UserService;
import com.taskmanagement.utils.CurrentUser;
import com.taskmanagement.utils.NavigationManager;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
@SuppressWarnings("unused")

public class UserController {

    private final UserService userService = new UserService();

    // --- Login Panel ---
    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Button loginButton;
    @FXML private Label loginErrorLabel;

    // --- Register Panel ---
    @FXML private TextField registerUsernameField;
    @FXML private TextField registerEmailField;
    @FXML private PasswordField registerPasswordField;
    @FXML private PasswordField registerConfirmPasswordField;
    @FXML private Button registerButton;
    @FXML private Label registerErrorLabel;

    // --- Profile Panel ---
    @FXML private TextField profileUsernameField;
    @FXML private TextField profileEmailField;
    @FXML private Label profileRoleLabel;
    @FXML private Button profileSaveButton;
    @FXML private Button profileLogoutButton;
    @FXML private Label profileMessageLabel;

    @FXML
    public void initialize() {
        //to check if user is logged in and load profile
        if (CurrentUser.isLoggedIn()) {
            loadProfile();
        }
    }

    //login
    @FXML
    public void handleLogin() {
        try {
            String username = loginUsernameField.getText().trim();
            String password = loginPasswordField.getText();

            User user = userService.login(username, password);

            // Navigate to Dashboard
            Stage stage = NavigationManager.getCurrentStage(loginButton.getScene().getWindow());
            NavigationManager.goToDashboard(stage);

        } catch (Exception e) {
            loginErrorLabel.setText(e.getMessage());
        }
    }
    //register
    @FXML
    public void handleRegister() {
        try {
            String username = registerUsernameField.getText().trim();
            String email = registerEmailField.getText().trim();
            String password = registerPasswordField.getText();
            String confirmPassword = registerConfirmPasswordField.getText();

            if (!password.equals(confirmPassword)) {
                registerErrorLabel.setText("Passwords do not match");
                return;
            }

            User user = userService.register(username, email, password);

            registerErrorLabel.setText("Registration successful! You can now log in.");

        } catch (Exception e) {
            registerErrorLabel.setText(e.getMessage());
        }
    }
    //profile
    private void loadProfile() {
        User user = CurrentUser.getInstance();
        if (user != null) {
            profileUsernameField.setText(user.getUsername());
            profileEmailField.setText(user.getEmail());
            profileRoleLabel.setText(user.getRoleDisplayName());
        }
    }
    //save profile
    @FXML
    public void handleProfileSave() {
        try {
            User user = CurrentUser.getInstance();
            user.setEmail(profileEmailField.getText().trim());
            userService.updateProfile(user);
            profileMessageLabel.setText("Profile updated successfully!");
        } catch (Exception e) {
            profileMessageLabel.setText("Error: " + e.getMessage());
        }
    }
    //logout
    @FXML
    public void handleProfileLogout() {
        userService.logout();
        Stage stage = NavigationManager.getCurrentStage(profileLogoutButton.getScene().getWindow());
        NavigationManager.goToLogin(stage);
    }

    public boolean isUsernameTaken(String username) {
        return userService.isUsernameTaken(username);
    }
    public User getCurrentUser() {
        return userService.getCurrentUser();
    }
}
