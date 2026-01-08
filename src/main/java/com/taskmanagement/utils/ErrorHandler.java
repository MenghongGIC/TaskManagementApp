package com.taskmanagement.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global exception handler and error reporting utility
 */
public class ErrorHandler {

    private static final Logger logger = Logger.getLogger(ErrorHandler.class.getName());
    private static ErrorCallback errorCallback;

    /**
     * Functional interface for error callbacks (UI notifications)
     */
    @FunctionalInterface
    public interface ErrorCallback {
        void onError(String title, String message);
    }

    /**
     * Set a callback for handling errors in UI
     */
    public static void setErrorCallback(ErrorCallback callback) {
        errorCallback = callback;
    }

    /**
     * Handle validation error
     */
    public static void handleValidationError(String message) {
        log(Level.WARNING, "Validation Error: " + message);
        notifyError("Validation Error", message);
    }

    /**
     * Handle security error
     */
    public static void handleSecurityError(String message) {
        log(Level.WARNING, "Security Error: " + message);
        notifyError("Security Error", message);
    }

    /**
     * Handle database error
     */
    public static void handleDatabaseError(String message, Exception e) {
        log(Level.SEVERE, "Database Error: " + message, e);
        notifyError("Database Error", "Failed to access database: " + message);
    }

    /**
     * Handle runtime error
     */
    public static void handleRuntimeError(String message, Exception e) {
        log(Level.SEVERE, "Runtime Error: " + message, e);
        notifyError("Error", message);
    }

    /**
     * Handle authentication error
     */
    public static void handleAuthenticationError(String message) {
        log(Level.WARNING, "Authentication Error: " + message);
        notifyError("Authentication Failed", message);
    }

    /**
     * Handle authorization error
     */
    public static void handleAuthorizationError(String message) {
        log(Level.WARNING, "Authorization Error: " + message);
        notifyError("Access Denied", message);
    }

    /**
     * Handle file operation error
     */
    public static void handleFileError(String message, Exception e) {
        log(Level.SEVERE, "File Error: " + message, e);
        notifyError("File Error", message);
    }

    /**
     * Log with level
     */
    public static void log(Level level, String message) {
        logger.log(level, message);
    }

    /**
     * Log with level and exception
     */
    public static void log(Level level, String message, Throwable thrown) {
        logger.log(level, message, thrown);
    }

    /**
     * Log info message
     */
    public static void logInfo(String message) {
        logger.info(message);
    }

    /**
     * Log warning message
     */
    public static void logWarning(String message) {
        logger.warning(message);
    }

    /**
     * Log error message
     */
    public static void logError(String message) {
        logger.severe(message);
    }

    /**
     * Notify error through callback
     */
    private static void notifyError(String title, String message) {
        if (errorCallback != null) {
            errorCallback.onError(title, message);
        }
    }

    /**
     * Get error details map for logging
     */
    public static Map<String, String> getErrorDetails(Exception e) {
        Map<String, String> details = new HashMap<>();
        details.put("exception_class", e.getClass().getName());
        details.put("message", e.getMessage() != null ? e.getMessage() : "No message");
        details.put("cause", e.getCause() != null ? e.getCause().toString() : "No cause");
        if (e.getStackTrace().length > 0) {
            StackTraceElement ste = e.getStackTrace()[0];
            details.put("file", ste.getFileName());
            details.put("method", ste.getMethodName());
            details.put("line", String.valueOf(ste.getLineNumber()));
        }
        return details;
    }

    /**
     * Format exception for display
     */
    public static String formatException(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Error: ").append(e.getClass().getSimpleName()).append("\n");
        sb.append("Message: ").append(e.getMessage()).append("\n");

        if (e.getStackTrace().length > 0) {
            StackTraceElement ste = e.getStackTrace()[0];
            sb.append("Location: ").append(ste.getFileName())
                    .append(" (line ").append(ste.getLineNumber()).append(")");
        }

        return sb.toString();
    }

    /**
     * Check if error is critical (should stop application)
     */
    public static boolean isCritical(Throwable e) {
        return e instanceof OutOfMemoryError ||
               e instanceof StackOverflowError ||
               e instanceof NoClassDefFoundError;
    }

    /**
     * Safe execution of a function with error handling
     */
    public static <T> T safeExecute(SafeFunction<T> function, T defaultValue) {
        try {
            return function.execute();
        } catch (Exception e) {
            logError("Safe execution failed: " + e.getMessage());
            return defaultValue;
        }
    }

    /**
     * Safe execution of a runnable with error handling
     */
    public static void safeExecute(SafeRunnable runnable) {
        try {
            runnable.execute();
        } catch (Exception e) {
            logError("Safe execution failed: " + e.getMessage());
        }
    }

    /**
     * Functional interface for safe function execution
     */
    @FunctionalInterface
    public interface SafeFunction<T> {
        T execute() throws Exception;
    }

    /**
     * Functional interface for safe runnable execution
     */
    @FunctionalInterface
    public interface SafeRunnable {
        void execute() throws Exception;
    }
}
