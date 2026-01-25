package com.taskmanagement.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Rectangle;
import javafx.scene.Scene;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.function.Consumer;

import com.taskmanagement.model.Task;
import com.taskmanagement.model.Project;
import com.taskmanagement.model.User;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.service.ProjectService;
import com.taskmanagement.utils.DateUtils;
import com.taskmanagement.utils.UIUtils;
import com.taskmanagement.App;

public class ProjectDetailController {
    // FXML Paths
    private static final String TASK_DETAIL_VIEW = "fxml/main/TaskDetailView.fxml";
    private static final String CREATE_TASK_VIEW = "/com/taskmanagement/fxml/dialog/CreateTaskView.fxml";
    private static final String EDIT_PROJECT_VIEW = "fxml/main/EditProjectView.fxml";
    
    // Dialog Titles
    private static final String TITLE_TASK = "Task: ";
    private static final String TITLE_CREATE_TASK = "Create Task in ";
    private static final String TITLE_EDIT_PROJECT = "Edit Project - ";
    
    // Messages
    private static final String MSG_CONFIRM_DELETE_PROJECT = "Are you sure you want to delete this project? This action cannot be undone.";
    private static final String MSG_NO_DESCRIPTION = "No description";
    private static final String MSG_UNKNOWN = "Unknown";
    private static final String MSG_UNASSIGNED = "Unassigned";
    
    // Status Colors
    private static final Map<String, String> STATUS_COLORS = Map.of(
        "to do", "#e8f4f8",
        "in progress", "#fff3cd",
        "done", "#d4edda"
    );
    
    // Priority Colors
    private static final Map<String, String> PRIORITY_BG_COLORS = Map.of(
        "Low", "#DBEAFE",
        "Medium", "#FCD34D",
        "High", "#FCA5A5"
    );
    
    private static final Map<String, String> PRIORITY_TEXT_COLORS = Map.of(
        "Low", "#0C4A6E",
        "Medium", "#78350F",
        "High", "#7F1D1D"
    );
    
    @FXML private Label projectNameLabel;
    @FXML private Label projectDescLabel;
    @FXML private Label projectCreatedByLabel;
    @FXML private Label projectCreatedAtLabel;
    @FXML private Label projectColorLabel;
    @FXML private Rectangle colorBox;
    
    @FXML private TextField taskSearchField;
    @FXML private TableView<Task> tasksTable;
    @FXML private TableColumn<Task, Long> taskIdColumn;
    @FXML private TableColumn<Task, String> taskTitleColumn;
    @FXML private TableColumn<Task, String> taskStatusColumn;
    @FXML private TableColumn<Task, String> taskPriorityColumn;
    @FXML private TableColumn<Task, String> taskDueColumn;
    @FXML private TableColumn<Task, String> taskAssigneeColumn;
    
    @FXML private Label statTotalTasks;
    @FXML private Label statInProgress;
    @FXML private Label statCompleted;
    @FXML private Label statTodo;
    
    @FXML private Label detailProjectId;
    @FXML private Label detailCreatedBy;
    @FXML private Label detailCreatedAt;
    @FXML private Label detailDescription;
    
    private Project project;
    private TaskService taskService;
    private ProjectService projectService;
    private ObservableList<Task> tasksList;
    private FilteredList<Task> filteredTasks;
    private ProjectController projectController;
    
    public void initialize() {
        taskService = new TaskService();
        projectService = new ProjectService();
        setupTaskTableColumns();
        setupTaskSearchListener();
    }
    @FXML
    private void setupTaskSearchListener() {
        taskSearchField.textProperty().addListener((observable, oldValue, newValue) -> filterTasks(newValue));
    }
    
    @FXML
    private void clearTaskSearch() {
        taskSearchField.clear();
        filteredTasks.setPredicate(t -> true);
    }
    
