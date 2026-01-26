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
    
    private ObservableList<Project> projects = FXCollections.observableArrayList();
    private Project selectedProject;
    private ObservableList<Task> projectTasks = FXCollections.observableArrayList();
    private String currentViewType = VIEW_TABLE;

    public ProjectService() {
        this.projectRepository = new ProjectRepository();
    }
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

    public Project getProjectById(Long id) {
        Project project = projectRepository.findById(id);
        if (project == null) return null;

        // Permission check
        if (!CurrentUser.isAdmin() && !project.canEdit()) {
            return null; // Return null instead of throwing exception
        }
        return project;
    }

    public void deleteProject(Long id) {
        Project project = getProjectById(id);
        if (project == null || !project.canDelete()) {
            throw new SecurityException(ERR_NO_PERMISSION_DELETE);
        }

        projectRepository.delete(id);
    }

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

    public void setProjects(List<Project> projectList) {
        this.projects.clear();
        this.projects.addAll(projectList);
    }

    public ObservableList<Project> getProjects() {
        return projects;
    }

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

    public void setProjectTasks(List<Task> tasks) {
        this.projectTasks.clear();
        if (tasks != null) {
            this.projectTasks.addAll(tasks);
        }
    }

    public ObservableList<Task> getProjectTasks() {
        return projectTasks;
    }

    public ObservableList<Task> getTasksByStatus(String status) {
        return projectTasks.stream()
                .filter(t -> status.equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public void updateTaskStatus(Long taskId, String newStatus) {
        projectTasks.stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .ifPresent(t -> t.setStatus(newStatus));
    }

    public void addTask(Task task) {
        projectTasks.add(task);
    }

    public void removeTask(Long taskId) {
        projectTasks.removeIf(t -> t.getId().equals(taskId));
    }

    public void setViewType(String viewType) {
        this.currentViewType = viewType;
    }

    public String getViewType() {
        return currentViewType;
    }

    public boolean isTableView() {
        return VIEW_TABLE.equals(currentViewType);
    }

    public boolean isKanbanView() {
        return VIEW_KANBAN.equals(currentViewType);
    }

    public boolean isListView() {
        return VIEW_LIST.equals(currentViewType);
    }
    public void clearState() {
        projects.clear();
        selectedProject = null;
        projectTasks.clear();
        currentViewType = VIEW_TABLE;
    }
}