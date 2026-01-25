# Task Management System - UI/UX Guide

## 🎨 Design System Overview

This is a professional, enterprise-grade Task Management System UI built with JavaFX, following modern design principles and best practices.

## 📐 Layout Structure

### Global Layout (MainLayout.fxml)
```
┌─────────────────────────────────────────────────┐
│  📋 Task Management System     👤 User  [Logout] │  ← Header (Fixed)
├─────────────────────────────────────────────────┤
│ 📍 Breadcrumb Navigation                        │  ← Breadcrumb (Fixed)
├─────────────┬───────────────────────────────────┤
│             │                                   │
│   📌 SIDEBAR│                                   │
│  (FIXED)    │      CONTENT AREA                 │
│             │      (DYNAMIC)                    │
│  Navigation │      Changes based on:            │
│  Project    │      • Sidebar clicks             │
│  Selection  │      • Project selection          │
│  Admin      │      • View switching             │
│             │                                   │
│             │      No nav buttons here!         │
│             │      No logout button here!       │
│             │                                   │
└─────────────┴───────────────────────────────────┘
```

## 🎯 Component Specifications

### Header Bar
**Purpose:** Global app navigation  
**Location:** Top, Fixed  
**Content:**
- Application logo/title (left)
- User name (center-right)
- Logout button (top-right corner only)

