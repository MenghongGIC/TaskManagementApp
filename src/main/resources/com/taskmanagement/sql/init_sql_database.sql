USE TaskManagementDB;
GO

-- Create Users table
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'Users')
BEGIN
    CREATE TABLE Users (
        id INT IDENTITY(1,1) PRIMARY KEY,
        username NVARCHAR(255) UNIQUE NOT NULL,
        password_hash NVARCHAR(255) NOT NULL,
        email NVARCHAR(255),
        role NVARCHAR(50) DEFAULT 'USER',
        position NVARCHAR(255),
        is_blocked BIT DEFAULT 0,
        created_at DATETIME DEFAULT GETDATE(),
        last_login DATETIME
    );
END
ELSE
BEGIN
    -- Add is_blocked column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Users' AND COLUMN_NAME = 'is_blocked')
    BEGIN
        ALTER TABLE Users ADD is_blocked BIT DEFAULT 0;
    END
END
GO

-- Create Projects table
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'Projects')
BEGIN
    CREATE TABLE Projects (
        id INT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(255) NOT NULL,
        description NVARCHAR(MAX),
        color NVARCHAR(50),
        created_by INT NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        FOREIGN KEY (created_by) REFERENCES Users(id)
    );
END
GO

-- Create Labels table
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'Labels')
BEGIN
    CREATE TABLE Labels (
        id INT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(255) NOT NULL,
        color NVARCHAR(50)
    );
END
GO

-- Create Tasks table
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'Tasks')
BEGIN
    CREATE TABLE Tasks (
        id INT IDENTITY(1,1) PRIMARY KEY,
        title NVARCHAR(255) NOT NULL,
        description NVARCHAR(MAX),
        status NVARCHAR(50) DEFAULT 'To Do',
        priority NVARCHAR(50) DEFAULT 'Medium',
        due_date DATE,
        project_id INT NOT NULL,
        assignee_id INT,
        created_by INT NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        FOREIGN KEY (project_id) REFERENCES Projects(id) ON DELETE CASCADE,
        FOREIGN KEY (assignee_id) REFERENCES Users(id),
        FOREIGN KEY (created_by) REFERENCES Users(id)
    );
END
GO

-- Create TaskLabels table
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'TaskLabels')
BEGIN
    CREATE TABLE TaskLabels (
        task_id INT NOT NULL,
        label_id INT NOT NULL,
        PRIMARY KEY (task_id, label_id),
        FOREIGN KEY (task_id) REFERENCES Tasks(id) ON DELETE CASCADE,
        FOREIGN KEY (label_id) REFERENCES Labels(id)
    );
END
GO

-- Create ActivityLog table
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'ActivityLog')
BEGIN
    CREATE TABLE ActivityLog (
        id INT IDENTITY(1,1) PRIMARY KEY,
        action NVARCHAR(50),
        entity_type NVARCHAR(50),
        entity_id INT,
        entity_name NVARCHAR(255),
        actor_id INT,
        details NVARCHAR(MAX),
        timestamp DATETIME DEFAULT GETDATE(),
        FOREIGN KEY (actor_id) REFERENCES Users(id)
    );
END
GO

-- Insert admin user
IF NOT EXISTS (SELECT 1 FROM Users WHERE username = 'admin')
BEGIN
    INSERT INTO Users (username, password_hash, email, role)
    VALUES ('admin', 'admin12345', 'admin@gmail.com', 'ADMIN');
END
GO

-- Insert sample users
IF NOT EXISTS (SELECT 1 FROM Users WHERE username = 'john')
BEGIN
    INSERT INTO Users (username, password_hash, email, role)
    VALUES ('john', 'password123', 'john@gmail.com', 'USER');
END
GO

IF NOT EXISTS (SELECT 1 FROM Users WHERE username = 'jane')
BEGIN
    INSERT INTO Users (username, password_hash, email, role)
    VALUES ('jane', 'password123', 'jane@gmail.com', 'USER');
END
GO

IF NOT EXISTS (SELECT 1 FROM Users WHERE username = 'bob')
BEGIN
    INSERT INTO Users (username, password_hash, email, role)
    VALUES ('bob', 'password123', 'bob@gmail.com', 'USER');
END
GO

-- Insert labels
IF NOT EXISTS (SELECT 1 FROM Labels WHERE name = 'Bug')
BEGIN
    INSERT INTO Labels (name, color) VALUES
    ('Bug', '#DC3545'),
    ('Feature', '#28A745'),
    ('Urgent', '#FD7E14'),
    ('Improvement', '#FFC107'),
    ('Documentation', '#17A2B8');
END
GO

-- Insert sample projects
IF NOT EXISTS (SELECT 1 FROM Projects WHERE name = 'Task Management App')
BEGIN
    INSERT INTO Projects (name, description, color, created_by)
    VALUES 
    ('Task Management App', 'A comprehensive task management application', '#007BFF', 1),
    ('Mobile App', 'Mobile application development project', '#28A745', 1),
    ('Website Redesign', 'Redesigning the company website', '#FFC107', 1);
END
GO

-- Insert sample tasks for Project 1
IF NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Setup Database Schema')
BEGIN
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
END
GO

-- Insert sample tasks for Project 2
IF NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Design Mobile UI')
BEGIN
    INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by)
    VALUES 
    ('Design Mobile UI', 'Create mockups and design for mobile application', 'In Progress', 'High', '2026-01-15', 2, 2, 1),
    ('Setup Mobile Development Environment', 'Configure development tools and SDKs', 'Done', 'High', '2025-12-27', 2, 3, 1),
    ('Implement Mobile Authentication', 'Add login and registration for mobile app', 'To Do', 'High', '2026-02-01', 2, NULL, 1);
END
GO

-- Insert sample tasks for Project 3
IF NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Design New Website Layout')
BEGIN
    INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by)
    VALUES 
    ('Design New Website Layout', 'Create modern website design', 'In Progress', 'High', '2026-01-20', 3, 2, 1),
    ('Content Migration', 'Migrate existing content to new website', 'To Do', 'Medium', '2026-02-05', 3, NULL, 1),
    ('Setup Hosting', 'Configure web hosting and domain', 'To Do', 'High', '2026-01-30', 3, 3, 1);
END
GO

PRINT 'Database initialization complete!';
GO
