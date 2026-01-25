package com.taskmanagement.controller;

import com.taskmanagement.model.User;
import com.taskmanagement.service.UserService;
import com.taskmanagement.utils.CurrentUser;
import com.taskmanagement.utils.NavigationManager;
import com.taskmanagement.utils.UIUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
@SuppressWarnings("unused")
public class UserController {
    
    // Error Messages
    private static final String MSG_PASSWORD_MISMATCH = "Passwords do not match";
    private static final String MSG_REGISTRATION_SUCCESS = "Registration successful! You can now log in.";
    private static final String MSG_PROFILE_SAVED = "Profile updated successfully!";
    private static final String MSG_PROFILE_ERROR = "Error: ";
    
    // UI Labels
    private static final String LABEL_ROLE = "Role: ";
    
    // Colors
    private static final String COLOR_SUCCESS = "-fx-text-fill: #27ae60;";
    private static final String COLOR_ERROR = "-fx-text-fill: #e74c3c;";
    
    // Messages - Login
    private static final String MSG_LOGIN_FAILED = "Login failed";
    
    // Messages - Register  
    private static final String MSG_REGISTER_FAILED = "Registration failed";
    
    private final UserService userService = new UserService();

    // Login Panel
    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Button loginButton;
    @FXML private Label loginErrorLabel;

    // Register Panel
    @FXML private TextField registerUsernameField;
    @FXML private TextField registerEmailField;
    @FXML private PasswordField registerPasswordField;
    @FXML private PasswordField registerConfirmPasswordField;
    @FXML private Button registerButton;
    @FXML private Label registerErrorLabel;

    // Profile Panel
    @FXML private TextField profileUsernameField;
    @FXML private TextField profileEmailField;
    @FXML private Label profileRoleLabel;
    @FXML private Button profileSaveButton;
    @FXML private Button profileLogoutButton;
    @FXML private Label profileMessageLabel;

    @FXML
    public void initialize() {
        if (CurrentUser.isLoggedIn()) {
            loadProfile();
        }
    }
    @FXML
    public void handleLogin() {
        try {
            String username = loginUsernameField.getText().trim();
            String password = loginPasswordField.getText();
            
            userService.login(username, password);
            navigateToDashboard(loginButton);
            
        } catch (Exception e) {
            setLoginError(e.getMessage());
        }
    }
    @FXML
    public void handleRegister() {
        try {
            String username = registerUsernameField.getText().trim();
            String email = registerEmailField.getText().trim();
            String password = registerPasswordField.getText();
            String confirmPassword = registerConfirmPasswordField.getText();

            if (!validatePasswords(password, confirmPassword)) {
                setRegisterError(MSG_PASSWORD_MISMATCH);
                return;
            }

            userService.register(username, email, password);
            setRegisterSuccess(MSG_REGISTRATION_SUCCESS);
            clearRegisterForm();
            
        } catch (Exception e) {
            setRegisterError(e.getMessage());
        }
    }
    private void loadProfile() {
        User user = CurrentUser.getInstance();
        if (user != null) {
            profileUsernameField.setText(user.getUsername());
            profileEmailField.setText(user.getEmail());
            profileRoleLabel.setText(LABEL_ROLE + user.getRoleDisplayName());
        }
    }
    @FXML
    public void handleProfileSave() {
        try {
            User user = CurrentUser.getInstance();
            user.setEmail(profileEmailField.getText().trim());
            userService.updateProfile(user);
            setProfileSuccess(MSG_PROFILE_SAVED);
        } catch (Exception e) {
            setProfileError(MSG_PROFILE_ERROR + e.getMessage());
        }
    }
    @FXML
    public void handleProfileLogout() {
        userService.logout();
        navigateToLogin(profileLogoutButton);
    }

    public boolean isUsernameTaken(String username) {
        return userService.isUsernameTaken(username);
    }

    public User getCurrentUser() {
        return userService.getCurrentUser();
    }
    
    private void navigateToDashboard(Button button) {
        Stage stage = NavigationManager.getCurrentStage(button.getScene().getWindow());
        NavigationManager.goToDashboard(stage);
    }
    
    private void navigateToLogin(Button button) {
        Stage stage = NavigationManager.getCurrentStage(button.getScene().getWindow());
        NavigationManager.goToLogin(stage);
    }
    
    private void setLoginError(String message) {
        loginErrorLabel.setText(message);
    }
    
    private void setRegisterError(String message) {
        registerErrorLabel.setText(message);
    }
    
    private void setRegisterSuccess(String message) {
        displayMessage(registerErrorLabel, message, COLOR_SUCCESS);
    }
    
    private void setProfileError(String message) {
        displayMessage(profileMessageLabel, message, COLOR_ERROR);
    }
    
    private void setProfileSuccess(String message) {
        displayMessage(profileMessageLabel, message, COLOR_SUCCESS);
    }
    
    private void displayMessage(Label label, String message, String color) {
        label.setText(message);
        label.setStyle(color);
    }
    
    private void clearRegisterForm() {
        registerUsernameField.clear();
        registerEmailField.clear();
        registerPasswordField.clear();
        registerConfirmPasswordField.clear();
    }
    
    private boolean validatePasswords(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }
}
