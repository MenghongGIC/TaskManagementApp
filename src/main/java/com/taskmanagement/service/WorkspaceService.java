package com.taskmanagement.service;

import com.taskmanagement.model.Project;
import com.taskmanagement.model.Team;
import com.taskmanagement.model.User;
import com.taskmanagement.model.Workspace;
import com.taskmanagement.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class WorkspaceService {
    private static final List<Workspace> workspaces = new ArrayList<>();
    private static final AtomicLong idGenerator = new AtomicLong(1L);

    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final TeamService teamService;

    public WorkspaceService() {
        this.userRepository = new UserRepository();
        this.projectService = new ProjectService();
        this.teamService = new TeamService();
    }

    public Workspace createWorkspace(String workspaceName, String description, Long ownerId, List<Long> memberIds) {
        validateWorkspaceName(workspaceName);

        User owner = findUser(ownerId);
        if (owner == null) {
            throw new IllegalArgumentException("Workspace owner is required");
        }

        Workspace workspace = new Workspace(workspaceName.trim(), description, owner);
        workspace.setWorkspaceId(idGenerator.getAndIncrement());
        workspace.setCreatedAt(LocalDateTime.now());

        Set<User> members = resolveMembers(memberIds);
        members.add(owner);
        workspace.setMembers(members);

        workspaces.add(workspace);
        ActivityLogService.logActivity("WORKSPACE_CREATED", "WORKSPACE", workspace.getWorkspaceId(), workspace.getWorkspaceName());
        return workspace;
    }

    public List<Workspace> getAllWorkspaces() {
        return new ArrayList<>(workspaces);
    }

    public List<Workspace> getWorkspacesForUser(Long userId) {
        if (userId == null) {
            return List.of();
        }

        return workspaces.stream()
                .filter(workspace -> workspace.getOwner() != null && userId.equals(workspace.getOwner().getId())
                        || workspace.getMembers().stream().anyMatch(member -> userId.equals(member.getId())))
                .toList();
    }

    public Workspace getWorkspaceById(Long workspaceId) {
        if (workspaceId == null) {
            return null;
        }
        return workspaces.stream()
                .filter(w -> workspaceId.equals(w.getWorkspaceId()))
                .findFirst()
                .orElse(null);
    }

    public Workspace updateWorkspace(Workspace workspace) {
        Workspace existing = requireWorkspace(workspace.getWorkspaceId());
        validateWorkspaceName(workspace.getWorkspaceName());

        existing.setWorkspaceName(workspace.getWorkspaceName());
        existing.setDescription(workspace.getDescription());
        existing.setOwner(workspace.getOwner());
        existing.setMembers(new LinkedHashSet<>(workspace.getMembers()));
        ActivityLogService.logActivity("WORKSPACE_UPDATED", "WORKSPACE", existing.getWorkspaceId(), existing.getWorkspaceName());
        return existing;
    }

    public void addMember(Long workspaceId, Long memberId) {
        Workspace workspace = requireWorkspace(workspaceId);
        User member = findUser(memberId);
        if (member == null) {
            throw new IllegalArgumentException("Member not found");
        }
        workspace.addMember(member);
        ActivityLogService.logActivity("WORKSPACE_MEMBER_ADDED", "WORKSPACE", workspace.getWorkspaceId(), workspace.getWorkspaceName(), member.getUsername());
    }

    public void removeMember(Long workspaceId, Long memberId) {
        Workspace workspace = requireWorkspace(workspaceId);
        User member = findUser(memberId);
        if (member == null) {
            throw new IllegalArgumentException("Member not found");
        }
        workspace.removeMember(member);
        ActivityLogService.logActivity("WORKSPACE_MEMBER_REMOVED", "WORKSPACE", workspace.getWorkspaceId(), workspace.getWorkspaceName(), member.getUsername());
    }

    public void addProject(Long workspaceId, Long projectId) {
        Workspace workspace = requireWorkspace(workspaceId);
        Project project = projectService.getProjectById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Project not found");
        }
        workspace.addProject(project);
        ActivityLogService.logActivity("WORKSPACE_PROJECT_ADDED", "WORKSPACE", workspace.getWorkspaceId(), workspace.getWorkspaceName(), project.getName());
    }

    public void addTeam(Long workspaceId, Long teamId) {
        Workspace workspace = requireWorkspace(workspaceId);
        Team team = teamService.getTeamById(teamId);
        if (team == null) {
            throw new IllegalArgumentException("Team not found");
        }
        workspace.addTeam(team);
        ActivityLogService.logActivity("WORKSPACE_TEAM_ADDED", "WORKSPACE", workspace.getWorkspaceId(), workspace.getWorkspaceName(), team.getTeamName());
    }

    public List<User> getAvailableUsers() {
        return userRepository.findAll();
    }

    public List<Project> getAvailableProjects() {
        return projectService.getAllProjects();
    }

    public List<Team> getAvailableTeams() {
        return teamService.getAllTeams();
    }

    private Workspace requireWorkspace(Long workspaceId) {
        Workspace workspace = getWorkspaceById(workspaceId);
        if (workspace == null) {
            throw new IllegalArgumentException("Workspace not found");
        }
        return workspace;
    }

    private void validateWorkspaceName(String workspaceName) {
        if (workspaceName == null || workspaceName.trim().isEmpty()) {
            throw new IllegalArgumentException("Workspace name is required");
        }
    }

    private User findUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId);
    }

    private Set<User> resolveMembers(List<Long> memberIds) {
        Set<User> members = new LinkedHashSet<>();
        if (memberIds == null) {
            return members;
        }
        for (Long memberId : memberIds) {
            User member = findUser(memberId);
            if (member != null) {
                members.add(member);
            }
        }
        return members;
    }
}
