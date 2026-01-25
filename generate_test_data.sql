-- ====================================================================
-- Generate Test Data: 10 Projects with 5 Tasks Each (Fixed Version)
-- ====================================================================
USE TaskManagementDB;
GO

-- ====================================================================
-- PROJECT 1: E-Commerce Platform
-- ====================================================================
INSERT INTO Projects (name, description, color, created_by, created_at)
SELECT 'E-Commerce Platform', 'Build a complete online shopping platform with payment integration', '#FF6B6B', 
       (SELECT TOP 1 id FROM Users WHERE username = 'admin'), GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Projects WHERE name = 'E-Commerce Platform');

DECLARE @proj1Id INT = (SELECT TOP 1 id FROM Projects WHERE name = 'E-Commerce Platform' ORDER BY id DESC);
DECLARE @adminId INT = (SELECT TOP 1 id FROM Users WHERE username = 'admin');
DECLARE @johnId INT = (SELECT TOP 1 id FROM Users WHERE username = 'john');
DECLARE @janeId INT = (SELECT TOP 1 id FROM Users WHERE username = 'jane');
DECLARE @bobId INT = (SELECT TOP 1 id FROM Users WHERE username = 'bob');

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Design database schema', 'Create comprehensive database design for products, orders, users', 'In Progress', 'High', DATEADD(DAY, 7, CAST(GETDATE() AS DATE)), @proj1Id, @johnId, @adminId, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Design database schema' AND project_id = @proj1Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Implement user authentication', 'Set up JWT-based authentication system with 2FA support', 'To Do', 'High', DATEADD(DAY, 10, CAST(GETDATE() AS DATE)), @proj1Id, @janeId, @adminId, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Implement user authentication' AND project_id = @proj1Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Create product catalog', 'Build product listing with filtering and search capabilities', 'To Do', 'Medium', DATEADD(DAY, 14, CAST(GETDATE() AS DATE)), @proj1Id, @bobId, @adminId, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Create product catalog' AND project_id = @proj1Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Integrate payment gateway', 'Connect Stripe/PayPal for secure payment processing', 'To Do', 'High', DATEADD(DAY, 21, CAST(GETDATE() AS DATE)), @proj1Id, @johnId, @adminId, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Integrate payment gateway' AND project_id = @proj1Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Setup CI/CD pipeline', 'Configure GitHub Actions for automated testing and deployment', 'To Do', 'Medium', DATEADD(DAY, 28, CAST(GETDATE() AS DATE)), @proj1Id, @janeId, @adminId, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Setup CI/CD pipeline' AND project_id = @proj1Id);
GO

-- ====================================================================
-- PROJECT 2: Mobile App Development
-- ====================================================================
INSERT INTO Projects (name, description, color, created_by, created_at)
SELECT 'Mobile App Development', 'Develop iOS and Android applications for our service', '#4ECDC4', 
       (SELECT TOP 1 id FROM Users WHERE username = 'admin'), GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Projects WHERE name = 'Mobile App Development');

DECLARE @proj2Id INT = (SELECT TOP 1 id FROM Projects WHERE name = 'Mobile App Development' ORDER BY id DESC);
DECLARE @admin2Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'admin');
DECLARE @john2Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'john');
DECLARE @jane2Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'jane');
DECLARE @bob2Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'bob');

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Set up React Native environment', 'Initialize and configure React Native project with required dependencies', 'Completed', 'High', DATEADD(DAY, 3, CAST(GETDATE() AS DATE)), @proj2Id, @john2Id, @admin2Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Set up React Native environment' AND project_id = @proj2Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Design UI/UX mockups', 'Create high-fidelity mockups for all app screens', 'In Progress', 'High', DATEADD(DAY, 8, CAST(GETDATE() AS DATE)), @proj2Id, @jane2Id, @admin2Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Design UI/UX mockups' AND project_id = @proj2Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Implement authentication flow', 'Add user login, signup, and password reset features', 'To Do', 'High', DATEADD(DAY, 12, CAST(GETDATE() AS DATE)), @proj2Id, @bob2Id, @admin2Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Implement authentication flow' AND project_id = @proj2Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Add push notifications', 'Integrate Firebase Cloud Messaging for push notifications', 'To Do', 'Medium', DATEADD(DAY, 18, CAST(GETDATE() AS DATE)), @proj2Id, @john2Id, @admin2Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Add push notifications' AND project_id = @proj2Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Implement offline mode', 'Add local data persistence and sync capabilities', 'To Do', 'Medium', DATEADD(DAY, 25, CAST(GETDATE() AS DATE)), @proj2Id, @jane2Id, @admin2Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Implement offline mode' AND project_id = @proj2Id);
GO

