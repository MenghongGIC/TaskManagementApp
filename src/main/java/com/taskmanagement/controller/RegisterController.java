package com.taskmanagement.controller;

import java.io.IOException;

import com.taskmanagement.App;
import com.taskmanagement.model.User;
import com.taskmanagement.service.UserService;
import com.taskmanagement.utils.UIUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {
    
    // Validation Messages
    private static final String MSG_EMAIL_REQUIRED = "Email is required";
    private static final String MSG_USERNAME_REQUIRED = "Username is required";
    private static final String MSG_PASSWORD_REQUIRED = "Password is required";
    private static final String MSG_PASSWORD_MIN_LENGTH = "Password must be at least 8 characters";
    private static final String MSG_PASSWORD_MISMATCH = "Passwords do not match";
    private static final String MSG_INVALID_EMAIL = "Please enter a valid email address";
    private static final String MSG_REGISTRATION_SUCCESS = "Registration successful! User account created with role: ";
    private static final String MSG_REGISTRATION_FAILED = "Registration failed: ";
    private static final String MSG_NAVIGATION_ERROR = "Error navigating to login: ";
    
    // Validation Constants
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final long REDIRECT_DELAY_MS = 1500;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String LOGIN_VIEW = "auth/LoginView";

    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordFieldVisible;
    @FXML private CheckBox showPasswordCheckBox;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordFieldVisible;
    @FXML private CheckBox showConfirmPasswordCheckBox;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    
    private final UserService userService = new UserService();


    @FXML
    public void initialize() {
        setLabelVisibility(false);
    }
    
    private void setLabelVisibility(boolean show) {
        if (errorLabel != null) errorLabel.setVisible(show && false);
        if (successLabel != null) successLabel.setVisible(show && false);
    }

    
    @FXML
    private void handleRegister() {
        clearMessages();
        
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = getPasswordValue(passwordField, passwordFieldVisible);
        String confirmPassword = getPasswordValue(confirmPasswordField, confirmPasswordFieldVisible);
        
        String validationError = validateRegistration(email, username, password, confirmPassword);
        if (validationError != null) {
            showError(validationError);
            return;
        }
        
        performRegistration(username, email, password);
    }
    
    private String validateRegistration(String email, String username, String password, String confirmPassword) {
        if (email.isEmpty()) return MSG_EMAIL_REQUIRED;
        if (username.isEmpty()) return MSG_USERNAME_REQUIRED;
        if (password.isEmpty()) return MSG_PASSWORD_REQUIRED;
        if (password.length() < MIN_PASSWORD_LENGTH) return MSG_PASSWORD_MIN_LENGTH;
        if (!password.equals(confirmPassword)) {
            confirmPasswordField.clear();
            return MSG_PASSWORD_MISMATCH;
        }
        if (!isValidEmail(email)) return MSG_INVALID_EMAIL;
        return null;
    }
    
    private void performRegistration(String username, String email, String password) {
        try {
            User newUser = userService.register(username, email, password);
            showSuccess(MSG_REGISTRATION_SUCCESS + newUser.getRole());
            clearFields();
            navigateToLoginAfterDelay();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError(MSG_REGISTRATION_FAILED + e.getMessage());
        }
    }
    
    private void navigateToLoginAfterDelay() {
        Platform.runLater(() -> {
            try {
                Thread.sleep(REDIRECT_DELAY_MS);
                App.setRoot(LOGIN_VIEW);
            } catch (IOException | InterruptedException e) {
                showError(MSG_NAVIGATION_ERROR + e.getMessage());
            }
        });
    }
    
    @FXML
    private void handleBackToLogin() throws IOException {
        App.setRoot(LOGIN_VIEW);
    }
    
    private void showError(String message) {
        if (errorLabel != null) {
            UIUtils.setErrorStyle(errorLabel, message);
            errorLabel.setVisible(true);
        }
        if (successLabel != null) {
            successLabel.setVisible(false);
        }
    }
    
    private void showSuccess(String message) {
        if (successLabel != null) {
            UIUtils.setSuccessStyle(successLabel, message);
            successLabel.setVisible(true);
        }
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
    }
    
    private void clearMessages() {
        if (errorLabel != null) errorLabel.setVisible(false);
        if (successLabel != null) successLabel.setVisible(false);
    }
    
    private void clearFields() {
        emailField.clear();
        usernameField.clear();
        passwordField.clear();
        passwordFieldVisible.clear();
        confirmPasswordField.clear();
        confirmPasswordFieldVisible.clear();
    }
    
    @FXML
    private void togglePasswordVisibility() {
        toggleFieldVisibility(showPasswordCheckBox, passwordField, passwordFieldVisible);
    }

    @FXML
    private void toggleConfirmPasswordVisibility() {
        toggleFieldVisibility(showConfirmPasswordCheckBox, confirmPasswordField, confirmPasswordFieldVisible);
    }
    
    private void toggleFieldVisibility(CheckBox checkBox, PasswordField hidden, TextField visible) {
        if (checkBox.isSelected()) {
            visible.setText(hidden.getText());
            setFieldVisibility(hidden, false, visible, true);
        } else {
            hidden.setText(visible.getText());
            setFieldVisibility(hidden, true, visible, false);
        }
    }
    
    private void setFieldVisibility(PasswordField hidden, boolean showHidden, TextField visible, boolean showVisible) {
        hidden.setVisible(showHidden);
        visible.setVisible(showVisible);
    }
    
    private String getPasswordValue(PasswordField hidden, TextField visible) {
        return hidden.isVisible() ? hidden.getText() : visible.getText();
    }
    private boolean isValidEmail(String email) {
        return email.matches(EMAIL_REGEX);
    }
}
