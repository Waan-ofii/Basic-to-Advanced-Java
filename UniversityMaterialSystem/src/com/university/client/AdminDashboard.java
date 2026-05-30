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
import java.util.Map;

public class AdminDashboard {
    
    private ServerConnection serverConnection;
    private User currentUser;
    private TableView<User> userTable;
    private Label statsLabel;
    private ComboBox<String> deptCombo;
    private ComboBox<Integer> batchCombo;
    private ComboBox<String> courseCombo;
    private List<Course> currentCourses;
    private Label statusLabel;
    
    public AdminDashboard(ServerConnection serverConnection, User user) {
        this.serverConnection = serverConnection;
        this.currentUser = user;
    }
    
    public void start(Stage stage) {
        // Header
        Label welcomeLabel = new Label("👑 Admin Dashboard");
        welcomeLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label adminLabel = new Label("Logged in as: " + currentUser.getName() + " (" + currentUser.getStudentId() + ")");
        adminLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ecf0f1;");
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8 20; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> {
            serverConnection.logout();
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.start(stage);
        });
        
        HBox leftHeader = new HBox(20, welcomeLabel, adminLabel);
        HBox headerBox = new HBox(20, leftHeader, logoutBtn);
        headerBox.setPadding(new Insets(15, 20, 15, 20));
        headerBox.setStyle("-fx-background-color: #2c3e50;");
        HBox.setHgrow(leftHeader, Priority.ALWAYS);
        
        // Status label
        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-padding: 5 15;");
        
        // Tab pane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-font-size: 14px;");
        
        // Statistics Tab
        Tab statsTab = new Tab("📊 Statistics");
        statsTab.setContent(createStatsPanel());
        statsTab.setClosable(false);
        
        // User Management Tab
        Tab userTab = new Tab("👥 User Management");
        userTab.setContent(createUserManagementPanel());
        userTab.setClosable(false);
        
        // Upload Material Tab
        Tab uploadTab = new Tab("📤 Upload Material");
        uploadTab.setContent(createUploadPanel());
        uploadTab.setClosable(false);
        
        tabPane.getTabs().addAll(statsTab, userTab, uploadTab);
        
        VBox mainLayout = new VBox(headerBox, tabPane, statusLabel);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        
        Scene scene = new Scene(mainLayout, 1300, 750);
        stage.setTitle("Admin Dashboard");
        stage.setScene(scene);
        stage.show();
        
