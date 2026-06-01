package com.university.shared;

import java.io.Serializable;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String code;
    private String name;
    private String department;
    private int batch;
    private String description;
    private int materialCount;
    
    public Course() {}
    
    public Course(String code, String name, String department, int batch) {
        this.code = code;
        this.name = name;
        this.department = department;
        this.batch = batch;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public int getBatch() { return batch; }
    public void setBatch(int batch) { this.batch = batch; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getMaterialCount() { return materialCount; }
    public void setMaterialCount(int materialCount) { this.materialCount = materialCount; }
    
    @Override
    public String toString() {
        return code + " - " + name;
    }
}