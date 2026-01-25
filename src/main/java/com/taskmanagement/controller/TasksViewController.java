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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.taskmanagement.model.Project;
import com.taskmanagement.model.Task;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.utils.UIUtils;

import java.io.IOException;
public class TasksViewController implements TaskAwareController {
    
    // UI Labels
    private static final String LABEL_SELECT_PROJECT = "Please select a project first";
    private static final String LABEL_SELECT_PROJECT_CREATE = "Please select a project first";
    private static final String LABEL_NO_DESCRIPTION = "No description";
    private static final String LABEL_PRIORITY = "Priority: ";
    private static final String LABEL_DUE = "Due: ";
    private static final String LABEL_DUE_NA = "N/A";
    private static final String LABEL_TASKS_LOADED = "✓ Tasks loaded";
    private static final String LABEL_ERROR_LOAD = "Error loading tasks";
    private static final String LABEL_LOADING = "Loading tasks for project: ";
    
    // Filter Labels
    private static final String LABEL_FILTER_PREFIX = "Filters: ";
    private static final String LABEL_SEARCH = "Search: '";
    private static final String LABEL_SEARCH_SUFFIX = "' ";
    private static final String LABEL_STATUS = "Status: ";
    private static final String LABEL_PRIORITY_FILTER = "Priority: ";
    
    // Status Filter Options
    private static final String FILTER_ALL = "All";
    private static final String STATUS_TODO = "To Do";
    private static final String STATUS_IN_PROGRESS = "In Progress";
    private static final String STATUS_DONE = "Done";
    
    // Priority Filter Options
    private static final String PRIORITY_LOW = "Low";
    private static final String PRIORITY_MEDIUM = "Medium";
    private static final String PRIORITY_HIGH = "High";
    
    // Dialog Titles
    private static final String TITLE_CREATE_TASK = "Create New Task";
    private static final String TITLE_TASK_DETAILS = "Task Details - ";
    private static final String TITLE_DELETE = "Delete Task";
    private static final String TITLE_ERROR = "Error";
    private static final String TITLE_INFO = "Info";
    
    // Messages
    private static final String MSG_OPENING_TASK = "Opening task detail: ";
    private static final String MSG_ERROR_PARSING = "Error parsing task ID";
    private static final String MSG_ERROR_UPDATE = "Error updating task: ";
    private static final String MSG_TASK_MOVED = "Task moved to ";
    private static final String MSG_DELETE_CONFIRM = "Delete '%s'?";
    private static final String MSG_DELETE_SUCCESS = "Task deleted";
    private static final String MSG_ERROR_DELETE = "Error deleting task";
    private static final String MSG_ERROR_UPDATE_STATUS = "Error updating task status";
    
    // Style Constants
    private static final String STYLE_ITEM = "-fx-border-color: #ecf0f1; -fx-border-width: 0 0 1 0; -fx-padding: 10; -fx-cursor: hand;";
    private static final String STYLE_TITLE = "-fx-font-size: 12px; -fx-font-weight: bold;";
    private static final String STYLE_BADGE = "-fx-padding: 3 8; -fx-background-radius: 3; -fx-font-size: 10px; -fx-text-fill: white;";
    private static final String STYLE_DESC = "-fx-font-size: 11px; -fx-text-fill: #7f8c8d;";
    private static final String STYLE_META = "-fx-font-size: 10px; -fx-text-fill: #7f8c8d;";
    private static final String STYLE_TABLE = "-fx-font-size: 11px;";
    private static final String STYLE_DELETE_BTN = "-fx-padding: 5 10; -fx-font-size: 10;";
    private static final String STYLE_STATUS_COMBO = "-fx-font-size: 10;";
    private static final String STYLE_KANBAN_CARD = "-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #fff; -fx-border-radius: 3; -fx-background-radius: 3; -fx-cursor: hand;";
    private static final String STYLE_KANBAN_CARD_DRAGGING = "-fx-border-color: #3498db; -fx-border-width: 2; -fx-padding: 10; -fx-background-color: #ecf0f1; -fx-opacity: 0.7; -fx-cursor: hand;";
    private static final String STYLE_KANBAN_CARD_LABEL = "-fx-font-size: 11px; -fx-font-weight: bold;";
    private static final String STYLE_KANBAN_CARD_META = "-fx-font-size: 10px; -fx-text-fill: #7f8c8d;";
    private static final String STYLE_KANBAN_DROP_HIGHLIGHT = "-fx-border-color: #3498db; -fx-border-width: 2; -fx-padding: 10;";
    
