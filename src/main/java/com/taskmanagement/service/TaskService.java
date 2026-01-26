package com.taskmanagement.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.taskmanagement.model.Project;
import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.utils.CurrentUser;

public class TaskService {

    private final TaskRepository taskRepository;

    // Status Constants
    private static final String STATUS_TODO = "To Do";
    private static final String STATUS_IN_PROGRESS = "In Progress";
    private static final String STATUS_DONE = "Done";

    // Priority Constants
    private static final String PRIORITY_LOW = "Low";
    private static final String PRIORITY_MEDIUM = "Medium";
    private static final String PRIORITY_HIGH = "High";

    // Error Messages
    private static final String ERR_NOT_LOGGED_IN = "User must be logged in";
    private static final String ERR_TASK_REQUIRED = "Task title is required";
    private static final String ERR_PROJECT_REQUIRED = "Project is required";
    private static final String ERR_TASK_NOT_FOUND = "Task not found";
    private static final String ERR_NO_PERMISSION_EDIT = "You don't have permission to edit this task";
    private static final String ERR_NO_PERMISSION_DELETE = "You don't have permission to delete this task";
    private static final String ERR_NO_PERMISSION_VIEW = "You don't have permission to view this task";
    private static final String ERR_NO_PERMISSION_ASSIGN = "You don't have permission to assign tasks";

    public TaskService() {
        this.taskRepository = new TaskRepository();
    }

    public Task createTask(String title, String description, Project project) {
        validateUserLoggedIn();
        validateTaskTitle(title);
        validateProjectExists(project);

        User currentUser = CurrentUser.getInstance();
        Task task = new Task(title.trim(), project, currentUser);
        task.setDescription(description);
        return taskRepository.save(task);
    }

    public Task updateTask(Task task) {
        validateUserLoggedIn();
        validateTaskExists(task);
        validateTaskPermission(task, ERR_NO_PERMISSION_EDIT);
        validateTaskTitle(task.getTitle());

        task.setTitle(task.getTitle().trim());
        return taskRepository.update(task);
    }

    public void deleteTask(Long taskId) {
        validateUserLoggedIn();
        Task task = getTaskOrThrow(taskId);
        validateTaskPermission(task, ERR_NO_PERMISSION_DELETE);
        taskRepository.delete(taskId);
    }

    public Task getTaskById(Long id) {
        Task task = taskRepository.findById(id);
        if (task != null && !canViewTask(task)) {
            throw new SecurityException(ERR_NO_PERMISSION_VIEW);
        }
        return task;
    }

    public List<Task> getAllTasks() {
        return filterVisibleTasks(taskRepository.findAll());
    }

    public List<Task> getTasksByProject(Long projectId) {
        return filterVisibleTasks(taskRepository.findByProjectId(projectId));
    }

    public List<Task> getTasksByAssignee(Long userId) {
        return filterVisibleTasks(
            getAllTasks().stream()
                .filter(t -> hasAssignee(t, userId))
                .collect(Collectors.toList())
        );
    }

    public List<Task> getTasksByStatus(String status) {
        return filterVisibleTasks(
            getAllTasks().stream()
                .filter(t -> matchesStatus(t, status))
                .collect(Collectors.toList())
        );
    }

    public List<Task> getTasksByPriority(String priority) {
        return filterVisibleTasks(
            getAllTasks().stream()
                .filter(t -> matchesPriority(t, priority))
                .collect(Collectors.toList())
        );
    }

    public List<Task> getOverdueTasks() {
        return filterVisibleTasks(
            getAllTasks().stream()
                .filter(Task::isOverdue)
                .collect(Collectors.toList())
        );
    }

    public List<Task> getDueTodayTasks() {
        return filterVisibleTasks(
            getAllTasks().stream()
                .filter(Task::isDueToday)
                .collect(Collectors.toList())
        );
    }

    public List<Task> getCompletedTasks() {
        return getTasksByStatus(STATUS_DONE);
    }

    public List<Task> getInProgressTasks() {
        return getTasksByStatus(STATUS_IN_PROGRESS);
    }

    public List<Task> getUnassignedTasks() {
        return filterVisibleTasks(
            getAllTasks().stream()
                .filter(Task::isUnassigned)
                .collect(Collectors.toList())
        );
    }

    public List<Task> filterTasks(String status, String priority, Long assigneeId, Long projectId, 
                                   LocalDate dueDateFrom, LocalDate dueDateTo) {
        return getAllTasks().stream()
                .filter(t -> matchesStatus(t, status))
                .filter(t -> matchesPriority(t, priority))
                .filter(t -> matchesAssignee(t, assigneeId))
                .filter(t -> matchesProject(t, projectId))
                .filter(t -> matchesDueDateRange(t, dueDateFrom, dueDateTo))
                .collect(Collectors.toList());
    }

