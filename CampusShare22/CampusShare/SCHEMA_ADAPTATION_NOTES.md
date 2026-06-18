# Schema Adaptation Summary

## Changes Made to Original Schema

### ✅ COMPATIBLE ELEMENTS (No Changes Needed)
The original schema correctly aligns with your Java models:
- `user` table structure matches User.java fields (username, password, full_name, email, role, dept_id, is_active, created_at)
- `material` table supports approval workflow (status, reviewed_by, review_comment)
- `course` table links to academic_year and semester correctly
- All foreign keys and relationships are properly defined
- ENUM types for role ('student', 'teacher', 'admin') and material status ('pending', 'approved', 'rejected')

### ⚠️ CHANGES MADE (For Project Compatibility)

#### 1. **Added profile_pic Column to User Table**
```sql
-- ADDED:
profile_pic VARCHAR(255),

-- In context:
CREATE TABLE user (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    ...
    profile_pic VARCHAR(255),  -- ← NEW (from User.java model)
    is_active BOOLEAN DEFAULT TRUE,
    ...
);
```
**Reason:** User.java model has a `profilePic` field that wasn't in the original schema

#### 2. **Password Hashing - Critical Change**
```sql
-- WRONG (Original - Plain Text):
INSERT INTO user (..., password, ...) VALUES 
(..., 'admin123', ...);

-- CORRECT (Adapted - SHA-256 Hashed):
INSERT INTO user (..., password, ...) VALUES 
(..., '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', ...);
```
**Reason:** 
- UserDAO.hashPassword() uses SHA-256 hashing before insertion
- Plain text passwords in SQL would fail validation at login
- Hashes computed from same algorithm as Java code

**Hash Reference:**
```
admin123   → 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
teacher123 → e54070a2569f16d2994c9284249a2a7cc3c9a5198d00921a97d9198642220d91
student123 → ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f
```

#### 3. **Enhanced Sample Data**
```sql
-- More descriptive course descriptions for testing
-- Added course descriptions that match typical CS curriculum

-- Added more sample materials with approved status
-- Allows immediate testing of student dashboard without admin approval flow
INSERT INTO material (...) VALUES
(1, 2, 'Course Syllabus', 'Complete course syllabus...', 'file', 'syllabus.pdf', 'approved', 1),
(1, 2, 'Lecture 1 - Introduction to Java', '...', 'file', 'lecture1.pdf', 'approved', 1),
(2, 2, 'Data Structures Cheat Sheet', '...', 'file', 'cheatsheet.pdf', 'approved', 1);
```

### ❌ NO CHANGES NEEDED
- Table names and column naming convention (snake_case)
- Foreign key relationships
- ENUM values
- Timestamps (CURRENT_TIMESTAMP)
- Unique constraints
- Auto-increment primary keys

## Validation Checklist

Before running the schema on your MySQL, verify:

✅ MySQL 8.0+ is installed and running
✅ Can connect: `mysql -u root -p`
✅ File location: `/home/biko/CampusShare/campusshare_schema.sql`
✅ Java application credentials match:
   - URL: jdbc:mysql://localhost:3306/campusshare
   - User: root
   - Password: vertrigo (or your actual MySQL password)

## After Installation

Test with this query:
```sql
SELECT u.user_id, u.username, u.role, d.dept_name
FROM user u
LEFT JOIN department d ON u.dept_id = d.dept_id
ORDER BY u.user_id;
```

Expected output:
```
user_id | username    | role   | dept_name
--------|-------------|--------|------------------
1       | admin       | admin  | NULL
2       | prof_smith  | teacher| Computer Science
3       | john_student| student| Computer Science
```

## Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| Login fails with correct password | Plaintext passwords used | Use SHA-256 hashed passwords from this file |
| "Table user doesn't exist" | Schema not executed | Run: `mysql -u root -p < campusshare_schema.sql` |
| Connection timeout | Wrong host/port | Update DatabaseConnection.java with correct MySQL host |
| "Access denied" | Wrong password | Verify MySQL password matches in DatabaseConnection.java |
| profile_pic field missing | Using old schema | Use the adapted schema file (has profile_pic column) |

