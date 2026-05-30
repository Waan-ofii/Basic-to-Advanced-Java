package com.university.client;

import com.university.shared.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class StudentDashboard {
    
    private ServerConnection serverConnection;
    private User currentUser;
    private ComboBox<String> departmentCombo;
    private ComboBox<Integer> batchCombo;
    private ListView<String> coursesListView;
    private ListView<String> materialsListView;
    private TextField searchField;
    private Label statusLabel;
    private List<Course> currentCourses;
    private List<Material> currentMaterials;
    
    public StudentDashboard(ServerConnection serverConnection, User user) {
        this.serverConnection = serverConnection;
        this.currentUser = user;
    }
    
    public void start(Stage stage) {
        // Header
        Label welcomeLabel = new Label("Welcome, " + currentUser.getName() + "!");
        welcomeLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label infoLabel = new Label("📚 " + currentUser.getDepartment() + " | Year " + currentUser.getBatch());
        infoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ecf0f1;");
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> {
            serverConnection.logout();
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.start(stage);
        });
        
        HBox leftHeader = new HBox(15, welcomeLabel, infoLabel);
        HBox headerBox = new HBox(20, leftHeader, logoutBtn);
        headerBox.setPadding(new Insets(15, 20, 15, 20));
        headerBox.setStyle("-fx-background-color: #2c3e50;");
        HBox.setHgrow(leftHeader, Priority.ALWAYS);
        
        // Search bar
        searchField = new TextField();
        searchField.setPromptText("🔍 Search materials by title or course...");
        searchField.setPrefWidth(500);
        searchField.setStyle("-fx-padding: 8; -fx-font-size: 14px;");
        
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 20; -fx-cursor: hand;");
        searchBtn.setOnAction(e -> searchMaterials());
        
        HBox searchBox = new HBox(10, searchField, searchBtn);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(15));
        searchBox.setStyle("-fx-background-color: white; -fx-border-color: #dcdde1; -fx-border-width: 0 0 1 0;");
        
        // Department selection
        Label deptLabel = new Label("Select Department:");
        deptLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        departmentCombo = new ComboBox<>();
        departmentCombo.setPrefWidth(220);
        departmentCombo.setStyle("-fx-padding: 5;");
        departmentCombo.setOnAction(e -> {
            if (departmentCombo.getValue() != null && batchCombo.getValue() != null) {
                loadCourses();
            }
        });
        
        // Batch selection
        Label batchLabel = new Label("Select Year:");
        batchLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        batchCombo = new ComboBox<>();
        batchCombo.getItems().addAll(1, 2, 3, 4);
        batchCombo.setPrefWidth(100);
        batchCombo.setStyle("-fx-padding: 5;");
        batchCombo.setOnAction(e -> {
            if (departmentCombo.getValue() != null && batchCombo.getValue() != null) {
                loadCourses();
            }
        });
        
        VBox selectionBox = new VBox(12, deptLabel, departmentCombo, batchLabel, batchCombo);
        selectionBox.setPadding(new Insets(20));
        selectionBox.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 0 1 0 0;");
        selectionBox.setPrefWidth(280);
        
        // Courses list
        Label coursesLabel = new Label("📚 Courses");
        coursesLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        coursesListView = new ListView<>();
        coursesListView.setPrefHeight(400);
        coursesListView.setStyle("-fx-font-size: 13px;");
        coursesListView.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                loadMaterials();
            }
        });
        
        VBox coursesBox = new VBox(10, coursesLabel, coursesListView);
        coursesBox.setPadding(new Insets(15));
        coursesBox.setPrefWidth(350);
        
        // Materials list
        Label materialsLabel = new Label("📄 Materials");
        materialsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        materialsListView = new ListView<>();
        materialsListView.setPrefHeight(400);
        materialsListView.setStyle("-fx-font-size: 13px;");
        
        Button downloadBtn = new Button("⬇ Download Selected Material");
        downloadBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-size: 14px; -fx-cursor: hand;");
        downloadBtn.setOnAction(e -> downloadMaterial());
        
        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10 20; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> {
            if (departmentCombo.getValue() != null && batchCombo.getValue() != null) {
                loadCourses();
            }
        });
        
        HButtonBox materialButtons = new HButtonBox(10, downloadBtn, refreshBtn);
        
        VBox materialsBox = new VBox(10, materialsLabel, materialsListView, materialButtons);
        materialsBox.setPadding(new Insets(15));
        materialsBox.setPrefWidth(450);
        
        // Status label
        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-padding: 5;");
        
        // Main layout
        HBox contentBox = new HBox(selectionBox, coursesBox, materialsBox);
        HBox.setHgrow(coursesBox, Priority.ALWAYS);
        HBox.setHgrow(materialsBox, Priority.ALWAYS);
        
        VBox mainLayout = new VBox(headerBox, searchBox, contentBox, statusLabel);
        VBox.setVgrow(contentBox, Priority.ALWAYS);
        
        Scene scene = new Scene(mainLayout, 1300, 750);
        stage.setTitle("Student Dashboard - " + currentUser.getName());
        stage.setScene(scene);
        stage.show();
        
        // Load initial data
        loadDepartments();
        
        // Set default values from logged in user
        if (currentUser.getDepartment() != null && !currentUser.getDepartment().isEmpty()) {
            Platform.runLater(() -> {
                departmentCombo.setValue(currentUser.getDepartment());
                batchCombo.setValue(currentUser.getBatch());
            });
        }
    }
    
    private void loadDepartments() {
        statusLabel.setText("Loading departments...");
        new Thread(() -> {
            try {
                List<String> departments = serverConnection.getDepartments();
                Platform.runLater(() -> {
                    departmentCombo.getItems().setAll(departments);
                    statusLabel.setText("Select a department and year to view courses");
                });
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Error loading departments: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }
    
    private void loadCourses() {
        String department = departmentCombo.getValue();
        Integer batch = batchCombo.getValue();
        
        if (department == null || batch == null) return;
        
        statusLabel.setText("Loading courses for " + department + " - Year " + batch + "...");
        coursesListView.getItems().clear();
        coursesListView.setDisable(true);
        
        new Thread(() -> {
            try {
                currentCourses = serverConnection.getCourses(department, batch);
                Platform.runLater(() -> {
                    coursesListView.getItems().clear();
                    for (Course course : currentCourses) {
                        String display = course.getCode() + " - " + course.getName();
                        if (course.getMaterialCount() > 0) {
                            display += " 📄 (" + course.getMaterialCount() + ")";
                        }
                        coursesListView.getItems().add(display);
                    }
                    coursesListView.setDisable(false);
                    statusLabel.setText("Found " + currentCourses.size() + " courses");
                    if (currentCourses.isEmpty()) {
                        statusLabel.setText("No courses found for " + department + " - Year " + batch);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Error loading courses: " + e.getMessage());
                    coursesListView.setDisable(false);
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    private void loadMaterials() {
        int selectedIndex = coursesListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || currentCourses == null || selectedIndex >= currentCourses.size()) return;
        
        Course selectedCourse = currentCourses.get(selectedIndex);
        statusLabel.setText("Loading materials for " + selectedCourse.getName() + "...");
        materialsListView.getItems().clear();
        materialsListView.setDisable(true);
        
        new Thread(() -> {
            try {
                currentMaterials = serverConnection.getMaterials(selectedCourse.getId());
                Platform.runLater(() -> {
                    materialsListView.getItems().clear();
                    for (Material material : currentMaterials) {
                        String display = getFileIcon(material.getType()) + " " + material.getTitle();
                        display += " (" + material.getType().toUpperCase() + ")";
                        display += " - by " + material.getUploaderName();
                        display += " - ⬇ " + material.getDownloadCount();
                        materialsListView.getItems().add(display);
                    }
                    materialsListView.setDisable(false);
                    statusLabel.setText(currentMaterials.size() + " materials available");
                    if (currentMaterials.isEmpty()) {
                        statusLabel.setText("No materials uploaded for this course yet");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Error loading materials: " + e.getMessage());
                    materialsListView.setDisable(false);
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    private void downloadMaterial() {
        int selectedIndex = materialsListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || currentMaterials == null || selectedIndex >= currentMaterials.size()) {
            showAlert("No Selection", "Please select a material to download", Alert.AlertType.WARNING);
            return;
        }
        
        Material selectedMaterial = currentMaterials.get(selectedIndex);
        statusLabel.setText("Downloading " + selectedMaterial.getTitle() + "...");
        
        new Thread(() -> {
            try {
                Material material = serverConnection.downloadMaterial(selectedMaterial.getId());
                
                // Save file dialog
                Platform.runLater(() -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save Material");
                    fileChooser.setInitialFileName(material.getTitle() + material.getFileExtension());
                    File saveFile = fileChooser.showSaveDialog(null);
                    
                    if (saveFile != null) {
                        try {
                            Files.write(saveFile.toPath(), material.getFileData());
                            showAlert("Download Complete", "File saved to: " + saveFile.getPath(), Alert.AlertType.INFORMATION);
                            statusLabel.setText("Download complete: " + material.getTitle());
                        } catch (Exception ex) {
                            statusLabel.setText("Error saving file: " + ex.getMessage());
                            showAlert("Error", "Failed to save file: " + ex.getMessage(), Alert.AlertType.ERROR);
                        }
                    } else {
                        statusLabel.setText("Download cancelled");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Download failed: " + e.getMessage());
                    showAlert("Download Failed", e.getMessage(), Alert.AlertType.ERROR);
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    private void searchMaterials() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            showAlert("Search", "Please enter a search keyword", Alert.AlertType.WARNING);
            return;
        }
        
        statusLabel.setText("Searching for: " + keyword + "...");
        materialsListView.getItems().clear();
        materialsListView.setDisable(true);
        
        new Thread(() -> {
            try {
                List<Material> results = serverConnection.searchMaterials(keyword);
                Platform.runLater(() -> {
                    materialsListView.getItems().clear();
                    for (Material material : results) {
                        String display = "🔍 " + material.getTitle();
                        display += " (" + material.getCourseName() + ")";
                        display += " - " + material.getDepartment() + " Year " + material.getBatch();
                        materialsListView.getItems().add(display);
                    }
                    materialsListView.setDisable(false);
                    currentMaterials = results;
                    statusLabel.setText("Found " + results.size() + " results for '" + keyword + "'");
                    if (results.isEmpty()) {
                        statusLabel.setText("No results found for '" + keyword + "'");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Search error: " + e.getMessage());
                    materialsListView.setDisable(false);
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    private String getFileIcon(String type) {
        switch(type.toLowerCase()) {
            case "pdf": return "📕";
            case "ppt": return "📊";
            case "docx": return "📝";
            case "image": return "🖼️";
            default: return "📄";
        }
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // Helper class for HBox with buttons
    private static class HButtonBox extends HBox {
        public HButtonBox(double spacing, Button... buttons) {
            super(spacing);
            setAlignment(Pos.CENTER);
            getChildren().addAll(buttons);
        }
    }
}