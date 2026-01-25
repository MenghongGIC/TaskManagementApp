package com.taskmanagement.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.taskmanagement.App;
import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import com.taskmanagement.model.Project;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.service.ProjectService;
import com.taskmanagement.utils.CurrentUser;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;

public class DashboardController {

    @FXML private TextField taskNameField;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private Label statusLabel;
    @FXML private ComboBox projectComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private Label taskCountLabel;
    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    @FXML private Button clearSearchBtn;
    @FXML private Button tableViewBtn;
    @FXML private Button kanbanViewBtn;
    @FXML private Button listViewBtn;
    @FXML private VBox todoColumn;
    @FXML private VBox inProgressColumn;
    @FXML private VBox doneColumn;
    
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
    @FXML private VBox taskListContainer;

    private Task draggedTask = null;
    private final List<Task> allTasks = new ArrayList<>();
    private boolean isTableViewActive = false;

    @FXML
    public void initialize() {
        try {
            // Initialize priority combo box if it exists
            if (priorityCombo != null) {
                priorityCombo.getItems().addAll("Low", "Medium", "High");
                priorityCombo.setValue("Medium");
            }
            
            // Initialize project combo box
            if (projectComboBox != null) {
                loadProjectsIntoCombo();
                projectComboBox.setOnAction(event -> handleProjectSelection());
            }
            
            // Initialize status combo box
            if (statusComboBox != null) {
                statusComboBox.getItems().addAll("All", "To Do", "In Progress", "Done");
                statusComboBox.setValue("All");
                statusComboBox.setOnAction(event -> filterTasks());
            }
            
            // Initialize priority combo box for filtering
            if (priorityComboBox != null) {
                priorityComboBox.getItems().addAll("All", "Low", "Medium", "High");
                priorityComboBox.setValue("All");
                priorityComboBox.setOnAction(event -> filterTasks());
            }
            
            // Setup search field listener
            if (searchField != null) {
                searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filterTasks();
                    // Show clear button when there's text, hide when empty
                    if (clearSearchBtn != null) {
                        clearSearchBtn.setVisible(!newValue.isEmpty());
                    }
                });
            }
            
            // Setup profile sidebar
            if (profileButton != null || settingsButton != null || logoutButton != null) {
                setupProfileSidebar();
            }
            
            // Load tasks from database
            loadTasks();
            
            // Configure scroll pane behavior
            if (todoScrollPane != null) configureScrollPane(todoScrollPane);
            if (inProgressScrollPane != null) configureScrollPane(inProgressScrollPane);
            if (doneScrollPane != null) configureScrollPane(doneScrollPane);
            
            // Setup drag handlers for panels
            if (todoPanel != null && todoScrollPane != null) setupDragHandlers(todoPanel, todoScrollPane);
            if (inProgressPanel != null && inProgressScrollPane != null) setupDragHandlers(inProgressPanel, inProgressScrollPane);
            if (donePanel != null && doneScrollPane != null) setupDragHandlers(donePanel, doneScrollPane);
            
            // Setup table view with table as default
            if (tasksTableView != null) {
                setupTableColumns();
                isTableViewActive = true; // Start with table view active
            }
            
