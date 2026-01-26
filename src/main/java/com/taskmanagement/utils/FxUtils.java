package com.taskmanagement.utils;

@Deprecated(since = "1.0.1", forRemoval = true)
public class FxUtils {

    @Deprecated(since = "1.0.1", forRemoval = true)
    public static void showInfo(String title, String message) {
        UIUtils.showSuccess(title, message);
    }
    @Deprecated(since = "1.0.1", forRemoval = true)
    public static void showError(String title, String message) {
        UIUtils.showError(title, message);
    }

    @Deprecated(since = "1.0.1", forRemoval = true)
    public static javafx.scene.Parent loadFXML(String fxmlPath) {
        return UIUtils.loadFXML(fxmlPath);
    }

    @Deprecated(since = "1.0.1", forRemoval = true)
    public static void switchScene(javafx.stage.Stage stage, String fxmlPath, String title) {
        UIUtils.switchScene(stage, fxmlPath, title);
    }
}
