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
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.service.UserService;
import com.taskmanagement.utils.CurrentUser;
import com.taskmanagement.utils.UIUtils;

public class TaskController {
    
    // Status & Priority Values
    private static final String[] STATUS_OPTIONS = {"To Do", "In Progress", "Done", "On Hold"};
    private static final String[] PRIORITY_OPTIONS = {"Low", "Medium", "High", "Critical"};
    private static final String DEFAULT_STATUS = "To Do";
    private static final String DEFAULT_PRIORITY = "Medium";
    
    // Dialog Messages
    private static final String TITLE_CREATE = "Create New Task";
    private static final String TITLE_EDIT = "Edit Task";
    private static final String TITLE_UPDATE_PROGRESS = "Update Task Progress";
    private static final String TITLE_DELETE = "Delete Task";
    
    // Error Messages
    private static final String MSG_ACCESS_DENIED = "Access Denied: Admin only";
    private static final String MSG_SELECT_TO_EDIT = "Please select a task to edit";
    private static final String MSG_SELECT_TO_DELETE = "Please select a task to delete";
    private static final String MSG_SELECT_TO_UPDATE = "Please select a task to update";
    private static final String MSG_TITLE_REQUIRED = "Title is required";
    private static final String MSG_LOAD_USERS_ERROR = "Error loading users: ";
    private static final String MSG_LOAD_TASKS_ERROR = "Error loading tasks: ";
    private static final String MSG_CREATE_ERROR = "Error creating task: ";
    private static final String MSG_UPDATE_ERROR = "Error updating task: ";
    private static final String MSG_DELETE_ERROR = "Error deleting task: ";
    private static final String MSG_UPDATE_PROGRESS_ERROR = "Error updating task progress: ";
    
    // Success Messages
    private static final String MSG_CREATE_SUCCESS = "Task created successfully";
    private static final String MSG_UPDATE_SUCCESS = "Task updated successfully";
    private static final String MSG_DELETE_SUCCESS = "Task deleted successfully";
    private static final String MSG_PROGRESS_SUCCESS = "Task progress updated successfully";
    private static final String MSG_REFRESH_SUCCESS = "Tasks refreshed";
    private static final String MSG_NO_CHANGE = "No change made";
    private static final String MSG_DELETE_CONFIRM = "This action cannot be undone.";
    private static final String MSG_UNASSIGNED = "Unassigned";

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
    private TaskRepository taskRepository;
    private ObservableList<Task> tasksList;
    private List<User> allUsers;

    @FXML
    public void initialize() {
        taskService = new TaskService();
        userService = new UserService();
        taskRepository = new TaskRepository();
        tasksList = FXCollections.observableArrayList();
        
        setupUI();
        loadAllUsers();
        configureButtonsByRole();
    }
    
    private void setupUI() {
        setupTableColumns();
        setupEventHandlers();
        loadTasks();
    }
    
    private void loadAllUsers() {
        try {
            allUsers = userService.getAllUsers();
        } catch (Exception e) {
            showError(MSG_LOAD_USERS_ERROR + e.getMessage());
            allUsers = List.of();
        }
    }
    
    private void configureButtonsByRole() {
        boolean isAdmin = CurrentUser.isAdmin();
        if (createBtn != null) createBtn.setVisible(isAdmin);
        if (editBtn != null) editBtn.setVisible(isAdmin);
        if (deleteBtn != null) deleteBtn.setVisible(isAdmin);
        if (updateProgressBtn != null) updateProgressBtn.setVisible(!isAdmin);
    }