    // Colors
    private static final String COLOR_DEFAULT = "-fx-background-color: #95a5a6;";
    
    // Status Colors Map
    private static final java.util.Map<String, String> STATUS_COLORS = java.util.Map.ofEntries(
        java.util.Map.entry("to do", "-fx-background-color: #e74c3c;"),
        java.util.Map.entry("in progress", "-fx-background-color: #f39c12;"),
        java.util.Map.entry("done", "-fx-background-color: #27ae60;")
    );
    
    // Priority Colors Map
    private static final java.util.Map<String, String> PRIORITY_COLORS = java.util.Map.ofEntries(
        java.util.Map.entry("low", "-fx-background-color: #27ae60;"),
        java.util.Map.entry("medium", "-fx-background-color: #f39c12;"),
        java.util.Map.entry("high", "-fx-background-color: #e74c3c;")
    );
    
    // Dialog Dimensions
    private static final int DIALOG_CREATE_WIDTH = 600;
    private static final int DIALOG_CREATE_HEIGHT = 500;
    private static final int DIALOG_DETAIL_WIDTH = 800;
    private static final int DIALOG_DETAIL_HEIGHT = 600;
    
    // Table Columns
    private static final String COL_ID = "ID";
    private static final String COL_TASK = "Task";
    private static final String COL_DESC = "Description";
    private static final String COL_STATUS = "Status";
    private static final String COL_PRIORITY = "Priority";
    private static final String COL_DUE = "Due Date";
    private static final String COL_ACTIONS = "Actions";
    
    // Table Column Widths
    private static final int WIDTH_ID = 50;
    private static final int WIDTH_TASK = 200;
    private static final int WIDTH_DESC = 200;
    private static final int WIDTH_STATUS = 100;
    private static final int WIDTH_PRIORITY = 80;
    private static final int WIDTH_DUE = 100;
    private static final int WIDTH_ACTIONS = 150;
    
    // Layout Constants
    private static final int KANBAN_CARD_WIDTH = 180;
    private static final int CARD_SPACING = 5;
    private static final int ACTION_SPACING = 5;
    private static final int STATUS_COMBO_WIDTH = 80;
    
    @FXML private StackPane viewStack;
    @FXML private Button tableViewBtn, kanbanViewBtn, listViewBtn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusComboBox, priorityComboBox;
    @FXML private Label statusLabel, taskCountLabel, filterStatusLabel, bottomStatusLabel;
    @FXML private TableView<Task> tasksTableView;
    @FXML private FlowPane todoColumn, inProgressColumn, doneColumn;
    @FXML private VBox taskListContainer;

    private TaskService taskService;
    private ObservableList<Task> allTasks;
    private ObservableList<Task> filteredTasks;
    private Project selectedProject;
    private int currentViewIndex = 0; 
    
