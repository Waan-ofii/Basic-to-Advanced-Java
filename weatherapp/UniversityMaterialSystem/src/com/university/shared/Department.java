package com.university.shared;

import java.io.Serializable;

public class Department implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String name;
    private int courseCount;
    private int studentCount;
    
    public Department() {}
    
    public Department(String name) {
        this.name = name;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getCourseCount() { return courseCount; }
    public void setCourseCount(int courseCount) { this.courseCount = courseCount; }
    
    public int getStudentCount() { return studentCount; }
    public void setStudentCount(int studentCount) { this.studentCount = studentCount; }
    
    @Override
    public String toString() {
        return name;
    }
}