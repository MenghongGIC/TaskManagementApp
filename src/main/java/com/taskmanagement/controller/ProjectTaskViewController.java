package com.taskmanagement.controller;

import com.taskmanagement.App;
import com.taskmanagement.model.Task;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.service.ProjectService;
import com.taskmanagement.utils.UIUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import java.util.Map;
import java.util.Optional;
public class ProjectTaskViewController implements TaskAwareController {
    
    // Status Values
    private static final String STATUS_TODO = "To Do";
    private static final String STATUS_IN_PROGRESS = "In Progress";
    private static final String STATUS_DONE = "Done";
    private static final String[] STATUS_OPTIONS = {STATUS_TODO, STATUS_IN_PROGRESS, STATUS_DONE};
    
    // Status Colors
    private static final Map<String, String> STATUS_COLORS = Map.of(
        "to do", "#3498db",
        "in progress", "#f39c12",
        "done", "#27ae60"
    );
    
    // Priority Colors
    private static final Map<String, String> PRIORITY_COLORS = Map.of(
        "HIGH", "#e74c3c",
        "MEDIUM", "#f39c12",
        "LOW", "#27ae60"
    );
    
    // View Types
    private static final String VIEW_TABLE = "table";
    private static final String VIEW_KANBAN = "kanban";
    private static final String VIEW_LIST = "list";
    
    // Button Colors
    private static final String ACTIVE_BUTTON_COLOR = "#3498db";
    private static final String INACTIVE_BUTTON_COLOR = "#95a5a6";
    
    // Styles
    private static final String KANBAN_CARD_STYLE = 
        "-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-padding: 10; " +
        "-fx-background-color: white; -fx-border-radius: 4; -fx-cursor: hand;";
    private static final String LIST_ITEM_STYLE = 
        "-fx-padding: 12; -fx-border-color: #ecf0f1; -fx-border-width: 0 0 1 0; -fx-cursor: hand;";
    private static final String STATUS_COMBO_STYLE = "-fx-font-size: 10px; -fx-padding: 3;";
    private static final String ACTION_BUTTON_STYLE = "-fx-font-size: 10px; -fx-padding: 3 8;";
    
    // Message Defaults
    private static final String DEFAULT_VALUE = "-";
    private static final String UNASSIGNED = "Unassigned";
    
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
        
