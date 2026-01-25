package com.taskmanagement.controller;

import java.io.IOException;

import com.taskmanagement.App;
import com.taskmanagement.model.User;
import com.taskmanagement.service.UserService;
import com.taskmanagement.utils.UIUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController {
    private static final String ERROR_EMPTY_FIELDS = "Please enter both username and password";
    private static final String ERROR_INVALID_CREDENTIALS = "Invalid username or password";
    private static final String ERROR_LOGIN_FAILED = "Login failed: ";
    private static final String ERROR_NAVIGATION = "Navigation error: ";
    private static final String MAIN_LAYOUT_VIEW = "main/MainLayout";
    private static final String REGISTER_VIEW = "auth/RegisterView";

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordFieldVisible;
    @FXML private CheckBox showPasswordCheckBox;
    @FXML private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        setupKeyPressHandlers();
    }

    private void setupKeyPressHandlers() {
        usernameField.setOnKeyPressed(event -> handleEnterKey(event));
        passwordField.setOnKeyPressed(event -> handleEnterKey(event));
        passwordFieldVisible.setOnKeyPressed(event -> handleEnterKey(event));
    }

    private void handleEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                handleLogin();
            } catch (IOException e) {
                showError(ERROR_NAVIGATION + e.getMessage());
            }
        }
    }

    @FXML
    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            showPasswordField();
        } else {
            hidePasswordField();
        }
    }

    private void showPasswordField() {
        String password = passwordField.getText();
        passwordFieldVisible.setText(password);
        passwordField.setVisible(false);
        passwordFieldVisible.setVisible(true);
        passwordFieldVisible.requestFocus();
    }

    private void hidePasswordField() {
        String password = passwordFieldVisible.getText();
        passwordField.setText(password);
        passwordField.setVisible(true);
        passwordFieldVisible.setVisible(false);
        passwordField.requestFocus();
    }

    @FXML
    private void handleLogin() throws IOException {
        String username = usernameField.getText().trim();
        String password = getPassword();

        if (!isValidInput(username, password)) {
            showError(ERROR_EMPTY_FIELDS);
            return;
        }

        try {
            User user = userService.login(username, password);
            if (user != null) {
                navigateToMainLayout();
            } else {
                handleLoginFailure();
            }
        } catch (RuntimeException e) {
            showError(ERROR_LOGIN_FAILED + e.getMessage());
            clearPasswordFields();
        }
    }

    private String getPassword() {
        return passwordField.isVisible() ? passwordField.getText() : passwordFieldVisible.getText();
    }

    private boolean isValidInput(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }

    private void navigateToMainLayout() {
        Platform.runLater(() -> {
            try {
                App.setRoot(MAIN_LAYOUT_VIEW);
            } catch (IOException e) {
                showError(ERROR_NAVIGATION + e.getMessage());
            }
        });
    }

    private void handleLoginFailure() {
        showError(ERROR_INVALID_CREDENTIALS);
        clearPasswordFields();
    }

    private void clearPasswordFields() {
        passwordField.clear();
        passwordFieldVisible.clear();
    }

    private void showError(String message) {
        if (errorLabel != null) {
            UIUtils.setErrorStyle(errorLabel, message);
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleRegister() throws IOException {
        App.setRoot(REGISTER_VIEW);
    }
}