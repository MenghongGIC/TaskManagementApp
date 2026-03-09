# Task Management System

A comprehensive JavaFX desktop application for efficiently managing tasks, projects, and teams. Built with Java 21, Maven, and SQL Server, featuring a modern GUI, role-based access control, and robust business logic.

## Refactor Update (2026-03-06)

The UI architecture has been streamlined to a Trello-style controller model with a clean, focused controller set:

- `MainController` (navigation shell)
- `AuthController` (login/register)
- `DashboardController` (overview metrics)
- `TaskController` (task list, filter, actions)
- `TaskFormController` (create/edit task dialog)
- `KanbanController` (drag-and-drop board)
- `ProjectController` (project CRUD)
- `TeamController` (team list/management view)
- `ActivityController` (activity log view)

FXML layout is organized under:

- `fxml/auth` (`LoginView.fxml`, `RegisterView.fxml`)
- `fxml/main` (`MainView.fxml`, `Dashboard.fxml`, `KanbanBoard.fxml`, `ActivityView.fxml`, `WorkspaceView.fxml`)
- `fxml/task` (`TaskList.fxml`, `TaskDetailView.fxml`, `TaskForm.fxml`)
- `fxml/project` (`ProjectList.fxml`, `ProjectDetailView.fxml`)
- `fxml/team` (`TeamListView.fxml`, `TeamDetailView.fxml`, `TeamForm.fxml`)
- `fxml/workspace` (`WorkspaceDashboard.fxml`, `WorkspaceForm.fxml`)
- `fxml/dialog` (`TaskForm.fxml`, `ProjectForm.fxml`)
- `fxml/components` (`TaskCard.fxml`, `KanbanColumn.fxml`)

Legacy/duplicate controllers and stale FXML files were removed to eliminate overlapping responsibilities and dead navigation paths.

## System Overview

The Task Management System is a professional-grade desktop application designed for teams and organizations to organize, track, and collaborate on projects and tasks. It provides a complete solution for task lifecycle management, from creation to completion, with comprehensive tracking and reporting capabilities.

## Core Features

### User & Authentication Management
- ✅ **User Registration & Login**: Secure authentication system with username and password
- ✅ **Role-Based Access Control (RBAC)**: Two-tier role system (Admin and User) with granular permissions
- ✅ **User Profiles**: Store user information including username, email, position, and role
- ✅ **Last Login Tracking**: Track user login activity for security auditing
- ✅ **Admin User Management**: Admins can manage user accounts and assign roles
- ✅ **Session Management**: Secure session handling with current user tracking

### Task Management
- ✅ **Complete Task CRUD**: Create, read, update, and delete tasks with full validation
- ✅ **Task Properties**: Title, description, status, priority, due date, and assignee
- ✅ **Task Assignment**: Assign tasks to specific team members with permission controls
- ✅ **Status Tracking**: Three task statuses (To Do, In Progress, Done) with status transitions
- ✅ **Priority Levels**: Four priority levels (Low, Medium, High, Urgent) with visual indicators
- ✅ **Due Date Management**: Set task due dates, with automatic overdue detection
- ✅ **Task Filtering**: Filter and search tasks by status, priority, assignee, or project
- ✅ **Task Permissions**: Edit and delete permissions based on task creator and role
- ✅ **Overdue Task Detection**: Automatically identify and highlight tasks past their due date

### Project Organization
- ✅ **Project Management**: Create, update, and delete projects with custom names and descriptions
- ✅ **Project Colors**: Assign custom colors to projects for visual organization
- ✅ **Task Grouping**: Group multiple tasks within a project for better organization
- ✅ **Project Ownership**: Track project creator and manage project permissions
- ✅ **Project Statistics**: View task counts and project details
- ✅ **Admin Project Control**: Admins can view and manage all projects system-wide
- ✅ **User Project Isolation**: Regular users see only their own projects

### Labels & Categorization
- ✅ **Task Labels**: Add labels to tasks for flexible categorization
- ✅ **Label Colors**: Assign custom hex colors to labels for visual distinction
- ✅ **Predefined Labels**: Common labels available (Bug, Feature, Documentation, Enhancement, Urgent, Blocked, In Review, On Hold)
- ✅ **Multi-Label Support**: Attach multiple labels to a single task
- ✅ **Label Management**: Full CRUD operations on custom labels

### Activity Tracking & Audit Trail
- ✅ **Complete Activity Log**: Track all system changes with timestamp and actor information
- ✅ **Task Actions Logged**: Log task creation, updates, deletion, and status changes
- ✅ **Project Actions Logged**: Log project creation, updates, and deletion
- ✅ **Assignment Tracking**: Record all task assignments with assignee details
- ✅ **Activity History**: Retrieve activity logs filtered by action, entity type, or user
- ✅ **Audit Trail**: Comprehensive audit trail for compliance and troubleshooting

