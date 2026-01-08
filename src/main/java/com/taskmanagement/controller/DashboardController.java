package com.taskmanagement.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.taskmanagement.App;
import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.utils.CurrentUser;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;

public class DashboardController {

    @FXML private TextField taskNameField;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private Label statusLabel;
    
    @FXML private VBox todoPanel;
    @FXML private VBox inProgressPanel;
    @FXML private VBox donePanel;
    
    @FXML private ScrollPane todoScrollPane;
    @FXML private ScrollPane inProgressScrollPane;
    @FXML private ScrollPane doneScrollPane;
    
    @FXML private ImageView profileImageView;
    @FXML private Label usernameLabel;
    @FXML private Button profileButton;
    @FXML private Button settingsButton;
    @FXML private Button logoutButton;
    
    @FXML private StackPane viewStack;
    @FXML private VBox kanbanView;
    @FXML private VBox tableView;
    @FXML private TableView<Task> tasksTableView;

    private Task draggedTask = null;
    private final List<Task> allTasks = new ArrayList<>();
    private boolean isTableViewActive = false;

    @FXML
    public void initialize() {
        // Initialize priority combo box
        priorityCombo.getItems().addAll("Low", "Medium", "High");
        priorityCombo.setValue("Medium");
        
        // Setup profile sidebar-qe==z
        setupProfileSidebar();
        
        // Load tasks from database
        loadTasks();
        
        // Configure scroll pane behavior
        configureScrollPane(todoScrollPane);
        configureScrollPane(inProgressScrollPane);
        configureScrollPane(doneScrollPane);
        
        // Setup drag handlers for panels
        setupDragHandlers(todoPanel, todoScrollPane);
        setupDragHandlers(inProgressPanel, inProgressScrollPane);
        setupDragHandlers(donePanel, doneScrollPane);
        
        // Setup table view with kanban as default
        setupTableColumns();
    }
    
    private void setupTableColumns() {
        int columnIndex = 0;
        for (TableColumn<Task, ?> column : tasksTableView.getColumns()) {
            if (columnIndex == 0) {
                column.setCellValueFactory(new PropertyValueFactory<>("id"));
            } else if (columnIndex == 1) {
                column.setCellValueFactory(new PropertyValueFactory<>("title"));
            } else if (columnIndex == 2) {
                column.setCellValueFactory(new PropertyValueFactory<>("status"));
            } else if (columnIndex == 3) {
                column.setCellValueFactory(new PropertyValueFactory<>("priority"));
            } else if (columnIndex == 4) {
                @SuppressWarnings("unchecked")
                TableColumn<Task, String> assigneeColumn = (TableColumn<Task, String>) column;
                assigneeColumn.setCellValueFactory(cellData -> 
                    new SimpleStringProperty(
                        cellData.getValue().getAssignee() != null ? 
                        cellData.getValue().getAssignee().getUsername() : "-"
                    )
                );
            } else if (columnIndex == 5) {
                column.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
            }
            columnIndex++;
        }
    }
    
    private void setupProfileSidebar() {
        // Set username label from current user
        String username = CurrentUser.getUsername();
        if (username != null && !username.isEmpty()) {
            usernameLabel.setText(username);
        }
        try {
            String imagePath = "/com/taskmanagement/img/profile-default.png";
            try {
                Image image = new Image(getClass().getResourceAsStream(imagePath));
                profileImageView.setImage(image);
            } catch (NullPointerException e) {
                System.out.println("Profile image resource not found at: " + imagePath);
                System.out.println("You can add images to: src/main/resources/com/taskmanagement/img/");
            }
        } catch (Exception e) {
            System.err.println("Error setting up profile image: " + e.getMessage());
        }
    }
    public void setProfileImage(String imagePath) {
        try {
            Image image = new Image("file:" + imagePath);
            profileImageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }
    }
    public void setProfileImageFromResource(String resourcePath) {
        try {
            Image image = new Image(getClass().getResourceAsStream(resourcePath));
            profileImageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Error loading image from resource: " + e.getMessage());
        }
    }
    
