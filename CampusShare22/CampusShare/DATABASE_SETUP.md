# CampusShare Database Setup Guide

## Overview
This guide explains how to set up the MySQL database for the CampusShare application. The schema has been adapted to match your Java models and includes proper password hashing.

## Key Adaptations Made

### 1. **Added Missing Column**
- Added `profile_pic VARCHAR(255)` to `user` table (defined in User.java)

### 2. **Password Security**
- **IMPORTANT:** All sample passwords are **SHA-256 hashed** (not plain text)
- UserDAO uses SHA-256 hashing in `hashPassword()` method
- When users register or login, passwords are hashed before database operations

**Sample credentials (plaintext → hashed):**
| User | Role | Username | Password | SHA-256 Hash |
|------|------|----------|----------|--------------|
| Admin | admin | `admin` | `admin123` | `240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9` |
| Teacher | CS Dept | `prof_smith` | `teacher123` | `e54070a2569f16d2994c9284249a2a7cc3c9a5198d00921a97d9198642220d91` |
| Student | CS Dept | `john_student` | `student123` | `ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f` |

### 3. **Sample Data Configuration**
- 4 departments (CS, EE, BUS, MATH)
- 4 academic years (Year 1-4)
- 2 semesters per year
- 1 admin + 1 teacher + 1 student (all in CS dept)
- 3 sample courses (CS101, CS201, CS301) taught by prof_smith
- Student john_student enrolled in all 3 courses
- 3 approved materials for immediate testing

## Installation Steps

### Prerequisites
- MySQL Server 8.0+ running
- MySQL root access (or user with CREATE DATABASE privilege)
- Default connection: `localhost:3306` (user: `root`, password: `vertrigo`)

### Step 1: Execute the Schema Script

```bash
# Option A: Using mysql command line
mysql -u root -p < /path/to/campusshare_schema.sql

# Option B: Using mysql CLI directly
mysql -u root -p vertrigo
mysql> SOURCE /path/to/campusshare_schema.sql;

# Option C: Using MySQL Workbench
# 1. Open MySQL Workbench
# 2. File → Open SQL Script → select campusshare_schema.sql
# 3. Click Execute
```

### Step 2: Verify Installation

```sql
-- Connect to the database
USE campusshare;

-- Check tables created
SHOW TABLES;

-- Verify sample data
SELECT COUNT(*) as 'Total Users' FROM user;
SELECT COUNT(*) as 'Total Courses' FROM course;
SELECT COUNT(*) as 'Total Materials' FROM material;
```

Expected output:
```
Total Users: 4 (1 admin + 1 teacher + 2 students)
Total Courses: 3
Total Materials: 3
```

### Step 3: Update Application Configuration (if needed)

The database connection settings in your project:

**File:** `src/dao/DatabaseConnection.java`
```java
private static final String URL = "jdbc:mysql://localhost:3306/campusshare";
private static final String USER = "root";
private static final String PASSWORD = "vertrigo";
```

If your MySQL setup differs, update these values:
- **URL**: Change `localhost` and/or port `3306` as needed
- **USER**: Your MySQL username
- **PASSWORD**: Your MySQL password

## Testing the Connection

### From Java Application
1. Run: `java -jar target/CampusShare.jar`
2. Login screen should appear
3. Test login with credentials:
   - **Admin:** username=`admin`, password=`admin123`
   - **Teacher:** username=`prof_smith`, password=`teacher123`
   - **Student:** username=`john_student`, password=`student123`

### From MySQL Command Line
```bash
mysql -h localhost -u root -p campusshare -e "SELECT user_id, username, role FROM user;"
```

## Database Workflow Notes

### Material Approval Workflow
1. **Teacher uploads** material → status = `'pending'`
2. **Admin reviews** material → status = `'approved'` or `'rejected'`
3. **Students see** only `'approved'` materials

### Field Naming Convention
- **Java:** camelCase (e.g., `userId`, `uploadedBy`)
- **Database:** snake_case (e.g., `user_id`, `uploaded_by`)
- DAOs handle automatic conversion via prepared statements

## Troubleshooting

### "Access Denied" Error
```
ERROR 1045 (28000): Access denied for user 'root'@'localhost'
```
**Solution:** Verify MySQL password in DatabaseConnection.java matches actual MySQL password

### "Unknown database 'campusshare'" Error
```
ERROR 1049 (42000): Unknown database 'campusshare'
```
**Solution:** Run the schema script to create the database first

### "Table 'campusshare.user' doesn't exist" Error
```
ERROR 1146 (42S02): Table 'campusshare.user' doesn't exist
```
**Solution:** Ensure the entire schema script executed successfully (check for errors above)

### Login Fails Even with Correct Password
**Cause:** Password not properly hashed in database
**Solution:** Verify the hashed password is correctly inserted (see password table above)

## Schema Compatibility with Java Models

| Java Model | Database Table | Key Fields |
|-----------|----------------|-----------|
| User.java | user | user_id, username, **password** (SHA-256), role, dept_id, **profile_pic** |
| Material.java | material | material_id, course_id, uploaded_by, status, **download_count** |
| Course.java | course | course_id, course_code, course_name, year_id, semester_id, teacher_id |
| Department.java | department | dept_id, dept_name, dept_code |
| AcademicYear.java | academic_year | year_id, year_name |
| Semester.java | semester | semester_id, semester_name |

**Bolded fields** are those that are particularly important for the application workflow.

## Next Steps

1. ✅ Execute schema script on MySQL
2. ✅ Verify data insertion
3. ✅ Test application login with sample credentials
4. ✅ Create additional test users/courses as needed
5. Run full application: `java -jar target/CampusShare.jar`

## Additional Notes

- Passwords in sample data are hashed for security
- Never commit plaintext passwords to version control
- For production, externalize database credentials to a config file
- Consider adding indexes on frequently queried columns (username, course_code, etc.)
