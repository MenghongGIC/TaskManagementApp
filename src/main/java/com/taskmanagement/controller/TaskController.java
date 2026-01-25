package com.taskmanagement.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.service.UserService;
import com.taskmanagement.utils.CurrentUser;

public class TaskController {

    @FXML private TextField searchField;
    @FXML private TableView<Task> tasksTable;
    @FXML private TableColumn<Task, String> titleColumn;
    @FXML private TableColumn<Task, String> descriptionColumn;
    @FXML private TableColumn<Task, String> statusColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, LocalDate> dueDateColumn;
    @FXML private TableColumn<Task, String> assigneeColumn;
    @FXML private Button createBtn;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;
    @FXML private Button refreshBtn;
    @FXML private Button updateProgressBtn;

    private TaskService taskService;
    private UserService userService;
    private ObservableList<Task> tasksList;

    @FXML
    public void initialize() {
        taskService = new TaskService();
        userService = new UserService();
        tasksList = FXCollections.observableArrayList();
        
        setupTableColumns();
        loadTasks();
        setupEventHandlers();
        configureButtonsByRole();
    }
    
    private void configureButtonsByRole() {
        boolean isAdmin = CurrentUser.isAdmin();
        
        
        if (createBtn != null) createBtn.setVisible(isAdmin);
        if (editBtn != null) editBtn.setVisible(isAdmin);
        if (deleteBtn != null) deleteBtn.setVisible(isAdmin);
        
        
        if (updateProgressBtn != null) {
            updateProgressBtn.setVisible(!isAdmin);
        }
    }

