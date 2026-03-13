package com.taskmanagement.controller;

import com.taskmanagement.App;
import com.taskmanagement.model.User;
import com.taskmanagement.service.UserService;
import com.taskmanagement.utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class AuthController {

    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Label loginErrorLabel;

    @FXML private TextField registerEmailField;
    @FXML private TextField registerUsernameField;
    @FXML private PasswordField registerPasswordField;
    @FXML private PasswordField registerConfirmPasswordField;
    @FXML private Label registerErrorLabel;
    @FXML private Label registerSuccessLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin() {
        if (loginUsernameField == null || loginPasswordField == null) {
            return;
        }

        String username = loginUsernameField.getText() == null ? "" : loginUsernameField.getText().trim();
        String password = loginPasswordField.getText() == null ? "" : loginPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            setLoginError("Please enter username and password");
            return;
        }

        try {
            User user = userService.login(username, password);
            App.setRoot("main/MainView");
        } catch (Exception e) {
            setLoginError(e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        if (registerUsernameField == null || registerPasswordField == null || registerConfirmPasswordField == null) {
            return;
        }

        String email = registerEmailField != null && registerEmailField.getText() != null ? registerEmailField.getText().trim(): "";
        String username = registerUsernameField.getText() == null ? "" : registerUsernameField.getText().trim();
        String password = registerPasswordField.getText() == null ? "" : registerPasswordField.getText();
        String confirm = registerConfirmPasswordField.getText() == null ? "" : registerConfirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            setRegisterError("Username and password are required");
            return;
        }

        if (!password.equals(confirm)) {
            setRegisterError("Passwords do not match");
            return;
        }

        try {
            User user = userService.register(username, email, password);
            setRegisterSuccess("Account created for " + user.getUsername() + ". Please login.");
        } catch (Exception e) {
            setRegisterError(e.getMessage());
        }
    }

    @FXML
    private void goToRegister() throws IOException {
        App.setRoot("auth/RegisterView");
    }

    @FXML
    private void goToLogin() throws IOException {
        App.setRoot("auth/LoginView");
    }

    private void setLoginError(String message) {
        if (loginErrorLabel != null) {
            UIUtils.setErrorStyle(loginErrorLabel, message);
            loginErrorLabel.setVisible(true);
        }
    }

    private void setRegisterError(String message) {
        if (registerErrorLabel != null) {
            UIUtils.setErrorStyle(registerErrorLabel, message);
            registerErrorLabel.setVisible(true);
        }
        if (registerSuccessLabel != null) {
            registerSuccessLabel.setVisible(false);
        }
    }

    private void setRegisterSuccess(String message) {
        if (registerSuccessLabel != null) {
            UIUtils.setSuccessStyle(registerSuccessLabel, message);
            registerSuccessLabel.setVisible(true);
        }
        if (registerErrorLabel != null) {
            registerErrorLabel.setVisible(false);
        }
    }
}
