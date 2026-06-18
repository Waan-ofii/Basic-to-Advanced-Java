package dao;

import models.Course;
import java.sql.*;
import java.util.*;

public class CourseDAO {
    
    public static List<Course> getCoursesByTeacher(int teacherId) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM course WHERE teacher_id = ? ORDER BY course_code";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Course course = extractCourseFromResultSet(rs);
                courses.add(course);
            }
        }
        return courses;
    }
    
    public static List<Course> getCoursesByDepartment(int deptId) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM course WHERE dept_id = ? ORDER BY course_code";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, deptId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Course course = extractCourseFromResultSet(rs);
                courses.add(course);
            }
        }
        return courses;
    }
    
    public static List<Course> getCoursesByYear(int yearId) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM course WHERE year_id = ? ORDER BY course_code";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, yearId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Course course = extractCourseFromResultSet(rs);
                courses.add(course);
            }
        }
        return courses;
    }
    
    public static List<Course> getCoursesBySemester(int semesterId) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM course WHERE semester_id = ? ORDER BY course_code";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, semesterId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Course course = extractCourseFromResultSet(rs);
                courses.add(course);
            }
        }
        return courses;
    }
    
    private static Course extractCourseFromResultSet(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getInt("course_id"));
        course.setCourseCode(rs.getString("course_code"));
        course.setCourseName(rs.getString("course_name"));
        course.setCredits(rs.getInt("credits"));
        try {
            course.setTeacherId(rs.getInt("teacher_id"));
        } catch (Exception e) {}
        try {
            course.setActive(rs.getBoolean("is_active"));
        } catch (Exception e) {}
        try {
            if (rs.getString("dept_name") != null) {
                course.setDeptName(rs.getString("dept_name"));
            }
        } catch (Exception e) {}
        try {
            if (rs.getString("year_name") != null) {
                course.setYearName(rs.getString("year_name"));
            }
        } catch (Exception e) {}
        try {
            if (rs.getString("semester_name") != null) {
                course.setSemesterName(rs.getString("semester_name"));
            }
        } catch (Exception e) {}
        try {
            course.setMaterialCount(rs.getInt("material_count"));
        } catch (Exception e) {}
        return course;
    }

    // Get enrolled courses for a student
    public static List<Course> getEnrolledCourses(int studentId) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, d.dept_name, y.year_name, s.semester_name, " +
                     "(SELECT COUNT(*) FROM material m WHERE m.course_id = c.course_id AND m.status = 'approved') as material_count " +
                     "FROM enrollment e " +
                     "JOIN course c ON e.course_id = c.course_id " +
                     "JOIN department d ON c.dept_id = d.dept_id " +
                     "JOIN academic_year y ON c.year_id = y.year_id " +
                     "JOIN semester s ON c.semester_id = s.semester_id " +
                     "WHERE e.student_id = ? " +
                     "ORDER BY c.course_code";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Course course = extractCourseFromResultSet(rs);
                courses.add(course);
            }
        }
        return courses;
    }

    // Get all courses (for Admin Dashboard)
    public static List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, d.dept_name, y.year_name, s.semester_name, u.full_name as teacher_name " +
                     "FROM course c " +
                     "LEFT JOIN department d ON c.dept_id = d.dept_id " +
                     "LEFT JOIN academic_year y ON c.year_id = y.year_id " +
                     "LEFT JOIN semester s ON c.semester_id = s.semester_id " +
                     "LEFT JOIN user u ON c.teacher_id = u.user_id " +
                     "ORDER BY c.course_code";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Course course = extractCourseFromResultSet(rs);
                try {
                    if (rs.getString("teacher_name") != null) {
                        course.setTeacherName(rs.getString("teacher_name"));
                    }
                } catch (Exception e) {}
                courses.add(course);
            }
        }
        return courses;
    }

    // Toggle course status
    public static boolean toggleCourseStatus(int courseId, boolean active) throws SQLException {
        String sql = "UPDATE course SET is_active = ? WHERE course_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setBoolean(1, active);
            pstmt.setInt(2, courseId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Create a new course
    public static boolean createCourse(Course course) throws SQLException {
        String sql = "INSERT INTO course (course_code, course_name, description, credits, dept_id, year_id, semester_id, teacher_id, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setString(3, course.getDescription());
            pstmt.setInt(4, course.getCredits());
            pstmt.setInt(5, course.getDeptId());
            pstmt.setInt(6, course.getYearId());
            pstmt.setInt(7, course.getSemesterId());
            if (course.getTeacherId() > 0) {
                pstmt.setInt(8, course.getTeacherId());
            } else {
                pstmt.setNull(8, java.sql.Types.INTEGER);
            }
            return pstmt.executeUpdate() > 0;
        }
    }

    public static boolean enrollStudentInCourse(int studentId, int courseId) throws SQLException {
        String sql = "INSERT INTO enrollment (student_id, course_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public static List<Course> getUnenrolledCourses(int studentId) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, d.dept_name, y.year_name, s.semester_name, u.full_name as teacher_name " +
                     "FROM course c " +
                     "LEFT JOIN department d ON c.dept_id = d.dept_id " +
                     "LEFT JOIN academic_year y ON c.year_id = y.year_id " +
                     "LEFT JOIN semester s ON c.semester_id = s.semester_id " +
                     "LEFT JOIN user u ON c.teacher_id = u.user_id " +
                     "WHERE c.is_active = 1 AND c.course_id NOT IN (SELECT course_id FROM enrollment WHERE student_id = ?) " +
                     "ORDER BY c.course_code";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Course course = extractCourseFromResultSet(rs);
                    try {
                        if (rs.getString("teacher_name") != null) {
                            course.setTeacherName(rs.getString("teacher_name"));
                        }
                    } catch (Exception e) {}
                    courses.add(course);
                }
            }
        }
        return courses;
    }
}
