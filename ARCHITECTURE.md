# Task Management System - Complete Architecture Guide

## 🏗️ System Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                    APPLICATION LAYOUT                   │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │ HEADER (FIXED - Never Changes)                  │  │
│  │ - Application Title                             │  │
│  │ - User Info                                     │  │
│  │ - Logout Button (ONLY logout in entire app)    │  │
│  └──────────────────────────────────────────────────┘  │
│                                                          │
│  ┌─────────────┬──────────────────────────────────┐   │
│  │             │                                  │   │
│  │  SIDEBAR    │      CONTENT AREA                │   │
│  │  (FIXED)    │      (DYNAMIC)                   │   │
│  │             │                                  │   │
│  │ Navigation  │  Changes based on:               │   │
│  │ - Dashboard │  1. Sidebar button click         │   │
│  │ - Projects  │  2. Project selection            │   │
│  │ - Tasks     │  3. View switching               │   │
│  │ - Admin     │                                  │   │
│  │             │  NO navigation buttons in        │   │
│  │ Project     │  content area                    │   │
│  │ Selection   │  NO logout button in             │   │
│  │ (Required)  │  content area                    │   │
│  │             │                                  │   │
│  │ Admin Panel │                                  │   │
│  │             │                                  │   │
│  └─────────────┴──────────────────────────────────┘   │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

## 📁 Directory Structure

```
src/main/
├── java/com/taskmanagement/
│   ├── controller/
│   │   ├── MainLayoutController.java        ✨ NEW - Main app controller
│   │   ├── TasksViewController.java         ✨ NEW - Unified task view
│   │   ├── DashboardController.java         (Legacy - can refactor later)
│   │   ├── ProjectController.java
│   │   ├── TaskDetailController.java
│   │   ├── LoginController.java
│   │   └── [other controllers...]
│   │
│   ├── service/
│   │   ├── TaskService.java
│   │   ├── ProjectService.java
│   │   └── [other services...]
│   │
│   ├── model/
│   │   ├── Task.java
│   │   ├── Project.java
│   │   └── [other models...]
│   │
│   └── utils/
│       ├── CurrentUser.java
│       └── [other utilities...]
│
└── resources/com/taskmanagement/
    ├── fxml/
    │   ├── auth/
    │   │   ├── LoginView.fxml
    │   │   └── RegisterView.fxml
    │   │
    │   ├── main/
    │   │   ├── MainLayout.fxml          ✨ NEW - Main app shell
    │   │   ├── TasksView.fxml           ✨ NEW - Unified task view
    │   │   ├── Dashboard.fxml           (Legacy)
    │   │   ├── ProjectListView.fxml
    │   │   ├── TaskDetailView.fxml
    │   │   └── [other views...]
    │   │
    │   ├── dialog/
    │   │   ├── CreateTaskView.fxml
    │   │   ├── CreateProjectView.fxml
    │   │   └── [other dialogs...]
    │   │
    │   ├── admin/
    │   │   └── AdminPanel.fxml
    │   │
    │   └── css/
    │       └── style.css                ✨ Enhanced - Comprehensive styling
    │
```

## 🎯 Key Features & Rules

### 1. Navigation Behavior ✅
- **Sidebar is FIXED and PERSISTENT**
  - Never reloads or changes
  - Always visible on every page
  - Only the center content changes
  
- **Navigation Options:**
  - 🏠 Dashboard - Overview page
  - 📁 Projects - Project management
  - ✓ Tasks - Task management (requires project selection)
  - 👨‍💼 Admin Panel - Admin controls

### 2. Header Rules ✅
- **Global Header (Top):**
  - Application title on the left
  - User info in the middle-right
  - **ONLY ONE Logout button (top-right)**
  - Breadcrumb navigation below

- **NO OTHER navigation or logout buttons allowed in content area**

### 3. Content Area Rules ✅
- **Displays Only:**
  - Page title relevant to current view
  - View switcher (Table/Kanban/List)
  - Search field
  - Filter dropdowns (Status, Priority)
  - Action buttons (Add, Refresh)
  - Task display area

- **NO navigation buttons**
- **NO logout button**
- **NO duplicate UI elements**

