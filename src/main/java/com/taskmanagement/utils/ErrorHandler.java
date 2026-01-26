package com.taskmanagement.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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

  
    @FunctionalInterface
    public interface ErrorCallback {
        void onError(String title, String message);
    }

    public static void setErrorCallback(ErrorCallback callback) {
        errorCallback = callback;
    }
    public static void handleValidationError(String message) {
        log(Level.WARNING, "Validation Error: " + message);
        notifyError(ERR_VALIDATION, message);
    }

    public static void handleSecurityError(String message) {
        log(Level.WARNING, "Security Error: " + message);
        notifyError(ERR_SECURITY, message);
    }

    public static void handleDatabaseError(String message, Exception e) {
        log(Level.SEVERE, "Database Error: " + message, e);
        notifyError(ERR_DATABASE, MSG_DB_FAILED + message);
    }

    public static void handleRuntimeError(String message, Exception e) {
        log(Level.SEVERE, "Runtime Error: " + message, e);
        notifyError(ERR_RUNTIME, message);
    }

    public static void handleAuthenticationError(String message) {
        log(Level.WARNING, "Authentication Error: " + message);
        notifyError(ERR_AUTHENTICATION, message);
    }

    public static void handleAuthorizationError(String message) {
        log(Level.WARNING, "Authorization Error: " + message);
        notifyError(ERR_AUTHORIZATION, message);
    }

    public static void handleFileError(String message, Exception e) {
        log(Level.SEVERE, "File Error: " + message, e);
        notifyError(ERR_FILE, message);
    }

    public static void log(Level level, String message) {
        logger.log(level, message);
    }

    public static void log(Level level, String message, Throwable thrown) {
        logger.log(level, message, thrown);
    }

    public static void logInfo(String message) {
        logger.info(message);
    }

    public static void logWarning(String message) {
        logger.warning(message);
    }

    public static void logError(String message) {
        logger.severe(message);
    }

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

    public static boolean isCritical(Throwable e) {
        return e instanceof OutOfMemoryError ||
               e instanceof StackOverflowError ||
               e instanceof NoClassDefFoundError;
    }

    public static <T> T safeExecute(SafeFunction<T> function, T defaultValue) {
        try {
            return function.execute();
        } catch (Exception e) {
            logError(MSG_SAFE_EXEC_FAILED + e.getMessage());
            return defaultValue;
        }
    }

    public static void safeExecute(SafeRunnable runnable) {
        try {
            runnable.execute();
        } catch (Exception e) {
            logError(MSG_SAFE_EXEC_FAILED + e.getMessage());
        }
    }

    private static void notifyError(String title, String message) {
        if (errorCallback != null) {
            errorCallback.onError(title, message);
        }
    }

    @FunctionalInterface
    public interface SafeFunction<T> {
        T execute() throws Exception;
    }
    @FunctionalInterface
    public interface SafeRunnable {
        void execute() throws Exception;
    }
}
