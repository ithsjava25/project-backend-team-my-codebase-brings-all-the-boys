-- ==========================================
-- TEST QUERIES FOR SCHOOL PORTAL DATABASE
-- ==========================================

SELECT *
FROM class_enrollments
WHERE user_id NOT IN (SELECT id
                      FROM users
                      WHERE deleted = false);

-- 1. Check if roles were created (by data.sql)
SELECT *
FROM roles;

-- 2. Insert a Test Teacher (ID 1 will be used if it's the first)
-- NOTE: In a real app, passwords would be BCrypt hashed.
INSERT INTO users (username, password, email, role_id, created_at, updated_at)
VALUES ('teacher_john', 'password123', 'john@school.edu', 2, NOW(), NOW());

-- 3. Insert a Test Student (ID 2)
INSERT INTO users (username, password, email, role_id, created_at, updated_at)
VALUES ('student_jane', 'password123', 'jane@school.edu', 3, NOW(), NOW());

-- 4. Verify Users with their Roles (JOIN)
SELECT u.username, u.email, r.name as role_name
FROM users u
         JOIN roles r ON u.role_id = r.id;

-- 5. Create a Test Assignment (Ärende)
INSERT INTO assignments (title, description, creator_id, status, created_at, updated_at)
VALUES ('Math Homework #1', 'Complete exercises 1-10 on page 42.', 1, 'CREATED', NOW(), NOW());

-- 6. Hand out the assignment to Jane (UserAssignment)
INSERT INTO user_assignments (assignment_id, student_id, status, created_at, updated_at)
VALUES (1, 2, 'ASSIGNED', NOW(), NOW());

-- 7. View Jane's assignments with Titles and Status
SELECT u.username, a.title, ua.status
FROM user_assignments ua
         JOIN users u ON ua.student_id = u.id
         JOIN assignments a ON ua.assignment_id = a.id
WHERE u.username = 'student_jane';

-- 8. Check Activity Logs (Audit Trail)
-- (These are usually created automatically by CaseService in Java)
SELECT *
FROM activity_logs
ORDER BY timestamp DESC;
