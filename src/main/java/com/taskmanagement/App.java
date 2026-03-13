package com.taskmanagement;

import java.io.IOException;
import java.sql.Connection;

import com.taskmanagement.database.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        preloadIkonliFonts();

        primaryStage = stage;
        scene = new Scene(loadFXML("auth/LoginView"), 1024, 768);
        scene.getStylesheets().addAll(
                getClass().getResource("/com/taskmanagement/css/style.css").toExternalForm(),
                getClass().getResource("/com/taskmanagement/css/style_enhancements.css").toExternalForm()
        );
        stage.setScene(scene);
        stage.setTitle("Task Management System");
        stage.setResizable(true);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setMaxWidth(1600);
        stage.setMaxHeight(1000);
        
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        try {
            Parent root = loadFXML(fxml);
            scene.setRoot(root);
            
            if (fxml.startsWith("main/")) {
                primaryStage.setWidth(1600);
                primaryStage.setHeight(1000);
            } else {
                primaryStage.setWidth(800);
                primaryStage.setHeight(800);
            }
            primaryStage.setResizable(true);
        } catch (IOException e) {
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

    private static void preloadIkonliFonts() {
        String[] fontPaths = {
            "/META-INF/resources/fontawesome5/5.15.3/fonts/fa-solid-900.ttf",
            "/META-INF/resources/fontawesome5/5.15.3/fonts/fa-regular-400.ttf",
            "/META-INF/resources/fontawesome5/5.15.3/fonts/fa-brands-400.ttf"
        };
        for (String path : fontPaths) {
            java.net.URL url = FontAwesomeSolid.class.getResource(path);
            if (url != null) {
                Font font = Font.loadFont(url.toExternalForm(), 16);
                System.out.println("[Ikonli] " + (font != null ? "Loaded: " + font.getFamily() : "FAILED to load") + " from " + path);
            } else {
                System.err.println("[Ikonli] URL null for: " + path);
            }
        }
    }

    public static void main(String[] args) {
        try {
            DBConnection dbConnection = DBConnection.getInstance();
            Connection connection = dbConnection.getConnection();
            if (connection == null || connection.isClosed()) {
                System.exit(1);
            }
        } catch (Exception e) {
            System.exit(1);
        }
        launch(args);
    }
}