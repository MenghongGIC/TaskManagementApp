package com.taskmanagement.controller;

import java.io.IOException;

import com.taskmanagement.App;
import com.taskmanagement.model.User;
import com.taskmanagement.service.UserService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordFieldVisible;
    @FXML private CheckBox showPasswordCheckBox;
    @FXML private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        // Add Enter key handling to both username and password fields
        usernameField.setOnKeyPressed(this::handleKeyPress);
        passwordField.setOnKeyPressed(this::handleKeyPress);
        passwordFieldVisible.setOnKeyPressed(this::handleKeyPress);
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                handleLogin();
            } catch (IOException e) {
                showError("Navigation error: " + e.getMessage());
            }
        }
    }

    @FXML
    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            passwordFieldVisible.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordFieldVisible.setVisible(true);
            passwordFieldVisible.requestFocus();
        } else {
            passwordField.setText(passwordFieldVisible.getText());
            passwordField.setVisible(true);
            passwordFieldVisible.setVisible(false);
            passwordField.requestFocus();
        }
    }

    @FXML
    private void handleLogin() throws IOException {
        String username = usernameField.getText().trim();
        // Get password from whichever field is visible
        String password = passwordField.isVisible() ? passwordField.getText() : passwordFieldVisible.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        try {
            User user = userService.login(username, password);
            if (user != null) {
                App.setRoot("main/Dashboard");
            } else {
                showError("Invalid username or password");
                passwordField.clear();
                passwordFieldVisible.clear();
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            passwordField.clear();
            passwordFieldVisible.clear();
        } catch (RuntimeException e) {
            showError("Login failed: " + e.getMessage());
            passwordField.clear();
            passwordFieldVisible.clear();
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