package com.taskmanagement.service;

import java.util.List;
import java.util.stream.Collectors;

import com.taskmanagement.model.Project;
import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import com.taskmanagement.repository.ProjectRepository;
import com.taskmanagement.utils.CurrentUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Service for managing project operations and state management
 * 
 * Handles both:
 * 1. Project CRUD operations (create, read, update, delete)
 * 2. Global state management (selected project, tasks, view type)
 * 
 * NOTE: This class consolidates the functionality previously split between
 * ProjectService (CRUD) and ProjectState (state management). ProjectState
 * is no longer needed and can be removed from the codebase.
 * 
 * Usage:
 * - For CRUD operations: createProject(), updateProject(), deleteProject(), etc.
 * - For state: setProjects(), selectProject(), setProjectTasks(), setViewType(), etc.
 */
public class ProjectService {

    // Error Messages
    private static final String ERR_NO_PERMISSION_CREATE = "You don't have permission to create projects";
    private static final String ERR_PROJECT_NAME_REQUIRED = "Project name is required";
    private static final String ERR_NO_USER_LOGGED_IN = "No user logged in";
    private static final String ERR_NO_PERMISSION_DELETE = "You cannot delete this project";
    private static final String ERR_NO_PERMISSION_EDIT = "You cannot edit this project";
    
    // View Type Constants
    private static final String VIEW_TABLE = "table";
    private static final String VIEW_KANBAN = "kanban";
    private static final String VIEW_LIST = "list";

    private final ProjectRepository projectRepository;
    
    // ===== State Management Fields =====
    private ObservableList<Project> projects = FXCollections.observableArrayList();
    private Project selectedProject;
    private ObservableList<Task> projectTasks = FXCollections.observableArrayList();
    private String currentViewType = VIEW_TABLE;

    public ProjectService() {
        this.projectRepository = new ProjectRepository();
    }

    /**
     * Create a new project
     * 
     * @param name the project name (required)
     * @param description the project description
     * @param color the project color
     * @return the created project
     * @throws SecurityException if user doesn't have permission
     * @throws IllegalArgumentException if project name is empty
     */
    public Project createProject(String name, String description, String color) {
        if (!CurrentUser.canCreateProjects()) {
            throw new SecurityException(ERR_NO_PERMISSION_CREATE);
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(ERR_PROJECT_NAME_REQUIRED);
        }

        User currentUser = CurrentUser.getInstance();
        if (currentUser == null) {
            throw new IllegalStateException(ERR_NO_USER_LOGGED_IN);
        }

        Project project = new Project(name.trim(), currentUser);
        project.setDescription(description);
        project.setColor(color);

        return projectRepository.save(project);
    }

    /**
     * Get all projects accessible to current user
     * Admins see all projects, regular users see only their own
     * 
     * @return list of accessible projects
     */
    public List<Project> getAllProjects() {
        // ADMIN sees all, USER sees own projects
        List<Project> all = projectRepository.findAll();

        if (CurrentUser.isAdmin()) {
            return all;
        }

        User current = CurrentUser.getInstance();
        if (current == null) return List.of();

        return all.stream()
                .filter(p -> p.getCreatedBy().getId().equals(current.getId()))
                .toList();
    }

    /**
     * Get project by ID with permission checking
     * 
     * @param id the project ID
     * @return the project, or null if not found or user lacks permission
     */
    public Project getProjectById(Long id) {
        Project project = projectRepository.findById(id);
        if (project == null) return null;

        // Permission check
        if (!CurrentUser.isAdmin() && !project.canEdit()) {
            return null; // Return null instead of throwing exception
        }
        return project;
    }

    /**
     * Delete a project with permission checking
     * 
     * @param id the project ID
     * @throws SecurityException if user lacks permission
     */
    public void deleteProject(Long id) {
        Project project = getProjectById(id);
        if (project == null || !project.canDelete()) {
            throw new SecurityException(ERR_NO_PERMISSION_DELETE);
        }

        projectRepository.delete(id);
    }

    /**
     * Update a project
     * 
     * @param updatedProject the project with updated information
     * @return the updated project
     * @throws SecurityException if user lacks permission
     */
    public Project updateProject(Project updatedProject) {
        Project existing = getProjectById(updatedProject.getId());
        if (existing == null || !existing.canEdit()) {
            throw new SecurityException(ERR_NO_PERMISSION_EDIT);
        }

        existing.setName(updatedProject.getName());
        existing.setDescription(updatedProject.getDescription());
        existing.setColor(updatedProject.getColor());

        projectRepository.update(existing);
        return existing;
    }

