-- Task Management System - Simplified Schema
-- Removes: Workspace, Team, Project hierarchies
-- Focus: Tasks are the top-level entity

IF DB_ID('TaskManagementDB') IS NULL
BEGIN
    CREATE DATABASE TaskManagementDB;
END
GO

USE TaskManagementDB;
GO

SET NOCOUNT ON;
GO

-- ========== USERS TABLE ==========
IF OBJECT_ID('dbo.Users', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Users (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        username NVARCHAR(255) NOT NULL UNIQUE,
        email NVARCHAR(255) NULL,
        password_hash NVARCHAR(255) NOT NULL,
        role NVARCHAR(50) NOT NULL CONSTRAINT DF_Users_role DEFAULT 'USER',
        position NVARCHAR(255) NULL,
        created_at DATETIME2 NOT NULL CONSTRAINT DF_Users_created_at DEFAULT SYSDATETIME(),
        last_login DATETIME2 NULL,
        CONSTRAINT CK_Users_role CHECK (UPPER(role) IN ('ADMIN', 'USER'))
    );
    CREATE INDEX IDX_Users_username ON dbo.Users(username);
END
GO

-- ========== LABELS TABLE ==========
IF OBJECT_ID('dbo.Labels', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Labels (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(255) NOT NULL,
        color NVARCHAR(50) NOT NULL CONSTRAINT DF_Labels_color DEFAULT '#007BFF'
    );
END
GO

-- ========== TASKS TABLE (Simplified - No Project, Team, or Workspace) ==========
IF OBJECT_ID('dbo.Tasks', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Tasks (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        title NVARCHAR(255) NOT NULL,
        description NVARCHAR(MAX) NULL,
        status NVARCHAR(50) NOT NULL CONSTRAINT DF_Tasks_status DEFAULT 'To Do',
        priority NVARCHAR(50) NOT NULL CONSTRAINT DF_Tasks_priority DEFAULT 'Medium',
        due_date DATE NULL,
        assignee_id BIGINT NULL,
        created_by BIGINT NOT NULL,
        created_at DATETIME2 NOT NULL CONSTRAINT DF_Tasks_created_at DEFAULT SYSDATETIME(),
        updated_at DATETIME2 NOT NULL CONSTRAINT DF_Tasks_updated_at DEFAULT SYSDATETIME(),
        CONSTRAINT FK_Tasks_Users_assignee FOREIGN KEY (assignee_id)
            REFERENCES dbo.Users(id),
        CONSTRAINT FK_Tasks_Users_created_by FOREIGN KEY (created_by)
            REFERENCES dbo.Users(id),
        CONSTRAINT CK_Tasks_status CHECK (
            status IN ('Backlog', 'To Do', 'In Progress', 'Blocked', 'Done')
        ),
        CONSTRAINT CK_Tasks_priority CHECK (
            priority IN ('Critical', 'High', 'Medium', 'Low', 'None')
        )
    );
    CREATE INDEX IDX_Tasks_assignee_id ON dbo.Tasks(assignee_id);
    CREATE INDEX IDX_Tasks_created_by ON dbo.Tasks(created_by);
    CREATE INDEX IDX_Tasks_due_date ON dbo.Tasks(due_date);
    CREATE INDEX IDX_Tasks_status ON dbo.Tasks(status);
END
GO

-- ========== TASK LABELS JUNCTION TABLE ==========
IF OBJECT_ID('dbo.TaskLabels', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.TaskLabels (
        task_id BIGINT NOT NULL,
        label_id BIGINT NOT NULL,
        PRIMARY KEY (task_id, label_id),
        CONSTRAINT FK_TaskLabels_Tasks_task FOREIGN KEY (task_id)
            REFERENCES dbo.Tasks(id) ON DELETE CASCADE,
        CONSTRAINT FK_TaskLabels_Labels_label FOREIGN KEY (label_id)
            REFERENCES dbo.Labels(id) ON DELETE CASCADE
    );
END
GO

-- ========== ACTIVITY LOG TABLE ==========
IF OBJECT_ID('dbo.ActivityLog', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.ActivityLog (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        action NVARCHAR(50) NULL,
        entity_type NVARCHAR(50) NULL,
        entity_id BIGINT NULL,
        entity_name NVARCHAR(255) NULL,
        user_id BIGINT NULL,
        details NVARCHAR(MAX) NULL,
        [timestamp] DATETIME2 NOT NULL CONSTRAINT DF_ActivityLog_timestamp DEFAULT SYSDATETIME(),
        CONSTRAINT FK_ActivityLog_Users_user FOREIGN KEY (user_id)
            REFERENCES dbo.Users(id)
    );
    CREATE INDEX IDX_ActivityLog_user_id ON dbo.ActivityLog(user_id);
    CREATE INDEX IDX_ActivityLog_entity_id ON dbo.ActivityLog(entity_id);
END
GO

PRINT 'Database schema created successfully!'
PRINT 'Task Management System - Simplified (Tasks only, no hierarchy)'
GO
