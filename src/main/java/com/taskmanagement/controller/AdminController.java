package com.taskmanagement.controller;

import com.taskmanagement.model.Role;
import com.taskmanagement.model.User;
import com.taskmanagement.service.UserService;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.utils.CurrentUser;
import com.taskmanagement.utils.NavigationManager;
import com.taskmanagement.utils.UIUtils;
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
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final String BTN_TASKS = "-fx-padding: 5 10; -fx-font-size: 11; -fx-background-color: #3498db; -fx-text-fill: white;";
    private static final String BTN_EDIT = "-fx-padding: 5 10; -fx-font-size: 11;";
    private static final String BTN_DELETE = "-fx-padding: 5 10; -fx-font-size: 11; -fx-text-fill: red;";
    private static final String GRID_PADDING = "20";
    private static final String GRID_GAP = "10";
    private static final String STATS_HEADER = "0";

    @FXML private Label totalUsersLabel, adminCountLabel, userCountLabel, blockedCountLabel, messageLabel;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilterCombo;
    
    @FXML private Button searchButton, logoutButton, refreshButton, addUserButton;

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Long> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> statusColumn;
    @FXML private TableColumn<User, String> createdColumn;
    @FXML private TableColumn<User, Void> actionsColumn;

    private ObservableList<User> usersList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (!CurrentUser.isAdmin()) {
            UIUtils.showError("Access Denied", "Admin privileges required");
            return;
        }
        setupTable();
        setupFilters();
        loadAllUsers();
        displayStats();
    }

    private void setupTable() {
        setupTableColumns();
        addActionsColumn();
        usersTable.setItems(usersList);
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("ACTIVE")
        );
        createdColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String dateStr = user.getCreatedAt() != null 
                ? user.getCreatedAt().format(dateFormatter) 
                : "N/A";
            return new javafx.beans.property.SimpleStringProperty(dateStr);
        });
    }

    private void addActionsColumn() {
        actionsColumn.setPrefWidth(280);
        actionsColumn.setMinWidth(280);
        actionsColumn.setCellFactory(param -> new TableCell<User, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    User user = getTableRow().getItem();
                    setGraphic(createActionButtons(user));
                }
            }
        });
    }

    private HBox createActionButtons(User user) {
        HBox actionBox = new HBox(8);
        actionBox.setPadding(new Insets(5));
        actionBox.setStyle("-fx-alignment: center; -fx-spacing: 8;");
        actionBox.setPrefWidth(260);
        
        Button tasksBtn = new Button("Tasks");
        tasksBtn.setStyle(BTN_TASKS);
        tasksBtn.setMinWidth(70);
        tasksBtn.setOnAction(e -> showUserTasksDialog(user));
        
        Button editBtn = new Button("Edit");
        editBtn.setStyle(BTN_EDIT);
        editBtn.setMinWidth(60);
        editBtn.setOnAction(e -> showEditUserDialog(user));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle(BTN_DELETE);
        deleteBtn.setMinWidth(70);
        deleteBtn.setOnAction(e -> deleteUser(user));
        
        if (user.getId().equals(CurrentUser.getId())) {
            deleteBtn.setDisable(true);
        }
        
        actionBox.getChildren().addAll(tasksBtn, editBtn, deleteBtn);
        return actionBox;
    }

    private void setupFilters() {
        roleFilterCombo.setItems(FXCollections.observableArrayList("All", "ADMIN", "USER"));
        roleFilterCombo.setValue("All");
        roleFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal)) {
                handleRoleFilter();
            }
        });
        searchField.textProperty().addListener((obs, oldVal, newVal) -> handleSearchUpdate(oldVal, newVal));
    }

    private void handleSearchUpdate(String oldVal, String newVal) {
        if (!newVal.trim().isEmpty()) {
            handleSearch();
        } else if (oldVal != null && !oldVal.trim().isEmpty()) {
            loadAllUsers();
        }
    }

    private void loadAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            usersList.setAll(users);
            setSuccess("Loaded " + users.size() + " users");
        } catch (Exception e) {
            setError("Error loading users: " + e.getMessage());
        }
    }

    private void displayStats() {
        try {
            List<User> allUsers = userService.getAllUsers();
            int adminCount = (int) allUsers.stream().filter(User::isAdmin).count();
            int userCount = (int) allUsers.stream().filter(User::isUser).count();
            updateStatsLabels(allUsers.size(), adminCount, userCount);
        } catch (Exception e) {
            setError("Error loading statistics: " + e.getMessage());
        }
    }

    private void updateStatsLabels(int total, int admins, int users) {
        Platform.runLater(() -> {
            totalUsersLabel.setText(String.valueOf(total));
            adminCountLabel.setText(String.valueOf(admins));
            userCountLabel.setText(String.valueOf(users));
            blockedCountLabel.setText(STATS_HEADER);
        });
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadAllUsers();
            return;
        }
        try {
            List<User> results = userService.getAllUsers().stream()
                .filter(u -> matches(u, query.toLowerCase()))
                .toList();
            usersList.setAll(results);
            setSuccess("Found " + results.size() + " user(s) matching '" + query + "'");
        } catch (Exception e) {
            setError("Search error: " + e.getMessage());
        }
    }

    private boolean matches(User user, String query) {
        return user.getUsername().toLowerCase().contains(query) || 
               user.getEmail().toLowerCase().contains(query);
    }

    @FXML
    private void handleRoleFilter() {
        String selectedRole = roleFilterCombo.getValue();
        if (selectedRole == null || "All".equals(selectedRole)) {
            loadAllUsers();
            return;
        }
        try {
            Role role = Role.valueOf(selectedRole);
            List<User> filtered = userService.getAllUsers().stream()
                .filter(u -> u.getRole() == role)
                .toList();
            usersList.setAll(filtered);
            setSuccess("Found " + filtered.size() + " " + selectedRole + " user(s)");
        } catch (Exception e) {
            setError("Filter error: " + e.getMessage());
            loadAllUsers();
        }
    }

    @FXML
    private void handleRefresh() {
        try {
            clearFilters();
            loadAllUsers();
            displayStats();
            setSuccess("Data refreshed successfully");
        } catch (Exception e) {
            setError("Refresh error: " + e.getMessage());
        }
    }

    private void clearFilters() {
        searchField.clear();
        roleFilterCombo.setValue("All");
    }

    @FXML
    private void handleAddUser() {
        showAddUserDialog();
    }

    private void showAddUserDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Create a new user account");
        GridPane grid = createAddUserGrid();
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            handleAddUserResult(grid);
        }
    }
    
    private GridPane createAddUserGrid() {
        GridPane grid = createBaseGrid();
        TextField usernameField = createTextField("Username");
        TextField emailField = createTextField("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        ComboBox<Role> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList(Role.ADMIN, Role.USER));
        roleCombo.setValue(Role.USER);
        addFormFields(grid, "Username", usernameField, "Email", emailField, "Password", passwordField, "Role", roleCombo);
        grid.setUserData(new Object[]{usernameField, emailField, passwordField, roleCombo});
        return grid;
    }

    private GridPane createBaseGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(Integer.parseInt(GRID_GAP));
        grid.setVgap(Integer.parseInt(GRID_GAP));
        grid.setPadding(new Insets(Integer.parseInt(GRID_PADDING)));
        return grid;
    }

    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        return field;
    }

    private void addFormFields(GridPane grid, Object... fieldPairs) {
        for (int i = 0; i < fieldPairs.length; i += 2) {
            grid.add(new Label((String) fieldPairs[i] + ":"), 0, i / 2);
            grid.add((javafx.scene.Node) fieldPairs[i + 1], 1, i / 2);
        }
    }
    
    private void handleAddUserResult(GridPane grid) {
        Object[] fields = (Object[]) grid.getUserData();
        TextField usernameField = (TextField) fields[0];
        TextField emailField = (TextField) fields[1];
        PasswordField passwordField = (PasswordField) fields[2];
        try {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            if (!validateAddUserForm(username, email, password)) {
                return;
            }
            userService.register(username, email, password);
            refreshUserData();
            setSuccess("User '" + username + "' created successfully");
        } catch (Exception e) {
            setError("Error creating user: " + e.getMessage());
        }
    }

    private boolean validateAddUserForm(String username, String email, String password) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            setError("All fields are required");
            return false;
        }
        return true;
    }

    private void showEditUserDialog(User user) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user: " + user.getUsername());
        
        GridPane grid = createEditUserGrid(user);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(selectedUser -> handleEditUserResult(user, grid));
    }
    
    private GridPane createEditUserGrid(User user) {
        GridPane grid = createBaseGrid();
        TextField usernameField = new TextField(user.getUsername());
        usernameField.setDisable(true);
        TextField emailField = createTextField(user.getEmail() != null ? user.getEmail() : "");
        TextField positionField = createTextField(user.getPosition() != null ? user.getPosition() : "");
        ComboBox<Role> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList(Role.ADMIN, Role.USER));
        roleCombo.setValue(user.getRole());
        addFormFields(grid, "Username", usernameField, "Email", emailField, "Position", positionField, "Role", roleCombo);
        grid.setUserData(new Object[]{emailField, positionField, roleCombo});
        return grid;
    }
    
    @SuppressWarnings("unchecked")
    private void handleEditUserResult(User user, GridPane grid) {
        Object[] fields = (Object[]) grid.getUserData();
        TextField emailField = (TextField) fields[0];
        TextField positionField = (TextField) fields[1];
        ComboBox<Role> roleCombo = (ComboBox<Role>) fields[2];
        try {
            user.setEmail(emailField.getText());
            user.setPosition(positionField.getText());
            user.setRole(roleCombo.getValue());
            userService.updateProfile(user);
            refreshUserData();
            setSuccess("User updated successfully");
        } catch (Exception e) {
            setError("Error updating user: " + e.getMessage());
        }
    }

    private void refreshUserData() {
        loadAllUsers();
        displayStats();
    }

    private void deleteUser(User user) {
        if (user.getId().equals(CurrentUser.getId())) {
            setError("Cannot delete your own account");
            return;
        }
        if (UIUtils.showDeleteConfirmation(user.getUsername())) {
            try {
                userRepository.delete(user.getId());
                refreshUserData();
                setSuccess("User deleted successfully");
            } catch (Exception e) {
                setError("Error deleting user: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleLogout() {
        if (UIUtils.showCustomConfirmation("Logout", "Confirm Logout", "Are you sure you want to logout?")) {
            try {
                CurrentUser.set(null);
                com.taskmanagement.App.setRoot("auth/LoginView");
            } catch (Exception e) {
                setError("Logout error: " + e.getMessage());
            }
        }
    }

    private void setSuccess(String message) {
        UIUtils.setSuccessStyle(messageLabel, message);
    }

    private void setError(String message) {
        UIUtils.setErrorStyle(messageLabel, message);
    }
    
    private void showUserTasksDialog(User user) {
        try {
            javafx.fxml.FXMLLoader loader = createTasksDialogLoader();
            BorderPane dialogRoot = loader.load();
            UserTasksDialogController controller = loader.getController();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("User Tasks - " + user.getUsername());
            javafx.scene.Scene scene = new javafx.scene.Scene(dialogRoot, 900, 600);
            dialogStage.setScene(scene);
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.initOwner(usersTable.getScene().getWindow());
            controller.setDialogStage(dialogStage);
            controller.loadUserTasks(user);
            dialogStage.show();
        } catch (Exception e) {
            setError("Error opening tasks dialog: " + e.getMessage());
        }
    }

    private javafx.fxml.FXMLLoader createTasksDialogLoader() {
        return new javafx.fxml.FXMLLoader(
            com.taskmanagement.App.class.getResource("fxml/dialog/UserTasksDialog.fxml")
        );
    }
}
