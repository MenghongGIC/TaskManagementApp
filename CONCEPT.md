# Task Management System - Architecture & Design Principles

## Table of Contents
1. [MVC + Service & Repository Architecture](#mvc--service--repository-architecture)
2. [SOLID Principles](#solid-principles)
3. [DRY (Don't Repeat Yourself)](#dry-dont-repeat-yourself)
4. [KISS (Keep It Simple, Stupid)](#kiss-keep-it-simple-stupid)
5. [Object-Oriented Programming (OOP)](#object-oriented-programming)
6. [File Processing & Database Interaction](#file-processing--database-interaction)

---

## MVC + Service & Repository Architecture

### Overview
The Task Management System follows a **layered architecture** combining **MVC (Model-View-Controller)** with **Service and Repository patterns** to ensure clear separation of concerns, maintainability, and testability.

### Architecture Layers

#### 1. View Layer (JavaFX Controllers)
**Responsibility**: Handle user interactions and display data
**Location**: `src/main/java/com/taskmanagement/controller/`
**Components**: 20+ specialized JavaFX controllers

```
Controllers (View Layer)
├── LoginController.java          - Authentication UI
├── DashboardController.java      - Main dashboard display
├── TaskDetailController.java     - Task display & editing
├── ProjectListController.java    - Project browsing
├── AdminController.java          - Admin panel
└── [17 more specialized controllers]
```

**Key Characteristics:**
- Handle FXML UI loading and initialization
- Capture user input and events
- Delegate business logic to services
- Update views based on model changes
- NO direct database access
- NO business logic implementation

**Example:**
```java
@FXML
public void initialize() {
    // Setup UI components
    setupComboBoxes();
    loadUsers();
    setupButtonHandlers();
    setViewMode();
}

private void handleSave() {
    // Delegate to service
    taskService.updateTask(task);
    if (onSaveCallback != null) onSaveCallback.run();
}
```

#### 2. Service Layer (Business Logic)
**Responsibility**: Implement business logic, validation, and orchestration
**Location**: `src/main/java/com/taskmanagement/service/`
**Components**: 6 core services

```
Services (Business Logic Layer)
├── TaskService.java              - Task CRUD & filtering
├── ProjectService.java           - Project management
├── UserService.java              - User operations
├── ActivityLogService.java       - Audit logging
├── LabelService.java             - Label management
└── AuthService.java              - Authentication facade
```

**Key Responsibilities:**
- Validate input data
- Enforce business rules
- Perform calculations and analytics
- Control transactions
- Manage permissions and security
- Coordinate multiple repositories

**Example: TaskService**
```java
public class TaskService {
    private final TaskRepository taskRepository;
    
    public Task createTask(String title, String description, Project project) {
        // Validation
        validateUserLoggedIn();
        validateTaskTitle(title);
        validateProjectExists(project);
        
        // Business logic
        User currentUser = CurrentUser.getInstance();
        Task task = new Task(title.trim(), project, currentUser);
        task.setDescription(description);
        
        // Delegate to repository
        return taskRepository.save(task);
    }
    
    public List<Task> getTasksByStatus(String status) {
        return filterVisibleTasks(
            getAllTasks().stream()
                .filter(t -> matchesStatus(t, status))
                .collect(Collectors.toList())
        );
    }
}
```

**Features:**
- Input validation with clear error messages
- Permission checking via `PermissionChecker`
- Stream API for efficient filtering and sorting
- Layered validation (user logged in → data valid → permission check)

#### 3. Repository Layer (Data Access)
**Responsibility**: Encapsulate database operations
**Location**: `src/main/java/com/taskmanagement/repository/`
**Components**: 4 repositories + base class

```
Repositories (Data Access Layer)
├── BaseRepository.java           - Abstract base with shared functionality
├── TaskRepository.java           - Task CRUD operations
├── ProjectRepository.java        - Project CRUD operations
└── UserRepository.java           - User CRUD operations
```

**Key Responsibilities:**
- Execute SQL queries
- Map database rows to objects
- Handle database exceptions
- Provide CRUD operations
- Encapsulate SQL details

**BaseRepository Pattern (DRY Principle):**
```java
public class BaseRepository {
    protected Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }
}
```

All repositories extend `BaseRepository` to avoid repeating connection logic.

**Example: TaskRepository**
```java
public class TaskRepository extends BaseRepository {
    
    private static final String SQL_INSERT = """
        INSERT INTO Tasks (title, description, status, priority, due_date, 
                          project_id, assignee_id, created_by)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
    
    public Task save(Task task) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            // ... more parameter binding
            
            pstmt.executeUpdate();
            // Get generated ID from database
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    task.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(ERR_SAVE + task.getTitle(), e);
        }
        return task;
    }
}
```

#### 4. Model Layer (Domain Objects)
**Responsibility**: Represent business entities
**Location**: `src/main/java/com/taskmanagement/model/`
**Components**: 9 domain classes + enums

```
Models (Domain Objects)
├── User.java                    - User entity with roles
├── Task.java                    - Task entity with properties
├── Project.java                 - Project container
├── Label.java                   - Task label/tag
├── ActivityLog.java             - Audit log entry
├── Role.java (enum)             - User roles (Admin, User)
├── Status.java (enum)           - Task statuses
├── Priority.java (enum)         - Task priorities
└── TaskStatus.java              - Task status tracking
```

**Key Features:**
- Pure data containers with getters/setters
- Business logic helpers (e.g., `isOverdue()`, `canEdit()`)
- Enums for fixed value sets
- Proper `equals()` and `hashCode()` implementation

**Example Model - Task:**
```java
public class Task {
    private Long id;
    private String title;
    private String description;
    private String status = "To Do";
    private String priority = "Medium";
    private LocalDate dueDate;
    private Project project;
    private User assignee;
    private User createdBy;
    private LocalDateTime createdAt = LocalDateTime.now();
    private Set<Label> labels = new HashSet<>();
    
    // Business logic helpers
    public boolean isOverdue() { 
        return DateUtils.isOverdue(dueDate); 
    }
    
    public boolean isDueToday() { 
        return DateUtils.isToday(dueDate); 
    }
    
    public boolean isCompleted() { 
        return "Done".equalsIgnoreCase(status); 
    }
    
    public boolean canEdit() {
        return PermissionChecker.canEditTask(this);
    }
}
```

#### 5. Database Layer (SQL Server)
**Responsibility**: Persistent data storage
**Location**: Database server (SQL Server 2019+)
**Connection**: `src/main/java/com/taskmanagement/database/DBConnection.java`

```
Database Tables
├── Users              - User accounts with authentication
├── Projects           - Project containers
├── Tasks              - Individual tasks
├── Labels             - Tag definitions
├── TaskLabels         - Task-Label many-to-many mapping
└── ActivityLog        - Audit trail
```

### Data Flow Diagram

```
User Interaction
    ↓
[View Layer - Controller]
    ↓ delegates to
[Service Layer - Business Logic]
    ↓ validates & coordinates
[Repository Layer - Data Access]
    ↓ executes SQL
[Database Layer - SQL Server]
    ↓ returns ResultSet
[Repository Layer - Maps to Objects]
    ↓ returns domain objects
[Service Layer - Processes Results]
    ↓ returns to
[View Layer - Updates UI]
    ↓
Display to User
```

### Advantages of This Architecture

1. **Separation of Concerns**: Each layer has single responsibility
2. **Testability**: Services can be tested independently of UI
3. **Reusability**: Services can be used by multiple controllers
4. **Maintainability**: Changes isolated to specific layers
5. **Scalability**: Easy to add new features without affecting existing code
6. **Performance**: Efficient database queries with prepared statements
7. **Security**: Permission checks in service layer, input validation

---

## SOLID Principles

### S - Single Responsibility Principle

**Definition**: A class should have one, and only one, reason to change.

**Implementation in Project:**

#### 1. Repositories - Data Access Only
```java
// UserRepository ONLY handles database operations
public class UserRepository extends BaseRepository {
    public User save(User user) { /* INSERT */ }
    public User findById(Long id) { /* SELECT */ }
    public User findByUsername(String username) { /* SELECT */ }
    public List<User> findAll() { /* SELECT */ }
    public void delete(Long id) { /* DELETE */ }
}

// Does NOT contain validation, business logic, or security checks
```

#### 2. Services - Business Logic Only
```java
// UserService ONLY handles business logic
public class UserService {
    public User register(String username, String email, String password) {
        // Validation
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException(ERR_USERNAME_REQUIRED);
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(ERR_PASSWORD_TOO_SHORT);
        }
        
        // Business logic
        User newUser = new User();
        newUser.setUsername(username.trim());
        newUser.setEmail(email);
        newUser.setRole(Role.USER);
        newUser.setCreatedAt(LocalDateTime.now());
        
        // Delegates to repository
        return userRepository.save(newUser);
    }
}

// Does NOT contain SQL, UI code, or utility functions
```

#### 3. Validators - Input Validation Only
```java
// InputValidator ONLY validates user input
public class InputValidator {
    public static boolean isValidEmail(String email) { /* ... */ }
    public static boolean isValidUsername(String username) { /* ... */ }
    public static boolean isValidPassword(String password) { /* ... */ }
    public static String validateTaskTitle(String title) { /* ... */ }
    // Does NOT contain business logic or database access
}
```

#### 4. PermissionChecker - Authorization Only
```java
// PermissionChecker ONLY checks permissions
public class PermissionChecker {
    public static boolean canEditProject(Project project) { /* ... */ }
    public static boolean canDeleteTask(Task task) { /* ... */ }
    // Does NOT contain business logic or data access
}
```

#### 5. Controllers - UI Interaction Only
```java
// TaskDetailController ONLY handles UI interactions
public class TaskDetailController {
    @FXML
    private void handleSave() {
        // Validate input
        if (titleField.getText().isEmpty()) {
            showError(MSG_TITLE_EMPTY);
            return;
        }
        
        // Delegate to service
        try {
            taskService.updateTask(task);
            showSuccess(MSG_SAVED);
            if (onSaveCallback != null) onSaveCallback.run();
        } catch (Exception e) {
            showError(MSG_SAVE_FAILED);
        }
    }
    
    // Does NOT contain SQL, business logic, or utility functions
}
```

**Benefits:**
- Each class has one reason to change
- Easier to test and debug
- Code is more focused and readable
- Changes to one aspect don't affect others

---

### O - Open/Closed Principle

**Definition**: Software should be open for extension but closed for modification.

**Implementation in Project:**

#### 1. Abstract Base Repository
```java
// BaseRepository is CLOSED for modification, OPEN for extension
public abstract class BaseRepository {
    protected Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }
}

// Each specific repository EXTENDS this without modifying it
public class TaskRepository extends BaseRepository {
    // Uses getConnection() from base class
    public Task save(Task task) {
        try (Connection conn = getConnection(); ...) {
            // Implementation
        }
    }
}

public class ProjectRepository extends BaseRepository {
    // Reuses getConnection() from base class
    public Project save(Project project) {
        try (Connection conn = getConnection(); ...) {
            // Implementation
        }
    }
}
```

#### 2. Singleton Pattern - DBConnection
```java
// DBConnection is CLOSED, can't modify connection logic
public class DBConnection {
    private static final DBConnection INSTANCE = new DBConnection();
    
    private DBConnection() {
        connect();
    }
    
    public static DBConnection getInstance() {
        return INSTANCE;
    }
}

// Yet OPEN for use by all repositories
// Any new repository can use: DBConnection.getInstance().getConnection()
```

#### 3. Enum Extensions for Values
```java
// Status enum is CLOSED for modification
public enum Status {
    BACKLOG("Backlog", "backlog"),
    TODO("To Do", "todo"),
    IN_PROGRESS("In Progress", "in-progress"),
    BLOCKED("Blocked", "blocked"),
    DONE("Done", "done");
    
    private final String displayName;
    private final String cssClass;
}

// Yet OPEN for extension through fromString() and getStyleClass()
Status status = Status.fromString("In Progress");
String styleClass = status.getStyleClass();
```

**Benefits:**
- New repositories can be added without modifying BaseRepository
- New enums can be added for different value sets
- Existing code continues to work without changes

---

### L - Liskov Substitution Principle

**Definition**: Subclasses should be substitutable for their base classes.

**Implementation in Project:**

#### 1. All Repositories Are Interchangeable
```java
// All repositories extend BaseRepository
BaseRepository taskRepo = new TaskRepository();
BaseRepository projectRepo = new ProjectRepository();
BaseRepository userRepo = new UserRepository();

// They can be used interchangeably
List<? extends BaseRepository> repos = List.of(
    new TaskRepository(),
    new ProjectRepository(),
    new UserRepository()
);

// All can call getConnection() the same way
for (BaseRepository repo : repos) {
    Connection conn = repo.getConnection();
}
```

#### 2. Role-Based Polymorphism
```java
// Roles are enum-based, behave consistently
Role adminRole = Role.ADMIN;
Role userRole = Role.USER;

// Both can be used interchangeably in permission checks
boolean canManage = role.ManageUsers();
boolean canCreate = role.CreateProjects();
```

**Benefits:**
- Code is flexible and doesn't depend on concrete types
- Easy to swap implementations
- Better abstraction and polymorphism

---

### I - Interface Segregation Principle

**Definition**: Classes should depend on specific interfaces, not general ones.

**Implementation in Project:**

#### 1. Service Layer Segregation
```java
// Clients depend on specific service interfaces
// Rather than one monolithic "Service" interface

// TaskService provides ONLY task operations
public class TaskService {
    public Task createTask(...) { }
    public Task updateTask(Task task) { }
    public void deleteTask(Long taskId) { }
    public List<Task> getAllTasks() { }
    public List<Task> getTasksByStatus(String status) { }
}

// ProjectService provides ONLY project operations
public class ProjectService {
    public Project createProject(...) { }
    public Project updateProject(Project project) { }
    public void deleteProject(Long projectId) { }
    public List<Project> getAllProjects() { }
}

// Clients only depend on what they need
public class TaskDetailController {
    private TaskService taskService;
    private UserService userService;
    // Uses ONLY TaskService and UserService, not "AllServices"
}
```

#### 2. Validator Segregation
```java
// InputValidator provides ONLY validation methods
// Not general utility methods

public class InputValidator {
    public static boolean isValidEmail(...) { }
    public static boolean isValidPassword(...) { }
    public static String validateTaskTitle(...) { }
    // NOT: calculateDueDateReminder() or formatCurrency()
}

// DateUtils handles ONLY date operations
public class DateUtils {
    public static String formatDate(LocalDate date) { }
    public static boolean isToday(LocalDate date) { }
    public static String getRelativeTime(LocalDateTime dateTime) { }
    // NOT: validateDate() or parseUserInput()
}

// PermissionChecker handles ONLY permissions
public class PermissionChecker {
    public static boolean canEditProject(...) { }
    public static boolean canDeleteTask(...) { }
    // NOT: loadUserPreferences() or calculateBonus()
}
```

**Benefits:**
- Dependencies are minimal and focused
- Classes aren't forced to depend on methods they don't use
- More flexible and maintainable code

---

### D - Dependency Inversion Principle

**Definition**: High-level modules should depend on abstractions, not low-level modules.

**Implementation in Project:**

#### 1. Services Depend on Repositories (Abstraction)
```java
// TaskService DEPENDS ON TaskRepository (abstraction)
public class TaskService {
    private final TaskRepository taskRepository;
    
    public TaskService() {
        this.taskRepository = new TaskRepository();
    }
    
    public Task createTask(String title, String description, Project project) {
        // Validation and business logic
        // Then delegates to repository abstraction
        return taskRepository.save(task);
    }
}

// If TaskRepository implementation changes, TaskService doesn't
```

#### 2. Controllers Depend on Services (Abstraction)
```java
// TaskDetailController DEPENDS ON TaskService (abstraction)
public class TaskDetailController {
    private TaskService taskService;
    private UserService userService;
    
    public TaskDetailController() {
        this.taskService = new TaskService();
        this.userService = new UserService();
    }
    
    private void handleSave() {
        // Uses service abstraction, not database directly
        taskService.updateTask(task);
    }
}

// If TaskService implementation changes, controller still works
```

#### 3. Repositories Depend on Connection Abstraction
```java
// All repositories depend on DBConnection (abstraction)
public class BaseRepository {
    protected Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }
}

// Specific repositories use this abstraction
public class TaskRepository extends BaseRepository {
    public Task save(Task task) {
        try (Connection conn = getConnection(); // Uses abstraction
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, ...)) {
            // ...
        }
    }
}

// If database connection changes, repositories don't
```

**Benefits:**
- High-level modules (controllers) don't depend on low-level (repositories)
- Both depend on abstractions (services, connection factory)
- Easy to swap implementations
- Better testability with mock services

---

## DRY (Don't Repeat Yourself)

**Definition**: Every piece of knowledge must have a single, unambiguous, authoritative representation.

### 1. BaseRepository - Connection Reuse

**Problem (Without DRY):**
```java
// Each repository would repeat this code
public class TaskRepository {
    public Task save(Task task) {
        try (Connection conn = DriverManager.getConnection(...)) { }
    }
    public List<Task> findAll() {
        try (Connection conn = DriverManager.getConnection(...)) { }
    }
}

public class ProjectRepository {
    public Project save(Project project) {
        try (Connection conn = DriverManager.getConnection(...)) { }
    }
    public List<Project> findAll() {
        try (Connection conn = DriverManager.getConnection(...)) { }
    }
}
// Connection logic repeated in EVERY repository!
```

**Solution (With DRY):**
```java
// Abstract base repository centralizes connection logic
public class BaseRepository {
    protected Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }
}

// All repositories extend and reuse this
public class TaskRepository extends BaseRepository {
    public Task save(Task task) {
        try (Connection conn = getConnection(); ...) { } // Reused!
    }
}

public class ProjectRepository extends BaseRepository {
    public Project save(Project project) {
        try (Connection conn = getConnection(); ...) { } // Reused!
    }
}
```

### 2. Singleton DBConnection - Single Instance

**Problem (Without DRY):**
```java
// Each repository would create new connections
public class TaskRepository {
    public List<Task> findAll() {
        Connection conn = DriverManager.getConnection(...);
        // NEW connection created!
    }
}

public class ProjectRepository {
    public List<Project> findAll() {
        Connection conn = DriverManager.getConnection(...);
        // ANOTHER new connection created!
    }
}
// Multiple connection objects, redundant instantiation
```

**Solution (With DRY):**
```java
// Singleton ensures single managed instance
public class DBConnection {
    private static final DBConnection INSTANCE = new DBConnection();
    
    private Connection connection;
    
    private DBConnection() {
        connect();
    }
    
    public Connection getConnection() {
        // Validates and reuses same connection
        if (isConnectionInvalid()) {
            connect();
        }
        return connection;
    }
    
    public static DBConnection getInstance() {
        return INSTANCE; // Always returns SAME instance
    }
}

// All repositories use same connection management
BaseRepository taskRepo = new TaskRepository();
BaseRepository projectRepo = new ProjectRepository();
// Both use: DBConnection.getInstance().getConnection()
```

### 3. Constants - Centralized Values

**Problem (Without DRY):**
```java
// Status strings repeated throughout code
public class TaskService {
    public List<Task> getDoneTask() {
        return tasks.filter(t -> "Done".equals(t.getStatus()));
    }
}

public class TaskRepository {
    private static final String SQL_UPDATE_STATUS = 
        "UPDATE Tasks SET status = 'Done' WHERE id = ?";
}

public class TaskDetailController {
    private static final String[] STATUS_OPTIONS = {"To Do", "In Progress", "Done"};
}
// String "Done" repeated 3+ times!
```

**Solution (With DRY):**
```java
// Centralize in constants
public class AppConstants {
    public static final String STATUS_TODO = "To Do";
    public static final String STATUS_IN_PROGRESS = "In Progress";
    public static final String STATUS_DONE = "Done";
}

// Reuse everywhere
public class TaskService {
    public List<Task> getCompletedTasks() {
        return tasks.filter(t -> AppConstants.STATUS_DONE.equals(t.getStatus()));
    }
}

public class TaskDetailController {
    private static final String[] STATUS_OPTIONS = {
        AppConstants.STATUS_TODO, 
        AppConstants.STATUS_IN_PROGRESS, 
        AppConstants.STATUS_DONE
    };
}
// Single source of truth!
```

### 4. Utility Classes - Shared Functionality

**Problem (Without DRY):**
```java
// Date formatting repeated in multiple classes
public class TaskDetailController {
    public void loadTask(Task task) {
        String formatted = task.getDueDate().format(
            DateTimeFormatter.ofPattern("MMM d, yyyy")
        );
        dueDateLabel.setText(formatted);
    }
}

public class DashboardController {
    public void loadTasks(List<Task> tasks) {
        for (Task task : tasks) {
            String formatted = task.getDueDate().format(
                DateTimeFormatter.ofPattern("MMM d, yyyy")
            );
            // Use formatted date
        }
    }
}
// Date format pattern repeated!
```

**Solution (With DRY):**
```java
// Centralize in utility class
public class DateUtils {
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("MMM d, yyyy");
    
    public static String formatDate(LocalDate date) {
        if (date == null) return "No date";
        return date.format(DATE_FORMATTER);
    }
}

// Reuse everywhere
public class TaskDetailController {
    public void loadTask(Task task) {
        dueDateLabel.setText(DateUtils.formatDate(task.getDueDate()));
    }
}

public class DashboardController {
    public void loadTasks(List<Task> tasks) {
        for (Task task : tasks) {
            taskDateLabel.setText(DateUtils.formatDate(task.getDueDate()));
        }
    }
}
```

### 5. Validation Logic - Centralized Rules

**Problem (Without DRY):**
```java
// Validation repeated in multiple services
public class UserService {
    public User register(String username, ...) {
        if (username == null || username.length() < 3 || username.length() > 20) {
            throw new IllegalArgumentException("Invalid username");
        }
    }
}

public class UserController {
    public void handleRegister(String username) {
        if (username == null || username.length() < 3 || username.length() > 20) {
            showError("Invalid username");
            return;
        }
    }
}
// Validation rule repeated!
```

**Solution (With DRY):**
```java
// Centralize in validator
public class InputValidator {
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final Pattern USERNAME_PATTERN = 
        Pattern.compile("^[A-Za-z0-9_-]{3,20}$");
    
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }
}

// Reuse everywhere
public class UserService {
    public User register(String username, ...) {
        if (!InputValidator.isValidUsername(username)) {
            throw new IllegalArgumentException("Invalid username");
        }
    }
}

public class UserController {
    public void handleRegister(String username) {
        if (!InputValidator.isValidUsername(username)) {
            showError("Invalid username");
            return;
        }
    }
}
```

### 6. SQL Queries - Prepared Statements

**Example of DRY SQL:**
```java
public class TaskRepository extends BaseRepository {
    // Define each query ONCE as a constant
    private static final String SQL_INSERT = """
        INSERT INTO Tasks (title, description, status, priority, due_date, 
                          project_id, assignee_id, created_by)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
    
    private static final String SQL_SELECT_ALL = """
        SELECT * FROM Tasks ORDER BY created_at DESC
        """;
    
    private static final String SQL_UPDATE = """
        UPDATE Tasks SET title = ?, description = ?, status = ?, 
                        priority = ?, due_date = ?
        WHERE id = ?
        """;
    
    // Reuse these constants throughout the repository
    public Task save(Task task) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, ...)) {
            // Use SQL_INSERT constant
        }
    }
}
```

**Benefits of DRY:**
- Single source of truth for values and logic
- Changes made once affect all usages
- Reduced bugs from inconsistencies
- Easier maintenance and testing

---

## KISS (Keep It Simple, Stupid)

**Definition**: Most systems work best if they are kept simple rather than made complex.

### 1. Simple Method Names & Clear Intent

**Complex (Bad):**
```java
public List<Task> getTsksByStAndPr(String s, String p) {
    // What do s and p mean?
}

public void prcTsk(Task t) {
    // What does "prc" mean? Process?
}
```

**Simple (Good):**
```java
// Clear method name and parameters
public List<Task> getTasksByStatusAndPriority(String status, String priority) {
    // Clear what this does
}

public void updateTask(Task task) {
    // Obvious intent
}
```

### 2. Avoid Over-Engineering

**Complex (Bad):**
```java
// Over-engineered with unnecessary abstraction
public interface DataProvider {
    <T> List<T> getData(String query);
}

public class GenericRepository<T> implements DataProvider {
    public <T> List<T> getData(String query) {
        // Generic implementation
    }
}

public class TaskRepositoryAdapter extends GenericRepository<Task> {
    // Adapter pattern just to access tasks
}
```

**Simple (Good):**
```java
// Direct, straightforward implementation
public class TaskRepository extends BaseRepository {
    public List<Task> findAll() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            
            List<Task> tasks = new ArrayList<>();
            while (rs.next()) {
                tasks.add(mapRowToTask(rs));
            }
            return tasks;
        }
    }
}
```

### 3. Minimal Method Complexity

**Complex (Bad):**
```java
// Long method doing multiple things
public void saveTask(Task task, Project project, User user, LocalDate dueDate) {
    // Validate task
    if (task == null) throw new Exception("Task null");
    if (task.getTitle().length() < 3) throw new Exception("Title too short");
    
    // Update project
    if (project != null) {
        project.setLastModified(LocalDateTime.now());
        projectRepository.update(project);
    }
    
    // Update user
    user.setTasksAssigned(user.getTasksAssigned() + 1);
    userRepository.update(user);
    
    // Handle due date
    if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
        throw new Exception("Date in past");
    }
    
    // Finally save task
    taskRepository.save(task);
    
    // Log activity
    ActivityLogService.logTaskCreated(task.getId(), task.getTitle());
}
```

**Simple (Good):**
```java
// Short, focused methods with clear responsibility
public Task createTask(String title, String description, Project project) {
    // Single responsibility: create task
    validateUserLoggedIn();
    validateTaskTitle(title);
    validateProjectExists(project);
    
    User currentUser = CurrentUser.getInstance();
    Task task = new Task(title.trim(), project, currentUser);
    task.setDescription(description);
    
    return taskRepository.save(task);
}

// Separate methods for related concerns
public void assignTaskToUser(Long taskId, Long userId) {
    // Single responsibility: assign task
    Task task = getTaskOrThrow(taskId);
    validateTaskPermission(task, "assign");
    
    User user = new User();
    user.setId(userId);
    task.setAssignee(user);
    
    taskRepository.update(task);
}
```

### 4. Clear Error Handling

**Complex (Bad):**
```java
try {
    task = taskRepository.save(task);
} catch (Exception e) {
    e.printStackTrace();
    // What happened? Why did it fail?
}
```

**Simple (Good):**
```java
// Clear error messages
private static final String ERR_SAVE = "Error saving task: ";

public Task save(Task task) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, ...)) {
        // ...
    } catch (SQLException e) {
        // Clear what failed and why
        throw new RuntimeException(ERR_SAVE + task.getTitle(), e);
    }
}
```

### 5. Straightforward Control Flow

**Complex (Bad):**
```java
// Nested conditions and unclear logic
public boolean canDeleteTask(Task task) {
    if (!CurrentUser.isLoggedIn()) {
        return false;
    } else {
        if (CurrentUser.isAdmin()) {
            return true;
        } else {
            if (task.getCreatedBy() != null) {
                if (task.getCreatedBy().getId().equals(CurrentUser.getId())) {
                    return true;
                }
            }
        }
    }
    return false;
}
```

**Simple (Good):**
```java
// Early returns, clear conditions
public static boolean canDeleteTask(Task task) {
    if (!CurrentUser.isLoggedIn()) {
        return false;
    }
    
    // Admin can delete anything
    if (CurrentUser.isAdmin()) {
        return true;
    }
    
    // User can delete own tasks
    if (task.getCreatedBy() != null && 
        task.getCreatedBy().getId().equals(CurrentUser.getId())) {
        return true;
    }
    
    return false;
}
```

### 6. Stream API - Simple Filtering

**Complex (Bad):**
```java
// Traditional nested loops for filtering
public List<Task> getHighPriorityTasks() {
    List<Task> result = new ArrayList<>();
    for (Task task : getAllTasks()) {
        if (task.getPriority().equals("High")) {
            if (!task.isCompleted()) {
                if (canViewTask(task)) {
                    result.add(task);
                }
            }
        }
    }
    return result;
}
```

**Simple (Good):**
```java
// Stream API - declarative and clear
public List<Task> getHighPriorityTasks() {
    return getAllTasks().stream()
        .filter(t -> "High".equals(t.getPriority()))
        .filter(t -> !t.isCompleted())
        .filter(this::canViewTask)
        .collect(Collectors.toList());
}
```

**Benefits of KISS:**
- Easier to understand and maintain
- Fewer bugs from complex logic
- Faster to develop new features
- Better for team collaboration
- Easier to test and debug

---

## Object-Oriented Programming

The Task Management System leverages four core OOP concepts:

### 1. Encapsulation

**Definition**: Bundling data and methods together while hiding internal details.

#### Private Fields with Public Getters/Setters

```java
public class User {
    // Private fields - hidden from outside
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private Role role = Role.USER;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    
    // Public getters - controlled access
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    
    // Public setters - controlled modification
    public void setUsername(String username) { 
        this.username = username; 
    }
    public void setRole(Role role) { 
        this.role = role != null ? role : Role.USER; 
    }
    
    // Private methods - implementation details hidden
    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username required");
        }
    }
}
```

**Benefits:**
- Internal representation can change without affecting external code
- Validation can be added to setters
- Read-only fields (only getter, no setter)
- Controlled access to sensitive data

#### Protected Methods in Base Classes

```java
public class BaseRepository {
    // Protected - accessible by subclasses only
    protected Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }
}

public class TaskRepository extends BaseRepository {
    public Task save(Task task) {
        // Can access protected method
        try (Connection conn = getConnection(); ...) {
            // Implementation
        }
    }
}
```

### 2. Inheritance

**Definition**: Creating a new class based on an existing class, inheriting its properties and methods.

#### Repository Inheritance

```java
// Base class with common functionality
public class BaseRepository {
    protected Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }
}

// Specialized repositories inherit and extend
public class TaskRepository extends BaseRepository {
    public Task save(Task task) {
        try (Connection conn = getConnection(); ...) {
            // Uses inherited getConnection()
        }
    }
}

public class ProjectRepository extends BaseRepository {
    public Project save(Project project) {
        try (Connection conn = getConnection(); ...) {
            // Reuses same getConnection()
        }
    }
}
```

**Benefit:** Common code (connection management) is centralized.

#### Enum Inheritance for Behavioral Types

```java
// Role enum - different behaviors
public enum Role {
    ADMIN("Admin"),
    USER("User");
    
    private final String displayName;
    
    // Each role has different capabilities
    public boolean ViewOwnTasks() { return this == USER; }
    public boolean ViewAllTasks() { return this == ADMIN || this == USER; }
    public boolean ManageUsers() { return this == ADMIN; }
    public boolean ManageTeams() { return this == ADMIN; }
}

// Status enum - different status values
public enum Status {
    BACKLOG("Backlog", "backlog"),
    TODO("To Do", "todo"),
    IN_PROGRESS("In Progress", "in-progress"),
    DONE("Done", "done");
    
    private final String displayName;
    private final String cssClass;
}
```

### 3. Polymorphism

**Definition**: Ability of objects to take on multiple forms or the same operation behaving differently.

#### Method Overloading

```java
// InputValidator class - same method name, different parameters
public class InputValidator {
    // Validate with default color
    public static Label createLabel(String name) {
        return new Label(name, "#007BFF");
    }
    
    // Validate with custom color
    public static Label createLabel(String name, String color) {
        return new Label(name.trim(), color);
    }
}
```

#### Method Overriding

```java
// Model classes with common methods
public class ActivityLog {
    @Override
    public String toString() {
        return String.format("[%s] %s %s '%s' by %s",
            timestamp, action, entityType, entityName, 
            user != null ? user.getUsername() : "System");
    }
    
    @Override
    public equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActivityLog that)) return false;
        return Objects.equals(id, that.id);
    }
}

public class Task {
    @Override
    public String toString() {
        return title;
    }
}
```

#### Polymorphic Collections

```java
// Collections can hold different types of repositories
List<BaseRepository> repositories = new ArrayList<>();
repositories.add(new TaskRepository());
repositories.add(new ProjectRepository());
repositories.add(new UserRepository());

// Each can be used the same way
for (BaseRepository repo : repositories) {
    Connection conn = repo.getConnection(); // Polymorphic call
}
```

### 4. Abstraction

**Definition**: Hiding complex implementation details and showing only essential features.

#### Abstract Concepts Through Services

```java
// TaskService abstracts database complexity
public class TaskService {
    private final TaskRepository taskRepository;
    
    // Controller doesn't know about SQL
    public Task createTask(String title, String description, Project project) {
        // High-level abstraction
        validateUserLoggedIn();
        validateTaskTitle(title);
        
        User currentUser = CurrentUser.getInstance();
        Task task = new Task(title.trim(), project, currentUser);
        task.setDescription(description);
        
        return taskRepository.save(task); // SQL hidden
    }
    
    // Complex filtering abstracted into service
    public List<Task> filterTasks(String status, String priority, 
                                   Long assigneeId, Long projectId,
                                   LocalDate dueDateFrom, LocalDate dueDateTo) {
        return getAllTasks().stream()
            .filter(t -> matchesStatus(t, status))
            .filter(t -> matchesPriority(t, priority))
            .filter(t -> matchesAssignee(t, assigneeId))
            .filter(t -> matchesProject(t, projectId))
            .filter(t -> matchesDueDateRange(t, dueDateFrom, dueDateTo))
            .collect(Collectors.toList());
    }
}
```

#### Abstract Classes & Interfaces (if used)

```java
// BaseRepository is abstract - encapsulates connection logic
public class BaseRepository {
    // Abstract concept: getting a connection
    protected Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }
}

// Subclasses focus on specific data access, not connection details
public class TaskRepository extends BaseRepository {
    // Implementation details abstracted away
    public Task save(Task task) {
        // Use abstracted getConnection()
    }
}
```

#### Singleton Pattern - Abstracts Instance Management

```java
// Complex initialization hidden
public class DBConnection {
    private static final DBConnection INSTANCE = new DBConnection();
    
    private Connection connection;
    
    private DBConnection() {
        connect(); // Initialization details hidden
    }
    
    public Connection getConnection() {
        // Connection validation hidden
        if (isConnectionInvalid()) {
            connect();
        }
        return connection;
    }
    
    public static DBConnection getInstance() {
        return INSTANCE; // Single instance, management abstracted
    }
}

// Users don't know about connection management
Connection conn = DBConnection.getInstance().getConnection();
```

---

## File Processing & Database Interaction

### 1. Database Initialization Script

**Location**: `src/main/resources/com/taskmanagement/sql/init_sql_database.sql`

**Purpose**: Initialize database schema and sample data

**Process:**
```sql
-- Create database
CREATE DATABASE TaskManagementDB;

-- Create tables with proper schema
CREATE TABLE Users (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    username NVARCHAR(50) NOT NULL UNIQUE,
    password_hash NVARCHAR(255) NOT NULL,
    email NVARCHAR(100),
    role NVARCHAR(20) DEFAULT 'User',
    created_at DATETIME DEFAULT GETDATE(),
    last_login DATETIME,
    position NVARCHAR(100)
);

CREATE TABLE Projects (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(500),
    color NVARCHAR(7),
    created_by BIGINT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (created_by) REFERENCES Users(id)
);

-- ... more tables and indexes
```

### 2. Database Connection Management

**Singleton Pattern for Connection Pooling:**

```java
public class DBConnection {
    private static final DBConnection INSTANCE = new DBConnection();
    
    private Connection connection;
    
    // Lazy initialization - connects on first use
    private DBConnection() {
        connect();
    }
    
    // Connection validation and reconnection
    public Connection getConnection() {
        try {
            if (isConnectionInvalid()) {
                System.out.println(ERR_INVALID_CONNECTION);
                connect();
            }
        } catch (SQLException e) {
            System.err.println(ERR_CHECK_VALIDITY);
            connect();
        }
        return connection;
    }
    
    // Private method - implementation detail
    private void connect() {
        try {
            if (isConnectionInvalid()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                connection.setAutoCommit(true);
                System.out.println(MSG_CONNECTED);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }
    
    // Validate connection still active
    private boolean isConnectionInvalid() throws SQLException {
        return connection == null || connection.isClosed() || 
               !connection.isValid(VALIDATION_TIMEOUT_SECONDS);
    }
    
    // Singleton accessor
    public static DBConnection getInstance() {
        return INSTANCE;
    }
}
```

**Usage in Repositories:**

```java
public class BaseRepository {
    protected Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }
}

public class TaskRepository extends BaseRepository {
    public Task save(Task task) {
        try (Connection conn = getConnection(); // Gets managed connection
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, 
                                                  Statement.RETURN_GENERATED_KEYS)) {
            // Use connection
        } catch (SQLException e) {
            throw new RuntimeException(ERR_SAVE, e);
        }
    }
}
```

### 3. Prepared Statements - SQL Injection Prevention

**Safe SQL Execution:**

```java
// SAFE - Uses prepared statements
private static final String SQL_INSERT = """
    INSERT INTO Tasks (title, description, status, priority, due_date, 
                      project_id, assignee_id, created_by)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """;

public Task save(Task task) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, 
                                          Statement.RETURN_GENERATED_KEYS)) {
        
        // Bind parameters - prevents SQL injection
        pstmt.setString(1, task.getTitle());
        pstmt.setString(2, task.getDescription());
        pstmt.setString(3, task.getStatus());
        pstmt.setString(4, task.getPriority());
        pstmt.setDate(5, dueDate != null ? Date.valueOf(task.getDueDate()) : null);
        pstmt.setObject(6, task.getProject() != null ? task.getProject().getId() : null);
        pstmt.setObject(7, task.getAssignee() != null ? task.getAssignee().getId() : null);
        pstmt.setLong(8, task.getCreatedBy().getId());
        
        pstmt.executeUpdate();
        
        // Get generated ID
        try (ResultSet rs = pstmt.getGeneratedKeys()) {
            if (rs.next()) {
                task.setId(rs.getLong(1));
            }
        }
    }
    return task;
}
```

**Benefits:**
- SQL injection prevention
- Automatic type conversion
- Better performance (query compilation)

### 4. ResultSet Mapping - Object Hydration

**Converting Database Rows to Objects:**

```java
private Task mapRowToTask(ResultSet rs) throws SQLException {
    Task task = new Task();
    
    // Map columns to object fields
    task.setId(rs.getLong(COL_ID));
    task.setTitle(rs.getString(COL_TITLE));
    task.setDescription(rs.getString(COL_DESCRIPTION));
    task.setStatus(rs.getString(COL_STATUS));
    task.setPriority(rs.getString(COL_PRIORITY));
    
    // Handle nullable date
    Date sqlDate = rs.getDate(COL_DUE_DATE);
    if (sqlDate != null) {
        task.setDueDate(sqlDate.toLocalDate());
    }
    
    // Create related objects
    if (rs.getLong(COL_PROJECT_ID) > 0) {
        Project project = new Project();
        project.setId(rs.getLong(COL_PROJECT_ID));
        project.setName(rs.getString(COL_PROJECT_NAME));
        task.setProject(project);
    }
    
    if (rs.getLong(COL_ASSIGNEE_ID) > 0) {
        User assignee = new User();
        assignee.setId(rs.getLong(COL_ASSIGNEE_ID));
        assignee.setUsername(rs.getString(COL_ASSIGNEE_NAME));
        task.setAssignee(assignee);
    }
    
    // Creator is always present
    User creator = new User();
    creator.setId(rs.getLong(COL_CREATOR_ID));
    creator.setUsername(rs.getString(COL_CREATOR_NAME));
    task.setCreatedBy(creator);
    
    task.setCreatedAt(rs.getTimestamp(COL_CREATED_AT).toLocalDateTime());
    
    return task;
}
```

### 5. Transaction Management

**Implicit Transactions:**

```java
// auto-commit enabled
connection.setAutoCommit(true);

// Each statement auto-commits
pstmt.executeUpdate(); // Auto-committed
```

**Resource Management with Try-With-Resources:**

```java
// Automatic resource cleanup
try (Connection conn = getConnection();
     PreparedStatement pstmt = conn.prepareStatement(SQL)) {
    
    // Use resources
    
} catch (SQLException e) {
    // Connection and statement auto-closed
    throw new RuntimeException("Error", e);
}
```

### 6. Error Handling

**Consistent Error Messages:**

```java
// Each operation has clear error messages
private static final String ERR_SAVE = "Error saving task: ";
private static final String ERR_LOAD_ALL = "Error loading all tasks";
private static final String ERR_FIND_BY_PROJECT = "Error loading tasks for project: ";
private static final String ERR_DELETE = "Error deleting task ID: ";

public Task save(Task task) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, ...)) {
        // Implementation
    } catch (SQLException e) {
        throw new RuntimeException(ERR_SAVE + task.getTitle(), e);
    }
}

public List<Task> findByProjectId(Long projectId) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_PROJECT_ID)) {
        // Implementation
    } catch (SQLException e) {
        throw new RuntimeException(ERR_FIND_BY_PROJECT + projectId, e);
    }
}
```

---

## Summary

This Task Management System exemplifies professional software engineering principles:

### Architectural Excellence
- **MVC Pattern**: Clean separation between UI, business logic, and data
- **Service Layer**: Business logic encapsulation
- **Repository Pattern**: Data access abstraction

### Design Principles
- **SOLID**: Each class has single responsibility, extensible without modification
- **DRY**: Centralized reusable code in utilities and base classes
- **KISS**: Simple, readable, maintainable code

### OOP Mastery
- **Encapsulation**: Private fields with controlled access
- **Inheritance**: Code reuse through BaseRepository
- **Polymorphism**: Flexible, extensible designs
- **Abstraction**: Complex operations hidden behind simple interfaces

### Professional Practices
- **Input Validation**: Comprehensive validation at service layer
- **Error Handling**: Clear error messages and exception handling
- **Security**: Permission checks, SQL injection prevention
- **Performance**: Prepared statements, connection pooling

This layered, principle-based approach makes the codebase:
- Easy to understand and navigate
- Simple to test and maintain
- Ready to scale and extend
- Production-grade quality

---

**Document Status**: Complete Overview of Architecture & Design Principles  
**Last Updated**: January 2026  
**Audience**: Developers, Architects, Code Reviewers
