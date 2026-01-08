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

    public TaskService() {
        this.taskRepository = new TaskRepository();
    }

    public Task createTask(String title, String description, Project project) {
        if (!CurrentUser.isLoggedIn()) {
            throw new SecurityException("User must be logged in");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title is required");
        }
        if (project == null || project.getId() == null) {
            throw new IllegalArgumentException("Project is required");
        }

        User currentUser = CurrentUser.getInstance();
        Task task = new Task(title.trim(), project, currentUser);
        task.setDescription(description);
        return taskRepository.save(task);
    }

    public Task updateTask(Task task) {
        if (!CurrentUser.isLoggedIn()) {
            throw new SecurityException("User must be logged in");
        }
        if (!task.canEdit()) {
            throw new SecurityException("You don't have permission to edit this task");
        }
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title is required");
        }

        task.setTitle(task.getTitle().trim());
        return taskRepository.update(task);
    }

    public void deleteTask(Long taskId) {
        if (!CurrentUser.isLoggedIn()) {
            throw new SecurityException("User must be logged in");
        }
        Task task = taskRepository.findById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found");
        }
        if (!task.canDelete()) {
            throw new SecurityException("You don't have permission to delete this task");
        }

        taskRepository.delete(taskId);
    }

    public Task getTaskById(Long id) {
        Task task = taskRepository.findById(id);
        if (task != null && !canViewTask(task)) {
            throw new SecurityException("You don't have permission to view this task");
        }
        return task;
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .filter(this::canViewTask)
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByProject(Long projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return tasks.stream()
                .filter(this::canViewTask)
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByAssignee(Long userId) {
        return getAllTasks().stream()
                .filter(t -> t.getAssignee() != null && t.getAssignee().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByStatus(String status) {
        return getAllTasks().stream()
                .filter(t -> t.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByPriority(String priority) {
        return getAllTasks().stream()
                .filter(t -> t.getPriority().equalsIgnoreCase(priority))
                .collect(Collectors.toList());
    }

    public List<Task> getOverdueTasks() {
        return getAllTasks().stream()
                .filter(Task::isOverdue)
                .collect(Collectors.toList());
    }

    public List<Task> getDueTodayTasks() {
        return getAllTasks().stream()
                .filter(Task::isDueToday)
                .collect(Collectors.toList());
    }

    public List<Task> getCompletedTasks() {
        return getTasksByStatus("Done");
    }

    public List<Task> getInProgressTasks() {
        return getTasksByStatus("In Progress");
    }

    public List<Task> getUnassignedTasks() {
        return getAllTasks().stream()
                .filter(Task::isUnassigned)
                .collect(Collectors.toList());
    }

    public List<Task> filterTasks(String status, String priority, Long assigneeId, Long projectId, LocalDate dueDateFrom, LocalDate dueDateTo) {
        return getAllTasks().stream()
                .filter(t -> status == null || t.getStatus().equalsIgnoreCase(status))
                .filter(t -> priority == null || t.getPriority().equalsIgnoreCase(priority))
                .filter(t -> assigneeId == null || (t.getAssignee() != null && t.getAssignee().getId().equals(assigneeId)))
                .filter(t -> projectId == null || (t.getProject() != null && t.getProject().getId().equals(projectId)))
                .filter(t -> dueDateFrom == null || (t.getDueDate() != null && !t.getDueDate().isBefore(dueDateFrom)))
                .filter(t -> dueDateTo == null || (t.getDueDate() != null && !t.getDueDate().isAfter(dueDateTo)))
                .collect(Collectors.toList());
    }

    public List<Task> searchTasks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllTasks();
        }
        String lowerQuery = query.toLowerCase();
        return getAllTasks().stream()
                .filter(t -> t.getTitle().toLowerCase().contains(lowerQuery)
                        || (t.getDescription() != null && t.getDescription().toLowerCase().contains(lowerQuery)))
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
        if (!CurrentUser.isLoggedIn()) {
            throw new SecurityException("User must be logged in");
        }

        Task task = taskRepository.findById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found");
        }

        if (!CurrentUser.canAssignTasks()) {
            throw new SecurityException("You don't have permission to assign tasks");
        }

        User user = new User();
        user.setId(userId);
        task.setAssignee(user);
        taskRepository.update(task);
    }

    public void changeTaskStatus(Long taskId, String newStatus) {
        Task task = taskRepository.findById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found");
        }

        if (!task.canEdit()) {
            throw new SecurityException("You don't have permission to edit this task");
        }

        task.setStatus(newStatus);
        taskRepository.update(task);
    }

    public void changeTaskPriority(Long taskId, String newPriority) {
        Task task = taskRepository.findById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found");
        }

        if (!task.canEdit()) {
            throw new SecurityException("You don't have permission to edit this task");
        }

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
