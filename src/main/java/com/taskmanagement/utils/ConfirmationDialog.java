package com.taskmanagement.utils;

/**
 * @deprecated Use UIUtils instead - this class is kept for backward compatibility only
 */
@Deprecated(since = "1.0.1", forRemoval = true)
public class ConfirmationDialog {

    /**
     * @deprecated Use UIUtils.showUnsavedChangesConfirmation() instead
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public static boolean showUnsavedChangesConfirmation() {
        return UIUtils.showUnsavedChangesConfirmation();
    }

    /**
     * @deprecated Use UIUtils.showDeleteConfirmation() instead
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public static boolean showDeleteConfirmation(String itemName) {
        return UIUtils.showDeleteConfirmation(itemName);
    }

    /**
     * @deprecated Use UIUtils.showDiscardChangesConfirmation() instead
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public static boolean showDiscardChangesConfirmation() {
        return UIUtils.showDiscardChangesConfirmation();
    }

    /**
     * @deprecated Use UIUtils.showCustomConfirmation() instead
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public static boolean showCustomConfirmation(String title, String header, String content) {
        return UIUtils.showCustomConfirmation(title, header, content);
    }

    /**
     * @deprecated Use UIUtils.showWarning() instead
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public static void showWarning(String title, String header, String content) {
        UIUtils.showWarning(title, header + " " + content);
    }

    /**
     * @deprecated Use UIUtils.showError() instead
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public static void showError(String title, String header, String content) {
        UIUtils.showError(title, header + " " + content);
    }

    /**
     * @deprecated Use UIUtils.showSuccess() instead
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public static void showSuccess(String title, String header, String content) {
        UIUtils.showSuccess(title, header + " " + content);
    }
}
