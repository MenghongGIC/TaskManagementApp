package com.taskmanagement.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration loader that reads from application.properties file
 */
public class ConfigLoader {

    private static ConfigLoader instance;
    private final Properties properties;

    private ConfigLoader() {
        properties = new Properties();
        loadProperties();
    }

    /**
     * Get the singleton instance
     */
    public static ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }

    /**
     * Load properties from application.properties file
     */
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                System.err.println("WARNING: application.properties file not found in classpath");
                loadDefaults();
                return;
            }

            properties.load(input);
        } catch (Exception e) {
            System.err.println("ERROR loading application.properties: " + e.getMessage());
            loadDefaults();
        }
    }

    /**
     * Load default values if properties file is not found
     */
    private void loadDefaults() {
        // Database defaults
        properties.setProperty("database.server", "localhost");
        properties.setProperty("database.port", "1433");
        properties.setProperty("database.name", "TaskManagementDB");
        properties.setProperty("database.username", "sa");
        properties.setProperty("database.password", "");

        // Application defaults
        properties.setProperty("app.name", "Task Management System");
        properties.setProperty("app.version", "1.0.0");

        // Task defaults
        properties.setProperty("task.default.priority", "Medium");
        properties.setProperty("task.default.status", "To Do");

        // Date format defaults
        properties.setProperty("date.format", "yyyy-MM-dd");
        properties.setProperty("datetime.format", "yyyy-MM-dd HH:mm:ss");
    }

    // Database Configuration
    public String getDatabaseServer() {
        return getProperty("database.server", "localhost");
    }

    public int getDatabasePort() {
        return getIntProperty("database.port", 1433);
    }

    public String getDatabaseName() {
        return getProperty("database.name", "TaskManagementDB");
    }

    public String getDatabaseUsername() {
        return getProperty("database.username", "sa");
    }

    public String getDatabasePassword() {
        return getProperty("database.password", "");
    }

    public String getDatabaseDriver() {
        return getProperty("database.driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    public String getDatabaseConnectionString() {
        return String.format(
                "jdbc:sqlserver://%s:%d;databaseName=%s;encrypt=true;trustServerCertificate=true;",
                getDatabaseServer(),
                getDatabasePort(),
                getDatabaseName()
        );
    }

    // Application Configuration
    public String getAppName() {
        return getProperty("app.name", "Task Management System");
    }

    public String getAppVersion() {
        return getProperty("app.version", "1.0.0");
    }

    public String getAppLanguage() {
        return getProperty("app.language", "en");
    }

    // Session Configuration
    public int getSessionTimeoutMinutes() {
        return getIntProperty("session.timeout.minutes", 30);
    }

    public boolean isRememberMeEnabled() {
        return getBooleanProperty("session.remember.me", true);
    }

    // Task Configuration
    public String getDefaultTaskPriority() {
        return getProperty("task.default.priority", "Medium");
    }

    public String getDefaultTaskStatus() {
        return getProperty("task.default.status", "To Do");
    }

    public String[] getTaskStatuses() {
        return getProperty("task.status.values", "To Do,In Progress,In Review,Done")
                .split(",");
    }

    public String[] getTaskPriorities() {
        return getProperty("task.priority.values", "Low,Medium,High,Urgent")
                .split(",");
    }

    // Security Configuration
    public int getMinPasswordLength() {
        return getIntProperty("security.password.min.length", 8);
    }

    public boolean isSpecialCharsRequired() {
        return getBooleanProperty("security.password.require.special.chars", false);
    }

    public int getMaxLoginAttempts() {
        return getIntProperty("security.max.login.attempts", 5);
    }

    public int getRememberMeDays() {
        return getIntProperty("security.session.remember.days", 30);
    }

    // Pagination Configuration
    public int getDefaultPageSize() {
        return getIntProperty("pagination.default.size", 10);
    }

    public int getMaxPageSize() {
        return getIntProperty("pagination.max.size", 100);
    }

    // Date Format Configuration
    public String getDateFormat() {
        return getProperty("date.format", "yyyy-MM-dd");
    }

    public String getDateTimeFormat() {
        return getProperty("datetime.format", "yyyy-MM-dd HH:mm:ss");
    }

    public String getUIDateFormat() {
        return getProperty("ui.date.format", "MMM dd, yyyy");
    }

    // UI Configuration
    public String getUITheme() {
        return getProperty("ui.theme", "light");
    }

    public String getUILanguage() {
        return getProperty("ui.language", "en");
    }

    // Email Configuration
    public boolean isEmailEnabled() {
        return getBooleanProperty("notification.email.enabled", false);
    }

    public String getEmailHost() {
        return getProperty("notification.email.host", "smtp.gmail.com");
    }

    public int getEmailPort() {
        return getIntProperty("notification.email.port", 587);
    }

    public String getEmailFrom() {
        return getProperty("notification.email.from", "noreply@taskmanagement.com");
    }

    // Generic property getters
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("Invalid integer property: " + key);
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    /**
     * Get all properties as a map
     */
    public Map<String, String> getAllProperties() {
        Map<String, String> map = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }
        return map;
    }

    /**
     * Set a property value (runtime only)
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * Check if a property exists
     */
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    /**
     * Print all properties (for debugging)
     */
    public void printAll() {
        System.out.println("=== Application Configuration ===");
        properties.forEach((key, value) ->
                System.out.println(key + " = " + value)
        );
    }
}
