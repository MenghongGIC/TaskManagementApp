package com.taskmanagement.controller;

import com.taskmanagement.App;
import com.taskmanagement.model.Task;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.service.ProjectService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Optional;

/**
 * Manages task display across 3 views: Table, Kanban, List
 * View switching uses state management (no refetch required)
 */
public class ProjectTaskViewController implements TaskAwareController {
    
    @FXML private Label projectNameLabel;
    @FXML private Label projectDescLabel;
    @FXML private StackPane viewStack;
    
    // View toggles
    @FXML private Button tableViewBtn;
    @FXML private Button kanbanViewBtn;
    @FXML private Button listViewBtn;
    
    // Table View Components
    @FXML private VBox tableViewContainer;
    @FXML private TableView<Task> tasksTable;
    @FXML private TableColumn<Task, String> nameColumn;
    @FXML private TableColumn<Task, String> descriptionColumn;
    @FXML private TableColumn<Task, String> statusColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, String> dueDateColumn;
    @FXML private TableColumn<Task, String> assigneeColumn;
    @FXML private TableColumn<Task, Void> actionsColumn;
    
    // Kanban View Components
    @FXML private VBox kanbanViewContainer;
    @FXML private FlowPane todoColumn;
    @FXML private FlowPane inProgressColumn;
    @FXML private FlowPane doneColumn;
    
    // List View Components
    @FXML private VBox listViewContainer;
    @FXML private VBox taskListContent;
    
    private ProjectService projectService;
    private TaskRepository taskRepository;
    private MainLayoutController mainLayoutController;
    private Task draggedTask = null;

    @Override
    public void setMainLayoutController(MainLayoutController controller) {
        this.mainLayoutController = controller;
    }

    @FXML
    public void initialize() {
        projectService = new ProjectService();
        taskRepository = new TaskRepository();
        
        setupViewToggleButtons();
        displayProjectInfo();
        switchToView(projectService.getViewType());
    }
    
    /**
     * Display current project information
     */
    private void displayProjectInfo() {
        if (projectService.getSelectedProject() != null) {
            if (projectNameLabel != null) {
                projectNameLabel.setText(projectService.getSelectedProject().getName());
            }
            if (projectDescLabel != null) {
                projectDescLabel.setText(projectService.getSelectedProject().getDescription() != null ?
                    projectService.getSelectedProject().getDescription() : "");
            }
        }
    }
    
    /**
     * Setup view toggle button handlers
     */
    private void setupViewToggleButtons() {
        if (tableViewBtn != null) {
            tableViewBtn.setOnAction(e -> switchToTableView());
        }
        if (kanbanViewBtn != null) {
            kanbanViewBtn.setOnAction(e -> switchToKanbanView());
        }
        if (listViewBtn != null) {
            listViewBtn.setOnAction(e -> switchToListView());
        }
    }
    
    // ===== TABLE VIEW =====
    
    private void switchToTableView() {
        projectService.setViewType("table");
        switchToView("table");
        setupTableView();
    }
    
