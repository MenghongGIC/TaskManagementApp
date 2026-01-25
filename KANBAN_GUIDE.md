# Extensible Priority & Status System for Kanban Board

## Overview

This implementation extends your existing Kanban board with:
- **Priority system** (visual styling via CSS classes)
- **Status system** (workflow-based column placement)
- **Complete independence** between the two systems

### Key Principles
✅ Priority is **visual only** (affects card styling)  
✅ Status determines **column placement** (workflow stage)  
✅ Any priority can exist in any status  
✅ No hardcoded colors (all CSS-based)  
✅ Minimal code changes to existing implementation  

---

## Architecture Overview

```
Task Model
├── title
├── description
├── priority (String: "Critical", "High", "Medium", "Low", "None")
├── status (String: "To Do", "In Progress", "Done", "Blocked", "Backlog")
└── Other fields (dueDate, assignee, etc.)

Priority Enum (Priority.java)
├── CRITICAL → CSS class: priority-critical
├── HIGH → CSS class: priority-high
├── MEDIUM → CSS class: priority-medium
├── LOW → CSS class: priority-low
└── NONE → CSS class: priority-none

Status Enum (Status.java)
├── BACKLOG → Column routing logic
├── TODO → Column routing logic
├── IN_PROGRESS → Column routing logic
├── BLOCKED → Column routing logic
└── DONE → Column routing logic

DashboardController (View Logic)
├── createKanbanTaskCard() → applies Priority CSS class
├── updateKanbanView() → routes tasks to correct Status column
└── All styling comes from CSS, not hardcoded
```

---

## How Priority Works

### Priority Enum (Priority.java)

```java
public enum Priority {
    CRITICAL("Critical", "critical"),      // Red
    HIGH("High", "high"),                  // Orange
    MEDIUM("Medium", "medium"),            // Yellow
    LOW("Low", "low"),                     // Green
    NONE("None", "none");                  // Gray
    
    private final String displayName;
    private final String cssClass;
    
    // Generates CSS class name: priority-critical, priority-high, etc.
    public String getStyleClass() {
        return "priority-" + cssClass;
    }
}
```

### Applying Priority to Task Cards

In `DashboardController.createKanbanTaskCard()`:

```java
private VBox createKanbanTaskCard(Task task) {
    VBox card = new VBox(8);
    // ... base styling ...
    
    // Extract priority and apply CSS class
    if (task.getPriority() != null) {
        Priority priority = Priority.fromString(task.getPriority());
        card.getStyleClass().add(priority.getStyleClass());
        // Result: adds "priority-critical", "priority-high", etc.
    }
    
    // ... rest of card creation ...
}
```

### Priority CSS Classes (style.css)

```css
/* CRITICAL - Red border, 3px, strong shadow */
.priority-critical {
    -fx-border-color: #E74C3C !important;
    -fx-border-width: 3 !important;
    -fx-effect: dropshadow(gaussian, rgba(231, 76, 60, 0.3), 6, 0, 0, 2);
}

/* HIGH - Orange border, 2px, moderate shadow */
.priority-high {
    -fx-border-color: #F39C12 !important;
    -fx-border-width: 2 !important;
    -fx-effect: dropshadow(gaussian, rgba(243, 156, 18, 0.2), 4, 0, 0, 1);
}

/* MEDIUM - Yellow border, 2px, light shadow */
.priority-medium {
    -fx-border-color: #F1C40F !important;
    -fx-border-width: 2 !important;
    -fx-effect: dropshadow(gaussian, rgba(241, 196, 15, 0.15), 3, 0, 0, 1);
}
.priority-low {
    -fx-border-color: #27AE60 !important;
    -fx-border-width: 1 !important;
    -fx-effect: dropshadow(gaussian, rgba(39, 174, 96, 0.1), 2, 0, 0, 1);
}

.priority-none {
    -fx-border-color: #95A5A6 !important;
    -fx-border-width: 1 !important;
    -fx-effect: dropshadow(gaussian, rgba(149, 165, 166, 0.1), 2, 0, 0, 1);
}
```

---

## How Status Works

### Status Enum (Status.java)

```java
public enum Status {
    BACKLOG("Backlog", "backlog"),
    TODO("To Do", "todo"),
    IN_PROGRESS("In Progress", "in-progress"),
    BLOCKED("Blocked", "blocked"),
    DONE("Done", "done");
    
    private final String displayName;
    private final String cssClass;
    
    // Converts "To Do" → TODO, "In Progress" → IN_PROGRESS
    public static Status fromString(String value) {
        String normalized = value.toUpperCase().replace(" ", "_");
        return Status.valueOf(normalized);
    }
}
```

### Routing Tasks to Columns

In `DashboardController.updateKanbanView()`:

```java
private void updateKanbanView() {
    // Clear existing cards from columns
    todoColumn.getChildren().clear();
    inProgressColumn.getChildren().clear();
    doneColumn.getChildren().clear();
    
    // Distribute tasks based on status
    for (Task task : filteredTasks) {
        VBox card = createKanbanTaskCard(task);
        
        // Route to correct column based on status
        if ("To Do".equals(task.getStatus())) {
            todoColumn.getChildren().add(card);
        } else if ("In Progress".equals(task.getStatus())) {
            inProgressColumn.getChildren().add(card);
        } else if ("Done".equals(task.getStatus())) {
            doneColumn.getChildren().add(card);
        }
    }
}
```

---

## Extensibility

### ✅ Adding a New Priority (5 minutes)

**Step 1:** Add to `Priority.java`

