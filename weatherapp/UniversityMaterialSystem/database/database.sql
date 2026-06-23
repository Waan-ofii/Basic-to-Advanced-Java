-- Create database
CREATE DATABASE IF NOT EXISTS university_db;
USE university_db;

-- Users table (students and admins)
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role ENUM('admin', 'student') DEFAULT 'student',
    status ENUM('active', 'banned') DEFAULT 'active',
    department VARCHAR(100),
    batch INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Departments table
CREATE TABLE IF NOT EXISTS departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE NOT NULL
);

-- Courses table
CREATE TABLE IF NOT EXISTS courses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(200) NOT NULL,
    department VARCHAR(100) NOT NULL,
    batch INT NOT NULL,
    description TEXT,
    UNIQUE KEY unique_course (code, department, batch)
);

-- Materials table
CREATE TABLE IF NOT EXISTS materials (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    course_id INT NOT NULL,
    department VARCHAR(100) NOT NULL,
    batch INT NOT NULL,
    uploaded_by VARCHAR(20),
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    download_count INT DEFAULT 0,
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- Insert sample data

-- Insert departments
INSERT INTO departments (name) VALUES 
('Software Engineering'),
('Computer Science'),
('Biomedical Engineering'),
('Electrical Engineering');

-- Insert admin user (password: admin123)
INSERT INTO users (student_id, name, password, email, role, department, batch) VALUES
('ADMIN001', 'System Admin', 'admin123', 'admin@university.edu', 'admin', 'Software Engineering', 0);

-- Insert sample students (password: student123)
INSERT INTO users (student_id, name, password, email, role, department, batch) VALUES
('STU001', 'John Doe', 'student123', 'john@university.edu', 'student', 'Software Engineering', 2),
('STU002', 'Jane Smith', 'student123', 'jane@university.edu', 'student', 'Computer Science', 3),
('STU003', 'Bob Johnson', 'student123', 'bob@university.edu', 'student', 'Biomedical Engineering', 1);

-- Insert sample courses
INSERT INTO courses (code, name, department, batch, description) VALUES
('SWE201', 'Object Oriented Programming', 'Software Engineering', 2, 'Java programming fundamentals'),
('SWE202', 'Data Structures', 'Software Engineering', 2, 'Advanced data structures and algorithms'),
('SWE301', 'Database Systems', 'Software Engineering', 3, 'SQL and database design'),
('CSC201', 'Computer Networks', 'Computer Science', 2, 'Network protocols and architecture'),
('CSC301', 'Operating Systems', 'Computer Science', 3, 'OS concepts and implementation'),
('BME101', 'Introduction to Biomedical', 'Biomedical Engineering', 1, 'Basics of biomedical engineering');

-- Insert sample materials
INSERT INTO materials (title, type, file_path, course_id, department, batch, uploaded_by) VALUES
('Java Programming Notes', 'pdf', 'uploads/Software_Engineering/2_Year/Java_Notes.pdf', 1, 'Software Engineering', 2, 'ADMIN001'),
('Data Structures Slides', 'ppt', 'uploads/Software_Engineering/2_Year/Data_Structures.pptx', 2, 'Software Engineering', 2, 'ADMIN001');

-- Sample SELECT queries for testing
SELECT '✅ Database setup complete!' as Status;
SELECT COUNT(*) as Total_Users FROM users;
SELECT COUNT(*) as Total_Courses FROM courses;
SELECT COUNT(*) as Total_Materials FROM materials;