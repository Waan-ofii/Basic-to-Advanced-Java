package dao;

import models.User;
import models.Department;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class UserDAO {
    
    // Hash password using SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }
    
    // Register new user
    public static boolean registerUser(User user) throws SQLException {
        String sql = "INSERT INTO user (username, password, full_name, email, role, dept_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashPassword(user.getPassword()));
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getRole());
            pstmt.setInt(6, user.getDeptId());
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // Validate login
    public static User validateLogin(String username, String password) throws SQLException {
        String sql = "SELECT u.*, d.dept_name FROM user u LEFT JOIN department d ON u.dept_id = d.dept_id WHERE u.username = ? AND u.password = ? AND u.is_active = 1";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setDeptId(rs.getInt("dept_id"));
                user.setDeptName(rs.getString("dept_name"));
                user.setActive(rs.getBoolean("is_active"));
                return user;
            }
            return null;
        }
    }
    
    // Get all departments (for registration ComboBox)
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

    // Get all users (for Admin Dashboard)
    public static List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.*, d.dept_name FROM user u LEFT JOIN department d ON u.dept_id = d.dept_id ORDER BY u.role, u.username";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setDeptId(rs.getInt("dept_id"));
                user.setDeptName(rs.getString("dept_name"));
                user.setActive(rs.getBoolean("is_active"));
                users.add(user);
            }
        }
        return users;
    }

    // Toggle user active status
    public static boolean toggleUserStatus(int userId, boolean active) throws SQLException {
        String sql = "UPDATE user SET is_active = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setBoolean(1, active);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Get all teachers
    public static List<User> getTeachers() throws SQLException {
        List<User> teachers = new ArrayList<>();
        String sql = "SELECT u.*, d.dept_name FROM user u LEFT JOIN department d ON u.dept_id = d.dept_id WHERE u.role = 'teacher' AND u.is_active = 1 ORDER BY u.full_name";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setDeptId(rs.getInt("dept_id"));
                user.setDeptName(rs.getString("dept_name"));
                user.setActive(rs.getBoolean("is_active"));
                teachers.add(user);
            }
        }
        return teachers;
    }

    public static boolean changePassword(int userId, String oldPassword, String newPassword) throws SQLException {
        String checkSql = "SELECT * FROM user WHERE user_id = ? AND password = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(checkSql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, hashPassword(oldPassword));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
            }
        }
        String updateSql = "UPDATE user SET password = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(updateSql)) {
            pstmt.setString(1, hashPassword(newPassword));
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        }
    }
}
