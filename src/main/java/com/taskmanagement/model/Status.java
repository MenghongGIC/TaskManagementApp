package com.taskmanagement.model;



public enum Status {
    BACKLOG("Backlog", "backlog"),
    TODO("To Do", "todo"),
    IN_PROGRESS("In Progress", "in-progress"),
    BLOCKED("Blocked", "blocked"),
    DONE("Done", "done");
    
    private final String displayName;
    private final String cssClass;
    
    Status(String displayName, String cssClass) {
        this.displayName = displayName;
        this.cssClass = cssClass;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getCssClass() {
        return cssClass;
    }
    
    

    public String getStyleClass() {
        return "status-" + cssClass;
    }
    
    

    public static Status fromString(String value) {
        if (value == null || value.isEmpty()) {
            return TODO;
        }
        
        
        String normalized = value.toUpperCase().replace(" ", "_");
        try {
            return Status.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return TODO;
        }
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