    private void setupTableColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        assigneeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(getAssigneeDisplay(cellData.getValue())));
    }
    
    private String getAssigneeDisplay(Task task) {
        return task.getAssignee() != null ? task.getAssignee().getUsername() : MSG_UNASSIGNED;
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
            showError(MSG_LOAD_TASKS_ERROR + e.getMessage());
        }
    }

    @FXML
    private void handleCreateTask() {
        if (!checkAdminAccess()) return;
        
        Dialog<Task> dialog = createTaskDialog(TITLE_CREATE, null);
        Optional<Task> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            try {
                Task newTask = taskRepository.save(result.get());
                if (newTask != null) {
                    tasksList.add(newTask);
                    showSuccess(MSG_CREATE_SUCCESS);
                }
            } catch (Exception e) {
                showError(MSG_CREATE_ERROR + e.getMessage());
            }
        }
    }

    @FXML
    private void handleEditTask() {
        if (!checkAdminAccess()) return;
        
        Task selectedTask = tasksTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showError(MSG_SELECT_TO_EDIT);
            return;
        }

        Dialog<Task> dialog = createTaskDialog(TITLE_EDIT, selectedTask);
        Optional<Task> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            try {
                Task updatedTask = taskRepository.update(result.get());
                int index = tasksList.indexOf(selectedTask);
                if (index >= 0) {
                    tasksList.set(index, updatedTask);
                }
                showSuccess(MSG_UPDATE_SUCCESS);
            } catch (Exception e) {
                showError(MSG_UPDATE_ERROR + e.getMessage());
            }
        }
    }

    @FXML
    private void handleDeleteTask() {
        if (!checkAdminAccess()) return;
        
        Task selectedTask = tasksTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showError(MSG_SELECT_TO_DELETE);
            return;
        }

        Optional<ButtonType> result = showConfirmation(TITLE_DELETE, MSG_DELETE_CONFIRM);
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                taskRepository.delete(selectedTask.getId());
                tasksList.remove(selectedTask);
                showSuccess(MSG_DELETE_SUCCESS);
            } catch (Exception e) {
                showError(MSG_DELETE_ERROR + e.getMessage());
            }
        }
    }
    
    private boolean checkAdminAccess() {
        if (!CurrentUser.isAdmin()) {
            showError(MSG_ACCESS_DENIED);
            return false;
        }
        return true;
    }

    @FXML
    private void handleRefresh() {
        loadTasks();
        showSuccess(MSG_REFRESH_SUCCESS);
    }

    @FXML
    private void handleUpdateProgress() {
        Task selectedTask = tasksTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showError(MSG_SELECT_TO_UPDATE);
            return;
        }

        Dialog<String> dialog = createProgressDialog(selectedTask);
        Optional<String> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            try {
                String newStatus = result.get();
                if (!newStatus.equals(selectedTask.getStatus())) {
                    taskService.changeTaskStatus(selectedTask.getId(), newStatus);
                    selectedTask.setStatus(newStatus);
                    tasksTable.refresh();
                    showSuccess(MSG_PROGRESS_SUCCESS);
                } else {
                    showSuccess(MSG_NO_CHANGE);
                }
            } catch (Exception e) {
                showError(MSG_UPDATE_PROGRESS_ERROR + e.getMessage());
            }
        }
    }
    
    private Dialog<String> createProgressDialog(Task task) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(TITLE_UPDATE_PROGRESS);
        dialog.setHeaderText("Change task status to: ");

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList(STATUS_OPTIONS));
        statusCombo.setValue(task.getStatus());

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.getChildren().addAll(
            new Label("Current Status: " + task.getStatus()),
            new Label("New Status:"),
            statusCombo
        );

        dialog.getDialogPane().setContent(content);

        ButtonType updateButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButton, cancelButton);

        dialog.setResultConverter(dialogButton -> 
            dialogButton == updateButton ? statusCombo.getValue() : null
        );

        return dialog;
    }

    private Dialog<Task> createTaskDialog(String title, Task task) {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(task == null ? "Add a new task" : "Edit task details");

        GridPane grid = createTaskForm(task);
        dialog.getDialogPane().setContent(grid);

        ButtonType saveButton = new ButtonType(task == null ? "Create" : "Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, cancelButton);

        dialog.setResultConverter(dialogButton -> 
            dialogButton == saveButton ? extractTaskFromForm(grid, task) : null
        );

        return dialog;
    }
    
    private GridPane createTaskForm(Task task) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titleField = createFormField("Title:", grid, 0, task != null ? task.getTitle() : "");
        TextArea descArea = createTextArea("Description:", grid, 1, task != null ? task.getDescription() : "");
        ComboBox<String> statusCombo = createComboBox("Status:", grid, 2, STATUS_OPTIONS, 
            task != null ? task.getStatus() : DEFAULT_STATUS);
        ComboBox<String> priorityCombo = createComboBox("Priority:", grid, 3, PRIORITY_OPTIONS, 
            task != null ? task.getPriority() : DEFAULT_PRIORITY);
        
        DatePicker datePicker = new DatePicker();
        grid.add(new Label("Due Date:"), 0, 4);
        grid.add(datePicker, 1, 4);
        if (task != null && task.getDueDate() != null) datePicker.setValue(task.getDueDate());

        ComboBox<User> userCombo = new ComboBox<>();
        userCombo.setItems(FXCollections.observableArrayList(allUsers));
        grid.add(new Label("Assign User:"), 0, 5);
        grid.add(userCombo, 1, 5);
        if (task != null && task.getAssignee() != null) userCombo.setValue(task.getAssignee());

        grid.setUserData(new Object[]{titleField, descArea, statusCombo, priorityCombo, datePicker, userCombo});
        return grid;
    }
    
    private TextField createFormField(String label, GridPane grid, int row, String initialValue) {
        TextField field = new TextField();
        field.setStyle("-fx-font-size: 12;");
        field.setText(initialValue);
        grid.add(new Label(label), 0, row);
        grid.add(field, 1, row);
        return field;
    }
    
    private TextArea createTextArea(String label, GridPane grid, int row, String initialValue) {
        TextArea area = new TextArea();
        area.setWrapText(true);
        area.setPrefRowCount(4);
        area.setText(initialValue);
        grid.add(new Label(label), 0, row);
        grid.add(area, 1, row);
        return area;
    }
    
    private ComboBox<String> createComboBox(String label, GridPane grid, int row, String[] options, String selected) {
        ComboBox<String> combo = new ComboBox<>();
        combo.setItems(FXCollections.observableArrayList(options));
        combo.setValue(selected);
        grid.add(new Label(label), 0, row);
        grid.add(combo, 1, row);
        return combo;
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
            showError(MSG_TITLE_REQUIRED);
            return null;
        }

        Task task = existingTask != null ? existingTask : new Task();
        task.setTitle(title);
        task.setDescription(descArea.getText());
        task.setStatus(getOrDefault(statusCombo.getValue(), DEFAULT_STATUS));
        task.setPriority(getOrDefault(priorityCombo.getValue(), DEFAULT_PRIORITY));
        task.setDueDate(datePicker.getValue());
        task.setAssignee(userCombo.getValue());

        return task;
    }
    
    private String getOrDefault(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }

    private void filterTasks(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            tasksTable.setItems(tasksList);
            return;
        }

        String searchLower = searchText.toLowerCase();
        ObservableList<Task> filtered = FXCollections.observableArrayList();

        for (Task task : tasksList) {
            if (matchesSearch(task, searchLower)) {
                filtered.add(task);
            }
        }

        tasksTable.setItems(filtered);
    }
    
    private boolean matchesSearch(Task task, String searchLower) {
        return task.getTitle().toLowerCase().contains(searchLower) ||
               (task.getDescription() != null && task.getDescription().toLowerCase().contains(searchLower));
    }

    private void showError(String message) {
        UIUtils.showError("Error", message);
    }

    private void showSuccess(String message) {
        UIUtils.showSuccess("Success", message);
    }
    
    private Optional<ButtonType> showConfirmation(String title, String content) {
        return Optional.of(UIUtils.showCustomConfirmation(title, null, content) ? ButtonType.OK : ButtonType.CANCEL);
    }
}
