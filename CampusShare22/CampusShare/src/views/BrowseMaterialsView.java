package views;

import application.CampusShareApp;
import models.*;
import dao.*;
import services.FileDownloadService;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import javafx.collections.*;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;

public class BrowseMaterialsView {
    private CampusShareApp app;
    private User currentUser;
    private BorderPane view;
    
    // Navigation components
    private ComboBox<Department> deptCombo;
    private ComboBox<AcademicYear> yearCombo;
    private ComboBox<Semester> semesterCombo;
    private ComboBox<Course> courseCombo;
    
    // Content area
    private TableView<Material> materialsTable;
    private Label selectedCourseLabel;
    private ProgressBar downloadProgress;
    private Label progressLabel;
    private TextField searchField;
    private ObservableList<Material> originalMaterialsList = FXCollections.observableArrayList();
    
    private List<Department> departments;
    private List<AcademicYear> years;
    private List<Semester> semesters;
    private List<Course> courses;
    
    public BrowseMaterialsView(CampusShareApp app, User user) {
        this.app = app;
        this.currentUser = user;
        loadInitialData();
        createView();
        preselectUserDept();
    }
    
    private void loadInitialData() {
        try {
            departments = CourseNavigationDAO.getAllDepartments();
        } catch (Exception e) {
            e.printStackTrace();
            departments = List.of();
        }
    }
    
    private void createView() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: #f1f5f9;");
        
        // ========== TOP: Navigation Bar ==========
        VBox topBox = new VBox(0);
        
