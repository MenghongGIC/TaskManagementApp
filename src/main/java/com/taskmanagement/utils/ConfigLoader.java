package com.taskmanagement.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration loader singleton that reads from application.properties file.
 * Provides centralized access to application configuration with sensible defaults.
 * Thread-safe singleton pattern with lazy initialization.
 */
public class ConfigLoader {

    // Singleton Instance
    private static ConfigLoader instance;
    private final Properties properties;

    // Error Messages
    private static final String ERR_FILE_NOT_FOUND = "WARNING: application.properties file not found in classpath";
    private static final String ERR_LOADING_FAILED = "ERROR loading application.properties: ";
    private static final String ERR_INVALID_INT = "Invalid integer property: ";

    // Property Keys - Database
    private static final String KEY_DB_SERVER = "database.server";
    private static final String KEY_DB_PORT = "database.port";
    private static final String KEY_DB_NAME = "database.name";
    private static final String KEY_DB_USERNAME = "database.username";
    private static final String KEY_DB_PASSWORD = "database.password";
    private static final String KEY_DB_DRIVER = "database.driver";

    // Property Keys - Application
    private static final String KEY_APP_NAME = "app.name";
    private static final String KEY_APP_VERSION = "app.version";
    private static final String KEY_APP_LANGUAGE = "app.language";

    // Property Keys - Session
    private static final String KEY_SESSION_TIMEOUT = "session.timeout.minutes";
    private static final String KEY_SESSION_REMEMBER = "session.remember.me";

    // Property Keys - Task
    private static final String KEY_TASK_PRIORITY = "task.default.priority";
    private static final String KEY_TASK_STATUS = "task.default.status";
    private static final String KEY_TASK_STATUSES = "task.status.values";
    private static final String KEY_TASK_PRIORITIES = "task.priority.values";

    // Property Keys - Security
    private static final String KEY_SEC_PASSWORD_LENGTH = "security.password.min.length";
    private static final String KEY_SEC_SPECIAL_CHARS = "security.password.require.special.chars";
    private static final String KEY_SEC_LOGIN_ATTEMPTS = "security.max.login.attempts";
    private static final String KEY_SEC_REMEMBER_DAYS = "security.session.remember.days";

    // Property Keys - Pagination
    private static final String KEY_PAGE_DEFAULT = "pagination.default.size";
    private static final String KEY_PAGE_MAX = "pagination.max.size";

    // Property Keys - Date Formats
    private static final String KEY_DATE_FORMAT = "date.format";
    private static final String KEY_DATETIME_FORMAT = "datetime.format";
    private static final String KEY_UI_DATE_FORMAT = "ui.date.format";

    // Property Keys - UI
    private static final String KEY_UI_THEME = "ui.theme";
    private static final String KEY_UI_LANGUAGE = "ui.language";

    // Property Keys - Email
    private static final String KEY_EMAIL_ENABLED = "notification.email.enabled";
    private static final String KEY_EMAIL_HOST = "notification.email.host";
    private static final String KEY_EMAIL_PORT = "notification.email.port";
    private static final String KEY_EMAIL_FROM = "notification.email.from";

    // Default Values
    private static final String DEFAULT_DB_SERVER = "localhost";
    private static final int DEFAULT_DB_PORT = 1433;
    private static final String DEFAULT_DB_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String DEFAULT_APP_NAME = "Task Management System";
    private static final String DEFAULT_APP_VERSION = "1.0.0";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_TASK_STATUSES = "To Do,In Progress,In Review,Done";
    private static final String DEFAULT_TASK_PRIORITIES = "Low,Medium,High,Urgent";

