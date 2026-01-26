package com.taskmanagement.utils;

@Deprecated(since = "1.0.1", forRemoval = true)

public class ConfirmationDialog {

    @Deprecated(since = "1.0.1", forRemoval = true)
    public static boolean showUnsavedChangesConfirmation() {
        return UIUtils.showUnsavedChangesConfirmation();
    }

    @Deprecated(since = "1.0.1", forRemoval = true)
    public static boolean showDeleteConfirmation(String itemName) {
        return UIUtils.showDeleteConfirmation(itemName);
    }

   @Deprecated(since = "1.0.1", forRemoval = true)
    public static boolean showDiscardChangesConfirmation() {
        return UIUtils.showDiscardChangesConfirmation();
    }
    @Deprecated(since = "1.0.1", forRemoval = true)
    public static boolean showCustomConfirmation(String title, String header, String content) {
        return UIUtils.showCustomConfirmation(title, header, content);
    }
    @Deprecated(since = "1.0.1", forRemoval = true)
    public static void showWarning(String title, String header, String content) {
        UIUtils.showWarning(title, header + " " + content);
    }
    @Deprecated(since = "1.0.1", forRemoval = true)
    public static void showError(String title, String header, String content) {
        UIUtils.showError(title, header + " " + content);
    }
    @Deprecated(since = "1.0.1", forRemoval = true)
    public static void showSuccess(String title, String header, String content) {
        UIUtils.showSuccess(title, header + " " + content);
    }
}
