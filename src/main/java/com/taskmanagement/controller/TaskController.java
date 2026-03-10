package com.taskmanagement.controller;

import com.taskmanagement.App;
import com.taskmanagement.model.Task;
import com.taskmanagement.service.ActivityLogService;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.utils.UIUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class TaskController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private Label statusLabel;
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> titleColumn;
    @FXML private TableColumn<Task, String> statusColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, String> dueDateColumn;
    @FXML private TableColumn<Task, Void> actionColumn;

    private final TaskService taskService = new TaskService();
    private FilteredList<Task> filtered;
    private String currentFilter = null;

    @FXML
    public void initialize() {
        statusFilter.getItems().setAll("All", "To Do", "In Progress", "Done");
        statusFilter.setValue("All");

        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        priorityColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPriority()));
        dueDateColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getDueDate() == null ? "-" : data.getValue().getDueDate().toString()
        ));

        setupActionColumn();
        loadTasks();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        statusFilter.setOnAction(event -> applyFilters());
    }

    @FXML
    private void handleCreate() {
        openForm(null);
    }

    @FXML
    private void handleRefresh() {
        loadTasks();
    }

    public void applyFilter(String filter) {
        currentFilter = filter;
        loadTasks();
        
        // Apply filter to statusFilter combobox based on the passed filter type
        if ("completed".equalsIgnoreCase(filter)) {
            statusFilter.setValue("Done");
        } else if ("in-progress".equalsIgnoreCase(filter)) {
            statusFilter.setValue("In Progress");
        } else if ("pending".equalsIgnoreCase(filter)) {
            statusFilter.setValue("To Do");
        } else if ("all".equalsIgnoreCase(filter) || filter == null) {
            statusFilter.setValue("All");
            currentFilter = null;
        }
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("Details");
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox box = new HBox(8, viewBtn, editBtn, deleteBtn);

            {
                viewBtn.setOnAction(e -> {
                    Task task = getTableView().getItems().get(getIndex());
                    openTaskDetails(task);
                });
                editBtn.setOnAction(e -> {
                    Task task = getTableView().getItems().get(getIndex());
                    openForm(task);
                });
                deleteBtn.setOnAction(e -> {
                    Task task = getTableView().getItems().get(getIndex());
                    confirmDelete(task);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void openTaskDetails(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/taskmanagement/fxml/task/TaskDetailView.fxml"));
            VBox root = loader.load();
            TaskDetailController controller = loader.getController();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Task Details");
            dialog.setScene(new Scene(root, 680, 640));

            controller.setDialogStage(dialog);
            controller.setOnTaskChanged(this::loadTasks);
            controller.loadTaskDetails(task);

            dialog.showAndWait();
        } catch (Exception e) {
            UIUtils.showError("Error", "Failed to open task details: " + e.getMessage());
        }
    }

    private void openForm(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/taskmanagement/fxml/dialog/TaskForm.fxml"));
            VBox root = loader.load();
            TaskFormController controller = loader.getController();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(task == null ? "Create Task" : "Edit Task");
            dialog.setScene(new Scene(root, 560, 540));

            controller.setDialogStage(dialog);
            controller.setTask(task);
            controller.setOnSaved(this::loadTasks);

            dialog.showAndWait();
        } catch (Exception e) {
            UIUtils.showError("Error", "Failed to open task form: " + e.getMessage());
        }
    }

    public boolean confirmDelete(Task task) {
        if (task == null) {
            return false;
        }

        boolean confirmed = UIUtils.showCustomConfirmation(
                "Delete Task",
                "Are you sure you want to delete this task?",
                "[Delete] [Cancel]"
        );
        if (!confirmed) {
            return false;
        }

        try {
            taskService.deleteTask(task.getId());
            ActivityLogService.logTaskDeleted(task.getId(), task.getTitle());
            loadTasks();
            return true;
        } catch (Exception e) {
            UIUtils.showError("Error", e.getMessage());
            return false;
        }
    }

    private void loadTasks() {
        try {
            filtered = new FilteredList<>(FXCollections.observableArrayList(taskService.getAllTasks()), t -> true);
            taskTable.setItems(filtered);
            statusLabel.setText("Loaded " + filtered.size() + " tasks");
            applyFilters();

            taskTable.setRowFactory(tv -> {
                javafx.scene.control.TableRow<Task> row = new javafx.scene.control.TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && !row.isEmpty()) {
                        openTaskDetails(row.getItem());
                    }
                });
                return row;
            });
        } catch (Exception e) {
            taskTable.getItems().clear();
            statusLabel.setText("Failed to load tasks: " + e.getMessage());
        }
    }

    private void applyFilters() {
        if (filtered == null) {
            return;
        }

        String query = searchField.getText() == null ? "" : searchField.getText().toLowerCase().trim();
        String selectedStatus = statusFilter.getValue();
        String filter = currentFilter;

        filtered.setPredicate(task -> {
            boolean matchesQuery = query.isEmpty()
                || task.getTitle().toLowerCase().contains(query)
                || (task.getDescription() != null && task.getDescription().toLowerCase().contains(query));

            boolean matchesStatus = selectedStatus == null
                || "All".equals(selectedStatus)
                || selectedStatus.equalsIgnoreCase(task.getStatus());

            // Special handling for overdue filter
            if ("overdue".equalsIgnoreCase(filter)) {
                boolean isOverdue = task.getDueDate() != null 
                    && task.getDueDate().isBefore(LocalDate.now())
                    && !"Done".equalsIgnoreCase(task.getStatus());
                return matchesQuery && isOverdue;
            }

            return matchesQuery && matchesStatus;
        });

        statusLabel.setText("Showing " + filtered.size() + " task(s)");
    }
}