-- ====================================================================
-- PROJECT 3: Data Analytics Dashboard
-- ====================================================================
INSERT INTO Projects (name, description, color, created_by, created_at)
SELECT 'Data Analytics Dashboard', 'Real-time analytics and reporting dashboard', '#95E1D3', 
       (SELECT TOP 1 id FROM Users WHERE username = 'admin'), GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Projects WHERE name = 'Data Analytics Dashboard');

DECLARE @proj3Id INT = (SELECT TOP 1 id FROM Projects WHERE name = 'Data Analytics Dashboard' ORDER BY id DESC);
DECLARE @admin3Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'admin');
DECLARE @bob3Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'bob');
DECLARE @john3Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'john');
DECLARE @jane3Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'jane');

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Connect to data sources', 'Establish connections to databases and APIs for data ingestion', 'In Progress', 'High', DATEADD(DAY, 5, CAST(GETDATE() AS DATE)), @proj3Id, @bob3Id, @admin3Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Connect to data sources' AND project_id = @proj3Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Build visualization components', 'Create interactive charts, graphs, and custom visualizations', 'In Progress', 'High', DATEADD(DAY, 10, CAST(GETDATE() AS DATE)), @proj3Id, @john3Id, @admin3Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Build visualization components' AND project_id = @proj3Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Implement real-time updates', 'Add WebSocket support for live data updates', 'To Do', 'High', DATEADD(DAY, 15, CAST(GETDATE() AS DATE)), @proj3Id, @jane3Id, @admin3Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Implement real-time updates' AND project_id = @proj3Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Create custom reports', 'Allow users to generate and export custom reports', 'To Do', 'Medium', DATEADD(DAY, 20, CAST(GETDATE() AS DATE)), @proj3Id, @bob3Id, @admin3Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Create custom reports' AND project_id = @proj3Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Add user permissions', 'Implement role-based access control for reports and data', 'To Do', 'Medium', DATEADD(DAY, 24, CAST(GETDATE() AS DATE)), @proj3Id, @john3Id, @admin3Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Add user permissions' AND project_id = @proj3Id);
GO

-- ====================================================================
-- PROJECT 4: Cloud Migration
-- ====================================================================
INSERT INTO Projects (name, description, color, created_by, created_at)
SELECT 'Cloud Migration', 'Migrate legacy systems to AWS cloud infrastructure', '#F38181', 
       (SELECT TOP 1 id FROM Users WHERE username = 'admin'), GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Projects WHERE name = 'Cloud Migration');

DECLARE @proj4Id INT = (SELECT TOP 1 id FROM Projects WHERE name = 'Cloud Migration' ORDER BY id DESC);
DECLARE @admin4Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'admin');
DECLARE @jane4Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'jane');
DECLARE @john4Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'john');
DECLARE @bob4Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'bob');

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Audit existing infrastructure', 'Document current systems and identify migration candidates', 'Completed', 'High', DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), @proj4Id, @jane4Id, @admin4Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Audit existing infrastructure' AND project_id = @proj4Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Plan cloud architecture', 'Design scalable and fault-tolerant architecture on AWS', 'In Progress', 'High', DATEADD(DAY, 7, CAST(GETDATE() AS DATE)), @proj4Id, @john4Id, @admin4Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Plan cloud architecture' AND project_id = @proj4Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Set up networking and security', 'Configure VPCs, security groups, and SSL certificates', 'To Do', 'High', DATEADD(DAY, 12, CAST(GETDATE() AS DATE)), @proj4Id, @bob4Id, @admin4Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Set up networking and security' AND project_id = @proj4Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Migrate databases', 'Transfer data and set up replication for critical databases', 'To Do', 'High', DATEADD(DAY, 18, CAST(GETDATE() AS DATE)), @proj4Id, @jane4Id, @admin4Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Migrate databases' AND project_id = @proj4Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Performance testing and optimization', 'Benchmark and optimize cloud infrastructure', 'To Do', 'Medium', DATEADD(DAY, 25, CAST(GETDATE() AS DATE)), @proj4Id, @john4Id, @admin4Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Performance testing and optimization' AND project_id = @proj4Id);
GO

