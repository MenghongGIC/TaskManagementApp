package com.taskmanagement.constants;

public class AppConstants {

    // Application Info
    public static final String APP_TITLE = "Task Management System";
    public static final String APP_VERSION = "1.0.0";

    // Task Status
    public static final String STATUS_TODO = "To Do";
    public static final String STATUS_IN_PROGRESS = "In Progress";
    public static final String STATUS_DONE = "Done";

    // Task Priority
    public static final String PRIORITY_LOW = "Low";
    public static final String PRIORITY_MEDIUM = "Medium";
    public static final String PRIORITY_HIGH = "High";

    // Status Colors (CSS)
    public static class StatusColors {
        public static final String TODO = "-fx-fill: #3498db;";
        public static final String IN_PROGRESS = "-fx-fill: #f39c12;";
        public static final String DONE = "-fx-fill: #27ae60;";
    }

    // Priority Colors (CSS)
    public static class PriorityColors {
        public static final String LOW = "-fx-text-fill: #27ae60;";
        public static final String MEDIUM = "-fx-text-fill: #f39c12;";
        public static final String HIGH = "-fx-text-fill: #e74c3c;";
    }

    // Priority Backgrounds (CSS)
    public static class PriorityBackgrounds {
        public static final String LOW = "-fx-background-color: #d5f4e6; -fx-text-fill: #27ae60;";
        public static final String MEDIUM = "-fx-background-color: #fdebd0; -fx-text-fill: #f39c12;";
        public static final String HIGH = "-fx-background-color: #fadbd8; -fx-text-fill: #e74c3c;";
    }

    // Error/Success Messages
    public static class Messages {
        public static final String ERROR_LOADING_TASKS = "Error loading tasks: ";
        public static final String ERROR_LOADING_PROJECTS = "Error loading projects: ";
        public static final String ERROR_LOADING_USERS = "Error loading users: ";
        public static final String ERROR_DELETING_TASK = "Error deleting task: ";
        public static final String ERROR_CREATING_TASK = "Error creating task: ";
        public static final String ERROR_UPDATING_TASK = "Error updating task: ";
        public static final String TASK_DELETED = "Task deleted successfully";
        public static final String TASK_CREATED = "Task created successfully";
        public static final String TASK_UPDATED = "Task updated successfully";
        public static final String PROJECT_CREATED = "Project created successfully";
        public static final String PROJECT_UPDATED = "Project updated successfully";
        public static final String PROJECT_DELETED = "Project deleted successfully";
        public static final String INVALID_INPUT = "Invalid input provided";
        public static final String PLEASE_FILL_REQUIRED = "Please fill all required fields";
        public static final String NO_CHANGES_DETECTED = "No changes detected";
        public static final String ENTER_BOTH_USERNAME_PASSWORD = "Please enter both username and password";
        public static final String INVALID_CREDENTIALS = "Invalid username or password";
    }

    // Validation Messages
    public static class Validation {
        public static final String EMAIL_REQUIRED = "Email is required";
        public static final String INVALID_EMAIL = "Invalid email format";
        public static final String USERNAME_REQUIRED = "Username is required";
        public static final String PASSWORD_REQUIRED = "Password is required";
        public static final String PASSWORD_MIN_LENGTH = "Password must be at least 8 characters";
    }

    // UI Constants
    public static class UI {
        public static final int DEFAULT_BUTTON_HEIGHT = 40;
        public static final int DEFAULT_BUTTON_WIDTH = 100;
        public static final int DIALOG_WIDTH = 600;
        public static final int DIALOG_HEIGHT = 400;
        public static final int TASK_CARD_WIDTH = 280;
        public static final String DEFAULT_FONT_SIZE = "14px";
    }

    // Database Constants
    public static class Database {
        public static final String TABLE_TASKS = "Tasks";
        public static final String TABLE_PROJECTS = "Projects";
        public static final String TABLE_USERS = "Users";
    }

    // FXML Paths
    public static class FXMLPaths {
        public static final String LOGIN = "auth/LoginView";
        public static final String REGISTER = "auth/RegisterView";
        public static final String MAIN_LAYOUT = "main/MainLayout";
        public static final String DASHBOARD = "main/Dashboard";
        public static final String PROJECT_LIST = "main/ProjectList";
        public static final String TASK_LIST = "main/TaskList";
        public static final String CREATE_TASK = "main/CreateTask";
        public static final String EDIT_TASK = "main/EditTask";
        public static final String CREATE_PROJECT = "main/CreateProject";
        public static final String EDIT_PROJECT = "main/EditProject";
    }
}
