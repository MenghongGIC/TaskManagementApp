IF DB_ID('TaskManagementDB') IS NULL
BEGIN
    CREATE DATABASE TaskManagementDB;
END
GO

USE TaskManagementDB;
GO

SET NOCOUNT ON;
GO

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
END
GO

IF OBJECT_ID('dbo.Projects', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Projects (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(255) NOT NULL,
        description NVARCHAR(MAX) NULL,
        color NVARCHAR(50) NULL,
        created_by BIGINT NOT NULL,
        created_at DATETIME2 NOT NULL CONSTRAINT DF_Projects_created_at DEFAULT SYSDATETIME(),
        CONSTRAINT FK_Projects_Users_created_by FOREIGN KEY (created_by)
            REFERENCES dbo.Users(id)
    );
END
GO

IF OBJECT_ID('dbo.Workspaces', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Workspaces (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        workspace_name NVARCHAR(255) NOT NULL,
        description NVARCHAR(MAX) NULL,
        owner_id BIGINT NULL,
        created_at DATETIME2 NOT NULL CONSTRAINT DF_Workspaces_created_at DEFAULT SYSDATETIME(),
        CONSTRAINT FK_Workspaces_Users_owner FOREIGN KEY (owner_id)
            REFERENCES dbo.Users(id)
    );
END
GO

IF OBJECT_ID('dbo.Teams', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Teams (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        team_name NVARCHAR(255) NOT NULL,
        description NVARCHAR(MAX) NULL,
        team_lead_id BIGINT NULL,
        created_at DATETIME2 NOT NULL CONSTRAINT DF_Teams_created_at DEFAULT SYSDATETIME(),
        CONSTRAINT FK_Teams_Users_team_lead FOREIGN KEY (team_lead_id)
            REFERENCES dbo.Users(id)
    );
END
GO

IF OBJECT_ID('dbo.Labels', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Labels (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(255) NOT NULL,
        color NVARCHAR(50) NOT NULL CONSTRAINT DF_Labels_color DEFAULT '#007BFF'
    );
END
GO

IF OBJECT_ID('dbo.Tasks', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Tasks (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        title NVARCHAR(255) NOT NULL,
        description NVARCHAR(MAX) NULL,
        status NVARCHAR(50) NOT NULL CONSTRAINT DF_Tasks_status DEFAULT 'To Do',
        priority NVARCHAR(50) NOT NULL CONSTRAINT DF_Tasks_priority DEFAULT 'Medium',
        due_date DATE NULL,
        project_id BIGINT NOT NULL,
        assignee_id BIGINT NULL,
        created_by BIGINT NOT NULL,
        created_at DATETIME2 NOT NULL CONSTRAINT DF_Tasks_created_at DEFAULT SYSDATETIME(),
        CONSTRAINT FK_Tasks_Projects_project FOREIGN KEY (project_id)
            REFERENCES dbo.Projects(id) ON DELETE CASCADE,
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
END
GO

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
END
GO

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

IF OBJECT_ID('dbo.TeamMembers', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.TeamMembers (
        team_id BIGINT NOT NULL,
        user_id BIGINT NOT NULL,
        joined_at DATETIME2 NOT NULL CONSTRAINT DF_TeamMembers_joined_at DEFAULT SYSDATETIME(),
        PRIMARY KEY (team_id, user_id),
        CONSTRAINT FK_TeamMembers_Teams_team FOREIGN KEY (team_id)
            REFERENCES dbo.Teams(id) ON DELETE CASCADE,
        CONSTRAINT FK_TeamMembers_Users_user FOREIGN KEY (user_id)
            REFERENCES dbo.Users(id) ON DELETE CASCADE
    );
END
GO

IF OBJECT_ID('dbo.WorkspaceMembers', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.WorkspaceMembers (
        workspace_id BIGINT NOT NULL,
        user_id BIGINT NOT NULL,
        added_at DATETIME2 NOT NULL CONSTRAINT DF_WorkspaceMembers_added_at DEFAULT SYSDATETIME(),
        PRIMARY KEY (workspace_id, user_id),
        CONSTRAINT FK_WorkspaceMembers_Workspaces_workspace FOREIGN KEY (workspace_id)
            REFERENCES dbo.Workspaces(id) ON DELETE CASCADE,
        CONSTRAINT FK_WorkspaceMembers_Users_user FOREIGN KEY (user_id)
            REFERENCES dbo.Users(id) ON DELETE CASCADE
    );
END
GO

IF OBJECT_ID('dbo.WorkspaceProjects', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.WorkspaceProjects (
        workspace_id BIGINT NOT NULL,
        project_id BIGINT NOT NULL,
        linked_at DATETIME2 NOT NULL CONSTRAINT DF_WorkspaceProjects_linked_at DEFAULT SYSDATETIME(),
        PRIMARY KEY (workspace_id, project_id),
        CONSTRAINT FK_WorkspaceProjects_Workspaces_workspace FOREIGN KEY (workspace_id)
            REFERENCES dbo.Workspaces(id) ON DELETE CASCADE,
        CONSTRAINT FK_WorkspaceProjects_Projects_project FOREIGN KEY (project_id)
            REFERENCES dbo.Projects(id) ON DELETE CASCADE
    );
END
GO

IF OBJECT_ID('dbo.WorkspaceTeams', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.WorkspaceTeams (
        workspace_id BIGINT NOT NULL,
        team_id BIGINT NOT NULL,
        linked_at DATETIME2 NOT NULL CONSTRAINT DF_WorkspaceTeams_linked_at DEFAULT SYSDATETIME(),
        PRIMARY KEY (workspace_id, team_id),
        CONSTRAINT FK_WorkspaceTeams_Workspaces_workspace FOREIGN KEY (workspace_id)
            REFERENCES dbo.Workspaces(id) ON DELETE CASCADE,
        CONSTRAINT FK_WorkspaceTeams_Teams_team FOREIGN KEY (team_id)
            REFERENCES dbo.Teams(id) ON DELETE CASCADE
    );
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE username = 'admin')
BEGIN
    INSERT INTO dbo.Users (username, email, password_hash, role)
    VALUES ('admin', 'admin@gmail.com', 'admin12345', 'ADMIN');
END
GO

PRINT 'Database schema generated from model classes successfully.';
GO