-- ====================================================================
-- PROJECT 5: AI/ML Integration
-- ====================================================================
INSERT INTO Projects (name, description, color, created_by, created_at)
SELECT 'AI/ML Integration', 'Integrate machine learning models for predictive analytics', '#AA96DA', 
       (SELECT TOP 1 id FROM Users WHERE username = 'admin'), GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Projects WHERE name = 'AI/ML Integration');

DECLARE @proj5Id INT = (SELECT TOP 1 id FROM Projects WHERE name = 'AI/ML Integration' ORDER BY id DESC);
DECLARE @admin5Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'admin');
DECLARE @john5Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'john');
DECLARE @bob5Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'bob');
DECLARE @jane5Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'jane');

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Collect and prepare training data', 'Gather and clean datasets for model training', 'In Progress', 'High', DATEADD(DAY, 6, CAST(GETDATE() AS DATE)), @proj5Id, @john5Id, @admin5Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Collect and prepare training data' AND project_id = @proj5Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Build prediction models', 'Develop and train ML models for user behavior prediction', 'In Progress', 'High', DATEADD(DAY, 12, CAST(GETDATE() AS DATE)), @proj5Id, @bob5Id, @admin5Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Build prediction models' AND project_id = @proj5Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Create model API endpoints', 'Build REST APIs to expose trained models', 'To Do', 'High', DATEADD(DAY, 16, CAST(GETDATE() AS DATE)), @proj5Id, @jane5Id, @admin5Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Create model API endpoints' AND project_id = @proj5Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Implement model monitoring', 'Set up tracking and alerting for model performance', 'To Do', 'Medium', DATEADD(DAY, 21, CAST(GETDATE() AS DATE)), @proj5Id, @john5Id, @admin5Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Implement model monitoring' AND project_id = @proj5Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Document model results', 'Create comprehensive documentation and business impact reports', 'To Do', 'Medium', DATEADD(DAY, 28, CAST(GETDATE() AS DATE)), @proj5Id, @jane5Id, @admin5Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Document model results' AND project_id = @proj5Id);
GO

-- ====================================================================
-- PROJECT 6: Security Audit
-- ====================================================================
INSERT INTO Projects (name, description, color, created_by, created_at)
SELECT 'Security Audit', 'Comprehensive security review and hardening of systems', '#FCBAD3', 
       (SELECT TOP 1 id FROM Users WHERE username = 'admin'), GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Projects WHERE name = 'Security Audit');

DECLARE @proj6Id INT = (SELECT TOP 1 id FROM Projects WHERE name = 'Security Audit' ORDER BY id DESC);
DECLARE @admin6Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'admin');
DECLARE @bob6Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'bob');
DECLARE @john6Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'john');
DECLARE @jane6Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'jane');

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Conduct vulnerability assessment', 'Perform security scanning and penetration testing', 'To Do', 'High', DATEADD(DAY, 9, CAST(GETDATE() AS DATE)), @proj6Id, @bob6Id, @admin6Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Conduct vulnerability assessment' AND project_id = @proj6Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Review code for security issues', 'Static analysis and manual code review for vulnerabilities', 'To Do', 'High', DATEADD(DAY, 14, CAST(GETDATE() AS DATE)), @proj6Id, @john6Id, @admin6Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Review code for security issues' AND project_id = @proj6Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Update dependencies', 'Upgrade vulnerable third-party libraries and packages', 'To Do', 'High', DATEADD(DAY, 18, CAST(GETDATE() AS DATE)), @proj6Id, @jane6Id, @admin6Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Update dependencies' AND project_id = @proj6Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Implement security best practices', 'Apply OWASP and industry security guidelines', 'To Do', 'Medium', DATEADD(DAY, 22, CAST(GETDATE() AS DATE)), @proj6Id, @bob6Id, @admin6Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Implement security best practices' AND project_id = @proj6Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Create security documentation', 'Document security architecture and compliance measures', 'To Do', 'Medium', DATEADD(DAY, 28, CAST(GETDATE() AS DATE)), @proj6Id, @john6Id, @admin6Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Create security documentation' AND project_id = @proj6Id);
GO

-- ====================================================================
-- PROJECT 7: Performance Optimization
-- ====================================================================
INSERT INTO Projects (name, description, color, created_by, created_at)
SELECT 'Performance Optimization', 'Improve application speed and resource utilization', '#A8E6CF', 
       (SELECT TOP 1 id FROM Users WHERE username = 'admin'), GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Projects WHERE name = 'Performance Optimization');