### 4. Project-First Workflow ✅
- **Project Selection Required**
  - Located in left sidebar
  - ComboBox with all available projects
  - Status indicator (selected/not selected)
  
- **Tasks Display**
  - Only shows tasks from selected project
  - If no project selected, shows warning message
  - Click "Tasks" button without project selection shows error

### 5. Task Display Modes ✅

All three views are in one unified `TasksView.fxml`:

#### **a) Table View**
```
┌─────────────────────────────────────────────┐
│ ID │ Task  │ Desc  │ Status │ Priority │ ... │
├─────────────────────────────────────────────┤
│ 1  │ Task1 │ ...   │ To Do  │ High     │ ... │
│ 2  │ Task2 │ ...   │ Done   │ Medium   │ ... │
└─────────────────────────────────────────────┘
```

#### **b) Kanban Board**
```
┌──────────────┬──────────────┬──────────────┐
│ 📌 To Do     │ ⚙️ Progress  │ ✅ Done      │
├──────────────┼──────────────┼──────────────┤
│ ┌──────────┐ │ ┌──────────┐ │ ┌──────────┐ │
│ │ Task 1   │ │ │ Task 3   │ │ │ Task 5   │ │
│ │ Priority │ │ │ Priority │ │ │ Priority │ │
│ │ Due Date │ │ │ Due Date │ │ │ Due Date │ │
│ └──────────┘ │ └──────────┘ │ └──────────┘ │
│              │              │              │
│ ┌──────────┐ │ ┌──────────┐ │              │
│ │ Task 2   │ │ │ Task 4   │ │              │
│ │ Priority │ │ │ Priority │ │              │
│ │ Due Date │ │ │ Due Date │ │              │
│ └──────────┘ │ └──────────┘ │              │
└──────────────┴──────────────┴──────────────┘
```
- **Drag-and-drop enabled**
- Click card to open details
- Status updates automatically on drop

#### **c) List View**
```
┌──────────────────────────────────────────┐
│ Task 1                    │ To Do   │ High │
│ Description of task 1                    │
├──────────────────────────────────────────┤
│ Task 2                    │ Done    │ Med  │
│ Description of task 2                    │
└──────────────────────────────────────────┘
```

### 6. Search & Filter ✅
Each task view includes:

**Search Field:**
- Real-time search
- Searches task name and description
- Instant filtering as you type

**Status Filter:**
- Dropdown: All, To Do, In Progress, Done
- Default: All

**Priority Filter:**
- Dropdown: All, Low, Medium, High
- Default: All

**Clear Button:**
- Resets all filters and search

**Dynamic Updates:**
- All views update instantly when filters change
- Status label shows active filters
- Task count updates in real-time

## 🔄 Data Flow

```
1. User Login
   └─> LoginController
       └─> CurrentUser.set(user)
           └─> App.setRoot("main/MainLayout")
               └─> MainLayoutController initializes

2. MainLayoutController Initialization
   └─> Load all projects into combo box
       └─> Listen for project selection
           └─> Load tasks for selected project

3. View Switching (Sidebar Navigation)
   └─> Button click → MainLayoutController
       └─> Load appropriate FXML into contentArea
           └─> Initialize controller
               └─> Load data for current project

4. Task Filtering
   └─> User types in search field
       └─> TasksViewController.applyFilters()
           └─> Filter observable list
               └─> Update all three views (hidden ones updated in background)

5. View Switching (Table/Kanban/List)
   └─> Button click → TasksViewController
       └─> Hide current view in StackPane
           └─> Show new view (already updated with filters)

6. Drag-Drop (Kanban Only)
   └─> User drags task card
       └─> Detect drag over drop zone
           └─> Accept drop
               └─> Update task status in database
                   └─> Refresh all views
```

## 🎨 Styling Guide

### CSS Classes Used:
- `.button` - Primary button style
- `.button-primary` - Blue primary actions
- `.button-success` - Green success actions
- `.button-danger` - Red delete actions
- `.sidebar-panel` - Sidebar container
- `.status-badge` - Status indicators
- `.priority-badge` - Priority indicators
- `.kanban-card` - Task cards in kanban
- `.table-view` - Table styling
- `.filter-panel` - Filter controls