### Dashboard & Analytics
- ✅ **Task Statistics**: View completion rates, task counts by status, and priority distribution
- ✅ **Dashboard View**: Centralized dashboard showing key metrics and insights
- ✅ **Quick Access**: Dashboard provides quick navigation to projects and tasks
- ✅ **Workload Visibility**: See task distribution across team members

### User Interface
- ✅ **Multiple Views**: Table, Kanban, and List views for different workflow preferences
- ✅ **Project List View**: Visual list view for project browsing
- ✅ **Project Detail View**: Comprehensive project details with task overview
- ✅ **Task Detail View**: Full task information with editing capabilities
- ✅ **User Task Dialog**: Dialog interface for managing user-specific tasks
- ✅ **Admin Panel**: Dedicated admin interface for user and system management
- ✅ **Responsive Layout**: Main layout controller with navigation and menu options

### Data Validation & Input Control
- ✅ **Email Validation**: RFC-compliant email format validation
- ✅ **Username Validation**: 3-20 character alphanumeric usernames with underscore and hyphen support
- ✅ **Password Validation**: Minimum 8-character password requirement with strength validation
- ✅ **Task Title Validation**: 3-255 character requirement with trim and clean input
- ✅ **Project Name Validation**: 3-100 character requirement with trim and clean input
- ✅ **Hex Color Validation**: Validate hex color codes for projects and labels
- ✅ **Date Validation**: Ensure due dates are present or future dates
- ✅ **Non-Empty Field Checks**: Prevent empty strings and whitespace-only inputs
- ✅ **Length Validation**: Enforce minimum and maximum length constraints on all text fields

### Permission & Security System
- ✅ **Permission-Based Access**: Control who can view, edit, and delete resources
- ✅ **Admin Privileges**: Full system access and all operations
- ✅ **User Privileges**: Limited access based on task ownership and project membership
- ✅ **Task Ownership**: Users can edit/delete only their own created tasks
- ✅ **View Permissions**: Permission checks prevent unauthorized data access
- ✅ **Security Exceptions**: Throw security exceptions for unauthorized operations

## Advanced Features

### Filtering & Search
- 🔍 **Task Search**: Full-text search across task titles and descriptions
- 🔍 **Multi-Filter Support**: Filter by multiple criteria simultaneously (status, priority, assignee)
- 🔍 **Project Filtering**: Filter projects by status, creator, or date range
- 🔍 **Advanced Queries**: Combine filters for complex task searches

### Reporting & Insights
- 📊 **Completion Metrics**: Calculate task completion rates by project
- 📊 **Status Distribution**: View tasks grouped by status
- 📊 **Priority Analytics**: Analyze task distribution across priority levels
- 📊 **Overdue Reporting**: Identify and report overdue tasks
- 📊 **Workload Analysis**: Analyze task distribution among team members

### Date Management
- 📅 **Due Date Tracking**: Set and track task due dates
- 📅 **Overdue Detection**: Automatic flagging of overdue tasks
- 📅 **Date Labels**: Smart date labels (Today, Tomorrow, Overdue, etc.)
- 📅 **Date Filtering**: Filter tasks due within specified timeframes
- 📅 **Created Date Tracking**: Track when tasks and projects were created
- 📅 **Last Login Tracking**: Monitor user login timestamps

## Technology Stack

- **Language**: Java 21 (LTS - Long Term Support)
- **GUI Framework**: JavaFX 21 (Modern desktop UI with FXML markup)
- **Build Tool**: Apache Maven 3.8+
- **Database**: Microsoft SQL Server 2019+ (via JDBC driver)
- **Testing Framework**: JUnit 5 (Jupiter)
- **Database Driver**: Microsoft SQL Server JDBC 12.6.3
- **Project Management**: Maven with Shade Plugin for packaging

## Project Architecture

