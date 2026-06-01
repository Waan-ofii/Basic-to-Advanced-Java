package com.university.shared;

import java.io.Serializable;
import java.sql.Timestamp;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String studentId;
    private String name;
    private String password;
    private String email;
    private String role; // "admin" or "student"
    private String status; // "active" or "banned"
    private String department;
    private int batch; // 1,2,3,4 for year level
    private Timestamp createdAt;
    
    public User() {}
    
    public User(String studentId, String name, String email, String department, int batch) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.department = department;
        this.batch = batch;
        this.role = "student";
        this.status = "active";
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public int getBatch() { return batch; }
    public void setBatch(int batch) { this.batch = batch; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public boolean isAdmin() {
        return "admin".equals(role);
    }
    
    public boolean isActive() {
        return "active".equals(status);
    }
    
    @Override
    public String toString() {
        return name + " (" + studentId + ") - " + department + " Year " + batch;
    }
}