    // Connection String Template
    private static final String CONNECTION_TEMPLATE = "jdbc:sqlserver://%s:%d;databaseName=%s;encrypt=true;trustServerCertificate=true;";
    private ConfigLoader() {
        properties = new Properties();
        loadProperties();
    }
    public static ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                System.err.println(ERR_FILE_NOT_FOUND);
                loadDefaults();
                return;
            }

            properties.load(input);
        } catch (Exception e) {
            System.err.println(ERR_LOADING_FAILED + e.getMessage());
            loadDefaults();
        }
    }
    private void loadDefaults() {
        // Database defaults
        properties.setProperty(KEY_DB_SERVER, DEFAULT_DB_SERVER);
        properties.setProperty(KEY_DB_PORT, String.valueOf(DEFAULT_DB_PORT));
        properties.setProperty(KEY_DB_NAME, "TaskManagementDB");
        properties.setProperty(KEY_DB_USERNAME, "sa");
        properties.setProperty(KEY_DB_PASSWORD, "");

        // Application defaults
        properties.setProperty(KEY_APP_NAME, DEFAULT_APP_NAME);
        properties.setProperty(KEY_APP_VERSION, DEFAULT_APP_VERSION);

        // Task defaults
        properties.setProperty(KEY_TASK_PRIORITY, "Medium");
        properties.setProperty(KEY_TASK_STATUS, "To Do");

        // Date format defaults
        properties.setProperty(KEY_DATE_FORMAT, DEFAULT_DATE_FORMAT);
        properties.setProperty(KEY_DATETIME_FORMAT, DEFAULT_DATETIME_FORMAT);
    }

    // ============ Database Configuration ============

    /**
     * Get database server hostname.
     *
     * @return database server address
     */
    public String getDatabaseServer() {
        return getProperty(KEY_DB_SERVER, DEFAULT_DB_SERVER);
    }

    /**
     * Get database server port.
     *
     * @return database port number
     */
    public int getDatabasePort() {
        return getIntProperty(KEY_DB_PORT, DEFAULT_DB_PORT);
    }

    /**
     * Get database name.
     *
     * @return database name
     */
    public String getDatabaseName() {
        return getProperty(KEY_DB_NAME, "TaskManagementDB");
    }

    /**
     * Get database username.
     *
     * @return database username
     */
    public String getDatabaseUsername() {
        return getProperty(KEY_DB_USERNAME, "sa");
    }

    /**
     * Get database password.
     *
     * @return database password
     */
    public String getDatabasePassword() {
        return getProperty(KEY_DB_PASSWORD, "");
    }

    /**
     * Get JDBC driver class name.
     *
     * @return driver class name
     */
    public String getDatabaseDriver() {
        return getProperty(KEY_DB_DRIVER, DEFAULT_DB_DRIVER);
    }

    /**
     * Get complete JDBC connection string.
     *
     * @return fully formatted connection string
     */
    public String getDatabaseConnectionString() {
        return String.format(CONNECTION_TEMPLATE,
                getDatabaseServer(),
                getDatabasePort(),
                getDatabaseName()
        );
    }

    // ============ Application Configuration ============

    /**
     * Get application name.
     *
     * @return application name
     */
    public String getAppName() {
        return getProperty(KEY_APP_NAME, DEFAULT_APP_NAME);
    }

    /**
     * Get application version.
     *
     * @return version string
     */
    public String getAppVersion() {
        return getProperty(KEY_APP_VERSION, DEFAULT_APP_VERSION);
    }

    /**
     * Get application language.
     *
     * @return language code (e.g., "en")
     */
    public String getAppLanguage() {
        return getProperty(KEY_APP_LANGUAGE, "en");
    }

    // ============ Session Configuration ============

    /**
     * Get session timeout in minutes.
     *
     * @return timeout in minutes
     */
    public int getSessionTimeoutMinutes() {
        return getIntProperty(KEY_SESSION_TIMEOUT, 30);
    }

    /**
     * Check if "Remember Me" functionality is enabled.
     *
     * @return true if remember me is enabled
     */
    public boolean isRememberMeEnabled() {
        return getBooleanProperty(KEY_SESSION_REMEMBER, true);
    }

    // ============ Task Configuration ============

    /**
     * Get default task priority.
     *
     * @return default priority level
     */
    public String getDefaultTaskPriority() {
        return getProperty(KEY_TASK_PRIORITY, "Medium");
    }

    /**
     * Get default task status.
     *
     * @return default status
     */
    public String getDefaultTaskStatus() {
        return getProperty(KEY_TASK_STATUS, "To Do");
    }

    /**
     * Get all valid task statuses.
     *
     * @return array of status values
     */
    public String[] getTaskStatuses() {
        return getProperty(KEY_TASK_STATUSES, DEFAULT_TASK_STATUSES).split(",");
    }

    /**
     * Get all valid task priorities.
     *
     * @return array of priority values
     */
    public String[] getTaskPriorities() {
        return getProperty(KEY_TASK_PRIORITIES, DEFAULT_TASK_PRIORITIES).split(",");
    }

    // ============ Security Configuration ============

    /**
     * Get minimum password length requirement.
     *
     * @return minimum characters required
     */
    public int getMinPasswordLength() {
        return getIntProperty(KEY_SEC_PASSWORD_LENGTH, 8);
    }

    /**
     * Check if special characters are required in passwords.
     *
     * @return true if special characters required
     */
    public boolean isSpecialCharsRequired() {
        return getBooleanProperty(KEY_SEC_SPECIAL_CHARS, false);
    }

    /**
     * Get maximum login attempts before lockout.
     *
     * @return maximum attempts allowed
     */
    public int getMaxLoginAttempts() {
        return getIntProperty(KEY_SEC_LOGIN_ATTEMPTS, 5);
    }

    /**
     * Get "Remember Me" session duration in days.
     *
     * @return days to remember
     */
    public int getRememberMeDays() {
        return getIntProperty(KEY_SEC_REMEMBER_DAYS, 30);
    }

    // ============ Pagination Configuration ============

    /**
     * Get default page size for data pagination.
     *
     * @return default items per page
     */
    public int getDefaultPageSize() {
        return getIntProperty(KEY_PAGE_DEFAULT, 10);
    }

    /**
     * Get maximum page size allowed.
     *
     * @return maximum items per page
     */
    public int getMaxPageSize() {
        return getIntProperty(KEY_PAGE_MAX, 100);
    }

    // ============ Date Format Configuration ============

    /**
     * Get date format pattern.
     *
     * @return SimpleDateFormat pattern
     */
    public String getDateFormat() {
        return getProperty(KEY_DATE_FORMAT, DEFAULT_DATE_FORMAT);
    }

    /**
     * Get date-time format pattern.
     *
     * @return SimpleDateFormat pattern
     */
    public String getDateTimeFormat() {
        return getProperty(KEY_DATETIME_FORMAT, DEFAULT_DATETIME_FORMAT);
    }

    /**
     * Get UI date format pattern.
     *
     * @return SimpleDateFormat pattern for UI display
     */
    public String getUIDateFormat() {
        return getProperty(KEY_UI_DATE_FORMAT, "MMM dd, yyyy");
    }

    // ============ UI Configuration ============

    /**
     * Get UI theme name.
     *
     * @return theme identifier (e.g., "light", "dark")
     */
    public String getUITheme() {
        return getProperty(KEY_UI_THEME, "light");
    }

    /**
     * Get UI language preference.
     *
     * @return language code
     */
    public String getUILanguage() {
        return getProperty(KEY_UI_LANGUAGE, "en");
    }

    // ============ Email Configuration ============

    /**
     * Check if email notifications are enabled.
     *
     * @return true if email is enabled
     */
    public boolean isEmailEnabled() {
        return getBooleanProperty(KEY_EMAIL_ENABLED, false);
    }

    /**
     * Get SMTP server hostname.
     *
     * @return email host address
     */
    public String getEmailHost() {
        return getProperty(KEY_EMAIL_HOST, "smtp.gmail.com");
    }

    /**
     * Get SMTP server port.
     *
     * @return SMTP port number
     */
    public int getEmailPort() {
        return getIntProperty(KEY_EMAIL_PORT, 587);
    }

    /**
     * Get sender email address for notifications.
     *
     * @return from email address
     */
    public String getEmailFrom() {
        return getProperty(KEY_EMAIL_FROM, "noreply@taskmanagement.com");
    }

    // ============ Generic Property Getters ============

    /**
     * Get property value without default.
     *
     * @param key the property key
     * @return property value or null if not found
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Get property value with default fallback.
     *
     * @param key the property key
     * @param defaultValue the value to return if property not found
     * @return property value or defaultValue
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get integer property value.
     *
     * @param key the property key
     * @param defaultValue the default if property not found or invalid
     * @return parsed integer value
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println(ERR_INVALID_INT + key);
            return defaultValue;
        }
    }

    /**
     * Get boolean property value.
     *
     * @param key the property key
     * @param defaultValue the default if property not found
     * @return parsed boolean value
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    // ============ Utility Methods ============

    /**
     * Get all properties as a map.
     *
     * @return map of all properties
     */
    public Map<String, String> getAllProperties() {
        Map<String, String> map = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }
        return map;
    }

    /**
     * Set or override a property value at runtime.
     *
     * @param key the property key
     * @param value the value to set
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * Check if a property exists.
     *
     * @param key the property key
     * @return true if property is defined
     */
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    /**
     * Print all properties to console for debugging.
     */
    public void printAll() {
        System.out.println("=== Application Configuration ===");
        properties.forEach((key, value) ->
                System.out.println(key + " = " + value)
        );
    }
}