    /**
     * Update project with individual field values
     * 
     * @param id the project ID
     * @param name the new project name
     * @param description the new description
     * @param color the new color
     * @return true if update successful
     * @throws SecurityException if user lacks permission
     */
    public boolean updateProject(Long id, String name, String description, String color) {
        Project existing = getProjectById(id);
        if (existing == null || !existing.canEdit()) {
            throw new SecurityException(ERR_NO_PERMISSION_EDIT);
        }

        existing.setName(name != null ? name.trim() : existing.getName());
        existing.setDescription(description);
        existing.setColor(color);

        projectRepository.update(existing);
        return true;
    }

    // ===== Projects Management =====
    /**
     * Set the projects list in state
     * 
     * @param projectList the projects to store
     */
    public void setProjects(List<Project> projectList) {
        this.projects.clear();
        this.projects.addAll(projectList);
    }

    /**
     * Get the observable projects list
     * 
     * @return the projects observable list
     */
    public ObservableList<Project> getProjects() {
        return projects;
    }

    // ===== Project Selection =====
    /**
     * Select a project and load its tasks
     * 
     * @param project the project to select
     */
    public void selectProject(Project project) {
        this.selectedProject = project;
        if (project != null && project.getTasks() != null) {
            this.projectTasks.clear();
            this.projectTasks.addAll(project.getTasks());
        }
    }

    /**
     * Get the currently selected project
     * 
     * @return the selected project, or null if none selected
     */
    public Project getSelectedProject() {
        return selectedProject;
    }

    /**
     * Get the ID of the currently selected project
     * 
     * @return the selected project ID, or null if none selected
     */
    public Long getSelectedProjectId() {
        return selectedProject != null ? selectedProject.getId() : null;
    }

    // ===== Task Management =====
    /**
     * Set the tasks for the selected project
     * 
     * @param tasks the tasks to store
     */
    public void setProjectTasks(List<Task> tasks) {
        this.projectTasks.clear();
        if (tasks != null) {
            this.projectTasks.addAll(tasks);
        }
    }

    /**
     * Get the observable list of project tasks
     * 
     * @return the project tasks observable list
     */
    public ObservableList<Task> getProjectTasks() {
        return projectTasks;
    }

    /**
     * Get tasks filtered by status without refetching from database
     * 
     * @param status the status to filter by
     * @return observable list of tasks with matching status
     */
    public ObservableList<Task> getTasksByStatus(String status) {
        return projectTasks.stream()
                .filter(t -> status.equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    /**
     * Update task status in state
     * 
     * @param taskId the task ID
     * @param newStatus the new status
     */
    public void updateTaskStatus(Long taskId, String newStatus) {
        projectTasks.stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .ifPresent(t -> t.setStatus(newStatus));
    }

    /**
     * Add a new task to the project tasks list
     * 
     * @param task the task to add
     */
    public void addTask(Task task) {
        projectTasks.add(task);
    }

    /**
     * Remove a task from the project tasks list
     * 
     * @param taskId the ID of the task to remove
     */
    public void removeTask(Long taskId) {
        projectTasks.removeIf(t -> t.getId().equals(taskId));
    }

    // ===== View Type Management =====
    /**
     * Set the current view type
     * 
     * @param viewType the view type (table, kanban, or list)
     */
    public void setViewType(String viewType) {
        this.currentViewType = viewType;
    }

    /**
     * Get the current view type
     * 
     * @return the current view type
     */
    public String getViewType() {
        return currentViewType;
    }

    /**
     * Check if current view is table view
     * 
     * @return true if in table view
     */
    public boolean isTableView() {
        return VIEW_TABLE.equals(currentViewType);
    }

    /**
     * Check if current view is kanban view
     * 
     * @return true if in kanban view
     */
    public boolean isKanbanView() {
        return VIEW_KANBAN.equals(currentViewType);
    }

    /**
     * Check if current view is list view
     * 
     * @return true if in list view
     */
    public boolean isListView() {
        return VIEW_LIST.equals(currentViewType);
    }

    // ===== Clear State =====
    /**
     * Clear all project state
     */
    public void clearState() {
        projects.clear();
        selectedProject = null;
        projectTasks.clear();
        currentViewType = VIEW_TABLE;
    }
}