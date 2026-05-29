package com.university.server.database;

import com.university.shared.models.Material;
import java.io.*;
import java.sql.*;
import java.util.*;

public class MaterialDAO {
    private static final String FILE_STORAGE_PATH = "./server_files/";
    
    public MaterialDAO() {
        new File(FILE_STORAGE_PATH).mkdirs();
    }
    
    public List<Material> searchMaterials(String keyword, String department, Integer yearLevel) {
        List<Material> materials = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM materials WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            query.append(" AND (title LIKE ? OR course_name LIKE ? OR description LIKE ?)");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (department != null && !department.isEmpty() && !department.equals("All")) {
            query.append(" AND department = ?");
            params.add(department);
        }
        
        if (yearLevel != null && yearLevel > 0) {
            query.append(" AND year_level = ?");
            params.add(yearLevel);
        }
        
        query.append(" ORDER BY upload_date DESC");
        
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Material material = new Material();
                material.setMaterialId(rs.getInt("material_id"));
                material.setTitle(rs.getString("title"));
                material.setDescription(rs.getString("description"));
                material.setMaterialType(rs.getString("material_type"));
                material.setCourseName(rs.getString("course_name"));
                material.setDepartment(rs.getString("department"));
                material.setYearLevel(rs.getInt("year_level"));
                material.setSemester(rs.getInt("semester"));
                material.setFilePath(rs.getString("file_path"));
                material.setFileSize(rs.getLong("file_size"));
                material.setUploaderId(rs.getString("uploader_id"));
                material.setUploadDate(rs.getTimestamp("upload_date"));
                material.setDownloadCount(rs.getInt("download_count"));
                materials.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }
    
    public boolean saveMaterial(Material material, byte[] fileData, String uploaderId) {
        String fileName = UUID.randomUUID().toString() + "_" + material.getTitle().replaceAll("\\s+", "_");
        String fullPath = FILE_STORAGE_PATH + fileName;
        
        // Save file to disk
        try (FileOutputStream fos = new FileOutputStream(fullPath)) {
            fos.write(fileData);
            material.setFilePath(fullPath);
            material.setFileSize((long) fileData.length);
            material.setUploaderId(uploaderId);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        // Save metadata to database
        String query = "INSERT INTO materials (title, description, material_type, course_name, "
                     + "department, year_level, semester, file_path, file_size, uploader_id) "
                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            stmt.setString(1, material.getTitle());
            stmt.setString(2, material.getDescription());
            stmt.setString(3, material.getMaterialType());
            stmt.setString(4, material.getCourseName());
            stmt.setString(5, material.getDepartment());
            stmt.setInt(6, material.getYearLevel());
            stmt.setInt(7, material.getSemester());
            stmt.setString(8, fullPath);
            stmt.setLong(9, material.getFileSize());
            stmt.setString(10, uploaderId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Material getMaterialById(int materialId) {
        String query = "SELECT * FROM materials WHERE material_id = ?";
        
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            stmt.setInt(1, materialId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Material material = new Material();
                material.setMaterialId(rs.getInt("material_id"));
                material.setTitle(rs.getString("title"));
                material.setDescription(rs.getString("description"));
                material.setMaterialType(rs.getString("material_type"));
                material.setCourseName(rs.getString("course_name"));
                material.setDepartment(rs.getString("department"));
                material.setYearLevel(rs.getInt("year_level"));
                material.setFilePath(rs.getString("file_path"));
                return material;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public byte[] getFileData(int materialId) {
        String query = "SELECT file_path FROM materials WHERE material_id = ?";
        
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            stmt.setInt(1, materialId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String filePath = rs.getString("file_path");
                File file = new File(filePath);
                byte[] fileData = new byte[(int) file.length()];
                
                try (FileInputStream fis = new FileInputStream(file)) {
                    fis.read(fileData);
                    return fileData;
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void incrementDownloadCount(int materialId) {
        String query = "UPDATE materials SET download_count = download_count + 1 WHERE material_id = ?";
        
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            stmt.setInt(1, materialId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<String> getRecentAnnouncements() {
        List<String> announcements = new ArrayList<>();
        String query = "SELECT message FROM announcements ORDER BY post_date DESC LIMIT 10";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                announcements.add(rs.getString("message"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return announcements;
    }
}