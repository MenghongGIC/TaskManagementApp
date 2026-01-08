package com.taskmanagement.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;        
    private Role role = Role.USER;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private String position; // Position/role/skill (e.g., 'Backend Developer', 'Designer')

    public User() {}

    public User(String username, String passwordHash, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role != null ? role : Role.USER;
    }

    public User(String username, String email, String passwordHash, Role role) {
        this(username, passwordHash, role);
        this.email = email;
    }

    public User(Long id, String username, String email, String passwordHash, Role role,
                LocalDateTime createdAt, LocalDateTime lastLogin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role != null ? role : Role.USER;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    // Getters and Setters
    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}

    public String getUsername(){return username;}
    public void setUsername(String username){this.username = username;}

    public String getEmail() { return email;}
    public void setEmail(String email) { this.email = email;}

    public String getPasswordHash(){return passwordHash;}
    public void setPasswordHash(String passwordHash) {this.passwordHash = passwordHash;}

    public Role getRole(){return role;}
    public void setRole(Role role){this.role = role != null ? role : Role.USER;}

    public LocalDateTime getCreatedAt() {return createdAt;}

    public void setCreatedAt(LocalDateTime createdAt){this.createdAt = createdAt;}

    public LocalDateTime getLastLogin(){return lastLogin;}

    public void setLastLogin(LocalDateTime lastLogin){this.lastLogin = lastLogin;}

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    // Convenience methods
    public String getRoleDisplayName() {
        return role != null ? role.getDisplayName() : "Unknown";
    }

    public boolean hasRole(Role roleToCheck) { return this.role == roleToCheck;}
    public boolean isAdmin() {return role == Role.ADMIN;}
    public boolean isUser() {return role == Role.USER;}

    // Permission checks delegated to Role enum
    public boolean canViewOwnTasks()         { return role.ViewOwnTasks(); }
    public boolean canCompleteOwnTasks()     { return role.CompleteOwnTasks(); }
    public boolean canViewAllTasks()         { return role.ViewAllTasks(); }
    public boolean canAssignTasks()          { return role.AssignTasks(); }
    public boolean canCreateProjects()       { return role.CreateProjects(); }
    public boolean canDeleteProject()        { return role.DeleteProject(); }
    public boolean canManageTeams()          { return role.ManageTeams(); }
    public boolean canManageUsers()          { return role.ManageUsers(); }
    public boolean hasFullAccess()           { return role.FullAccess(); }
    public boolean canViewPublicProjects()   { return role.ViewPublicProjects(); }
    public boolean canRegisterOrLogin()      { return role.RegisterOrLogin(); }

    @Override
    public String toString() {
        return username + " (" + getRoleDisplayName() + ")" + (id != null ? " [#" + id + "]" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        User user = (User) o;

        if (id != null && user.id != null) {
            return Objects.equals(id, user.id);
        }

        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id != null ? id : username);
    }
}