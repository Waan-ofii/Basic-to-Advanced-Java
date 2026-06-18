package dao;

import models.*;
import java.sql.*;
import java.util.*;

public class CourseNavigationDAO {
    
    // Get all departments for browsing
    public static List<Department> getAllDepartments() throws SQLException {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM department ORDER BY dept_name";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Department dept = new Department();
                dept.setDeptId(rs.getInt("dept_id"));
                dept.setDeptName(rs.getString("dept_name"));
                dept.setDeptCode(rs.getString("dept_code"));
                departments.add(dept);
            }
        }
        return departments;
    }
    
    // Get years for a department (based on available courses)
    public static List<AcademicYear> getYearsByDepartment(int deptId) throws SQLException {
        List<AcademicYear> years = new ArrayList<>();
        String sql = "SELECT DISTINCT ay.* FROM academic_year ay " +
                    "JOIN course c ON ay.year_id = c.year_id " +
                    "WHERE c.dept_id = ? AND c.is_active = 1 " +
                    "ORDER BY ay.sort_order";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, deptId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                AcademicYear year = new AcademicYear();
                year.setYearId(rs.getInt("year_id"));
                year.setYearName(rs.getString("year_name"));
                years.add(year);
            }
        }
        return years;
    }
    
    // Get semesters for a department and year
    public static List<Semester> getSemestersByDeptAndYear(int deptId, int yearId) throws SQLException {
        List<Semester> semesters = new ArrayList<>();
        String sql = "SELECT DISTINCT s.* FROM semester s " +
                    "JOIN course c ON s.semester_id = c.semester_id " +
                    "WHERE c.dept_id = ? AND c.year_id = ? AND c.is_active = 1 " +
                    "ORDER BY s.sort_order";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, deptId);
            pstmt.setInt(2, yearId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Semester semester = new Semester();
                semester.setSemesterId(rs.getInt("semester_id"));
                semester.setSemesterName(rs.getString("semester_name"));
                semesters.add(semester);
            }
        }
        return semesters;
    }
    
    // Get courses for a department, year, and semester
    public static List<Course> getCoursesByDeptYearSemester(int deptId, int yearId, int semesterId) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name as teacher_name, d.dept_name " +
                    "FROM course c " +
                    "LEFT JOIN user u ON c.teacher_id = u.user_id " +
                    "JOIN department d ON c.dept_id = d.dept_id " +
                    "WHERE c.dept_id = ? AND c.year_id = ? AND c.semester_id = ? AND c.is_active = 1 " +
                    "ORDER BY c.course_code";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, deptId);
            pstmt.setInt(2, yearId);
            pstmt.setInt(3, semesterId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseCode(rs.getString("course_code"));
                course.setCourseName(rs.getString("course_name"));
                course.setDescription(rs.getString("description"));
                course.setCredits(rs.getInt("credits"));
                course.setTeacherName(rs.getString("teacher_name"));
                course.setDeptName(rs.getString("dept_name"));
                courses.add(course);
            }
        }
        return courses;
    }

    // Get all academic years (for course creation)
    public static List<AcademicYear> getAllAcademicYears() throws SQLException {
        List<AcademicYear> years = new ArrayList<>();
        String sql = "SELECT * FROM academic_year ORDER BY sort_order";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                AcademicYear year = new AcademicYear();
                year.setYearId(rs.getInt("year_id"));
                year.setYearName(rs.getString("year_name"));
                years.add(year);
            }
        }
        return years;
    }

    // Get all semesters (for course creation)
    public static List<Semester> getAllSemesters() throws SQLException {
        List<Semester> semesters = new ArrayList<>();
        String sql = "SELECT * FROM semester ORDER BY sort_order";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Semester semester = new Semester();
                semester.setSemesterId(rs.getInt("semester_id"));
                semester.setSemesterName(rs.getString("semester_name"));
                semesters.add(semester);
            }
        }
        return semesters;
    }
}