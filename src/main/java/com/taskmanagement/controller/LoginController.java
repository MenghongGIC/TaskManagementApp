package com.taskmanagement.controller;

import java.io.IOException;

import com.taskmanagement.App;
import com.taskmanagement.model.User;
import com.taskmanagement.service.UserService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin() throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        try {
            User user = userService.login(username, password);
            if (user != null) {
                // Login successful - navigate to Dashboard view
                App.setRoot("main/Dashboard");
            } else {
                showError("Invalid username or password");
                passwordField.clear();
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            passwordField.clear();
        } catch (RuntimeException e) {
            showError("Login failed: " + e.getMessage());
            passwordField.clear();
        }
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        } else {
            System.err.println("Error: " + message);
        }
    }

    @FXML
    private void handleRegister() throws IOException {
        App.setRoot("auth/RegisterView");
    }
}