```
TaskManagementSystem/
├── pom.xml                          # Maven project configuration
├── application.properties            # Application configuration
├── README.md                         # This file
│
├── src/main/java/com/taskmanagement/
│   ├── App.java                      # Main application entry point
│   │
│   ├── controller/                   # JavaFX UI Controllers (modular)
│   │   ├── MainController.java       # Main navigation shell
│   │   ├── AuthController.java       # Login/register actions
│   │   ├── DashboardController.java  # Dashboard metrics
│   │   ├── TaskController.java       # Task list and actions
│   │   ├── TaskFormController.java   # Create/edit task form
│   │   ├── TaskDetailController.java # Task detail dialog
│   │   ├── KanbanController.java     # Kanban board interactions
│   │   ├── ProjectController.java    # Project list and CRUD
│   │   ├── ProjectDetailController.java # Project detail dialog
│   │   ├── TeamController.java       # Team management list
│   │   ├── TeamFormController.java   # Team create/update form
│   │   ├── TeamDetailController.java # Team detail dialog
│   │   ├── WorkspaceController.java  # Workspace dashboard actions
│   │   └── ActivityController.java   # Activity log view
│   │
│   ├── service/                      # Business Logic Layer (6 services)
│   │   ├── TaskService.java          # Task CRUD and operations
│   │   ├── ProjectService.java       # Project management and state
│   │   ├── UserService.java          # User authentication and profiles
│   │   ├── ActivityLogService.java   # Activity logging and audit trail
│   │   ├── LabelService.java         # Label management
│   │   └── AuthService.java          # Authentication facade
│   │
│   ├── repository/                   # Data Access Layer (4 repositories)
│   │   ├── BaseRepository.java       # Abstract base for all repositories
│   │   ├── TaskRepository.java       # Task database operations
│   │   ├── ProjectRepository.java    # Project database operations
│   │   └── UserRepository.java       # User database operations
│   │
│   ├── model/                        # Domain Models (9 classes)
│   │   ├── User.java                 # User entity
│   │   ├── Task.java                 # Task entity
│   │   ├── Project.java              # Project entity
│   │   ├── Label.java                # Label entity
│   │   ├── ActivityLog.java          # Activity log entry
│   │   ├── Role.java                 # Role enumeration (Admin, User)
│   │   ├── Status.java               # Task status enumeration
│   │   ├── Priority.java             # Priority enumeration
│   │   └── TaskStatus.java           # Task status tracking
│   │
│   ├── database/                     # Data Access Configuration
│   │   └── DBConnection.java         # SQL Server connection management
│   │
│   ├── utils/                        # Utility & Helper Classes
│   │   ├── InputValidator.java       # Input validation utilities
│   │   ├── PermissionChecker.java    # Permission and access checks
│   │   ├── DateUtils.java            # Date formatting and analysis
│   │   ├── ColorValidator.java       # Hex color validation
│   │   ├── CurrentUser.java          # Session management
│   │   └── [Additional utilities]
│   │
│   └── constants/                    # Application Constants
│       └── AppConstants.java         # Global constants and messages
│
├── src/main/resources/com/taskmanagement/
│   ├── fxml/                         # JavaFX UI Layouts (FXML files)
│   │   ├── auth/                     # Login and registration screens
│   │   ├── main/                     # Main shell + dashboard + activity
│   │   ├── task/                     # Task list/detail/form screens
│   │   ├── project/                  # Project list/detail screens
│   │   ├── team/                     # Team list/detail/form screens
│   │   ├── workspace/                # Workspace dashboard/form screens
│   │   ├── dialog/                   # Reusable dialogs
│   │   └── components/               # Reusable FXML components
│   │
│   ├── css/                          # Stylesheets
│   │   └── style.css                 # Application styling
│   │
│   ├── icons/                        # UI Icons and Images
│   │
│   └── sql/                          # Database Scripts
│       └── init_sql_database.sql    # Database initialization script
│
├── src/test/java/com/taskmanagement/
│   ├── service/                      # Service layer tests
│   ├── ui/                           # FXML/controller wiring smoke tests
│   └── utils/                        # Utility class tests
│
└── target/                           # Build output (Maven)
    ├── classes/                      # Compiled classes
    ├── test-classes/                 # Compiled tests
    └── generated-sources/            # Generated source code
```

## Layered Architecture Pattern

```
┌─────────────────────────────────────────────────────────────┐
│           JavaFX UI Layer (Controllers)                     │
│     Handles user interaction and display logic              │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────┴────────────────────────────────────────┐
│         Business Logic Layer (Services)                     │
│   - TaskService - ProjectService - UserService             │
│   - ActivityLogService - LabelService - AuthService        │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────┴────────────────────────────────────────┐
│         Data Access Layer (Repositories)                    │
│   - TaskRepository - ProjectRepository - UserRepository    │
│   - BaseRepository (common functionality)                  │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────┴────────────────────────────────────────┐
│      Database Layer (SQL Server)                            │
│     - Users - Projects - Tasks - Labels                    │
│     - TaskLabels - ActivityLog                             │
└─────────────────────────────────────────────────────────────┘
```

## Getting Started

### Prerequisites
- **Java 21 or higher** installed
- **Maven 3.8+** installed
- **SQL Server** 2019 or later
- Git (optional, for cloning)

### Installation

1. **Clone or download the project**
```bash
cd /path/to/TaskManagementApp
```

2. **Create the database**
   - Open SQL Server Management Studio
   - Run the SQL script: `src/main/resources/com/taskmanagement/sql/init_sql_database.sql`
   - This creates the database, tables, and sample data

