package com.taskmanagement.controller;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.taskmanagement.App;
import com.taskmanagement.constants.AppConstants;
import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import com.taskmanagement.model.Project;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.service.ProjectService;
import com.taskmanagement.utils.CurrentUser;
import com.taskmanagement.utils.UIUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.beans.property.SimpleStringProperty;
@SuppressWarnings("unused")

public class DashboardController {

    private static final String CARD_STYLE = "-fx-background-color: white; -fx-border-radius: 5; -fx-padding: 12; -fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);";
    private static final String CARD_DRAG_STYLE = "-fx-background-color: #e8f4f8; -fx-border-radius: 5; -fx-padding: 12; -fx-border-color: #3498db; -fx-border-width: 2; -fx-cursor: move; -fx-opacity: 0.8;";
    private static final String KANBAN_CARD_STYLE = "-fx-background-color: white; -fx-border-radius: 5; -fx-padding: 10; -fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 4, 0, 0, 2);";
    private static final String COLUMN_HIGHLIGHT_STYLE = "-fx-border-color: #2ecc71; -fx-border-width: 3; -fx-border-radius: 5; -fx-background-color: #c8e6c9; -fx-padding: 5;";
    private static final String COLUMN_NORMAL_STYLE = "-fx-padding: 5; -fx-fill-width: true;";
    private static final String BTN_ACTIVE = "-fx-padding: 8 12; -fx-font-size: 11px; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 4;";
    private static final String BTN_INACTIVE = "-fx-padding: 8 12; -fx-font-size: 11px; -fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 4;";
    private static final String LABEL_TITLE = "-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2c3e50;";
    private static final String LABEL_DESC = "-fx-font-size: 10px; -fx-text-fill: #7f8c8d;";
    private static final String PROFILE_IMG_PATH = "/com/taskmanagement/img/profile-default.png";

    // Input Controls
    @FXML private TextField taskNameField;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private ComboBox<Project> projectComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ComboBox<String> priorityComboBox;

    // View Toggle Buttons
    @FXML private Button tableViewBtn;
    @FXML private Button kanbanViewBtn;
    @FXML private Button listViewBtn;
    @FXML private Button searchBtn;
    @FXML private Button clearSearchBtn;

    // Kanban View - Columns
    @FXML private VBox todoColumn;
    @FXML private VBox inProgressColumn;
    @FXML private VBox doneColumn;

    // Kanban View - Panels
    @FXML private VBox todoPanel;
    @FXML private VBox inProgressPanel;
    @FXML private VBox donePanel;

    // Kanban View - ScrollPanes
    @FXML private ScrollPane todoScrollPane;
    @FXML private ScrollPane inProgressScrollPane;
    @FXML private ScrollPane doneScrollPane;

    // Table View
    @FXML private VBox tableView;
    @FXML private TableView<Task> tasksTableView;

    // List View
    @FXML private VBox taskListContainer;

    // View Stack
    @FXML private StackPane viewStack;
    @FXML private VBox kanbanView;

    // Status & Info Labels
    @FXML private Label statusLabel;
    @FXML private Label taskCountLabel;
    @FXML private Label usernameLabel;

    // Profile & User Controls
    @FXML private ImageView profileImageView;
    @FXML private Button profileButton;
    @FXML private Button settingsButton;
    @FXML private Button logoutButton;

    // State
    private Task draggedTask = null;
    private final List<Task> allTasks = new ArrayList<>();
    private boolean isTableViewActive = false;

    @FXML
    public void initialize() {
        try {
            initializeComboBoxes();
            initializeSearch();
            loadTasks();
            configureScrollPanes();
            setupDragDropColumns();
            initializeTableView();
            initializeViewStack();
        } catch (Exception e) {
            UIUtils.showError("Initialization Error", e.getMessage());
        }
    }

