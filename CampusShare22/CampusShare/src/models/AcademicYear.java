package models;

public class AcademicYear {
    private int yearId;
    private String yearName;
    private int sortOrder;
    
    public int getYearId() { return yearId; }
    public void setYearId(int yearId) { this.yearId = yearId; }
    public String getYearName() { return yearName; }
    public void setYearName(String yearName) { this.yearName = yearName; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    
    @Override
    public String toString() { return yearName; }
}