3. **Update database connection** (if needed)
   - Edit `src/main/java/com/taskmanagement/database/DBConnection.java`
   - Update the connection string, username, and password

4. **Build the project**
```bash
mvn clean compile
```

5. **Run the application**
```bash
mvn clean compile javafx:run
```

Or run with a single command:
```bash
mvn clean compile javafx:run
```

### Default Test Credentials
- **Admin User**: username: `admin`, password: `admin12345`
- **Regular User**: username: `john`, password: `password123`
- **Regular User**: username: `jane`, password: `password123`

## Maven Commands

| Command | Description |
|---------|-------------|
| `mvn clean` | Delete the target folder |
| `mvn compile` | Compile source code |
| `mvn clean compile javafx:run` | Build and run the application |
| `mvn package` | Create a JAR file |
| `mvn test` | Run unit tests |

## Service Layer Architecture

The application follows a layered architecture with clear separation of concerns:

### TaskService
Complete task lifecycle management with CRUD operations, filtering, searching, and analytics.

```java
TaskService taskService = new TaskService();

// Create task
Task task = taskService.createTask("Fix login bug", "Users can't login", project);
task.setPriority("High");
task.setDueDate(LocalDate.now().plusDays(3));

// Retrieve and filter tasks
List<Task> allTasks = taskService.getAllTasks();
List<Task> projectTasks = taskService.getTasksByProject(projectId);
List<Task> userTasks = taskService.getTasksByAssignee(userId);
List<Task> highPriority = taskService.getTasksByPriority("High");
List<Task> completedTasks = taskService.getTasksByStatus("Done");

// Search and analyze
List<Task> results = taskService.searchTasks("keyword");
List<Task> overdue = taskService.getOverdueTasks();
List<Task> dueWithin = taskService.getTasksDueWithin(7);
int completionRate = taskService.getCompletionRate(projectId);

// Update and delete
taskService.updateTask(modifiedTask);
taskService.deleteTask(taskId);
```

### ProjectService
Full project management with team collaboration and permission control.

```java
ProjectService projectService = new ProjectService();

// Create project
Project project = projectService.createProject("Website Redesign", "Q1 Project", "#007BFF");
project.setDescription("Complete website modernization");

// Retrieve projects
List<Project> allProjects = projectService.getAllProjects();
Project p = projectService.getProjectById(projectId);
List<Project> userProjects = projectService.getUserProjects(userId);

// State management
projectService.selectProject(project);
projectService.setViewType("kanban");
projectService.setProjectTasks(tasks);

// Update and delete
projectService.updateProject(modifiedProject);
projectService.deleteProject(projectId);
```

### UserService
User account management, authentication, and profile operations.

```java
UserService userService = new UserService();

// User registration
User user = userService.register("john_doe", "john@example.com", "SecurePass123");

// User authentication
User loggedIn = userService.login("john_doe", "SecurePass123");

// Update user profile
user.setPosition("Software Developer");
userService.updateUser(modifiedUser);

// Change password
userService.changePassword(userId, "OldPassword", "NewPassword123");

// Admin operations
List<User> allUsers = userService.getAllUsers();
userService.changeUserRole(userId, Role.ADMIN);

// Logout
userService.logout();
```

### ActivityLogService
Track all system changes and create an audit trail for compliance.

```java
// Log activities
ActivityLogService.logTaskCreated(taskId, "Task Title");
ActivityLogService.logTaskStatusChanged(taskId, "Title", "To Do", "In Progress");
ActivityLogService.logTaskAssigned(taskId, "Title", "john_doe");
ActivityLogService.logTaskUpdated(taskId, "Title", "description updated");
ActivityLogService.logTaskDeleted(taskId, "Title");

ActivityLogService.logProjectCreated(projectId, "Project Name");
ActivityLogService.logProjectUpdated(projectId, "Project Name", "description changed");
ActivityLogService.logProjectDeleted(projectId, "Project Name");

// Retrieve activity history
List<ActivityLog> allActivities = ActivityLogService.getAllActivities();
List<ActivityLog> recent = ActivityLogService.getRecentActivities(20);
List<ActivityLog> taskActions = ActivityLogService.getActivitiesByEntity("TASK");
List<ActivityLog> userActions = ActivityLogService.getActivitiesByActor(userId);
List<ActivityLog> creations = ActivityLogService.getActivitiesByAction("TASK_CREATED");
```

### LabelService
Manage task labels with custom colors and predefined templates.

```java
LabelService labelService = new LabelService();

// Create custom labels
Label bugLabel = labelService.createLabel("Bug", "#DC3545");
Label featureLabel = labelService.createLabel("Feature");  // Uses default color

// Get predefined labels
List<Label> commonLabels = labelService.getCommonLabels();
// Includes: Bug, Feature, Documentation, Enhancement, Urgent, Blocked, In Review, On Hold

// Get color palette
List<String> colors = labelService.getColorPalette();

// Validate colors
boolean valid = labelService.isValidColor("#007BFF");
```