        // Header Banner
        HBox headerBanner = new HBox();
        headerBanner.setAlignment(Pos.CENTER_LEFT);
        headerBanner.setPadding(new Insets(0, 24, 0, 24));
        headerBanner.setPrefHeight(60);
        headerBanner.setStyle(
            "-fx-background-color: linear-gradient(to right, #1e1b4b, #312e81, #3730a3);" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 8, 0, 0, 3);"
        );
        
        HBox logo = new HBox(10);
        logo.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("🎓");
        icon.setFont(Font.font(20));
        Label appName = new Label("CampusShare");
        appName.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 18));
        appName.setTextFill(Color.WHITE);
        Label badge = new Label("Browse");
        badge.setStyle(
            "-fx-background-color: rgba(165,180,252,0.25);" +
            "-fx-text-fill: #c7d2fe;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 2 8 2 8;" +
            "-fx-background-radius: 20;"
        );
        logo.getChildren().addAll(icon, appName, badge);
        
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        
        Button backBtn = new Button("⬅  Back to Dashboard");
        backBtn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.15);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 7 16 7 16;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.25);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 7 16 7 16;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.15);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 7 16 7 16;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        backBtn.setOnAction(e -> {
            app.loginSuccess(currentUser);
        });
        
        headerBanner.getChildren().addAll(logo, headerSpacer, backBtn);
        
        // Navigation row card
        VBox filterCard = new VBox(10);
        filterCard.setPadding(new Insets(16, 24, 16, 24));
        filterCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 0 0 1 0;"
        );
        
        HBox navRow = new HBox(16);
        navRow.setAlignment(Pos.CENTER_LEFT);
        
        // Department
        VBox deptCol = comboGroup("Department", deptCombo = new ComboBox<>());
        deptCombo.getItems().addAll(departments);
        deptCombo.setPromptText("Select Department");
        deptCombo.setPrefWidth(220);
        deptCombo.setOnAction(e -> loadYears());
        
        // Year
        VBox yearCol = comboGroup("Year", yearCombo = new ComboBox<>());
        yearCombo.setPromptText("Select Year");
        yearCombo.setPrefWidth(140);
        yearCombo.setDisable(true);
        yearCombo.setOnAction(e -> loadSemesters());
        
        // Semester
        VBox semesterCol = comboGroup("Semester", semesterCombo = new ComboBox<>());
        semesterCombo.setPromptText("Select Semester");
        semesterCombo.setPrefWidth(140);
        semesterCombo.setDisable(true);
        semesterCombo.setOnAction(e -> loadCourses());
        
        // Course
        VBox courseCol = comboGroup("Course", courseCombo = new ComboBox<>());
        courseCombo.setPromptText("Select Course");
        courseCombo.setPrefWidth(260);
        courseCombo.setDisable(true);
        courseCombo.setOnAction(e -> loadMaterials());
        
        navRow.getChildren().addAll(deptCol, yearCol, semesterCol, courseCol);
        filterCard.getChildren().add(navRow);
        
        topBox.getChildren().addAll(headerBanner, filterCard);
        
        // ========== CENTER: Selected Course Info & Materials Table ==========
        VBox centerBox = new VBox(16);
        centerBox.setPadding(new Insets(24));
        
        // Selected course info & Search bar
        HBox centerHeader = new HBox(20);
        centerHeader.setAlignment(Pos.CENTER_LEFT);
        
        selectedCourseLabel = new Label("Select a course to view materials");
        selectedCourseLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        selectedCourseLabel.setStyle("-fx-text-fill: #1e293b;");
        
        Region centerSpacer = new Region();
        HBox.setHgrow(centerSpacer, Priority.ALWAYS);
        
        searchField = new TextField();
        searchField.setPromptText("🔍  Search materials by title or desc...");
        searchField.setPrefWidth(280);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterMaterials(newValue));
        
        centerHeader.getChildren().addAll(selectedCourseLabel, centerSpacer, searchField);
        
        // Materials Table
        materialsTable = new TableView<>();
        materialsTable.setPlaceholder(new Label("No materials available for this course"));
        VBox.setVgrow(materialsTable, Priority.ALWAYS);
        
        TableColumn<Material, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        titleCol.setPrefWidth(220);
        
        TableColumn<Material, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        descCol.setPrefWidth(320);
        
        TableColumn<Material, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaterialType()));
        typeCol.setPrefWidth(80);
        typeCol.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(String t, boolean e) {
                super.updateItem(t, e);
                if (e || t == null) { setGraphic(null); return; }
                Label tag = new Label(t.toUpperCase());
                tag.setStyle(
                    "-fx-background-color: " + typeColor(t) + ";" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 10px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 2 7 2 7;" +
                    "-fx-background-radius: 20;"
                );
                setGraphic(tag);
            }
        });
        
        TableColumn<Material, String> uploaderCol = new TableColumn<>("Uploaded By");
        uploaderCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUploaderName()));
        uploaderCol.setPrefWidth(140);
        
        TableColumn<Material, String> dateCol = new TableColumn<>("Upload Date");
        dateCol.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getUploadDate() != null
                ? cellData.getValue().getUploadDate().toString().substring(0, 10) : "N/A"));
        dateCol.setPrefWidth(110);
        
        TableColumn<Material, Integer> downloadsCol = new TableColumn<>("Downloads");
        downloadsCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDownloadCount()).asObject());
        downloadsCol.setPrefWidth(90);
        
        TableColumn<Material, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(110);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button downloadBtn = new Button("⬇  Download");
            {
                downloadBtn.setStyle(
                    "-fx-background-color: #4f46e5;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 5 12 5 12;" +
                    "-fx-background-radius: 6;" +
                    "-fx-cursor: hand;"
                );
                downloadBtn.setOnMouseEntered(e -> downloadBtn.setStyle(
                    "-fx-background-color: #4338ca;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 5 12 5 12;" +
                    "-fx-background-radius: 6;" +
                    "-fx-cursor: hand;"
                ));
                downloadBtn.setOnMouseExited(e -> downloadBtn.setStyle(
                    "-fx-background-color: #4f46e5;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 5 12 5 12;" +
                    "-fx-background-radius: 6;" +
                    "-fx-cursor: hand;"
                ));
                downloadBtn.setOnAction(e -> {
                    Material material = getTableView().getItems().get(getIndex());
                    downloadMaterial(material);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(downloadBtn);
                }
            }
        });
        
        materialsTable.getColumns().addAll(titleCol, descCol, typeCol, uploaderCol, dateCol, downloadsCol, actionCol);
        
        // Download progress area
        HBox progressBox = new HBox(12);
        progressBox.setAlignment(Pos.CENTER_LEFT);
        progressBox.setPadding(new Insets(10, 14, 10, 14));
        progressBox.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        
        downloadProgress = new ProgressBar(0);
        downloadProgress.setPrefWidth(220);
        downloadProgress.setPrefHeight(8);
        downloadProgress.setVisible(false);
        
        progressLabel = new Label();
        progressLabel.setVisible(false);
        progressLabel.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px;");
        
        progressBox.getChildren().addAll(downloadProgress, progressLabel);
        
        centerBox.getChildren().addAll(centerHeader, materialsTable, progressBox);
        
        view.setTop(topBox);
        view.setCenter(centerBox);
    }
    
    private void preselectUserDept() {
        if (currentUser != null && currentUser.getDeptId() > 0 && departments != null) {
            for (Department dept : departments) {
                if (dept.getDeptId() == currentUser.getDeptId()) {
                    deptCombo.setValue(dept);
                    loadYears();
                    break;
                }
            }
        }
    }
    
    private void loadYears() {
        Department selected = deptCombo.getValue();
        if (selected != null) {
            try {
                years = CourseNavigationDAO.getYearsByDepartment(selected.getDeptId());
                yearCombo.getItems().clear();
                yearCombo.getItems().addAll(years);
                yearCombo.setDisable(false);
                if (!years.isEmpty()) {
                    yearCombo.setValue(years.get(0));
                } else {
                    yearCombo.setValue(null);
                    semesterCombo.getItems().clear();
                    semesterCombo.setDisable(true);
                    courseCombo.getItems().clear();
                    courseCombo.setDisable(true);
                    materialsTable.getItems().clear();
                    selectedCourseLabel.setText("No active courses found for this year");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void loadSemesters() {
        Department dept = deptCombo.getValue();
        AcademicYear year = yearCombo.getValue();
        if (dept != null && year != null) {
            try {
                semesters = CourseNavigationDAO.getSemestersByDeptAndYear(dept.getDeptId(), year.getYearId());
                semesterCombo.getItems().clear();
                semesterCombo.getItems().addAll(semesters);
                semesterCombo.setDisable(false);
                if (!semesters.isEmpty()) {
                    semesterCombo.setValue(semesters.get(0));
                } else {
                    semesterCombo.setValue(null);
                    courseCombo.getItems().clear();
                    courseCombo.setDisable(true);
                    materialsTable.getItems().clear();
                    selectedCourseLabel.setText("No semesters found for this selection");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void loadCourses() {
        Department dept = deptCombo.getValue();
        AcademicYear year = yearCombo.getValue();
        Semester semester = semesterCombo.getValue();
        if (dept != null && year != null && semester != null) {
            try {
                courses = CourseNavigationDAO.getCoursesByDeptYearSemester(dept.getDeptId(), year.getYearId(), semester.getSemesterId());
                courseCombo.getItems().clear();
                courseCombo.getItems().addAll(courses);
                courseCombo.setDisable(false);
                if (!courses.isEmpty()) {
                    courseCombo.setValue(courses.get(0));
                } else {
                    courseCombo.setValue(null);
                    materialsTable.getItems().clear();
                    selectedCourseLabel.setText("No courses found for this semester");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void loadMaterials() {
        Course selectedCourse = courseCombo.getValue();
        if (selectedCourse != null) {
            selectedCourseLabel.setText("Materials for: " + selectedCourse.getCourseCode() + " - " + selectedCourse.getCourseName());
            try {
                List<Material> materials = MaterialDAO.getApprovedMaterialsByCourse(selectedCourse.getCourseId());
                originalMaterialsList.setAll(materials);
                materialsTable.setItems(FXCollections.observableArrayList(originalMaterialsList));
                if (searchField != null) {
                    searchField.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void filterMaterials(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            materialsTable.setItems(FXCollections.observableArrayList(originalMaterialsList));
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            ObservableList<Material> filteredList = FXCollections.observableArrayList();
            for (Material material : originalMaterialsList) {
                if ((material.getTitle() != null && material.getTitle().toLowerCase().contains(lowerCaseFilter)) ||
                    (material.getDescription() != null && material.getDescription().toLowerCase().contains(lowerCaseFilter)) ||
                    (material.getMaterialType() != null && material.getMaterialType().toLowerCase().contains(lowerCaseFilter)) ||
                    (material.getUploaderName() != null && material.getUploaderName().toLowerCase().contains(lowerCaseFilter))) {
                    filteredList.add(material);
                }
            }
            materialsTable.setItems(filteredList);
        }
    }
    
    private void downloadMaterial(Material material) {
        if (material.getMaterialType().equals("file") && material.getFilePath() != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(material.getFileName());
            fileChooser.setTitle("Save Material");
            File saveFile = fileChooser.showSaveDialog(view.getScene().getWindow());
            
            if (saveFile != null) {
                downloadProgress.setVisible(true);
                progressLabel.setVisible(true);
                progressLabel.setText("Downloading: " + material.getFileName());
                
                Task<Void> downloadTask = FileDownloadService.downloadFileTask(
                    material.getFilePath(),
                    saveFile.getAbsolutePath(),
                    downloadProgress,
                    () -> {
                        downloadProgress.setVisible(false);
                        progressLabel.setVisible(false);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Download Complete");
                        alert.setHeaderText(null);
                        alert.setContentText("File saved to: " + saveFile.getAbsolutePath());
                        alert.showAndWait();
                        
                        // Increment download count
                        try {
                            MaterialDAO.incrementDownloadCount(material.getMaterialId());
                            loadMaterials(); // Refresh to show updated count
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                );
                
                new Thread(downloadTask).start();
            }
        } else if (material.getMaterialType().equals("link") && material.getLinkUrl() != null) {
            // Open link in default browser
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(material.getLinkUrl()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private VBox comboGroup(String labelText, ComboBox<?> cb) {
        VBox g = new VBox(6);
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-text-fill: #475569; -fx-font-weight: bold; -fx-font-size: 11px;");
        g.getChildren().addAll(lbl, cb);
        return g;
    }
    
    private String typeColor(String type) {
        if (type == null) return "#6366f1";
        switch (type.toLowerCase()) {
            case "pdf":             return "#dc2626";
            case "link":            return "#0284c7";
            case "doc": case "docx":return "#2563eb";
            case "ppt": case "pptx":return "#d97706";
            default:                return "#6366f1";
        }
    }
    
    public BorderPane getView() {
        return view;
    }
}