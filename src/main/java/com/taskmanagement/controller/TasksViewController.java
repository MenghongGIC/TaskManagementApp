package com.taskmanagement.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.taskmanagement.model.Project;
import com.taskmanagement.model.Task;
import com.taskmanagement.service.TaskService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;



public class TasksViewController implements TaskAwareController {
    
    @FXML private StackPane viewStack;
    @FXML private Button tableViewBtn, kanbanViewBtn, listViewBtn;
    
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private Label statusLabel;
    @FXML private Label taskCountLabel;
    @FXML private Label filterStatusLabel;
    @FXML private Label bottomStatusLabel;
    
    
    @FXML private TableView<Task> tasksTableView;
    
    
    @FXML private FlowPane todoColumn, inProgressColumn, doneColumn;
    
    
    @FXML private VBox taskListContainer;
    
    private TaskService taskService;
    private ObservableList<Task> allTasks;
    private ObservableList<Task> filteredTasks;
    private MainLayoutController mainLayoutController;
    private Project selectedProject;
    private int currentViewIndex = 0; 
    
    @FXML
    public void initialize() {
        System.out.println("🔧 Initializing TasksViewController");
        
        taskService = new TaskService();
        allTasks = FXCollections.observableArrayList();
        filteredTasks = FXCollections.observableArrayList();
        
        
        statusComboBox.setItems(FXCollections.observableArrayList("All", "To Do", "In Progress", "Done"));
        statusComboBox.setValue("All");
        priorityComboBox.setItems(FXCollections.observableArrayList("All", "Low", "Medium", "High"));
        priorityComboBox.setValue("All");
        
        
        setupSearchAndFilter();
        
        
        initializeTableView();
        
        
        setupKanbanDropZones();
        
        
        
        
        System.out.println("✅ TasksViewController initialized");
    }
    
    

