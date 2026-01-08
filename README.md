# Task Management App

A comprehensive JavaFX desktop application for managing tasks, projects, and teams. Built with Maven, featuring a modern UI, database persistence, and robust business logic.

## Features

### Core Features
- ✅ **User Authentication**: Secure login and registration with role-based access control
- ✅ **Task Management**: Create, edit, delete, and assign tasks with detailed information
- ✅ **Project Organization**: Group tasks into projects with team collaboration
- ✅ **Status & Priority**: Track task progress with customizable statuses and priorities
- ✅ **Comments & Collaboration**: Add comments and activity tracking to tasks
- ✅ **Labels & Categorization**: Organize tasks with labels and color coding
- ✅ **Team Management**: Create teams, manage members, and assign projects

### Advanced Features
- 📊 **Dashboard Analytics**: View statistics, completion rates, and workload distribution
- 🔍 **Search & Filter**: Advanced filtering by status, priority, assignee, due date
- 📅 **Due Date Management**: Set due dates, track overdue tasks, view upcoming items
- 🏷️ **Activity Log**: Complete audit trail of all system changes
- 👥 **Role-Based Access Control**: Admin and User roles with permission management
- 🔐 **Input Validation**: Comprehensive validation for all user input
- 📈 **Performance Optimization**: Efficient database queries and caching

## Technologies

- **Java**: 21 (easily upgradable to newer versions)
- **JavaFX**: 21 (modern UI controls + FXML markup)
- **Build Tool**: Maven
- **Database**: SQL Server (via JDBC)
- **Testing**: JUnit 5

## Project Structure

```
TaskManagementApp/
├── src/
│   ├── main/
│   │   ├── java/com/taskmanagement/
│   │   │   ├── App.java                 # Main application entry point
│   │   │   ├── controller/              # JavaFX Controllers
│   │   │   ├── database/                # Database connection management
│   │   │   ├── model/                   # Domain models
│   │   │   ├── repository/              # Data access layer
│   │   │   ├── service/                 # Business logic layer
│   │   │   └── utils/                   # Utilities & helpers
│   │   └── resources/
│   │       └── com/taskmanagement/
│   │           ├── css/                 # Stylesheets
│   │           ├── fxml/                # UI layouts
│   │           ├── img/                 # Images & icons
│   │           └── sql/                 # Database scripts
│   └── test/
│       └── java/com/taskmanagement/     # Unit tests
├── pom.xml                              # Maven configuration
├── application.properties                # Configuration file
└── ReadMe.md                            # This file
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

## API & Services Overview

### TaskService
Complete task management with CRUD operations, filtering, searching, and sorting.

```java
TaskService taskService = new TaskService();

// Create task
Task task = taskService.createTask("New Task", "Description", project);

// Filter and search
List<Task> urgentTasks = taskService.getTasksByPriority("High");
List<Task> searchResults = taskService.searchTasks("keyword");
List<Task> dueSoon = taskService.getTasksDueWithin(7); // Due within 7 days

// Get statistics
int completionRate = taskService.getCompletionRate(projectId);
List<Task> overdue = taskService.getOverdueTasks();
```

### ProjectService
Manage projects with team collaboration and permission control.

```java
ProjectService projectService = new ProjectService();

// Create project
Project project = projectService.createProject("New Project", "Description", "#007BFF", team);

// List and retrieve
List<Project> projects = projectService.getAllProjects();
Project p = projectService.getProjectById(projectId);

// Update and delete
projectService.updateProject(updatedProject);
projectService.deleteProject(projectId);
```

### CommentService
Add discussions and notes to tasks.

```java
CommentService commentService = new CommentService();

// Add comment
Comment comment = commentService.addComment(taskId, "Great progress!");

// Retrieve comments
List<Comment> comments = commentService.getCommentsByTask(taskId);

// Edit and delete
commentService.updateComment(commentId, "Updated comment");
commentService.deleteComment(commentId);
```

### UserService
User authentication and profile management.

```java
UserService userService = new UserService();

// Register new user
User user = userService.register("username", "email@example.com", "password123");

// Login
User loggedIn = userService.login("username", "password123");

// Update profile
userService.updateProfile(updatedUser);
```

### DashboardAnalytics
Generate statistics and metrics for dashboards.

```java
// Get overall statistics
Map<String, Object> stats = DashboardAnalytics.getDashboardStats(allTasks, allProjects);