    private void setupDragHandlers(VBox panel, ScrollPane scrollPane) {
        panel.setOnDragOver(event -> {
            if (draggedTask != null && event.getGestureSource() != panel) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        
        panel.setOnDragEntered(event -> {
            if (draggedTask != null && event.getGestureSource() != panel) {
                panel.setStyle("-fx-background-color: #d6eaf8; -fx-padding: 10; -fx-border-color: #3498db; -fx-border-width: 2;");
            }
        });
        
        panel.setOnDragExited(event -> {
            panel.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        });
        
        panel.setOnDragDropped(event -> {
            handleDropOnPanel(event, panel);
        });
        
        // Setup handlers on the scroll pane to catch events from contained cards
        scrollPane.setOnDragOver(event -> {
            if (draggedTask != null && event.getGestureSource() != panel) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        
        scrollPane.setOnDragEntered(event -> {
            if (draggedTask != null && event.getGestureSource() != panel) {
                panel.setStyle("-fx-background-color: #d6eaf8; -fx-padding: 10; -fx-border-color: #3498db; -fx-border-width: 2;");
            }
        });
        
        scrollPane.setOnDragExited(event -> {
            panel.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        });
        
        scrollPane.setOnDragDropped(event -> {
            handleDropOnPanel(event, panel);
        });
    }
    
    private void handleDropOnPanel(DragEvent event, VBox targetPanel) {
        boolean success = false;
        
        if (draggedTask != null && draggedTask.getId() != null) {
            VBox panel = targetPanel;
            
            // Determine which panel was dropped on
            if (panel == todoPanel) {
                draggedTask.setStatus("To Do");
            } else if (panel == inProgressPanel) {
                draggedTask.setStatus("In Progress");
            } else if (panel == donePanel) {
                draggedTask.setStatus("Done");
            }
            
            try {
                // Update task in database
                TaskRepository taskRepository = new TaskRepository();
                taskRepository.update(draggedTask);
                
                // Remove task card from all panels
                removeTaskCardFromAllPanels();
                
                // Create new card and add to target panel
                HBox newCard = createTaskCard(draggedTask);
                panel.getChildren().add(newCard);
                
                // Update table view if active
                if (isTableViewActive) {
                    updateTableView();
                }
                
                success = true;
                showStatus("✓ Task moved to " + draggedTask.getStatus(), false);
            } catch (Exception e) {
                showStatus("Error updating task: " + e.getMessage(), true);
                e.printStackTrace();
                success = false;
            }
        } else if (draggedTask != null && draggedTask.getId() == null) {
            showStatus("Error: Cannot move task - task has no ID. Please refresh and try again.", true);
        }
        
        // Reset styling
        todoPanel.setStyle("-fx-background-color: transparent;");
        inProgressPanel.setStyle("-fx-background-color: transparent;");
        donePanel.setStyle("-fx-background-color: transparent;");
        
        event.setDropCompleted(success);
        event.consume();
    }
    
    private void removeTaskCardFromAllPanels() {
        todoPanel.getChildren().removeIf(node -> {
            if (node instanceof HBox) {
                return ((HBox) node).getUserData() == draggedTask;
            }
            return false;
        });
        inProgressPanel.getChildren().removeIf(node -> {
            if (node instanceof HBox) {
                return ((HBox) node).getUserData() == draggedTask;
            }
            return false;
        });
        donePanel.getChildren().removeIf(node -> {
            if (node instanceof HBox) {
                return ((HBox) node).getUserData() == draggedTask;
            }
            return false;
        });
    }

    private void configureScrollPane(ScrollPane scrollPane) {
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
    }

    private void loadTasks() {
        try {
            // Clear all panels
            todoPanel.getChildren().clear();
            inProgressPanel.getChildren().clear();
            donePanel.getChildren().clear();
            allTasks.clear();

            // Load tasks from database
            TaskRepository taskRepository = new TaskRepository();
            List<Task> dbTasks = taskRepository.findAll();
            
            if (dbTasks != null && !dbTasks.isEmpty()) {
                allTasks.addAll(dbTasks);
                System.out.println("✓ Loaded " + dbTasks.size() + " tasks from database");
                
                // Add each task to the appropriate panel
                for (Task task : dbTasks) {
                    addTaskCard(task);
                }
            } else {
                System.out.println("ℹ No tasks found in database");
            }
        } catch (Exception e) {
            System.err.println("Error loading tasks: " + e.getMessage());
            e.printStackTrace();
            showStatus("Error loading tasks: " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleCreateTask() {
        String taskName = taskNameField.getText().trim();
        String priority = priorityCombo.getValue();

        if (taskName.isEmpty()) {
            showStatus("Please enter a task name", true);
            return;
        }

        try {
            Task newTask = new Task();
            newTask.setTitle(taskName);
            newTask.setDescription("Priority: " + priority);
            newTask.setStatus("To Do");
            
            // Set current user as creator
            User currentUser = new User();
            currentUser.setId(CurrentUser.getId());
            newTask.setCreatedBy(currentUser);
            
            // Save to database
            TaskRepository taskRepository = new TaskRepository();
            Task savedTask = taskRepository.save(newTask);
            
            // Verify task was saved with ID
            if (savedTask == null || savedTask.getId() == null) {
                showStatus("Error: Task was not saved properly (missing ID)", true);
                return;
            }
            
            allTasks.add(savedTask);
            addTaskCard(savedTask);
            taskNameField.clear();
            showStatus("Task created successfully", false);
        } catch (Exception e) {
            showStatus("Error creating task: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreateProject() {
        String projectName = taskNameField.getText().trim();

        if (projectName.isEmpty()) {
            showStatus("Please enter a project name", true);
            return;
        }

        try {
            // Create project logic
            Task projectTask = new Task();
            projectTask.setTitle("📁 " + projectName);
            projectTask.setDescription("Project");
            projectTask.setStatus("To Do");

            allTasks.add(projectTask);
            addTaskCard(projectTask);
            taskNameField.clear();
            showStatus("Project created successfully", false);
        } catch (Exception e) {
            showStatus("Error creating project: " + e.getMessage(), true);
        }
    }

    private void addTaskCard(Task task) {
        HBox taskCard = createTaskCard(task);
        
        String status = task.getStatus() != null ? task.getStatus() : "To Do";
        if ("In Progress".equalsIgnoreCase(status)) {
            inProgressPanel.getChildren().add(taskCard);
        } else if ("Done".equalsIgnoreCase(status)) {
            donePanel.getChildren().add(taskCard);
        } else {
            todoPanel.getChildren().add(taskCard);
        }
    }

    private HBox createTaskCard(Task task) {
        HBox card = new HBox(10);
        card.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-padding: 12; " +
                     "-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-cursor: hand;");
        card.setPrefHeight(100);
        card.setMinHeight(100);
        card.setUserData(task);  // Store task reference for drag and drop identification
        card.setStyle(card.getStyle() + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        // Card content
        VBox content = new VBox(5);
        Label titleLabel = new Label(task.getTitle());
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label descLabel = new Label(task.getDescription() != null ? task.getDescription() : "");
        descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        descLabel.setWrapText(true);
        
        content.getChildren().addAll(titleLabel, descLabel);
        
        // Action buttons
        VBox actions = new VBox(5);
        Button editBtn = new Button("✏️");
        editBtn.setStyle("-fx-padding: 5; -fx-font-size: 10;");
        editBtn.setOnAction(e -> handleEditTask(task));
        
        Button deleteBtn = new Button("🗑️");
        deleteBtn.setStyle("-fx-padding: 5; -fx-font-size: 10;");
        deleteBtn.setOnAction(e -> handleDeleteTask(task));
        
        actions.getChildren().addAll(editBtn, deleteBtn);

        card.getChildren().addAll(content, actions);
        HBox.setHgrow(content, javafx.scene.layout.Priority.ALWAYS);

        // Drag detection - initiate drag and drop
        card.setOnDragDetected(event -> {
            draggedTask = task;  // Set the dragged task reference
            
            Dragboard dragboard = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content2 = new ClipboardContent();
            content2.putString(String.valueOf(task.getId() != null ? task.getId() : task.getTitle()));
            dragboard.setContent(content2);
            
            try {
                dragboard.setDragView(card.snapshot(null, null), 50, 50);
            } catch (Exception e) {
                System.err.println("Error creating drag view: " + e.getMessage());
            }
            
            // Visual feedback
            card.setStyle("-fx-background-color: #e8f4f8; -fx-border-radius: 5; -fx-padding: 12; " +
                         "-fx-border-color: #3498db; -fx-border-width: 2; -fx-cursor: move; -fx-opacity: 0.8;");
            event.consume();
        });

        // Drag done - reset styling
        card.setOnDragDone(event -> {
            card.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-padding: 12; " +
                         "-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-cursor: hand; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            draggedTask = null;
            event.consume();
        });

        return card;
    }

    @FXML
    private void handleEditTask(Task task) {
        showStatus("Edit feature coming soon for: " + task.getTitle(), false);
    }

    @FXML
    private void handleDeleteTask(Task task) {
        try {
            TaskRepository taskRepository = new TaskRepository();
            taskRepository.delete(task.getId());
            
            allTasks.remove(task);
            loadTasks();
            showStatus("✓ Task deleted", false);
        } catch (Exception e) {
            showStatus("Error deleting task: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewProfile() throws IOException {
        App.setRoot("main/Profile");
    }

    @FXML
    private void handleSettings() {
        showStatus("Settings panel coming soon", false);
    }

    @FXML
    private void handleLogout() throws IOException {
        CurrentUser.clear();
        App.setRoot("auth/LoginView");
    }

    @FXML
    private void toggleTableView() {
        isTableViewActive = !isTableViewActive;
        if (isTableViewActive) {
            viewStack.getChildren().clear();
            viewStack.getChildren().add(tableView);
            updateTableView();
        } else {
            viewStack.getChildren().clear();
            viewStack.getChildren().add(kanbanView);
        }
    }

    private void updateTableView() {
        tasksTableView.getItems().clear();
        tasksTableView.getItems().addAll(allTasks);
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
    }
}

