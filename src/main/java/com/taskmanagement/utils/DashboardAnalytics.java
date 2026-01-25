package com.taskmanagement.utils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.taskmanagement.model.Project;
import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;

/**
 * Analytics utility for generating dashboard statistics and metrics.
 * Provides methods for calculating completion rates, task counts, distributions, and urgency scoring.
 */
public class DashboardAnalytics {

    // Priority Score Constants
    private static final int PRIORITY_URGENT = 4;
    private static final int PRIORITY_HIGH = 3;
    private static final int PRIORITY_MEDIUM = 2;
    private static final int PRIORITY_LOW = 1;
    private static final int PRIORITY_UNKNOWN = 0;

    // Default Constants
    private static final int DEFAULT_DAYS_UNTIL_DUE = 10;
    private static final String STATUS_IN_PROGRESS = "In Progress";

    // Calculation Constants
    private static final double PERCENT_MULTIPLIER = 100.0;
    private static final double URGENCY_DIVISOR = 10.0;

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with static methods only.
     */
    private DashboardAnalytics() {
        // Utility class, no instantiation
    }

    // ============ Completion & Count Methods ============

    /**
     * Calculate overall task completion rate as a percentage.
     *
     * @param tasks the list of tasks
     * @return completion rate (0.0-100.0), or 0.0 if list is empty
     */
    public static double getCompletionRate(List<Task> tasks) {
        if (tasks.isEmpty()) return 0.0;
        long completed = tasks.stream().filter(Task::isCompleted).count();
        return (completed * PERCENT_MULTIPLIER) / tasks.size();
    }

    /**
     * Get count of overdue tasks.
     *
     * @param tasks the list of tasks
     * @return number of overdue tasks
     */
    public static long getOverdueCount(List<Task> tasks) {
        return tasks.stream().filter(Task::isOverdue).count();
    }

    /**
     * Get count of tasks due today.
     *
     * @param tasks the list of tasks
     * @return number of tasks due today
     */
    public static long getDueTodayCount(List<Task> tasks) {
        return tasks.stream().filter(Task::isDueToday).count();
    }

    /**
     * Get count of completed tasks.
     *
     * @param tasks the list of tasks
     * @return number of completed tasks
     */
    public static long getCompletedCount(List<Task> tasks) {
        return tasks.stream().filter(Task::isCompleted).count();
    }

    /**
     * Get count of in-progress tasks.
     *
     * @param tasks the list of tasks
     * @return number of in-progress tasks
     */
    public static long getInProgressCount(List<Task> tasks) {
        return tasks.stream()
                .filter(t -> STATUS_IN_PROGRESS.equalsIgnoreCase(t.getStatus()))
                .count();
    }

    /**
     * Get count of unassigned tasks.
     *
     * @param tasks the list of tasks
     * @return number of unassigned tasks
     */
    public static long getUnassignedCount(List<Task> tasks) {
        return tasks.stream().filter(Task::isUnassigned).count();
    }

    // ============ Distribution Methods ============