    private void setupTableView() {
        if (tasksTable == null) return;
        
        // Setup columns
        nameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));
        descriptionColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDescription() != null ? 
                cellData.getValue().getDescription() : "-"));
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
        priorityColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPriority()));
        dueDateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDueDate() != null ? 
                cellData.getValue().getDueDate().toString() : "-"));
        assigneeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getAssignee() != null ? 
                cellData.getValue().getAssignee().getUsername() : "-"));
        
        // Setup actions column
        setupTableActionsColumn();
        
        // Populate table with project tasks
        tasksTable.setItems(projectService.getProjectTasks());
    }
    
    private void setupTableActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<Task, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || getTableView().getItems().isEmpty()) {
                    setGraphic(null);
                    return;
                }
                
                Task task = getTableView().getItems().get(getIndex());
                
                ComboBox<String> statusCombo = new ComboBox<>();
                statusCombo.getItems().addAll("To Do", "In Progress", "Done");
                statusCombo.setValue(task.getStatus());
                statusCombo.setStyle("-fx-font-size: 10px; -fx-padding: 3;");
                statusCombo.setOnAction(e -> {
                    try {
                        task.setStatus(statusCombo.getValue());
                        taskRepository.update(task);
                        projectService.updateTaskStatus(task.getId(), statusCombo.getValue());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                
                Button deleteBtn = new Button("Delete");
                deleteBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3 8;");
                deleteBtn.setOnAction(e -> handleDeleteTask(task));
                
                HBox actions = new HBox(5, statusCombo, deleteBtn);
                actions.setAlignment(Pos.CENTER);
                setGraphic(actions);
            }
        });
    }
    
    // ===== KANBAN VIEW =====
    
    private void switchToKanbanView() {
        projectService.setViewType("kanban");
        switchToView("kanban");
        setupKanbanView();
    }
    
    private void setupKanbanView() {
        if (todoColumn == null || inProgressColumn == null || doneColumn == null) return;
        
        todoColumn.getChildren().clear();
        inProgressColumn.getChildren().clear();
        doneColumn.getChildren().clear();
        
        for (Task task : projectService.getProjectTasks()) {
            VBox card = createKanbanCard(task);
            
            if ("In Progress".equalsIgnoreCase(task.getStatus())) {
                inProgressColumn.getChildren().add(card);
            } else if ("Done".equalsIgnoreCase(task.getStatus())) {
                doneColumn.getChildren().add(card);
            } else {
                todoColumn.getChildren().add(card);
            }
        }
    }
    
    private VBox createKanbanCard(Task task) {
        VBox card = new VBox(5);
        card.setStyle(
            "-fx-border-color: #bdc3c7; -fx-border-width: 1; " +
            "-fx-padding: 10; -fx-background-color: white; " +
            "-fx-border-radius: 4; -fx-cursor: hand;"
        );
        card.setMinWidth(200);
        card.setMaxWidth(200);
        card.setUserData(task);
        
        // Task title
        Label titleLabel = new Label(task.getTitle());
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        titleLabel.setWrapText(true);
        
        // Priority badge
        String priorityColor = getPriorityColor(task.getPriority());
        Label priorityLabel = new Label("Priority: " + task.getPriority());
        priorityLabel.setStyle(
            "-fx-font-size: 10px; -fx-text-fill: white; -fx-padding: 3 8; " +
            "-fx-background-color: " + priorityColor + "; -fx-background-radius: 3;"
        );
        
        // Assignee
        String assignee = task.getAssignee() != null ? 
                         task.getAssignee().getUsername() : "Unassigned";
        Label assigneeLabel = new Label("📌 " + assignee);
        assigneeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
        
        // Due date
        if (task.getDueDate() != null) {
            Label dueDateLabel = new Label("📅 " + task.getDueDate());
            dueDateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #e74c3c;");
            card.getChildren().addAll(titleLabel, priorityLabel, assigneeLabel, dueDateLabel);
        } else {
            card.getChildren().addAll(titleLabel, priorityLabel, assigneeLabel);
        }
        
        // Drag handlers
        setupCardDragHandlers(card);
        
        return card;
    }
    
    private void setupCardDragHandlers(VBox card) {
        card.setOnDragDetected(event -> {
            draggedTask = (Task) card.getUserData();
            Dragboard db = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(draggedTask.getId().toString());
            db.setContent(content);
            event.consume();
        });
    }
    
    // ===== LIST VIEW =====
    
    private void switchToListView() {
        projectService.setViewType("list");
        switchToView("list");
        setupListView();
    }
    
    private void setupListView() {
        if (taskListContent == null) return;
        
        taskListContent.getChildren().clear();
        
        for (Task task : projectService.getProjectTasks()) {
            HBox listItem = createListItem(task);
            taskListContent.getChildren().add(listItem);
        }
    }
    
    private HBox createListItem(Task task) {
        HBox item = new HBox(15);
        item.setStyle(
            "-fx-padding: 12; -fx-border-color: #ecf0f1; " +
            "-fx-border-width: 0 0 1 0; -fx-cursor: hand;"
        );
        
        // Status indicator
        String statusColor = getStatusColor(task.getStatus());
        VBox statusIndicator = new VBox();
        statusIndicator.setPrefWidth(5);
        statusIndicator.setStyle("-fx-background-color: " + statusColor + ";");
        
        // Task details
        VBox details = new VBox(3);
        Label titleLabel = new Label(task.getTitle());
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        HBox infoRow = new HBox(15);
        Label statusLabel = new Label("Status: " + task.getStatus());
        statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        
        String assignee = task.getAssignee() != null ? 
                         task.getAssignee().getUsername() : "Unassigned";
        Label assigneeLabel = new Label("Assignee: " + assignee);
        assigneeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        
        infoRow.getChildren().addAll(statusLabel, assigneeLabel);
        details.getChildren().addAll(titleLabel, infoRow);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        item.getChildren().addAll(statusIndicator, details, spacer);
        
        return item;
    }
    
    // ===== UTILITY METHODS =====
    
    private void switchToView(String viewType) {
        if (viewStack == null) return;
        
        tableViewBtn.setStyle("-fx-background-color: " + (viewType.equals("table") ? "#3498db" : "#95a5a6") + "; -fx-text-fill: white;");
        kanbanViewBtn.setStyle("-fx-background-color: " + (viewType.equals("kanban") ? "#3498db" : "#95a5a6") + "; -fx-text-fill: white;");
        listViewBtn.setStyle("-fx-background-color: " + (viewType.equals("list") ? "#3498db" : "#95a5a6") + "; -fx-text-fill: white;");
    }
    
    private String getStatusColor(String status) {
        return switch (status.toLowerCase()) {
            case "to do" -> "#3498db";
            case "in progress" -> "#f39c12";
            case "done" -> "#27ae60";
            default -> "#95a5a6";
        };
    }
    
    private String getPriorityColor(String priority) {
        return switch (priority.toUpperCase()) {
            case "HIGH" -> "#e74c3c";
            case "MEDIUM" -> "#f39c12";
            case "LOW" -> "#27ae60";
            default -> "#95a5a6";
        };
    }
    
    private void handleDeleteTask(Task task) {
        Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete task: " + task.getTitle() + "?",
            ButtonType.OK, ButtonType.CANCEL).showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                taskRepository.delete(task.getId());
                projectService.removeTask(task.getId());
                setupTableView(); // Refresh current view
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Failed to delete task: " + e.getMessage()).show();
            }
        }
    }
    
    @FXML
    private void handleBackToProjects() {
        try {
            App.setRoot("main/ProjectListView");
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Navigation error: " + e.getMessage()).show();
        }
    }
}
