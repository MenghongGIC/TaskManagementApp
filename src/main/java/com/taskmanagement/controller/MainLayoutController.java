package com.taskmanagement.controller;

import java.io.IOException;
import com.taskmanagement.App;
import com.taskmanagement.utils.CurrentUser;
import com.taskmanagement.utils.HeroiconsIconManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainLayoutController {
    
    @FXML private StackPane contentArea;
    @FXML private Label userInfoLabel;
    @FXML private Label breadcrumbLabel;
    
    @FXML private Button dashboardBtn, projectsBtn, adminBtn;
    
    private static final String VIEW_DASHBOARD = "main/Dashboard";
    private static final String VIEW_PROJECTS = "main/ProjectListView";
    private static final String VIEW_TASKS = "main/TasksView";
    private static final String VIEW_ADMIN = "admin/AdminPanel";
    
    @FXML
    public void initialize() {
        System.out.println("🔧 Initializing MainLayoutController");
        
        // Update user info with icon
        String username = CurrentUser.getUsername();
        if (username != null) {
            userInfoLabel.setText(username);
            HeroiconsIconManager.setLabelIcon(userInfoLabel, HeroiconsIconManager.Icon.USER, 16);
        }
        
        // Setup button icons
        setupButtonIcons();
        
        // Add debugging for button clicks
        setupButtonDebug();
        
        // Load dashboard by default
        switchToDashboard();
        
        System.out.println("✅ MainLayoutController initialized");
    }
    
    /**
     * Setup button icons using Heroicons
     */
    private void setupButtonIcons() {
        HeroiconsIconManager.setButtonIcon(dashboardBtn, HeroiconsIconManager.Icon.DASHBOARD, 18);
        HeroiconsIconManager.setButtonIcon(projectsBtn, HeroiconsIconManager.Icon.FOLDER, 18);
        HeroiconsIconManager.setButtonIcon(adminBtn, HeroiconsIconManager.Icon.COG, 18);
    }
    
    /**
     * Setup button click debugging
     */
    private void setupButtonDebug() {
        dashboardBtn.setOnMousePressed(e -> System.out.println("🖱️ Dashboard button mouse pressed"));
        projectsBtn.setOnMousePressed(e -> System.out.println("🖱️ Projects button mouse pressed"));
        adminBtn.setOnMousePressed(e -> System.out.println("🖱️ Admin button mouse pressed"));
    }
    
    // ===== Navigation Methods =====
    
    @FXML
    private void switchToDashboard() {
        System.out.println("📊 Switching to Dashboard");
        breadcrumbLabel.setText("Dashboard");
        loadView("main/Dashboard");
        updateButtonStyles(dashboardBtn);
    }
    
    @FXML
    private void switchToProjects() {
        System.out.println("📁 Switching to Projects");
        breadcrumbLabel.setText("Projects");
        loadView("main/ProjectListView");
        updateButtonStyles(projectsBtn);
    }
    
    @FXML
    private void switchToAdminPanel() {
        System.out.println("👨‍💼 Switching to Admin Panel");
        breadcrumbLabel.setText("Administration");
        loadView("admin/AdminPanel");
        updateButtonStyles(adminBtn);
    }
    
    /**
     * Load a view into the content area
     */
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/taskmanagement/fxml/" + fxmlPath + ".fxml"));
            Parent view = loader.load();
            
            // Pass reference to MainLayoutController if the view needs it
            Object controller = loader.getController();
            if (controller instanceof TaskAwareController) {
                ((TaskAwareController) controller).setMainLayoutController(this);
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            System.out.println("✅ View loaded: " + fxmlPath);
            
        } catch (IOException e) {
            System.err.println("❌ Error loading view: " + fxmlPath);
            e.printStackTrace();
            contentArea.getChildren().clear();
            Label errorLabel = new Label("Error loading view: " + e.getMessage());
            contentArea.getChildren().add(errorLabel);
        }
    }
    
    /**
     * Update button styles to highlight active button
     */
    private void updateButtonStyles(Button activeButton) {
        // Reset all buttons
        dashboardBtn.setStyle("-fx-padding: 12 15; -fx-font-size: 12px; -fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        projectsBtn.setStyle("-fx-padding: 12 15; -fx-font-size: 12px; -fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        adminBtn.setStyle("-fx-padding: 12 15; -fx-font-size: 12px; -fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        
        // Highlight active button
        activeButton.setStyle("-fx-padding: 12 15; -fx-font-size: 12px; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
    }
    
    @FXML
    private void handleLogout() {
        System.out.println("🚪 Logging out...");
        CurrentUser.clear();
        try {
            App.setRoot("auth/LoginView");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * Interface for controllers that need access to MainLayoutController
 */
interface TaskAwareController {
    void setMainLayoutController(MainLayoutController controller);
}