        setupUI();
        displayProjectInfo();
        switchToView(projectService.getViewType());
    }
    
    private void setupUI() {
        setupViewToggleButtons();
    }
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
    
    private void switchToTableView() {
        projectService.setViewType(VIEW_TABLE);
        switchToView(VIEW_TABLE);
        setupTableView();
    }
    
    private void setupTableView() {
        if (tasksTable == null) return;
        
        setupTableColumns();
        setupTableActionsColumn();
        tasksTable.setItems(projectService.getProjectTasks());
    }
    
    private void setupTableColumns() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getOrDefault(cellData.getValue().getDescription(), DEFAULT_VALUE)));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        priorityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPriority()));
        dueDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(formatDueDate(cellData.getValue())));
        assigneeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getAssigneeDisplay(cellData.getValue())));
    }
    
    private String formatDueDate(Task task) {
        return task.getDueDate() != null ? task.getDueDate().toString() : DEFAULT_VALUE;
    }
    
    private String getAssigneeDisplay(Task task) {
        return task.getAssignee() != null ? task.getAssignee().getUsername() : UNASSIGNED;
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
                HBox actions = createTableActionButtons(task);
                setGraphic(actions);
            }
        });
    }
    
    private HBox createTableActionButtons(Task task) {
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll(STATUS_OPTIONS);
        statusCombo.setValue(task.getStatus());
        statusCombo.setStyle(STATUS_COMBO_STYLE);
        statusCombo.setOnAction(e -> handleStatusChange(task, statusCombo.getValue()));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle(ACTION_BUTTON_STYLE);
        deleteBtn.setOnAction(e -> handleDeleteTask(task));
        
        HBox actions = new HBox(5, statusCombo, deleteBtn);
        actions.setAlignment(Pos.CENTER);
        return actions;
    }
    
    private void handleStatusChange(Task task, String newStatus) {
        try {
            task.setStatus(newStatus);
            taskRepository.update(task);
            projectService.updateTaskStatus(task.getId(), newStatus);
        } catch (Exception e) {
            showErrorAlert("Error", "Failed to update task status: " + e.getMessage());
        }
    }    
    private void switchToKanbanView() {
        projectService.setViewType(VIEW_KANBAN);
        switchToView(VIEW_KANBAN);
        setupKanbanView();
    }
    
    private void setupKanbanView() {
        if (todoColumn == null || inProgressColumn == null || doneColumn == null) return;
        
        todoColumn.getChildren().clear();
        inProgressColumn.getChildren().clear();
        doneColumn.getChildren().clear();
        
        for (Task task : projectService.getProjectTasks()) {
            VBox card = createKanbanCard(task);
            addCardToColumn(card, task.getStatus());
        }
    }
    
    private void addCardToColumn(VBox card, String status) {
        if (STATUS_IN_PROGRESS.equalsIgnoreCase(status)) {
            inProgressColumn.getChildren().add(card);
        } else if (STATUS_DONE.equalsIgnoreCase(status)) {
            doneColumn.getChildren().add(card);
        } else {
            todoColumn.getChildren().add(card);
        }
    }
    
    private VBox createKanbanCard(Task task) {
        VBox card = new VBox(5);
        card.setStyle(KANBAN_CARD_STYLE);
        card.setMinWidth(200);
        card.setMaxWidth(200);
        card.setUserData(task);
        
        Label titleLabel = new Label(task.getTitle());
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        titleLabel.setWrapText(true);
        
        Label priorityLabel = createPriorityBadge(task.getPriority());
        Label assigneeLabel = createAssigneeLabel(task);
        
        card.getChildren().addAll(titleLabel, priorityLabel, assigneeLabel);
        
        if (task.getDueDate() != null) {
            Label dueDateLabel = new Label(task.getDueDate().toString());
            dueDateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #e74c3c;");
            card.getChildren().add(dueDateLabel);
        }
        
        setupCardDragHandlers(card);
        return card;
    }
    
    private Label createPriorityBadge(String priority) {
        String priorityColor = PRIORITY_COLORS.getOrDefault(priority, INACTIVE_BUTTON_COLOR);
        Label priorityLabel = new Label("Priority: " + priority);
        priorityLabel.setStyle(
            "-fx-font-size: 10px; -fx-text-fill: white; -fx-padding: 3 8; " +
            "-fx-background-color: " + priorityColor + "; -fx-background-radius: 3;"
        );
        return priorityLabel;
    }
    
    private Label createAssigneeLabel(Task task) {
        String assignee = task.getAssignee() != null ? 
                         task.getAssignee().getUsername() : UNASSIGNED;
        Label assigneeLabel = new Label(assignee);
        assigneeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
        return assigneeLabel;
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

    private void switchToListView() {
        projectService.setViewType(VIEW_LIST);
        switchToView(VIEW_LIST);
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
        item.setStyle(LIST_ITEM_STYLE);
        
        VBox statusIndicator = createStatusIndicator(task.getStatus());
        VBox details = createListItemDetails(task);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        item.getChildren().addAll(statusIndicator, details, spacer);
        return item;
    }
    
    private VBox createStatusIndicator(String status) {
        String statusColor = STATUS_COLORS.getOrDefault(status.toLowerCase(), INACTIVE_BUTTON_COLOR);
        VBox statusIndicator = new VBox();
        statusIndicator.setPrefWidth(5);
        statusIndicator.setStyle("-fx-background-color: " + statusColor + ";");
        return statusIndicator;
    }
    
    private VBox createListItemDetails(Task task) {
        VBox details = new VBox(3);
        Label titleLabel = new Label(task.getTitle());
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        HBox infoRow = createListItemInfoRow(task);
        details.getChildren().addAll(titleLabel, infoRow);
        return details;
    }
    
    private HBox createListItemInfoRow(Task task) {
        HBox infoRow = new HBox(15);
        Label statusLabel = new Label("Status: " + task.getStatus());
        statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        
        String assignee = task.getAssignee() != null ? 
                         task.getAssignee().getUsername() : "Unassigned";
        Label assigneeLabel = new Label("Assignee: " + assignee);
        assigneeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        
        infoRow.getChildren().addAll(statusLabel, assigneeLabel);
        return infoRow;
    }
    private void switchToView(String viewType) {
        if (viewStack == null) return;
        
        String tableColor = viewType.equals(VIEW_TABLE) ? ACTIVE_BUTTON_COLOR : INACTIVE_BUTTON_COLOR;
        String kanbanColor = viewType.equals(VIEW_KANBAN) ? ACTIVE_BUTTON_COLOR : INACTIVE_BUTTON_COLOR;
        String listColor = viewType.equals(VIEW_LIST) ? ACTIVE_BUTTON_COLOR : INACTIVE_BUTTON_COLOR;
        
        tableViewBtn.setStyle("-fx-background-color: " + tableColor + "; -fx-text-fill: white;");
        kanbanViewBtn.setStyle("-fx-background-color: " + kanbanColor + "; -fx-text-fill: white;");
        listViewBtn.setStyle("-fx-background-color: " + listColor + "; -fx-text-fill: white;");
    }
    
    private String getOrDefault(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }
    
    private void handleDeleteTask(Task task) {
        Optional<ButtonType> result = showConfirmation("Delete Task",
            String.format("Delete task: %s?", task.getTitle()));
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                taskRepository.delete(task.getId());
                projectService.removeTask(task.getId());
                refreshCurrentView();
            } catch (Exception e) {
                showErrorAlert("Error", "Failed to delete task: " + e.getMessage());
            }
        }
    }
    
    private void refreshCurrentView() {
        String currentView = projectService.getViewType();
        if (VIEW_TABLE.equals(currentView)) {
            setupTableView();
        } else if (VIEW_KANBAN.equals(currentView)) {
            setupKanbanView();
        } else if (VIEW_LIST.equals(currentView)) {
            setupListView();
        }
    }
    
    @FXML
    private void handleBackToProjects() {
        try {
            App.setRoot("main/ProjectListView");
        } catch (Exception e) {
            showErrorAlert("Error", "Navigation error: " + e.getMessage());
        }
    }    
    private void showErrorAlert(String title, String message) {
        UIUtils.showError(title, message);
    }
    
    private Optional<ButtonType> showConfirmation(String title, String content) {
        return Optional.of(UIUtils.showCustomConfirmation(title, null, content) ? ButtonType.OK : ButtonType.CANCEL);
    }
}
