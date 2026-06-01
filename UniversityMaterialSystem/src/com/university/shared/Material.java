package com.university.shared;

import java.io.Serializable;
import java.sql.Timestamp;

public class Material implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String title;
    private String type; // pdf, ppt, docx, image
    private String filePath;
    private int courseId;
    private String courseName;
    private String department;
    private int batch;
    private String uploadedBy;
    private String uploaderName;
    private Timestamp uploadDate;
    private int downloadCount;
    private byte[] fileData; // For file transfer
    
    public Material() {}
    
    public Material(String title, String type, int courseId, String department, int batch) {
        this.title = title;
        this.type = type;
        this.courseId = courseId;
        this.department = department;
        this.batch = batch;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public int getBatch() { return batch; }
    public void setBatch(int batch) { this.batch = batch; }
    
    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
    
    public String getUploaderName() { return uploaderName; }
    public void setUploaderName(String uploaderName) { this.uploaderName = uploaderName; }
    
    public Timestamp getUploadDate() { return uploadDate; }
    public void setUploadDate(Timestamp uploadDate) { this.uploadDate = uploadDate; }
    
    public int getDownloadCount() { return downloadCount; }
    public void setDownloadCount(int downloadCount) { this.downloadCount = downloadCount; }
    
    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { this.fileData = fileData; }
    
    public String getFileExtension() {
        switch(type) {
            case "pdf": return ".pdf";
            case "ppt": return ".pptx";
            case "docx": return ".docx";
            case "image": return ".jpg";
            default: return "";
        }
    }
}