### Color Scheme:
- **Primary:** #3498db (Blue) - Main actions
- **Success:** #27ae60 (Green) - Add/Create
- **Danger:** #e74c3c (Red) - Delete/Logout
- **Warning:** #f39c12 (Orange) - In Progress
- **Neutral:** #95a5a6 (Gray) - Secondary actions
- **Background:** #f8f9fa (Light Gray)
- **Text:** #2c3e50 (Dark Gray)
- **Borders:** #ecf0f1 (Light Border)

## 📝 FXML Best Practices

1. **Scene Builder Compatible**
   - All IDs properly defined
   - Controllers assigned to FXML files
   - Stylesheets linked correctly

2. **Layout Patterns**
   - BorderPane for main structure
   - VBox/HBox for containers
   - StackPane for view switching
   - FlowPane for kanban columns

3. **Accessibility**
   - Proper spacing and padding
   - Clear labels and placeholders
   - Keyboard navigation support
   - Tooltip hints on hover

## 🔐 Security Considerations

1. **Current User Management**
   - Uses `CurrentUser` singleton
   - Cleared on logout
   - Required for all operations

2. **Permission Checks**
   - Admin panel only visible to admins
   - Project operations check permissions
   - Task operations validate user access

3. **Data Isolation**
   - Tasks filtered by selected project
   - Users only see their own data
   - Admin can see all data

## 🚀 How to Use the System

### For Regular Users:
1. Login with credentials
2. Select project from sidebar dropdown
3. Switch between Table/Kanban/List views as needed
4. Search and filter tasks
5. Click task to view/edit details
6. Drag tasks in Kanban to change status
7. Logout when done

### For Admins:
1. Login with admin credentials
2. Use Admin Panel for user/project management
3. All user features available
4. Can access all projects and data

## 📊 Component Interaction Map

```
App.java (Main Entry)
    ↓
LoginView → LoginController
    ↓ (Success)
MainLayout.fxml ← MainLayoutController (CORE)
    ├─ Header (Global - Never Changes)
    ├─ Sidebar (Fixed - Never Changes)
    │   ├─ Dashboard Button → Load Dashboard.fxml
    │   ├─ Projects Button → Load ProjectListView.fxml
    │   ├─ Tasks Button → Load TasksView.fxml (requires project)
    │   ├─ Admin Button → Load AdminPanel.fxml
    │   └─ Project ComboBox → Set selected project
    │
    └─ ContentArea (Dynamic - Changes on Navigation)
        ├─ Dashboard.fxml ← DashboardController
        ├─ ProjectListView.fxml ← ProjectController
        ├─ TasksView.fxml ← TasksViewController (NEW - Main feature)
        │   ├─ Table View (TableView)
        │   ├─ Kanban View (FlowPane with drag-drop)
        │   └─ List View (VBox with task items)
        │
        └─ AdminPanel.fxml ← AdminController
```

## ✨ Unique Features

1. **Unified Task Management**
   - All three views in one place
   - Synchronized filtering across views
   - Instant view switching

2. **Smart Project Selection**
   - Required before task management
   - Visual feedback on status
   - Persistent across navigation

3. **Professional UI**
   - Clean, modern design
   - Color-coded status/priority
   - Smooth animations
   - Responsive layout

4. **Drag-Drop Integration**
   - Visual feedback during drag
   - Instant database updates
   - Automatic view refresh

5. **Real-Time Filtering**
   - Live search results
   - Multi-filter support
   - Dynamic status counts

## 🛠️ Extending the System

### Adding New View:
1. Create new FXML file in `src/main/resources/fxml/main/`
2. Create controller implementing `TaskAwareController`
3. Add navigation button in MainLayoutController
4. Load view in `loadView()` method

### Adding New Filter:
1. Add ComboBox in TasksView.fxml
2. Add listener in `setupSearchAndFilter()` 
3. Update filter logic in `applyFilters()`

### Customizing Styling:
1. Edit `src/main/resources/css/style.css`
2. Changes apply globally
3. Override with inline styles if needed

---

**Version:** 1.0.0  
**Last Updated:** January 24, 2026  
**Framework:** JavaFX 21  
**Architecture:** MVC with Controllers
