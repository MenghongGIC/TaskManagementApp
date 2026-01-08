CREATE DATABASE TaskManagementDB;
GO

-- Users table
CREATE TABLE Users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    password_hash NVARCHAR(255) NOT NULL,
    email NVARCHAR(100),
    role NVARCHAR(20) DEFAULT 'USER',
    position NVARCHAR(100),
    created_at DATETIME2 DEFAULT GETDATE(),
    last_login DATETIME2 NULL
);
GO

CREATE TABLE Teams (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL UNIQUE,
        description NVARCHAR(MAX),
        created_by BIGINT NOT NULL,
        created_at DATETIME2 DEFAULT GETDATE(),
        FOREIGN KEY (created_by) REFERENCES Users(id) ON DELETE NO ACTION
    );
    GO

    -- TeamMembers junction table (many-to-many)
    CREATE TABLE TeamMembers (
        team_id BIGINT NOT NULL,
        user_id BIGINT NOT NULL,
        joined_at DATETIME2 DEFAULT GETDATE(),
        PRIMARY KEY (team_id, user_id),
        FOREIGN KEY (team_id) REFERENCES Teams(id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
    );
    GO

    -- Projects table
    CREATE TABLE Projects (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        description NVARCHAR(MAX),
        color NVARCHAR(7),  -- e.g., '#007BFF'
        created_by BIGINT NOT NULL,
        team_id BIGINT NULL,
        created_at DATETIME2 DEFAULT GETDATE(),
        FOREIGN KEY (created_by) REFERENCES Users(id),
        FOREIGN KEY (team_id) REFERENCES Teams(id) ON DELETE SET NULL
    );
    GO

    -- Labels table (e.g., Bug, Feature, Urgent)
    CREATE TABLE Labels (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(50) NOT NULL UNIQUE,
        color NVARCHAR(20) DEFAULT '#007BFF'
    );
    GO

    -- Tasks table
    CREATE TABLE Tasks (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        title NVARCHAR(100) NOT NULL,
        description NVARCHAR(MAX),
        status NVARCHAR(20) DEFAULT 'To Do',
        priority NVARCHAR(20) DEFAULT 'Medium',
        due_date DATE NULL,
        project_id BIGINT NULL,
        assignee_id BIGINT NULL,
        created_by BIGINT NOT NULL,
        created_at DATETIME2 DEFAULT GETDATE(),
        FOREIGN KEY (project_id) REFERENCES Projects(id) ON DELETE CASCADE,
        FOREIGN KEY (assignee_id) REFERENCES Users(id) ON DELETE SET NULL,
        FOREIGN KEY (created_by) REFERENCES Users(id)
    );
    GO

    -- TaskLabels junction table (many-to-many)
    CREATE TABLE TaskLabels (
        task_id BIGINT NOT NULL,
        label_id BIGINT NOT NULL,
        PRIMARY KEY (task_id, label_id),
        FOREIGN KEY (task_id) REFERENCES Tasks(id) ON DELETE CASCADE,
        FOREIGN KEY (label_id) REFERENCES Labels(id) ON DELETE CASCADE
    );
    GO

    -- Comments table
    CREATE TABLE Comments (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        content NVARCHAR(MAX) NOT NULL,
        task_id BIGINT NOT NULL,
        author_id BIGINT NOT NULL,
        created_at DATETIME2 DEFAULT GETDATE(),
        FOREIGN KEY (task_id) REFERENCES Tasks(id) ON DELETE CASCADE,
        FOREIGN KEY (author_id) REFERENCES Users(id)
    );
    GO

    -- ActivityLog table for audit trail
    CREATE TABLE ActivityLog (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        action NVARCHAR(50) NOT NULL,
        entity_type NVARCHAR(50) NOT NULL,
        entity_id BIGINT NOT NULL,
        entity_name NVARCHAR(255),
        actor_id BIGINT,
        details NVARCHAR(MAX),
        timestamp DATETIME2 DEFAULT GETDATE(),
        FOREIGN KEY (actor_id) REFERENCES Users(id) ON DELETE SET NULL
    );
    GO

    -- =============================================
    -- Insert default data
    -- =============================================

    -- Admin user (username: admin, password: admin12345)
    INSERT INTO Users (username, password_hash, email, role)
    VALUES ('admin', 'admin12345', 'admin@gmail.com', 'ADMIN');
    GO

    -- Sample users
    INSERT INTO Users (username, password_hash, email, role)
    VALUES 
    ('john', 'password123', 'john@gmail.com', 'USER'),
    ('jane', 'password123', 'jane@gmail.com', 'USER'),
    ('bob', 'password123', 'bob@gmail.com', 'USER');
    GO

    -- Some default labels
    INSERT INTO Labels (name, color) VALUES
    ('Bug', '#DC3545'),
    ('Feature', '#28A745'),
    ('Urgent', '#FD7E14'),
    ('Improvement', '#FFC107'),
    ('Documentation', '#17A2B8');
    GO

    -- Sample team
    INSERT INTO Teams (name, description, created_by)
    VALUES ('Development Team', 'Main development team', 1);
    GO

    -- Add team members
    INSERT INTO TeamMembers (team_id, user_id) VALUES
    (1, 1),
    (1, 2),
    (1, 3);
    GO

    -- Sample projects
    INSERT INTO Projects (name, description, color, created_by, team_id)
    VALUES 
    ('Task Management App', 'A comprehensive task management application', '#007BFF', 1, 1),
    ('Mobile App', 'Mobile application development project', '#28A745', 1, 1),
    ('Website Redesign', 'Redesigning the company website', '#FFC107', 1, 1);
    GO

    -- Sample tasks for Project 1
    INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by)
    VALUES 
    ('Setup Database Schema', 'Create all necessary database tables and relationships', 'Done', 'High', '2025-12-20', 1, 2, 1),
    ('Create Login UI', 'Design and implement user login interface', 'Done', 'High', '2025-12-21', 1, 2, 1),
    ('Implement Drag and Drop', 'Add drag-and-drop functionality for task management', 'In Progress', 'High', '2025-12-28', 1, 3, 1),
    ('Add User Registration', 'Create registration form for new users', 'Done', 'Medium', '2025-12-25', 1, 3, 1),
    ('Create Dashboard', 'Build the main Kanban dashboard', 'In Progress', 'High', '2025-12-30', 1, 2, 1),
    ('Add Task Categories', 'Implement task categorization feature', 'To Do', 'Medium', '2026-01-10', 1, NULL, 1),
    ('Implement Notifications', 'Add notification system for task updates', 'To Do', 'Low', '2026-01-15', 1, NULL, 1),
    ('Database Performance Optimization', 'Optimize database queries and indexes', 'To Do', 'Low', '2026-01-20', 1, NULL, 1);
    GO

    -- Sample tasks for Project 2
    INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by)
    VALUES 
    ('Design Mobile UI', 'Create mockups and design for mobile application', 'In Progress', 'High', '2026-01-15', 2, 2, 1),
    ('Setup Mobile Development Environment', 'Configure development tools and SDKs', 'Done', 'High', '2025-12-27', 2, 3, 1),
    ('Implement Mobile Authentication', 'Add login and registration for mobile app', 'To Do', 'High', '2026-02-01', 2, NULL, 1);
    GO

    -- Sample tasks for Project 3
    INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by)
    VALUES 
    ('Design New Website Layout', 'Create modern website design', 'In Progress', 'High', '2026-01-20', 3, 2, 1),
    ('Content Migration', 'Migrate existing content to new website', 'To Do', 'Medium', '2026-02-05', 3, NULL, 1),
    ('Setup Hosting', 'Configure web hosting and domain', 'To Do', 'High', '2026-01-30', 3, 3, 1);
    GO

    -- Sample comments
    INSERT INTO Comments (content, task_id, author_id)
    VALUES 
    ('This task is almost complete. Need to test on different browsers.', 3, 2),
    ('Great progress on the dashboard! Keep it up.', 5, 1),
    ('We should prioritize this for the next sprint.', 6, 1),
    ('Mobile design looks great! Moving forward with development.', 9, 2);
    GO
