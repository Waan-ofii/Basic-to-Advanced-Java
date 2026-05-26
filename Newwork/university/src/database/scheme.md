-- Create database
CREATE DATABASE university_materials;
USE university_materials;

-- Students table
CREATE TABLE students (
    student_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    department VARCHAR(100),
    academic_level ENUM('Freshman', 'Pre-Engineering', 'Department') DEFAULT 'Freshman',
    year_level INT DEFAULT 1,
    is_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Departments table
CREATE TABLE departments (
    dept_id INT PRIMARY KEY AUTO_INCREMENT,
    dept_name VARCHAR(100) UNIQUE NOT NULL,
    dept_code VARCHAR(10) UNIQUE NOT NULL
);

-- Materials table
CREATE TABLE materials (
    material_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    material_type ENUM('Lecture Note', 'Assignment', 'Slide', 'PDF', 'Exam') DEFAULT 'PDF',
    course_name VARCHAR(100),
    department VARCHAR(100),
    year_level INT,
    semester INT,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    uploader_id VARCHAR(20),
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    download_count INT DEFAULT 0,
    FOREIGN KEY (uploader_id) REFERENCES students(student_id)
);

-- Announcements table
CREATE TABLE announcements (
    id INT PRIMARY KEY AUTO_INCREMENT,
    message TEXT NOT NULL,
    department VARCHAR(100),
    year_level INT,
    posted_by VARCHAR(20),
    post_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (posted_by) REFERENCES students(student_id)
);

-- Insert sample data
INSERT INTO students VALUES 
('STU001', 'John Doe', 'pass123', 'john@university.edu', 'Software Engineering', 'Department', 2, TRUE, NOW()),
('STU002', 'Jane Smith', 'pass123', 'jane@university.edu', 'Computer Science', 'Department', 3, FALSE, NOW()),
('STU003', 'Bob Johnson', 'pass123', 'bob@university.edu', 'Pre-Engineering', 'Pre-Engineering', 1, FALSE, NOW());

INSERT INTO departments VALUES 
(1, 'Software Engineering', 'SWE'),
(2, 'Computer Science', 'CSC'),
(3, 'Biomedical Engineering', 'BME'),
(4, 'Electrical Engineering', 'ECE'),
(5, 'Common Courses', 'COM');