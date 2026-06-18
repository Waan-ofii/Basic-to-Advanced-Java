package views;

import application.CampusShareApp;
import models.*;
import dao.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.collections.*;
import javafx.beans.property.*;
import java.util.*;

public class AdminDashboardView {
    private CampusShareApp app;
    private User currentUser;
    private BorderPane view;

    // Structure elements
    private BorderPane contentArea;
    private StackPane activeTabContent;
    private ScrollPane pendingScrollPane;
    private ScrollPane materialsScrollPane;
    private ScrollPane coursesScrollPane;
    private ScrollPane usersScrollPane;
    private ScrollPane reportsScrollPane;

    // Header controls
    private Label welcomeLabel;
    private Label welcomeSub;
    private StackPane searchContainer;
    private TextField searchField;

    // Sidebar buttons
    private Button btnPending;
    private Button btnAllMaterials;
    private Button btnCourses;
    private Button btnUsers;
    private Button btnReports;
    private String activeTabName = "Pending Approvals";

    // Views / Tables
    private TableView<Material> pendingTable;
    private TextArea reviewCommentArea;
    private TableView<Material> allMaterialsTable;
    private TableView<Course> coursesTable;
    private TableView<User> usersTable;
    private TableView<Map<String, Object>> popTable;
    private TableView<Map<String, Object>> matTable;

    public AdminDashboardView(CampusShareApp app, User user) {
        this.app = app;
        this.currentUser = user;
        createView();
    }

    private void createView() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: #faf9fb;");

        // Build ScrollPanes for each tab
        pendingScrollPane = buildPendingTab();
        materialsScrollPane = buildMaterialsTab();
        coursesScrollPane = buildCoursesTab();
        usersScrollPane = buildUsersTab();
        reportsScrollPane = buildReportsTab();

        activeTabContent = new StackPane(pendingScrollPane);

        // Assemble Content Area
        contentArea = new BorderPane();
        contentArea.setTop(buildTopBar());
        contentArea.setCenter(activeTabContent);

