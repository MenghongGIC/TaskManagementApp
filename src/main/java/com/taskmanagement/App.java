package com.taskmanagement;

import java.io.IOException;
import java.sql.Connection;

import com.taskmanagement.database.DBConnection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        scene = new Scene(loadFXML("auth/LoginView"), 1024, 768);
        stage.setScene(scene);
        stage.setTitle("Task Management System");
        stage.setResizable(true);
        
        // Set minimum size (prevents window from being too small)
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        
        // Set maximum size (prevents window from being too large)
        stage.setMaxWidth(1400);
        stage.setMaxHeight(1000);
        
        try {
            Image icon = new Image(getClass().getResourceAsStream("/com/taskmanagement/img/app-icon.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("Icon not found: " + e.getMessage());
        }
        
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        
        // Expand window for dashboard views (now wider for left sidebar + kanban board)
        if (fxml.startsWith("main/")) {
            primaryStage.setWidth(1600);
            primaryStage.setHeight(1000);
            primaryStage.setResizable(true);
        } else {
            // Keep medium size for auth views but resizable
            primaryStage.setWidth(800);
            primaryStage.setHeight(800);
            primaryStage.setResizable(true);
        }
    }
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/taskmanagement/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        System.out.println("=== Testing Database Connection ===");
        try {
            DBConnection dbConnection = DBConnection.getInstance();
            Connection connection = dbConnection.getConnection();
            
            if (connection != null && !connection.isClosed()) {
                System.out.println("✓ Database connection verified successfully!");
                System.out.println("✓ Application is ready to start.");
            } else {
                System.out.println("✗ Database connection failed: Connection is null or closed.");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("✗ Database connection test failed:");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        
        System.out.println("=== Starting Application ===\n");
        launch(args);
    }
}