    private void setupSearchAndFilter() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        statusComboBox.setOnAction(e -> applyFilters());
        priorityComboBox.setOnAction(e -> applyFilters());
    }
    
    

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String statusFilter = statusComboBox.getValue();
        String priorityFilter = priorityComboBox.getValue();
        
        filteredTasks.clear();
        
        for (Task task : allTasks) {
            
            boolean matchesSearch = searchText.isEmpty() || 
                task.getTitle().toLowerCase().contains(searchText) ||
                (task.getDescription() != null && task.getDescription().toLowerCase().contains(searchText));
            
            
            boolean matchesStatus = statusFilter.equals("All") || 
                (task.getStatus() != null && task.getStatus().equals(statusFilter));
            
            
            boolean matchesPriority = priorityFilter.equals("All") || 
                (task.getPriority() != null && task.getPriority().equals(priorityFilter));
            
            if (matchesSearch && matchesStatus && matchesPriority) {
                filteredTasks.add(task);
            }
        }
        
        
        updateTableView();
        updateKanbanView();
        updateListView();
        
        
        updateStatusLabels();
    }
    
    

    private void updateStatusLabels() {
        taskCountLabel.setText(filteredTasks.size() + " of " + allTasks.size() + " tasks");
        
        String filterInfo = "";
        if (!searchField.getText().isEmpty()) {
            filterInfo += "Search: '" + searchField.getText() + "' ";
        }
        if (!statusComboBox.getValue().equals("All")) {
            filterInfo += "Status: " + statusComboBox.getValue() + " ";
        }
        if (!priorityComboBox.getValue().equals("All")) {
            filterInfo += "Priority: " + priorityComboBox.getValue();
        }
        
        filterStatusLabel.setText(filterInfo.isEmpty() ? "" : "Filters: " + filterInfo);
    }
    
    

    @FXML
    private void clearFilters() {
        searchField.clear();
        statusComboBox.setValue("All");
        priorityComboBox.setValue("All");
    }
    
    
    
    @FXML
    private void showTableView() {
        currentViewIndex = 0;
        viewStack.getChildren().get(0).setVisible(true);
        viewStack.getChildren().get(1).setVisible(false);
        viewStack.getChildren().get(2).setVisible(false);
        updateButtonStyles();
    }
    
    @FXML
    private void showKanbanView() {
        currentViewIndex = 1;
        viewStack.getChildren().get(0).setVisible(false);
        viewStack.getChildren().get(1).setVisible(true);
        viewStack.getChildren().get(2).setVisible(false);
        updateButtonStyles();
    }
    
    @FXML
    private void showListView() {
        currentViewIndex = 2;
        viewStack.getChildren().get(0).setVisible(false);
        viewStack.getChildren().get(1).setVisible(false);
        viewStack.getChildren().get(2).setVisible(true);
        updateButtonStyles();
    }
    
    

    private void updateButtonStyles() {
        tableViewBtn.setStyle("-fx-padding: 8 15; -fx-font-size: 11px; -fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 3;");
        kanbanViewBtn.setStyle("-fx-padding: 8 15; -fx-font-size: 11px; -fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 3;");
        listViewBtn.setStyle("-fx-padding: 8 15; -fx-font-size: 11px; -fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 3;");
        
        if (currentViewIndex == 0) {
            tableViewBtn.setStyle("-fx-padding: 8 15; -fx-font-size: 11px; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 3;");
        } else if (currentViewIndex == 1) {
            kanbanViewBtn.setStyle("-fx-padding: 8 15; -fx-font-size: 11px; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 3;");
        } else {
            listViewBtn.setStyle("-fx-padding: 8 15; -fx-font-size: 11px; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 3;");
        }
    }
    
    
    
    

    @FXML
    private void loadTasks() {
        try {
            if (selectedProject == null) {
                statusLabel.setText("⚠️ Please select a project first");
                taskCountLabel.setText("0 tasks");
                return;
            }
            
            System.out.println("📥 Loading tasks for project: " + selectedProject.getName());
            
            
            allTasks.clear();
            allTasks.addAll(taskService.getTasksByProject(selectedProject.getId()));
            
            applyFilters();
            statusLabel.setText("✓ Tasks loaded");
            
        } catch (Exception e) {
            System.err.println("Error loading tasks: " + e.getMessage());
            statusLabel.setText("Error loading tasks");
        }
    }
    
    @FXML
    private void refreshTasks() {
        loadTasks();
    }
    
    
    
    private void initializeTableView() {
        tasksTableView.getColumns().clear();
        
        TableColumn<Task, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleLongProperty(cellData.getValue().getId()).asObject());
        idColumn.setPrefWidth(50);
        
        TableColumn<Task, String> titleColumn = new TableColumn<>("Task");
        titleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));
        titleColumn.setPrefWidth(200);
        
        TableColumn<Task, String> descColumn = new TableColumn<>("Description");
        descColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getDescription() != null ? cellData.getValue().getDescription() : ""));
        descColumn.setPrefWidth(200);
        
        TableColumn<Task, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getStatus() != null ? cellData.getValue().getStatus() : "To Do"));
        statusColumn.setPrefWidth(100);
        
        TableColumn<Task, String> priorityColumn = new TableColumn<>("Priority");
        priorityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPriority()));
        priorityColumn.setPrefWidth(80);
        
        TableColumn<Task, String> dueDateColumn = new TableColumn<>("Due Date");
        dueDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getDueDate() != null ? cellData.getValue().getDueDate().toString() : "N/A"));
        dueDateColumn.setPrefWidth(100);
        
        TableColumn<Task, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setPrefWidth(150);
        actionsColumn.setCellFactory(param -> createActionCell());
        
        tasksTableView.getColumns().addAll(idColumn, titleColumn, descColumn, statusColumn, priorityColumn, dueDateColumn, actionsColumn);
        tasksTableView.setStyle("-fx-font-size: 11px;");
        
        
        tasksTableView.setRowFactory(tv -> {
            TableRow<Task> row = new TableRow<Task>() {
                @Override
                protected void updateItem(Task task, boolean empty) {
                    super.updateItem(task, empty);
                    setCursor(empty ? javafx.scene.Cursor.DEFAULT : javafx.scene.Cursor.HAND);
                }
            };
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    openTaskDetail(row.getItem());
                }
            });
            return row;
        });
    }
    
    private void updateTableView() {
        tasksTableView.setItems(filteredTasks);
    }
    
    private TableCell<Task, Void> createActionCell() {
        return new TableCell<Task, Void>() {
            private final Button deleteBtn = new Button("Delete");
            private final ComboBox<String> statusCombo = new ComboBox<>();
            private final HBox hbox = new HBox(5);
            
            {
                deleteBtn.setStyle("-fx-padding: 5 10; -fx-font-size: 10;");
                statusCombo.setPrefWidth(80);
                statusCombo.setStyle("-fx-font-size: 10;");
                statusCombo.getItems().addAll("To Do", "In Progress", "Done");
                
                deleteBtn.setOnAction(e -> {
                    Task task = getTableView().getItems().get(getIndex());
                    handleDeleteTask(task);
                });
                
                statusCombo.setOnAction(e -> {
                    Task task = getTableView().getItems().get(getIndex());
                    handleStatusChange(task, statusCombo.getValue());
                });
                
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.getChildren().addAll(deleteBtn, statusCombo);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        };
    }
    
    
    
    private void setupKanbanDropZones() {
        setupKanbanColumnDropZone(todoColumn, "To Do");
        setupKanbanColumnDropZone(inProgressColumn, "In Progress");
        setupKanbanColumnDropZone(doneColumn, "Done");
    }
    
    private void setupKanbanColumnDropZone(FlowPane column, String status) {
        column.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
                column.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-padding: 10;");
            }
            event.consume();
        });
        
        column.setOnDragExited(event -> {
            column.setStyle("");
            event.consume();
        });
        
        column.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            
            if (db.hasString()) {
                try {
                    long taskId = Long.parseLong(db.getString());
                    handleTaskDropped(taskId, status);
                    success = true;
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing task ID");
                }
            }
            
            event.setDropCompleted(success);
            event.consume();
        });
    }
    
    private void updateKanbanView() {
        todoColumn.getChildren().clear();
        inProgressColumn.getChildren().clear();
        doneColumn.getChildren().clear();
        
        for (Task task : filteredTasks) {
            VBox card = createKanbanTaskCard(task);
            
            if (task.getStatus() != null) {
                switch (task.getStatus().toLowerCase()) {
                    case "to do":
                        todoColumn.getChildren().add(card);
                        break;
                    case "in progress":
                        inProgressColumn.getChildren().add(card);
                        break;
                    case "done":
                        doneColumn.getChildren().add(card);
                        break;
                }
            }
        }
    }
    
    private VBox createKanbanTaskCard(Task task) {
        VBox card = new VBox(5);
        card.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10; " +
                     "-fx-background-color: #fff; -fx-border-radius: 3; -fx-background-radius: 3; -fx-cursor: hand;");
        card.setMaxWidth(180);
        card.setPrefWidth(180);
        card.setUserData(task.getId());
        
        Label nameLabel = new Label(task.getTitle());
        nameLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);
        
        Label priorityLabel = new Label("Priority: " + task.getPriority());
        priorityLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
        
        Label dueDateLabel = new Label("Due: " + (task.getDueDate() != null ? task.getDueDate() : "N/A"));
        dueDateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
        
        card.getChildren().addAll(nameLabel, priorityLabel, dueDateLabel);
        
        
        card.setOnDragDetected(event -> {
            Dragboard db = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(task.getId()));
            db.setContent(content);
            card.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-padding: 10; " +
                         "-fx-background-color: #ecf0f1; -fx-opacity: 0.7; -fx-cursor: hand;");
            event.consume();
        });
        
        card.setOnDragDone(event -> {
            card.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10; " +
                         "-fx-background-color: #fff; -fx-border-radius: 3; -fx-background-radius: 3; -fx-cursor: hand;");
            event.consume();
        });
        
        card.setOnMouseClicked(event -> openTaskDetail(task));
        
        return card;
    }
    
    private void handleTaskDropped(long taskId, String newStatus) {
        try {
            Task task = taskService.getTaskById(taskId);
            if (task != null) {
                task.setStatus(newStatus);
                taskService.updateTask(task);
                System.out.println("✅ Task moved to " + newStatus);
                applyFilters();
            }
        } catch (Exception e) {
            System.err.println("Error updating task: " + e.getMessage());
        }
    }
    
    
    
    private void updateListView() {
        taskListContainer.getChildren().clear();
        
        for (Task task : filteredTasks) {
            VBox taskItem = createListTaskItem(task);
            taskListContainer.getChildren().add(taskItem);
        }
    }
    
    private VBox createListTaskItem(Task task) {
        VBox item = new VBox(5);
        item.setStyle("-fx-border-color: #ecf0f1; -fx-border-width: 0 0 1 0; -fx-padding: 10; -fx-cursor: hand;");
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label(task.getTitle());
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        
        Label statusLabel = new Label(task.getStatus());
        statusLabel.setStyle("-fx-padding: 3 8; -fx-background-radius: 3; -fx-font-size: 10px; -fx-text-fill: white;");
        statusLabel.setStyle(statusLabel.getStyle() + getStatusColor(task.getStatus()));
        
        Label priorityLabel = new Label(task.getPriority());
        priorityLabel.setStyle("-fx-padding: 3 8; -fx-background-radius: 3; -fx-font-size: 10px; -fx-text-fill: white;" + getPriorityColor(task.getPriority()));
        
        Region spacer = new Region();
        spacer.setMinWidth(Region.USE_COMPUTED_SIZE);
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Label dueDateLabel = new Label("Due: " + (task.getDueDate() != null ? task.getDueDate() : "N/A"));
        dueDateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
        
        header.getChildren().addAll(titleLabel, statusLabel, priorityLabel, spacer, dueDateLabel);
        
        Label descLabel = new Label(task.getDescription() != null ? task.getDescription() : "No description");
        descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        descLabel.setWrapText(true);
        
        item.getChildren().addAll(header, descLabel);
        item.setOnMouseClicked(event -> openTaskDetail(task));
        
        return item;
    }
    
    
    
    @FXML
    private void handleAddTask() {
        if (selectedProject == null) {
            showAlert("Info", "Please select a project first");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/taskmanagement/fxml/dialog/CreateTaskView.fxml"));
            VBox root = loader.load();
            
            CreateTaskController controller = loader.getController();
            controller.setProject(selectedProject);
            controller.setDialogStage(null);
            
            Stage stage = new Stage();
            stage.setTitle("Create New Task");
            stage.setScene(new Scene(root, 600, 500));
            stage.initModality(Modality.APPLICATION_MODAL);
            
            controller.setDialogStage(stage);
            controller.setOnTaskCreated(() -> {
                loadTasks();
            });
            
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void openTaskDetail(Task task) {
        System.out.println("👁️ Opening task detail: " + task.getTitle());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/taskmanagement/fxml/main/TaskDetailView.fxml"));
            BorderPane root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Task Details - " + task.getTitle());
            stage.setScene(new Scene(root, 800, 600));
            stage.initModality(Modality.APPLICATION_MODAL);
            
            stage.showAndWait();
            loadTasks();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleDeleteTask(Task task) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Task");
        confirm.setHeaderText("Delete '" + task.getTitle() + "'?");
        
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    taskService.deleteTask(task.getId());
                    loadTasks();
                    showAlert("Success", "Task deleted");
                } catch (Exception e) {
                    showAlert("Error", "Error deleting task");
                }
            }
        });
    }
    
    private void handleStatusChange(Task task, String newStatus) {
        try {
            task.setStatus(newStatus);
            taskService.updateTask(task);
            loadTasks();
        } catch (Exception e) {
            showAlert("Error", "Error updating task status");
        }
    }
    
    
    
    private String getStatusColor(String status) {
        if (status == null) return "-fx-background-color: #95a5a6;";
        switch (status.toLowerCase()) {
            case "to do": return "-fx-background-color: #e74c3c;";
            case "in progress": return "-fx-background-color: #f39c12;";
            case "done": return "-fx-background-color: #27ae60;";
            default: return "-fx-background-color: #95a5a6;";
        }
    }
    
    private String getPriorityColor(String priority) {
        if (priority == null) return "-fx-background-color: #95a5a6;";
        switch (priority.toLowerCase()) {
            case "low": return "-fx-background-color: #27ae60;";
            case "medium": return "-fx-background-color: #f39c12;";
            case "high": return "-fx-background-color: #e74c3c;";
            default: return "-fx-background-color: #95a5a6;";
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @Override
    public void setMainLayoutController(MainLayoutController controller) {
        this.mainLayoutController = controller;
        
        
    }
}