### AuthService
Unified authentication interface delegating to UserService.

```java
AuthService authService = new AuthService();

// Login and logout
User user = authService.login("username", "password");
authService.logout();

// Register new user
User newUser = authService.register("newuser", "email@example.com", "password123");

// Check authentication status
boolean isLoggedIn = authService.isLoggedIn();
boolean isAdmin = authService.isAdmin();
User current = authService.getCurrentUser();

// Admin operations
authService.changeUserRole(userId, Role.ADMIN);
List<User> allUsers = authService.getAllUsers();
```

### Utility Classes

#### InputValidator
Comprehensive input validation across the application.

```java
// Validation methods
InputValidator.isValidEmail("user@example.com");
InputValidator.isValidUsername("john_doe");      // 3-20 chars, alphanumeric, _, -
InputValidator.isValidPassword("securePass123"); // Min 8 chars
InputValidator.isValidColor("#007BFF");          // Hex color format
InputValidator.isNotEmpty("text");
InputValidator.isLengthValid("text", 1, 100);
InputValidator.isFutureOrToday(LocalDate.now().plusDays(1));
```

#### PermissionChecker
Permission and access control utilities for models.

```java
// Task permissions
boolean canEdit = PermissionChecker.canEditTask(task);
boolean canDelete = PermissionChecker.canDeleteTask(task);
boolean canView = PermissionChecker.canViewTask(task);
boolean canAssign = PermissionChecker.canAssignTask(task);

// Project permissions
boolean canEditProject = PermissionChecker.canEditProject(project);
boolean canDeleteProject = PermissionChecker.canDeleteProject(project);
boolean canViewProject = PermissionChecker.canViewProject(project);
```

#### DateUtils
Smart date formatting and analysis utilities.

```java
// Date checks
boolean overdue = DateUtils.isOverdue(dueDate);
boolean today = DateUtils.isToday(dueDate);
String label = DateUtils.getSmartDateLabel(dueDate); // "Today", "Tomorrow", "Overdue"
int daysUntil = DateUtils.getDaysUntil(dueDate);
```

#### CurrentUser
Session management for the logged-in user.

```java
// Get current user
User user = CurrentUser.getInstance();

// Check authentication
boolean loggedIn = CurrentUser.isLoggedIn();
boolean isAdmin = CurrentUser.isAdmin();

// Update user
CurrentUser.set(user);
CurrentUser.updateLastLogin(user);
CurrentUser.logout();

// Permission checks
boolean canCreateProjects = CurrentUser.canCreateProjects();
boolean canManageUsers = CurrentUser.canManageUsers();
```

## Database Schema

The application uses SQL Server with the following comprehensive data model:

### Core Tables

| Table | Purpose | Key Fields |
|-------|---------|-----------|
| **Users** | User accounts with authentication and roles | username, email, passwordHash, role, createdAt, lastLogin, position |
| **Projects** | Project containers for task organization | name, description, color, createdBy, createdAt |
| **Tasks** | Individual tasks with full lifecycle tracking | title, description, status, priority, dueDate, project_id, assignee_id, createdBy_id, createdAt |
| **Labels** | Reusable tags for task categorization | name, color |
| **TaskLabels** | Many-to-many mapping between tasks and labels | task_id, label_id |
| **ActivityLog** | Complete audit trail of all system actions | action, entityType, entityId, entityName, actor_id, details, timestamp |

### Database Features
- **Referential Integrity**: Foreign keys enforce data consistency
- **Timestamps**: Automatic tracking of creation and modification times
- **User Tracking**: All actions tracked with actor information for audit trail
- **Normalization**: Proper table design following database normalization principles
- **Indexes**: Optimized queries on frequently searched columns

## Data Validation Rules

The system enforces comprehensive validation on all user inputs:

### User Inputs
| Field | Rules | Error Handling |
|-------|-------|----------------|
| **Username** | 3-20 characters, alphanumeric with `_` and `-` | IllegalArgumentException |
| **Password** | Minimum 8 characters | IllegalArgumentException |
| **Email** | RFC-compliant format (user@domain.com) | IllegalArgumentException |
| **Position** | Optional field for user profile | Trimmed and cleaned |

### Task Inputs
| Field | Rules | Error Handling |
|-------|-------|----------------|
| **Title** | Required, 3-255 characters | IllegalArgumentException, SecurityException |
| **Description** | Optional, cleaned and trimmed | Automatic cleanup |
| **Status** | One of: "To Do", "In Progress", "Done" | Validated against constants |
| **Priority** | One of: "Low", "Medium", "High" | Validated against constants |
| **Due Date** | Present or future date only | DateException |
| **Assignee** | Valid user ID or null (unassigned) | SecurityException |