DECLARE @proj7Id INT = (SELECT TOP 1 id FROM Projects WHERE name = 'Performance Optimization' ORDER BY id DESC);
DECLARE @admin7Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'admin');
DECLARE @jane7Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'jane');
DECLARE @john7Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'john');
DECLARE @bob7Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'bob');

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Profile application performance', 'Identify bottlenecks and performance hotspots', 'Completed', 'High', DATEADD(DAY, 3, CAST(GETDATE() AS DATE)), @proj7Id, @jane7Id, @admin7Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Profile application performance' AND project_id = @proj7Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Optimize database queries', 'Add indexes and optimize slow SQL queries', 'In Progress', 'High', DATEADD(DAY, 8, CAST(GETDATE() AS DATE)), @proj7Id, @john7Id, @admin7Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Optimize database queries' AND project_id = @proj7Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Implement caching strategies', 'Add Redis and in-memory caching layer', 'To Do', 'High', DATEADD(DAY, 13, CAST(GETDATE() AS DATE)), @proj7Id, @bob7Id, @admin7Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Implement caching strategies' AND project_id = @proj7Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Frontend performance improvements', 'Optimize bundle size, lazy loading, and rendering', 'To Do', 'Medium', DATEADD(DAY, 19, CAST(GETDATE() AS DATE)), @proj7Id, @jane7Id, @admin7Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Frontend performance improvements' AND project_id = @proj7Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Setup performance monitoring', 'Implement APM tools for continuous performance tracking', 'To Do', 'Medium', DATEADD(DAY, 26, CAST(GETDATE() AS DATE)), @proj7Id, @john7Id, @admin7Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Setup performance monitoring' AND project_id = @proj7Id);
GO

-- ====================================================================
-- PROJECT 8: API Development
-- ====================================================================
INSERT INTO Projects (name, description, color, created_by, created_at)
SELECT 'API Development', 'Build comprehensive REST and GraphQL APIs', '#FFD3B6', 
       (SELECT TOP 1 id FROM Users WHERE username = 'admin'), GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Projects WHERE name = 'API Development');

DECLARE @proj8Id INT = (SELECT TOP 1 id FROM Projects WHERE name = 'API Development' ORDER BY id DESC);
DECLARE @admin8Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'admin');
DECLARE @john8Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'john');
DECLARE @bob8Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'bob');
DECLARE @jane8Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'jane');

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Design API specifications', 'Create OpenAPI/Swagger documentation for all endpoints', 'In Progress', 'High', DATEADD(DAY, 5, CAST(GETDATE() AS DATE)), @proj8Id, @john8Id, @admin8Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Design API specifications' AND project_id = @proj8Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Implement REST endpoints', 'Build CRUD operations for all resources', 'In Progress', 'High', DATEADD(DAY, 10, CAST(GETDATE() AS DATE)), @proj8Id, @bob8Id, @admin8Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Implement REST endpoints' AND project_id = @proj8Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Add GraphQL support', 'Create GraphQL schema and resolvers', 'To Do', 'Medium', DATEADD(DAY, 15, CAST(GETDATE() AS DATE)), @proj8Id, @jane8Id, @admin8Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Add GraphQL support' AND project_id = @proj8Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Implement rate limiting', 'Add API rate limiting and throttling mechanisms', 'To Do', 'Medium', DATEADD(DAY, 20, CAST(GETDATE() AS DATE)), @proj8Id, @john8Id, @admin8Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Implement rate limiting' AND project_id = @proj8Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Write API tests', 'Create comprehensive unit and integration tests', 'To Do', 'Medium', DATEADD(DAY, 25, CAST(GETDATE() AS DATE)), @proj8Id, @jane8Id, @admin8Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Write API tests' AND project_id = @proj8Id);
GO

-- ====================================================================
-- PROJECT 9: Documentation Overhaul
-- ====================================================================
INSERT INTO Projects (name, description, color, created_by, created_at)
SELECT 'Documentation Overhaul', 'Update and expand project documentation', '#FFAAA5', 
       (SELECT TOP 1 id FROM Users WHERE username = 'admin'), GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Projects WHERE name = 'Documentation Overhaul');