    public List<Task> searchTasks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllTasks();
        }
        String lowerQuery = query.toLowerCase();
        return getAllTasks().stream()
                .filter(t -> matchesSearchQuery(t, lowerQuery))
                .collect(Collectors.toList());
    }

    public List<Task> sortTasks(List<Task> tasks, String sortBy) {
        Comparator<Task> comparator = switch (sortBy) {
            case "priority" -> Comparator.comparing(Task::getPriority).reversed();
            case "dueDate" -> Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()));
            case "status" -> Comparator.comparing(Task::getStatus);
            case "title" -> Comparator.comparing(Task::getTitle);
            case "overdue" -> Comparator.comparing(Task::isOverdue).reversed();
            default -> Comparator.comparing(Task::getCreatedAt).reversed();
        };

        return tasks.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public void assignTask(Long taskId, Long userId) {
        validateUserLoggedIn();
        if (!CurrentUser.canAssignTasks()) {
            throw new SecurityException(ERR_NO_PERMISSION_ASSIGN);
        }

        Task task = getTaskOrThrow(taskId);
        User user = new User();
        user.setId(userId);
        task.setAssignee(user);
        taskRepository.update(task);
    }

    public void changeTaskStatus(Long taskId, String newStatus) {
        Task task = getTaskOrThrow(taskId);
        validateTaskPermission(task, ERR_NO_PERMISSION_EDIT);
        task.setStatus(newStatus);
        taskRepository.update(task);
    }

    public void changeTaskPriority(Long taskId, String newPriority) {
        Task task = getTaskOrThrow(taskId);
        validateTaskPermission(task, ERR_NO_PERMISSION_EDIT);
        task.setPriority(newPriority);
        taskRepository.update(task);
    }

    public int getCompletionRate(Long projectId) {
        List<Task> projectTasks = getTasksByProject(projectId);
        if (projectTasks.isEmpty()) return 0;

        long completed = projectTasks.stream().filter(Task::isCompleted).count();
        return (int) ((completed * 100) / projectTasks.size());
    }

    public int getOverdueCount() {
        return (int) getAllTasks().stream().filter(Task::isOverdue).count();
    }

    public List<Task> getTasksDueWithin(int days) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        return getAllTasks().stream()
                .filter(t -> t.getDueDate() != null)
                .filter(t -> !t.getDueDate().isBefore(today) && !t.getDueDate().isAfter(endDate))
                .sorted(Comparator.comparing(Task::getDueDate))
                .collect(Collectors.toList());
    }

    private void validateUserLoggedIn() {
        if (!CurrentUser.isLoggedIn()) {
            throw new SecurityException(ERR_NOT_LOGGED_IN);
        }
    }

    private void validateTaskTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException(ERR_TASK_REQUIRED);
        }
    }

    private void validateProjectExists(Project project) {
        if (project == null || project.getId() == null) {
            throw new IllegalArgumentException(ERR_PROJECT_REQUIRED);
        }
    }

    private void validateTaskExists(Task task) {
        if (task == null) {
            throw new IllegalArgumentException(ERR_TASK_NOT_FOUND);
        }
    }

    private void validateTaskPermission(Task task, String errorMessage) {
        boolean hasPermission = task.canEdit() || task.canDelete();
        if (!hasPermission) {
            throw new SecurityException(errorMessage);
        }
    }

    private Task getTaskOrThrow(Long taskId) {
        Task task = taskRepository.findById(taskId);
        if (task == null) {
            throw new IllegalArgumentException(ERR_TASK_NOT_FOUND);
        }
        return task;
    }

    private List<Task> filterVisibleTasks(List<Task> tasks) {
        return tasks.stream()
                .filter(this::canViewTask)
                .collect(Collectors.toList());
    }

    private boolean matchesStatus(Task task, String status) {
        return status == null || task.getStatus().equalsIgnoreCase(status);
    }

    private boolean matchesPriority(Task task, String priority) {
        return priority == null || task.getPriority().equalsIgnoreCase(priority);
    }

    private boolean matchesAssignee(Task task, Long assigneeId) {
        return assigneeId == null || hasAssignee(task, assigneeId);
    }

    private boolean matchesProject(Task task, Long projectId) {
        return projectId == null || (task.getProject() != null && task.getProject().getId().equals(projectId));
    }

    private boolean matchesDueDateRange(Task task, LocalDate dueDateFrom, LocalDate dueDateTo) {
        if (task.getDueDate() == null) return dueDateFrom == null && dueDateTo == null;
        if (dueDateFrom != null && task.getDueDate().isBefore(dueDateFrom)) return false;
        if (dueDateTo != null && task.getDueDate().isAfter(dueDateTo)) return false;
        return true;
    }

    private boolean matchesSearchQuery(Task task, String query) {
        return task.getTitle().toLowerCase().contains(query) ||
               (task.getDescription() != null && task.getDescription().toLowerCase().contains(query));
    }

    private boolean hasAssignee(Task task, Long userId) {
        return task.getAssignee() != null && task.getAssignee().getId().equals(userId);
    }

    private boolean canViewTask(Task task) {
        if (!CurrentUser.isLoggedIn()) return false;
        if (CurrentUser.isAdmin()) return true;

        User current = CurrentUser.getInstance();
        if (current == null) return false;

        // Can view own tasks
        if (task.getCreatedBy() != null && task.getCreatedBy().getId().equals(current.getId())) return true;
        if (task.getAssignee() != null && task.getAssignee().getId().equals(current.getId())) return true;

        // Can view project tasks if in project
        if (task.getProject() != null && task.getProject().canEdit()) return true;

        return false;
    }
}
