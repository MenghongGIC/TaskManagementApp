package com.taskmanagement.controller;

import java.io.IOException;

import com.taskmanagement.App;
import com.taskmanagement.model.User;
import com.taskmanagement.service.UserService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Label successLabel;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
        if (successLabel != null) {
            successLabel.setVisible(false);
        }
    }

    @FXML
    private void handleRegister() {
        clearMessages();

        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation
        if (email.isEmpty()) {
            showError("Email is required");
            return;
        }

        if (username.isEmpty()) {
            showError("Username is required");
            return;
        }

        if (password.isEmpty()) {
            showError("Password is required");
            return;
        }

        if (password.length() < 8) {
            showError("Password must be at least 8 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            confirmPasswordField.clear();
            return;
        }

        // Email validation
        if (!isValidEmail(email)) {
            showError("Please enter a valid email address");
            return;
        }

        try {
            User newUser = userService.register(username, email, password);
            showSuccess("Registration successful! User account created with role: " + newUser.getRole());
            clearFields();
            // Navigate back to login after a brief delay
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(1500);
                    App.setRoot("auth/LoginView");
                } catch (IOException | InterruptedException e) {
                    showError("Error navigating to login: " + e.getMessage());
                }
            });
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Registration failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToLogin() throws IOException {
        App.setRoot("auth/LoginView");
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
        if (successLabel != null) {
            successLabel.setVisible(false);
        }
    }

    private void showSuccess(String message) {
        if (successLabel != null) {
            successLabel.setText(message);
            successLabel.setVisible(true);
        }
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
    }

    private void clearMessages() {
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
        if (successLabel != null) {
            successLabel.setVisible(false);
        }
    }

    private void clearFields() {
        emailField.clear();
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
