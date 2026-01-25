package com.taskmanagement.model;



public enum Priority {
    CRITICAL("Critical", "critical"),      
    HIGH("High", "high"),                  
    MEDIUM("Medium", "medium"),            
    LOW("Low", "low"),                     
    NONE("None", "none");                  
    
    private final String displayName;
    private final String cssClass;
    
    Priority(String displayName, String cssClass) {
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
        return "priority-" + cssClass;
    }
    
    

    public static Priority fromString(String value) {
        if (value == null || value.isEmpty()) {
            return NONE;
        }
        try {
            return Priority.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
