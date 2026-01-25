package com.taskmanagement.utils;

/**
 * @deprecated Use UIUtils instead - this class is kept for backward compatibility only
 */
@Deprecated(since = "1.0.1", forRemoval = true)
public class FxUtils {

    /**
     * @deprecated Use UIUtils.showSuccess() instead
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public static void showInfo(String title, String message) {
        UIUtils.showSuccess(title, message);
    }

    /**
     * @deprecated Use UIUtils.showError() instead
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public static void showError(String title, String message) {
        UIUtils.showError(title, message);
    }

    /**
     * @deprecated Use UIUtils.loadFXML() instead
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public static javafx.scene.Parent loadFXML(String fxmlPath) {
        return UIUtils.loadFXML(fxmlPath);
    }

    /**
     * @deprecated Use UIUtils.switchScene() instead
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public static void switchScene(javafx.stage.Stage stage, String fxmlPath, String title) {
        UIUtils.switchScene(stage, fxmlPath, title);
    }
}
