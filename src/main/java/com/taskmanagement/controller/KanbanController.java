package com.taskmanagement.controller;

import com.taskmanagement.model.Task;
import com.taskmanagement.service.TaskService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;

import java.util.List;

public class KanbanController {

    @FXML private VBox todoColumn;
    @FXML private VBox inProgressColumn;
    @FXML private VBox doneColumn;
    @FXML private Label todoCountLabel;
    @FXML private Label inProgressCountLabel;
    @FXML private Label doneCountLabel;

    private final TaskService taskService = new TaskService();

    @FXML
    public void initialize() {
        if (todoColumn == null || inProgressColumn == null || doneColumn == null) {
            return;
        }

        setupDropTarget(todoColumn, "To Do");
        setupDropTarget(inProgressColumn, "In Progress");
        setupDropTarget(doneColumn, "Done");
        loadBoard();
    }

    @FXML
    private void refreshBoard() {
        loadBoard();
    }

    private void loadBoard() {
        try {
            if (todoColumn == null || inProgressColumn == null || doneColumn == null) {
                throw new IllegalStateException("Kanban columns are not initialized");
            }

            todoColumn.getChildren().clear();
            inProgressColumn.getChildren().clear();
            doneColumn.getChildren().clear();

            int todoCount = 0;
            int inProgressCount = 0;
            int doneCount = 0;

            List<Task> tasks = taskService.getAllTasks();
            for (Task task : tasks) {
                VBox card = createCard(task);
                String status = task.getStatus() == null ? "To Do" : task.getStatus();
                switch (status) {
                    case "In Progress" -> {
                        inProgressColumn.getChildren().add(card);
                        inProgressCount++;
                    }
                    case "Done" -> {
                        doneColumn.getChildren().add(card);
                        doneCount++;
                    }
                    default -> {
                        todoColumn.getChildren().add(card);
                        todoCount++;
                    }
                }
            }

            // Update count labels
            if (todoCountLabel != null) todoCountLabel.setText(String.valueOf(todoCount));
            if (inProgressCountLabel != null) inProgressCountLabel.setText(String.valueOf(inProgressCount));
            if (doneCountLabel != null) doneCountLabel.setText(String.valueOf(doneCount));

        } catch (Exception e) {
            renderInlineError("Failed to load board: " + safeMessage(e));
        }
    }

    private VBox createCard(Task task) {
        // Main card container with CSS class for styling
        VBox card = new VBox(8);
        card.getStyleClass().add("task-card");
        card.setPadding(new Insets(10));
        
        // Apply priority-based left border
        applyPriorityBorder(card, task.getPriority());
        
        // Title
        Label titleLabel = new Label(task.getTitle() != null ? task.getTitle() : "Untitled");
        titleLabel.setWrapText(true);
        titleLabel.getStyleClass().add("task-card-title");
        
        // Assignee row
        HBox assigneeRow = new HBox(4);
        Label assigneeLabel = new Label(
            "👤 " + (task.getAssignee() != null ? task.getAssignee().getUsername() : "Unassigned")
        );
        assigneeLabel.getStyleClass().add("task-card-assignee");
        assigneeRow.getChildren().add(assigneeLabel);
        
        // Priority badge
        Label priorityBadge = createPriorityBadge(task.getPriority());
        
        // Priority row
        HBox priorityRow = new HBox(4);
        priorityRow.getChildren().add(priorityBadge);
        priorityRow.setHgrow(priorityBadge, Priority.ALWAYS);
        
        // Due date
        Label dueDateLabel = new Label(
            "📅 " + (task.getDueDate() != null ? task.getDueDate().toString() : "No date")
        );
        dueDateLabel.getStyleClass().add("task-card-due-date");
        
        card.getChildren().addAll(titleLabel, assigneeRow, priorityRow, dueDateLabel);
        
        // Drag detection
        card.setOnDragDetected(event -> {
            Dragboard db = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(task.getId()));
            db.setContent(content);
            event.consume();
        });
        