    private void initializeComboBoxes() {
        if (priorityCombo != null) {
            priorityCombo.getItems().addAll(AppConstants.PRIORITY_LOW, AppConstants.PRIORITY_MEDIUM, AppConstants.PRIORITY_HIGH);
            priorityCombo.setValue(AppConstants.PRIORITY_MEDIUM);
        }
        
        if (projectComboBox != null) {
            loadProjectsIntoCombo();
            projectComboBox.setOnAction(event -> handleProjectSelection());
        }
        
        if (statusComboBox != null) {
            statusComboBox.getItems().addAll("All", AppConstants.STATUS_TODO, AppConstants.STATUS_IN_PROGRESS, AppConstants.STATUS_DONE);
            statusComboBox.setValue("All");
            statusComboBox.setOnAction(event -> filterTasks());
        }
        
        if (priorityComboBox != null) {
            priorityComboBox.getItems().addAll("All", AppConstants.PRIORITY_LOW, AppConstants.PRIORITY_MEDIUM, AppConstants.PRIORITY_HIGH);
            priorityComboBox.setValue("All");
            priorityComboBox.setOnAction(event -> filterTasks());
        }
    }

    private void initializeSearch() {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, old, newVal) -> {
                filterTasks();
                if (clearSearchBtn != null) clearSearchBtn.setVisible(!newVal.isEmpty());
            });
        }
    }

    private void configureScrollPanes() {
        Arrays.asList(todoScrollPane, inProgressScrollPane, doneScrollPane).forEach(this::configureScrollPane);
    }

    private void setupDragDropColumns() {
        setupColumnDropHandler(todoScrollPane, todoColumn, AppConstants.STATUS_TODO);
        setupColumnDropHandler(inProgressScrollPane, inProgressColumn, AppConstants.STATUS_IN_PROGRESS);
        setupColumnDropHandler(doneScrollPane, doneColumn, AppConstants.STATUS_DONE);
    }

    private void initializeTableView() {
        if (tasksTableView != null) {
            setupTableColumns();
            isTableViewActive = true;
        }
    }

    private void initializeViewStack() {
        if (viewStack != null && viewStack.getChildren().size() > 1) {
            for (int i = 0; i < viewStack.getChildren().size(); i++) {
                viewStack.getChildren().get(i).setVisible(i == 0);
            }
        }
    }
    
    private void setupTableColumns() {
        if (tasksTableView == null) {
            return;
        }
        
        tasksTableView.getColumns().clear();
        
        TableColumn<Task, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        
        TableColumn<Task, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(150);
        
        TableColumn<Task, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        
        TableColumn<Task, String> priorityCol = new TableColumn<>("Priority");
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priorityCol.setPrefWidth(100);
        
        TableColumn<Task, String> assigneeCol = new TableColumn<>("Assignee");
        assigneeCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(
                cellData.getValue().getAssignee() != null ? 
                cellData.getValue().getAssignee().getUsername() : "-"
            )
        );
        assigneeCol.setPrefWidth(120);
        
        TableColumn<Task, String> dueCol = new TableColumn<>("Due Date");
        dueCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        dueCol.setPrefWidth(120);
        
        tasksTableView.getColumns().addAll(idCol, titleCol, statusCol, priorityCol, assigneeCol, dueCol);
        
        tasksTableView.setRowFactory(tv -> {
            javafx.scene.control.TableRow<Task> row = new javafx.scene.control.TableRow<Task>() {
                @Override
                protected void updateItem(Task item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("");
                    } else {
                        setStyle("-fx-cursor: hand;");
                    }
                }
            };
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openTaskDetail(row.getItem());
                }
            });
            return row;
        });
    }
    
    private void updateTableViewDisplay() {
        if (tasksTableView == null) {
            return;
        }
        List<Task> filteredTasks = getFilteredTasks();
        
        ObservableList<Task> tableItems = FXCollections.observableArrayList(filteredTasks);
        tasksTableView.setItems(tableItems);
        
        if (taskCountLabel != null) {
            taskCountLabel.setText(filteredTasks.size() + " tasks");
        }
    }
    
    private void configureScrollPane(ScrollPane scrollPane) {
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
    }

    private void loadTasks() {
        try {
            clearAllPanels();
            allTasks.clear();

            String selectedProjectName = getSelectedProjectName();
            TaskRepository taskRepository = new TaskRepository();
            List<Task> dbTasks = taskRepository.findAll();
            
            if (dbTasks != null && !dbTasks.isEmpty()) {
                List<Task> filtered = "All Projects".equals(selectedProjectName) ? dbTasks :
                    dbTasks.stream()
                        .filter(t -> t.getProject() != null && selectedProjectName.equals(t.getProject().getName()))
                        .collect(Collectors.toList());
                allTasks.addAll(filtered);
            }
            
            filterTasks();
            updateTableViewDisplay();
        } catch (Exception e) {
            showStatus(AppConstants.Messages.ERROR_LOADING_TASKS + e.getMessage(), true);
        }
    }

    private void clearAllPanels() {
        Arrays.asList(todoPanel, inProgressPanel, donePanel, todoColumn, inProgressColumn, doneColumn, taskListContainer)
            .stream().filter(Objects::nonNull).forEach(pane -> pane.getChildren().clear());
    }
    private String getSelectedProjectName() {
        Object obj = projectComboBox != null ? projectComboBox.getValue() : null;
        if (obj instanceof Project) {
            String name = ((Project) obj).getName();
            if (name != null && !name.isEmpty()) return name;
        }
        return "All Projects";
    }
    @FXML
    private void handleCreateTask() {
        String taskName = taskNameField != null ? taskNameField.getText().trim() : "";
        if (taskName.isEmpty()) {
            showStatus(AppConstants.Messages.PLEASE_FILL_REQUIRED, true);
            return;
        }

        try {
            Object obj = projectComboBox != null ? projectComboBox.getValue() : null;
            if (!(obj instanceof Project)) {
                showStatus("Please select a project", true);
                return;
            }
            
            Project project = (Project) obj;
            Task newTask = new Task();
            newTask.setTitle(taskName);
            newTask.setProject(project);
            newTask.setStatus(AppConstants.STATUS_TODO);
            newTask.setPriority(priorityCombo != null ? (String) priorityCombo.getValue() : AppConstants.PRIORITY_MEDIUM);
            User currentUser = new User();
            currentUser.setUsername(CurrentUser.getUsername());
            newTask.setCreatedBy(currentUser);
            
            TaskRepository taskRepository = new TaskRepository();
            Task savedTask = taskRepository.save(newTask);
            
            if (savedTask == null || savedTask.getId() == null) {
                showStatus("Error: Task was not saved properly (missing ID)", true);
                return;
            }
            
            allTasks.add(savedTask);
            filterTasks();
            taskNameField.clear();
            showStatus(AppConstants.Messages.TASK_CREATED, false);
        } catch (Exception e) {
            showStatus("Error creating task: " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleCreateProject() {
        String projectName = taskNameField != null ? taskNameField.getText().trim() : "";
        if (projectName.isEmpty()) {
            showStatus(AppConstants.Messages.PLEASE_FILL_REQUIRED, true);
            return;
        }

        try {
            Project newProject = new Project();
            newProject.setName(projectName);
            newProject.setDescription("New Project");
            
            ProjectService projectService = new ProjectService();
            projectService.createProject(projectName, "New Project", "#3498db");
            loadProjectsIntoCombo();
            taskNameField.clear();
            showStatus(AppConstants.Messages.PROJECT_CREATED, false);
        } catch (Exception e) {
            showStatus("Error: " + e.getMessage(), true);
        }
    }
    
    private void _addTaskCard(Task task) {
        if (todoPanel == null && inProgressPanel == null && donePanel == null) return;
        
        HBox taskCard = createTaskCard(task);
        String status = task.getStatus() != null ? task.getStatus() : AppConstants.STATUS_TODO;
        
        if (AppConstants.STATUS_IN_PROGRESS.equals(status) && inProgressPanel != null) {
            inProgressPanel.getChildren().add(taskCard);
        } else if (AppConstants.STATUS_DONE.equals(status) && donePanel != null) {
            donePanel.getChildren().add(taskCard);
        } else if (todoPanel != null) {
            todoPanel.getChildren().add(taskCard);
        }
    }

    private HBox createTaskCard(Task task) {
        HBox card = new HBox(10);
        card.setStyle(CARD_STYLE);
        card.setPrefHeight(100);
        card.setMinHeight(100);
        card.setUserData(task);

        VBox content = createCardContent(task);
        VBox actions = createCardActions(task);
        card.getChildren().addAll(content, actions);
        HBox.setHgrow(content, javafx.scene.layout.Priority.ALWAYS);
        setupTaskCardDragDrop(card, task);
        return card;
    }

    private VBox createCardContent(Task task) {
        VBox content = new VBox(5);
        Label titleLabel = new Label(task.getTitle());
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label descLabel = new Label(task.getDescription() != null ? task.getDescription() : "");
        descLabel.setStyle(LABEL_DESC);
        descLabel.setWrapText(true);
        content.getChildren().addAll(titleLabel, descLabel);
        return content;
    }

    private VBox createCardActions(Task task) {
        VBox actions = new VBox(5);
        Button editBtn = new Button("✏️");
        editBtn.setStyle("-fx-padding: 5; -fx-font-size: 10;");
        editBtn.setOnAction(e -> handleEditTask(task));
        Button deleteBtn = new Button("🗑️");
        deleteBtn.setStyle("-fx-padding: 5; -fx-font-size: 10;");
        deleteBtn.setOnAction(e -> handleDeleteTask(task));
        actions.getChildren().addAll(editBtn, deleteBtn);
        return actions;
    }

    @FXML
    private void handleEditTask(Task task) {
        showStatus("Edit feature coming soon for: " + task.getTitle(), false);
    }

    @FXML
    private void handleDeleteTask(Task task) {
        try {
            new TaskRepository().delete(task.getId());
            allTasks.remove(task);
            loadTasks();
            showStatus(AppConstants.Messages.TASK_DELETED, false);
        } catch (Exception e) {
            showStatus(AppConstants.Messages.ERROR_DELETING_TASK + e.getMessage(), true);
        }
    }

    @FXML
    private void handleProfile() {
        showStatus("Profile panel coming soon", false);
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
    private void showTableView() {
        switchView(0, tableViewBtn);
        updateTableViewDisplay();
    }

    @FXML
    private void showKanbanView() {
        switchView(1, kanbanViewBtn);
        filterTasks();
    }

    @FXML
    private void showListView() {
        switchView(2, listViewBtn);
        filterTasks();
    }

    private void switchView(int viewIndex, Button activeButton) {
        isTableViewActive = (viewIndex == 0);
        if (viewStack != null && viewStack.getChildren().size() > viewIndex) {
            for (int i = 0; i < viewStack.getChildren().size(); i++) {
                viewStack.getChildren().get(i).setVisible(i == viewIndex);
            }
        }
        updateButtonStyles(activeButton);
    }

    @FXML
    private void showCreateTaskDialog() {
        Object obj = projectComboBox != null ? projectComboBox.getValue() : null;
        
        if (!(obj instanceof Project)) {
            showStatus("Please select a project first", true);
            return;
        }
        
        Project project = (Project) obj;
        if ("All Projects".equals(project.getName())) {
            showStatus("Please select a specific project", true);
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/taskmanagement/fxml/dialog/CreateTaskView.fxml"));
            BorderPane root = loader.load();
            
            CreateTaskController controller = loader.getController();
            controller.setCurrentProject(project);
            
            Stage stage = new Stage();
            stage.setTitle("Create New Task");
            stage.setScene(new Scene(root, 650, 700));
            stage.initModality(Modality.APPLICATION_MODAL);
            controller.setDialogStage(stage);
            stage.setOnHidden(event -> { loadTasks(); filterTasks(); });
            stage.showAndWait();
        } catch (Exception e) {
            showStatus("Error opening task dialog: " + e.getMessage(), true);
        }
    }

    @FXML
    private void refreshTasks() {
        try {
            loadTasks();
            filterTasks();
            showStatus("Tasks refreshed", false);
        } catch (Exception e) {
            showStatus("Error refreshing tasks", true);
        }
    }
    
    private void updateButtonStyles(Button activeButton) {
        if (tableViewBtn != null) tableViewBtn.setStyle(tableViewBtn == activeButton ? BTN_ACTIVE : BTN_INACTIVE);
        if (kanbanViewBtn != null) kanbanViewBtn.setStyle(kanbanViewBtn == activeButton ? BTN_ACTIVE : BTN_INACTIVE);
        if (listViewBtn != null) listViewBtn.setStyle(listViewBtn == activeButton ? BTN_ACTIVE : BTN_INACTIVE);
    }

    @FXML
    private void performSearch() {
        String searchText = searchField != null ? searchField.getText().trim() : "";
        if (!searchText.isEmpty()) {
            filterTasks();
            if (isTableViewActive) {
                updateTableViewDisplay();
            }
        }
    }

    @FXML
    private void clearSearch() {
        if (searchField != null) {
            searchField.clear();
            filterTasks();
            if (isTableViewActive) {
                updateTableViewDisplay();
            }
        }
        if (clearSearchBtn != null) {
            clearSearchBtn.setVisible(false);
        }
    }

    private void loadProjectsIntoCombo() {
        try {
            if (projectComboBox == null) return;
            
            List<Project> projects = new ProjectService().getAllProjects();
            List<Project> items = new ArrayList<>();
            
            Project allProjects = new Project();
            allProjects.setName("All Projects");
            items.add(allProjects);
            
            if (projects != null && !projects.isEmpty()) {
                items.addAll(projects);
            }
            
            projectComboBox.setItems(FXCollections.observableArrayList(items));
            projectComboBox.setConverter(new javafx.util.StringConverter<Project>() {
                @Override
                public String toString(Project obj) {
                    return obj != null ? obj.getName() : "All Projects";
                }
                @Override
                public Project fromString(String s) { return null; }
            });
            projectComboBox.setValue(allProjects);
        } catch (Exception e) {
            setFallbackProjects();
        }
    }

    private void setFallbackProjects() {
        if (projectComboBox == null) return;
        Project all = new Project();
        all.setName("All Projects");
        projectComboBox.setItems(FXCollections.observableArrayList(all));
        projectComboBox.setValue(all);
    }

    private void handleProjectSelection() {
        Object obj = projectComboBox != null ? projectComboBox.getValue() : null;
        if (!(obj instanceof Project)) return;
        
        Project project = (Project) obj;
        if ("All Projects".equals(project.getName())) loadTasks();
        
        if (isTableViewActive) updateTableViewDisplay();
        else filterTasks();
    }

    private void filterTasks() {
        clearAllPanels();
        List<Task> filtered = getFilteredTasks();
        
        for (Task task : filtered) {
            addTaskToAppropriateView(task);
        }
        
        if (taskCountLabel != null) taskCountLabel.setText(filtered.size() + " tasks");
    }

    private void addTaskToAppropriateView(Task task) {
        String status = task.getStatus();
        
        if (AppConstants.STATUS_TODO.equals(status)) addToTodoViews(task);
        else if (AppConstants.STATUS_IN_PROGRESS.equals(status)) addToInProgressViews(task);
        else if (AppConstants.STATUS_DONE.equals(status)) addToDoneViews(task);
        
        if (taskListContainer != null) taskListContainer.getChildren().add(createListItem(task));
    }

    private void addToTodoViews(Task task) {
        if (todoPanel != null) todoPanel.getChildren().add(createTaskCard(task));
        if (todoColumn != null) todoColumn.getChildren().add(createKanbanCard(task));
    }

    private void addToInProgressViews(Task task) {
        if (inProgressPanel != null) inProgressPanel.getChildren().add(createTaskCard(task));
        if (inProgressColumn != null) inProgressColumn.getChildren().add(createKanbanCard(task));
    }

    private void addToDoneViews(Task task) {
        if (donePanel != null) donePanel.getChildren().add(createTaskCard(task));
        if (doneColumn != null) doneColumn.getChildren().add(createKanbanCard(task));
    }

    private List<Task> getFilteredTasks() {
        String projectName = getSelectedProjectName();
        String status = statusComboBox != null ? statusComboBox.getValue() : "All";
        String priority = priorityComboBox != null ? priorityComboBox.getValue() : "All";
        String search = (searchField != null ? searchField.getText().trim() : "").toLowerCase();

        return allTasks.stream()
            .filter(task -> matchesProjectFilter(task, projectName))
            .filter(task -> matchesStatusFilter(task, status))
            .filter(task -> matchesPriorityFilter(task, priority))
            .filter(task -> search.isEmpty() || matchesSearchCriteria(task, search))
            .sorted(Comparator.comparingLong(task -> task.getId() != null ? task.getId() : 0L))
            .collect(Collectors.toList());
    }

    private boolean matchesProjectFilter(Task task, String projectName) {
        if ("All Projects".equals(projectName)) return true;
        return task.getProject() != null && projectName.equals(task.getProject().getName());
    }

    private boolean matchesStatusFilter(Task task, String status) {
        if (status == null || "All".equals(status)) return true;
        return status.equals(task.getStatus());
    }

    private boolean matchesPriorityFilter(Task task, String priority) {
        if (priority == null || "All".equals(priority)) return true;
        return priority.equals(task.getPriority());
    }

    private boolean matchesSearchCriteria(Task task, String search) {
        if (search == null || search.isEmpty()) return true;
        return containsIgnoreCase(task.getTitle(), search)
            || containsIgnoreCase(task.getDescription(), search)
            || String.valueOf(task.getId()).contains(search)
            || containsIgnoreCase(task.getStatus(), search)
            || containsIgnoreCase(task.getPriority(), search);
    }

    public List<Task> searchTasks(String query) {
        if (query == null || query.isEmpty()) return allTasks;
        
        String search = query.toLowerCase();
        return allTasks.stream()
            .filter(task -> matchesSearchCriteria(task, search))
            .sorted((t1, t2) -> Long.compare(t1.getId(), t2.getId()))
            .collect(Collectors.toList());
    }
    
    private boolean containsIgnoreCase(String text, String search) {
        return text != null && text.toLowerCase().contains(search);
    }

    private void setupColumnDropHandler(ScrollPane scrollPane, VBox column, String status) {
        if (scrollPane == null || column == null) return;

        configureDropTarget(scrollPane, column, status);
    }

    private void configureDropTarget(Node target, VBox column, String status) {
        target.setOnDragOver(event -> {
            if (event.getGestureSource() != target && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
                if (column != null) highlightColumn(column, true);
            }
            event.consume();
        });

        target.setOnDragEntered(event -> {
            if (column != null) highlightColumn(column, true);
        });

        target.setOnDragExited(event -> {
            if (column != null) highlightColumn(column, false);
        });

        target.setOnDragDropped(event -> {
            boolean success = event.getDragboard().hasString() && handleKanbanDrop(column, status);
            if (column != null) highlightColumn(column, false);
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private boolean handleKanbanDrop(VBox column, String status) {
        if (draggedTask == null) {
            return false;
        }
        return updateTaskStatus(draggedTask, status);
    }

    private boolean updateTaskStatus(Task task, String newStatus) {
        if (task == null || newStatus == null) {
            return false;
        }

        if (newStatus.equals(task.getStatus())) {
            return true;
        }

        task.setStatus(newStatus);
        try {
            new TaskRepository().update(task);
            refreshViewsAfterTaskUpdate();
            showStatus("Task moved to " + newStatus, false);
            return true;
        } catch (Exception e) {
            showStatus(AppConstants.Messages.ERROR_UPDATING_TASK + e.getMessage(), true);
            return false;
        }
    }

    private void refreshViewsAfterTaskUpdate() {
        filterTasks();
        updateTableViewDisplay();
    }

    private void highlightColumn(VBox column, boolean highlight) {
        column.setStyle(highlight ? COLUMN_HIGHLIGHT_STYLE : COLUMN_NORMAL_STYLE);
    }

    private VBox createKanbanCard(Task task) {
        VBox card = new VBox(5);
        card.setStyle(KANBAN_CARD_STYLE);
        card.setPrefWidth(200);
        card.setMinHeight(80);
        card.setUserData(task);

        Label titleLabel = new Label(task.getTitle());
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        titleLabel.setWrapText(true);
        
        Label descriptionLabel = new Label(task.getDescription() != null ? task.getDescription() : "");
        descriptionLabel.setStyle(LABEL_DESC);
        descriptionLabel.setWrapText(true);
        if (task.getDescription() == null || task.getDescription().isEmpty()) {
            descriptionLabel.setVisible(false);
        }
        
        Label priorityLabel = createPriorityBadge(task);
        card.getChildren().addAll(titleLabel, descriptionLabel, priorityLabel);
        card.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                openTaskDetail(task);
            }
        });
        setupTaskCardDragDrop(card, task);
        return card;
    }

    private Label createPriorityBadge(Task task) {
        String priorityColor = UIUtils.getPriorityColor(task.getPriority());
        Label priorityLabel = new Label(task.getPriority() != null ? task.getPriority() : AppConstants.PRIORITY_LOW);
        priorityLabel.setStyle("-fx-background-color: " + priorityColor + "; -fx-text-fill: white; "
                             + "-fx-padding: 3 8; -fx-border-radius: 3; -fx-font-size: 9px; "
                             + "-fx-font-weight: bold;");
        return priorityLabel;
    }

    private void setupTaskCardDragDrop(Node card, Task task) {
        card.setOnDragDetected(event -> handleTaskDragDetected(card, task, event));
        card.setOnDragEntered(event -> handleTaskDragEntered(card, event));
        card.setOnDragExited(event -> handleTaskDragExited(card, event));
        card.setOnDragDone(event -> handleTaskDragDone(event));
    }

    private void handleTaskDragDetected(Node card, Task task, MouseEvent event) {
        draggedTask = task;
        Dragboard dragboard = card.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.putString(String.valueOf(task.getId() != null ? task.getId() : task.getTitle()));
        dragboard.setContent(content);
        try {
            if (card instanceof VBox) {
                dragboard.setDragView(card.snapshot(null, null), 50, 50);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (card instanceof VBox) {
            card.setStyle(((VBox) card).getStyle() + "; -fx-border-color: #3498db; -fx-border-width: 2; -fx-opacity: 0.8;");
        }
        event.consume();
    }

    private void handleTaskDragEntered(Node card, DragEvent event) {
        if (event.getGestureSource() != card && event.getDragboard().hasString()) {
            if (card instanceof VBox) {
                card.setStyle(((VBox) card).getStyle() + "; -fx-border-color: #2ecc71; -fx-border-width: 2;");
            }
        }
    }

    private void handleTaskDragExited(Node card, DragEvent event) {
        if (card instanceof VBox) {
            card.setStyle(KANBAN_CARD_STYLE);
        }
    }

    private void handleTaskDragDone(DragEvent event) {
        draggedTask = null;
        event.consume();
    }

    private VBox createListItem(Task task) {
        VBox item = new VBox(5);
        item.setStyle("-fx-border-color: #ecf0f1; -fx-border-width: 0 0 1 0; -fx-padding: 12; -fx-background-color: #ffffff;");
        
        HBox headerRow = createListItemHeader(task);
        Label descLabel = createListItemDescription(task);
        HBox footerRow = createListItemFooter(task);
        
        item.getChildren().addAll(headerRow, descLabel, footerRow);
        item.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                openTaskDetail(task);
            }
        });
        item.setStyle(item.getStyle() + "; -fx-cursor: hand;");
        return item;
    }

    private HBox createListItemHeader(Task task) {
        HBox headerRow = new HBox(10);
        headerRow.setStyle("-fx-alignment: CENTER_LEFT;");
        
        Label titleLabel = new Label(task.getTitle() != null ? task.getTitle() : "No Title");
        titleLabel.setStyle(LABEL_TITLE);
        
        Label statusLabel = new Label(task.getStatus() != null ? task.getStatus() : AppConstants.STATUS_TODO);
        statusLabel.setStyle("-fx-background-color: " + UIUtils.getStatusColor(task.getStatus()) + "; -fx-text-fill: white; -fx-padding: 2 8; -fx-border-radius: 2; -fx-font-size: 10px;");
        
        Label priorityLabel = new Label(task.getPriority() != null ? task.getPriority() : AppConstants.PRIORITY_LOW);
        priorityLabel.setStyle("-fx-background-color: " + UIUtils.getPriorityColor(task.getPriority()) + "; -fx-text-fill: white; -fx-padding: 2 8; -fx-border-radius: 2; -fx-font-size: 10px;");
        
        headerRow.getChildren().addAll(titleLabel, statusLabel, priorityLabel);
        HBox.setHgrow(titleLabel, javafx.scene.layout.Priority.ALWAYS);
        return headerRow;
    }

    private Label createListItemDescription(Task task) {
        Label descLabel = new Label(task.getDescription() != null ? task.getDescription() : "");
        descLabel.setStyle(LABEL_DESC);
        descLabel.setWrapText(true);
        return descLabel;
    }

    private HBox createListItemFooter(Task task) {
        HBox footerRow = new HBox(20);
        footerRow.setStyle("-fx-alignment: CENTER_LEFT;");
        
        String assignee = task.getAssignee() != null ? task.getAssignee().getUsername() : "Unassigned";
        Label assigneeLabel = new Label("👤" + assignee);
        assigneeLabel.setStyle(LABEL_DESC);
        
        String dueDate = task.getDueDate() != null ? task.getDueDate().toString() : "No due date";
        Label dueLabel = new Label("📅" + dueDate);
        dueLabel.setStyle(LABEL_DESC);
        
        footerRow.getChildren().addAll(assigneeLabel, dueLabel);
        return footerRow;
    }

    private void openTaskDetail(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/taskmanagement/fxml/main/TaskDetailView.fxml"));
            BorderPane root = loader.load();
            
            TaskDetailController controller = loader.getController();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Task Details - " + task.getTitle());
            dialogStage.setScene(new Scene(root, 700, 600));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            
            controller.setTask(task, dialogStage);
            
            dialogStage.setOnHidden(event -> {
                loadTasks();
                filterTasks();
            });
            
            dialogStage.showAndWait();
        } catch (IOException e) {
            showStatus("Error opening task details", true);
        }
    }

    private void showStatus(String message, boolean isError) {
        if (statusLabel != null) {
            if (isError) UIUtils.setErrorStyle(statusLabel, message);
            else UIUtils.setSuccessStyle(statusLabel, message);
        }
    }
}
