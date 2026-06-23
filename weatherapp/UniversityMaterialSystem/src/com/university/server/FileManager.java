package com.university.server;

import java.io.*;
import java.nio.file.*;

public class FileManager {
    private static final String UPLOAD_DIR = "uploads";
    
    public static void initializeStorage() {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
            System.out.println("📁 Created uploads directory");
        }
        
        // Create department folders
        String[] departments = {"Software_Engineering", "Computer_Science", "Biomedical_Engineering", "Electrical_Engineering"};
        for (String dept : departments) {
            File deptDir = new File(UPLOAD_DIR + "/" + dept);
            if (!deptDir.exists()) {
                deptDir.mkdir();
            }
            
            // Create batch folders (1-4)
            for (int batch = 1; batch <= 4; batch++) {
                File batchDir = new File(UPLOAD_DIR + "/" + dept + "/" + batch + "_Year");
                if (!batchDir.exists()) {
                    batchDir.mkdir();
                }
            }
        }
        
        System.out.println("✅ File storage system ready");
    }
    
    public static String saveFile(byte[] fileData, String department, int batch, String fileName) {
        try {
            // Create directory path
            String deptFolder = department.replaceAll("\\s+", "_");
            String batchFolder = batch + "_Year";
            String fullPath = UPLOAD_DIR + "/" + deptFolder + "/" + batchFolder;
            
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // Save file
            String filePath = fullPath + "/" + fileName;
            Files.write(Paths.get(filePath), fileData);
            
            System.out.println("💾 File saved: " + filePath);
            return filePath;
            
        } catch (IOException e) {
            System.err.println("❌ Failed to save file: " + e.getMessage());
            return null;
        }
    }
    
    public static byte[] readFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("File not found: " + filePath);
                return null;
            }
            
            return Files.readAllBytes(Paths.get(filePath));
            
        } catch (IOException e) {
            System.err.println("❌ Failed to read file: " + e.getMessage());
            return null;
        }
    }
    
    public static boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            return file.delete();
        } catch (Exception e) {
            return false;
        }
    }
    
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        return file.exists() ? file.length() : 0;
    }
}