    private void setupTableColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        assigneeColumn.setCellValueFactory(cellData -> {
            String assigneeName = cellData.getValue().getAssignee() != null 
                ? cellData.getValue().getAssignee().getUsername() 
                : "Unassigned";
            return new javafx.beans.property.SimpleStringProperty(assigneeName);
        });
    }

    private void setupEventHandlers() {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterTasks(newVal));
        }
    }

    private void loadTasks() {
        try {
            List<Task> tasks = taskService.getAllTasks();
            tasksList.clear();
            tasksList.addAll(tasks);
            tasksTable.setItems(tasksList);
        } catch (Exception e) {
            showError("Error loading tasks: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateTask() {
        if (!CurrentUser.isAdmin()) {
            showError("Access Denied: Admin only");
            return;
        }

        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Create New Task");
        dialog.setHeaderText("Add a new task");

        GridPane grid = createTaskForm(null);
        dialog.getDialogPane().setContent(grid);

        ButtonType saveButton = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, cancelButton);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                return extractTaskFromForm(grid, null);
            }
            return null;
        });

        Optional<Task> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                Task taskData = result.get();
                
                com.taskmanagement.repository.TaskRepository repo = new com.taskmanagement.repository.TaskRepository();
                Task newTask = repo.save(taskData);
                if (newTask != null) {
                    tasksList.add(newTask);
                    showSuccess("Task created successfully");
                }
            } catch (Exception e) {
                showError("Error creating task: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleEditTask() {
        if (!CurrentUser.isAdmin()) {
            showError("Access Denied: Admin only");
            return;
        }

        Task selectedTask = tasksTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showError("Please select a task to edit");
            return;
        }

        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Edit Task");
        dialog.setHeaderText("Edit task details");

        GridPane grid = createTaskForm(selectedTask);
        dialog.getDialogPane().setContent(grid);

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, cancelButton);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                return extractTaskFromForm(grid, selectedTask);
            }
            return null;
        });

        Optional<Task> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                Task updatedData = result.get();
                com.taskmanagement.repository.TaskRepository repo = new com.taskmanagement.repository.TaskRepository();
                Task updatedTask = repo.update(updatedData);
                int index = tasksList.indexOf(selectedTask);
                if (index >= 0) {
                    tasksList.set(index, updatedTask);
                }
                showSuccess("Task updated successfully");
            } catch (Exception e) {
                showError("Error updating task: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleDeleteTask() {
        if (!CurrentUser.isAdmin()) {
            showError("Access Denied: Admin only");
            return;
        }

        Task selectedTask = tasksTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showError("Please select a task to delete");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Task");
        confirmation.setHeaderText("Are you sure?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                com.taskmanagement.repository.TaskRepository repo = new com.taskmanagement.repository.TaskRepository();
                repo.delete(selectedTask.getId());
                tasksList.remove(selectedTask);
                showSuccess("Task deleted successfully");
            } catch (Exception e) {
                showError("Error deleting task: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRefresh() {
        loadTasks();
        showSuccess("Tasks refreshed");
    }

    @FXML
    private void handleUpdateProgress() {
        Task selectedTask = tasksTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showError("Please select a task to update");
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Update Task Progress");
        dialog.setHeaderText("Change task status to: ");

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList("To Do", "In Progress", "Done", "On Hold"));
        statusCombo.setValue(selectedTask.getStatus());

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.getChildren().addAll(
            new Label("Current Status: " + selectedTask.getStatus()),
            new Label("New Status:"),
            statusCombo
        );

        dialog.getDialogPane().setContent(content);

        ButtonType updateButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButton, cancelButton);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButton) {
                return statusCombo.getValue();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                String newStatus = result.get();
                if (!newStatus.equals(selectedTask.getStatus())) {
                    taskService.changeTaskStatus(selectedTask.getId(), newStatus);
                    selectedTask.setStatus(newStatus);
                    tasksTable.refresh();
                    showSuccess("Task progress updated successfully");
                } else {
                    showSuccess("No change made");
                }
            } catch (Exception e) {
                showError("Error updating task progress: " + e.getMessage());
            }
        }
    }

    private GridPane createTaskForm(Task task) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        
        Label titleLabel = new Label("Title:");
        TextField titleField = new TextField();
        titleField.setStyle("-fx-font-size: 12;");
        if (task != null) titleField.setText(task.getTitle());

        
        Label descLabel = new Label("Description:");
        TextArea descArea = new TextArea();
        descArea.setWrapText(true);
        descArea.setPrefRowCount(4);
        if (task != null) descArea.setText(task.getDescription());

        
        Label statusLabel = new Label("Status:");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList("To Do", "In Progress", "Done", "On Hold"));
        if (task != null) statusCombo.setValue(task.getStatus());

        
        Label priorityLabel = new Label("Priority:");
        ComboBox<String> priorityCombo = new ComboBox<>();
        priorityCombo.setItems(FXCollections.observableArrayList("Low", "Medium", "High", "Critical"));
        if (task != null) priorityCombo.setValue(task.getPriority());

        
        Label dateLabel = new Label("Due Date:");
        DatePicker datePicker = new DatePicker();
        if (task != null && task.getDueDate() != null) datePicker.setValue(task.getDueDate());

        
        Label assigneeLabel = new Label("Assign User:");
        ComboBox<User> userCombo = new ComboBox<>();
        try {
            List<User> users = userService.getAllUsers();
            userCombo.setItems(FXCollections.observableArrayList(users));
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        if (task != null && task.getAssignee() != null) {
            userCombo.setValue(task.getAssignee());
        }

        
        grid.add(titleLabel, 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(descLabel, 0, 1);
        grid.add(descArea, 1, 1);
        grid.add(statusLabel, 0, 2);
        grid.add(statusCombo, 1, 2);
        grid.add(priorityLabel, 0, 3);
        grid.add(priorityCombo, 1, 3);
        grid.add(dateLabel, 0, 4);
        grid.add(datePicker, 1, 4);
        grid.add(assigneeLabel, 0, 5);
        grid.add(userCombo, 1, 5);

        
        grid.setUserData(new Object[]{titleField, descArea, statusCombo, priorityCombo, datePicker, userCombo});

        return grid;
    }

    private Task extractTaskFromForm(GridPane grid, Task existingTask) {
        Object[] fields = (Object[]) grid.getUserData();
        TextField titleField = (TextField) fields[0];
        TextArea descArea = (TextArea) fields[1];
        ComboBox<String> statusCombo = (ComboBox<String>) fields[2];
        ComboBox<String> priorityCombo = (ComboBox<String>) fields[3];
        DatePicker datePicker = (DatePicker) fields[4];
        ComboBox<User> userCombo = (ComboBox<User>) fields[5];

        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showError("Title is required");
            return null;
        }

        Task task = existingTask != null ? existingTask : new Task();
        task.setTitle(title);
        task.setDescription(descArea.getText());
        task.setStatus(statusCombo.getValue() != null ? statusCombo.getValue() : "To Do");
        task.setPriority(priorityCombo.getValue() != null ? priorityCombo.getValue() : "Medium");
        task.setDueDate(datePicker.getValue());
        task.setAssignee(userCombo.getValue());

        return task;
    }

    private void filterTasks(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            tasksTable.setItems(tasksList);
            return;
        }

        String searchLower = searchText.toLowerCase();
        ObservableList<Task> filtered = FXCollections.observableArrayList();

        for (Task task : tasksList) {
            if (task.getTitle().toLowerCase().contains(searchLower) ||
                (task.getDescription() != null && task.getDescription().toLowerCase().contains(searchLower))) {
                filtered.add(task);
            }
        }

        tasksTable.setItems(filtered);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
