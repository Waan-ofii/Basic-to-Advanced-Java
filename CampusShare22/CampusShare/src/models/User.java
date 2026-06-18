package models;

import java.sql.Timestamp;

public class User {
    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role;
    private int deptId;
    private String deptName;
    private boolean isActive;
    private String profilePic;
    private Timestamp createdAt;
    
    public User() {}
    
    public User(String username, String password, String fullName, String email, String role, int deptId) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.deptId = deptId;
    }
    
    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public int getDeptId() { return deptId; }
    public String getDeptName() { return deptName; }
    public boolean isActive() { return isActive; }
    public String getProfilePic() { return profilePic; }
    public Timestamp getCreatedAt() { return createdAt; }
    
    // Setters
    public void setUserId(int userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setDeptId(int deptId) { this.deptId = deptId; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public void setActive(boolean active) { isActive = active; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return fullName + " (" + username + ")";
    }
}
