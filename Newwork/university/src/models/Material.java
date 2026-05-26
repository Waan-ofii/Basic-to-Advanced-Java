package com.university.shared.models;

import java.io.Serializable;
import java.sql.Timestamp;

public class Material implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int materialId;
    private String title;
    private String description;
    private String materialType;
    private String courseName;
    private String department;
    private int yearLevel;
    private int semester;
    private String filePath;
    private long fileSize;
    private String uploaderId;
    private Timestamp uploadDate;
    private int downloadCount;
    
    // Constructors
    public Material() {}
    
    public Material(String title, String description, String materialType, 
                    String courseName, String department, int yearLevel, String filePath) {
        this.title = title;
        this.description = description;
        this.materialType = materialType;
        this.courseName = courseName;
        this.department = department;
        this.yearLevel = yearLevel;
        this.filePath = filePath;
    }
    
    // Getters and Setters
    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getMaterialType() { return materialType; }
    public void setMaterialType(String materialType) { this.materialType = materialType; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public int getYearLevel() { return yearLevel; }
    public void setYearLevel(int yearLevel) { this.yearLevel = yearLevel; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public String getUploaderId() { return uploaderId; }
    public void setUploaderId(String uploaderId) { this.uploaderId = uploaderId; }
    public Timestamp getUploadDate() { return uploadDate; }
    public void setUploadDate(Timestamp uploadDate) { this.uploadDate = uploadDate; }
    public int getDownloadCount() { return downloadCount; }
    public void setDownloadCount(int downloadCount) { this.downloadCount = downloadCount; }
}