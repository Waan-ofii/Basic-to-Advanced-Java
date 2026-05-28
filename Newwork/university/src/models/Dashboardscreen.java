package com.university.client.ui;

import com.university.client.network.ServerConnection;
import com.university.shared.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class DashboardScreen {
    private ServerConnection serverConnection;
    private User currentUser;
    private String selectedDepartment = "All";
    private Integer selectedYear = 0;
    
    public DashboardScreen(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
        this.currentUser = serverConnection.getCurrentUser();
    }
    
    public void start(Stage stage) {
        // Header
        Label welcomeLabel = new Label("Welcome, " + currentUser.getName() + "!");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        Label userInfoLabel = new Label(currentUser.getDepartment() + " | Year " + currentUser.getYearLevel());
        userInfoLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        // Navigation buttons
        Button freshmanBtn = createNavButton("Freshman", "#e74c3c");
        Button preEngBtn = createNavButton("Pre-Engineering", "#e67e22");
        Button sweBtn = createNavButton("Software Engineering", "#3498db");
        Button csBtn = createNavButton("Computer Science", "#9b59b6");
        Button bmeBtn = createNavButton("Biomedical Engineering", "#1abc9c");
        Button eceBtn = createNavButton("Electrical Engineering", "#34495e");
        
        // Year selection
        ToggleGroup yearGroup = new ToggleGroup();
        RadioButton year1 = createYearButton("1st Year", yearGroup);
        RadioButton year2 = createYearButton("2nd Year", yearGroup);
        RadioButton year3 = createYearButton("3rd Year", yearGroup);
        RadioButton year4 = createYearButton("4th Year", yearGroup);
        
        yearGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedYear = Integer.parseInt(((RadioButton) newVal).getText().charAt(0) + "");
                refreshMaterials();
            }
        });
        
        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search materials by keyword...");
        searchField.setPrefWidth(400);
        
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        searchBtn.setOnAction(e -> searchMaterials(searchField.getText()));
        
        HBox searchBox = new HBox(10, searchField, searchBtn);
        searchBox.setAlignment(Pos.CENTER);
        
        // Materials area
        ListView<String> materialsListView = new ListView<>();
        materialsListView.setPrefHeight(400);
        
        Button viewMaterialBtn = new Button("View/Download Material");
        viewMaterialBtn.setOnAction(e -> {
            String selected = materialsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // Extract material ID and show material viewer
                showMaterialViewer(selected);
            }
        });
        
        // Upload button (only for admins)
        Button uploadBtn = null;
        if (currentUser.isAdmin()) {
            uploadBtn = new Button("Upload New Material");
            uploadBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
            uploadBtn.setOnAction(e -> showUploadScreen());
        }
        
        // Layout
        GridPane navGrid = new GridPane();
        navGrid.setHgap(10);
        navGrid.setVgap(10);
        navGrid.add(freshmanBtn, 0, 0);
        navGrid.add(preEngBtn, 1, 0);
        navGrid.add(sweBtn, 2, 0);
        navGrid.add(csBtn, 3, 0);
        navGrid.add(bmeBtn, 4, 0);
        navGrid.add(eceBtn, 5, 0);
        
        HBox yearBox = new HBox(15, year1, year2, year3, year4);
        yearBox.setAlignment(Pos.CENTER);
        
        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(20));
        rightPanel.getChildren().addAll(searchBox, materialsListView, viewMaterialBtn);
        if (uploadBtn != null) {
            rightPanel.getChildren().add(uploadBtn);
        }
        
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(createHeader(welcomeLabel, userInfoLabel));
        mainLayout.setLeft(createSidebar(navGrid, yearBox));
        mainLayout.setCenter(rightPanel);
        
        Scene scene = new Scene(mainLayout, 1200, 700);
        stage.setTitle("Material Sharing System - Dashboard");
        stage.setScene(scene);
        stage.show();
        
        // Initial load
        refreshMaterials();
    }
    
    private VBox createHeader(Label welcome, Label userInfo) {
        VBox header = new VBox(5, welcome, userInfo);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");
        welcome.setStyle("-fx-text-fill: white;");
        userInfo.setStyle("-fx-text-fill: #ecf0f1;");
        return header;
    }
    
    private VBox createSidebar(GridPane navGrid, HBox yearBox) {
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #ecf0f1;");
        sidebar.setPrefWidth(300);
        
        Label deptLabel = new Label("Select Department");
        deptLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label yearLabel = new Label("Select Year Level");
        yearLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        sidebar.getChildren().addAll(deptLabel, navGrid, new Separator(), yearLabel, yearBox);
        return sidebar;
    }
    
    private Button createNavButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-min-width: 150px;");
        btn.setOnAction(e -> {
            selectedDepartment = text;
            refreshMaterials();
        });
        return btn;
    }
    
    private RadioButton createYearButton(String text,