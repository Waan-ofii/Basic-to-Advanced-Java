package views;

import application.CampusShareApp;
import models.*;
import dao.*;
import services.FileDownloadService;
import services.NotificationClient;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.collections.*;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class StudentDashboardView {
    private CampusShareApp app;
    private User currentUser;
    private BorderPane view;

    // Structure elements
    private BorderPane contentArea;
    private StackPane activeTabContent;
    private StackPane dashboardTabPane;
    private ScrollPane coursesGridScrollPane;
    private ScrollPane settingsScrollPane;
    
    // Header controls
    private Label welcomeLabel;
    private Label welcomeSub;
    private StackPane searchContainer;
    private TextField searchField;
    private Label unreadBadge;

    // Sidebar buttons
    private Button btnDashboard;
    private Button btnCourses;
    private Button btnLibrary;
    private Button btnSettings;
    private String activeTabName = "Dashboard";

    // Dashboard widgets
    private ScrollPane centerScrollPane;
    private HBox coursesCarouselBox;
    private VBox materialsVBox;
    private Label continueMsgLabel;
    private Button btnResume;
    private Label downloadsCountLabel;

    // Data lists
    private ObservableList<Material> originalMaterialsList = FXCollections.observableArrayList();
    private List<Course> enrolledCoursesList = new ArrayList<>();
    private Course selectedCourseFilter = null;
    private Material lastDownloadedMaterial = null;

    private ListView<Notification> notificationList;

    // Download progress
    private ProgressBar downloadProgress;
    private Label progressLabel;

    // Socket client
    private NotificationClient notificationClient;
    private ContextMenu notificationPopup;

    // ─────────────────────────────────────────────────────────────────────────
    public StudentDashboardView(CampusShareApp app, User user) {
        this.app = app;
        this.currentUser = user;
        createView();
        loadData();
        setupNotificationClient();
    }

    // ─── Layout ──────────────────────────────────────────────────────────────
    private void createView() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: #faf9fb;");

        buildNotificationDropdown();
        
        // Setup central tab panels
        dashboardTabPane = buildDashboardTabPane();
        
        coursesGridScrollPane = new ScrollPane();
        coursesGridScrollPane.setFitToWidth(true);
        coursesGridScrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");
        
        settingsScrollPane = new ScrollPane();
        settingsScrollPane.setFitToWidth(true);
        settingsScrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");
        
        activeTabContent = new StackPane(dashboardTabPane);
        
        // Assemble Content Area (Header + Active Tab Content)
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
        String dept = currentUser.getDeptName() != null ? currentUser.getDeptName() : "Student";
        Label userDept = new Label(dept);
        userDept.setFont(Font.font("Segoe UI", 11));
        userDept.setTextFill(Color.web("#a78bfa"));
        userDetails.getChildren().addAll(userName, userDept);

        profileCard.getChildren().addAll(avatar, userDetails);

        // 3. Menu Buttons
        VBox menuBox = new VBox(8);
        VBox.setVgrow(menuBox, Priority.ALWAYS);

        btnDashboard = new Button();
        btnCourses = new Button();
        btnLibrary = new Button();
        btnSettings = new Button();

        btnDashboard.setOnAction(e -> switchTab("Dashboard"));
        btnCourses.setOnAction(e -> switchTab("My Courses"));
        btnLibrary.setOnAction(e -> switchTab("Library"));
        btnSettings.setOnAction(e -> switchTab("Settings"));

        menuBox.getChildren().addAll(btnDashboard, btnCourses, btnLibrary, btnSettings);
        
        // Set initial active state
        styleSidebarButton(btnDashboard, "🏠  Dashboard", true);
        styleSidebarButton(btnCourses, "📚  My Courses", false);
        styleSidebarButton(btnLibrary, "📖  Library", false);
        styleSidebarButton(btnSettings, "⚙️  Settings", false);

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
        btnSignOut.setOnAction(e -> {
            cleanup();
            app.logout();
        });

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
        
        styleSidebarButton(btnDashboard, "🏠  Dashboard", "Dashboard".equals(tabName));
        styleSidebarButton(btnCourses, "📚  My Courses", "My Courses".equals(tabName));
        styleSidebarButton(btnLibrary, "📖  Library", "Library".equals(tabName));
        styleSidebarButton(btnSettings, "⚙️  Settings", "Settings".equals(tabName));

        activeTabContent.getChildren().clear();

        // Manage search field visibility
        searchContainer.setVisible("Dashboard".equals(tabName) || "My Courses".equals(tabName));
        searchContainer.setManaged("Dashboard".equals(tabName) || "My Courses".equals(tabName));
        searchField.clear();

        if ("Dashboard".equals(tabName)) {
            welcomeLabel.setText("Welcome back, " + currentUser.getFullName().split(" ")[0] + "! 👋");
            welcomeSub.setText("Here's what's happening in your courses today.");
            activeTabContent.getChildren().add(dashboardTabPane);
        } else if ("My Courses".equals(tabName)) {
            welcomeLabel.setText("My Courses 📚");
            welcomeSub.setText("Manage your enrolled courses and view files.");
            buildCoursesGrid(enrolledCoursesList);
            activeTabContent.getChildren().add(coursesGridScrollPane);
        } else if ("Library".equals(tabName)) {
            welcomeLabel.setText("Library 📖");
            welcomeSub.setText("Discover study materials across all departments.");
            BrowseMaterialsView libraryView = new BrowseMaterialsView(app, currentUser);
            activeTabContent.getChildren().add(libraryView.getView());
        } else if ("Settings".equals(tabName)) {
            welcomeLabel.setText("Settings ⚙️");
            welcomeSub.setText("Manage your account details and security settings.");
            buildSettingsPane();
            activeTabContent.getChildren().add(settingsScrollPane);
        }
    }

    // ── Top Navigation Bar ───────────────────────────────────────────────────
    private HBox buildTopBar() {
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(20, 30, 10, 30));
        topBar.setStyle("-fx-background-color: #faf9fb;");

        VBox welcomeBox = new VBox(4);
        String name = currentUser.getFullName() != null ? currentUser.getFullName().split(" ")[0] : "Student";
        welcomeLabel = new Label("Welcome back, " + name + "! 👋");
        welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        welcomeLabel.setTextFill(Color.web("#1e1b4b"));

        welcomeSub = new Label("Here's what's happening in your courses today.");
        welcomeSub.setFont(Font.font("Segoe UI", 12));
        welcomeSub.setTextFill(Color.web("#64748b"));
        welcomeBox.getChildren().addAll(welcomeLabel, welcomeSub);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        // Search container
        searchContainer = new StackPane();
        searchField = new TextField();
        searchField.setPromptText("Search materials, courses...");
        searchField.setPrefWidth(260);
        searchField.setStyle(
            "-fx-background-color: #f1f5f9;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 16 8 36;" +
            "-fx-font-size: 13px;"
        );
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if ("Dashboard".equals(activeTabName)) {
                filterMaterials(newVal);
            } else if ("My Courses".equals(activeTabName)) {
                filterCoursesGrid(newVal);
            }
        });

        Label searchIcon = new Label("🔍");
        searchIcon.setStyle("-fx-text-fill: #94a3b8; -fx-padding: 0 0 0 12;");
        StackPane.setAlignment(searchIcon, Pos.CENTER_LEFT);
        searchContainer.getChildren().addAll(searchField, searchIcon);

        // Notification bell button with SVG bell shape
        StackPane bellContainer = new StackPane();
        bellContainer.setCursor(javafx.scene.Cursor.HAND);
        
        javafx.scene.shape.SVGPath bellIcon = new javafx.scene.shape.SVGPath();
        bellIcon.setContent("M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 4.36 6 6.92 6 10v5l-2 2v1h16v-1l-2-2z");
        bellIcon.setFill(Color.web("#4b5563"));
        bellIcon.setScaleX(0.9);
        bellIcon.setScaleY(0.9);

        Button bellBtn = new Button();
        bellBtn.setGraphic(bellIcon);
        bellBtn.setStyle(
            "-fx-background-color: #f1f5f9;" +
            "-fx-background-radius: 20;" +
            "-fx-pref-width: 36;" +
            "-fx-pref-height: 36;" +
            "-fx-min-width: 36;" +
            "-fx-min-height: 36;" +
            "-fx-alignment: center;"
        );
        bellBtn.setOnMouseEntered(ev -> {
            bellBtn.setStyle(
                "-fx-background-color: #e2e8f0;" +
                "-fx-background-radius: 20;" +
                "-fx-pref-width: 36;" +
                "-fx-pref-height: 36;" +
                "-fx-min-width: 36;" +
                "-fx-min-height: 36;" +
                "-fx-alignment: center;"
            );
            bellIcon.setFill(Color.web("#1e293b"));
        });
        bellBtn.setOnMouseExited(ev -> {
            bellBtn.setStyle(
                "-fx-background-color: #f1f5f9;" +
                "-fx-background-radius: 20;" +
                "-fx-pref-width: 36;" +
                "-fx-pref-height: 36;" +
                "-fx-min-width: 36;" +
                "-fx-min-height: 36;" +
                "-fx-alignment: center;"
            );
            bellIcon.setFill(Color.web("#4b5563"));
        });
        
        bellBtn.setOnAction(e -> {
            if (notificationPopup.isShowing()) {
                notificationPopup.hide();
            } else {
                notificationPopup.show(bellBtn, Side.BOTTOM, -280, 5);
            }
        });

        unreadBadge = new Label("0");
        unreadBadge.setStyle(
            "-fx-background-color: #ef4444;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 9px;" +
            "-fx-padding: 1 5 1 5;" +
            "-fx-background-radius: 10;"
        );
        unreadBadge.setMouseTransparent(true);
        unreadBadge.setVisible(false);
        StackPane.setAlignment(unreadBadge, Pos.TOP_RIGHT);
        StackPane.setMargin(unreadBadge, new Insets(-2, -2, 0, 0));
        bellContainer.getChildren().addAll(bellBtn, unreadBadge);

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

        HBox topActions = new HBox(12, searchContainer, bellContainer, avatarCircle);
        topActions.setAlignment(Pos.CENTER_RIGHT);

        topBar.getChildren().addAll(welcomeBox, topSpacer, topActions);
        return topBar;
    }

    // ── Tab: Dashboard Pane ───────────────────────────────────────────────────
    private StackPane buildDashboardTabPane() {
        StackPane overlayPane = new StackPane();

        centerScrollPane = new ScrollPane();
        centerScrollPane.setFitToWidth(true);
        centerScrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        VBox contentBox = new VBox(24);
        contentBox.setPadding(new Insets(10, 30, 24, 30));
        contentBox.setStyle("-fx-background-color: #faf9fb;");

        // 1. Studies and stats section
        HBox statsAndStudiesBox = new HBox(20);
        statsAndStudiesBox.setAlignment(Pos.CENTER_LEFT);

        // Card A: Continue Studying
        StackPane continueCard = new StackPane();
        HBox.setHgrow(continueCard, Priority.ALWAYS);
        continueCard.setPrefHeight(160);
        continueCard.setStyle(
            "-fx-background-color: linear-gradient(to right, #fdf2f8, #fbcfe8);" +
            "-fx-background-radius: 16;" +
            "-fx-padding: 20;"
        );

        Label watermark = new Label("📖");
        watermark.setFont(Font.font("Segoe UI", 84));
        watermark.setTextFill(Color.web("#db2777"));
        watermark.setOpacity(0.12);
        StackPane.setAlignment(watermark, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(watermark, new Insets(0, 10, -10, 0));

        VBox continueContent = new VBox(8);
        continueContent.setAlignment(Pos.CENTER_LEFT);
        Label continueTitle = new Label("Continue Studying");
        continueTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        continueTitle.setTextFill(Color.web("#831843"));

        continueMsgLabel = new Label("Explore new study materials in your courses. Keep it up!");
        continueMsgLabel.setFont(Font.font("Segoe UI", 12));
        continueMsgLabel.setTextFill(Color.web("#9d174d"));
        continueMsgLabel.setWrapText(true);
        continueMsgLabel.setMaxWidth(380);

        btnResume = new Button("Resume Session");
        btnResume.setCursor(javafx.scene.Cursor.HAND);
        btnResume.setStyle(
            "-fx-background-color: #db2777;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 18 8 18;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 11px;"
        );
        btnResume.setOnMouseEntered(e -> btnResume.setStyle(
            "-fx-background-color: #be185d;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 18 8 18;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 11px;"
        ));
        btnResume.setOnMouseExited(e -> btnResume.setStyle(
            "-fx-background-color: #db2777;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 18 8 18;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 11px;"
        ));
        btnResume.setOnAction(e -> {
            if (lastDownloadedMaterial != null) {
                downloadMaterial(lastDownloadedMaterial);
            } else if (!originalMaterialsList.isEmpty()) {
                downloadMaterial(originalMaterialsList.get(0));
            }
        });

        continueContent.getChildren().addAll(continueTitle, continueMsgLabel, btnResume);
        continueCard.getChildren().addAll(watermark, continueContent);

        // Card B: Downloads Stat
        VBox downloadsCard = new VBox(10);
        downloadsCard.setPrefWidth(240);
        downloadsCard.setMinWidth(240);
        downloadsCard.setPrefHeight(160);
        downloadsCard.setPadding(new Insets(20));
        downloadsCard.setStyle(
            "-fx-background-color: #f3f4f6;" +
            "-fx-background-radius: 16;"
        );

        Label downloadsLabel = new Label("DOWNLOADS");
        downloadsLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        downloadsLabel.setTextFill(Color.web("#4b5563"));

        downloadsCountLabel = new Label("0");
        downloadsCountLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        downloadsCountLabel.setTextFill(Color.web("#111827"));

        Label downloadsSubLabel = new Label("this week");
        downloadsSubLabel.setFont(Font.font("Segoe UI", 12));
        downloadsSubLabel.setTextFill(Color.web("#4b5563"));

        HBox lineBar = new HBox(4);
        lineBar.setPadding(new Insets(6, 0, 0, 0));
        for (int i = 0; i < 4; i++) {
            Region segment = new Region();
            segment.setPrefSize(40, 4);
            if (i < 3) {
                segment.setStyle("-fx-background-color: #db2777; -fx-background-radius: 2;");
            } else {
                segment.setStyle("-fx-background-color: #e5e7eb; -fx-background-radius: 2;");
            }
            lineBar.getChildren().add(segment);
        }

        downloadsCard.getChildren().addAll(downloadsLabel, downloadsCountLabel, downloadsSubLabel, lineBar);
        statsAndStudiesBox.getChildren().addAll(continueCard, downloadsCard);

        // 2. My Courses Carousel Section
        VBox coursesSection = new VBox(12);

        HBox coursesHeaderRow = new HBox();
        coursesHeaderRow.setAlignment(Pos.CENTER_LEFT);
        Label coursesTitle = new Label("My Courses");
        coursesTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        coursesTitle.setTextFill(Color.web("#1f2937"));

        Region coursesSpacer = new Region();
        HBox.setHgrow(coursesSpacer, Priority.ALWAYS);

        Hyperlink viewAllLink = new Hyperlink("View All  ➔");
        viewAllLink.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        viewAllLink.setTextFill(Color.web("#db2777"));
        viewAllLink.setUnderline(false);
        viewAllLink.setOnAction(e -> {
            selectedCourseFilter = null;
            loadData();
        });

        coursesHeaderRow.getChildren().addAll(coursesTitle, coursesSpacer, viewAllLink);

        ScrollPane coursesScroll = new ScrollPane();
        coursesScroll.setFitToHeight(true);
        coursesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        coursesScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        coursesScroll.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        coursesCarouselBox = new HBox(16);
        coursesCarouselBox.setPadding(new Insets(4, 4, 10, 4));
        coursesScroll.setContent(coursesCarouselBox);
        coursesSection.getChildren().addAll(coursesHeaderRow, coursesScroll);

        // 3. Recent Study Materials Section
        VBox materialsSection = new VBox(12);

        HBox materialsHeaderRow = new HBox();
        materialsHeaderRow.setAlignment(Pos.CENTER_LEFT);
        Label materialsTitle = new Label("Recent Study Materials");
        materialsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        materialsTitle.setTextFill(Color.web("#1f2937"));

        Region materialsSpacer = new Region();
        HBox.setHgrow(materialsSpacer, Priority.ALWAYS);

        Button btnFilter = new Button("☶");
        btnFilter.setCursor(javafx.scene.Cursor.HAND);
        btnFilter.setStyle(
            "-fx-background-color: #f1f5f9;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 6 10 6 10;" +
            "-fx-font-weight: bold;"
        );

        materialsHeaderRow.getChildren().addAll(materialsTitle, materialsSpacer, btnFilter);

        materialsVBox = new VBox(10);
        materialsSection.getChildren().addAll(materialsHeaderRow, materialsVBox);

        // 4. Progress bar row
        HBox progressRow = new HBox(12);
        progressRow.setAlignment(Pos.CENTER_LEFT);
        progressRow.setPadding(new Insets(10, 14, 10, 14));
        progressRow.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 8;");

        downloadProgress = new ProgressBar(0);
        downloadProgress.setPrefWidth(220);
        downloadProgress.setPrefHeight(8);
        downloadProgress.setVisible(false);
        progressLabel = new Label();
        progressLabel.setVisible(false);
        progressLabel.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px;");
        progressRow.getChildren().addAll(downloadProgress, progressLabel);

        contentBox.getChildren().addAll(statsAndStudiesBox, coursesSection, materialsSection, progressRow);
        centerScrollPane.setContent(contentBox);

        // FAB upload button overlay
        Button btnFAB = new Button("+");
        btnFAB.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        btnFAB.setTextFill(Color.WHITE);
        btnFAB.setCursor(javafx.scene.Cursor.HAND);
        btnFAB.setPrefSize(52, 52);
        btnFAB.setMaxSize(52, 52);
        btnFAB.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #db2777, #ec4899);" +
            "-fx-background-radius: 30;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(219,39,119,0.35), 10, 0, 0, 4);"
        );
        btnFAB.setOnMouseEntered(e -> btnFAB.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #be185d, #db2777);" +
            "-fx-background-radius: 30;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(219,39,119,0.5), 12, 0, 0, 5);"
        ));
        btnFAB.setOnMouseExited(e -> btnFAB.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #db2777, #ec4899);" +
            "-fx-background-radius: 30;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(219,39,119,0.35), 10, 0, 0, 4);"
        ));
        btnFAB.setOnAction(e -> showUploadDialog());
        StackPane.setAlignment(btnFAB, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(btnFAB, new Insets(0, 24, 24, 0));

        overlayPane.getChildren().addAll(centerScrollPane, btnFAB);
        return overlayPane;
    }

    // ── Tab: My Courses Grid ──────────────────────────────────────────────────
    private void buildCoursesGrid(List<Course> courses) {
        FlowPane grid = new FlowPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(10, 30, 24, 30));
        grid.setStyle("-fx-background-color: #faf9fb;");

        for (Course course : courses) {
            VBox card = new VBox(12);
            card.setPrefWidth(180);
            card.setMinWidth(180);
            card.setPadding(new Insets(16));
            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #f3e8ff;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 16;" +
                "-fx-background-radius: 16;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 6, 0, 0, 1);"
            );

            StackPane iconWrapper = new StackPane();
            iconWrapper.setPrefSize(32, 32);
            iconWrapper.setMaxSize(32, 32);
            iconWrapper.setStyle("-fx-background-color: #fae8ff; -fx-background-radius: 10;");

            String emoji = "📚";
            String code = course.getCourseCode().toLowerCase();
            String name = course.getCourseName().toLowerCase();
            if (name.contains("programming") || code.contains("cs101") || name.contains("java") || name.contains("c++")) {
                emoji = "💻";
            } else if (name.contains("data structure") || name.contains("algorithm")) {
                emoji = "🌿";
            } else if (name.contains("database") || name.contains("sql") || name.contains("system")) {
                emoji = "📊";
            }
            Label icon = new Label(emoji);
            icon.setFont(Font.font(16));
            iconWrapper.getChildren().add(icon);

            Label title = new Label(course.getCourseName());
            title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            title.setTextFill(Color.web("#1f2937"));
            title.setWrapText(true);
            title.setPrefHeight(36);
            title.setMaxHeight(36);

            Label desc = new Label(course.getCourseCode() + " • " + (course.getTeacherName() != null ? course.getTeacherName() : "Staff"));
            desc.setFont(Font.font("Segoe UI", 10));
            desc.setTextFill(Color.web("#6b7280"));

            HBox bottomRow = new HBox();
            bottomRow.setAlignment(Pos.CENTER_LEFT);

            Label filesBadge = new Label(course.getMaterialCount() + " FILES");
            filesBadge.setStyle(
                "-fx-background-color: #fae8ff;" +
                "-fx-text-fill: #a21caf;" +
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 2 6 2 6;" +
                "-fx-background-radius: 8;"
            );

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label statusLabel = new Label("Active");
            statusLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 9));
            statusLabel.setTextFill(Color.web("#db2777"));

            bottomRow.getChildren().addAll(filesBadge, spacer, statusLabel);
            card.getChildren().addAll(iconWrapper, title, desc, bottomRow);

            // Hover animations
            card.setOnMouseEntered(e -> {
                card.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #db2777;" +
                    "-fx-border-width: 1.5;" +
                    "-fx-border-radius: 16;" +
                    "-fx-background-radius: 16;" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(219,39,119,0.18), 12, 0, 0, 4);"
                );
                card.setTranslateY(-4);
            });
            card.setOnMouseExited(e -> {
                card.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #f3e8ff;" +
                    "-fx-border-width: 1.5;" +
                    "-fx-border-radius: 16;" +
                    "-fx-background-radius: 16;" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 8, 0, 0, 2);"
                );
                card.setTranslateY(0);
            });

            card.setCursor(javafx.scene.Cursor.HAND);
            card.setOnMouseClicked(e -> {
                selectedCourseFilter = course;
                switchTab("Dashboard");
            });

            grid.getChildren().add(card);
        }

        // Dotted enroll card
        VBox enrollCard = new VBox(12);
        enrollCard.setPrefWidth(180);
        enrollCard.setMinWidth(180);
        enrollCard.setPrefHeight(146);
        enrollCard.setPadding(new Insets(16));
        enrollCard.setAlignment(Pos.CENTER);
        enrollCard.setStyle(
            "-fx-border-style: dashed;" +
            "-fx-border-color: #cbd5e1;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-background-color: transparent;" +
            "-fx-cursor: hand;"
        );

        StackPane addCircle = new StackPane();
        addCircle.setPrefSize(36, 36);
        addCircle.setMaxSize(36, 36);
        addCircle.setStyle(
            "-fx-border-color: #94a3b8;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 18;"
        );
        Label plusSign = new Label("+");
        plusSign.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        plusSign.setTextFill(Color.web("#475569"));
        addCircle.getChildren().add(plusSign);

        Label enrollText = new Label("Enroll in Course");
        enrollText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        enrollText.setTextFill(Color.web("#475569"));

        enrollCard.getChildren().addAll(addCircle, enrollText);

        enrollCard.setOnMouseEntered(e -> {
            enrollCard.setStyle(
                "-fx-border-style: dashed;" +
                "-fx-border-color: #db2777;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 16;" +
                "-fx-background-radius: 16;" +
                "-fx-background-color: #fdf2f8;" +
                "-fx-cursor: hand;"
            );
            addCircle.setStyle(
                "-fx-background-color: #db2777;" +
                "-fx-border-color: #db2777;" +
                "-fx-border-radius: 18;"
            );
            plusSign.setTextFill(Color.WHITE);
        });
        enrollCard.setOnMouseExited(e -> {
            enrollCard.setStyle(
                "-fx-border-style: dashed;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 16;" +
                "-fx-background-radius: 16;" +
                "-fx-background-color: transparent;" +
                "-fx-cursor: hand;"
            );
            addCircle.setStyle(
                "-fx-border-color: #94a3b8;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 18;"
            );
            plusSign.setTextFill(Color.web("#475569"));
        });
        enrollCard.setOnMouseClicked(e -> showEnrollmentDialog());

        grid.getChildren().add(enrollCard);
        coursesGridScrollPane.setContent(grid);
    }

    private void filterCoursesGrid(String filterText) {
        if (filterText == null || filterText.isBlank()) {
            buildCoursesGrid(enrolledCoursesList);
        } else {
            String filter = filterText.toLowerCase();
            List<Course> filtered = new ArrayList<>();
            for (Course c : enrolledCoursesList) {
                if (c.getCourseName().toLowerCase().contains(filter) || c.getCourseCode().toLowerCase().contains(filter)) {
                    filtered.add(c);
                }
            }
            buildCoursesGrid(filtered);
        }
    }

    // ── Tab: Settings Pane ────────────────────────────────────────────────────
    private void buildSettingsPane() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(10, 30, 24, 30));
        root.setStyle("-fx-background-color: #faf9fb;");

        // Profile Box card
        VBox profileBox = new VBox(16);
        profileBox.setPadding(new Insets(20));
        profileBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #f1f5f9;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.01), 6, 0, 0, 1);"
        );

        Label profileTitle = new Label("Profile Information");
        profileTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1f2937;");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(12);

        Label lblName = new Label("Full Name:");
        lblName.setStyle("-fx-text-fill: #6b7280; -fx-font-weight: bold;");
        Label valName = new Label(currentUser.getFullName());
        valName.setStyle("-fx-text-fill: #1f2937; -fx-font-weight: bold;");

        Label lblUser = new Label("Username:");
        lblUser.setStyle("-fx-text-fill: #6b7280; -fx-font-weight: bold;");
        Label valUser = new Label("@" + currentUser.getUsername());
        valUser.setStyle("-fx-text-fill: #1f2937;");

        Label lblEmail = new Label("Email Address:");
        lblEmail.setStyle("-fx-text-fill: #6b7280; -fx-font-weight: bold;");
        Label valEmail = new Label(currentUser.getEmail());
        valEmail.setStyle("-fx-text-fill: #1f2937;");

        Label lblRole = new Label("Role:");
        lblRole.setStyle("-fx-text-fill: #6b7280; -fx-font-weight: bold;");
        Label valRole = new Label(currentUser.getRole().toUpperCase());
        valRole.setStyle(
            "-fx-background-color: #fae8ff;" +
            "-fx-text-fill: #a21caf;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 2 8 2 8;" +
            "-fx-background-radius: 10;"
        );

        Label lblDept = new Label("Department:");
        lblDept.setStyle("-fx-text-fill: #6b7280; -fx-font-weight: bold;");
        Label valDept = new Label(currentUser.getDeptName() != null ? currentUser.getDeptName() : "General");
        valDept.setStyle("-fx-text-fill: #1f2937;");

        grid.add(lblName, 0, 0);  grid.add(valName, 1, 0);
        grid.add(lblUser, 0, 1);  grid.add(valUser, 1, 1);
        grid.add(lblEmail, 0, 2); grid.add(valEmail, 1, 2);
        grid.add(lblRole, 0, 3);  grid.add(valRole, 1, 3);
        grid.add(lblDept, 0, 4);  grid.add(valDept, 1, 4);

        profileBox.getChildren().addAll(profileTitle, grid);

        // Security Box card (change password)
        VBox securityBox = new VBox(16);
        securityBox.setPadding(new Insets(20));
        securityBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #f1f5f9;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.01), 6, 0, 0, 1);"
        );

        Label securityTitle = new Label("Change Password");
        securityTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1f2937;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(20);
        formGrid.setVgap(12);

        Label lblOld = new Label("Current Password:");
        lblOld.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 12px;");
        PasswordField txtOld = new PasswordField();
        txtOld.setPromptText("Enter current password");
        txtOld.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 8;");
        txtOld.setPrefWidth(220);

        Label lblNew = new Label("New Password:");
        lblNew.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 12px;");
        PasswordField txtNew = new PasswordField();
        txtNew.setPromptText("Enter new password");
        txtNew.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 8;");

        Label lblConfirm = new Label("Confirm Password:");
        lblConfirm.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 12px;");
        PasswordField txtConfirm = new PasswordField();
        txtConfirm.setPromptText("Confirm new password");
        txtConfirm.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 8;");

        Button btnSave = new Button("Update Password");
        btnSave.setCursor(javafx.scene.Cursor.HAND);
        btnSave.setStyle(
            "-fx-background-color: #db2777;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 20 8 20;"
        );
        btnSave.setOnMouseEntered(e -> btnSave.setStyle(
            "-fx-background-color: #be185d;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 20 8 20;"
        ));
        btnSave.setOnMouseExited(e -> btnSave.setStyle(
            "-fx-background-color: #db2777;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 20 8 20;"
        ));

        btnSave.setOnAction(e -> {
            String oldPass = txtOld.getText();
            String newPass = txtNew.getText();
            String confirmPass = txtConfirm.getText();

            if (oldPass.isBlank() || newPass.isBlank() || confirmPass.isBlank()) {
                new Alert(Alert.AlertType.ERROR, "Please fill in all fields.", ButtonType.OK).showAndWait();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                new Alert(Alert.AlertType.ERROR, "New passwords do not match.", ButtonType.OK).showAndWait();
                return;
            }

            try {
                if (UserDAO.changePassword(currentUser.getUserId(), oldPass, newPass)) {
                    new Alert(Alert.AlertType.INFORMATION, "Password updated successfully!", ButtonType.OK).showAndWait();
                    txtOld.clear();
                    txtNew.clear();
                    txtConfirm.clear();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Current password is incorrect.", ButtonType.OK).showAndWait();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Database error: " + ex.getMessage(), ButtonType.OK).showAndWait();
            }
        });

        formGrid.add(lblOld, 0, 0);     formGrid.add(txtOld, 1, 0);
        formGrid.add(lblNew, 0, 1);     formGrid.add(txtNew, 1, 1);
        formGrid.add(lblConfirm, 0, 2); formGrid.add(txtConfirm, 1, 2);
        formGrid.add(btnSave, 1, 3);

        securityBox.getChildren().addAll(securityTitle, formGrid);

        root.getChildren().addAll(profileBox, securityBox);
        settingsScrollPane.setContent(root);
    }

    // ── Dropdown Notification Menu Content ────────────────────────────────────
    private void buildNotificationDropdown() {
        notificationPopup = new ContextMenu();
        notificationPopup.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-padding: 0;"
        );

        VBox dropdownContent = new VBox(10);
        dropdownContent.setPadding(new Insets(12));
        dropdownContent.setPrefWidth(320);
        dropdownContent.setPrefHeight(350);
        dropdownContent.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #cbd5e1;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 4);"
        );

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        Label heading = new Label("🔔 Notifications");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        heading.setTextFill(Color.web("#1e293b"));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button markBtn = new Button("Mark all read");
        markBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #db2777;" +
            "-fx-font-size: 11px;" +
            "-fx-cursor: hand;" +
            "-fx-underline: false;" +
            "-fx-font-weight: bold;"
        );
        markBtn.setOnAction(e -> markAllAsRead());
        header.getChildren().addAll(heading, sp, markBtn);

        notificationList = new ListView<>();
        notificationList.setPlaceholder(new Label("No notifications yet"));
        VBox.setVgrow(notificationList, Priority.ALWAYS);
        notificationList.setPrefHeight(280);
        notificationList.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background-insets: 0;" +
            "-fx-padding: 0;"
        );

        notificationList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Notification item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); return; }

                VBox card = new VBox(4);
                card.setPadding(new Insets(8, 10, 8, 12));
                card.setMaxWidth(Double.MAX_VALUE);

                Label title = new Label(item.getTitle());
                title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

                Label msg = new Label(item.getMessage());
                msg.setWrapText(true);
                msg.setFont(Font.font("Segoe UI", 12));
                msg.setTextFill(Color.web("#475569"));

                Label ts = new Label(item.getCreatedAt() != null
                    ? item.getCreatedAt().toString().substring(0, 16) : "");
                ts.setFont(Font.font("Segoe UI", 10));
                ts.setTextFill(Color.web("#94a3b8"));

                card.getChildren().addAll(title, msg, ts);

                if (!item.isRead()) {
                    card.setStyle(
                        "-fx-background-color: #fdf2f8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #db2777;" +
                        "-fx-border-width: 0 0 0 3;" +
                        "-fx-border-radius: 8;"
                    );
                    title.setTextFill(Color.web("#be185d"));
                } else {
                    card.setStyle(
                        "-fx-background-color: #f8fafc;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 0 0 0 3;" +
                        "-fx-border-radius: 8;"
                    );
                    title.setTextFill(Color.web("#64748b"));
                }
                setGraphic(card);
            }
        });

        notificationList.setOnMouseClicked(e -> {
            Notification sel = notificationList.getSelectionModel().getSelectedItem();
            if (sel != null && !sel.isRead()) {
                markAsRead(sel);
            }
        });

        dropdownContent.getChildren().addAll(header, notificationList);

        CustomMenuItem item = new CustomMenuItem(dropdownContent, false);
        notificationPopup.getItems().add(item);
    }

    // ── Data Loading & Card Rebuilding ─────────────────────────────────────────
    private void loadData() {
        try {
            loadLastDownloadedMaterial();

            enrolledCoursesList = CourseDAO.getEnrolledCourses(currentUser.getUserId());
            populateCourses(enrolledCoursesList);

            List<Material> recentMaterials = MaterialDAO.getRecentMaterialsForEnrolledCourses(currentUser.getUserId(), 20);
            originalMaterialsList.setAll(recentMaterials);
            populateRecentMaterials(recentMaterials);

            // Update stats download count
            int totalDownloads = 0;
            for (Material m : recentMaterials) {
                totalDownloads += m.getDownloadCount();
            }
            if (downloadsCountLabel != null) {
                downloadsCountLabel.setText(String.valueOf(totalDownloads));
            }

            refreshNotifications();
            
            // Re-render courses grid if currently active tab
            if ("My Courses".equals(activeTabName)) {
                buildCoursesGrid(enrolledCoursesList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadLastDownloadedMaterial() {
        int lastId = getLastDownloadedMaterialId();
        if (lastId != -1) {
            try {
                this.lastDownloadedMaterial = MaterialDAO.getMaterialById(lastId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        updateContinueStudyingCard();
    }

    private void updateContinueStudyingCard() {
        if (continueMsgLabel == null) return;
        if (lastDownloadedMaterial != null) {
            continueMsgLabel.setText("You were last looking at \"" + lastDownloadedMaterial.getTitle() + "\". Keep it up!");
            btnResume.setText("Resume Session");
        } else if (!originalMaterialsList.isEmpty()) {
            continueMsgLabel.setText("Explore new study materials in your courses. Keep it up!");
            btnResume.setText("Start Studying");
        } else {
            continueMsgLabel.setText("You have no study materials available. Enroll in a course to start!");
            btnResume.setText("Browse Courses");
        }
    }

    private void populateCourses(List<Course> courses) {
        coursesCarouselBox.getChildren().clear();

        for (Course course : courses) {
            VBox card = new VBox(12);
            card.setPrefWidth(180);
            card.setMinWidth(180);
            card.setPadding(new Insets(16));

            boolean isSelected = selectedCourseFilter != null && selectedCourseFilter.getCourseId() == course.getCourseId();
            if (isSelected) {
                card.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #db2777;" +
                    "-fx-border-width: 1.5;" +
                    "-fx-border-radius: 16;" +
                    "-fx-background-radius: 16;" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(219,39,119,0.12), 10, 0, 0, 3);"
                );
            } else {
                card.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #f3e8ff;" +
                    "-fx-border-width: 1.5;" +
                    "-fx-border-radius: 16;" +
                    "-fx-background-radius: 16;" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 6, 0, 0, 1);"
                );
            }

            StackPane iconWrapper = new StackPane();
            iconWrapper.setPrefSize(32, 32);
            iconWrapper.setMaxSize(32, 32);
            iconWrapper.setStyle("-fx-background-color: #fae8ff; -fx-background-radius: 10;");

            String emoji = "📚";
            String code = course.getCourseCode().toLowerCase();
            String name = course.getCourseName().toLowerCase();
            if (name.contains("programming") || code.contains("cs101") || name.contains("java") || name.contains("c++")) {
                emoji = "💻";
            } else if (name.contains("data structure") || name.contains("algorithm")) {
                emoji = "🌿";
            } else if (name.contains("database") || name.contains("sql") || name.contains("system")) {
                emoji = "📊";
            }
            Label icon = new Label(emoji);
            icon.setFont(Font.font(16));
            iconWrapper.getChildren().add(icon);

            Label title = new Label(course.getCourseName());
            title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            title.setTextFill(Color.web("#1f2937"));
            title.setWrapText(true);
            title.setPrefHeight(36);
            title.setMaxHeight(36);

            Label desc = new Label(course.getCourseCode() + " • " + (course.getTeacherName() != null ? course.getTeacherName() : "Staff"));
            desc.setFont(Font.font("Segoe UI", 10));
            desc.setTextFill(Color.web("#6b7280"));

            HBox bottomRow = new HBox();
            bottomRow.setAlignment(Pos.CENTER_LEFT);

            Label filesBadge = new Label(course.getMaterialCount() + " FILES");
            filesBadge.setStyle(
                "-fx-background-color: #fae8ff;" +
                "-fx-text-fill: #a21caf;" +
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 2 6 2 6;" +
                "-fx-background-radius: 8;"
            );

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label statusLabel = new Label("Active");
            statusLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 9));
            statusLabel.setTextFill(Color.web("#db2777"));

            bottomRow.getChildren().addAll(filesBadge, spacer, statusLabel);
            card.getChildren().addAll(iconWrapper, title, desc, bottomRow);

            // Hover animations
            card.setOnMouseEntered(e -> {
                card.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #db2777;" +
                    "-fx-border-width: 1.5;" +
                    "-fx-border-radius: 16;" +
                    "-fx-background-radius: 16;" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(219,39,119,0.18), 12, 0, 0, 4);"
                );
                card.setTranslateY(-4);
            });
            card.setOnMouseExited(e -> {
                boolean active = selectedCourseFilter != null && selectedCourseFilter.getCourseId() == course.getCourseId();
                card.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: " + (active ? "#db2777" : "#f3e8ff") + ";" +
                    "-fx-border-width: 1.5;" +
                    "-fx-border-radius: 16;" +
                    "-fx-background-radius: 16;" +
                    "-fx-effect: dropshadow(three-pass-box, " + (active ? "rgba(219,39,119,0.12)" : "rgba(0,0,0,0.02)") + ", 8, 0, 0, 2);"
                );
                card.setTranslateY(0);
            });

            card.setCursor(javafx.scene.Cursor.HAND);
            card.setOnMouseClicked(e -> {
                if (selectedCourseFilter != null && selectedCourseFilter.getCourseId() == course.getCourseId()) {
                    selectedCourseFilter = null;
                } else {
                    selectedCourseFilter = course;
                }
                loadData();
            });

            coursesCarouselBox.getChildren().add(card);
        }

        // Dotted enroll card
        VBox enrollCard = new VBox(12);
        enrollCard.setPrefWidth(180);
        enrollCard.setMinWidth(180);
        enrollCard.setPrefHeight(146);
        enrollCard.setPadding(new Insets(16));
        enrollCard.setAlignment(Pos.CENTER);
        enrollCard.setStyle(
            "-fx-border-style: dashed;" +
            "-fx-border-color: #cbd5e1;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-background-color: transparent;" +
            "-fx-cursor: hand;"
        );

        StackPane addCircle = new StackPane();
        addCircle.setPrefSize(36, 36);
        addCircle.setMaxSize(36, 36);
        addCircle.setStyle("-fx-border-color: #94a3b8; -fx-border-width: 1.5; -fx-border-radius: 18;");
        Label plusSign = new Label("+");
        plusSign.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        plusSign.setTextFill(Color.web("#475569"));
        addCircle.getChildren().add(plusSign);

        Label enrollText = new Label("Enroll in Course");
        enrollText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        enrollText.setTextFill(Color.web("#475569"));

        enrollCard.getChildren().addAll(addCircle, enrollText);

        enrollCard.setOnMouseEntered(e -> {
            enrollCard.setStyle(
                "-fx-border-style: dashed;" +
                "-fx-border-color: #db2777;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 16;" +
                "-fx-background-radius: 16;" +
                "-fx-background-color: #fdf2f8;" +
                "-fx-cursor: hand;"
            );
            addCircle.setStyle(
                "-fx-background-color: #db2777;" +
                "-fx-border-color: #db2777;" +
                "-fx-border-radius: 18;"
            );
            plusSign.setTextFill(Color.WHITE);
        });
        enrollCard.setOnMouseExited(e -> {
            enrollCard.setStyle(
                "-fx-border-style: dashed;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 16;" +
                "-fx-background-radius: 16;" +
                "-fx-background-color: transparent;" +
                "-fx-cursor: hand;"
            );
            addCircle.setStyle(
                "-fx-border-color: #94a3b8;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 18;"
            );
            plusSign.setTextFill(Color.web("#475569"));
        });
        enrollCard.setOnMouseClicked(e -> showEnrollmentDialog());

        coursesCarouselBox.getChildren().add(enrollCard);
    }

    private void populateRecentMaterials(List<Material> materials) {
        materialsVBox.getChildren().clear();

        List<Material> displayed = new ArrayList<>();
        for (Material m : materials) {
            if (selectedCourseFilter != null && m.getCourseId() != selectedCourseFilter.getCourseId()) {
                continue;
            }
            displayed.add(m);
        }

        if (displayed.isEmpty()) {
            Label placeholder = new Label("No study materials available");
            placeholder.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-padding: 20 0 20 0;");
            materialsVBox.getChildren().add(placeholder);
            return;
        }

        for (Material material : displayed) {
            HBox row = new HBox(16);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 16, 12, 16));
            row.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #f1f5f9;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.01), 6, 0, 0, 1);"
            );

            StackPane typeCircle = new StackPane();
            typeCircle.setPrefSize(38, 38);
            typeCircle.setMaxSize(38, 38);
            String type = material.getMaterialType() != null ? material.getMaterialType().toLowerCase() : "pdf";
            String badgeBgColor = "#fee2e2";
            String iconChar = "📄";

            switch (type) {
                case "pdf":
                    badgeBgColor = "#fee2e2";
                    iconChar = "📄";
                    break;
                case "link":
                    badgeBgColor = "#e0f2fe";
                    iconChar = "🔗";
                    break;
                case "doc": case "docx":
                    badgeBgColor = "#dbeafe";
                    iconChar = "📝";
                    break;
                case "ppt": case "pptx":
                    badgeBgColor = "#fef3c7";
                    iconChar = "📊";
                    break;
            }

            typeCircle.setStyle("-fx-background-color: " + badgeBgColor + "; -fx-background-radius: 20;");
            Label iconLbl = new Label(iconChar);
            iconLbl.setFont(Font.font(16));
            typeCircle.getChildren().add(iconLbl);

            VBox textDetails = new VBox(4);
            HBox.setHgrow(textDetails, Priority.ALWAYS);

            Label titleLabel = new Label(material.getTitle());
            titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            titleLabel.setTextFill(Color.web("#1f2937"));

            String dateStr = material.getUploadDate() != null ? material.getUploadDate().toString().substring(0, 10) : "N/A";
            Label metaLabel = new Label(material.getCourseName() + " • Uploaded " + dateStr);
            metaLabel.setFont(Font.font("Segoe UI", 11));
            metaLabel.setTextFill(Color.web("#6b7280"));

            textDetails.getChildren().addAll(titleLabel, metaLabel);

            Button getBtn = new Button("Get");
            if ("link".equals(type)) getBtn.setText("Open");
            getBtn.setCursor(javafx.scene.Cursor.HAND);
            getBtn.setStyle(
                "-fx-background-color: #6366f1;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 6 16 6 16;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 11px;"
            );

            getBtn.setOnMouseEntered(e -> getBtn.setStyle(
                "-fx-background-color: #4f46e5;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 6 16 6 16;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 11px;"
            ));
            getBtn.setOnMouseExited(e -> getBtn.setStyle(
                "-fx-background-color: #6366f1;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 6 16 6 16;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 11px;"
            ));

            getBtn.setOnAction(e -> downloadMaterial(material));

            row.getChildren().addAll(typeCircle, textDetails, getBtn);

            row.setOnMouseEntered(e -> row.setStyle(
                "-fx-background-color: #faf5ff;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #c084fc;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(162,28,175,0.06), 8, 0, 0, 2);"
            ));
            row.setOnMouseExited(e -> row.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #f1f5f9;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.01), 6, 0, 0, 1);"
            ));

            materialsVBox.getChildren().add(row);
        }
    }

    private void filterMaterials(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            populateRecentMaterials(new ArrayList<>(originalMaterialsList));
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            List<Material> filteredList = new ArrayList<>();
            for (Material material : originalMaterialsList) {
                if ((material.getTitle() != null && material.getTitle().toLowerCase().contains(lowerCaseFilter)) ||
                    (material.getCourseName() != null && material.getCourseName().toLowerCase().contains(lowerCaseFilter)) ||
                    (material.getMaterialType() != null && material.getMaterialType().toLowerCase().contains(lowerCaseFilter)) ||
                    (material.getUploaderName() != null && material.getUploaderName().toLowerCase().contains(lowerCaseFilter))) {
                    filteredList.add(material);
                }
            }
            populateRecentMaterials(filteredList);
        }
    }

    private void refreshNotifications() {
        try {
            notificationList.getItems().setAll(
                NotificationDAO.getNotificationsForUser(currentUser.getUserId()));
            int unread = NotificationDAO.getUnreadNotificationCount(currentUser.getUserId());
            unreadBadge.setText(String.valueOf(unread));
            unreadBadge.setVisible(unread > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void markAsRead(Notification n) {
        try {
            if (NotificationDAO.markAsRead(n.getNotificationId())) refreshNotifications();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void markAllAsRead() {
        for (Notification n : notificationList.getItems())
            if (!n.isRead()) markAsRead(n);
    }

    // ── File Downloads ────────────────────────────────────────────────────────
    private void downloadMaterial(Material material) {
        if ("file".equals(material.getMaterialType()) && material.getFilePath() != null) {
            FileChooser fc = new FileChooser();
            fc.setInitialFileName(material.getFileName());
            fc.setTitle("Save Material");
            File save = fc.showSaveDialog(view.getScene().getWindow());
            if (save == null) return;

            downloadProgress.setVisible(true);
            progressLabel.setVisible(true);
            progressLabel.setText("Downloading: " + material.getFileName());

            Task<Void> task = FileDownloadService.downloadFileTask(
                material.getFilePath(), save.getAbsolutePath(), downloadProgress,
                () -> {
                    downloadProgress.setVisible(false);
                    progressLabel.setVisible(false);

                    saveLastDownloadedMaterial(material.getMaterialId());

                    new Alert(Alert.AlertType.INFORMATION,
                        "Saved to: " + save.getAbsolutePath(), ButtonType.OK).showAndWait();
                    try {
                        MaterialDAO.incrementDownloadCount(material.getMaterialId());
                        loadData();
                    } catch (Exception ex) { ex.printStackTrace(); }
                });
            new Thread(task).start();

        } else if ("link".equals(material.getMaterialType()) && material.getLinkUrl() != null) {
            try {
                saveLastDownloadedMaterial(material.getMaterialId());
                loadData();

                java.awt.Desktop.getDesktop().browse(new java.net.URI(material.getLinkUrl()));
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private void saveLastDownloadedMaterial(int materialId) {
        try {
            java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(StudentDashboardView.class);
            prefs.putInt("last_download_" + currentUser.getUserId(), materialId);
        } catch (Exception e) {}
    }

    private int getLastDownloadedMaterialId() {
        try {
            java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(StudentDashboardView.class);
            return prefs.getInt("last_download_" + currentUser.getUserId(), -1);
        } catch (Exception e) {
            return -1;
        }
    }

    // ── Enroll Dialog ────────────────────────────────────────────────────────
    private void showEnrollmentDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Enroll in Course");
        dialog.setHeaderText("Choose a course to enroll in");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);
        dialogPane.setStyle("-fx-background-color: white;");

        VBox content = new VBox(12);
        content.setPrefWidth(420);
        content.setPrefHeight(380);

        TextField searchCourse = new TextField();
        searchCourse.setPromptText("🔍 Search courses...");
        searchCourse.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 8;");

        ListView<Course> unenrolledList = new ListView<>();
        VBox.setVgrow(unenrolledList, Priority.ALWAYS);
        unenrolledList.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        List<Course> allUnenrolled = new ArrayList<>();
        try {
            allUnenrolled.addAll(CourseDAO.getUnenrolledCourses(currentUser.getUserId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        unenrolledList.getItems().setAll(allUnenrolled);

        searchCourse.textProperty().addListener((obs, oldV, newV) -> {
            if (newV == null || newV.isBlank()) {
                unenrolledList.getItems().setAll(allUnenrolled);
            } else {
                String filter = newV.toLowerCase();
                List<Course> filtered = new ArrayList<>();
                for (Course c : allUnenrolled) {
                    if (c.getCourseName().toLowerCase().contains(filter) || c.getCourseCode().toLowerCase().contains(filter)) {
                        filtered.add(c);
                    }
                }
                unenrolledList.getItems().setAll(filtered);
            }
        });

        unenrolledList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Course item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setPadding(new Insets(8));
                    row.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8;");

                    VBox info = new VBox(2);
                    Label name = new Label(item.getCourseName());
                    name.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1f2937;");
                    Label code = new Label(item.getCourseCode() + " • " + (item.getTeacherName() != null ? item.getTeacherName() : "No Instructor"));
                    code.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
                    info.getChildren().addAll(name, code);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button btnEnroll = new Button("Enroll");
                    btnEnroll.setStyle(
                        "-fx-background-color: #db2777;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 4 12 4 12;" +
                        "-fx-cursor: hand;"
                    );
                    btnEnroll.setOnAction(e -> {
                        try {
                            if (CourseDAO.enrollStudentInCourse(currentUser.getUserId(), item.getCourseId())) {
                                new Alert(Alert.AlertType.INFORMATION, "Successfully enrolled in " + item.getCourseName() + "!", ButtonType.OK).showAndWait();
                                dialog.close();
                                loadData();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            new Alert(Alert.AlertType.ERROR, "Enrollment failed: " + ex.getMessage(), ButtonType.OK).showAndWait();
                        }
                    });

                    row.getChildren().addAll(info, spacer, btnEnroll);
                    setGraphic(row);
                }
            }
        });

        content.getChildren().addAll(searchCourse, unenrolledList);
        dialogPane.setContent(content);
        dialog.showAndWait();
    }

    // ── Upload Material Dialog ───────────────────────────────────────────────
    private void showUploadDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Upload Study Material");
        dialog.setHeaderText("Share study material with your peers");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setStyle("-fx-background-color: white;");

        VBox form = new VBox(12);
        form.setPadding(new Insets(10));
        form.setPrefWidth(400);

        ComboBox<Course> courseCombo = new ComboBox<>();
        courseCombo.setMaxWidth(Double.MAX_VALUE);
        try {
            courseCombo.getItems().setAll(CourseDAO.getEnrolledCourses(currentUser.getUserId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!courseCombo.getItems().isEmpty()) {
            courseCombo.getSelectionModel().select(0);
        }

        TextField titleInput = new TextField();
        titleInput.setPromptText("Enter title (e.g. Chapter 1 Basics)");

        TextArea descInput = new TextArea();
        descInput.setPromptText("Enter description...");
        descInput.setPrefRowCount(3);

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("File", "Link");
        typeCombo.getSelectionModel().select(0);

        HBox fileRow = new HBox(10);
        fileRow.setAlignment(Pos.CENTER_LEFT);
        Button btnSelectFile = new Button("Choose File...");
        Label lblFileName = new Label("No file selected");
        fileRow.getChildren().addAll(btnSelectFile, lblFileName);

        TextField linkInput = new TextField();
        linkInput.setPromptText("Enter link URL (e.g. https://...)");
        linkInput.setVisible(false);
        linkInput.setManaged(false);

        typeCombo.setOnAction(e -> {
            boolean isFile = "File".equals(typeCombo.getValue());
            fileRow.setVisible(isFile);
            fileRow.setManaged(isFile);
            linkInput.setVisible(!isFile);
            linkInput.setManaged(!isFile);
        });

        final File[] selectedFile = new File[1];
        btnSelectFile.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Material File");
            selectedFile[0] = fileChooser.showOpenDialog(view.getScene().getWindow());
            if (selectedFile[0] != null) {
                lblFileName.setText(selectedFile[0].getName() + " (" + (selectedFile[0].length() / 1024) + " KB)");
            }
        });

        form.getChildren().addAll(
            new Label("Select Course:"), courseCombo,
            new Label("Title:"), titleInput,
            new Label("Description:"), descInput,
            new Label("Type:"), typeCombo,
            fileRow, linkInput
        );

        dialogPane.setContent(form);

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setText("Upload");
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            Course selectedCourse = courseCombo.getValue();
            String title = titleInput.getText();
            String type = typeCombo.getValue().toLowerCase();

            if (selectedCourse == null || title.isBlank()) {
                new Alert(Alert.AlertType.ERROR, "Please select a course and fill in the title.", ButtonType.OK).showAndWait();
                event.consume();
                return;
            }

            Material material = new Material();
            material.setCourseId(selectedCourse.getCourseId());
            material.setUploadedBy(currentUser.getUserId());
            material.setTitle(title.trim());
            material.setDescription(descInput.getText().trim());
            material.setMaterialType(type);

            if ("file".equals(type)) {
                if (selectedFile[0] == null) {
                    new Alert(Alert.AlertType.ERROR, "Please select a file to upload.", ButtonType.OK).showAndWait();
                    event.consume();
                    return;
                }
                File destDir = new File("uploads");
                if (!destDir.exists()) destDir.mkdirs();
                File destFile = new File(destDir, System.currentTimeMillis() + "_" + selectedFile[0].getName());
                try {
                    java.nio.file.Files.copy(selectedFile[0].toPath(), destFile.toPath());
                    material.setFileName(selectedFile[0].getName());
                    material.setFilePath(destFile.getAbsolutePath());
                    material.setFileSize(selectedFile[0].length());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Failed to copy file.", ButtonType.OK).showAndWait();
                    event.consume();
                    return;
                }
            } else {
                String link = linkInput.getText();
                if (link.isBlank()) {
                    new Alert(Alert.AlertType.ERROR, "Please enter a link URL.", ButtonType.OK).showAndWait();
                    event.consume();
                    return;
                }
                material.setLinkUrl(link.trim());
                material.setFileName("web_link");
                material.setFileSize(0);
            }

            try {
                if (MaterialDAO.uploadMaterial(material)) {
                    new Alert(Alert.AlertType.INFORMATION, "Material uploaded successfully and is pending admin approval!", ButtonType.OK).showAndWait();
                    loadData();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Upload failed. Please try again.", ButtonType.OK).showAndWait();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Database error: " + ex.getMessage(), ButtonType.OK).showAndWait();
            }
        });

        dialog.showAndWait();
    }

    // ── Socket & Notification Handlers ───────────────────────────────────────
    private void setupNotificationClient() {
        notificationClient = new NotificationClient(
            "localhost", 5000, currentUser.getUserId(), "student",
            msg -> Platform.runLater(this::loadData));
        new Thread(notificationClient).start();
    }

    public void cleanup() {
        if (notificationClient != null) notificationClient.disconnect();
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.isBlank()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    public BorderPane getView() { return view; }
}
