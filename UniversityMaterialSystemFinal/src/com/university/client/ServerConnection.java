package com.university.client;

import com.university.shared.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ServerConnection {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public ServerConnection(String host, int port) {
        try {
            socket = new Socket(host, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to server at " + host + ":" + port);
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }
    }

    public User login(String studentId, String password) throws Exception {
        output.writeObject("LOGIN");
        output.writeObject(studentId);
        output.writeObject(password);

        String response = (String) input.readObject();
        if (response.equals("SUCCESS")) {
            return (User) input.readObject();
        } else {
            String error = (String) input.readObject();
            throw new Exception(error);
        }
    }

    public List<String> getDepartments() throws Exception {
        output.writeObject("GET_DEPARTMENTS");
        return (List<String>) input.readObject();
    }

    public List<Integer> getBatches() throws Exception {
        output.writeObject("GET_BATCHES");
        return (List<Integer>) input.readObject();
    }

    public List<Course> getCourses(String department, int batch) throws Exception {
        output.writeObject("GET_COURSES");
        output.writeObject(department);
        output.writeObject(batch);
        return (List<Course>) input.readObject();
    }

    public List<Material> getMaterials(int courseId) throws Exception {
        output.writeObject("GET_MATERIALS");
        output.writeObject(courseId);
        return (List<Material>) input.readObject();
    }

    public Material downloadMaterial(int materialId) throws Exception {
        output.writeObject("DOWNLOAD_MATERIAL");
        output.writeObject(materialId);

        String response = (String) input.readObject();
        if (response.equals("SUCCESS")) {
            return (Material) input.readObject();
        } else {
            String error = (String) input.readObject();
            throw new Exception(error);
        }
    }

    public boolean uploadMaterial(String title, String type, int courseId, byte[] fileData) throws Exception {
        output.writeObject("UPLOAD_MATERIAL");
        output.writeObject(title);
        output.writeObject(type);
        output.writeObject(courseId);
        output.writeObject(fileData);

        String response = (String) input.readObject();
        if (response.equals("SUCCESS")) {
            input.readObject(); // consume success message
            return true;
        } else {
            String error = (String) input.readObject();
            throw new Exception(error);
        }
    }

    public List<User> getAllUsers() throws Exception {
        output.writeObject("GET_ALL_USERS");
        return (List<User>) input.readObject();
    }

    public boolean banUser(int userId) throws Exception {
        output.writeObject("BAN_USER");
        output.writeObject(userId);
        String response = (String) input.readObject();
        return response.equals("SUCCESS");
    }

    public boolean grantAdmin(int userId) throws Exception {
        output.writeObject("GRANT_ADMIN");
        output.writeObject(userId);
        String response = (String) input.readObject();
        return response.equals("SUCCESS");
    }

    public boolean deleteUser(int userId) throws Exception {
        output.writeObject("DELETE_USER");
        output.writeObject(userId);
        String response = (String) input.readObject();
        return response.equals("SUCCESS");
    }

    public Map<String, Integer> getStatistics() throws Exception {
        output.writeObject("GET_STATISTICS");
        return (Map<String, Integer>) input.readObject();
    }

    public List<Material> searchMaterials(String keyword) throws Exception {
        output.writeObject("SEARCH");
        output.writeObject(keyword);
        return (List<Material>) input.readObject();
    }

    public void logout() {
        try {
            output.writeObject("LOGOUT");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}