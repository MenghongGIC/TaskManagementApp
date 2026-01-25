package com.taskmanagement.state;

import com.taskmanagement.model.Project;
import com.taskmanagement.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global state management for project-based task management
 * Maintains project list, selected project, and filtered tasks
 */
public class ProjectState {
    private static ProjectState instance;
    
    private ObservableList<Project> projects = FXCollections.observableArrayList();
    private Project selectedProject;
    private ObservableList<Task> projectTasks = FXCollections.observableArrayList();
    private String currentViewType = "table";
    
    private ProjectState() {}
    
    public static ProjectState getInstance() {
        if (instance == null) {
            instance = new ProjectState();
        }
        return instance;
    }
    
    // ===== Projects Management =====
    public void setProjects(List<Project> projectList) {
        this.projects.clear();
        this.projects.addAll(projectList);
    }
    
    public ObservableList<Project> getProjects() {
        return projects;
    }
    
    // ===== Project Selection =====
    public void selectProject(Project project) {
        this.selectedProject = project;
        if (project != null && project.getTasks() != null) {
            this.projectTasks.clear();
            this.projectTasks.addAll(project.getTasks());
        }
    }
    
    public Project getSelectedProject() {
        return selectedProject;
    }
    
    public Long getSelectedProjectId() {
        return selectedProject != null ? selectedProject.getId() : null;
    }
    
    // ===== Task Management =====
    public void setProjectTasks(List<Task> tasks) {
        this.projectTasks.clear();
        if (tasks != null) {
            this.projectTasks.addAll(tasks);
        }
    }
    
    public ObservableList<Task> getProjectTasks() {
        return projectTasks;
    }
    
    /**
     * Get tasks filtered by status without refetching from database
     */
    public ObservableList<Task> getTasksByStatus(String status) {
        return projectTasks.stream()
                .filter(t -> status.equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }
    
    /**
     * Update task status in state
     */
    public void updateTaskStatus(Long taskId, String newStatus) {
        projectTasks.stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .ifPresent(t -> t.setStatus(newStatus));
    }
    
    /**
     * Add new task to project tasks
     */
    public void addTask(Task task) {
        projectTasks.add(task);
    }
    
    /**
     * Remove task from project tasks
     */
    public void removeTask(Long taskId) {
        projectTasks.removeIf(t -> t.getId().equals(taskId));
    }
    
    // ===== View Type Management =====
    public void setViewType(String viewType) {
        this.currentViewType = viewType;
    }
    
    public String getViewType() {
        return currentViewType;
    }
    
    public boolean isTableView() {
        return "table".equals(currentViewType);
    }
    
    public boolean isKanbanView() {
        return "kanban".equals(currentViewType);
    }
    
    public boolean isListView() {
        return "list".equals(currentViewType);
    }
    
    // ===== Clear State =====
    public void clear() {
        projects.clear();
        selectedProject = null;
        projectTasks.clear();
        currentViewType = "table";
    }
}
