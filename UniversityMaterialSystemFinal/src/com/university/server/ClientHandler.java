package com.university.server;

import com.university.shared.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private User currentUser;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String requestType = (String) input.readObject();

                switch (requestType) {
                    case "LOGIN":
                        handleLogin();
                        break;
                    case "GET_DEPARTMENTS":
                        handleGetDepartments();
                        break;
                    case "GET_BATCHES":
                        handleGetBatches();
                        break;
                    case "GET_COURSES":
                        handleGetCourses();
                        break;
                    case "GET_MATERIALS":
                        handleGetMaterials();
                        break;
                    case "DOWNLOAD_MATERIAL":
                        handleDownloadMaterial();
                        break;
                    case "UPLOAD_MATERIAL":
                        handleUploadMaterial();
                        break;
                    case "GET_ALL_USERS":
                        handleGetAllUsers();
                        break;
                    case "BAN_USER":
                        handleBanUser();
                        break;
                    case "GRANT_ADMIN":
                        handleGrantAdmin();
                        break;
                    case "DELETE_USER":
                        handleDeleteUser();
                        break;
                    case "GET_STATISTICS":
                        handleGetStatistics();
                        break;
                    case "SEARCH":
                        handleSearch();
                        break;
                    case "LOGOUT":
                        System.out.println("User logged out: " + currentUser.getName());
                        output.writeObject("LOGOUT_SUCCESS");
                        return;
                    default:
                        output.writeObject("ERROR: Unknown request");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client disconnected: " + socket.getInetAddress());
        } finally {
            closeConnection();
        }
    }

    private void handleLogin() throws IOException, ClassNotFoundException {
        String studentId = (String) input.readObject();
        String password = (String) input.readObject();

        String query = "SELECT * FROM users WHERE student_id = ? AND password = ? AND status = 'active'";

        try (ResultSet rs = DatabaseConnection.executeQuery(query, studentId, password)) {
            if (rs.next()) {
                currentUser = new User();
                currentUser.setId(rs.getInt("id"));
                currentUser.setStudentId(rs.getString("student_id"));
                currentUser.setName(rs.getString("name"));
                currentUser.setEmail(rs.getString("email"));
                currentUser.setRole(rs.getString("role"));
                currentUser.setDepartment(rs.getString("department"));
                currentUser.setBatch(rs.getInt("batch"));
                currentUser.setStatus(rs.getString("status"));

                output.writeObject("SUCCESS");
                output.writeObject(currentUser);
                System.out.println("✅ Login successful: " + currentUser.getName());
            } else {
                output.writeObject("FAILURE");
                output.writeObject("Invalid student ID or password");
            }
        } catch (SQLException e) {
            output.writeObject("FAILURE");
            output.writeObject("Database error: " + e.getMessage());
        }
    }

    private void handleGetDepartments() throws IOException {
        List<String> departments = Arrays.asList(
                "Software Engineering", "Computer Science",
                "Biomedical Engineering", "Electrical Engineering"
        );
        output.writeObject(departments);
    }

    private void handleGetBatches() throws IOException {
        List<Integer> batches = Arrays.asList(1, 2, 3, 4);
        output.writeObject(batches);
    }

    private void handleGetCourses() throws IOException, ClassNotFoundException {
        String department = (String) input.readObject();
        int batch = (Integer) input.readObject();

        String query = "SELECT * FROM courses WHERE department = ? AND batch = ?";
        List<Course> courses = new ArrayList<>();

        try (ResultSet rs = DatabaseConnection.executeQuery(query, department, batch)) {
            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setCode(rs.getString("code"));
                course.setName(rs.getString("name"));
                course.setDepartment(rs.getString("department"));
                course.setBatch(rs.getInt("batch"));
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        output.writeObject(courses);
    }

    private void handleGetMaterials() throws IOException, ClassNotFoundException {
        int courseId = (Integer) input.readObject();

        String query = "SELECT m.*, u.name as uploader_name FROM materials m " +
                "LEFT JOIN users u ON m.uploaded_by = u.student_id " +
                "WHERE m.course_id = ? ORDER BY m.upload_date DESC";
        List<Material> materials = new ArrayList<>();

        try (ResultSet rs = DatabaseConnection.executeQuery(query, courseId)) {
            while (rs.next()) {
                Material material = new Material();
                material.setId(rs.getInt("id"));
                material.setTitle(rs.getString("title"));
                material.setType(rs.getString("type"));
                material.setCourseId(rs.getInt("course_id"));
                material.setDepartment(rs.getString("department"));
                material.setBatch(rs.getInt("batch"));
                material.setUploadedBy(rs.getString("uploaded_by"));
                material.setUploaderName(rs.getString("uploader_name"));
                material.setUploadDate(rs.getTimestamp("upload_date"));
                material.setDownloadCount(rs.getInt("download_count"));
                materials.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        output.writeObject(materials);
    }

    private void handleDownloadMaterial() throws IOException, ClassNotFoundException {
        int materialId = (Integer) input.readObject();

        String query = "SELECT * FROM materials WHERE id = ?";

        try (ResultSet rs = DatabaseConnection.executeQuery(query, materialId)) {
            if (rs.next()) {
                Material material = new Material();
                material.setId(rs.getInt("id"));
                material.setTitle(rs.getString("title"));
                material.setType(rs.getString("type"));
                material.setFilePath(rs.getString("file_path"));

                byte[] fileData = FileManager.readFile(material.getFilePath());
                material.setFileData(fileData);

                String updateQuery = "UPDATE materials SET download_count = download_count + 1 WHERE id = ?";
                DatabaseConnection.executeUpdate(updateQuery, materialId);

                output.writeObject("SUCCESS");
                output.writeObject(material);
            } else {
                output.writeObject("FAILURE");
                output.writeObject("Material not found");
            }
        } catch (SQLException e) {
            output.writeObject("FAILURE");
            output.writeObject("Database error: " + e.getMessage());
        }
    }

    private void handleUploadMaterial() throws IOException, ClassNotFoundException {
        if (currentUser == null || !currentUser.isAdmin()) {
            output.writeObject("ERROR: Unauthorized");
            return;
        }

        String title = (String) input.readObject();
        String type = (String) input.readObject();
        int courseId = (Integer) input.readObject();
        byte[] fileData = (byte[]) input.readObject();

        String courseQuery = "SELECT department, batch FROM courses WHERE id = ?";
        String department = "";
        int batch = 0;

        try (ResultSet rs = DatabaseConnection.executeQuery(courseQuery, courseId)) {
            if (rs.next()) {
                department = rs.getString("department");
                batch = rs.getInt("batch");
            }
        } catch (SQLException e) {
            output.writeObject("FAILURE");
            output.writeObject("Course not found");
            return;
        }

        String fileName = title.replaceAll("\\s+", "_") + getFileExtension(type);
        String filePath = FileManager.saveFile(fileData, department, batch, fileName);

        if (filePath == null) {
            output.writeObject("FAILURE");
            output.writeObject("Failed to save file");
            return;
        }

        String insertQuery = "INSERT INTO materials (title, type, file_path, course_id, department, batch, uploaded_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            DatabaseConnection.executeUpdate(insertQuery, title, type, filePath, courseId, department, batch, currentUser.getStudentId());
            output.writeObject("SUCCESS");
            output.writeObject("Material uploaded successfully!");
        } catch (SQLException e) {
            output.writeObject("FAILURE");
            output.writeObject("Database error: " + e.getMessage());
        }
    }

    private void handleGetAllUsers() throws IOException {
        if (currentUser == null || !currentUser.isAdmin()) {
            output.writeObject(new ArrayList<>());
            return;
        }

        String query = "SELECT * FROM users ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();

        try (ResultSet rs = DatabaseConnection.executeQuery(query)) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setStudentId(rs.getString("student_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                user.setDepartment(rs.getString("department"));
                user.setBatch(rs.getInt("batch"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        output.writeObject(users);
    }

    private void handleBanUser() throws IOException, ClassNotFoundException {
        if (currentUser == null || !currentUser.isAdmin()) {
            output.writeObject("ERROR: Unauthorized");
            return;
        }

        int userId = (Integer) input.readObject();
        String query = "UPDATE users SET status = 'banned' WHERE id = ?";

        try {
            DatabaseConnection.executeUpdate(query, userId);
            output.writeObject("SUCCESS");
        } catch (SQLException e) {
            output.writeObject("FAILURE");
            output.writeObject(e.getMessage());
        }
    }

    private void handleGrantAdmin() throws IOException, ClassNotFoundException {
        if (currentUser == null || !currentUser.isAdmin()) {
            output.writeObject("ERROR: Unauthorized");
            return;
        }

        int userId = (Integer) input.readObject();
        String query = "UPDATE users SET role = 'admin' WHERE id = ?";

        try {
            DatabaseConnection.executeUpdate(query, userId);
            output.writeObject("SUCCESS");
        } catch (SQLException e) {
            output.writeObject("FAILURE");
            output.writeObject(e.getMessage());
        }
    }

    private void handleDeleteUser() throws IOException, ClassNotFoundException {
        if (currentUser == null || !currentUser.isAdmin()) {
            output.writeObject("ERROR: Unauthorized");
            return;
        }

        int userId = (Integer) input.readObject();
        String query = "DELETE FROM users WHERE id = ?";

        try {
            DatabaseConnection.executeUpdate(query, userId);
            output.writeObject("SUCCESS");
        } catch (SQLException e) {
            output.writeObject("FAILURE");
            output.writeObject(e.getMessage());
        }
    }

    private void handleGetStatistics() throws IOException {
        if (currentUser == null || !currentUser.isAdmin()) {
            output.writeObject(new HashMap<>());
            return;
        }

        Map<String, Integer> stats = new HashMap<>();

        try {
            ResultSet rs = DatabaseConnection.executeQuery("SELECT COUNT(*) FROM users WHERE role = 'student'");
            if (rs.next()) stats.put("totalStudents", rs.getInt(1));

            rs = DatabaseConnection.executeQuery("SELECT COUNT(*) FROM materials");
            if (rs.next()) stats.put("totalMaterials", rs.getInt(1));

            rs = DatabaseConnection.executeQuery("SELECT COUNT(*) FROM courses");
            if (rs.next()) stats.put("totalCourses", rs.getInt(1));

            rs = DatabaseConnection.executeQuery("SELECT COUNT(*) FROM users WHERE role = 'admin'");
            if (rs.next()) stats.put("totalAdmins", rs.getInt(1));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        output.writeObject(stats);
    }

    private void handleSearch() throws IOException, ClassNotFoundException {
        String keyword = (String) input.readObject();

        String query = "SELECT m.*, u.name as uploader_name, c.name as course_name " +
                "FROM materials m " +
                "LEFT JOIN users u ON m.uploaded_by = u.student_id " +
                "LEFT JOIN courses c ON m.course_id = c.id " +
                "WHERE m.title LIKE ? OR c.name LIKE ? " +
                "ORDER BY m.upload_date DESC";

        String searchPattern = "%" + keyword + "%";
        List<Material> materials = new ArrayList<>();

        try (ResultSet rs = DatabaseConnection.executeQuery(query, searchPattern, searchPattern)) {
            while (rs.next()) {
                Material material = new Material();
                material.setId(rs.getInt("id"));
                material.setTitle(rs.getString("title"));
                material.setType(rs.getString("type"));
                material.setCourseName(rs.getString("course_name"));
                material.setDepartment(rs.getString("department"));
                material.setBatch(rs.getInt("batch"));
                material.setUploaderName(rs.getString("uploader_name"));
                materials.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        output.writeObject(materials);
    }

    private String getFileExtension(String type) {
        switch(type) {
            case "pdf": return ".pdf";
            case "ppt": return ".pptx";
            case "docx": return ".docx";
            case "image": return ".jpg";
            default: return ".bin";
        }
    }

    private void closeConnection() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}