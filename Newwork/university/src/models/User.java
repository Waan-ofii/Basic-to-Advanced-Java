package com.university.shared.models;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String studentId;
    private String name;
    private String email;
    private String department;
    private String academicLevel;
    private int yearLevel;
    private boolean isAdmin;
    
    public User(String studentId, String name, String email, 
                String department, String academicLevel, int yearLevel, boolean isAdmin) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.department = department;
        this.academicLevel = academicLevel;
        this.yearLevel = yearLevel;
        this.isAdmin = isAdmin;
    }
    
    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getAcademicLevel() { return academicLevel; }
    public void setAcademicLevel(String academicLevel) { this.academicLevel = academicLevel; }
    public int getYearLevel() { return yearLevel; }
    public void setYearLevel(int yearLevel) { this.yearLevel = yearLevel; }
    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }
    
    @Override
    public String toString() {
        return name + " (" + studentId + ") - " + department + " Year " + yearLevel;
    }
}