    private void filterTasks(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredTasks.setPredicate(t -> true);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            filteredTasks.setPredicate(task ->
                task.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                (task.getDescription() != null && task.getDescription().toLowerCase().contains(lowerCaseFilter)) ||
                task.getStatus().toLowerCase().contains(lowerCaseFilter)
            );
        }
    }
    
    @FXML
    private void handleAddTaskToProject() {
        System.out.println("➕ Opening create task dialog for project: " + project.getName());
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(CREATE_TASK_VIEW));
            BorderPane dialogRoot = loader.load();
            CreateTaskController controller = loader.getController();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle(TITLE_CREATE_TASK + project.getName());
            dialogStage.setScene(new Scene(dialogRoot, 650, 700));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(projectNameLabel.getScene().getWindow());
            
            controller.setProject(project);
            controller.setDialogStage(dialogStage);
            controller.setOnTaskCreated(() -> {
                System.out.println("🔄 Refreshing task list after task creation");
                loadProjectTasks();
                updateStatistics();
            });
            
            dialogStage.showAndWait();
        } catch (IOException e) {
            showErrorAlert("Error opening create task dialog", e);
        }
    }
    
    @FXML
    private void handleEditProject() {
        System.out.println("Opening edit project dialog for: " + project.getName());
        
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(EDIT_PROJECT_VIEW));
            BorderPane editRoot = loader.load();
            EditProjectController controller = loader.getController();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle(TITLE_EDIT_PROJECT + project.getName());
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(editRoot, 500, 400));
            
            controller.setProject(project, dialogStage);
            controller.setOnSaveCallback(() -> {
                System.out.println("🔄 Refreshing project details after edit");
                Project updatedProject = projectService.getProjectById(project.getId());
                if (updatedProject != null) {
                    setProject(updatedProject);
                }
            });
            
            dialogStage.showAndWait();
        } catch (Exception e) {
            showErrorAlert("Failed to open edit dialog", e);
        }
    }
    
    @FXML
    private void handleDeleteProject() {
        Optional<ButtonType> result = showConfirmation("Delete Project", MSG_CONFIRM_DELETE_PROJECT);
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                projectService.deleteProject(project.getId());
                showSuccessAlert("Project deleted successfully!");
                handleClose();
            } catch (Exception e) {
                showErrorAlert("Failed to delete project", e);
            }
        }
    }
    
    @FXML
    private void handleClose() {
        System.out.println("Going back to Projects list");
        if (projectController != null) {
            projectController.clearProjectDetails();
        }
    }
    
    public void setProjectController(ProjectController controller) {
        this.projectController = controller;
    }
    
    public void setProject(Project project) {
        this.project = project;
        populateProjectDetails();
        loadProjectTasks();
    }
    
    
    private void populateProjectDetails() {
        projectNameLabel.setText(project.getName());
        projectDescLabel.setText(getOrDefault(project.getDescription(), MSG_NO_DESCRIPTION));
        
        String createdBy = project.getCreatedBy() != null ? project.getCreatedBy().getUsername() : MSG_UNKNOWN;
        projectCreatedByLabel.setText(createdBy);
        detailCreatedBy.setText(createdBy);
        
        String createdAt = DateUtils.formatDateTime(project.getCreatedAt());
        projectCreatedAtLabel.setText(createdAt);
        detailCreatedAt.setText(createdAt);
        
        String color = getOrDefault(project.getColor(), "#3498db");
        projectColorLabel.setText(color);
        colorBox.setStyle("-fx-fill: " + color + "; -fx-stroke: #bdc3c7; -fx-stroke-width: 1;");
        
        detailProjectId.setText(String.valueOf(project.getId()));
        detailDescription.setText(getOrDefault(project.getDescription(), MSG_NO_DESCRIPTION + " provided"));
    }
    
    
    private void setupTaskTableColumns() {
        taskIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        taskTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        taskStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        taskPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        
        taskStatusColumn.setCellFactory(param -> createStatusCell());
        taskPriorityColumn.setCellFactory(param -> createPriorityCell());
        
        taskDueColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                DateUtils.formatDate(cellData.getValue().getDueDate())
            )
        );
        
        taskAssigneeColumn.setCellValueFactory(cellData -> {
            User assignee = cellData.getValue().getAssignee();
            return new javafx.beans.property.SimpleStringProperty(
                assignee != null ? assignee.getUsername() : MSG_UNASSIGNED
            );
        });
        
        tasksTable.setRowFactory(tv -> createTaskRow());
    }
    
    private TableCell<Task, String> createStatusCell() {
        return new TableCell<Task, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(status);
                    String bgColor = STATUS_COLORS.getOrDefault(status.toLowerCase(), "#f0f0f0");
                    setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: #000000; " +
                            "-fx-padding: 5 10; -fx-border-radius: 3; -fx-font-weight: bold;");
                }
            }
        };
    }
    
    private TableCell<Task, String> createPriorityCell() {
        return new TableCell<Task, String>() {
            @Override
            protected void updateItem(String priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(priority);
                    String bgColor = PRIORITY_BG_COLORS.getOrDefault(priority, "#DBEAFE");
                    String textColor = PRIORITY_TEXT_COLORS.getOrDefault(priority, "#0C4A6E");
                    setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + 
                            "; -fx-padding: 3 8; -fx-border-radius: 2; -fx-font-weight: 600;");
                }
            }
        };
    }
    
    private TableRow<Task> createTaskRow() {
        TableRow<Task> row = new TableRow<Task>() {
            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
            }
        };
        row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !row.isEmpty()) {
                editTask(row.getItem());
            }
        });
        return row;
    }
    
    private String getOrDefault(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }
    
    
    private void editTask(Task task) {
        try {
            System.out.println("📂 Opening task detail popup for: " + task.getTitle());
            loadTaskDialog(TASK_DETAIL_VIEW, TITLE_TASK + task.getTitle(), 700, 600, 
                loader -> {
                    TaskDetailController controller = loader.getController();
                    controller.setTask(task, null);
                    controller.setOnSaveCallback(() -> {
                        System.out.println("Task updated, refreshing task list");
                        loadProjectTasks();
                    });
                    controller.setOnDeleteCallback(() -> {
                        System.out.println("Task deleted, refreshing task list");
                        loadProjectTasks();
                    });
                });
        } catch (IOException e) {
            showErrorAlert("Error opening task detail popup", e);
        }
    }
    
    private void loadProjectTasks() {
        try {
            List<Task> allTasks = taskService.getAllTasks();
            List<Task> projectTasks = allTasks.stream()
                .filter(t -> t.getProject() != null && t.getProject().getId().equals(project.getId()))
                .collect(Collectors.toList());
            
            tasksList = FXCollections.observableArrayList(projectTasks);
            filteredTasks = new FilteredList<>(tasksList, t -> true);
            tasksTable.setItems(filteredTasks);
            
            updateStatistics();
        } catch (Exception e) {
            showErrorAlert("Failed to load tasks", e);
        }
    }
    
    private void updateStatistics() {
        long total = tasksList.size();
        long inProgress = tasksList.stream().filter(t -> "In Progress".equals(t.getStatus())).count();
        long completed = tasksList.stream().filter(t -> "Done".equals(t.getStatus())).count();
        long todo = tasksList.stream().filter(t -> "To Do".equals(t.getStatus())).count();
        
        statTotalTasks.setText(String.valueOf(total));
        statInProgress.setText(String.valueOf(inProgress));
        statCompleted.setText(String.valueOf(completed));
        statTodo.setText(String.valueOf(todo));
    }
    
    private void loadTaskDialog(String fxmlPath, String title, int width, int height,
                               Consumer<FXMLLoader> setupController) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
        BorderPane view = loader.load();
        setupController.accept(loader);
        showDialog(view, title, width, height, Modality.WINDOW_MODAL);
    }
    
    private void showDialog(BorderPane view, String title, int width, int height, Modality modality) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(view, width, height));
        stage.initModality(modality);
        stage.initOwner(projectNameLabel.getScene().getWindow());
        stage.showAndWait();
    }
    
    private void showSuccessAlert(String message) {
        UIUtils.showSuccess("Success", message);
    }
    
    private void showErrorAlert(String title, Exception e) {
        System.err.println(title + ": " + e.getMessage());
        e.printStackTrace();
        UIUtils.showError(title, e.getMessage());
    }
    
    private Optional<ButtonType> showConfirmation(String title, String content) {
        return Optional.of(UIUtils.showCustomConfirmation(title, null, content) ? ButtonType.OK : ButtonType.CANCEL);
    }
}

