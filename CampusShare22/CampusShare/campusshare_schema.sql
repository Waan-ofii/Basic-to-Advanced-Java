-- ============================================
-- CampusShare Database Schema
-- Adapted for Java Models - SHA-256 Hashed Passwords
-- ============================================

CREATE DATABASE IF NOT EXISTS campusshare;
USE campusshare;

-- ============================================
-- TABLE CREATION
-- ============================================

-- Create department table
CREATE TABLE department (
    dept_id INT PRIMARY KEY AUTO_INCREMENT,
    dept_name VARCHAR(100) UNIQUE NOT NULL,
    dept_code VARCHAR(10) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create user table (ENHANCED with profile_pic column)
CREATE TABLE user (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('student', 'teacher', 'admin') NOT NULL,
    dept_id INT,
    profile_pic VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dept_id) REFERENCES department(dept_id)
);

-- Create academic_year table
CREATE TABLE academic_year (
    year_id INT PRIMARY KEY AUTO_INCREMENT,
    year_name VARCHAR(20) NOT NULL,
    sort_order INT DEFAULT 0
);

-- Create semester table
CREATE TABLE semester (
    semester_id INT PRIMARY KEY AUTO_INCREMENT,
    semester_name VARCHAR(20) NOT NULL,
    semester_number INT DEFAULT 1,
    sort_order INT DEFAULT 0
);

-- Create course table
CREATE TABLE course (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    course_name VARCHAR(200) NOT NULL,
    description TEXT,
    credits INT DEFAULT 3,
    dept_id INT NOT NULL,
    year_id INT NOT NULL,
    semester_id INT NOT NULL,
    teacher_id INT,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (dept_id) REFERENCES department(dept_id),
    FOREIGN KEY (year_id) REFERENCES academic_year(year_id),
    FOREIGN KEY (semester_id) REFERENCES semester(semester_id),
    FOREIGN KEY (teacher_id) REFERENCES user(user_id)
);

-- Create material table (with approval workflow)
CREATE TABLE material (
    material_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    uploaded_by INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    material_type ENUM('file', 'link', 'video') DEFAULT 'file',
    file_name VARCHAR(255),
    file_path VARCHAR(500),
    file_size BIGINT,
    link_url VARCHAR(500),
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    reviewed_by INT,
    review_comment TEXT,
    reviewed_at TIMESTAMP NULL,
    view_count INT DEFAULT 0,
    download_count INT DEFAULT 0,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES course(course_id),
    FOREIGN KEY (uploaded_by) REFERENCES user(user_id),
    FOREIGN KEY (reviewed_by) REFERENCES user(user_id)
);

-- Create enrollment table
CREATE TABLE enrollment (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    enrollment_date DATE DEFAULT (CURRENT_DATE),
    FOREIGN KEY (student_id) REFERENCES user(user_id),
    FOREIGN KEY (course_id) REFERENCES course(course_id),
    UNIQUE KEY (student_id, course_id)
);

-- Create notification table
CREATE TABLE notification (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    material_id INT,
    title VARCHAR(200),
    message TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (material_id) REFERENCES material(material_id)
);

-- ============================================
-- INSERT SAMPLE DATA
-- ============================================

-- Insert departments
INSERT INTO department (dept_name, dept_code) VALUES 
('Computer Science', 'CS'),
('Electrical Engineering', 'EE'),
('Business Administration', 'BUS'),
('Mathematics', 'MATH');

-- Insert academic years
INSERT INTO academic_year (year_name, sort_order) VALUES 
('Year 1', 1), 
('Year 2', 2), 
('Year 3', 3), 
('Year 4', 4);

-- Insert semesters
INSERT INTO semester (semester_name, semester_number, sort_order) VALUES 
('Semester 1', 1, 1), 
('Semester 2', 2, 2);

-- ============================================
-- USER INSERTS (with SHA-256 hashed passwords)
-- Passwords are hashed using SHA-256:
--   admin123 → 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
--   teacher123 → cde383eee8ee7a4400adf7a15f716f179a2eb97646b37e089eb8d6d04e663416
--   student123 → 703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b
-- ============================================

-- Insert admin user
INSERT INTO user (username, password, full_name, email, role) VALUES 
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 
 'System Administrator', 'admin@campus.edu', 'admin');

-- Insert a sample teacher (Dr. John Smith, CS Department)
INSERT INTO user (username, password, full_name, email, role, dept_id) VALUES 
('prof_smith', 'e54070a2569f16d2994c9284249a2a7cc3c9a5198d00921a97d9198642220d91', 
 'Dr. John Smith', 'smith@campus.edu', 'teacher', 1);

-- Insert sample courses
INSERT INTO course (course_code, course_name, description, credits, dept_id, year_id, semester_id, teacher_id) VALUES
('CS101', 'Introduction to Programming', 
 'Learn the fundamentals of programming using Java', 3, 1, 1, 1, 2),
('CS201', 'Data Structures', 
 'Explore arrays, linked lists, trees, and graphs', 4, 1, 2, 1, 2),
('CS301', 'Database Systems', 
 'Design and implement relational databases', 3, 1, 3, 1, 2);

-- Insert a sample student (John Student, CS Department)
INSERT INTO user (username, password, full_name, email, role, dept_id) VALUES 
('john_student', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 
 'John Student', 'john@campus.edu', 'student', 1);

-- Enroll student in courses
INSERT INTO enrollment (student_id, course_id) VALUES 
(3, 1), 
(3, 2), 
(3, 3);

-- Insert sample materials (with approved status for immediate testing)
INSERT INTO material (course_id, uploaded_by, title, description, material_type, file_name, status, reviewed_by) VALUES
(1, 2, 'Course Syllabus', 
 'Complete course syllabus and grading policy', 'file', 'syllabus.pdf', 'approved', 1),
(1, 2, 'Lecture 1 - Introduction to Java', 
 'Slides covering programming basics and Java fundamentals', 'file', 'lecture1.pdf', 'approved', 1),
(2, 2, 'Data Structures Cheat Sheet', 
 'Quick reference guide for common data structures', 'file', 'cheatsheet.pdf', 'approved', 1);

-- ============================================
-- VERIFICATION QUERIES (Optional - for testing)
-- ============================================
-- SELECT 'Users:' as table_name, COUNT(*) as count FROM user
-- UNION ALL
-- SELECT 'Courses' as table_name, COUNT(*) as count FROM course
-- UNION ALL
-- SELECT 'Materials' as table_name, COUNT(*) as count FROM material
-- UNION ALL
-- SELECT 'Enrollments' as table_name, COUNT(*) as count FROM enrollment;