**Styling:**
- Background: Dark blue (#2c3e50)
- Text: White
- Height: ~60px
- No navigation buttons
- No search bar

### Sidebar
**Purpose:** Main navigation  
**Location:** Left side, Fixed, 220px wide  
**Content:**
1. **Navigation Section**
   - 🏠 Dashboard (Blue by default)
   - 📁 Projects (Gray initially)
   - ✓ Tasks (Gray initially)
   - 👨‍💼 Admin Panel (Red)

2. **Project Selection**
   - Dropdown to select project
   - Status indicator (✓ selected / ❌ none)
   - Required for Tasks view

3. **Footer**
   - Version number (v1.0.0)

**Styling:**
- Background: Dark (#34495e)
- Text: White
- Buttons: Blue hover effect
- Max width: 220px
- No horizontal scrolling

### Content Area
**Purpose:** Dynamic page content  
**Location:** Center, right of sidebar  
**Behavior:**
- Changes when sidebar buttons clicked
- Changes when project selected
- **ZERO navigation UI in this area**
- **ZERO logout button in this area**

## 📋 Task Management Views

### View 1: Table View

**Purpose:** Detailed, spreadsheet-like task overview

**Layout:**
```
┌──────────────────────────────────────────────────┐
│ ✓ Task Management        🔍 Search  [Clear]      │
│ View: [Table] [Kanban] [List]                    │
│ Status: ○ All  ○ To Do  ○ In Progress  ○ Done    │
│ Priority: ○ All  ○ Low  ○ Medium  ○ High        │
├──────────────────────────────────────────────────┤
│ ID │ Task Name │ Desc │ Status │ Priority │ ...  │
├────┼───────────┼──────┼────────┼──────────┤      │
│ 1  │ Design UI │ ...  │ To Do  │ High     │ ...  │ ← Clickable row
│ 2  │ DB Setup  │ ...  │ Done   │ Medium   │ ...  │
└──────────────────────────────────────────────────┘
```

**Features:**
- Sortable columns
- Clickable rows (opens detail view)
- Status color-coded
- Priority color-coded
- Delete button in each row
- Status change dropdown in each row

**Columns:**
1. ID (50px)
2. Task Name (200px)
3. Description (200px)
4. Status (100px) - Badge
5. Priority (80px) - Badge
6. Due Date (100px)
7. Assignee (120px)
8. Actions (150px)

### View 2: Kanban Board

**Purpose:** Visual workflow management with drag-drop

**Layout:**
```
┌────────────────┬────────────────┬────────────────┐
│ 📌 To Do       │ ⚙️ In Progress │ ✅ Done        │
├────────────────┼────────────────┼────────────────┤
│ ┌────────────┐ │ ┌────────────┐ │ ┌────────────┐ │
│ │ Task 1     │ │ │ Task 3     │ │ │ Task 5     │ │
│ │ High       │ │ │ Medium     │ │ │ Low        │ │
│ │ Due 12/24  │ │ │ Due 12/26  │ │ │ Due 12/28  │ │
│ └────────────┘ │ └────────────┘ │ └────────────┘ │
│                │                │                │
│ ┌────────────┐ │ ┌────────────┐ │                │
│ │ Task 2     │ │ │ Task 4     │ │                │
│ │ Medium     │ │ │ High       │ │                │
│ │ Due 12/25  │ │ │ Due 12/27  │ │                │
│ └────────────┘ │ └────────────┘ │                │
└────────────────┴────────────────┴────────────────┘
```

**Features:**
- Three columns: To Do | In Progress | Done
- Draggable task cards
- Hover effects on cards
- Visual feedback during drag
- Click card to view details
- Automatic status update on drop

**Card Design:**
- Width: 180px
- Title: Bold, 11px
- Priority: Badge style
- Due date: Gray text, 10px
- Background: White with border
- Shadow on hover

**Column Headers:**
- Emoji + Text (e.g., "📌 To Do")
- Bold, 13px
- Dark text
- Light background

### View 3: List View

**Purpose:** Compact, readable task list

**Layout:**
```
┌─────────────────────────────────────────────────┐
│ Task 1 Name                    [To Do] [High]    │
│ This is the task description that can be long  │
├─────────────────────────────────────────────────┤
│ Task 2 Name                    [Done] [Medium]   │
│ Another task description here                  │
├─────────────────────────────────────────────────┤
│ Task 3 Name                    [In Progress]    │
│ Task description goes here                     │
└─────────────────────────────────────────────────┘
```

**Features:**
- Full-width task items
- Title bold, 12px
- Description italic, gray, 11px
- Status badge on right
- Priority badge on right
- Separator between items
- Clickable entire row

**Item Layout:**
```
Title                 Status    Priority
Description here
```

## 🎨 Color & Styling

### Color Palette

| Purpose | Color | HEX | Usage |
|---------|-------|-----|-------|
| Primary | Blue | #3498db | Buttons, active states |
| Success | Green | #27ae60 | Add/Create actions |
| Danger | Red | #e74c3c | Delete, Logout |
| Warning | Orange | #f39c12 | In Progress status |
| Neutral | Gray | #95a5a6 | Secondary buttons |
| Dark BG | Gray | #2c3e50 | Header, sidebar |
| Light BG | Gray | #f8f9fa | Page background |
| Border | Gray | #ecf0f1 | Lines, dividers |

### Status Badges

**To Do:**
- Background: Light red (#e74c3c)
- Text: White
- Padding: 4px 8px
- Border-radius: 3px

**In Progress:**
- Background: Orange (#f39c12)
- Text: White
- Padding: 4px 8px
- Border-radius: 3px

**Done:**
- Background: Green (#27ae60)
- Text: White
- Padding: 4px 8px
- Border-radius: 3px

### Priority Badges

**Low:**
- Background: Light blue (#dbeafe)
- Text: Dark blue (#0c4a6e)
- Padding: 3px 8px

**Medium:**
- Background: Yellow (#fcd34d)
- Text: Dark brown (#78350f)
- Padding: 3px 8px

**High:**
- Background: Light red (#fca5a5)
- Text: Dark red (#7f1d1d)
- Padding: 3px 8px

## 🔘 Button Styles

### Primary Action Button
- Background: Blue (#3498db)
- Text: White
- Padding: 10px 15px
- Border-radius: 4px
- Cursor: Hand
- Hover: Darker blue (#2980b9)

**Usage:** Save, Create, Add, Refresh

### Success Button
- Background: Green (#27ae60)
- Text: White
- Padding: 10px 15px
- Hover: Darker green (#229954)

**Usage:** Add Task, New Project

### Danger Button
- Background: Red (#e74c3c)
- Text: White
- Padding: 10px 15px
- Hover: Darker red (#c0392b)

**Usage:** Delete, Logout

### Secondary Button
- Background: Gray (#95a5a6)
- Text: White
- Padding: 10px 15px
- Hover: Darker gray (#7f8c8d)

**Usage:** Cancel, Clear

### Sidebar Button
- Background: Blue (#3498db) for active
- Background: Gray (#95a5a6) for inactive
- Text: White
- Padding: 12px 15px
- Hover: Darker shade
- Border-radius: 5px

## 📏 Spacing & Typography

### Typography
```
Header Title:        22px, Bold, White
Page Title:          28px, Bold, Dark
Section Header:      16px, Bold, Dark
Body Text:           12px, Regular, Dark
Small Text:          11px, Regular, Gray
Labels:              12px, Regular, Dark
Button Text:         12px, Bold, White
```

### Spacing
```
Page Padding:        20px
Component Gap:       10px
Button Padding:      10px 15px
Small Button:        8px 12px
Card Padding:        15px
Header Height:       ~60px
Sidebar Width:       220px
```

## 🎯 Search & Filter Layout

**Location:** Top of content area, below title

**Layout:**
```
┌─────────────────────────────────────────────┐
│ 🔍 Search field [Status] [Priority] [Clear] │
└─────────────────────────────────────────────┘
```

**Components:**
1. **Search TextField**
   - Placeholder: "🔍 Search by task name or description..."
   - Width: 400px (max)
   - Real-time filtering
   - Clears instantly on backspace

2. **Status ComboBox**
   - Options: All, To Do, In Progress, Done
   - Default: All
   - Width: 120px
   - Instant filtering

3. **Priority ComboBox**
   - Options: All, Low, Medium, High
   - Default: All
   - Width: 120px
   - Instant filtering

4. **Clear Button**
   - Text: "✕ Clear"
   - Resets all filters
   - Color: Gray (#95a5a6)

## ⌨️ Keyboard Navigation

- **Tab:** Navigate between controls
- **Enter:** Confirm action/submit
- **Esc:** Cancel dialog
- **Ctrl+F:** Focus search field
- **Ctrl+Shift+D:** Dashboard (could be added)

## 🖱️ Mouse Interactions

- **Hover:** Subtle color change on buttons
- **Click:** Immediate visual feedback
- **Drag (Kanban):** Visual opacity change
- **Row Select:** Highlight background

## ♿ Accessibility

- **Screen Reader:** All buttons have labels
- **Contrast:** WCAG AA compliant
- **Focus Indicator:** Visible focus states
- **Keyboard Access:** All features keyboard-accessible

## 📱 Responsive Design

**Minimum Window Size:** 1000px × 600px
**Recommended:** 1400px × 900px

**Responsive Behavior:**
- Sidebar fixed width, doesn't collapse
- Content area shrinks/grows
- Table columns reorder on small screens
- Kanban stacks vertically on very small screens

## 🎬 Animations & Transitions

- **Button Hover:** 200ms ease-in
- **Color Change:** 150ms ease-out
- **View Switch:** Instant (no animation)
- **Card Hover:** Subtle shadow effect
- **Drag Feedback:** Visual opacity change

## 🔐 Security UI Indicators

- **Project Not Selected:** Red warning label
- **Logout Button:** Red color (prominent)
- **Admin Panel:** Red button (restricted access)
- **Permission Denied:** Error dialog with clear message

## 📊 Data Display Patterns

### Empty State
```
┌──────────────────────────┐
│  No tasks found          │
│  Try selecting another   │
│  project or creating a   │
│  new task                │
└──────────────────────────┘
```

### Loading State
```
📥 Loading tasks...
```

### Error State
```
❌ Error loading tasks
Error details here
[Retry] button
```

### Success State
```
✅ Task created successfully
```

---

**Design Framework:** Material Design Lite (Inspired)  
**Color Science:** Accessible, WCAG AA Compliant  
**Typography:** System fonts (Segoe UI, Roboto, Helvetica)  
**Icons:** Unicode emojis for clarity and simplicity