### Project Inputs
| Field | Rules | Error Handling |
|-------|-------|----------------|
| **Name** | Required, 3-100 characters | IllegalArgumentException, SecurityException |
| **Description** | Optional, cleaned and trimmed | Automatic cleanup |
| **Color** | Valid hex color code (#RRGGBB) | ColorValidator exception |

### Label Inputs
| Field | Rules | Error Handling |
|-------|-------|----------------|
| **Name** | Required, not empty | IllegalArgumentException |
| **Color** | Valid hex color code (#RRGGBB) | ColorValidator |

### Validation Features
- ✅ Automatic string trimming on all text inputs
- ✅ Whitespace-only input rejection
- ✅ Null-safe validation with proper error messages
- ✅ Hex color code validation (#000000 - #FFFFFF)
- ✅ Future/present date validation for due dates
- ✅ Length constraint enforcement
- ✅ RFC-5321 email format validation
- ✅ Permission-based validation for editing operations

## Permission & Access Control System

The system implements a comprehensive role-based access control (RBAC) model:

### Admin Role
**Full system access with all privileges:**

| Capability | Permission |
|-----------|-----------|
| View All Tasks | ✅ Can view every task in the system |
| View All Projects | ✅ Can view and manage all projects |
| Create Projects | ✅ Can create new projects |
| Delete Projects | ✅ Can delete any project |
| Edit All Tasks | ✅ Can edit tasks created by any user |
| Delete All Tasks | ✅ Can delete tasks created by any user |
| Assign Tasks | ✅ Can assign tasks to any user |
| Manage Users | ✅ Can change user roles and manage accounts |
| Manage Teams | ✅ Can create and manage team memberships |
| View Activity Log | ✅ Can access complete audit trail |
| Admin Panel Access | ✅ Can access admin dashboard and controls |

### User Role
**Limited access based on ownership and project membership:**

| Capability | Permission |
|-----------|-----------|
| View Own Tasks | ✅ Can view tasks assigned to them |
| View Own Projects | ✅ Can view only projects they created |
| Create Projects | ✅ Can create new projects |
| Delete Own Projects | ❌ Cannot delete projects (Admin only) |
| Edit Own Tasks | ✅ Can edit tasks they created |
| Delete Own Tasks | ✅ Can delete tasks they created |
| Complete Own Tasks | ✅ Can mark own tasks as done |
| Assign Tasks | ❌ Cannot assign tasks (Admin only) |
| Manage Users | ❌ Cannot manage other users |
| View All Projects | ❌ Can only view own projects |
| Register/Login | ✅ Can register and login |

### Permission Enforcement
- ✅ **Permission Checks**: All CRUD operations validated against user permissions
- ✅ **Security Exceptions**: Throw SecurityException on unauthorized access attempts
- ✅ **Task Ownership**: Track task creator for edit/delete authorization
- ✅ **Project Ownership**: Track project creator for permission checks
- ✅ **Granular Controls**: Fine-grained permissions for each operation
- ✅ **Admin Override**: Admins can bypass user ownership restrictions
- ✅ **Audit Logging**: All permission checks logged for compliance

## Testing

The application includes comprehensive unit tests:

```bash
mvn test
```

### Test Coverage
- ✅ **Service Layer Tests**: TaskService, ProjectService, UserService, ActivityLogService
- ✅ **Input Validation Tests**: Comprehensive validation rule testing
- ✅ **Permission Tests**: Access control and authorization verification
- ✅ **Integration Tests**: Service-to-repository integration tests
- ✅ **Utility Tests**: DateUtils, ColorValidator, PermissionChecker tests

### Running Specific Tests
```bash
# Run tests for a specific class
mvn test -Dtest=TaskServiceTest

# Run with coverage report
mvn clean test jacoco:report
```

## Configuration

Edit `application.properties` to customize application settings:

```properties
# Database Configuration
db.connection.string=jdbc:sqlserver://SERVER;databaseName=DB_NAME
db.username=sa
db.password=your_password

# Application Settings
app.title=Task Management System
app.version=1.0.0

# Default Values
task.default.priority=Medium
task.default.status=To Do
project.default.color=#007BFF

# Pagination
pagination.page.size=20
pagination.max.items=50

# Date Format
date.format=yyyy-MM-dd
datetime.format=yyyy-MM-dd HH:mm:ss

# UI Settings
ui.theme=light
ui.language=en
```

## Troubleshooting

### Connection Issues
| Issue | Solution |
|-------|----------|
| SQL Server connection fails | 1. Verify SQL Server is running<br>2. Check connection string in `application.properties`<br>3. Verify database exists<br>4. Check firewall allows port 1433 |
| "Cannot create database connection" | 1. Run `init_sql_database.sql` to create database<br>2. Verify user has CREATE TABLE permissions<br>3. Check mssql-jdbc driver version (12.6.3+) |
| Authentication failures | 1. Verify database tables created<br>2. Run init script with admin user<br>3. Check user credentials in database |

### Build Issues
| Issue | Solution |
|-------|----------|
| Java version mismatch | 1. Check: `java -version` (should be 21+)<br>2. Set JAVA_HOME: `export JAVA_HOME=/path/to/jdk21`<br>3. Verify Maven recognizes Java: `mvn -version` |
| Maven compilation fails | 1. Clear Maven cache: `mvn clean`<br>2. Update Maven: `mvn -U clean compile`<br>3. Check dependencies: `mvn dependency:tree` |
| JavaFX module not found | 1. Verify JavaFX 21 in pom.xml<br>2. Run: `mvn clean compile javafx:run` |
| Shade plugin errors | 1. Update maven-shade-plugin to 3.5.1+<br>2. Clear target: `rm -rf target/`<br>3. Rebuild: `mvn clean package` |

### Runtime Issues
| Issue | Solution |
|-------|----------|
| Application won't start | 1. Check console for detailed error messages<br>2. Verify database connection works<br>3. Check JavaFX libraries loaded correctly<br>4. Review application.properties |
| Tasks not loading | 1. Verify database tables exist<br>2. Check user has SELECT permissions<br>3. Review logs for SQL errors<br>4. Ensure projects exist in database |
| Login not working | 1. Verify user credentials in database<br>2. Check password hashing algorithm<br>3. Run: `SELECT * FROM Users` to verify data<br>4. Check user role assignments |
| Permission denied errors | 1. Verify user role (Admin vs User)<br>2. Check task/project ownership<br>3. Review PermissionChecker logic<br>4. Check CurrentUser session state |

### Performance Issues
| Issue | Solution |
|-------|----------|
| Slow task loading | 1. Check database indexes on task_id<br>2. Verify query performance in TaskRepository<br>3. Limit query results with pagination<br>4. Check SQL Server resource usage |
| High memory usage | 1. Review observable list sizes<br>2. Implement result pagination<br>3. Clear cache periodically<br>4. Monitor listener count |
| UI freezing | 1. Move long operations to background threads<br>2. Use JavaFX Task for async operations<br>3. Limit UI update frequency<br>4. Profile with JProfiler or YourKit |

## Roadmap & Future Enhancements

### Planned Features (v1.1)
- [ ] **Comments System**: Add inline comments and discussions on tasks
- [ ] **Task Dependencies**: Define task dependencies and blocking relationships
- [ ] **Subtasks**: Break down tasks into smaller subtasks
- [ ] **Time Tracking**: Track time spent on tasks and generate reports
- [ ] **Email Notifications**: Notify users of task assignments and due date reminders

### Planned Features (v1.2)
- [ ] **File Attachments**: Attach documents and files to tasks
- [ ] **Calendar View**: Visualize tasks on a calendar
- [ ] **Recurring Tasks**: Create recurring or repeating tasks
- [ ] **Task Templates**: Create and reuse task templates
- [ ] **Team Workspaces**: Organize projects into team workspaces

### Planned Features (v2.0)
- [ ] **REST API**: Expose functionality via REST API for third-party integration
- [ ] **Mobile App**: Companion mobile application for iOS and Android
- [ ] **Real-time Collaboration**: Real-time updates and live collaboration
- [ ] **Advanced Reporting**: PDF/Excel export with customizable reports
- [ ] **Dashboard Widgets**: Customizable dashboard with drag-and-drop widgets
- [ ] **Dark Theme**: Dark mode UI support
- [ ] **Multi-language**: Internationalization support
- [ ] **Cloud Sync**: Cloud backup and sync capabilities

### Performance & Optimization (Ongoing)
- [ ] Database query optimization and caching
- [ ] Lazy loading for large datasets
- [ ] Connection pooling
- [ ] Indexed searches

## Version History

### v1.0.0 (January 2026) - Initial Release
**Status**: Production Ready

#### Core Features
- ✅ Complete user authentication and authorization system
- ✅ Task management with full CRUD operations
- ✅ Project organization and management
- ✅ Role-based access control (Admin and User roles)
- ✅ Activity logging and audit trail
- ✅ Task labels with custom colors
- ✅ Comprehensive input validation
- ✅ Permission and access control system

#### UI & Controllers
- ✅ 20 specialized JavaFX controllers for different views
- ✅ Login and registration screens
- ✅ Dashboard with statistics and quick access
- ✅ Task list and detail views with editing
- ✅ Project list and detail views
- ✅ Admin panel for user management
- ✅ Multiple view types (Table, Kanban, List)
- ✅ User profile management interface

#### Services & Architecture
- ✅ 6 core services (Task, Project, User, Label, Auth, ActivityLog)
- ✅ 4 repositories with base abstraction
- ✅ 9 domain models with business logic
- ✅ Utility classes for validation, permissions, dates
- ✅ Layered architecture (Controller → Service → Repository → Database)
- ✅ Constants and messaging system

#### Database
- ✅ SQL Server integration with JDBC driver
- ✅ Comprehensive schema with 6 main tables
- ✅ Activity logging tables for audit trail
- ✅ Proper relationships and constraints
- ✅ Database initialization script

#### Testing & Quality
- ✅ Unit tests for services and utilities
- ✅ Input validation test coverage
- ✅ Permission check tests
- ✅ Integration tests
- ✅ Maven build configuration

### Known Limitations
- Single-user session (one user per application instance)
- No real-time collaboration features
- No offline mode or local caching
- Limited to desktop platform (JavaFX)
- No mobile application support
- No REST API for third-party integration

### Technical Details
- **Java Version**: 21 (LTS)
- **JavaFX Version**: 21
- **Maven Version**: 3.8+
- **SQL Server**: 2019 or later
- **Build Time**: ~10-15 seconds
- **Package Size**: ~50-100 MB (with dependencies)
- **Memory Usage**: ~200-300 MB at runtime

---

## Contributing

Contributions are welcome! To contribute:

1. **Fork the repository**: Create your own copy
2. **Create a feature branch**: `git checkout -b feature/your-feature-name`
3. **Make your changes**: Implement your feature or bugfix
4. **Write tests**: Add tests for new functionality
5. **Commit changes**: `git commit -m "Add your commit message"`
6. **Push to branch**: `git push origin feature/your-feature-name`
7. **Create Pull Request**: Submit a PR with detailed description

### Contribution Guidelines
- Follow existing code style and conventions
- Add unit tests for new features
- Update documentation for significant changes
- Write clear commit messages
- Ensure all tests pass before submitting PR
- Update README if adding new user-facing features

## License

This project is open source and available under the **MIT License**.

```
MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

## Support

For issues, questions, or suggestions:

### Getting Help
1. **Documentation**: Review the README and inline code documentation
2. **Troubleshooting**: Check the [Troubleshooting](#troubleshooting) section
3. **API Docs**: Review the [Service Layer Architecture](#service-layer-architecture) section
4. **Code Examples**: Check [Service Layer Architecture](#service-layer-architecture) for usage examples

### Reporting Issues
1. **Search Existing Issues**: Check if issue already reported
2. **Provide Details**: Include:
   - Java version (`java -version`)
   - Maven version (`mvn -version`)
   - SQL Server version
   - Steps to reproduce
   - Error messages and stack traces
   - Screenshots if UI-related
3. **Use Issue Template**: Follow the provided issue template
4. **Be Specific**: Provide minimal reproducible example

### Feature Requests
1. **Check Roadmap**: Review [Roadmap & Future Enhancements](#roadmap--future-enhancements)
2. **Describe Use Case**: Explain why feature is needed
3. **Propose Solution**: Suggest implementation approach
4. **Vote on Features**: Support existing feature requests

### Contact & Community
- **Project Owner**: [Your Name/Organization]
- **Email**: [Contact Email]
- **Documentation**: See sections above
- **Code Quality**: Following Java best practices and SOLID principles

---

## Quick Reference

### Default Test Credentials
```
Admin User:
  Username: admin
  Password: admin12345
  Role: Admin
  
Regular User:
  Username: john
  Password: password123
  Role: User
  
Regular User:
  Username: jane
  Password: password123
  Role: User
```

### Maven Command Cheatsheet
```bash
# Build commands
mvn clean                           # Clean build artifacts
mvn compile                         # Compile source code
mvn test                            # Run all tests
mvn clean compile javafx:run        # Build and run application
mvn package                         # Create JAR package
mvn clean package                   # Clean build and package

# Project info
mvn help:describe -Dplugin=groupId:artifactId
mvn dependency:tree                 # Show dependency tree
mvn dependency:analyze              # Analyze unused dependencies

# Documentation
mvn javadoc:javadoc                # Generate JavaDoc
```

### Important File Locations
| File | Purpose |
|------|---------|
| `pom.xml` | Maven configuration and dependencies |
| `application.properties` | Application settings |
| `src/main/resources/com/taskmanagement/sql/init_sql_database.sql` | Database schema and sample data |
| `src/main/java/com/taskmanagement/database/DBConnection.java` | Database connection settings |
| `src/main/resources/com/taskmanagement/css/style.css` | Application styling |

---

**Last Updated**: January 2026  
**Status**: Production Ready  
**Maintainability**: High - Well-documented, tested, and structured  
**Community**: Open source - contributions welcome!



