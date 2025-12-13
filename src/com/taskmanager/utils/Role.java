package com.taskmanager.utils;


//User roles with clear permissions

public enum Role {
    
    EMPLOYEE("Employee"),      // Can view & complete own tasks
    SUPERVISOR("Supervisor"),  // Can assign tasks to team members
    MANAGER("Manager"),        // Can create projects, manage teams
    ADMIN("Admin");            // Full access (God mode)

    private final String displayName;
    
    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Convert from Strings
    public static Role fromString(String text) {
        if (text != null) {
            for (Role role : Role.values()) {
                if (text.equalsIgnoreCase(role.displayName) || text.equalsIgnoreCase(role.name())) {
                    return role;
                }
            }
        }
        return EMPLOYEE;
    }

    @Override
    public String toString() {
        return displayName;
    }
}