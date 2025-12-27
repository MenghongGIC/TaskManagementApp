package com.taskmanagement.service;

import java.util.List;

import com.taskmanagement.model.Project;
import com.taskmanagement.model.Team;
import com.taskmanagement.model.User;
import com.taskmanagement.repository.ProjectRepository;
import com.taskmanagement.utils.CurrentUser;

public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService() {
        this.projectRepository = new ProjectRepository();
    }

    public Project createProject(String name, String description, String color, Team team) {
        if (!CurrentUser.canCreateProjects()) {
            throw new SecurityException("You don't have permission to create projects");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name is required");
        }

        User currentUser = CurrentUser.getInstance();
        if (currentUser == null) {
            throw new IllegalStateException("No user logged in");
        }

        Project project = new Project(name.trim(), currentUser);
        project.setDescription(description);
        project.setColor(color);
        project.setTeam(team);

        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        // ADMIN sees all, USER sees own + team projects
        List<Project> all = projectRepository.findAll();

        if (CurrentUser.isAdmin()) {
            return all;
        }

        User current = CurrentUser.getInstance();
        if (current == null) return List.of();

        return all.stream()
                .filter(p -> p.getCreatedBy().getId().equals(current.getId()) ||
                             (p.getTeam() != null && p.getTeam().isMemberCurrentUser()))
                .toList();
    }

    public Project getProjectById(Long id) {
        Project project = projectRepository.findById(id);
        if (project == null) return null;

        // Permission check
        if (CurrentUser.isAdmin() || project.canEdit()) {
            return project;
        }
        throw new SecurityException("Access denied to project");
    }

    public void deleteProject(Long id) {
        Project project = getProjectById(id);
        if (project == null || !project.canDelete()) {
            throw new SecurityException("You cannot delete this project");
        }

        projectRepository.delete(id);
    }

    public Project updateProject(Project updatedProject) {
        Project existing = getProjectById(updatedProject.getId());
        if (existing == null || !existing.canEdit()) {
            throw new SecurityException("You cannot edit this project");
        }

        existing.setName(updatedProject.getName());
        existing.setDescription(updatedProject.getDescription());
        existing.setColor(updatedProject.getColor());
        existing.setTeam(updatedProject.getTeam());

        return projectRepository.save(existing);
    }
}