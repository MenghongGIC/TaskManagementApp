package com.taskmanagement.model;

public enum Role {
    ADMIN("Admin"),
    USER("User");

    private final String displayName;

    Role(String displayName) { 
        this.displayName = displayName; 
    }

    public String getDisplayName() {return displayName;}

    public static Role fromString(String text) {
        if (text != null) {
            for (Role role : Role.values()) {
                if (text.equalsIgnoreCase(role.displayName) || text.equalsIgnoreCase(role.name())) {
                    return role;
                }
            }
        }
        return USER;
    }
    @Override
    public String toString() {
        return displayName;
    }
    // Permissions
    public boolean ViewOwnTasks() { return this == USER; }
    public boolean CompleteOwnTasks() { return this == USER; }
    public boolean ViewAllTasks() { return this == ADMIN || this == USER; }
    public boolean AssignTasks() { return this == ADMIN; }
    public boolean CreateProjects() { return this == ADMIN || this == USER; }
    public boolean DeleteProject() { return this == ADMIN; }
    public boolean ManageTeams() { return this == ADMIN; }
    public boolean ManageUsers() { return this == ADMIN; }
    public boolean FullAccess() { return this == ADMIN; }
    public boolean ViewPublicProjects() { return true; }
    public boolean RegisterOrLogin() { return this == USER; }
}