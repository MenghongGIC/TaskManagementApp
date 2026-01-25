package com.taskmanagement;

import java.io.IOException;
import java.sql.Connection;

import com.taskmanagement.database.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        

        stage.setMinWidth(800);
        stage.setMinHeight(600);
        
        stage.setMaxWidth(1400);
        stage.setMaxHeight(1000);
        
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        try {
            System.out.println("DEBUG: Loading FXML: " + fxml);
            Parent root = loadFXML(fxml);
            System.out.println("DEBUG: FXML loaded successfully: " + fxml);
            scene.setRoot(root);
            System.out.println("DEBUG: Root set successfully");
            
            if (fxml.startsWith("main/")) {
                primaryStage.setWidth(1600);
                primaryStage.setHeight(1000);
                primaryStage.setResizable(true);
            } else {
                primaryStage.setWidth(800);
                primaryStage.setHeight(800);
                primaryStage.setResizable(true);
            }
            System.out.println("DEBUG: Stage resized");
        } catch (IOException e) {
            System.err.println("ERROR loading FXML " + fxml + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/taskmanagement/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        try {
            DBConnection dbConnection = DBConnection.getInstance();
            Connection connection = dbConnection.getConnection();
            
            if (connection != null && !connection.isClosed()) {
                System.out.println("✓ Database connection verified successfully!");
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