// Get specific metrics
double completionRate = DashboardAnalytics.getCompletionRate(tasks);
long overdueCount = DashboardAnalytics.getOverdueCount(tasks);
Map<String, Long> byStatus = DashboardAnalytics.getTasksByStatus(tasks);

// Get task urgency
List<Task> urgent = DashboardAnalytics.getSortedByUrgency(tasks);
```

### InputValidator
Comprehensive input validation for all user data.

```java
// Validate various inputs
InputValidator.isValidEmail("user@example.com");
InputValidator.isValidUsername("john_doe");
InputValidator.isValidPassword("securePass123");
InputValidator.isValidColor("#007BFF");

// Validate business objects
String title = InputValidator.validateTaskTitle(title);
String projectName = InputValidator.validateProjectName(name);
String comment = InputValidator.validateComment(text);
```

### ActivityLogService
Track all system changes and user actions.

```java
// Log activities
ActivityLogService.logTaskCreated(taskId, "Task Title");
ActivityLogService.logTaskStatusChanged(taskId, "Title", "To Do", "Done");
ActivityLogService.logProjectCreated(projectId, "Project Name");

// Retrieve activity history
List<ActivityLog> recent = ActivityLogService.getRecentActivities(20);
List<ActivityLog> history = ActivityLogService.getEntityHistory("TASK", taskId);
List<ActivityLog> userActions = ActivityLogService.getActivitiesByActor(userId);
```

## Database Schema

The application uses SQL Server with the following main tables:

| Table | Purpose |
|-------|---------|
| **Users** | User accounts with roles and credentials |
| **Teams** | Team groups for collaboration |
| **TeamMembers** | Many-to-many mapping between users and teams |
| **Projects** | Projects that group tasks together |
| **Tasks** | Individual tasks with status, priority, and assignments |
| **Comments** | Discussion comments on tasks |
| **Labels** | Tags for categorizing tasks |
| **TaskLabels** | Many-to-many mapping between tasks and labels |
| **ActivityLog** | Audit trail of all system changes |

## Validation Rules

### Task Title
- Required, 3-255 characters

### Project Name
- Required, 3-100 characters

### Username
- 3-20 characters
- Alphanumeric, underscore, hyphen allowed

### Password
- Minimum 8 characters (highly recommended)
- Can include special characters

### Email
- Valid email format (user@domain.com)

### Priority Values
- Low, Medium, High, Urgent

### Status Values
- To Do, In Progress, In Review, Done

## Permission System

### Admin Role
- Full access to all features
- Can manage users and teams
- Can view all projects and tasks
- Can manage system-wide settings

### User Role
- Can create and manage own tasks
- Can view and edit tasks assigned to them
- Can view projects they're part of
- Can comment on tasks

## Testing

Run unit tests with Maven:

```bash
mvn test
```

Tests include:
- Input validation tests
- Business logic tests
- Service integration tests

## Configuration

Edit `application.properties` to customize:
- Database connection settings
- Default task values (priority, status)
- Pagination settings
- Date formats
- UI themes and language

## Troubleshooting

### Connection Issues
1. Verify SQL Server is running
2. Check connection string in `DBConnection.java`
3. Ensure database exists (run init script)
4. Check firewall settings

### Build Issues
1. Ensure Java 21+ is installed: `java -version`
2. Ensure Maven is installed: `mvn -version`
3. Clear Maven cache: `mvn clean`
4. Rebuild: `mvn clean compile`

### Runtime Issues
1. Check console output for error messages
2. Verify database tables exist
3. Check user credentials
4. Ensure JavaFX libraries are properly configured

## Future Enhancements

- [ ] Email notifications for task assignments and due dates
- [ ] File attachments for tasks
- [ ] Recurring/repeating tasks
- [ ] Task templates and duplication
- [ ] Advanced reporting (PDF/Excel export)
- [ ] Calendar view for tasks
- [ ] Real-time collaboration features
- [ ] Mobile app companion
- [ ] Rest API for third-party integration
- [ ] Dark theme support

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is open source and available under the MIT License.

## Support

For issues, questions, or suggestions:
1. Check the troubleshooting section
2. Review the API documentation above
3. Check existing GitHub issues
4. Create a new issue with detailed information

## Version History

### v1.0.0 (Current)
- Initial release with full task management functionality
- User authentication and authorization
- Project and team management
- Comments and activity logging
- Dashboard analytics
- Comprehensive validation

---

**Last Updated**: December 2025
**Status**: Production Ready