    @FXML
    public void initialize() {
        System.out.println("Initializing TasksViewController");

        taskService = new TaskService();
        allTasks = FXCollections.observableArrayList();
        filteredTasks = FXCollections.observableArrayList();
        statusComboBox.setItems(FXCollections.observableArrayList(FILTER_ALL, STATUS_TODO, STATUS_IN_PROGRESS, STATUS_DONE));
        statusComboBox.setValue(FILTER_ALL);
        priorityComboBox.setItems(FXCollections.observableArrayList(FILTER_ALL, PRIORITY_LOW, PRIORITY_MEDIUM, PRIORITY_HIGH));
        priorityComboBox.setValue(FILTER_ALL);

        setupSearchAndFilter();
        initializeTableView();
        setupKanbanDropZones();

        System.out.println("TasksViewController initialized");
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
            boolean matchesSearch = isMatchingSearch(task, searchText);
            boolean matchesStatus = isMatchingStatus(task, statusFilter);
            boolean matchesPriority = isMatchingPriority(task, priorityFilter);
            
            if (matchesSearch && matchesStatus && matchesPriority) {
                filteredTasks.add(task);
            }
        }
        updateTableView();
        updateKanbanView();
        updateListView();
        updateStatusLabels();
    }
    
    private boolean isMatchingSearch(Task task, String searchText) {
        return searchText.isEmpty() || 
            task.getTitle().toLowerCase().contains(searchText) ||
            (task.getDescription() != null && task.getDescription().toLowerCase().contains(searchText));
    }
    
    private boolean isMatchingStatus(Task task, String statusFilter) {
        return statusFilter.equals(FILTER_ALL) || 
            (task.getStatus() != null && task.getStatus().equals(statusFilter));
    }
    
    private boolean isMatchingPriority(Task task, String priorityFilter) {
        return priorityFilter.equals(FILTER_ALL) || 
            (task.getPriority() != null && task.getPriority().equals(priorityFilter));
    }
    private void updateStatusLabels() {
        taskCountLabel.setText(filteredTasks.size() + " of " + allTasks.size() + " tasks");
        
        StringBuilder filterInfo = new StringBuilder();
        if (!searchField.getText().isEmpty()) {
            filterInfo.append(LABEL_SEARCH).append(searchField.getText()).append(LABEL_SEARCH_SUFFIX);
        }
        if (!statusComboBox.getValue().equals(FILTER_ALL)) {
            filterInfo.append(LABEL_STATUS).append(statusComboBox.getValue()).append(" ");
        }
        if (!priorityComboBox.getValue().equals(FILTER_ALL)) {
            filterInfo.append(LABEL_PRIORITY_FILTER).append(priorityComboBox.getValue());
        }
        
        filterStatusLabel.setText(filterInfo.isEmpty() ? "" : LABEL_FILTER_PREFIX + filterInfo.toString());
    }
    @FXML
    private void clearFilters() {
        searchField.clear();
        statusComboBox.setValue(FILTER_ALL);
        priorityComboBox.setValue(FILTER_ALL);
    }
    @FXML
    private void showTableView() {
        switchView(0);
    }
    
    @FXML
    private void showKanbanView() {
        switchView(1);
    }
    
    @FXML
    private void showListView() {
        switchView(2);
    }
    
    private void switchView(int viewIndex) {
        currentViewIndex = viewIndex;
        for (int i = 0; i < viewStack.getChildren().size(); i++) {
            viewStack.getChildren().get(i).setVisible(i == viewIndex);
        }
        updateButtonStyles();
    }

    private void updateButtonStyles() {
        String baseStyle = "-fx-padding: 8 15; -fx-font-size: 11px; -fx-text-fill: white; -fx-background-radius: 3;";
        String inactiveStyle = baseStyle + " -fx-background-color: #95a5a6;";
        String activeStyle = baseStyle + " -fx-background-color: #3498db;";
        
        tableViewBtn.setStyle(inactiveStyle);
        kanbanViewBtn.setStyle(inactiveStyle);
        listViewBtn.setStyle(inactiveStyle);
        
        switch (currentViewIndex) {
            case 0 -> tableViewBtn.setStyle(activeStyle);
            case 1 -> kanbanViewBtn.setStyle(activeStyle);
            case 2 -> listViewBtn.setStyle(activeStyle);
        }
    }
    @FXML
    private void loadTasks() {
        try {
            if (selectedProject == null) {
                statusLabel.setText(LABEL_SELECT_PROJECT);
                taskCountLabel.setText("0 tasks");
                return;
            }
            
            System.out.println(LABEL_LOADING + selectedProject.getName());
            
            allTasks.clear();
            allTasks.addAll(taskService.getTasksByProject(selectedProject.getId()));
            
            applyFilters();
            statusLabel.setText(LABEL_TASKS_LOADED);
            
        } catch (Exception e) {
            System.err.println(LABEL_ERROR_LOAD + e.getMessage());
            statusLabel.setText(LABEL_ERROR_LOAD);
        }
    }
    
    @FXML
    private void refreshTasks() {
        loadTasks();
    }
    
    private void initializeTableView() {
        tasksTableView.getColumns().clear();
        addColumn(COL_ID, "id", WIDTH_ID);
        addColumn(COL_TASK, "title", WIDTH_TASK);
        addColumn(COL_DESC, "description", WIDTH_DESC);
        addColumn(COL_STATUS, "status", WIDTH_STATUS);
        addColumn(COL_PRIORITY, "priority", WIDTH_PRIORITY);
        addColumn(COL_DUE, "dueDate", WIDTH_DUE);
        
        TableColumn<Task, Void> actionsColumn = new TableColumn<>(COL_ACTIONS);
        actionsColumn.setPrefWidth(WIDTH_ACTIONS);
        actionsColumn.setCellFactory(param -> createActionCell());
        tasksTableView.getColumns().add(actionsColumn);
        
        tasksTableView.setStyle(STYLE_TABLE);
        setupRowFactory();
    }
    
    private void addColumn(String title, String property, int width) {
        TableColumn<Task, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> {
            Object value = switch (property) {
                case "id" -> cellData.getValue().getId();
                case "title" -> cellData.getValue().getTitle();
                case "description" -> cellData.getValue().getDescription() != null ? cellData.getValue().getDescription() : "";
                case "status" -> cellData.getValue().getStatus() != null ? cellData.getValue().getStatus() : STATUS_TODO;
                case "priority" -> cellData.getValue().getPriority();
                case "dueDate" -> cellData.getValue().getDueDate() != null ? cellData.getValue().getDueDate().toString() : LABEL_DUE_NA;
                default -> "";
            };
            return new javafx.beans.property.SimpleStringProperty(value.toString());
        });
        column.setPrefWidth(width);
        tasksTableView.getColumns().add(column);
    }
    
    private void setupRowFactory() {
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
            private final HBox hbox = new HBox(ACTION_SPACING);
            
            {
                deleteBtn.setStyle(STYLE_DELETE_BTN);
                statusCombo.setPrefWidth(STATUS_COMBO_WIDTH);
                statusCombo.setStyle(STYLE_STATUS_COMBO);
                statusCombo.getItems().addAll(STATUS_TODO, STATUS_IN_PROGRESS, STATUS_DONE);
                
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
                column.setStyle(STYLE_KANBAN_DROP_HIGHLIGHT);
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
                    System.err.println(MSG_ERROR_PARSING);
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
            
            String status = task.getStatus() != null ? task.getStatus().toLowerCase() : STATUS_TODO.toLowerCase();
            switch (status) {
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
    
    private VBox createKanbanTaskCard(Task task) {
        VBox card = new VBox(CARD_SPACING);
        card.setStyle(STYLE_KANBAN_CARD);
        card.setMaxWidth(KANBAN_CARD_WIDTH);
        card.setPrefWidth(KANBAN_CARD_WIDTH);
        card.setUserData(task.getId());
        
        Label nameLabel = new Label(task.getTitle());
        nameLabel.setStyle(STYLE_KANBAN_CARD_LABEL);
        nameLabel.setWrapText(true);
        
        Label priorityLabel = new Label(LABEL_PRIORITY + task.getPriority());
        priorityLabel.setStyle(STYLE_KANBAN_CARD_META);
        
        Label dueDateLabel = new Label(LABEL_DUE + (task.getDueDate() != null ? task.getDueDate() : LABEL_DUE_NA));
        dueDateLabel.setStyle(STYLE_KANBAN_CARD_META);
        
        card.getChildren().addAll(nameLabel, priorityLabel, dueDateLabel);
        
        card.setOnDragDetected(event -> {
            Dragboard db = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(task.getId()));
            db.setContent(content);
            card.setStyle(STYLE_KANBAN_CARD_DRAGGING);
            event.consume();
        });
        
        card.setOnDragDone(event -> {
            card.setStyle(STYLE_KANBAN_CARD);
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
                System.out.println(MSG_TASK_MOVED + newStatus);
                applyFilters();
            }
        } catch (Exception e) {
            System.err.println(MSG_ERROR_UPDATE + e.getMessage());
        }
    }
    private void updateListView() {
        taskListContainer.getChildren().clear();
        for (Task task : filteredTasks) {
            taskListContainer.getChildren().add(createListTaskItem(task));
        }
    }
    
    private VBox createListTaskItem(Task task) {
        VBox item = new VBox(CARD_SPACING);
        item.setStyle(STYLE_ITEM);
        
        HBox header = new HBox(CARD_SPACING);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(
            createStyledLabel(task.getTitle(), STYLE_TITLE),
            createBadgeLabel(task.getStatus(), getStatusColor(task.getStatus())),
            createBadgeLabel(task.getPriority(), getPriorityColor(task.getPriority())),
            createSpacer(),
            createStyledLabel(LABEL_DUE + (task.getDueDate() != null ? task.getDueDate() : LABEL_DUE_NA), STYLE_META)
        );
        
        Label descLabel = createStyledLabel(
            task.getDescription() != null ? task.getDescription() : LABEL_NO_DESCRIPTION,
            STYLE_DESC
        );
        descLabel.setWrapText(true);
        
        item.getChildren().addAll(header, descLabel);
        item.setOnMouseClicked(event -> openTaskDetail(task));
        
        return item;
    }
    
    private Label createStyledLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        return label;
    }
    
    private Label createBadgeLabel(String text, String backgroundColor) {
        Label label = new Label(text);
        label.setStyle(STYLE_BADGE + " " + backgroundColor);
        return label;
    }
    
    private Region createSpacer() {
        Region spacer = new Region();
        spacer.setMinWidth(Region.USE_COMPUTED_SIZE);
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        return spacer;
    }
    
    @FXML
    private void handleAddTask() {
        if (selectedProject == null) {
            UIUtils.showWarning(TITLE_INFO, LABEL_SELECT_PROJECT_CREATE);
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/taskmanagement/fxml/dialog/CreateTaskView.fxml"));
            VBox root = loader.load();
            
            CreateTaskController controller = loader.getController();
            controller.setProject(selectedProject);
            
            Stage stage = new Stage();
            stage.setTitle(TITLE_CREATE_TASK);
            stage.setScene(new Scene(root, DIALOG_CREATE_WIDTH, DIALOG_CREATE_HEIGHT));
            stage.initModality(Modality.APPLICATION_MODAL);
            
            controller.setDialogStage(stage);
            controller.setOnTaskCreated(this::loadTasks);
            
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void openTaskDetail(Task task) {
        System.out.println(MSG_OPENING_TASK + task.getTitle());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/taskmanagement/fxml/main/TaskDetailView.fxml"));
            BorderPane root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle(TITLE_TASK_DETAILS + task.getTitle());
            stage.setScene(new Scene(root, DIALOG_DETAIL_WIDTH, DIALOG_DETAIL_HEIGHT));
            stage.initModality(Modality.APPLICATION_MODAL);
            
            stage.showAndWait();
            loadTasks();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleDeleteTask(Task task) {
        String confirmMessage = String.format(MSG_DELETE_CONFIRM, task.getTitle());
        if (UIUtils.showCustomConfirmation(TITLE_DELETE, null, confirmMessage)) {
            try {
                taskService.deleteTask(task.getId());
                loadTasks();
                UIUtils.showSuccess(TITLE_INFO, MSG_DELETE_SUCCESS);
            } catch (Exception e) {
                UIUtils.showError(TITLE_ERROR, MSG_ERROR_DELETE);
            }
        }
    }
    
    private void handleStatusChange(Task task, String newStatus) {
        try {
            task.setStatus(newStatus);
            taskService.updateTask(task);
            loadTasks();
        } catch (Exception e) {
            UIUtils.showError(TITLE_ERROR, MSG_ERROR_UPDATE_STATUS);
        }
    }
    
    private String getStatusColor(String status) {
        return STATUS_COLORS.getOrDefault(status != null ? status.toLowerCase() : "", COLOR_DEFAULT);
    }
    
    private String getPriorityColor(String priority) {
        return PRIORITY_COLORS.getOrDefault(priority != null ? priority.toLowerCase() : "", COLOR_DEFAULT);
    }
    
    @Override
    public void setMainLayoutController(MainLayoutController controller) {}
}
