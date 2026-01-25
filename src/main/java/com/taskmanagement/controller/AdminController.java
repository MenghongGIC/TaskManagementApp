package com.taskmanagement.controller;

import com.taskmanagement.model.Role;
import com.taskmanagement.model.User;
import com.taskmanagement.service.UserService;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.utils.CurrentUser;
import com.taskmanagement.utils.NavigationManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class AdminController {

    private final UserService userService = new UserService();
    private final UserRepository userRepository = new UserRepository();
    private final TaskService taskService = new TaskService();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // --- Header / Stats Panel ---
    @FXML private Label totalUsersLabel;
    @FXML private Label adminCountLabel;
    @FXML private Label userCountLabel;
    @FXML private Label blockedCountLabel;

    // --- Search and Filter ---
    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilterCombo;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    @FXML private Button addUserButton;

    // --- Users Table ---
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Long> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> statusColumn;
    @FXML private TableColumn<User, String> createdColumn;
    @FXML private TableColumn<User, Void> actionsColumn;

    // --- Bottom Controls ---
    @FXML private Label messageLabel;
    @FXML private Button logoutButton;

    private ObservableList<User> usersList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (!CurrentUser.isAdmin()) {
            showError("Access Denied: Admin privileges required");
            return;
        }

        initializeTable();
        initializeFilters();
        loadAllUsers();
        displayStats();
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        statusColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String status = "ACTIVE";
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        createdColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String dateStr = user.getCreatedAt() != null 
                ? user.getCreatedAt().format(dateFormatter) 
                : "N/A";
            return new javafx.beans.property.SimpleStringProperty(dateStr);
        });

        addActionsColumn();

        usersTable.setItems(usersList);
    }

    private void addActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<User, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    User user = getTableRow().getItem();
                    HBox actionBox = new HBox(5);
                    actionBox.setPadding(new Insets(5));

                    Button tasksBtn = new Button("Tasks");
                    tasksBtn.setStyle("-fx-padding: 5 10; -fx-font-size: 11; -fx-background-color: #3498db; -fx-text-fill: white;");
                    tasksBtn.setOnAction(e -> showUserTasksDialog(user));

                    Button editBtn = new Button("Edit");
                    editBtn.setStyle("-fx-padding: 5 10; -fx-font-size: 11;");
                    editBtn.setOnAction(e -> showEditUserDialog(user));

                    Button deleteBtn = new Button("Delete");
                    deleteBtn.setStyle("-fx-padding: 5 10; -fx-font-size: 11; -fx-text-fill: red;");
                    deleteBtn.setOnAction(e -> deleteUser(user));

                    if (user.getId().equals(CurrentUser.getId())) {
                        deleteBtn.setDisable(true);
                    }

                    actionBox.getChildren().addAll(tasksBtn, editBtn, deleteBtn);
                    setGraphic(actionBox);
                }
            }
        });
    }

    private void initializeFilters() {
        roleFilterCombo.setItems(FXCollections.observableArrayList("All", "ADMIN", "USER"));
        roleFilterCombo.setValue("All");
        
        roleFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal)) {
                handleRoleFilter();
            }
        });
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                handleSearch();
            } else if (oldVal != null && !oldVal.trim().isEmpty()) {
                loadAllUsers();
            }
        });
    }

    private void loadAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            usersList.setAll(users);
            showSuccess("Loaded " + users.size() + " users");
        } catch (Exception e) {
            showError("Error loading users: " + e.getMessage());
        }
    }

    private void displayStats() {
        try {
            List<User> allUsers = userService.getAllUsers();
            int adminCount = (int) allUsers.stream().filter(u -> u.isAdmin()).count();
            int userCount = (int) allUsers.stream().filter(u -> u.isUser()).count();
            
            Platform.runLater(() -> {
                totalUsersLabel.setText(String.valueOf(allUsers.size()));
                adminCountLabel.setText(String.valueOf(adminCount));
                userCountLabel.setText(String.valueOf(userCount));
                blockedCountLabel.setText("0");
            });
        } catch (Exception e) {
            showError("Error loading statistics: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadAllUsers();
            return;
        }

        try {
            System.out.println("🔍 Searching for: " + query);
            List<User> allUsers = userService.getAllUsers();
            String queryLower = query.toLowerCase();
            List<User> results = allUsers.stream()
                .filter(u -> u.getUsername().toLowerCase().contains(queryLower) || 
                           u.getEmail().toLowerCase().contains(queryLower))
                .toList();
            usersList.setAll(results);
            showSuccess("✓ Found " + results.size() + " user(s) matching '" + query + "'");
        } catch (Exception e) {
            System.err.println("Search error: " + e.getMessage());
            e.printStackTrace();
            showError("❌ Search error: " + e.getMessage());
        }
    }

    @FXML
    private void handleRoleFilter() {
        String selectedRole = roleFilterCombo.getValue();
        if (selectedRole == null || "All".equals(selectedRole)) {
            loadAllUsers();
            return;
        }

        try {
            System.out.println("🔽 Filtering by role: " + selectedRole);
            Role role = Role.valueOf(selectedRole);
            List<User> allUsers = userService.getAllUsers();
            List<User> filtered = allUsers.stream()
                .filter(u -> u.getRole() == role)
                .toList();
            usersList.setAll(filtered);
            showSuccess("✓ Found " + filtered.size() + " " + selectedRole + " user(s)");
        } catch (Exception e) {
            System.err.println("Filter error: " + e.getMessage());
            e.printStackTrace();
            showError("❌ Filter error: " + e.getMessage());
            loadAllUsers();
        }
    }

    @FXML
    private void handleRefresh() {
        try {
            System.out.println("🔄 Refreshing data...");
            searchField.clear();
            roleFilterCombo.setValue("All");
            loadAllUsers();
            displayStats();
            showSuccess("✓ Data refreshed successfully");
        } catch (Exception e) {
            System.err.println("Refresh error: " + e.getMessage());
            e.printStackTrace();
            showError("❌ Refresh error: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddUser() {
        System.out.println("➕ Opening Add User dialog");
        showAddUserDialog();
    }

    private void showAddUserDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Create a new user account");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        ComboBox<Role> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList(Role.ADMIN, Role.USER));
        roleCombo.setValue(Role.USER);

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            try {
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String password = passwordField.getText();

                // Validation
                if (username.isEmpty()) {
                    showError("❌ Username cannot be empty");
                    return;
                }
                if (email.isEmpty()) {
                    showError("❌ Email cannot be empty");
                    return;
                }
                if (password.isEmpty()) {
                    showError("❌ Password cannot be empty");
                    return;
                }

                User newUser = new User();
                newUser.setUsername(username);
                newUser.setEmail(email);
                newUser.setPasswordHash(password);
                newUser.setRole(roleCombo.getValue());
                newUser.setCreatedAt(LocalDateTime.now());

                System.out.println("➕ Creating new user: " + username);
                userService.register(username, email, password);
                loadAllUsers();
                displayStats();
                showSuccess("✓ User '" + username + "' created successfully");
            } catch (Exception e) {
                System.err.println("Error creating user: " + e.getMessage());
                e.printStackTrace();
                showError("❌ Error creating user: " + e.getMessage());
            }
        });
    }

    private void showEditUserDialog(User user) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user: " + user.getUsername());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField usernameField = new TextField(user.getUsername());
        usernameField.setDisable(true); // Username cannot be changed
        TextField emailField = new TextField(user.getEmail() != null ? user.getEmail() : "");
        TextField positionField = new TextField(user.getPosition() != null ? user.getPosition() : "");
        ComboBox<Role> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList(Role.ADMIN, Role.USER));
        roleCombo.setValue(user.getRole());

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Position:"), 0, 2);
        grid.add(positionField, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(selectedUser -> {
            try {
                user.setEmail(emailField.getText());
                user.setPosition(positionField.getText());
                user.setRole(roleCombo.getValue());

                userService.updateProfile(user);
                loadAllUsers();
                displayStats();
                showSuccess("User updated successfully");
            } catch (Exception e) {
                showError("Error updating user: " + e.getMessage());
            }
        });
    }

    private void deleteUser(User user) {
        if (user.getId().equals(CurrentUser.getId())) {
            showError("Cannot delete your own account");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete User");
        alert.setContentText("Are you sure you want to delete user '" + user.getUsername() + "'? This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userRepository.delete(user.getId());
                loadAllUsers();
                displayStats();
                showSuccess("User deleted successfully");
            } catch (Exception e) {
                showError("Error deleting user: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Confirm Logout");
        alert.setContentText("Are you sure you want to logout?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                CurrentUser.set(null);
                com.taskmanagement.App.setRoot("auth/LoginView");
            } catch (Exception e) {
                showError("Logout error: " + e.getMessage());
            }
        }
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: green;");
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: red;");
    }
    
    /**
     * Show user tasks in a popup dialog
     */
    private void showUserTasksDialog(User user) {
        try {
            System.out.println("📋 Opening user tasks dialog for: " + user.getUsername());
            
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(com.taskmanagement.App.class.getResource("fxml/dialog/UserTasksDialog.fxml"));
            BorderPane dialogRoot = loader.load();
            UserTasksDialogController controller = loader.getController();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Tasks - " + user.getUsername());
            dialogStage.setScene(new javafx.scene.Scene(dialogRoot, 900, 600));
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.initOwner(usersTable.getScene().getWindow());
            
            controller.setDialogStage(dialogStage);
            controller.loadUserTasks(user);
            
            dialogStage.show();
        } catch (Exception e) {
            System.err.println("❌ Error opening user tasks dialog: " + e.getMessage());
            e.printStackTrace();
            showError("Error opening tasks dialog: " + e.getMessage());
        }
    }
}
