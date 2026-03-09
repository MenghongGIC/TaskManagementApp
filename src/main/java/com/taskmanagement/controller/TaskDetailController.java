package com.taskmanagement.controller;

import com.taskmanagement.App;
import com.taskmanagement.model.ActivityLog;
import com.taskmanagement.model.Task;
import com.taskmanagement.service.ActivityLogService;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.utils.UIUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.stream.Collectors;

public class TaskDetailController {

    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label projectLabel;
    @FXML private Label statusLabel;
    @FXML private Label priorityLabel;
    @FXML private Label assignedMembersLabel;
    @FXML private Label labelsLabel;
    @FXML private Label dueDateLabel;
    @FXML private ListView<String> activityLogList;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private final TaskService taskService = new TaskService();
    private Task task;
    private Stage dialogStage;
    private Runnable onTaskChanged;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setOnTaskChanged(Runnable onTaskChanged) {
        this.onTaskChanged = onTaskChanged;
    }

    public void loadTaskDetails(Task task) {
        this.task = task;
        if (task == null) {
            return;
        }

        titleLabel.setText(nullSafe(task.getTitle()));
        descriptionLabel.setText(nullSafe(task.getDescription()));
        projectLabel.setText(task.getProject() != null ? nullSafe(task.getProject().getName()) : "-");
        statusLabel.setText(nullSafe(task.getStatus()));
        priorityLabel.setText(nullSafe(task.getPriority()));
        assignedMembersLabel.setText(task.getAssignee() != null ? task.getAssignee().getUsername() : "Unassigned");
        labelsLabel.setText(task.getLabels().isEmpty()
                ? "-"
                : task.getLabels().stream().map(label -> label.getName()).collect(Collectors.joining(", ")));
        dueDateLabel.setText(task.getDueDate() != null ? task.getDueDate().toString() : "-");

        activityLogList.getItems().setAll(
                ActivityLogService.getEntityHistory("TASK", task.getId()).stream()
                        .map(ActivityLog::toString)
                        .toList()
        );
    }

    @FXML
    private void handleEditTask() {
        if (task == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/taskmanagement/fxml/dialog/TaskForm.fxml"));
            VBox root = loader.load();
            TaskFormController controller = loader.getController();

            Stage editDialog = new Stage();
            editDialog.initModality(Modality.APPLICATION_MODAL);
            editDialog.setTitle("Edit Task");
            editDialog.setScene(new Scene(root, 560, 540));

            controller.setDialogStage(editDialog);
            controller.setTask(task);
            controller.setOnSaved(() -> {
                Task refreshed = taskService.getTaskById(task.getId());
                loadTaskDetails(refreshed != null ? refreshed : task);
                if (onTaskChanged != null) {
                    onTaskChanged.run();
                }
            });

            editDialog.showAndWait();
        } catch (Exception e) {
            UIUtils.showError("Error", "Failed to open edit form: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteTask() {
        if (task == null) {
            return;
        }

        if (!confirmDelete(task)) {
            return;
        }

        try {
            taskService.deleteTask(task.getId());
            ActivityLogService.logTaskDeleted(task.getId(), task.getTitle());
            if (onTaskChanged != null) {
                onTaskChanged.run();
            }
            closeDialog();
        } catch (Exception e) {
            UIUtils.showError("Delete failed", e.getMessage());
        }
    }

    public boolean confirmDelete(Task task) {
        return UIUtils.showCustomConfirmation(
                "Delete Task",
                "Are you sure you want to delete this task?",
                task.getTitle()
        );
    }

    @FXML
    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private String nullSafe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