        view.setLeft(buildLeftPanel());
        view.setCenter(contentArea);
    }

    // ── Left Panel (Sidebar) ──────────────────────────────────────────────────
    private VBox buildLeftPanel() {
        VBox sidebar = new VBox(24);
        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(250);
        sidebar.setPadding(new Insets(24, 16, 24, 16));
        sidebar.setStyle("-fx-background-color: #211326;"); // Deep plum

        // 1. Logo
        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Label logoIcon = new Label("🎓");
        logoIcon.setFont(Font.font(20));
        logoIcon.setTextFill(Color.WHITE);
        Label logoText = new Label("CampusShare");
        logoText.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 18));
        logoText.setTextFill(Color.WHITE);
        logoBox.getChildren().addAll(logoIcon, logoText);

        // 2. Profile Card
        HBox profileCard = new HBox(12);
        profileCard.setAlignment(Pos.CENTER_LEFT);
        profileCard.setPadding(new Insets(12));
        profileCard.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.06);" +
            "-fx-background-radius: 12;"
        );

        String initials = getInitials(currentUser.getFullName());
        StackPane avatar = new StackPane();
        avatar.setPrefSize(40, 40);
        avatar.setMaxSize(40, 40);
        avatar.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #db2777, #7c3aed);" +
            "-fx-background-radius: 20;"
        );
        Label avatarText = new Label(initials);
        avatarText.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        avatar.getChildren().add(avatarText);

        VBox userDetails = new VBox(2);
        Label userName = new Label(currentUser.getFullName());
        userName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        userName.setTextFill(Color.WHITE);

        Label adminBadge = new Label("ADMIN");
        adminBadge.setStyle(
            "-fx-background-color: rgba(239, 68, 68, 0.2);" +
            "-fx-text-fill: #fca5a5;" +
            "-fx-font-size: 9px; -fx-font-weight: bold;" +
            "-fx-padding: 1 5 1 5; -fx-background-radius: 8;"
        );
        userDetails.getChildren().addAll(userName, adminBadge);
        profileCard.getChildren().addAll(avatar, userDetails);

        // 3. Menu Buttons
        VBox menuBox = new VBox(8);
        VBox.setVgrow(menuBox, Priority.ALWAYS);

        btnPending = new Button();
        btnAllMaterials = new Button();
        btnCourses = new Button();
        btnUsers = new Button();
        btnReports = new Button();

        btnPending.setOnAction(e -> switchTab("Pending Approvals"));
        btnAllMaterials.setOnAction(e -> switchTab("All Materials"));
        btnCourses.setOnAction(e -> switchTab("Manage Courses"));
        btnUsers.setOnAction(e -> switchTab("Manage Users"));
        btnReports.setOnAction(e -> switchTab("Reports"));

        menuBox.getChildren().addAll(btnPending, btnAllMaterials, btnCourses, btnUsers, btnReports);

        styleSidebarButton(btnPending, "⏳  Pending Approvals", true);
        styleSidebarButton(btnAllMaterials, "📋  All Materials", false);
        styleSidebarButton(btnCourses, "🏛  Manage Courses", false);
        styleSidebarButton(btnUsers, "👥  Manage Users", false);
        styleSidebarButton(btnReports, "📊  Reports", false);

        // 4. Sign Out Button
        Button btnSignOut = new Button("🚪  Sign Out");
        btnSignOut.setMaxWidth(Double.MAX_VALUE);
        btnSignOut.setCursor(javafx.scene.Cursor.HAND);
        btnSignOut.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-border-color: rgba(255,255,255,0.2);" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 10 18 10 18;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;"
        );
        btnSignOut.setOnMouseEntered(e -> btnSignOut.setStyle(
            "-fx-background-color: rgba(255,255,255,0.08);" +
            "-fx-text-fill: white;" +
            "-fx-border-color: rgba(255,255,255,0.4);" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 10 18 10 18;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;"
        ));
        btnSignOut.setOnMouseExited(e -> btnSignOut.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-border-color: rgba(255,255,255,0.2);" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 10 18 10 18;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;"
        ));
        btnSignOut.setOnAction(e -> app.logout());

        sidebar.getChildren().addAll(logoBox, profileCard, menuBox, btnSignOut);
        return sidebar;
    }

    private void styleSidebarButton(Button btn, String text, boolean active) {
        btn.setText(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setCursor(javafx.scene.Cursor.HAND);
        if (active) {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #db2777, #ec4899);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 10 16 10 16;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;"
            );
            btn.setOnMouseEntered(null);
            btn.setOnMouseExited(null);
        } else {
            btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #d8b4fe;" +
                "-fx-padding: 10 16 10 16;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;"
            );
            btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: rgba(255,255,255,0.06);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 10 16 10 16;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;"
            ));
            btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #d8b4fe;" +
                "-fx-padding: 10 16 10 16;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;"
            ));
        }
    }

    // ── Tab Switcher ──────────────────────────────────────────────────────────
    private void switchTab(String tabName) {
        this.activeTabName = tabName;

        styleSidebarButton(btnPending, "⏳  Pending Approvals", "Pending Approvals".equals(tabName));
        styleSidebarButton(btnAllMaterials, "📋  All Materials", "All Materials".equals(tabName));
        styleSidebarButton(btnCourses, "🏛  Manage Courses", "Manage Courses".equals(tabName));
        styleSidebarButton(btnUsers, "👥  Manage Users", "Manage Users".equals(tabName));
        styleSidebarButton(btnReports, "📊  Reports", "Reports".equals(tabName));

        activeTabContent.getChildren().clear();

        // Manage search visibility (Only hide for Reports tab)
        boolean showSearch = !"Reports".equals(tabName);
        searchContainer.setVisible(showSearch);
        searchContainer.setManaged(showSearch);
        searchField.clear();

        if ("Pending Approvals".equals(tabName)) {
            welcomeLabel.setText("Pending Approvals ⏳");
            welcomeSub.setText("Review and moderate uploaded study materials.");
            loadPendingMaterials();
            activeTabContent.getChildren().add(pendingScrollPane);
        } else if ("All Materials".equals(tabName)) {
            welcomeLabel.setText("All Materials 📋");
            welcomeSub.setText("Overview of all approved, pending and rejected content.");
            loadAllMaterials(allMaterialsTable, "All");
            activeTabContent.getChildren().add(materialsScrollPane);
        } else if ("Manage Courses".equals(tabName)) {
            welcomeLabel.setText("Manage Courses 🏛");
            welcomeSub.setText("Add, deactivate, or assign instructors to courses.");
            loadCoursesData(coursesTable);
            activeTabContent.getChildren().add(coursesScrollPane);
        } else if ("Manage Users".equals(tabName)) {
            welcomeLabel.setText("Manage Users 👥");
            welcomeSub.setText("Activate or suspend students and teachers.");
            loadUsersData(usersTable);
            activeTabContent.getChildren().add(usersScrollPane);
        } else if ("Reports".equals(tabName)) {
            welcomeLabel.setText("System Reports 📊");
            welcomeSub.setText("Overview of platform analytics and most popular courses.");
            refreshReportsData();
            activeTabContent.getChildren().add(reportsScrollPane);
        }
    }

    // ── Top Navigation Bar ───────────────────────────────────────────────────
    private HBox buildTopBar() {
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(20, 30, 10, 30));
        topBar.setStyle("-fx-background-color: #faf9fb;");

        VBox welcomeBox = new VBox(4);
        welcomeLabel = new Label("Pending Approvals ⏳");
        welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        welcomeLabel.setTextFill(Color.web("#1e1b4b"));

        welcomeSub = new Label("Review and moderate uploaded study materials.");
        welcomeSub.setFont(Font.font("Segoe UI", 12));
        welcomeSub.setTextFill(Color.web("#64748b"));
        welcomeBox.getChildren().addAll(welcomeLabel, welcomeSub);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        // Search container
        searchContainer = new StackPane();
        searchField = new TextField();
        searchField.setPromptText("Search contents...");
        searchField.setPrefWidth(260);
        searchField.setStyle(
            "-fx-background-color: #f1f5f9;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 16 8 36;" +
            "-fx-font-size: 13px;"
        );

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterData(newVal);
        });

        Label searchIcon = new Label("🔍");
        searchIcon.setStyle("-fx-text-fill: #94a3b8; -fx-padding: 0 0 0 12;");
        StackPane.setAlignment(searchIcon, Pos.CENTER_LEFT);
        searchContainer.getChildren().addAll(searchField, searchIcon);

        // Avatar circle
        StackPane avatarCircle = new StackPane();
        avatarCircle.setPrefSize(36, 36);
        avatarCircle.setMaxSize(36, 36);
        avatarCircle.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #db2777, #7c3aed);" +
            "-fx-background-radius: 20;"
        );
        Label initialsLabel = new Label(getInitials(currentUser.getFullName()));
        initialsLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
        avatarCircle.getChildren().add(initialsLabel);

        HBox topActions = new HBox(12, searchContainer, avatarCircle);
        topActions.setAlignment(Pos.CENTER_RIGHT);

        topBar.getChildren().addAll(welcomeBox, topSpacer, topActions);
        return topBar;
    }

    // ── Tab: Pending Approvals Pane ───────────────────────────────────────────
    private ScrollPane buildPendingTab() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(10, 30, 24, 30));
        contentBox.setStyle("-fx-background-color: #faf9fb;");

        VBox cardContainer = new VBox(16);
        cardContainer.setPadding(new Insets(20));
        cardContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #f1f5f9;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.01), 6, 0, 0, 1);"
        );

        pendingTable = new TableView<>();
        pendingTable.setPlaceholder(new Label("No pending approvals found."));
        pendingTable.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        TableColumn<Material, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        titleCol.setPrefWidth(200);

        TableColumn<Material, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
        courseCol.setPrefWidth(180);

        TableColumn<Material, String> uploaderCol = new TableColumn<>("Uploaded By");
        uploaderCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUploaderName()));
        uploaderCol.setPrefWidth(140);

        TableColumn<Material, String> dateCol = new TableColumn<>("Upload Date");
        dateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUploadDate() != null
            ? cellData.getValue().getUploadDate().toString().substring(0, 16) : "N/A"));
        dateCol.setPrefWidth(140);

        TableColumn<Material, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(200);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final HBox buttons = new HBox(8);
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            {
                approveBtn.setCursor(javafx.scene.Cursor.HAND);
                approveBtn.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 4 12 4 12;");
                rejectBtn.setCursor(javafx.scene.Cursor.HAND);
                rejectBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 4 12 4 12;");
                buttons.getChildren().addAll(approveBtn, rejectBtn);
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Material material = getTableView().getItems().get(getIndex());
                    approveBtn.setOnAction(e -> approveMaterial(material));
                    rejectBtn.setOnAction(e -> rejectMaterial(material));
                    setGraphic(buttons);
                }
            }
        });

        pendingTable.getColumns().addAll(titleCol, courseCol, uploaderCol, dateCol, actionCol);

        // Review Comment Box card
        VBox reviewBox = new VBox(10);
        reviewBox.setPadding(new Insets(16));
        reviewBox.setStyle(
            "-fx-background-color: #f8fafc;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;"
        );

        Label reviewLabel = new Label("Review Comment (Required for rejection):");
        reviewLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569; -fx-font-size: 13px;");
        reviewCommentArea = new TextArea();
        reviewCommentArea.setPromptText("Enter reason for rejection...");
        reviewCommentArea.setPrefHeight(60);
        reviewCommentArea.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 8; -fx-font-size: 13px;");

        reviewBox.getChildren().addAll(reviewLabel, reviewCommentArea);

        cardContainer.getChildren().addAll(pendingTable, reviewBox);
        contentBox.getChildren().add(cardContainer);
        scrollPane.setContent(contentBox);

        loadPendingMaterials();
        return scrollPane;
    }

    // ── Tab: All Materials Pane ───────────────────────────────────────────────
    private ScrollPane buildMaterialsTab() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(10, 30, 24, 30));
        contentBox.setStyle("-fx-background-color: #faf9fb;");

        VBox cardContainer = new VBox(16);
        cardContainer.setPadding(new Insets(20));
        cardContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #f1f5f9;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.01), 6, 0, 0, 1);"
        );

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Approved", "Rejected");
        statusFilter.setValue("All");
        statusFilter.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-font-size: 12px;");

        allMaterialsTable = new TableView<>();
        allMaterialsTable.setPlaceholder(new Label("No materials found"));
        allMaterialsTable.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        TableColumn<Material, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        titleCol.setPrefWidth(180);

        TableColumn<Material, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
        courseCol.setPrefWidth(150);

        TableColumn<Material, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaterialType().toUpperCase()));
        typeCol.setPrefWidth(70);

        TableColumn<Material, String> uploaderCol = new TableColumn<>("Uploaded By");
        uploaderCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUploaderName()));
        uploaderCol.setPrefWidth(120);

        TableColumn<Material, String> dateCol = new TableColumn<>("Upload Date");
        dateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUploadDate() != null
            ? cellData.getValue().getUploadDate().toString().substring(0, 16) : "N/A"));
        dateCol.setPrefWidth(120);

        TableColumn<Material, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        statusCol.setPrefWidth(90);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item.toUpperCase());
                    badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 9));
                    switch (item.toLowerCase()) {
                        case "approved":
                            badge.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #166534; -fx-padding: 3 8 3 8; -fx-background-radius: 8;");
                            break;
                        case "pending":
                            badge.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #92400e; -fx-padding: 3 8 3 8; -fx-background-radius: 8;");
                            break;
                        case "rejected":
                            badge.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-padding: 3 8 3 8; -fx-background-radius: 8;");
                            break;
                    }
                    setGraphic(badge);
                }
            }
        });

        TableColumn<Material, Integer> downloadsCol = new TableColumn<>("Downloads");
        downloadsCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDownloadCount()).asObject());
        downloadsCol.setPrefWidth(80);

        allMaterialsTable.getColumns().addAll(titleCol, courseCol, typeCol, uploaderCol, dateCol, statusCol, downloadsCol);

        statusFilter.setOnAction(e -> loadAllMaterials(allMaterialsTable, statusFilter.getValue()));

        cardContainer.getChildren().addAll(statusFilter, allMaterialsTable);
        contentBox.getChildren().add(cardContainer);
        scrollPane.setContent(contentBox);
        return scrollPane;
    }

    // ── Tab: Manage Courses Pane ──────────────────────────────────────────────
    private ScrollPane buildCoursesTab() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(10, 30, 24, 30));
        contentBox.setStyle("-fx-background-color: #faf9fb;");

        VBox cardContainer = new VBox(16);
        cardContainer.setPadding(new Insets(20));
        cardContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #f1f5f9;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.01), 6, 0, 0, 1);"
        );

        HBox toolsRow = new HBox(12);
        toolsRow.setAlignment(Pos.CENTER_LEFT);

        Button addCourseBtn = new Button("➕ Add New Course");
        addCourseBtn.setCursor(javafx.scene.Cursor.HAND);
        addCourseBtn.setStyle(
            "-fx-background-color: #db2777;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 20 8 20;"
        );
        addCourseBtn.setOnMouseEntered(e -> addCourseBtn.setStyle(
            "-fx-background-color: #be185d;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 20 8 20;"
        ));
        addCourseBtn.setOnMouseExited(e -> addCourseBtn.setStyle(
            "-fx-background-color: #db2777;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 20 8 20;"
        ));

        toolsRow.getChildren().add(addCourseBtn);

        coursesTable = new TableView<>();
        coursesTable.setPlaceholder(new Label("No courses available"));
        coursesTable.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        TableColumn<Course, String> codeCol = new TableColumn<>("Course Code");
        codeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseCode()));
        codeCol.setPrefWidth(100);

        TableColumn<Course, String> nameCol = new TableColumn<>("Course Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
        nameCol.setPrefWidth(220);

        TableColumn<Course, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDeptName()));
        deptCol.setPrefWidth(140);

        TableColumn<Course, String> teacherCol = new TableColumn<>("Teacher");
        teacherCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTeacherName() != null ? cellData.getValue().getTeacherName() : "Unassigned"));
        teacherCol.setPrefWidth(140);

        TableColumn<Course, Integer> creditsCol = new TableColumn<>("Credits");
        creditsCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCredits()).asObject());
        creditsCol.setPrefWidth(70);

        TableColumn<Course, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isActive() ? "Active" : "Inactive"));
        statusCol.setPrefWidth(90);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item.toUpperCase());
                    badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 9));
                    if ("active".equalsIgnoreCase(item)) {
                        badge.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #166534; -fx-padding: 3 8 3 8; -fx-background-radius: 8;");
                    } else {
                        badge.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-padding: 3 8 3 8; -fx-background-radius: 8;");
                    }
                    setGraphic(badge);
                }
            }
        });

        TableColumn<Course, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(120);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button toggleBtn = new Button();
            {
                toggleBtn.setCursor(javafx.scene.Cursor.HAND);
                toggleBtn.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 12; -fx-padding: 4 12 4 12;");
                toggleBtn.setOnAction(e -> {
                    Course c = getTableView().getItems().get(getIndex());
                    toggleCourseStatus(c, coursesTable);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Course c = getTableView().getItems().get(getIndex());
                    toggleBtn.setText(c.isActive() ? "Deactivate" : "Activate");
                    toggleBtn.setStyle(c.isActive()
                        ? "-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 4 12 4 12;"
                        : "-fx-background-color: #22c55e; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 4 12 4 12;");
                    setGraphic(toggleBtn);
                }
            }
        });

        coursesTable.getColumns().addAll(codeCol, nameCol, deptCol, teacherCol, creditsCol, statusCol, actionCol);

        addCourseBtn.setOnAction(e -> showAddCourseDialog(coursesTable));

        cardContainer.getChildren().addAll(toolsRow, coursesTable);
        contentBox.getChildren().add(cardContainer);
        scrollPane.setContent(contentBox);
        return scrollPane;
    }

    // ── Tab: Manage Users Pane ────────────────────────────────────────────────
    private ScrollPane buildUsersTab() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(10, 30, 24, 30));
        contentBox.setStyle("-fx-background-color: #faf9fb;");

        VBox cardContainer = new VBox(16);
        cardContainer.setPadding(new Insets(20));
        cardContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #f1f5f9;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.01), 6, 0, 0, 1);"
        );

        usersTable = new TableView<>();
        usersTable.setPlaceholder(new Label("No users registered"));
        usersTable.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        TableColumn<User, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        userCol.setPrefWidth(120);

        TableColumn<User, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));
        nameCol.setPrefWidth(140);

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        emailCol.setPrefWidth(160);

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().toUpperCase()));
        roleCol.setPrefWidth(90);

        TableColumn<User, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDeptName() != null ? cellData.getValue().getDeptName() : "N/A"));
        deptCol.setPrefWidth(140);

        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isActive() ? "Active" : "Inactive"));
        statusCol.setPrefWidth(90);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item.toUpperCase());
                    badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 9));
                    if ("active".equalsIgnoreCase(item)) {
                        badge.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #166534; -fx-padding: 3 8 3 8; -fx-background-radius: 8;");
                    } else {
                        badge.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-padding: 3 8 3 8; -fx-background-radius: 8;");
                    }
                    setGraphic(badge);
                }
            }
        });

        TableColumn<User, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(120);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button toggleBtn = new Button();
            {
                toggleBtn.setCursor(javafx.scene.Cursor.HAND);
                toggleBtn.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 12; -fx-padding: 4 12 4 12;");
                toggleBtn.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    toggleUserStatus(u, usersTable);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User u = getTableView().getItems().get(getIndex());
                    toggleBtn.setText(u.isActive() ? "Deactivate" : "Activate");
                    toggleBtn.setStyle(u.isActive()
                        ? "-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 4 12 4 12;"
                        : "-fx-background-color: #22c55e; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 4 12 4 12;");
                    setGraphic(toggleBtn);
                }
            }
        });

        usersTable.getColumns().addAll(userCol, nameCol, emailCol, roleCol, deptCol, statusCol, actionCol);

        cardContainer.getChildren().add(usersTable);
        contentBox.getChildren().add(cardContainer);
        scrollPane.setContent(contentBox);
        return scrollPane;
    }

    // ── Tab: Reports Pane ─────────────────────────────────────────────────────
    private ScrollPane buildReportsTab() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(10, 30, 24, 30));
        contentBox.setStyle("-fx-background-color: #faf9fb;");

        HBox tablesBox = new HBox(20);
        HBox.setHgrow(tablesBox, Priority.ALWAYS);

        // Report 1: Popular Courses Card
        VBox popularCoursesBox = new VBox(12);
        popularCoursesBox.setPadding(new Insets(16));
        HBox.setHgrow(popularCoursesBox, Priority.ALWAYS);
        popularCoursesBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #f1f5f9;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.01), 6, 0, 0, 1);"
        );

        Label popTitle = new Label("📊 Popular Courses (by Downloads)");
        popTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        popTitle.setTextFill(Color.web("#1e293b"));

        popTable = new TableView<>();
        popTable.setPlaceholder(new Label("No statistics available"));
        popTable.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        TableColumn<Map<String, Object>, String> cCodeCol = new TableColumn<>("Course Code");
        cCodeCol.setCellValueFactory(cellData -> new SimpleStringProperty((String) cellData.getValue().get("course_code")));
        cCodeCol.setPrefWidth(90);

        TableColumn<Map<String, Object>, String> cNameCol = new TableColumn<>("Course Name");
        cNameCol.setCellValueFactory(cellData -> new SimpleStringProperty((String) cellData.getValue().get("course_name")));
        cNameCol.setPrefWidth(160);

        TableColumn<Map<String, Object>, Integer> cCountCol = new TableColumn<>("Materials");
        cCountCol.setCellValueFactory(cellData -> new SimpleIntegerProperty((Integer) cellData.getValue().get("material_count")).asObject());
        cCountCol.setPrefWidth(70);

        TableColumn<Map<String, Object>, Integer> cDownloadsCol = new TableColumn<>("Total Downloads");
        cDownloadsCol.setCellValueFactory(cellData -> new SimpleIntegerProperty((Integer) cellData.getValue().get("total_downloads")).asObject());
        cDownloadsCol.setPrefWidth(100);

        popTable.getColumns().addAll(cCodeCol, cNameCol, cCountCol, cDownloadsCol);
        popularCoursesBox.getChildren().addAll(popTitle, popTable);

        // Report 2: Most Downloaded Materials Card
        VBox popularMaterialsBox = new VBox(12);
        popularMaterialsBox.setPadding(new Insets(16));
        HBox.setHgrow(popularMaterialsBox, Priority.ALWAYS);
        popularMaterialsBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #f1f5f9;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.01), 6, 0, 0, 1);"
        );

        Label matTitle = new Label("💾 Most Downloaded Study Materials");
        matTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        matTitle.setTextFill(Color.web("#1e293b"));

        matTable = new TableView<>();
        matTable.setPlaceholder(new Label("No statistics available"));
        matTable.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        TableColumn<Map<String, Object>, String> mTitleCol = new TableColumn<>("Material Title");
        mTitleCol.setCellValueFactory(cellData -> new SimpleStringProperty((String) cellData.getValue().get("title")));
        mTitleCol.setPrefWidth(160);

        TableColumn<Map<String, Object>, String> mCourseCol = new TableColumn<>("Course Name");
        mCourseCol.setCellValueFactory(cellData -> new SimpleStringProperty((String) cellData.getValue().get("course_name")));
        mCourseCol.setPrefWidth(130);

        TableColumn<Map<String, Object>, String> mUploaderCol = new TableColumn<>("Uploader");
        mUploaderCol.setCellValueFactory(cellData -> new SimpleStringProperty((String) cellData.getValue().get("uploader_name")));
        mUploaderCol.setPrefWidth(110);

        TableColumn<Map<String, Object>, Integer> mDownloadsCol = new TableColumn<>("Downloads");
        mDownloadsCol.setCellValueFactory(cellData -> new SimpleIntegerProperty((Integer) cellData.getValue().get("download_count")).asObject());
        mDownloadsCol.setPrefWidth(80);

        matTable.getColumns().addAll(mTitleCol, mCourseCol, mUploaderCol, mDownloadsCol);
        popularMaterialsBox.getChildren().addAll(matTitle, matTable);

        tablesBox.getChildren().addAll(popularCoursesBox, popularMaterialsBox);
        contentBox.getChildren().addAll(tablesBox);
        scrollPane.setContent(contentBox);
        return scrollPane;
    }

    // ── Moderation Logic ──────────────────────────────────────────────────────
    private void loadPendingMaterials() {
        try {
            List<Material> pending = MaterialDAO.getPendingMaterials();
            pendingTable.getItems().setAll(pending);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void approveMaterial(Material material) {
        try {
            if (MaterialDAO.approveMaterial(material.getMaterialId(), currentUser.getUserId(), "Approved")) {
                showAlert("Approved", "Material \"" + material.getTitle() + "\" has been approved.");
                loadPendingMaterials();
                reviewCommentArea.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rejectMaterial(Material material) {
        String reason = reviewCommentArea.getText().trim();
        if (reason.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please provide a reason for rejection.", ButtonType.OK).showAndWait();
            return;
        }

        try {
            if (MaterialDAO.rejectMaterial(material.getMaterialId(), currentUser.getUserId(), reason)) {
                showAlert("Rejected", "Material \"" + material.getTitle() + "\" has been rejected.");
                loadPendingMaterials();
                reviewCommentArea.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAllMaterials(TableView<Material> table, String status) {
        try {
            List<Material> list = "All".equals(status)
                ? MaterialDAO.getAllMaterials()
                : MaterialDAO.getAllMaterialsByStatus(status.toLowerCase());
            table.getItems().setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Courses Management Logic ──────────────────────────────────────────────
    private void loadCoursesData(TableView<Course> table) {
        try {
            List<Course> list = CourseDAO.getAllCourses();
            table.getItems().setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleCourseStatus(Course course, TableView<Course> table) {
        try {
            boolean nextStatus = !course.isActive();
            if (CourseDAO.toggleCourseStatus(course.getCourseId(), nextStatus)) {
                course.setActive(nextStatus);
                table.refresh();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAddCourseDialog(TableView<Course> table) {
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Add New Course");
        dialog.setHeaderText("Enter details of the new course");

        DialogPane dialogPane = dialog.getDialogPane();
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        dialogPane.setStyle("-fx-background-color: white;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 20, 20, 20));

        TextField codeField = new TextField();
        codeField.setPromptText("e.g. CS101");
        codeField.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 8;");
        
        TextField nameField = new TextField();
        nameField.setPromptText("Course Title");
        nameField.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 8;");
        
        TextField descField = new TextField();
        descField.setPromptText("Course Description");
        descField.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 8;");
        
        TextField creditsField = new TextField("3");
        creditsField.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 8;");

        ComboBox<Department> deptCombo = new ComboBox<>();
        deptCombo.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8;");
        
        ComboBox<AcademicYear> yearCombo = new ComboBox<>();
        yearCombo.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8;");
        
        ComboBox<Semester> semCombo = new ComboBox<>();
        semCombo.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8;");
        
        ComboBox<User> teacherCombo = new ComboBox<>();
        teacherCombo.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8;");

        try {
            deptCombo.getItems().setAll(CourseNavigationDAO.getAllDepartments());
            yearCombo.getItems().setAll(CourseNavigationDAO.getAllAcademicYears());
            semCombo.getItems().setAll(CourseNavigationDAO.getAllSemesters());

            User unassignedTeacher = new User();
            unassignedTeacher.setUserId(-1);
            unassignedTeacher.setFullName("Unassigned");
            teacherCombo.getItems().add(unassignedTeacher);
            teacherCombo.getItems().addAll(UserDAO.getTeachers());
            teacherCombo.setValue(unassignedTeacher);
        } catch (Exception e) {
            e.printStackTrace();
        }

        grid.add(new Label("Course Code:"), 0, 0);   grid.add(codeField, 1, 0);
        grid.add(new Label("Course Name:"), 0, 1);   grid.add(nameField, 1, 1);
        grid.add(new Label("Description:"), 0, 2);   grid.add(descField, 1, 2);
        grid.add(new Label("Credits:"), 0, 3);       grid.add(creditsField, 1, 3);
        grid.add(new Label("Department:"), 0, 4);    grid.add(deptCombo, 1, 4);
        grid.add(new Label("Academic Year:"), 0, 5); grid.add(yearCombo, 1, 5);
        grid.add(new Label("Semester:"), 0, 6);      grid.add(semCombo, 1, 6);
        grid.add(new Label("Teacher:"), 0, 7);       grid.add(teacherCombo, 1, 7);

        dialogPane.setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Course newCourse = new Course();
                newCourse.setCourseCode(codeField.getText().trim());
                newCourse.setCourseName(nameField.getText().trim());
                newCourse.setDescription(descField.getText().trim());
                try {
                    newCourse.setCredits(Integer.parseInt(creditsField.getText().trim()));
                } catch (Exception e) {
                    newCourse.setCredits(3);
                }
                if (deptCombo.getValue() != null) newCourse.setDeptId(deptCombo.getValue().getDeptId());
                if (yearCombo.getValue() != null) newCourse.setYearId(yearCombo.getValue().getYearId());
                if (semCombo.getValue() != null) newCourse.setSemesterId(semCombo.getValue().getSemesterId());
                if (teacherCombo.getValue() != null) newCourse.setTeacherId(teacherCombo.getValue().getUserId());
                return newCourse;
            }
            return null;
        });

        Optional<Course> result = dialog.showAndWait();
        result.ifPresent(course -> {
            try {
                if (CourseDAO.createCourse(course)) {
                    loadCoursesData(table);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    // ── Users Management Logic ────────────────────────────────────────────────
    private void loadUsersData(TableView<User> table) {
        try {
            List<User> list = UserDAO.getAllUsers();
            table.getItems().setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleUserStatus(User user, TableView<User> table) {
        try {
            boolean nextStatus = !user.isActive();
            if (UserDAO.toggleUserStatus(user.getUserId(), nextStatus)) {
                user.setActive(nextStatus);
                table.refresh();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshReportsData() {
        try {
            popTable.getItems().setAll(MaterialDAO.getPopularCoursesReport());
            matTable.getItems().setAll(MaterialDAO.getMostDownloadedMaterialsReport());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Filtering Logic ───────────────────────────────────────────────────────
    private void filterData(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            if ("Pending Approvals".equals(activeTabName)) loadPendingMaterials();
            else if ("All Materials".equals(activeTabName)) loadAllMaterials(allMaterialsTable, "All");
            else if ("Manage Courses".equals(activeTabName)) loadCoursesData(coursesTable);
            else if ("Manage Users".equals(activeTabName)) loadUsersData(usersTable);
            return;
        }

        String lower = searchText.toLowerCase();

        if ("Pending Approvals".equals(activeTabName)) {
            try {
                List<Material> list = MaterialDAO.getPendingMaterials();
                List<Material> filtered = new ArrayList<>();
                for (Material m : list) {
                    if ((m.getTitle() != null && m.getTitle().toLowerCase().contains(lower)) ||
                        (m.getCourseName() != null && m.getCourseName().toLowerCase().contains(lower)) ||
                        (m.getUploaderName() != null && m.getUploaderName().toLowerCase().contains(lower))) {
                        filtered.add(m);
                    }
                }
                pendingTable.getItems().setAll(filtered);
            } catch (Exception e) { e.printStackTrace(); }

        } else if ("All Materials".equals(activeTabName)) {
            try {
                List<Material> list = MaterialDAO.getAllMaterials();
                List<Material> filtered = new ArrayList<>();
                for (Material m : list) {
                    if ((m.getTitle() != null && m.getTitle().toLowerCase().contains(lower)) ||
                        (m.getCourseName() != null && m.getCourseName().toLowerCase().contains(lower)) ||
                        (m.getUploaderName() != null && m.getUploaderName().toLowerCase().contains(lower))) {
                        filtered.add(m);
                    }
                }
                allMaterialsTable.getItems().setAll(filtered);
            } catch (Exception e) { e.printStackTrace(); }

        } else if ("Manage Courses".equals(activeTabName)) {
            try {
                List<Course> list = CourseDAO.getAllCourses();
                List<Course> filtered = new ArrayList<>();
                for (Course c : list) {
                    if ((c.getCourseName() != null && c.getCourseName().toLowerCase().contains(lower)) ||
                        (c.getCourseCode() != null && c.getCourseCode().toLowerCase().contains(lower)) ||
                        (c.getDeptName() != null && c.getDeptName().toLowerCase().contains(lower))) {
                        filtered.add(c);
                    }
                }
                coursesTable.getItems().setAll(filtered);
            } catch (Exception e) { e.printStackTrace(); }

        } else if ("Manage Users".equals(activeTabName)) {
            try {
                List<User> list = UserDAO.getAllUsers();
                List<User> filtered = new ArrayList<>();
                for (User u : list) {
                    if ((u.getUsername() != null && u.getUsername().toLowerCase().contains(lower)) ||
                        (u.getFullName() != null && u.getFullName().toLowerCase().contains(lower)) ||
                        (u.getEmail() != null && u.getEmail().toLowerCase().contains(lower))) {
                        filtered.add(u);
                    }
                }
                usersTable.getItems().setAll(filtered);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    // ── Helper methods ────────────────────────────────────────────────────────
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.isBlank()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    public BorderPane getView() {
        return view;
    }
}