DECLARE @proj9Id INT = (SELECT TOP 1 id FROM Projects WHERE name = 'Documentation Overhaul' ORDER BY id DESC);
DECLARE @admin9Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'admin');
DECLARE @jane9Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'jane');
DECLARE @john9Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'john');
DECLARE @bob9Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'bob');

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Update README files', 'Improve setup instructions and project overview', 'Completed', 'Medium', DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), @proj9Id, @jane9Id, @admin9Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Update README files' AND project_id = @proj9Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Write developer guide', 'Create comprehensive guide for new developers', 'In Progress', 'Medium', DATEADD(DAY, 7, CAST(GETDATE() AS DATE)), @proj9Id, @john9Id, @admin9Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Write developer guide' AND project_id = @proj9Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Create API documentation', 'Document all API endpoints with examples', 'To Do', 'Medium', DATEADD(DAY, 12, CAST(GETDATE() AS DATE)), @proj9Id, @bob9Id, @admin9Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Create API documentation' AND project_id = @proj9Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Add architecture diagrams', 'Create visual representations of system architecture', 'To Do', 'Low', DATEADD(DAY, 18, CAST(GETDATE() AS DATE)), @proj9Id, @jane9Id, @admin9Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Add architecture diagrams' AND project_id = @proj9Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Create troubleshooting guide', 'Document common issues and their solutions', 'To Do', 'Medium', DATEADD(DAY, 24, CAST(GETDATE() AS DATE)), @proj9Id, @john9Id, @admin9Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Create troubleshooting guide' AND project_id = @proj9Id);
GO

-- ====================================================================
-- PROJECT 10: Testing Framework
-- ====================================================================
INSERT INTO Projects (name, description, color, created_by, created_at)
SELECT 'Testing Framework', 'Implement comprehensive testing infrastructure', '#FF8B94', 
       (SELECT TOP 1 id FROM Users WHERE username = 'admin'), GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Projects WHERE name = 'Testing Framework');

DECLARE @proj10Id INT = (SELECT TOP 1 id FROM Projects WHERE name = 'Testing Framework' ORDER BY id DESC);
DECLARE @admin10Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'admin');
DECLARE @bob10Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'bob');
DECLARE @john10Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'john');
DECLARE @jane10Id INT = (SELECT TOP 1 id FROM Users WHERE username = 'jane');

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Set up test environment', 'Configure testing frameworks and tools', 'Completed', 'High', DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), @proj10Id, @bob10Id, @admin10Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Set up test environment' AND project_id = @proj10Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Write unit tests', 'Achieve 80% code coverage with unit tests', 'In Progress', 'High', DATEADD(DAY, 10, CAST(GETDATE() AS DATE)), @proj10Id, @john10Id, @admin10Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Write unit tests' AND project_id = @proj10Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Create integration tests', 'Build integration tests for API and database', 'In Progress', 'High', DATEADD(DAY, 15, CAST(GETDATE() AS DATE)), @proj10Id, @jane10Id, @admin10Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Create integration tests' AND project_id = @proj10Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Setup automated testing', 'Configure CI/CD pipeline for automated testing', 'To Do', 'Medium', DATEADD(DAY, 20, CAST(GETDATE() AS DATE)), @proj10Id, @bob10Id, @admin10Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Setup automated testing' AND project_id = @proj10Id);

INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by, created_at)
SELECT 'Create test documentation', 'Document testing procedures and guidelines', 'To Do', 'Medium', DATEADD(DAY, 27, CAST(GETDATE() AS DATE)), @proj10Id, @john10Id, @admin10Id, GETDATE()
WHERE NOT EXISTS (SELECT 1 FROM Tasks WHERE title = 'Create test documentation' AND project_id = @proj10Id);
GO

-- ====================================================================
-- Summary
-- ====================================================================
SELECT 'Test data generation completed!' AS Status;
SELECT COUNT(*) AS TotalProjects FROM Projects WHERE name IN (
    'E-Commerce Platform', 'Mobile App Development', 'Data Analytics Dashboard', 
    'Cloud Migration', 'AI/ML Integration', 'Security Audit', 'Performance Optimization', 
    'API Development', 'Documentation Overhaul', 'Testing Framework'
);
SELECT COUNT(*) AS TotalTasks FROM Tasks WHERE project_id IN (
    SELECT id FROM Projects WHERE name IN (
        'E-Commerce Platform', 'Mobile App Development', 'Data Analytics Dashboard', 
        'Cloud Migration', 'AI/ML Integration', 'Security Audit', 'Performance Optimization', 
        'API Development', 'Documentation Overhaul', 'Testing Framework'
    )
);
