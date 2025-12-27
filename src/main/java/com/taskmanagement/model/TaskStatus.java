package com.taskmanagement.model;

public enum TaskStatus {
    TO_DO("To Do"),
    IN_PROGRESS("In Progress"),
    DONE("Done");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TaskStatus fromString(String value) {
        if (value != null) {
            for (TaskStatus status : TaskStatus.values()) {
                if (status.displayName.equalsIgnoreCase(value)) {
                    return status;
                }
            }
        }
        return TO_DO;
    }
}