    /**
     * Get task distribution by status.
     *
     * @param tasks the list of tasks
     * @return map of status -> count
     */
    public static Map<String, Long> getTasksByStatus(List<Task> tasks) {
        return tasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));
    }

    /**
     * Get task distribution by priority.
     *
     * @param tasks the list of tasks
     * @return map of priority -> count
     */
    public static Map<String, Long> getTasksByPriority(List<Task> tasks) {
        return tasks.stream()
                .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));
    }

    /**
     * Get task distribution by assignee.
     *
     * @param tasks the list of tasks
     * @return map of assignee username -> count
     */
    public static Map<String, Long> getTasksByAssignee(List<Task> tasks) {
        return tasks.stream()
                .filter(t -> t.getAssignee() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getAssignee().getUsername(),
                        Collectors.counting()
                ));
    }

    /**
     * Get task distribution by project.
     *
     * @param tasks the list of tasks
     * @return map of project name -> count
     */
    public static Map<String, Long> getTasksByProject(List<Task> tasks) {
        return tasks.stream()
                .filter(t -> t.getProject() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getProject().getName(),
                        Collectors.counting()
                ));
    }

    /**
     * Get team workload (tasks per team member).
     *
     * @param tasks the list of tasks
     * @return map of User -> task count
     */
    public static Map<User, Long> getTeamWorkload(List<Task> tasks) {
        return tasks.stream()
                .filter(t -> t.getAssignee() != null)
                .collect(Collectors.groupingBy(Task::getAssignee, Collectors.counting()));
    }

    // ============ Upcoming Tasks Method ============

    /**
     * Get tasks due within the next N days.
     *
     * @param tasks the list of tasks
     * @param days the number of days to look ahead
     * @return list of upcoming tasks sorted by due date
     */
    public static List<Task> getUpcomingTasks(List<Task> tasks, int days) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        return tasks.stream()
                .filter(t -> t.getDueDate() != null)
                .filter(t -> !t.getDueDate().isBefore(today) && !t.getDueDate().isAfter(endDate))
                .sorted((a, b) -> a.getDueDate().compareTo(b.getDueDate()))
                .collect(Collectors.toList());
    }

    // ============ Statistics Methods ============

    /**
     * Get project-specific statistics.
     *
     * @param project the project
     * @param projectTasks the tasks in the project
     * @return map containing project stats (name, totalTasks, completedTasks, inProgressTasks,
     *         overdueCount, completionRate, tasksByStatus, tasksByPriority)
     */
    public static Map<String, Object> getProjectStats(Project project, List<Task> projectTasks) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("name", project.getName());
        stats.put("totalTasks", projectTasks.size());
        stats.put("completedTasks", getCompletedCount(projectTasks));
        stats.put("inProgressTasks", getInProgressCount(projectTasks));
        stats.put("overdueCount", getOverdueCount(projectTasks));
        stats.put("completionRate", getCompletionRate(projectTasks));
        stats.put("tasksByStatus", getTasksByStatus(projectTasks));
        stats.put("tasksByPriority", getTasksByPriority(projectTasks));

        return stats;
    }

    /**
     * Get overall dashboard statistics for all tasks and projects.
     *
     * @param allTasks all tasks
     * @param allProjects all projects
     * @return map containing dashboard stats (totalTasks, completedTasks, inProgressTasks,
     *         overdueCount, dueTodayCount, unassignedCount, completionRate, totalProjects,
     *         tasksByStatus, tasksByPriority, tasksByAssignee)
     */
    public static Map<String, Object> getDashboardStats(List<Task> allTasks, List<Project> allProjects) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalTasks", allTasks.size());
        stats.put("completedTasks", getCompletedCount(allTasks));
        stats.put("inProgressTasks", getInProgressCount(allTasks));
        stats.put("overdueCount", getOverdueCount(allTasks));
        stats.put("dueTodayCount", getDueTodayCount(allTasks));
        stats.put("unassignedCount", getUnassignedCount(allTasks));
        stats.put("completionRate", getCompletionRate(allTasks));
        stats.put("totalProjects", allProjects.size());
        stats.put("tasksByStatus", getTasksByStatus(allTasks));
        stats.put("tasksByPriority", getTasksByPriority(allTasks));
        stats.put("tasksByAssignee", getTasksByAssignee(allTasks));

        return stats;
    }

    // ============ Priority & Urgency Methods ============

    /**
     * Get priority urgency score (higher = more urgent).
     *
     * @param priority the priority level (Urgent, High, Medium, Low)
     * @return urgency score (4, 3, 2, 1, or 0)
     */
    public static int getPriorityScore(String priority) {
        return switch (priority) {
            case "Urgent" -> PRIORITY_URGENT;
            case "High" -> PRIORITY_HIGH;
            case "Medium" -> PRIORITY_MEDIUM;
            case "Low" -> PRIORITY_LOW;
            default -> PRIORITY_UNKNOWN;
        };
    }

    /**
     * Calculate task urgency based on priority and due date.
     * Higher score indicates more urgent task.
     *
     * @param task the task to score
     * @return urgency score
     */
    public static double getTaskUrgency(Task task) {
        double priorityScore = getPriorityScore(task.getPriority());
        double daysUntilDue = DEFAULT_DAYS_UNTIL_DUE;

        if (task.getDueDate() != null) {
            daysUntilDue = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), task.getDueDate());
            if (daysUntilDue < 0) {
                daysUntilDue = -daysUntilDue; // overdue tasks have higher urgency
            }
        }

        return priorityScore * (URGENCY_DIVISOR / (daysUntilDue + 1));
    }

    /**
     * Get sorted list of tasks by urgency (most urgent first).
     *
     * @param tasks the list of tasks
     * @return tasks sorted by urgency descending
     */
    public static List<Task> getSortedByUrgency(List<Task> tasks) {
        return tasks.stream()
                .sorted((a, b) -> Double.compare(getTaskUrgency(b), getTaskUrgency(a)))
                .collect(Collectors.toList());
    }
}
