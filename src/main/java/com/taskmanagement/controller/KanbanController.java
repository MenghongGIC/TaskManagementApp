package com.taskmanagement.controller;

import com.taskmanagement.model.Task;
import com.taskmanagement.service.TaskService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

import java.util.List;

public class KanbanController {

    @FXML private VBox todoColumn;
    @FXML private VBox inProgressColumn;
    @FXML private VBox doneColumn;

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

            List<Task> tasks = taskService.getAllTasks();
            for (Task task : tasks) {
                Label card = createCard(task);
                String status = task.getStatus() == null ? "To Do" : task.getStatus();
                switch (status) {
                    case "In Progress" -> inProgressColumn.getChildren().add(card);
                    case "Done" -> doneColumn.getChildren().add(card);
                    default -> todoColumn.getChildren().add(card);
                }
            }
        } catch (Exception e) {
            renderInlineError("Failed to load board: " + safeMessage(e));
        }
    }

    private Label createCard(Task task) {
        Label card = new Label(task.getTitle() + "\n" + (task.getPriority() == null ? "" : task.getPriority()));
        card.setWrapText(true);
        card.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8;");

        card.setOnDragDetected(event -> {
            Dragboard db = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(task.getId()));
            db.setContent(content);
            event.consume();
        });

        return card;
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
