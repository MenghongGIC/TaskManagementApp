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
 * Analytics utility for generating dashboard statistics and metrics
 */
public class DashboardAnalytics {

    /**
     * Calculate overall task completion rate
     */
    public static double getCompletionRate(List<Task> tasks) {
        if (tasks.isEmpty()) return 0.0;
        long completed = tasks.stream().filter(Task::isCompleted).count();
        return (completed * 100.0) / tasks.size();
    }

    /**
     * Get count of overdue tasks
     */
    public static long getOverdueCount(List<Task> tasks) {
        return tasks.stream().filter(Task::isOverdue).count();
    }

    /**
     * Get count of tasks due today
     */
    public static long getDueTodayCount(List<Task> tasks) {
        return tasks.stream().filter(Task::isDueToday).count();
    }

    /**
     * Get count of completed tasks
     */
    public static long getCompletedCount(List<Task> tasks) {
        return tasks.stream().filter(Task::isCompleted).count();
    }

    /**
     * Get count of in-progress tasks
     */
    public static long getInProgressCount(List<Task> tasks) {
        return tasks.stream().filter(t -> "In Progress".equalsIgnoreCase(t.getStatus())).count();
    }

    /**
     * Get count of unassigned tasks
     */
    public static long getUnassignedCount(List<Task> tasks) {
        return tasks.stream().filter(Task::isUnassigned).count();
    }

    /**
     * Get task distribution by status
     */
    public static Map<String, Long> getTasksByStatus(List<Task> tasks) {
        return tasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));
    }

    /**
     * Get task distribution by priority
     */
    public static Map<String, Long> getTasksByPriority(List<Task> tasks) {
        return tasks.stream()
                .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));
    }

    /**
     * Get task distribution by assignee
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
     * Get task distribution by project
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
     * Get team workload (tasks per team member)
     */
    public static Map<User, Long> getTeamWorkload(List<Task> tasks) {
        return tasks.stream()
                .filter(t -> t.getAssignee() != null)
                .collect(Collectors.groupingBy(Task::getAssignee, Collectors.counting()));
    }

    /**
     * Get tasks due within the next N days
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

    /**
     * Get project statistics
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
     * Get overall dashboard statistics
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

    /**
     * Get priority urgency score (higher = more urgent)
     */
    public static int getPriorityScore(String priority) {
        return switch (priority) {
            case "Urgent" -> 4;
            case "High" -> 3;
            case "Medium" -> 2;
            case "Low" -> 1;
            default -> 0;
        };
    }

    /**
     * Calculate task urgency (based on priority and due date)
     */
    public static double getTaskUrgency(Task task) {
        double priorityScore = getPriorityScore(task.getPriority());
        double daysUntilDue = 10; // default

        if (task.getDueDate() != null) {
            daysUntilDue = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), task.getDueDate());
            if (daysUntilDue < 0) daysUntilDue = -daysUntilDue; // overdue tasks have higher urgency
        }

        return priorityScore * (10.0 / (daysUntilDue + 1));
    }

    /**
     * Get sorted list of tasks by urgency
     */
    public static List<Task> getSortedByUrgency(List<Task> tasks) {
        return tasks.stream()
                .sorted((a, b) -> Double.compare(getTaskUrgency(b), getTaskUrgency(a)))
                .collect(Collectors.toList());
    }
}
