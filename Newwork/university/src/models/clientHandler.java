package com.university.server.handlers;

import com.university.server.database.*;
import com.university.shared.models.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private User currentUser;
    
    private AuthDAO authDAO = new AuthDAO();
    private MaterialDAO materialDAO = new MaterialDAO();
    
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
                    case "SEARCH_MATERIALS":
                        handleSearch();
                        break;
                    case "GET_DEPARTMENTS":
                        handleGetDepartments();
                        break;
                    case "UPLOAD_MATERIAL":
                        handleUploadMaterial();
                        break;
                    case "DOWNLOAD_MATERIAL":
                        handleDownloadMaterial();
                        break;
                    case "GET_ANNOUNCEMENTS":
                        handleGetAnnouncements();
                        break;
                    case "LOGOUT":
                        handleLogout();
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
        
        User user = authDAO.authenticate(studentId, password);
        
        if (user != null) {
            this.currentUser = user;
            output.writeObject("SUCCESS");
            output.writeObject(user);
            System.out.println("User logged in: " + user.getName());
        } else {
            output.writeObject("FAILURE");
            output.writeObject("Invalid credentials");
        }
    }
    
    private void handleSearch() throws IOException, ClassNotFoundException {
        String keyword = (String) input.readObject();
        String department = (String) input.readObject();
        Integer yearLevel = (Integer) input.readObject();
        
        List<Material> results = materialDAO.searchMaterials(keyword, department, yearLevel);
        output.writeObject(results);
    }
    
    private void handleGetDepartments() throws IOException {
        List<String> departments = Arrays.asList(
            "Freshman", "Pre-Engineering",
            "Software Engineering", "Computer Science",
            "Biomedical Engineering", "Electrical Engineering"
        );
        output.writeObject(departments);
    }
    
    private void handleUploadMaterial() throws IOException, ClassNotFoundException {
        if (currentUser == null || !currentUser.isAdmin()) {
            output.writeObject("ERROR: Unauthorized");
            return;
        }
        
        Material material = (Material) input.readObject();
        byte[] fileData = (byte[]) input.readObject();
        
        boolean success = materialDAO.saveMaterial(material, fileData, currentUser.getStudentId());
        output.writeObject(success ? "SUCCESS" : "FAILURE");
    }
    
    private void handleDownloadMaterial() throws IOException, ClassNotFoundException {
        int materialId = (Integer) input.readObject();
        
        Material material = materialDAO.getMaterialById(materialId);
        byte[] fileData = materialDAO.getFileData(materialId);
        
        if (material != null && fileData != null) {
            output.writeObject("SUCCESS");
            output.writeObject(material);
            output.writeObject(fileData);
            materialDAO.incrementDownloadCount(materialId);
        } else {
            output.writeObject("FAILURE");
        }
    }
    
    private void handleGetAnnouncements() throws IOException {
        List<String> announcements = materialDAO.getRecentAnnouncements();
        output.writeObject(announcements);
    }
    
    private void handleLogout() throws IOException {
        System.out.println("User logged out: " + currentUser.getName());
        output.writeObject("LOGOUT_SUCCESS");
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