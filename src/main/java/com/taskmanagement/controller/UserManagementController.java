package com.taskmanagement.controller;

import com.taskmanagement.model.Role;
import com.taskmanagement.model.User;
import com.taskmanagement.service.UserService;
import com.taskmanagement.utils.CurrentUser;
import com.taskmanagement.utils.UIUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

public class UserManagementController {

    @FXML private TextField searchField;
    @FXML private Label statusLabel;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> createdAtColumn;
    @FXML private TableColumn<User, Void> actionColumn;

    private final UserService userService = new UserService();
    private List<User> allUsers;

    @FXML
    public void initialize() {
        // Check if current user is admin
        if (!CurrentUser.isAdmin()) {
            statusLabel.setText("Access Denied: Admin privileges required");
            userTable.setDisable(true);
            return;
        }

        setupTableColumns();
        loadUsers();
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterUsers(newVal));
    }

    private void setupTableColumns() {
        usernameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getEmail() != null ? data.getValue().getEmail() : "-"
        ));
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getRole().toString()
        ));
        createdAtColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getCreatedAt() != null ? data.getValue().getCreatedAt().toString() : "-"
        ));

        setupActionColumn();
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(col -> new TableCell<User, Void>() {
            private final Button roleButton = new Button();

            {
                roleButton.setStyle("-fx-padding: 5px 10px; -fx-font-size: 11px;");
                roleButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    changeUserRole(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView().getItems().isEmpty()) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    roleButton.setText(user.getRole() == Role.ADMIN ? "Remove Admin" : "Make Admin");
                    roleButton.setStyle(
                        user.getRole() == Role.ADMIN
                            ? "-fx-padding: 5px 10px; -fx-font-size: 11px; -fx-background-color: #ffcccc;"
                            : "-fx-padding: 5px 10px; -fx-font-size: 11px; -fx-background-color: #ccffcc;"
                    );
                    setGraphic(roleButton);
                }
            }
        });
    }

    private void loadUsers() {
        try {
            allUsers = userService.getAllUsers();
            userTable.setItems(FXCollections.observableArrayList(allUsers));
            statusLabel.setText("Users loaded: " + allUsers.size());
        } catch (Exception e) {
            UIUtils.setErrorStyle(statusLabel, "Error loading users: " + e.getMessage());
            statusLabel.setVisible(true);
        }
    }

    private void filterUsers(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            userTable.setItems(FXCollections.observableArrayList(allUsers));
        } else {
            String lowerSearch = searchText.toLowerCase().trim();
            List<User> filtered = allUsers.stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(lowerSearch)
                            || (u.getEmail() != null && u.getEmail().toLowerCase().contains(lowerSearch)))
                    .toList();
            userTable.setItems(FXCollections.observableArrayList(filtered));
            statusLabel.setText("Found: " + filtered.size() + " users");
        }
    }

    private void changeUserRole(User user) {
        try {
            Role newRole = user.getRole() == Role.ADMIN ? Role.USER : Role.ADMIN;
            userService.updateUserRole(user.getId(), newRole);
            user.setRole(newRole);
            userTable.refresh();
            statusLabel.setText("User role updated for: " + user.getUsername());
        } catch (Exception e) {
            UIUtils.setErrorStyle(statusLabel, "Error updating role: " + e.getMessage());
            statusLabel.setVisible(true);
        }
    }

    @FXML
    private void handleRefresh() {
        loadUsers();
        searchField.clear();
    }
}
