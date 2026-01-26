package com.taskmanagement.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
    public String getDatabaseServer() {
        return getProperty(KEY_DB_SERVER, DEFAULT_DB_SERVER);
    }
    public int getDatabasePort() {
        return getIntProperty(KEY_DB_PORT, DEFAULT_DB_PORT);
    }

    public String getDatabaseName() {
        return getProperty(KEY_DB_NAME, "TaskManagementDB");
    }
    public String getDatabaseUsername() {
        return getProperty(KEY_DB_USERNAME, "sa");
    }
    public String getDatabasePassword() {
        return getProperty(KEY_DB_PASSWORD, "");
    }

    public String getDatabaseDriver() {
        return getProperty(KEY_DB_DRIVER, DEFAULT_DB_DRIVER);
    }

    public String getDatabaseConnectionString() {
        return String.format(CONNECTION_TEMPLATE,
                getDatabaseServer(),
                getDatabasePort(),
                getDatabaseName()
        );
    }

    public String getAppName() {
        return getProperty(KEY_APP_NAME, DEFAULT_APP_NAME);
    }

    public String getAppVersion() {
        return getProperty(KEY_APP_VERSION, DEFAULT_APP_VERSION);
    }

    public String getAppLanguage() {
        return getProperty(KEY_APP_LANGUAGE, "en");
    }

    public int getSessionTimeoutMinutes() {
        return getIntProperty(KEY_SESSION_TIMEOUT, 30);
    }
    public boolean isRememberMeEnabled() {
        return getBooleanProperty(KEY_SESSION_REMEMBER, true);
    }
    public String getDefaultTaskPriority() {
        return getProperty(KEY_TASK_PRIORITY, "Medium");
    }

    public String getDefaultTaskStatus() {
        return getProperty(KEY_TASK_STATUS, "To Do");
    }
    public String[] getTaskStatuses() {
        return getProperty(KEY_TASK_STATUSES, DEFAULT_TASK_STATUSES).split(",");
    }

    public String[] getTaskPriorities() {
        return getProperty(KEY_TASK_PRIORITIES, DEFAULT_TASK_PRIORITIES).split(",");
    }
    public int getMinPasswordLength() {
        return getIntProperty(KEY_SEC_PASSWORD_LENGTH, 8);
    }
    public boolean isSpecialCharsRequired() {
        return getBooleanProperty(KEY_SEC_SPECIAL_CHARS, false);
    }
    public int getMaxLoginAttempts() {
        return getIntProperty(KEY_SEC_LOGIN_ATTEMPTS, 5);
    }
    public int getRememberMeDays() {
        return getIntProperty(KEY_SEC_REMEMBER_DAYS, 30);
    }
    public int getDefaultPageSize() {
        return getIntProperty(KEY_PAGE_DEFAULT, 10);
    }
    public int getMaxPageSize() {
        return getIntProperty(KEY_PAGE_MAX, 100);
    }
    public String getDateFormat() {
        return getProperty(KEY_DATE_FORMAT, DEFAULT_DATE_FORMAT);
    }
    public String getDateTimeFormat() {
        return getProperty(KEY_DATETIME_FORMAT, DEFAULT_DATETIME_FORMAT);
    }
    public String getUIDateFormat() {
        return getProperty(KEY_UI_DATE_FORMAT, "MMM dd, yyyy");
    }

    public String getUITheme() {
        return getProperty(KEY_UI_THEME, "light");
    }

    public String getUILanguage() {
        return getProperty(KEY_UI_LANGUAGE, "en");
    }

    public boolean isEmailEnabled() {
        return getBooleanProperty(KEY_EMAIL_ENABLED, false);
    }

    public String getEmailHost() {
        return getProperty(KEY_EMAIL_HOST, "smtp.gmail.com");
    }

    public int getEmailPort() {
        return getIntProperty(KEY_EMAIL_PORT, 587);
    }

    public String getEmailFrom() {
        return getProperty(KEY_EMAIL_FROM, "noreply@taskmanagement.com");
    }

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
            System.err.println(ERR_INVALID_INT + key);
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

    public Map<String, String> getAllProperties() {
        Map<String, String> map = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }
        return map;
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    public void printAll() {
        System.out.println("=== Application Configuration ===");
        properties.forEach((key, value) ->
                System.out.println(key + " = " + value)
        );
    }
}