        // Load initial data
        loadStatistics();
        loadUsers();
    }
    
    private VBox createStatsPanel() {
        statsLabel = new Label();
        statsLabel.setStyle("-fx-font-size: 16px; -fx-padding: 20;");
        
        Button refreshBtn = new Button("🔄 Refresh Statistics");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10 20; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> loadStatistics());
        
        VBox panel = new VBox(20, statsLabel, refreshBtn);
        panel.setPadding(new Insets(30));
        panel.setAlignment(Pos.TOP_CENTER);
        return panel;
    }
    
    private VBox createUserManagementPanel() {
        // Create table
        userTable = new TableView<>();
        userTable.setPrefHeight(500);
        
        TableColumn<User, String> idCol = new TableColumn<>("Student ID");
        idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStudentId()));
        idCol.setPrefWidth(120);
        
        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setPrefWidth(150);
        
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        emailCol.setPrefWidth(200);
        
        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole()));
        roleCol.setPrefWidth(80);
        
        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
        statusCol.setPrefWidth(80);
        
        TableColumn<User, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDepartment()));
        deptCol.setPrefWidth(150);
        
        TableColumn<User, Integer> batchCol = new TableColumn<>("Year");
        batchCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getBatch()).asObject());
        batchCol.setPrefWidth(60);
        
        userTable.getColumns().addAll(idCol, nameCol, emailCol, roleCol, statusCol, deptCol, batchCol);
        
        // Buttons
        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> loadUsers());
        
        Button grantAdminBtn = new Button("👑 Grant Admin");
        grantAdminBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 8 15; -fx-cursor: hand;");
        grantAdminBtn.setOnAction(e -> grantAdmin());
        
        Button banUserBtn = new Button("🔨 Ban User");
        banUserBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-padding: 8 15; -fx-cursor: hand;");
        banUserBtn.setOnAction(e -> banUser());
        
        Button deleteUserBtn = new Button("🗑️ Delete User");
        deleteUserBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8 15; -fx-cursor: hand;");
        deleteUserBtn.setOnAction(e -> deleteUser());
        
        Button activateUserBtn = new Button("✅ Activate User");
        activateUserBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 8 15; -fx-cursor: hand;");
        activateUserBtn.setOnAction(e -> activateUser());
        
        HBox buttonBox = new HBox(10, refreshBtn, grantAdminBtn, banUserBtn, activateUserBtn, deleteUserBtn);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        buttonBox.setAlignment(Pos.CENTER);
        
        VBox panel = new VBox(10, userTable, buttonBox);
        panel.setPadding(new Insets(20));
        return panel;
    }
    
    private VBox createUploadPanel() {
        // Form fields
        Label titleLabel = new Label("Material Title:");
        titleLabel.setStyle("-fx-font-weight: bold;");
        TextField titleField = new TextField();
        titleField.setPromptText("Enter material title");
        titleField.setPrefWidth(400);
        
        Label typeLabel = new Label("File Type:");
        typeLabel.setStyle("-fx-font-weight: bold;");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("pdf", "ppt", "docx", "image");
        typeCombo.setPromptText("Select file type");
        typeCombo.setPrefWidth(200);
        
        Label deptLabel = new Label("Department:");
        deptLabel.setStyle("-fx-font-weight: bold;");
        deptCombo = new ComboBox<>();
        deptCombo.setPromptText("Select department");
        deptCombo.setPrefWidth(250);
        
        Label batchLabel = new Label("Year:");
        batchLabel.setStyle("-fx-font-weight: bold;");
        batchCombo = new ComboBox<>();
        batchCombo.getItems().addAll(1, 2, 3, 4);
        batchCombo.setPromptText("Select year");
        batchCombo.setPrefWidth(100);
        
        Label courseLabel = new Label("Course:");
        courseLabel.setStyle("-fx-font-weight: bold;");
        courseCombo = new ComboBox<>();
        courseCombo.setPromptText("Select course");
        courseCombo.setPrefWidth(350);
        
        Label fileLabel = new Label("File:");
        fileLabel.setStyle("-fx-font-weight: bold;");
        Label selectedFileLabel = new Label("No file selected");
        selectedFileLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        Button selectFileBtn = new Button("📁 Choose File");
        selectFileBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15; -fx-cursor: hand;");
        
        Button uploadBtn = new Button("📤 Upload Material");
        uploadBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10 25; -fx-font-size: 14px; -fx-cursor: hand;");
        
        // File data holder
        final byte[][] fileData = new byte[1][1];
        final String[] fileName = {""};
        
        selectFileBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Material File");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("PowerPoint Files", "*.pptx", "*.ppt"),
                new FileChooser.ExtensionFilter("Word Files", "*.docx", "*.doc"),
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.jpeg")
            );
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                try {
                    fileData[0] = Files.readAllBytes(file.toPath());
                    fileName[0] = file.getName();
                    selectedFileLabel.setText(file.getName() + " (" + (file.length() / 1024) + " KB)");
                    selectedFileLabel.setStyle("-fx-text-fill: #27ae60;");
                } catch (Exception ex) {
                    selectedFileLabel.setText("Error loading file");
                    selectedFileLabel.setStyle("-fx-text-fill: #e74c3c;");
                }
            }
        });
        
        // Load departments
        new Thread(() -> {
            try {
                List<String> depts = serverConnection.getDepartments();
                Platform.runLater(() -> deptCombo.getItems().setAll(depts));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        // Load courses when department and batch are selected
        deptCombo.setOnAction(e -> {
            if (deptCombo.getValue() != null && batchCombo.getValue() != null) {
                loadCoursesForUpload();
            }
        });
        
        batchCombo.setOnAction(e -> {
            if (deptCombo.getValue() != null && batchCombo.getValue() != null) {
                loadCoursesForUpload();
            }
        });
        
        uploadBtn.setOnAction(e -> {
            String title = titleField.getText().trim();
            String type = typeCombo.getValue();
            String courseSelection = courseCombo.getValue();
            
            if (title.isEmpty()) {
                showAlert("Error", "Please enter a title", Alert.AlertType.ERROR);
                return;
            }
            if (type == null) {
                showAlert("Error", "Please select file type", Alert.AlertType.ERROR);
                return;
            }
            if (courseSelection == null) {
                showAlert("Error", "Please select a course", Alert.AlertType.ERROR);
                return;
            }
            if (fileData[0] == null || fileData[0].length == 0) {
                showAlert("Error", "Please select a file to upload", Alert.AlertType.ERROR);
                return;
            }
            
            // Get course ID
            int courseId = -1;
            if (currentCourses != null) {
                for (Course c : currentCourses) {
                    if ((c.getCode() + " - " + c.getName()).equals(courseSelection)) {
                        courseId = c.getId();
                        break;
                    }
                }
            }
            
            if (courseId == -1) {
                showAlert("Error", "Invalid course selected", Alert.AlertType.ERROR);
                return;
            }
            
            uploadBtn.setDisable(true);
            uploadBtn.setText("Uploading...");
            statusLabel.setText("Uploading " + title + "...");
            
            final int finalCourseId = courseId;
            new Thread(() -> {
                try {
                    boolean success = serverConnection.uploadMaterial(title, type, finalCourseId, fileData[0]);
                    Platform.runLater(() -> {
                        uploadBtn.setDisable(false);
                        uploadBtn.setText("📤 Upload Material");
                        
                        if (success) {
                            showAlert("Success", "Material uploaded successfully!", Alert.AlertType.INFORMATION);
                            statusLabel.setText("Upload complete: " + title);
                            titleField.clear();
                            typeCombo.setValue(null);
                            courseCombo.setValue(null);
                            selectedFileLabel.setText("No file selected");
                            selectedFileLabel.setStyle("-fx-text-fill: #7f8c8d;");
                            fileData[0] = null;
                        } else {
                            statusLabel.setText("Upload failed");
                            showAlert("Error", "Upload failed", Alert.AlertType.ERROR);
                        }
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        uploadBtn.setDisable(false);
                        uploadBtn.setText("📤 Upload Material");
                        statusLabel.setText("Upload error: " + ex.getMessage());
                        showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                    });
                }
            }).start();
        });
        
        // Layout
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));
        
        formGrid.add(titleLabel, 0, 0);
        formGrid.add(titleField, 1, 0);
        formGrid.add(typeLabel, 0, 1);
        formGrid.add(typeCombo, 1, 1);
        formGrid.add(deptLabel, 0, 2);
        formGrid.add(deptCombo, 1, 2);
        formGrid.add(batchLabel, 0, 3);
        formGrid.add(batchCombo, 1, 3);
        formGrid.add(courseLabel, 0, 4);
        formGrid.add(courseCombo, 1, 4);
        formGrid.add(fileLabel, 0, 5);
        
        HBox fileBox = new HBox(10, selectFileBtn, selectedFileLabel);
        formGrid.add(fileBox, 1, 5);
        
        HBox uploadBox = new HBox(uploadBtn);
        uploadBox.setAlignment(Pos.CENTER);
        uploadBox.setPadding(new Insets(20));
        formGrid.add(uploadBox, 1, 6);
        
        VBox panel = new VBox(formGrid);
        return panel;
    }
    
    private void loadCoursesForUpload() {
        String department = deptCombo.getValue();
        Integer batch = batchCombo.getValue();
        
        new Thread(() -> {
            try {
                currentCourses = serverConnection.getCourses(department, batch);
                Platform.runLater(() -> {
                    courseCombo.getItems().clear();
                    for (Course c : currentCourses) {
                        courseCombo.getItems().add(c.getCode() + " - " + c.getName());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void loadStatistics() {
        statusLabel.setText("Loading statistics...");
        new Thread(() -> {
            try {
                Map<String, Integer> stats = serverConnection.getStatistics();
                Platform.runLater(() -> {
                    String text = "📊 SYSTEM STATISTICS\n\n" +
                                 "╔════════════════════════════════╗\n" +
                                 "║  👥 Total Students: " + padRight(stats.getOrDefault("totalStudents", 0), 23) + "║\n" +
                                 "║  👑 Total Admins: " + padRight(stats.getOrDefault("totalAdmins", 0), 25) + "║\n" +
                                 "║  📚 Total Courses: " + padRight(stats.getOrDefault("totalCourses", 0), 24) + "║\n" +
                                 "║  📄 Total Materials: " + padRight(stats.getOrDefault("totalMaterials", 0), 22) + "║\n" +
                                 "╚════════════════════════════════╝";
                    statsLabel.setText(text);
                    statusLabel.setText("Statistics loaded");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Error loading statistics: " + e.getMessage());
                    statsLabel.setText("Error loading statistics");
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    private String padRight(int number, int width) {
        String s = String.valueOf(number);
        return s + " ".repeat(Math.max(0, width - s.length()));
    }
    
    private void loadUsers() {
        statusLabel.setText("Loading users...");
        new Thread(() -> {
            try {
                List<User> users = serverConnection.getAllUsers();
                Platform.runLater(() -> {
                    userTable.setItems(FXCollections.observableArrayList(users));
                    statusLabel.setText("Loaded " + users.size() + " users");
                });
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Error loading users: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }
    
    private void grantAdmin() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a user to grant admin privileges", Alert.AlertType.WARNING);
            return;
        }
        
        if (selected.isAdmin()) {
            showAlert("Info", selected.getName() + " is already an admin", Alert.AlertType.INFORMATION);
            return;
        }
        
        statusLabel.setText("Granting admin to " + selected.getName() + "...");
        new Thread(() -> {
            try {
                boolean success = serverConnection.grantAdmin(selected.getId());
                Platform.runLater(() -> {
                    if (success) {
                        showAlert("Success", "Admin privileges granted to " + selected.getName(), Alert.AlertType.INFORMATION);
                        statusLabel.setText("Admin granted to " + selected.getName());
                        loadUsers();
                    } else {
                        statusLabel.setText("Failed to grant admin");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }
    
    private void banUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a user to ban", Alert.AlertType.WARNING);
            return;
        }
        
        if (selected.getStudentId().equals(currentUser.getStudentId())) {
            showAlert("Error", "You cannot ban yourself!", Alert.AlertType.ERROR);
            return;
        }
        
        if (!selected.isActive()) {
            showAlert("Info", selected.getName() + " is already banned", Alert.AlertType.INFORMATION);
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Ban");
        confirm.setHeaderText("Ban User");
        confirm.setContentText("Are you sure you want to ban " + selected.getName() + "?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                statusLabel.setText("Banning " + selected.getName() + "...");
                new Thread(() -> {
                    try {
                        boolean success = serverConnection.banUser(selected.getId());
                        Platform.runLater(() -> {
                            if (success) {
                                showAlert("Success", selected.getName() + " has been banned", Alert.AlertType.INFORMATION);
                                statusLabel.setText("User banned: " + selected.getName());
                                loadUsers();
                            } else {
                                statusLabel.setText("Failed to ban user");
                            }
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
                        e.printStackTrace();
                    }
                }).start();
            }
        });
    }
    
    private void activateUser() {
        // This would unban a user - you'd need a separate API endpoint
        showAlert("Info", "Activate user feature - would unban a user", Alert.AlertType.INFORMATION);
    }
    
    private void deleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a user to delete", Alert.AlertType.WARNING);
            return;
        }
        
        if (selected.getStudentId().equals(currentUser.getStudentId())) {
            showAlert("Error", "You cannot delete yourself!", Alert.AlertType.ERROR);
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete User");
        confirm.setContentText("Are you sure you want to permanently delete " + selected.getName() + "?\nThis action cannot be undone!");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                statusLabel.setText("Deleting " + selected.getName() + "...");
                new Thread(() -> {
                    try {
                        boolean success = serverConnection.deleteUser(selected.getId());
                        Platform.runLater(() -> {
                            if (success) {
                                showAlert("Success", selected.getName() + " has been deleted", Alert.AlertType.INFORMATION);
                                statusLabel.setText("User deleted: " + selected.getName());
                                loadUsers();
                            } else {
                                statusLabel.setText("Failed to delete user");
                            }
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
                        e.printStackTrace();
                    }
                }).start();
            }
        });
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}