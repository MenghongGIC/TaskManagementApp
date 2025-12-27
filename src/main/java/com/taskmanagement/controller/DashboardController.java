package com.taskmanagement.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.taskmanagement.App;
import com.taskmanagement.model.Task;
import com.taskmanagement.utils.CurrentUser;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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

    private Task draggedTask = null;
    private final List<Task> allTasks = new ArrayList<>();

    @FXML
    public void initialize() {
        // Initialize priority combo box
        priorityCombo.getItems().addAll("Low", "Medium", "High");
        priorityCombo.setValue("Medium");
        
        // Load tasks from database
        loadTasks();
        
        // Configure scroll pane behavior
        configureScrollPane(todoScrollPane);
        configureScrollPane(inProgressScrollPane);
        configureScrollPane(doneScrollPane);
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

            // Load sample tasks
            for (Task task : allTasks) {
                addTaskCard(task);
            }
        } catch (Exception e) {
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
            
            allTasks.add(newTask);
            addTaskCard(newTask);
            taskNameField.clear();
            showStatus("✓ Task created successfully", false);
        } catch (Exception e) {
            showStatus("Error creating task: " + e.getMessage(), true);
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
            showStatus("✓ Project created successfully", false);
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

        // Drag functionality - Works with both mouse and Mac touchpad
        card.setOnDragDetected(event -> {
            draggedTask = task;
            Dragboard dragboard = card.startDragAndDrop(TransferMode.MOVE);
            dragboard.setDragView(card.snapshot(null, null), 50, 50);
            event.consume();
        });

        // Also detect mouse drag to trigger drag detection on touchpad
        card.setOnMouseDragged(event -> {
            if (draggedTask == null) {
                draggedTask = task;
                Dragboard dragboard = card.startDragAndDrop(TransferMode.MOVE);
                dragboard.setDragView(card.snapshot(null, null), 50, 50);
                event.consume();
            }
        });

        card.setOnDragDone(event -> {
            draggedTask = null;
            event.consume();
        });

        return card;
    }

    @FXML
    private void handleDragOver(DragEvent event) {
        if (draggedTask != null) {
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        }
    }

    @FXML
    private void handleDragDropped(DragEvent event) {
        boolean success = false;

        if (draggedTask != null) {
            // Get the source node
            Object source = event.getSource();
            VBox targetPanel = null;

            // Find which panel the task was dropped on
            if (source == todoPanel) {
                targetPanel = todoPanel;
                draggedTask.setStatus("To Do");
            } else if (source == inProgressPanel) {
                targetPanel = inProgressPanel;
                draggedTask.setStatus("In Progress");
            } else if (source == donePanel) {
                targetPanel = donePanel;
                draggedTask.setStatus("Done");
            }

            // If dropped on a child, find parent panel
            if (targetPanel == null && source instanceof javafx.scene.Node) {
                javafx.scene.Node node = (javafx.scene.Node) source;
                javafx.scene.Parent parent = node.getParent();
                while (parent != null) {
                    if (parent == todoPanel) {
                        targetPanel = todoPanel;
                        draggedTask.setStatus("To Do");
                        break;
                    } else if (parent == inProgressPanel) {
                        targetPanel = inProgressPanel;
                        draggedTask.setStatus("In Progress");
                        break;
                    } else if (parent == donePanel) {
                        targetPanel = donePanel;
                        draggedTask.setStatus("Done");
                        break;
                    }
                    parent = parent.getParent();
                }
            }

            if (targetPanel != null) {
                try {
                    success = true;
                    loadTasks();
                    showStatus("✓ Task moved to " + draggedTask.getStatus(), false);
                } catch (Exception e) {
                    showStatus("Error updating task", true);
                    success = false;
                }
            } else {
                showStatus("Cannot drop outside columns", true);
            }
        }

        event.setDropCompleted(success);
        event.consume();
    }

    @FXML
    private void handleEditTask(Task task) {
        showStatus("Edit feature coming soon for: " + task.getTitle(), false);
        // Future: Show edit dialog
    }

    @FXML
    private void handleDeleteTask(Task task) {
        try {
            allTasks.remove(task);
            loadTasks();
            showStatus("✓ Task deleted", false);
        } catch (Exception e) {
            showStatus("Error deleting task", true);
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

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
    }
}

