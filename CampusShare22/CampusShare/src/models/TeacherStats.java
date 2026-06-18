package models;

public class TeacherStats {
    private int totalUploads;
    private int pendingCount;
    private int approvedCount;
    private int rejectedCount;
    private int totalDownloads;
    private String mostDownloadedTitle;
    
    public TeacherStats() {
    }
    
    public int getTotalUploads() {
        return totalUploads;
    }
    
    public void setTotalUploads(int totalUploads) {
        this.totalUploads = totalUploads;
    }
    
    public int getPendingCount() {
        return pendingCount;
    }
    
    public void setPendingCount(int pendingCount) {
        this.pendingCount = pendingCount;
    }
    
    public int getApprovedCount() {
        return approvedCount;
    }
    
    public void setApprovedCount(int approvedCount) {
        this.approvedCount = approvedCount;
    }
    
    public int getRejectedCount() {
        return rejectedCount;
    }
    
    public void setRejectedCount(int rejectedCount) {
        this.rejectedCount = rejectedCount;
    }
    
    public int getTotalDownloads() {
        return totalDownloads;
    }
    
    public void setTotalDownloads(int totalDownloads) {
        this.totalDownloads = totalDownloads;
    }
    
    public String getMostDownloadedTitle() {
        return mostDownloadedTitle;
    }
    
    public void setMostDownloadedTitle(String mostDownloadedTitle) {
        this.mostDownloadedTitle = mostDownloadedTitle;
    }
}