            // CRITICAL: Initialize StackPane view visibility
            // Ensure table view is visible by default
            if (viewStack != null) {
                if (viewStack.getChildren().size() > 0) {
                    // Show table view (index 0)
                    viewStack.getChildren().get(0).setVisible(true);
                }
                if (viewStack.getChildren().size() > 1) {
                    // Hide kanban view (index 1)
                    viewStack.getChildren().get(1).setVisible(false);
                }
                if (viewStack.getChildren().size() > 2) {
                    // Hide list view (index 2)
                    viewStack.getChildren().get(2).setVisible(false);
                }
            }
        } catch (Exception e) {
            System.err.println("Error initializing DashboardController: " + e.getMessage());
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
        
        // Add row double-click handler to open task detail
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
        
        // Check visibility of table and its parent
        
        String selectedProjectName = "All Projects";
        Object selectedProjectObj = projectComboBox != null ? projectComboBox.getValue() : null;
        if (selectedProjectObj instanceof Project) {
            selectedProjectName = ((Project)selectedProjectObj).getName();
        }
        final String finalProjectName = selectedProjectName;
        final String selectedStatus = statusComboBox != null ? statusComboBox.getValue() : "All";
        final String selectedPriority = priorityComboBox != null ? priorityComboBox.getValue() : "All";
        final String searchText = searchField != null ? searchField.getText().toLowerCase() : "";
        
        System.out.println("   Filtering by - Project: " + finalProjectName + ", Status: " + selectedStatus + ", Priority: " + selectedPriority);
        
        List<Task> filteredTasks = allTasks.stream()
            .filter(task -> {
                if (!"All Projects".equals(finalProjectName) && task.getProject() != null) {
                    if (!task.getProject().getName().equals(finalProjectName)) {
                        return false;
                    }
                }
                
                if (selectedStatus != null && !"All".equals(selectedStatus) && !selectedStatus.equals(task.getStatus())) {
                    return false;
                }
                
                if (selectedPriority != null && !"All".equals(selectedPriority) && !selectedPriority.equals(task.getPriority())) {
                    return false;
                }
                
                if (!searchText.isEmpty()) {
                    String title = task.getTitle() != null ? task.getTitle().toLowerCase() : "";
                    String description = task.getDescription() != null ? task.getDescription().toLowerCase() : "";
                    return title.contains(searchText) || description.contains(searchText);
                }
                
                return true;
            })
            .collect(Collectors.toList());
        
        ObservableList<Task> tableItems = FXCollections.observableArrayList(filteredTasks);
        tasksTableView.setItems(tableItems);
        
        if (taskCountLabel != null) {
            taskCountLabel.setText(filteredTasks.size() + " tasks");
        }
    }
    
    private void setupProfileSidebar() {
        try {
            if (usernameLabel != null) {
                String username = CurrentUser.getUsername();
                if (username != null && !username.isEmpty()) {
                    usernameLabel.setText(username);
                }
            }
            
            if (profileImageView != null) {
                String imagePath = "/com/taskmanagement/img/profile-default.png";
                try {
                    Image image = new Image(getClass().getResourceAsStream(imagePath));
                    profileImageView.setImage(image);
                } catch (NullPointerException e) {
                    System.out.println("Profile image resource not found at: " + imagePath);
                    System.out.println("You can add images to: src/main/resources/com/taskmanagement/img/");
                }
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
            // Only load into panels if they exist
            if (todoPanel != null) todoPanel.getChildren().clear();
            if (inProgressPanel != null) inProgressPanel.getChildren().clear();
            if (donePanel != null) donePanel.getChildren().clear();
            allTasks.clear();

            String selectedProjectName = "All Projects";
            Object selectedProjectObj = projectComboBox != null ? projectComboBox.getValue() : null;
            if (selectedProjectObj instanceof Project) {
                selectedProjectName = ((Project)selectedProjectObj).getName();
            }
            
            // Load tasks from database
            TaskRepository taskRepository = new TaskRepository();
            List<Task> dbTasks = taskRepository.findAll();
            
            // Filter tasks by selected project
            List<Task> filteredTasks = new ArrayList<>();
            if (dbTasks != null && !dbTasks.isEmpty()) {
                if ("All Projects".equals(selectedProjectName)) {
                    // Load all tasks
                    filteredTasks.addAll(dbTasks);
                } else {
                    // Load only tasks from selected project
                    for (Task task : dbTasks) {
                        if (task.getProject() != null && 
                            task.getProject().getName().equals(selectedProjectName)) {
                            filteredTasks.add(task);
                        }
                    }
                }
            }
            
            if (!filteredTasks.isEmpty()) {
                allTasks.addAll(filteredTasks);
                System.out.println("✓ Loaded " + filteredTasks.size() + " tasks for project: " + selectedProjectName);
                
                // Add each task to the appropriate panel (if panels exist)
                if (todoPanel != null || inProgressPanel != null || donePanel != null) {
                    for (Task task : filteredTasks) {
                        addTaskCard(task);
                    }
                }
            } else {
                System.out.println("ℹ No tasks found for project: " + selectedProjectName);
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
        // Only add card if we have a panel to add it to
        if (todoPanel == null && inProgressPanel == null && donePanel == null) {
            return;
        }
        
        HBox taskCard = createTaskCard(task);
        
        String status = task.getStatus() != null ? task.getStatus() : "To Do";
        if ("In Progress".equalsIgnoreCase(status) && inProgressPanel != null) {
            inProgressPanel.getChildren().add(taskCard);
        } else if ("Done".equalsIgnoreCase(status) && donePanel != null) {
            donePanel.getChildren().add(taskCard);
        } else if (todoPanel != null) {
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
        if (tasksTableView != null) {
            tasksTableView.getItems().clear();
            tasksTableView.getItems().addAll(allTasks);
        }
    }

    private void showStatus(String message, boolean isError) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
        } else {
            System.out.println((isError ? "ERROR: " : "STATUS: ") + message);
        }
    }

    @FXML
    private void showTableView() {
        System.out.println("📊 Switching to Table View");
        isTableViewActive = true;
        
        // Hide other views in the StackPane
        if (viewStack != null && viewStack.getChildren().size() > 1) {
            System.out.println("   StackPane has " + viewStack.getChildren().size() + " children");
            viewStack.getChildren().get(0).setVisible(true);   // Table - SHOULD BE FIRST
            System.out.println("   Child 0 (Table) visible: " + viewStack.getChildren().get(0).isVisible());
            
            viewStack.getChildren().get(1).setVisible(false);  // Kanban
            System.out.println("   Child 1 (Kanban) visible: " + viewStack.getChildren().get(1).isVisible());
            
            if (viewStack.getChildren().size() > 2) {
                viewStack.getChildren().get(2).setVisible(false);  // List
                System.out.println("   Child 2 (List) visible: " + viewStack.getChildren().get(2).isVisible());
            }
        }
        
        updateTableViewDisplay();
        updateButtonStyles(tableViewBtn);
        System.out.println("✅ Table View displayed");
    }

    @FXML
    private void showKanbanView() {
        System.out.println("📋 Switching to Kanban View");
        isTableViewActive = false;
        
        // Hide other views in the StackPane
        if (viewStack != null && viewStack.getChildren().size() > 1) {
            viewStack.getChildren().get(0).setVisible(false);  // Table
            viewStack.getChildren().get(1).setVisible(true);   // Kanban
            if (viewStack.getChildren().size() > 2) {
                viewStack.getChildren().get(2).setVisible(false);  // List
            }
        }
        
        filterTasks();
        updateButtonStyles(kanbanViewBtn);
        System.out.println("✅ Kanban View displayed");
    }

    @FXML
    private void showListView() {
        System.out.println("📝 Switching to List View");
        isTableViewActive = false;
        
        // Hide other views in the StackPane
        if (viewStack != null && viewStack.getChildren().size() > 2) {
            viewStack.getChildren().get(0).setVisible(false);  // Table
            viewStack.getChildren().get(1).setVisible(false);  // Kanban
            viewStack.getChildren().get(2).setVisible(true);   // List
        }
        
        filterTasks();
        updateButtonStyles(listViewBtn);
        System.out.println("✅ List View displayed");
    }

    @FXML
    private void showCreateTaskDialog() {
        System.out.println("➕ Opening Create Task Dialog");
        Object selectedProjectObj = projectComboBox != null ? projectComboBox.getValue() : null;
        
        if (selectedProjectObj == null || !(selectedProjectObj instanceof Project)) {
            showStatus("❌ Please select a project first", true);
            return;
        }
        
        Project selectedProject = (Project) selectedProjectObj;
        if ("All Projects".equals(selectedProject.getName())) {
            showStatus("❌ Please select a specific project", true);
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/taskmanagement/fxml/dialog/CreateTaskView.fxml"));
            BorderPane root = loader.load();
            
            // Get the controller and set the project
            CreateTaskController controller = loader.getController();
            controller.setCurrentProject(selectedProject);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create New Task");
            dialogStage.setScene(new Scene(root, 650, 700));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            
            // Set the dialog stage in the controller
            controller.setDialogStage(dialogStage);
            
            dialogStage.setOnHidden(event -> {
                System.out.println("Task dialog closed, refreshing tasks...");
                loadTasks();
                filterTasks();
            });
            
            dialogStage.showAndWait();
        } catch (Exception e) {
            System.err.println("Error opening create task dialog: " + e.getMessage());
            showStatus("Error opening task dialog: " + e.getMessage(), true);
        }
    }

    @FXML
    private void refreshTasks() {
        System.out.println("🔄 Refreshing tasks");
        try {
            loadTasks();
            filterTasks();
            showStatus("✅ Tasks refreshed", false);
        } catch (Exception e) {
            System.err.println("Error refreshing tasks: " + e.getMessage());
            showStatus("Error refreshing tasks", true);
        }
    }
    
    private void updateButtonStyles(Button activeButton) {
        String activeStyle = "-fx-padding: 8 12; -fx-font-size: 11px; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 4;";
        String inactiveStyle = "-fx-padding: 8 12; -fx-font-size: 11px; -fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 4;";
        
        if (tableViewBtn != null) tableViewBtn.setStyle(tableViewBtn == activeButton ? activeStyle : inactiveStyle);
        if (kanbanViewBtn != null) kanbanViewBtn.setStyle(kanbanViewBtn == activeButton ? activeStyle : inactiveStyle);
        if (listViewBtn != null) listViewBtn.setStyle(listViewBtn == activeButton ? activeStyle : inactiveStyle);
    }

    /**
     * Perform search when search button is clicked
     */
    @FXML
    private void performSearch() {
        String searchText = searchField != null ? searchField.getText().trim() : "";
        if (!searchText.isEmpty()) {
            System.out.println("Search: '" + searchText + "'");
            filterTasks();
            // If table view is active, also update table display
            if (isTableViewActive) {
                updateTableViewDisplay();
            }
        } else {
            System.out.println("Search field is empty");
        }
    }

    /**
     * Clear search field and display all tasks
     */
    @FXML
    private void clearSearch() {
        if (searchField != null) {
            searchField.clear();
            System.out.println("Search cleared - displaying all tasks");
            filterTasks();
            // If table view is active, also update table display
            if (isTableViewActive) {
                updateTableViewDisplay();
            }
        }
        // Hide clear button
        if (clearSearchBtn != null) {
            clearSearchBtn.setVisible(false);
        }
    }

    /**
     * Load all projects into the project combo box
     */
    private void loadProjectsIntoCombo() {
        try {
            System.out.println("📁 Loading projects into combo box...");
            
            if (projectComboBox == null) {
                System.err.println("❌ projectComboBox is null!");
                return;
            }
            
            ProjectService projectService = new ProjectService();
            List<Project> projects = projectService.getAllProjects();
            
            System.out.println("📊 Projects loaded from service: " + (projects != null ? projects.size() : 0));
            
            // Store project objects and create a special "All Projects" object
            ObservableList<Object> projectItems = FXCollections.observableArrayList();
            Project allProjectsItem = new Project();
            allProjectsItem.setName("All Projects");
            projectItems.add(allProjectsItem);
            
            if (projects != null && !projects.isEmpty()) {
                System.out.println("✓ Adding " + projects.size() + " projects to combo");
                projects.forEach(p -> {
                    projectItems.add(p);
                    System.out.println("  - " + p.getName());
                });
            } else {
                System.out.println("⚠️ No projects found in database");
            }
            
            projectComboBox.setItems((ObservableList)projectItems);
            projectComboBox.setConverter(new javafx.util.StringConverter<Object>() {
                @Override
                public String toString(Object obj) {
                    if (obj instanceof Project) {
                        return ((Project)obj).getName();
                    }
                    return "All Projects";
                }
                @Override
                public Object fromString(String string) {
                    return null;
                }
            });
            projectComboBox.setValue(allProjectsItem);
            System.out.println("✅ Project combo box loaded successfully with " + projectItems.size() + " items");
        } catch (Exception e) {
            System.err.println("❌ Error loading projects: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback: Add at least the "All Projects" option
            if (projectComboBox != null) {
                ObservableList<Object> fallback = FXCollections.observableArrayList();
                Project allProjects = new Project();
                allProjects.setName("All Projects");
                fallback.add(allProjects);
                projectComboBox.setItems(fallback);
                projectComboBox.setValue(allProjects);
            }
        }
    }

    /**
     * Handle project selection from combo box
     */
    private void handleProjectSelection() {
        Object selectedProjectObj = projectComboBox != null ? projectComboBox.getValue() : null;
        
        if (selectedProjectObj == null || !(selectedProjectObj instanceof Project)) {
            return;
        }
        
        Project selectedProject = (Project) selectedProjectObj;
        String projectName = selectedProject.getName();
        
        System.out.println("📂 Project selected: " + projectName);
        System.out.println("   isTableViewActive: " + isTableViewActive);
        System.out.println("   allTasks.size(): " + allTasks.size());
        
        if ("All Projects".equals(projectName)) {
            loadTasks();
        }
        
        // Update whichever view is currently active
        if (isTableViewActive) {
            System.out.println("   Calling updateTableViewDisplay()");
            updateTableViewDisplay();
        } else {
            System.out.println("   Calling filterTasks()");
            filterTasks();
        }
    }

    /**
     * Filter tasks based on search, status, and priority
     */
    private void filterTasks() {
        final String searchText = searchField != null ? searchField.getText().toLowerCase() : "";
        final String selectedStatus = statusComboBox != null ? statusComboBox.getValue() : "All";
        final String selectedPriority = priorityComboBox != null ? priorityComboBox.getValue() : "All";
        
        String selectedProjectName = "All Projects";
        Object selectedProjectObj = projectComboBox != null ? projectComboBox.getValue() : null;
        if (selectedProjectObj instanceof Project) {
            selectedProjectName = ((Project)selectedProjectObj).getName();
        }
        final String finalProjectName = selectedProjectName;
        
        // Debug logging
        if (!searchText.isEmpty()) {
            System.out.println("🔍 Searching for: '" + searchText + "'");
        } else {
            System.out.println("📋 Displaying all tasks (search cleared)");
        }
        
        // Clear all panels
        if (todoPanel != null) todoPanel.getChildren().clear();
        if (inProgressPanel != null) inProgressPanel.getChildren().clear();
        if (donePanel != null) donePanel.getChildren().clear();
        if (todoColumn != null) todoColumn.getChildren().clear();
        if (inProgressColumn != null) inProgressColumn.getChildren().clear();
        if (doneColumn != null) doneColumn.getChildren().clear();
        if (taskListContainer != null) taskListContainer.getChildren().clear();
        
        // Filter tasks
        List<Task> filteredTasks = allTasks.stream()
            .filter(task -> {
                // Filter by project
                if (!"All Projects".equals(finalProjectName) && task.getProject() != null) {
                    if (!task.getProject().getName().equals(finalProjectName)) {
                        return false;
                    }
                }
                
                // Filter by status
                if (!"All".equals(selectedStatus) && !selectedStatus.equals(task.getStatus())) {
                    return false;
                }
                
                // Filter by priority
                if (!"All".equals(selectedPriority) && !selectedPriority.equals(task.getPriority())) {
                    return false;
                }
                
                // Filter by search text using dedicated search method
                // If search is empty, show all tasks that pass other filters
                if (!searchText.isEmpty()) {
                    return matchesSearchCriteria(task, searchText);
                }
                
                return true;
            })
            .sorted((t1, t2) -> Long.compare(t1.getId(), t2.getId())) // Sort by ID in ascending order
            .collect(Collectors.toList());
        
        // Display filtered tasks
        for (Task task : filteredTasks) {
            // Add to kanban panels
            if ("To Do".equals(task.getStatus())) {
                if (todoPanel != null) {
                    HBox card = createTaskCard(task);
                    todoPanel.getChildren().add(card);
                }
                if (todoColumn != null) {
                    VBox kanbanCard = createKanbanCard(task);
                    todoColumn.getChildren().add(kanbanCard);
                }
            } else if ("In Progress".equals(task.getStatus())) {
                if (inProgressPanel != null) {
                    HBox card = createTaskCard(task);
                    inProgressPanel.getChildren().add(card);
                }
                if (inProgressColumn != null) {
                    VBox kanbanCard = createKanbanCard(task);
                    inProgressColumn.getChildren().add(kanbanCard);
                }
            } else if ("Done".equals(task.getStatus())) {
                if (donePanel != null) {
                    HBox card = createTaskCard(task);
                    donePanel.getChildren().add(card);
                }
                if (doneColumn != null) {
                    VBox kanbanCard = createKanbanCard(task);
                    doneColumn.getChildren().add(kanbanCard);
                }
            }
            
            // Add to list view
            if (taskListContainer != null) {
                VBox listItem = createListItem(task);
                taskListContainer.getChildren().add(listItem);
            }
        }
        
        // Update task count
        if (taskCountLabel != null) {
            taskCountLabel.setText(filteredTasks.size() + " tasks");
        }
        
        // Setup drop handlers for kanban columns (both ScrollPane and inner VBox)
        if (todoScrollPane != null && todoColumn != null) {
            setupColumnDropHandler(todoScrollPane, todoColumn, "To Do");
        }
        if (inProgressScrollPane != null && inProgressColumn != null) {
            setupColumnDropHandler(inProgressScrollPane, inProgressColumn, "In Progress");
        }
        if (doneScrollPane != null && doneColumn != null) {
            setupColumnDropHandler(doneScrollPane, doneColumn, "Done");
        }
    }
    
    /**
     * Search method to match tasks against search criteria
     * Searches in: title, description, and task ID
     */
    private boolean matchesSearchCriteria(Task task, String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return true;
        }
        
        // Search in task title
        String title = task.getTitle() != null ? task.getTitle().toLowerCase() : "";
        if (title.contains(searchText)) {
            return true;
        }
        
        // Search in task description
        String description = task.getDescription() != null ? task.getDescription().toLowerCase() : "";
        if (description.contains(searchText)) {
            return true;
        }
        
        // Search in task ID
        String taskId = String.valueOf(task.getId());
        if (taskId.contains(searchText)) {
            return true;
        }
        
        // Search in task status
        String status = task.getStatus() != null ? task.getStatus().toLowerCase() : "";
        if (status.contains(searchText)) {
            return true;
        }
        
        // Search in task priority
        String priority = task.getPriority() != null ? task.getPriority().toLowerCase() : "";
        if (priority.contains(searchText)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Advanced search method with multiple search options
     */
    public List<Task> searchTasks(String query, boolean searchTitle, boolean searchDescription, 
                                  boolean searchId, boolean searchStatus, boolean searchPriority) {
        if (query == null || query.isEmpty()) {
            return allTasks;
        }
        
        final String searchText = query.toLowerCase();
        return allTasks.stream()
            .filter(task -> {
                if (searchTitle) {
                    String title = task.getTitle() != null ? task.getTitle().toLowerCase() : "";
                    if (title.contains(searchText)) return true;
                }
                
                if (searchDescription) {
                    String description = task.getDescription() != null ? task.getDescription().toLowerCase() : "";
                    if (description.contains(searchText)) return true;
                }
                
                if (searchId) {
                    String taskId = String.valueOf(task.getId());
                    if (taskId.contains(searchText)) return true;
                }
                
                if (searchStatus) {
                    String status = task.getStatus() != null ? task.getStatus().toLowerCase() : "";
                    if (status.contains(searchText)) return true;
                }
                
                if (searchPriority) {
                    String priority = task.getPriority() != null ? task.getPriority().toLowerCase() : "";
                    if (priority.contains(searchText)) return true;
                }
                
                return false;
            })
            .sorted((t1, t2) -> Long.compare(t1.getId(), t2.getId()))
            .collect(Collectors.toList());
    }
    
    /**
     * Simple search method - searches all fields
     */
    public List<Task> searchTasks(String query) {
        return searchTasks(query, true, true, true, true, true);
    }
    
    /**
     * Setup drag and drop handlers for kanban columns
     */
    private void setupColumnDropHandler(ScrollPane scrollPane, VBox column, String status) {
        // Create a handler function for setting up drag handlers
        Runnable setupHandlers = () -> {
            // Handle drag over on the ScrollPane
            scrollPane.setOnDragOver(event -> {
                if (event.getGestureSource() != column && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    // Highlight column
                    highlightColumn(column, status, true);
                }
            });
            
            scrollPane.setOnDragExited(event -> {
                highlightColumn(column, status, false);
            });
            
            scrollPane.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    try {
                        Long taskId = Long.parseLong(db.getString());
                        if (draggedTask != null) {
                            String oldStatus = draggedTask.getStatus();
                            
                            // Only update if status actually changed
                            if (!oldStatus.equals(status)) {
                                // Update task status in database
                                draggedTask.setStatus(status);
                                TaskRepository taskRepository = new TaskRepository();
                                taskRepository.update(draggedTask);
                                
                                System.out.println("✅ Task #" + taskId + " (" + draggedTask.getTitle() + ") moved from '" + oldStatus + "' to '" + status + "'");
                                
                                // Refresh the view to show task in new column
                                filterTasks();
                                success = true;
                            } else {
                                System.out.println("ℹ️  Task #" + taskId + " dropped in same column, no change needed");
                                success = true;
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("❌ Error moving task: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                
                // Reset column style
                highlightColumn(column, status, false);
                event.setDropCompleted(success);
            });
            
            // Also add handlers to the inner VBox for drop zones between cards
            column.setOnDragOver(event -> {
                if (event.getGestureSource() != column && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    highlightColumn(column, status, true);
                }
            });
            
            column.setOnDragExited(event -> {
                highlightColumn(column, status, false);
            });
            
            column.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    try {
                        Long taskId = Long.parseLong(db.getString());
                        if (draggedTask != null) {
                            String oldStatus = draggedTask.getStatus();
                            
                            // Only update if status actually changed
                            if (!oldStatus.equals(status)) {
                                // Update task status in database
                                draggedTask.setStatus(status);
                                TaskRepository taskRepository = new TaskRepository();
                                taskRepository.update(draggedTask);
                                
                                System.out.println("✅ Task #" + taskId + " (" + draggedTask.getTitle() + ") moved from '" + oldStatus + "' to '" + status + "'");
                                
                                // Refresh the view to show task in new column
                                filterTasks();
                                success = true;
                            } else {
                                System.out.println("ℹ️  Task #" + taskId + " dropped in same column, no change needed");
                                success = true;
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("❌ Error moving task: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                
                // Reset column style
                highlightColumn(column, status, false);
                event.setDropCompleted(success);
            });
        };
        
        setupHandlers.run();
    }
    
    /**
     * Helper method to highlight/unhighlight a column
     */
    private void highlightColumn(VBox column, String status, boolean highlight) {
        if (highlight) {
            // Highlight on drag over
            column.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 3; -fx-border-radius: 5; -fx-background-color: #c8e6c9; -fx-padding: 5; -fx-opacity: 1.0;");
        } else {
            // Reset to original color based on status
            if ("To Do".equals(status)) {
                column.setStyle("-fx-padding: 5; -fx-fill-width: true;");
            } else if ("In Progress".equals(status)) {
                column.setStyle("-fx-padding: 5; -fx-fill-width: true;");
            } else if ("Done".equals(status)) {
                column.setStyle("-fx-padding: 5; -fx-fill-width: true;");
            }
        }
    }

    /**
     * Create a kanban-style card for tasks
     */
    private VBox createKanbanCard(Task task) {
        VBox card = new VBox(8);
        card.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 6; "
                    + "-fx-padding: 12; -fx-background-color: #ffffff; -fx-min-width: 220; "
                    + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");
        card.setUserData(task); // Store task data for drag and drop
        
        // Title
        Label titleLabel = new Label(task.getTitle() != null ? task.getTitle() : "No Title");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2c3e50; "
                          + "-fx-wrap-text: true;");
        titleLabel.setWrapText(true);
        
        // Description
        Label descriptionLabel = new Label(task.getDescription() != null ? task.getDescription() : "");
        descriptionLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d; -fx-wrap-text: true;");
        descriptionLabel.setWrapText(true);
        if (task.getDescription() == null || task.getDescription().isEmpty()) {
            descriptionLabel.setVisible(false);
        }
        
        // Priority badge
        String priorityColor = getPriorityColor(task.getPriority());
        Label priorityLabel = new Label(task.getPriority() != null ? task.getPriority() : "Low");
        priorityLabel.setStyle("-fx-background-color: " + priorityColor + "; -fx-text-fill: white; "
                             + "-fx-padding: 3 8; -fx-border-radius: 3; -fx-font-size: 9px; "
                             + "-fx-font-weight: bold;");
        
        card.getChildren().addAll(titleLabel, descriptionLabel, priorityLabel);
        
        // Add double-click handler to open task detail
        card.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                openTaskDetail(task);
            }
        });
        
        // Change cursor to hand on hover
        card.setStyle(card.getStyle() + "; -fx-cursor: hand;");
        
        // Add drag and drop support
        setupTaskCardDragDrop(card, task);
        
        return card;
    }
    
    /**
     * Setup drag and drop for task cards
     */
    private void setupTaskCardDragDrop(VBox card, Task task) {
        // Make card draggable
        card.setOnDragDetected(event -> {
            Dragboard db = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(task.getId()));
            db.setContent(content);
            draggedTask = task;
            
            // Visual feedback - make card semi-transparent while dragging
            card.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 6; "
                        + "-fx-padding: 12; -fx-background-color: #ffffff; -fx-opacity: 0.6; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 4);");
            
            System.out.println("🎯 Started dragging Task #" + task.getId() + ": " + task.getTitle());
        });
        
        // Style on drag entered (when hovering over another card)
        card.setOnDragEntered(event -> {
            if (event.getGestureSource() != card && event.getDragboard().hasString()) {
                card.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 3; -fx-border-radius: 6; "
                            + "-fx-padding: 12; -fx-background-color: #f0fdf4; "
                            + "-fx-effect: dropshadow(gaussian, rgba(46,204,113,0.3), 8, 0, 0, 4);");
            }
        });
        
        // Reset style when drag exits the card
        card.setOnDragExited(event -> {
            String priorityColor = getPriorityColor(task.getPriority());
            card.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 6; "
                        + "-fx-padding: 12; -fx-background-color: #ffffff; -fx-opacity: 1.0; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");
        });
        
        // Complete drag - reset style
        card.setOnDragDone(event -> {
            String priorityColor = getPriorityColor(task.getPriority());
            card.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 6; "
                        + "-fx-padding: 12; -fx-background-color: #ffffff; -fx-opacity: 1.0; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");
        });
    }

    /**
     * Get color for priority level
     */
    private String getPriorityColor(String priority) {
        if (priority == null) return "#95a5a6";
        switch (priority) {
            case "High": return "#e74c3c";
            case "Medium": return "#f39c12";
            case "Low": return "#27ae60";
            default: return "#95a5a6";
        }
    }

    /**
     * Create a list view item for tasks
     */
    private VBox createListItem(Task task) {
        VBox item = new VBox(5);
        item.setStyle("-fx-border-color: #ecf0f1; -fx-border-width: 0 0 1 0; -fx-padding: 12; "
                    + "-fx-background-color: #ffffff;");
        
        // Title and status row
        HBox headerRow = new HBox(10);
        headerRow.setStyle("-fx-alignment: CENTER_LEFT;");
        
        Label titleLabel = new Label(task.getTitle() != null ? task.getTitle() : "No Title");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2c3e50;");
        
        Label statusLabel = new Label(task.getStatus() != null ? task.getStatus() : "To Do");
        String statusColor = getStatusColor(task.getStatus());
        statusLabel.setStyle("-fx-background-color: " + statusColor + "; -fx-text-fill: white; "
                           + "-fx-padding: 2 8; -fx-border-radius: 2; -fx-font-size: 10px;");
        
        Label priorityLabel = new Label(task.getPriority() != null ? task.getPriority() : "Low");
        String priorityColor = getPriorityColor(task.getPriority());
        priorityLabel.setStyle("-fx-background-color: " + priorityColor + "; -fx-text-fill: white; "
                             + "-fx-padding: 2 8; -fx-border-radius: 2; -fx-font-size: 10px;");
        
        headerRow.getChildren().addAll(titleLabel, statusLabel, priorityLabel);
        
        // Description
        Label descLabel = new Label(task.getDescription() != null ? task.getDescription() : "");
        descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        descLabel.setWrapText(true);
        
        // Assignee and due date row
        HBox footerRow = new HBox(20);
        footerRow.setStyle("-fx-alignment: CENTER_LEFT;");
        
        String assignee = task.getAssignee() != null ? task.getAssignee().getUsername() : "Unassigned";
        Label assigneeLabel = new Label("👤 " + assignee);
        assigneeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
        
        String dueDate = task.getDueDate() != null ? task.getDueDate().toString() : "No due date";
        Label dueLabel = new Label("📅 " + dueDate);
        dueLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
        
        footerRow.getChildren().addAll(assigneeLabel, dueLabel);
        
        item.getChildren().addAll(headerRow, descLabel, footerRow);
        
        // Add double-click handler to open task detail
        item.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                openTaskDetail(task);
            }
        });
        
        // Change cursor to hand on hover
        item.setStyle(item.getStyle() + "; -fx-cursor: hand;");
        
        return item;
    }

    /**
     * Open task detail dialog for viewing/editing/deleting a task
     */
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
                System.out.println("Task detail closed, refreshing tasks...");
                loadTasks();
                filterTasks();
            });
            
            dialogStage.showAndWait();
        } catch (IOException e) {
            System.err.println("❌ Error opening task detail dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get color for status level
     */
    private String getStatusColor(String status) {
        if (status == null) return "#95a5a6";
        switch (status) {
            case "To Do": return "#3498db";
            case "In Progress": return "#f39c12";
            case "Done": return "#27ae60";
            default: return "#95a5a6";
        }
    }
}

