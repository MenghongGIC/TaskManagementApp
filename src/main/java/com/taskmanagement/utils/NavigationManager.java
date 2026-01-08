package com.taskmanagement.utils;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class NavigationManager {

    private static final String CSS_PATH = "/css/style.css";

    private NavigationManager() {
        // Utility class - prevent instantiation
    }

    /**
     * Core method: Load an FXML screen into the stage
     */
    public static void navigateTo(Stage stage, String fxmlPath, String windowTitle, boolean maximized) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Add global CSS if it exists
            String css = NavigationManager.class.getResource(CSS_PATH).toExternalForm();
            if (css != null) {
                scene.getStylesheets().add(css);
            }

            // Smooth transition
            stage.hide();
            stage.setScene(scene);
            stage.setTitle(windowTitle);
            stage.setMaximized(maximized);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlPath);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
    public static void navigateTo(Stage stage, String fxmlPath, String windowTitle) {
        navigateTo(stage, fxmlPath, windowTitle, true);
    }
    public static void goToDashboard(Stage stage) {
        navigateTo(stage, "/fxml/main/MainDashboard.fxml", "Task Manager - Dashboard", true);
    }
    public static void goToLogin(Stage stage) {
        navigateTo(stage, "/fxml/auth/AuthPanel.fxml", "Task Manager - Login", false);
    }
    public static void goToProfile(Stage stage) {
        navigateTo(stage, "/fxml/user/ProfilePanel.fxml", "Task Manager - Profile", true);
    }
    public static Stage getCurrentStage(javafx.stage.Window window) {
        return (window instanceof Stage) ? (Stage) window : null;
    }
}