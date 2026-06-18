package dao;

import models.Material;
import java.sql.*;
import java.util.*;

public class MaterialDAO {
    
    // Upload new material (status = 'pending')
    public static boolean uploadMaterial(Material material) throws SQLException {
        String sql = "INSERT INTO material (course_id, uploaded_by, title, description, material_type, " +
                    "file_name, file_path, file_size, link_url, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'pending')";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, material.getCourseId());
            pstmt.setInt(2, material.getUploadedBy());
            pstmt.setString(3, material.getTitle());
            pstmt.setString(4, material.getDescription());
            pstmt.setString(5, material.getMaterialType());
            pstmt.setString(6, material.getFileName());
            pstmt.setString(7, material.getFilePath());
            pstmt.setLong(8, material.getFileSize());
            pstmt.setString(9, material.getLinkUrl());
            
            int affected = pstmt.executeUpdate();
            
            if (affected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int materialId = rs.getInt(1);
                    createAdminNotification(materialId, "New material pending approval");
                }
                return true;
            }
            return false;
        }
    }
    
    // Get pending materials for admin
    public static List<Material> getPendingMaterials() throws SQLException {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT m.*, c.course_name, u.full_name as uploader_name " +
                    "FROM material m " +
                    "JOIN course c ON m.course_id = c.course_id " +
                    "JOIN user u ON m.uploaded_by = u.user_id " +
                    "WHERE m.status = 'pending' " +
                    "ORDER BY m.upload_date DESC";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                materials.add(extractMaterialFromResultSet(rs));
            }
        }
        return materials;
    }
    
    // Get materials by course (only approved ones for students)
    public static List<Material> getApprovedMaterialsByCourse(int courseId) throws SQLException {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT m.*, c.course_name, u.full_name as uploader_name " +
                    "FROM material m " +
                    "JOIN course c ON m.course_id = c.course_id " +
                    "JOIN user u ON m.uploaded_by = u.user_id " +
                    "WHERE m.course_id = ? AND m.status = 'approved' " +
                    "ORDER BY m.upload_date DESC";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                materials.add(extractMaterialFromResultSet(rs));
            }
        }
        return materials;
    }
    
    // Approve material (Admin action)
    public static boolean approveMaterial(int materialId, int adminId, String comment) throws SQLException {
        String sql = "UPDATE material SET status = 'approved', reviewed_by = ?, review_comment = ?, reviewed_at = NOW() WHERE material_id = ?";
        String title = "";
        String courseName = "";
        String getInfoSql = "SELECT m.title, c.course_name FROM material m JOIN course c ON m.course_id = c.course_id WHERE m.material_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(getInfoSql)) {
            pstmt.setInt(1, materialId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    title = rs.getString("title");
                    courseName = rs.getString("course_name");
                }
            }
        }

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, adminId);
            pstmt.setString(2, comment);
            pstmt.setInt(3, materialId);
            boolean updated = pstmt.executeUpdate() > 0;
            
            if (updated) {
                notifyUploader(materialId, "Your material \"" + title + "\" has been approved!");
                services.NotificationServer.sendSocketNotification("BROADCAST", "New Material:\"" + title + "\" is now available in " + courseName);
            }
            return updated;
        }
    }
    
    // Reject material (Admin action)
    public static boolean rejectMaterial(int materialId, int adminId, String reason) throws SQLException {
        String sql = "UPDATE material SET status = 'rejected', reviewed_by = ?, review_comment = ?, reviewed_at = NOW() WHERE material_id = ?";
        String title = "";
        String getInfoSql = "SELECT title FROM material WHERE material_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(getInfoSql)) {
            pstmt.setInt(1, materialId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    title = rs.getString("title");
                }
            }
        }

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, adminId);
            pstmt.setString(2, reason);
            pstmt.setInt(3, materialId);
            boolean updated = pstmt.executeUpdate() > 0;
            
            if (updated) {
                notifyUploader(materialId, "Your material \"" + title + "\" was rejected. Reason: " + reason);
            }
            return updated;
        }
    }
    
    // Increment download count (for analytics)
    public static void incrementDownloadCount(int materialId) throws SQLException {
        String sql = "UPDATE material SET download_count = download_count + 1 WHERE material_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, materialId);
            pstmt.executeUpdate();
        }
    }
    
    // Get materials by uploader
    public static List<Material> getMaterialsByUploader(int userId) throws SQLException {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT m.*, c.course_name, u.full_name as uploader_name " +
                    "FROM material m " +
                    "JOIN course c ON m.course_id = c.course_id " +
                    "JOIN user u ON m.uploaded_by = u.user_id " +
                    "WHERE m.uploaded_by = ? " +
                    "ORDER BY m.upload_date DESC";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                materials.add(extractMaterialFromResultSet(rs));
            }
        }
        return materials;
    }
    
    // Get materials by uploader and status
    public static List<Material> getMaterialsByUploaderAndStatus(int userId, String status) throws SQLException {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT m.*, c.course_name, u.full_name as uploader_name " +
                    "FROM material m " +
                    "JOIN course c ON m.course_id = c.course_id " +
                    "JOIN user u ON m.uploaded_by = u.user_id " +
                    "WHERE m.uploaded_by = ? AND m.status = ? " +
                    "ORDER BY m.upload_date DESC";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, status);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                materials.add(extractMaterialFromResultSet(rs));
            }
        }
        return materials;
    }
    
    // Delete material
    public static boolean deleteMaterial(int materialId) throws SQLException {
        String getPathSql = "SELECT file_path FROM material WHERE material_id = ?";
        String filePath = null;
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(getPathSql)) {
            pstmt.setInt(1, materialId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    filePath = rs.getString("file_path");
                }
            }
        }

        String sql = "DELETE FROM material WHERE material_id = ?";
        boolean deleted = false;
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, materialId);
            deleted = pstmt.executeUpdate() > 0;
        }

        if (deleted && filePath != null) {
            java.io.File file = new java.io.File(filePath);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        }
        return deleted;
    }

    // Get recent approved materials for enrolled courses
    public static List<Material> getRecentMaterialsForEnrolledCourses(int studentId, int limit) throws SQLException {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT m.*, c.course_name, u.full_name as uploader_name " +
                     "FROM material m " +
                     "JOIN course c ON m.course_id = c.course_id " +
                     "JOIN user u ON m.uploaded_by = u.user_id " +
                     "JOIN enrollment e ON e.course_id = m.course_id " +
                     "WHERE e.student_id = ? AND m.status = 'approved' " +
                     "ORDER BY m.upload_date DESC LIMIT ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                materials.add(extractMaterialFromResultSet(rs));
            }
        }
        return materials;
    }
    
    public static Material getMaterialById(int materialId) throws SQLException {
        String sql = "SELECT m.*, c.course_name, u.full_name as uploader_name " +
                     "FROM material m " +
                     "JOIN course c ON m.course_id = c.course_id " +
                     "JOIN user u ON m.uploaded_by = u.user_id " +
                     "WHERE m.material_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, materialId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractMaterialFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    // Get teacher statistics
    public static models.TeacherStats getTeacherStats(int userId) throws SQLException {
        models.TeacherStats stats = new models.TeacherStats();
        String sql = "SELECT " +
                    "COUNT(*) as total, " +
                    "SUM(CASE WHEN status='pending' THEN 1 ELSE 0 END) as pending, " +
                    "SUM(CASE WHEN status='approved' THEN 1 ELSE 0 END) as approved, " +
                    "SUM(CASE WHEN status='rejected' THEN 1 ELSE 0 END) as rejected, " +
                    "SUM(download_count) as total_downloads " +
                    "FROM material WHERE uploaded_by = ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                stats.setTotalUploads(rs.getInt("total"));
                stats.setPendingCount(rs.getInt("pending"));
                stats.setApprovedCount(rs.getInt("approved"));
                stats.setRejectedCount(rs.getInt("rejected"));
                stats.setTotalDownloads(rs.getInt("total_downloads"));
                
                // Get most downloaded material
                String mostDownloadedSql = "SELECT title FROM material WHERE uploaded_by = ? AND status = 'approved' ORDER BY download_count DESC LIMIT 1";
                try (PreparedStatement pstmt2 = DatabaseConnection.getConnection().prepareStatement(mostDownloadedSql)) {
                    pstmt2.setInt(1, userId);
                    ResultSet rs2 = pstmt2.executeQuery();
                    if (rs2.next()) {
                        stats.setMostDownloadedTitle(rs2.getString("title"));
                    }
                }
            }
        }
        return stats;
    }
    
    // Get recent activity for teacher
    public static java.util.List<String> getRecentActivity(int userId) throws SQLException {
        java.util.List<String> activities = new java.util.ArrayList<>();
        String sql = "SELECT CONCAT('Material: ', title, ' - Status: ', status, ' (', DATE_FORMAT(upload_date, '%Y-%m-%d'), ')') as activity " +
                    "FROM material WHERE uploaded_by = ? ORDER BY upload_date DESC LIMIT 10";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                activities.add(rs.getString("activity"));
            }
        }
        return activities;
    }
    
    // Helper method to extract Material from ResultSet
    private static Material extractMaterialFromResultSet(ResultSet rs) throws SQLException {
        Material material = new Material();
        material.setMaterialId(rs.getInt("material_id"));
        material.setCourseId(rs.getInt("course_id"));
        material.setCourseName(rs.getString("course_name"));
        material.setUploadedBy(rs.getInt("uploaded_by"));
        material.setUploaderName(rs.getString("uploader_name"));
        material.setTitle(rs.getString("title"));
        material.setDescription(rs.getString("description"));
        material.setMaterialType(rs.getString("material_type"));
        material.setFileName(rs.getString("file_name"));
        material.setFilePath(rs.getString("file_path"));
        material.setFileSize(rs.getLong("file_size"));
        material.setLinkUrl(rs.getString("link_url"));
        material.setStatus(rs.getString("status"));
        material.setReviewComment(rs.getString("review_comment"));
        material.setViewCount(rs.getInt("view_count"));
        material.setDownloadCount(rs.getInt("download_count"));
        material.setUploadDate(rs.getTimestamp("upload_date"));
        material.setReviewedAt(rs.getTimestamp("reviewed_at"));
        return material;
    }
    
    // Notification methods
    private static void createAdminNotification(int materialId, String message) throws SQLException {
        String sql = "INSERT INTO notification (user_id, material_id, title, message) " +
                    "SELECT user_id, ?, 'New Material Pending', ? FROM user WHERE role = 'admin'";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, materialId);
            pstmt.setString(2, message);
            pstmt.executeUpdate();
        }
        // Send socket alert to admins
        services.NotificationServer.sendSocketNotification("ADMIN", "New Material Pending: " + message);
    }
    
    private static void notifyUploader(int materialId, String message) throws SQLException {
        String sql = "INSERT INTO notification (user_id, material_id, title, message) " +
                    "SELECT uploaded_by, ?, 'Material Update', ? FROM material WHERE material_id = ?";
        int uploaderId = -1;
        // Let's get the uploader ID first
        String selectSql = "SELECT uploaded_by FROM material WHERE material_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(selectSql)) {
            pstmt.setInt(1, materialId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    uploaderId = rs.getInt("uploaded_by");
                }
            }
        }
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, materialId);
            pstmt.setString(2, message);
            pstmt.setInt(3, materialId);
            pstmt.executeUpdate();
        }
        
        if (uploaderId != -1) {
            services.NotificationServer.sendSocketNotification("SEND", uploaderId + ":Material Update:" + message);
        }
    }

    // Get all materials (for Admin Dashboard)
    public static List<Material> getAllMaterials() throws SQLException {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT m.*, c.course_name, u.full_name as uploader_name " +
                     "FROM material m " +
                     "JOIN course c ON m.course_id = c.course_id " +
                     "JOIN user u ON m.uploaded_by = u.user_id " +
                     "ORDER BY m.upload_date DESC";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                materials.add(extractMaterialFromResultSet(rs));
            }
        }
        return materials;
    }

    // Get all materials by status
    public static List<Material> getAllMaterialsByStatus(String status) throws SQLException {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT m.*, c.course_name, u.full_name as uploader_name " +
                     "FROM material m " +
                     "JOIN course c ON m.course_id = c.course_id " +
                     "JOIN user u ON m.uploaded_by = u.user_id " +
                     "WHERE m.status = ? " +
                     "ORDER BY m.upload_date DESC";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    materials.add(extractMaterialFromResultSet(rs));
                }
            }
        }
        return materials;
    }

    // Get popular courses report
    public static List<Map<String, Object>> getPopularCoursesReport() throws SQLException {
        List<Map<String, Object>> report = new ArrayList<>();
        String sql = "SELECT c.course_code, c.course_name, COUNT(m.material_id) as material_count, COALESCE(SUM(m.download_count), 0) as total_downloads " +
                     "FROM course c " +
                     "LEFT JOIN material m ON c.course_id = m.course_id " +
                     "GROUP BY c.course_id, c.course_code, c.course_name " +
                     "ORDER BY total_downloads DESC, material_count DESC " +
                     "LIMIT 10";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("course_code", rs.getString("course_code"));
                row.put("course_name", rs.getString("course_name"));
                row.put("material_count", rs.getInt("material_count"));
                row.put("total_downloads", rs.getInt("total_downloads"));
                report.add(row);
            }
        }
        return report;
    }

    // Get most downloaded materials report
    public static List<Map<String, Object>> getMostDownloadedMaterialsReport() throws SQLException {
        List<Map<String, Object>> report = new ArrayList<>();
        String sql = "SELECT m.title, c.course_name, u.full_name as uploader_name, m.download_count " +
                     "FROM material m " +
                     "JOIN course c ON m.course_id = c.course_id " +
                     "JOIN user u ON m.uploaded_by = u.user_id " +
                     "ORDER BY m.download_count DESC " +
                     "LIMIT 10";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("title", rs.getString("title"));
                row.put("course_name", rs.getString("course_name"));
                row.put("uploader_name", rs.getString("uploader_name"));
                row.put("download_count", rs.getInt("download_count"));
                report.add(row);
            }
        }
        return report;
    }
}