        // Hover effect (shadow through CSS)
        card.setOnMouseEntered(event -> {
            card.getStyleClass().add("task-card-hover");
        });
        
        card.setOnMouseExited(event -> {
            card.getStyleClass().remove("task-card-hover");
        });
        
        return card;
    }
    
    private void applyPriorityBorder(VBox card, String priority) {
        Color priorityColor = getPriorityBorderColor(priority);
        Color grayBorder = Color.web("#dcdcdc");
        
        // Create a border with priority color on left (4px) and gray on other sides (1px)
        BorderStroke priorityStroke = new BorderStroke(
            grayBorder,                          // top paint
            grayBorder,                          // right paint
            grayBorder,                          // bottom paint
            priorityColor,                       // left paint
            BorderStrokeStyle.SOLID,             // top style
            BorderStrokeStyle.SOLID,             // right style
            BorderStrokeStyle.SOLID,             // bottom style
            BorderStrokeStyle.SOLID,             // left style
            new CornerRadii(8),
            new BorderWidths(1, 1, 1, 4),
            new Insets(0)
        );
        
        card.setBorder(new Border(priorityStroke));
    }
    
    private Color getPriorityBorderColor(String priority) {
        if (priority == null) priority = "MEDIUM";
        
        return switch (priority.toLowerCase()) {
            case "low" -> Color.web("#28a745");        // green
            case "medium" -> Color.web("#007bff");     // blue
            case "high" -> Color.web("#dc3545");       // red
            case "urgent" -> Color.web("#fd7e14");     // orange
            case "critical" -> Color.web("#8b0000");   // dark red
            default -> Color.web("#007bff");           // blue (default)
        };
    }
    
    private Label createPriorityBadge(String priority) {
        Label badge = new Label(priority != null ? priority.toUpperCase() : "MEDIUM");
        badge.setPadding(new Insets(4, 8, 4, 8));
        badge.setStyle(getPriorityBadgeStyle(priority));
        return badge;
    }
    
    private String getPriorityBadgeStyle(String priority) {
        if (priority == null) priority = "Medium";
        
        return switch (priority.toLowerCase()) {
            case "low" -> "-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 4 8; " +
                        "-fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;";
            case "medium" -> "-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 4 8; " +
                           "-fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;";
            case "high" -> "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 4 8; " +
                         "-fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;";
            case "urgent" -> "-fx-background-color: #fd7e14; -fx-text-fill: white; -fx-padding: 4 8; " +
                           "-fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;";
            case "critical" -> "-fx-background-color: #8b0000; -fx-text-fill: white; -fx-padding: 4 8; " +
                             "-fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;";
            default -> "-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 4 8; " +
                      "-fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;";
        };
    }

    private void setupDropTarget(VBox column, String status) {
        column.setOnDragOver(event -> {
            if (event.getGestureSource() != column && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        column.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                try {
                    long taskId = Long.parseLong(db.getString());
                    taskService.changeTaskStatus(taskId, status);
                    success = true;
                    loadBoard();
                } catch (Exception e) {
                    renderInlineError("Failed to move task: " + safeMessage(e));
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void renderInlineError(String message) {
        if (todoColumn == null || inProgressColumn == null || doneColumn == null) {
            return;
        }
        todoColumn.getChildren().clear();
        inProgressColumn.getChildren().clear();
        doneColumn.getChildren().clear();

        Label error = new Label(message);
        error.setWrapText(true);
        error.setStyle("-fx-text-fill: #b91c1c; -fx-background-color: #fee2e2; -fx-padding: 8; -fx-background-radius: 8;");
        todoColumn.getChildren().add(error);
    }

    private String safeMessage(Throwable throwable) {
        if (throwable == null) {
            return "Unknown error";
        }
        String message = throwable.getMessage();
        if (message == null || message.isBlank()) {
            return throwable.getClass().getSimpleName();
        }
        return message;
    }
}