```java
public enum Priority {
    CRITICAL("Critical", "critical"),
    HIGH("High", "high"),
    MEDIUM("Medium", "medium"),
    LOW("Low", "low"),
    URGENT("Urgent", "urgent"),  // ← NEW
    NONE("None", "none");
}
```

**Step 2:** Add CSS rule to `style.css`

```css
/* URGENT - Hot pink, highest priority after critical */
.priority-urgent {
    -fx-border-color: #D81B60 !important;
    -fx-border-width: 3 !important;
    -fx-effect: dropshadow(gaussian, rgba(216, 27, 96, 0.3), 6, 0, 0, 2);
}
```

**Step 3:** Done! ✅
- Priority automatically appears in UI
- CSS class automatically applied to cards
- No other code changes needed
- Database already stores as string

---

### ✅ Adding a New Status (3 minutes)

**Step 1:** Add to `Status.java`

```java
public enum Status {
    BACKLOG("Backlog", "backlog"),
    TODO("To Do", "todo"),
    IN_PROGRESS("In Progress", "in-progress"),
    UNDER_REVIEW("Under Review", "under-review"),  // ← NEW
    BLOCKED("Blocked", "blocked"),
    DONE("Done", "done");
}
```

**Step 2:** Add column routing in `DashboardController.updateKanbanView()`

```java
private void updateKanbanView() {
    // ... clear columns ...
    
    for (Task task : filteredTasks) {
        VBox card = createKanbanTaskCard(task);
        
        if ("To Do".equals(task.getStatus())) {
            todoColumn.getChildren().add(card);
        } else if ("Under Review".equals(task.getStatus())) {
            underReviewColumn.getChildren().add(card);  // ← NEW
        } else if ("In Progress".equals(task.getStatus())) {
            inProgressColumn.getChildren().add(card);
        } 
        // ... etc
    }
}
```

**Step 3:** (Optional) Add CSS styling in `style.css`

```css
.status-under-review {
    -fx-background-color: #E8EAF6;
    -fx-border-color: #3F51B5;
    -fx-border-width: 1;
}
```

---

## Complete Example: Task Creation

```java
// Create a task with both priority and status
Task task = new Task("Implement login feature", project, currentUser);

// Set priority (affects card color/border)
task.setPriority("Critical");  // Will apply .priority-critical CSS

// Set status (determines column)
task.setStatus("In Progress");  // Will appear in In Progress column

// Save to database
taskService.save(task);

// Result: Card appears in "In Progress" column with red 3px border
```

---

## Visual Reference

### Priority Visual Indicators

| Priority | Border | Width | Effect | Use Case |
|----------|--------|-------|--------|----------|
| CRITICAL | Red (#E74C3C) | 3px | Strong shadow | Immediate action required |
| HIGH | Orange (#F39C12) | 2px | Moderate shadow | Important, do soon |
| MEDIUM | Yellow (#F1C40F) | 2px | Light shadow | Standard priority |
| LOW | Green (#27AE60) | 1px | Minimal shadow | Can wait |
| NONE | Gray (#95A5A6) | 1px | Minimal shadow | Backlog/future |

### Status Columns

| Status | Purpose | Usage |
|--------|---------|-------|
| BACKLOG | Not yet assigned or ready | Planning phase |
| TODO | Ready to start | Assigned to team member |
| IN_PROGRESS | Currently being worked on | Work in progress |
| BLOCKED | Waiting on something external | Dependency issue |
| DONE | Completed | Ready for review/deployment |

---

## File Locations

```
src/main/java/com/taskmanagement/
├── model/
│   ├── Task.java ..................... Has priority & status fields
│   ├── Priority.java ................. Enum with CSS class names
│   └── Status.java ................... Enum for workflow stages
│
├── controller/
│   └── DashboardController.java ....... Applies priority CSS, routes by status
│
└── resources/com/taskmanagement/css/
    └── style.css ..................... Priority CSS rules
```

---

## FAQ & Troubleshooting

**Q: Can a task have Critical priority and be in Backlog status?**  
A: Yes! They are completely independent. A task can be critical but not yet assigned to the backlog.

**Q: What happens if I change a priority but don't add the CSS rule?**  
A: The priority enum will work, but the card won't have custom styling. Add the CSS rule to apply visual changes.

**Q: How do I change the colors of existing priorities?**  
A: Edit only the CSS rules in `style.css`. For example:
```css
.priority-high {
    -fx-border-color: #FF0000 !important;  /* Changed from #F39C12 */
}
```

**Q: Can I add a status without modifying DashboardController?**  
A: For a basic implementation, you'd need to add routing logic. However, you could create a more dynamic routing system:
```java
private void routeTaskToColumn(Task task, VBox card) {
    Status status = Status.fromString(task.getStatus());
    
    switch(status) {
        case BACKLOG -> backlogColumn.getChildren().add(card);
        case TODO -> todoColumn.getChildren().add(card);
        case IN_PROGRESS -> inProgressColumn.getChildren().add(card);
        case BLOCKED -> blockedColumn.getChildren().add(card);
        case DONE -> doneColumn.getChildren().add(card);
    }
}
```

**Q: How are priority/status stored in the database?**  
A: As strings (VARCHAR). The enums are purely for Java code organization and CSS mapping.

---

## Summary

| Aspect | Implementation |
|--------|-----------------|
| **Priority** | Enum → CSS class → Card border styling |
| **Status** | Enum → Column routing logic |
| **Independence** | Task has both independently |
| **Extensibility** | Add enum value + CSS rule (priority) or enum value + routing (status) |
| **No Hardcoding** | All colors are CSS-based |
| **Database** | Both stored as strings (compatible with existing schema) |
