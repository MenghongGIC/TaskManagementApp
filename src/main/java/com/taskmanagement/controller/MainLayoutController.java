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
    private static final String FXML_BASE_PATH = "/com/taskmanagement/fxml/";
    private static final String LOGIN_VIEW = "auth/LoginView";

    private static final String INACTIVE_BTN_STYLE = 
        "-fx-padding: 12 15; -fx-font-size: 12px; -fx-background-color: #95a5a6; " +
        "-fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;";
    private static final String ACTIVE_BTN_STYLE = 
        "-fx-padding: 12 15; -fx-font-size: 12px; -fx-background-color: #3498db; " +
        "-fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;";
    
    private enum NavView {
        DASHBOARD("main/Dashboard", "Dashboard"),
        PROJECTS("main/ProjectListView", "Projects"),
        ADMIN("admin/AdminPanel", "Administration");
        
        private final String path;
        private final String label;
        
        NavView(String path, String label) {
            this.path = path;
            this.label = label;
        }
        
        public String getPath() { return path; }
        public String getLabel() { return label; }
    }
    
    @FXML private StackPane contentArea;
    @FXML private Label userInfoLabel;
    @FXML private Label breadcrumbLabel;
    @FXML private Button dashboardBtn, projectsBtn, adminBtn;
    private Button currentActiveButton;
    
    @FXML
    public void initialize() {
        System.out.println("Initializing MainLayoutController");
        setupUserInfo();
        setupButtonIcons();
        navigateTo(NavView.DASHBOARD);
        System.out.println("✅ MainLayoutController initialized");
    }
    
    private void setupUserInfo() {
        String username = CurrentUser.getUsername();
        if (username != null) {
            userInfoLabel.setText(username);
            HeroiconsIconManager.setLabelIcon(userInfoLabel, HeroiconsIconManager.Icon.USER, 16);
        }
    }
    
    private void setupButtonIcons() {
        HeroiconsIconManager.setButtonIcon(dashboardBtn, HeroiconsIconManager.Icon.DASHBOARD, 18);
        HeroiconsIconManager.setButtonIcon(projectsBtn, HeroiconsIconManager.Icon.FOLDER, 18);
        HeroiconsIconManager.setButtonIcon(adminBtn, HeroiconsIconManager.Icon.COG, 18);
    }
    
    @FXML
    private void switchToDashboard() {
        navigateTo(NavView.DASHBOARD, dashboardBtn);
    }
    
    @FXML
    private void switchToProjects() {
        navigateTo(NavView.PROJECTS, projectsBtn);
    }
    
    @FXML
    private void switchToAdminPanel() {
        navigateTo(NavView.ADMIN, adminBtn);
    }
    
    private void navigateTo(NavView view) {
        navigateTo(view, getButtonForView(view));
    }
    
    private void navigateTo(NavView view, Button activeButton) {
        System.out.println("📌 Switching to " + view.getLabel());
        breadcrumbLabel.setText(view.getLabel());
        loadView(view.getPath());
        updateButtonStyles(activeButton);
    }
    
    private Button getButtonForView(NavView view) {
        return switch (view) {
            case DASHBOARD -> dashboardBtn;
            case PROJECTS -> projectsBtn;
            case ADMIN -> adminBtn;
        };
    }
    
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_BASE_PATH + fxmlPath + ".fxml"));
            Parent view = loader.load();
            
            Object controller = loader.getController();
            if (controller instanceof TaskAwareController) {
                ((TaskAwareController) controller).setMainLayoutController(this);
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            System.out.println("View loaded: " + fxmlPath);
            
        } catch (IOException e) {
            System.err.println("Error loading view: " + fxmlPath);
            showErrorView("Error loading view: " + e.getMessage());
        }
    }
    
    private void showErrorView(String errorMessage) {
        contentArea.getChildren().clear();
        Label errorLabel = new Label(errorMessage);
        errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px;");
        contentArea.getChildren().add(errorLabel);
    }
    
    private void updateButtonStyles(Button activeButton) {
        dashboardBtn.setStyle(INACTIVE_BTN_STYLE);
        projectsBtn.setStyle(INACTIVE_BTN_STYLE);
        adminBtn.setStyle(INACTIVE_BTN_STYLE);
        activeButton.setStyle(ACTIVE_BTN_STYLE);
        currentActiveButton = activeButton;
    }
    
    @FXML
    private void handleLogout() {
        System.out.println("🚪 Logging out...");
        CurrentUser.clear();
        try {
            App.setRoot(LOGIN_VIEW);
        } catch (IOException e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
interface TaskAwareController {
    void setMainLayoutController(MainLayoutController controller);
}
