package com.taskmanagement.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global exception handler and error reporting utility.
 * Provides centralized error handling with logging and UI notification capabilities.
 * Supports multiple error types (validation, security, database, etc.) with semantic error messages.
 */
public class ErrorHandler {

    private static final Logger logger = Logger.getLogger(ErrorHandler.class.getName());
    private static ErrorCallback errorCallback;

    // Error Type Constants
    private static final String ERR_VALIDATION = "Validation Error";
    private static final String ERR_SECURITY = "Security Error";
    private static final String ERR_DATABASE = "Database Error";
    private static final String ERR_RUNTIME = "Error";
    private static final String ERR_AUTHENTICATION = "Authentication Failed";
    private static final String ERR_AUTHORIZATION = "Access Denied";
    private static final String ERR_FILE = "File Error";

    // Error Message Templates
    private static final String MSG_DB_FAILED = "Failed to access database: ";
    private static final String MSG_SAFE_EXEC_FAILED = "Safe execution failed: ";

    /**
     * Functional interface for error callbacks (UI notifications).
     * Allows custom error display implementations in the UI layer.
     */
    @FunctionalInterface
    public interface ErrorCallback {
        /**
         * Called when an error needs to be displayed to the user.
         *
         * @param title the error title/type
         * @param message the error message
         */
        void onError(String title, String message);
    }

    // ============ Callback Management ============

    /**
     * Set a callback for handling errors in UI.
     *
     * @param callback the error callback implementation
     */
    public static void setErrorCallback(ErrorCallback callback) {
        errorCallback = callback;
    }

    // ============ Error Type Handlers ============

    /**
     * Handle validation error - typically for input validation failures.
     *
     * @param message the validation error message
     */
    public static void handleValidationError(String message) {
        log(Level.WARNING, "Validation Error: " + message);
        notifyError(ERR_VALIDATION, message);
    }

    /**
     * Handle security error - for security-related failures.
     *
     * @param message the security error message
     */
    public static void handleSecurityError(String message) {
        log(Level.WARNING, "Security Error: " + message);
        notifyError(ERR_SECURITY, message);
    }

    /**
     * Handle database error - for SQL/database operation failures.
     *
     * @param message the database error message
     * @param e the exception that occurred
     */
    public static void handleDatabaseError(String message, Exception e) {
        log(Level.SEVERE, "Database Error: " + message, e);
        notifyError(ERR_DATABASE, MSG_DB_FAILED + message);
    }

    /**
     * Handle runtime error - for unexpected runtime exceptions.
     *
     * @param message the error message
     * @param e the exception that occurred
     */
    public static void handleRuntimeError(String message, Exception e) {
        log(Level.SEVERE, "Runtime Error: " + message, e);
        notifyError(ERR_RUNTIME, message);
    }

    /**
     * Handle authentication error - for login/authentication failures.
     *
     * @param message the authentication error message
     */
    public static void handleAuthenticationError(String message) {
        log(Level.WARNING, "Authentication Error: " + message);
        notifyError(ERR_AUTHENTICATION, message);
    }

    /**
     * Handle authorization error - for permission/access failures.
     *
     * @param message the authorization error message
     */
    public static void handleAuthorizationError(String message) {
        log(Level.WARNING, "Authorization Error: " + message);
        notifyError(ERR_AUTHORIZATION, message);
    }

    /**
     * Handle file operation error - for file I/O failures.
     *
     * @param message the file error message
     * @param e the exception that occurred
     */
    public static void handleFileError(String message, Exception e) {
        log(Level.SEVERE, "File Error: " + message, e);
        notifyError(ERR_FILE, message);
    }

    // ============ Logging Methods ============

    /**
     * Log a message at the specified level.
     *
     * @param level the log level
     * @param message the log message
     */
    public static void log(Level level, String message) {
        logger.log(level, message);
    }

    /**
     * Log a message at the specified level with an exception.
     *
     * @param level the log level
     * @param message the log message
     * @param thrown the exception to log
     */
    public static void log(Level level, String message, Throwable thrown) {
        logger.log(level, message, thrown);
    }

    /**
     * Log an info-level message.
     *
     * @param message the info message
     */
    public static void logInfo(String message) {
        logger.info(message);
    }

    /**
     * Log a warning-level message.
     *
     * @param message the warning message
     */
    public static void logWarning(String message) {
        logger.warning(message);
    }

    /**
     * Log an error-level message.
     *
     * @param message the error message
     */
    public static void logError(String message) {
        logger.severe(message);
    }

    // ============ Exception Analysis ============

    /**
     * Get error details map for logging and debugging.
     *
     * @param e the exception to analyze
     * @return map containing exception details (class, message, cause, file, method, line)
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
     * Format exception for display purposes.
     *
     * @param e the exception to format
     * @return formatted exception string with class, message, and location
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
     * Check if an error is critical (should stop application).
     *
     * @param e the exception to check
     * @return true if the error is critical (OutOfMemoryError, StackOverflowError, or NoClassDefFoundError)
     */
    public static boolean isCritical(Throwable e) {
        return e instanceof OutOfMemoryError ||
               e instanceof StackOverflowError ||
               e instanceof NoClassDefFoundError;
    }

    // ============ Safe Execution Methods ============

    /**
     * Safely execute a function with exception handling.
     *
     * @param function the function to execute
     * @param defaultValue the value to return if execution fails
     * @param <T> the return type
     * @return the result of the function or defaultValue if exception occurs
     */
    public static <T> T safeExecute(SafeFunction<T> function, T defaultValue) {
        try {
            return function.execute();
        } catch (Exception e) {
            logError(MSG_SAFE_EXEC_FAILED + e.getMessage());
            return defaultValue;
        }
    }

    /**
     * Safely execute a runnable with exception handling.
     *
     * @param runnable the runnable to execute
     */
    public static void safeExecute(SafeRunnable runnable) {
        try {
            runnable.execute();
        } catch (Exception e) {
            logError(MSG_SAFE_EXEC_FAILED + e.getMessage());
        }
    }

    // ============ Private Helper Methods ============

    /**
     * Notify error through callback if set.
     *
     * @param title the error title
     * @param message the error message
     */
    private static void notifyError(String title, String message) {
        if (errorCallback != null) {
            errorCallback.onError(title, message);
        }
    }

    // ============ Functional Interfaces ============

    /**
     * Functional interface for safe function execution.
     * Used with safeExecute() to wrap functions with exception handling.
     *
     * @param <T> the return type
     */
    @FunctionalInterface
    public interface SafeFunction<T> {
        /**
         * Execute a function that may throw an exception.
         *
         * @return the result
         * @throws Exception if an error occurs during execution
         */
        T execute() throws Exception;
    }

    /**
     * Functional interface for safe runnable execution.
     * Used with safeExecute() to wrap runnables with exception handling.
     */
    @FunctionalInterface
    public interface SafeRunnable {
        /**
         * Execute a runnable that may throw an exception.
         *
         * @throws Exception if an error occurs during execution
         */
        void execute() throws Exception;
    }
}
