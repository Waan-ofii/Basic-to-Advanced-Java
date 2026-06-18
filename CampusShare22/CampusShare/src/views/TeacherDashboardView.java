package views;

import application.CampusShareApp;
import models.*;
import dao.*;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class TeacherDashboardView {
    private CampusShareApp app;
    private User currentUser;
    private BorderPane view;

    // Structure elements
    private BorderPane contentArea;
    private StackPane activeTabContent;
    private ScrollPane dashboardScrollPane;
    private ScrollPane uploadScrollPane;
    private ScrollPane myUploadsScrollPane;
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
    private Button btnUpload;
    private Button btnMyUploads;
    private Button btnCourses;
    private Button btnSettings;
    private String activeTabName = "Dashboard";

    // Dashboard elements
    private Label downloadsCountLabel;
    private Label mostDownloadedLabel;
    private GridPane statsGrid;
    private VBox recentMaterialsVBox;

    // Upload Form Controls
    private ComboBox<Course> courseSelector;
    private TextField titleField;
    private TextArea descriptionArea;
    private ToggleGroup materialTypeGroup;
    private RadioButton fileRadio;
    private RadioButton linkRadio;
    private RadioButton videoRadio;
    private Label selectedFileLabel;
    private Button chooseFileBtn;
    private File selectedFile;
    private TextField linkUrlField;
    private ProgressBar uploadProgress;
    private Label uploadStatusLabel;

    // My Uploads Controls
    private TableView<Material> myUploadsTable;
    private ComboBox<Material> rejectedMaterialCombo;
    private TextArea revisionNoteArea;
    private ComboBox<String> statusFilter;

    // Data lists
    private ObservableList<Material> originalMaterialsList = FXCollections.observableArrayList();
    private List<Course> myCoursesList = new ArrayList<>();
    private Course selectedCourseFilter = null;

    // Notification dropdown
    private ListView<Notification> notificationList;
    private ContextMenu notificationPopup;
    private NotificationClient notificationClient;

    // ─────────────────────────────────────────────────────────────────────────
    public TeacherDashboardView(CampusShareApp app, User user) {
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

        // Setup scrollable tab panes
        dashboardScrollPane = buildDashboardTab();
        
        uploadScrollPane = buildUploadTab();
        
        myUploadsScrollPane = buildMyUploadsTab();

        coursesGridScrollPane = new ScrollPane();
        coursesGridScrollPane.setFitToWidth(true);
        coursesGridScrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        settingsScrollPane = new ScrollPane();
        settingsScrollPane.setFitToWidth(true);
        settingsScrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        activeTabContent = new StackPane(dashboardScrollPane);

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
        String dept = currentUser.getDeptName() != null ? currentUser.getDeptName() : "Teacher";
        Label userDept = new Label(dept);
        userDept.setFont(Font.font("Segoe UI", 11));
        userDept.setTextFill(Color.web("#a78bfa"));
        userDetails.getChildren().addAll(userName, userDept);

        profileCard.getChildren().addAll(avatar, userDetails);

        // 3. Menu Buttons
        VBox menuBox = new VBox(8);
        VBox.setVgrow(menuBox, Priority.ALWAYS);

        btnDashboard = new Button();
        btnUpload = new Button();
        btnMyUploads = new Button();
        btnCourses = new Button();
        btnSettings = new Button();

        btnDashboard.setOnAction(e -> switchTab("Dashboard"));
        btnUpload.setOnAction(e -> switchTab("Upload Material"));
        btnMyUploads.setOnAction(e -> switchTab("My Uploads"));
        btnCourses.setOnAction(e -> switchTab("My Courses"));
        btnSettings.setOnAction(e -> switchTab("Settings"));

        menuBox.getChildren().addAll(btnDashboard, btnUpload, btnMyUploads, btnCourses, btnSettings);

        styleSidebarButton(btnDashboard, "🏠  Dashboard", true);
        styleSidebarButton(btnUpload, "📤  Upload Material", false);
        styleSidebarButton(btnMyUploads, "📁  My Uploads", false);
        styleSidebarButton(btnCourses, "📚  My Courses", false);
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
        styleSidebarButton(btnUpload, "📤  Upload Material", "Upload Material".equals(tabName));
        styleSidebarButton(btnMyUploads, "📁  My Uploads", "My Uploads".equals(tabName));
        styleSidebarButton(btnCourses, "📚  My Courses", "My Courses".equals(tabName));
        styleSidebarButton(btnSettings, "⚙️  Settings", "Settings".equals(tabName));

        activeTabContent.getChildren().clear();

        // Manage search field visibility
        boolean showSearch = "Dashboard".equals(tabName) || "My Uploads".equals(tabName) || "My Courses".equals(tabName);
        searchContainer.setVisible(showSearch);
        searchContainer.setManaged(showSearch);
        searchField.clear();

        if ("Dashboard".equals(tabName)) {
            welcomeLabel.setText("Welcome back, " + currentUser.getFullName().split(" ")[0] + "! 👋");
            welcomeSub.setText("Here's your teaching overview for today.");
            loadData();
            activeTabContent.getChildren().add(dashboardScrollPane);
        } else if ("Upload Material".equals(tabName)) {
            welcomeLabel.setText("Upload Material 📤");
            welcomeSub.setText("Publish educational resources to your courses.");
            activeTabContent.getChildren().add(uploadScrollPane);
        } else if ("My Uploads".equals(tabName)) {
            welcomeLabel.setText("My Uploads 📁");
            welcomeSub.setText("Track status, delete, or resubmit your course materials.");
            loadMyUploads();
            activeTabContent.getChildren().add(myUploadsScrollPane);
        } else if ("My Courses".equals(tabName)) {
            welcomeLabel.setText("My Courses 📚");
            welcomeSub.setText("Manage your active courses and lecture materials.");
            buildCoursesGrid(myCoursesList);
            activeTabContent.getChildren().add(coursesGridScrollPane);
        } else if ("Settings".equals(tabName)) {
            welcomeLabel.setText("Settings ⚙️");
            welcomeSub.setText("Update your password and view account details.");
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
        String name = currentUser.getFullName() != null ? currentUser.getFullName().split(" ")[0] : "Teacher";
        welcomeLabel = new Label("Welcome back, " + name + "! 👋");
        welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        welcomeLabel.setTextFill(Color.web("#1e1b4b"));

        welcomeSub = new Label("Here's your teaching overview for today.");
        welcomeSub.setFont(Font.font("Segoe UI", 12));
        welcomeSub.setTextFill(Color.web("#64748b"));
        welcomeBox.getChildren().addAll(welcomeLabel, welcomeSub);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        // Search container
        searchContainer = new StackPane();
        searchField = new TextField();
        searchField.setPromptText("Search materials, uploads...");
        searchField.setPrefWidth(260);
        searchField.setStyle(
            "-fx-background-color: #f1f5f9;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 16 8 36;" +
            "-fx-font-size: 13px;"
        );

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if ("Dashboard".equals(activeTabName)) {
                filterRecentMaterials(newVal);
            } else if ("My Courses".equals(activeTabName)) {
                filterCoursesGrid(newVal);
            } else if ("My Uploads".equals(activeTabName)) {
                filterUploadsTable(newVal);
            }
        });

        Label searchIcon = new Label("🔍");
        searchIcon.setStyle("-fx-text-fill: #94a3b8; -fx-padding: 0 0 0 12;");
        StackPane.setAlignment(searchIcon, Pos.CENTER_LEFT);
        searchContainer.getChildren().addAll(searchField, searchIcon);

        // Notification bell button
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
    private ScrollPane buildDashboardTab() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        VBox contentBox = new VBox(24);
        contentBox.setPadding(new Insets(10, 30, 24, 30));
        contentBox.setStyle("-fx-background-color: #faf9fb;");

        // 1. Banner section
        HBox bannerRow = new HBox(20);
        bannerRow.setAlignment(Pos.CENTER_LEFT);

        // Card A: Teacher Hub Banner
        StackPane teacherHubCard = new StackPane();
        HBox.setHgrow(teacherHubCard, Priority.ALWAYS);
        teacherHubCard.setPrefHeight(160);
        teacherHubCard.setStyle(
            "-fx-background-color: linear-gradient(to right, #fdf2f8, #fbcfe8);" +
            "-fx-background-radius: 16;" +
            "-fx-padding: 20;"
        );

        Label watermark = new Label("📚");
        watermark.setFont(Font.font("Segoe UI", 84));
        watermark.setTextFill(Color.web("#db2777"));
        watermark.setOpacity(0.12);
        StackPane.setAlignment(watermark, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(watermark, new Insets(0, 10, -10, 0));

        VBox bannerContent = new VBox(8);
        bannerContent.setAlignment(Pos.CENTER_LEFT);
        Label bannerTitle = new Label("Teacher Hub");
        bannerTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        bannerTitle.setTextFill(Color.web("#831843"));

        Label bannerMsgLabel = new Label("Prepare lecture materials, upload syllabi, and keep track of your students' downloads.");
        bannerMsgLabel.setFont(Font.font("Segoe UI", 12));
        bannerMsgLabel.setTextFill(Color.web("#9d174d"));
        bannerMsgLabel.setWrapText(true);
        bannerMsgLabel.setMaxWidth(380);

        Button btnQuickUpload = new Button("Quick Upload");
        btnQuickUpload.setCursor(javafx.scene.Cursor.HAND);
        btnQuickUpload.setStyle(
            "-fx-background-color: #db2777;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 18 8 18;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 11px;"
        );
        btnQuickUpload.setOnMouseEntered(e -> btnQuickUpload.setStyle(
            "-fx-background-color: #be185d;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 18 8 18;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 11px;"
        ));
        btnQuickUpload.setOnMouseExited(e -> btnQuickUpload.setStyle(
            "-fx-background-color: #db2777;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 18 8 18;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 11px;"
        ));
        btnQuickUpload.setOnAction(e -> switchTab("Upload Material"));

        bannerContent.getChildren().addAll(bannerTitle, bannerMsgLabel, btnQuickUpload);
        teacherHubCard.getChildren().addAll(watermark, bannerContent);

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

        Label downloadsLabel = new Label("STUDENT DOWNLOADS");
        downloadsLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        downloadsLabel.setTextFill(Color.web("#4b5563"));

        downloadsCountLabel = new Label("0");
        downloadsCountLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        downloadsCountLabel.setTextFill(Color.web("#111827"));

        mostDownloadedLabel = new Label("Most downloaded: N/A");
        mostDownloadedLabel.setFont(Font.font("Segoe UI", 11));
        mostDownloadedLabel.setTextFill(Color.web("#4b5563"));
        mostDownloadedLabel.setWrapText(true);

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

        downloadsCard.getChildren().addAll(downloadsLabel, downloadsCountLabel, mostDownloadedLabel, lineBar);
        bannerRow.getChildren().addAll(teacherHubCard, downloadsCard);

        // 2. Stats Cards Grid
        statsGrid = new GridPane();
        statsGrid.setHgap(16);
        statsGrid.setVgap(16);

        // 3. Recent Materials/Activity Section
        VBox recentSection = new VBox(12);
        Label sectionTitle = new Label("Recent Material Uploads");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        sectionTitle.setTextFill(Color.web("#1f2937"));

        recentMaterialsVBox = new VBox(10);
        recentSection.getChildren().addAll(sectionTitle, recentMaterialsVBox);

        contentBox.getChildren().addAll(bannerRow, statsGrid, recentSection);
        scrollPane.setContent(contentBox);
        return scrollPane;
    }

    private void addStatCard(GridPane grid, String label, String value, int col, int row, String bgColor, String borderColor, String textColor) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setPrefWidth(220);
        card.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.01), 6, 0, 0, 1);"
        );

        Label labelLabel = new Label(label.toUpperCase());
        labelLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        labelLabel.setTextFill(Color.web(textColor));
        labelLabel.setOpacity(0.7);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        valueLabel.setTextFill(Color.web(textColor));

        card.getChildren().addAll(labelLabel, valueLabel);
        grid.add(card, col, row);
    }

    // ── Tab: Upload Material Pane ─────────────────────────────────────────────
    private ScrollPane buildUploadTab() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(10, 30, 24, 30));
        content.setStyle("-fx-background-color: #faf9fb;");

        VBox formCard = new VBox(20);
        formCard.setPadding(new Insets(24));
        formCard.setMaxWidth(800);
        formCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #f1f5f9;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 8, 0, 0, 2);"
        );

        Label cardTitle = new Label("Material Details");
        cardTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        cardTitle.setTextFill(Color.web("#1f2937"));

        GridPane formGrid = new GridPane();
        formGrid.setHgap(20);
        formGrid.setVgap(16);

        // Course Selector
        Label lblCourse = new Label("Select Course:*");
        lblCourse.setStyle("-fx-text-fill: #4b5563; -fx-font-weight: bold; -fx-font-size: 13px;");
        courseSelector = new ComboBox<>();
        courseSelector.setPromptText("Choose a course");
        courseSelector.setPrefWidth(400);
        courseSelector.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 4; -fx-font-size: 13px;");

        // Title
        Label lblTitle = new Label("Material Title:*");
        lblTitle.setStyle("-fx-text-fill: #4b5563; -fx-font-weight: bold; -fx-font-size: 13px;");
        titleField = new TextField();
        titleField.setPromptText("Enter material title (e.g. Lecture 1 - Introduction)");
        titleField.setPrefWidth(400);
        titleField.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 8; -fx-font-size: 13px;");

        // Description
        Label lblDesc = new Label("Description:*");
        lblDesc.setStyle("-fx-text-fill: #4b5563; -fx-font-weight: bold; -fx-font-size: 13px;");
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Enter description or syllabus details...");
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setPrefWidth(400);
        descriptionArea.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 8; -fx-font-size: 13px;");

        // Material Type
        Label lblType = new Label("Material Type:*");
        lblType.setStyle("-fx-text-fill: #4b5563; -fx-font-weight: bold; -fx-font-size: 13px;");
        materialTypeGroup = new ToggleGroup();

        HBox typeBox = new HBox(20);
        typeBox.setAlignment(Pos.CENTER_LEFT);
        fileRadio = new RadioButton("📁 File Upload");
        fileRadio.setToggleGroup(materialTypeGroup);
        fileRadio.setSelected(true);
        fileRadio.setStyle("-fx-text-fill: #475569; -fx-font-weight: bold; -fx-font-size: 13px;");

        linkRadio = new RadioButton("🔗 External Link");
        linkRadio.setToggleGroup(materialTypeGroup);
        linkRadio.setStyle("-fx-text-fill: #475569; -fx-font-weight: bold; -fx-font-size: 13px;");

        videoRadio = new RadioButton("🎥 Video Url");
        videoRadio.setToggleGroup(materialTypeGroup);
        videoRadio.setStyle("-fx-text-fill: #475569; -fx-font-weight: bold; -fx-font-size: 13px;");

        typeBox.getChildren().addAll(fileRadio, linkRadio, videoRadio);

        // Upload Sub-sections
        VBox uploadMethodContainer = new VBox(12);

        // File Row
        HBox fileRow = new HBox(12);
        fileRow.setAlignment(Pos.CENTER_LEFT);
        chooseFileBtn = new Button("Choose File...");
        chooseFileBtn.setCursor(javafx.scene.Cursor.HAND);
        chooseFileBtn.setStyle(
            "-fx-background-color: #db2777;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 6 16 6 16;"
        );
        chooseFileBtn.setOnMouseEntered(e -> chooseFileBtn.setStyle(
            "-fx-background-color: #be185d;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 6 16 6 16;"
        ));
        chooseFileBtn.setOnMouseExited(e -> chooseFileBtn.setStyle(
            "-fx-background-color: #db2777;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 6 16 6 16;"
        ));
        chooseFileBtn.setOnAction(e -> chooseFile());

        selectedFileLabel = new Label("No file selected");
        selectedFileLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        fileRow.getChildren().addAll(chooseFileBtn, selectedFileLabel);

        // URL row
        linkUrlField = new TextField();
        linkUrlField.setPromptText("Enter link URL (e.g. https://example.com/lecture.pdf)");
        linkUrlField.setPrefWidth(400);
        linkUrlField.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 8; -fx-font-size: 13px;");
        linkUrlField.setVisible(false);
        linkUrlField.setManaged(false);

        uploadMethodContainer.getChildren().addAll(fileRow, linkUrlField);

        fileRadio.setOnAction(e -> {
            fileRow.setVisible(true); fileRow.setManaged(true);
            linkUrlField.setVisible(false); linkUrlField.setManaged(false);
        });
        linkRadio.setOnAction(e -> {
            fileRow.setVisible(false); fileRow.setManaged(false);
            linkUrlField.setVisible(true); linkUrlField.setManaged(true);
        });
        videoRadio.setOnAction(e -> {
            fileRow.setVisible(false); fileRow.setManaged(false);
            linkUrlField.setVisible(true); linkUrlField.setManaged(true);
        });

        // Submit Button Row
        HBox submitRow = new HBox(16);
        submitRow.setAlignment(Pos.CENTER_LEFT);

        Button submitBtn = new Button("Submit for Approval");
        submitBtn.setCursor(javafx.scene.Cursor.HAND);
        submitBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #db2777, #ec4899);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 10 24 10 24;" +
            "-fx-font-size: 13px;"
        );
        submitBtn.setOnMouseEntered(e -> submitBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #be185d, #db2777);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 10 24 10 24;" +
            "-fx-font-size: 13px;"
        ));
        submitBtn.setOnMouseExited(e -> submitBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #db2777, #ec4899);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 10 24 10 24;" +
            "-fx-font-size: 13px;"
        ));
        submitBtn.setOnAction(e -> submitMaterial());

        uploadProgress = new ProgressBar(0);
        uploadProgress.setPrefWidth(150);
        uploadProgress.setVisible(false);

        uploadStatusLabel = new Label();
        uploadStatusLabel.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px;");

        submitRow.getChildren().addAll(submitBtn, uploadProgress, uploadStatusLabel);

        formGrid.add(lblCourse, 0, 0); formGrid.add(courseSelector, 1, 0);
        formGrid.add(lblTitle, 0, 1); formGrid.add(titleField, 1, 1);
        formGrid.add(lblDesc, 0, 2); formGrid.add(descriptionArea, 1, 2);
        formGrid.add(lblType, 0, 3); formGrid.add(typeBox, 1, 3);
        formGrid.add(new Label("Source:*"), 0, 4); formGrid.add(uploadMethodContainer, 1, 4);
        formGrid.add(submitRow, 1, 5);

        formCard.getChildren().addAll(cardTitle, formGrid);
        content.getChildren().add(formCard);
        scrollPane.setContent(content);
        return scrollPane;
    }

    // ── Tab: My Uploads Pane ──────────────────────────────────────────────────
    private ScrollPane buildMyUploadsTab() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(10, 30, 24, 30));
        content.setStyle("-fx-background-color: #faf9fb;");

        // Card Container
        VBox tableCard = new VBox(16);
        tableCard.setPadding(new Insets(20));
        tableCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #f1f5f9;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.01), 6, 0, 0, 1);"
        );

        // Filter Row
        HBox filterRow = new HBox(12);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        Label filterLabel = new Label("Filter by Status:");
        filterLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569; -fx-font-size: 13px;");

        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Approved", "Rejected");
        statusFilter.setValue("All");
        statusFilter.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-font-size: 12px;");
        statusFilter.setOnAction(e -> filterUploads(statusFilter.getValue()));

        filterRow.getChildren().addAll(filterLabel, statusFilter);

        // Table
        myUploadsTable = new TableView<>();
        myUploadsTable.setPlaceholder(new Label("No materials uploaded yet."));
        myUploadsTable.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        TableColumn<Material, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        titleCol.setPrefWidth(180);

        TableColumn<Material, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
        courseCol.setPrefWidth(160);

        TableColumn<Material, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaterialType().toUpperCase()));
        typeCol.setPrefWidth(70);

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

        TableColumn<Material, String> dateCol = new TableColumn<>("Upload Date");
        dateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUploadDate() != null
            ? cellData.getValue().getUploadDate().toString().substring(0, 10) : "N/A"));
        dateCol.setPrefWidth(100);

        TableColumn<Material, Integer> downloadsCol = new TableColumn<>("Downloads");
        downloadsCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDownloadCount()).asObject());
        downloadsCol.setPrefWidth(80);

        TableColumn<Material, String> commentCol = new TableColumn<>("Review Comment");
        commentCol.setCellValueFactory(cellData -> {
            String comment = cellData.getValue().getReviewComment();
            return new SimpleStringProperty(comment != null ? comment : "-");
        });
        commentCol.setPrefWidth(180);

        TableColumn<Material, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(80);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");
            {
                deleteBtn.setCursor(javafx.scene.Cursor.HAND);
                deleteBtn.setStyle(
                    "-fx-background-color: #ef4444;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 12;" +
                    "-fx-padding: 4 10 4 10;" +
                    "-fx-font-size: 11px;"
                );
                deleteBtn.setOnAction(e -> {
                    Material material = getTableView().getItems().get(getIndex());
                    deleteMaterial(material);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Material material = getTableView().getItems().get(getIndex());
                    if ("pending".equals(material.getStatus()) || "rejected".equals(material.getStatus())) {
                        setGraphic(deleteBtn);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        myUploadsTable.getColumns().addAll(titleCol, courseCol, typeCol, statusCol, dateCol, downloadsCol, commentCol, actionCol);

        // Resubmit Section Card
        VBox resubmitCard = new VBox(12);
        resubmitCard.setPadding(new Insets(20));
        resubmitCard.setStyle(
            "-fx-background-color: #f8fafc;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;"
        );

        Label resubmitTitle = new Label("Resubmit Rejected Material");
        resubmitTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        resubmitTitle.setTextFill(Color.web("#1e293b"));

        rejectedMaterialCombo = new ComboBox<>();
        rejectedMaterialCombo.setPromptText("Select rejected material to resubmit");
        rejectedMaterialCombo.setPrefWidth(400);
        rejectedMaterialCombo.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 4; -fx-font-size: 13px;");

        revisionNoteArea = new TextArea();
        revisionNoteArea.setPromptText("Explain what changes you made...");
        revisionNoteArea.setPrefHeight(60);
        revisionNoteArea.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 6; -fx-font-size: 13px;");

        Button resubmitBtn = new Button("Resubmit for Approval");
        resubmitBtn.setCursor(javafx.scene.Cursor.HAND);
        resubmitBtn.setStyle(
            "-fx-background-color: #6366f1;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 16;" +
            "-fx-padding: 8 20 8 20;"
        );
        resubmitBtn.setOnMouseEntered(e -> resubmitBtn.setStyle(
            "-fx-background-color: #4f46e5;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 16;" +
            "-fx-padding: 8 20 8 20;"
        ));
        resubmitBtn.setOnMouseExited(e -> resubmitBtn.setStyle(
            "-fx-background-color: #6366f1;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 16;" +
            "-fx-padding: 8 20 8 20;"
        ));
        resubmitBtn.setOnAction(e -> {
            Material selected = rejectedMaterialCombo.getValue();
            if (selected != null) {
                resubmitMaterial(selected, revisionNoteArea.getText());
            }
        });

        resubmitCard.getChildren().addAll(resubmitTitle, rejectedMaterialCombo, revisionNoteArea, resubmitBtn);

        tableCard.getChildren().addAll(filterRow, myUploadsTable);
        content.getChildren().addAll(tableCard, resubmitCard);
        scrollPane.setContent(content);

        return scrollPane;
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

            Label desc = new Label(course.getCourseCode() + " • Credits: " + course.getCredits());
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

        coursesGridScrollPane.setContent(grid);
    }

    private void filterCoursesGrid(String filterText) {
        if (filterText == null || filterText.isBlank()) {
            buildCoursesGrid(myCoursesList);
        } else {
            String filter = filterText.toLowerCase();
            List<Course> filtered = new ArrayList<>();
            for (Course c : myCoursesList) {
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

        // Security Box card
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
        notificationPopup.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

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
        markBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #db2777; -fx-font-size: 11px; -fx-cursor: hand; -fx-font-weight: bold;");
        markBtn.setOnAction(e -> markAllAsRead());
        header.getChildren().addAll(heading, sp, markBtn);

        notificationList = new ListView<>();
        notificationList.setPlaceholder(new Label("No notifications yet"));
        VBox.setVgrow(notificationList, Priority.ALWAYS);
        notificationList.setPrefHeight(280);
        notificationList.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

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

                Label ts = new Label(item.getCreatedAt() != null ? item.getCreatedAt().toString().substring(0, 16) : "");
                ts.setFont(Font.font("Segoe UI", 10));
                ts.setTextFill(Color.web("#94a3b8"));

                card.getChildren().addAll(title, msg, ts);

                if (!item.isRead()) {
                    card.setStyle("-fx-background-color: #fdf2f8; -fx-background-radius: 8; -fx-border-color: #db2777; -fx-border-width: 0 0 0 3; -fx-border-radius: 8;");
                    title.setTextFill(Color.web("#be185d"));
                } else {
                    card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 0 3; -fx-border-radius: 8;");
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

    // ── Data Loading & Logic ──────────────────────────────────────────────────
    private void loadData() {
        try {
            // Load courses
            myCoursesList = CourseDAO.getCoursesByTeacher(currentUser.getUserId());
            
            // Populate course selector in Upload Tab
            courseSelector.getItems().setAll(myCoursesList);
            courseSelector.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Course item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getCourseCode() + " - " + item.getCourseName());
                    }
                }
            });
            courseSelector.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Course item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getCourseCode() + " - " + item.getCourseName());
                    }
                }
            });

            // Rebuild home page statistics and recent uploads list
            TeacherStats stats = MaterialDAO.getTeacherStats(currentUser.getUserId());
            
            statsGrid.getChildren().clear();
            addStatCard(statsGrid, "Total Uploads", String.valueOf(stats.getTotalUploads()), 0, 0, "#eff6ff", "#dbeafe", "#1e40af");
            addStatCard(statsGrid, "Pending Approval", String.valueOf(stats.getPendingCount()), 1, 0, "#fffbeb", "#fef3c7", "#92400e");
            addStatCard(statsGrid, "Approved", String.valueOf(stats.getApprovedCount()), 2, 0, "#f0fdf4", "#dcfce7", "#166534");
            addStatCard(statsGrid, "Rejected", String.valueOf(stats.getRejectedCount()), 3, 0, "#fef2f2", "#fee2e2", "#991b1b");

            if (downloadsCountLabel != null) {
                downloadsCountLabel.setText(String.valueOf(stats.getTotalDownloads()));
            }
            if (mostDownloadedLabel != null) {
                mostDownloadedLabel.setText("Most downloaded: " + (stats.getMostDownloadedTitle() != null ? stats.getMostDownloadedTitle() : "N/A"));
            }

            List<Material> recent = MaterialDAO.getMaterialsByUploader(currentUser.getUserId());
            originalMaterialsList.setAll(recent);
            populateRecentMaterials(recent);

            refreshNotifications();

            // Re-render courses grid if currently active
            if ("My Courses".equals(activeTabName)) {
                buildCoursesGrid(myCoursesList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateRecentMaterials(List<Material> materials) {
        recentMaterialsVBox.getChildren().clear();

        List<Material> displayed = new ArrayList<>();
        for (Material m : materials) {
            if (selectedCourseFilter != null && m.getCourseId() != selectedCourseFilter.getCourseId()) {
                continue;
            }
            displayed.add(m);
        }

        if (displayed.isEmpty()) {
            Label placeholder = new Label("No uploads found.");
            placeholder.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-padding: 20 0 20 0;");
            recentMaterialsVBox.getChildren().add(placeholder);
            return;
        }

        // Only show top 5 on dashboard home page
        int limit = Math.min(displayed.size(), 5);
        for (int i = 0; i < limit; i++) {
            Material material = displayed.get(i);
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
                case "file":
                    badgeBgColor = "#fee2e2";
                    iconChar = "📄";
                    break;
                case "link":
                    badgeBgColor = "#e0f2fe";
                    iconChar = "🔗";
                    break;
                case "video":
                    badgeBgColor = "#fef3c7";
                    iconChar = "🎥";
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

            // Status Badge
            Label statusBadge = new Label(material.getStatus().toUpperCase());
            statusBadge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 9));
            switch (material.getStatus().toLowerCase()) {
                case "approved":
                    statusBadge.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #166534; -fx-padding: 3 8 3 8; -fx-background-radius: 8;");
                    break;
                case "pending":
                    statusBadge.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #92400e; -fx-padding: 3 8 3 8; -fx-background-radius: 8;");
                    break;
                case "rejected":
                    statusBadge.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-padding: 3 8 3 8; -fx-background-radius: 8;");
                    break;
            }

            row.getChildren().addAll(typeCircle, textDetails, statusBadge);

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

            recentMaterialsVBox.getChildren().add(row);
        }
    }

    private void filterRecentMaterials(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            populateRecentMaterials(new ArrayList<>(originalMaterialsList));
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            List<Material> filteredList = new ArrayList<>();
            for (Material material : originalMaterialsList) {
                if ((material.getTitle() != null && material.getTitle().toLowerCase().contains(lowerCaseFilter)) ||
                    (material.getCourseName() != null && material.getCourseName().toLowerCase().contains(lowerCaseFilter)) ||
                    (material.getMaterialType() != null && material.getMaterialType().toLowerCase().contains(lowerCaseFilter)) ||
                    (material.getStatus() != null && material.getStatus().toLowerCase().contains(lowerCaseFilter))) {
                    filteredList.add(material);
                }
            }
            populateRecentMaterials(filteredList);
        }
    }

    private void filterUploadsTable(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            loadMyUploads();
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            try {
                List<Material> materials = MaterialDAO.getMaterialsByUploader(currentUser.getUserId());
                List<Material> filtered = new ArrayList<>();
                for (Material m : materials) {
                    if ((m.getTitle() != null && m.getTitle().toLowerCase().contains(lowerCaseFilter)) ||
                        (m.getCourseName() != null && m.getCourseName().toLowerCase().contains(lowerCaseFilter)) ||
                        (m.getMaterialType() != null && m.getMaterialType().toLowerCase().contains(lowerCaseFilter)) ||
                        (m.getStatus() != null && m.getStatus().toLowerCase().contains(lowerCaseFilter))) {
                        filtered.add(m);
                    }
                }
                myUploadsTable.getItems().setAll(filtered);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadMyUploads() {
        try {
            List<Material> materials = MaterialDAO.getMaterialsByUploader(currentUser.getUserId());
            myUploadsTable.getItems().setAll(materials);
            loadRejectedMaterialsForResubmit(rejectedMaterialCombo);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load uploads: " + e.getMessage());
        }
    }

    private void filterUploads(String status) {
        if ("All".equals(status)) {
            loadMyUploads();
        } else {
            try {
                List<Material> materials = MaterialDAO.getMaterialsByUploaderAndStatus(currentUser.getUserId(), status.toLowerCase());
                myUploadsTable.getItems().setAll(materials);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ── Upload Material Functions ─────────────────────────────────────────────
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Material File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
            new FileChooser.ExtensionFilter("Word Documents", "*.doc", "*.docx"),
            new FileChooser.ExtensionFilter("PowerPoint", "*.ppt", "*.pptx"),
            new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.jpeg"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = fileChooser.showOpenDialog(view.getScene().getWindow());
        if (file != null) {
            selectedFile = file;
            selectedFileLabel.setText(file.getName() + " (" + formatFileSize(file.length()) + ")");
            selectedFileLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        }
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024));
    }

    private void submitMaterial() {
        if (courseSelector.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a course.");
            return;
        }

        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a title.");
            return;
        }

        String description = descriptionArea.getText().trim();
        if (description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a description.");
            return;
        }

        Material material = new Material();
        material.setCourseId(courseSelector.getValue().getCourseId());
        material.setUploadedBy(currentUser.getUserId());
        material.setTitle(title);
        material.setDescription(description);

        String materialType = fileRadio.isSelected() ? "file" : (linkRadio.isSelected() ? "link" : "video");
        material.setMaterialType(materialType);

        if (fileRadio.isSelected()) {
            if (selectedFile == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a file to upload.");
                return;
            }

            String uploadDir = "uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String uniqueFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
            String destinationPath = uploadDir + uniqueFileName;

            try {
                Files.copy(selectedFile.toPath(), new File(destinationPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                material.setFilePath(destinationPath);
                material.setFileName(selectedFile.getName());
                material.setFileSize(selectedFile.length());
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Upload Error", "Failed to save file: " + e.getMessage());
                return;
            }
        } else {
            String url = linkUrlField.getText().trim();
            if (url.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a URL.");
                return;
            }
            material.setLinkUrl(url);
            material.setFileName("web_link");
            material.setFileSize(0);
        }

        uploadProgress.setVisible(true);
        uploadStatusLabel.setText("Submitting material for approval...");
        uploadStatusLabel.setStyle("-fx-text-fill: #f39c12;");

        Thread uploadThread = new Thread(() -> {
            try {
                // Mock network delay progress
                for (double p = 0; p <= 1.0; p += 0.2) {
                    final double progress = p;
                    Platform.runLater(() -> uploadProgress.setProgress(progress));
                    Thread.sleep(150);
                }

                boolean success = MaterialDAO.uploadMaterial(material);

                Platform.runLater(() -> {
                    uploadProgress.setVisible(false);
                    if (success) {
                        uploadStatusLabel.setText("✓ Submitted successfully!");
                        uploadStatusLabel.setStyle("-fx-text-fill: #27ae60;");
                        clearUploadForm();
                        loadData();
                        showAlert(Alert.AlertType.INFORMATION, "Success",
                                "Your material has been submitted for admin approval.\nYou will be notified once reviewed.");
                    } else {
                        uploadStatusLabel.setText("✗ Submission failed.");
                        uploadStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    uploadProgress.setVisible(false);
                    uploadStatusLabel.setText("✗ Error: " + e.getMessage());
                    uploadStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
                });
                e.printStackTrace();
            }
        });
        uploadThread.start();
    }

    private void clearUploadForm() {
        courseSelector.setValue(null);
        titleField.clear();
        descriptionArea.clear();
        fileRadio.setSelected(true);
        selectedFile = null;
        selectedFileLabel.setText("No file selected");
        selectedFileLabel.setStyle("-fx-text-fill: #64748b;");
        linkUrlField.clear();
        uploadProgress.setProgress(0);
    }

    private void deleteMaterial(Material material) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Material");
        confirm.setContentText("Are you sure you want to delete \"" + material.getTitle() + "\"?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (material.getFilePath() != null) {
                        File file = new File(material.getFilePath());
                        if (file.exists()) file.delete();
                    }

                    if (MaterialDAO.deleteMaterial(material.getMaterialId())) {
                        loadData();
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Material deleted successfully.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete: " + e.getMessage());
                }
            }
        });
    }

    private void resubmitMaterial(Material oldMaterial, String revisionNote) {
        Material newMaterial = new Material();
        newMaterial.setCourseId(oldMaterial.getCourseId());
        newMaterial.setUploadedBy(currentUser.getUserId());
        newMaterial.setTitle(oldMaterial.getTitle());
        newMaterial.setDescription(oldMaterial.getDescription());
        newMaterial.setMaterialType(oldMaterial.getMaterialType());
        newMaterial.setFilePath(oldMaterial.getFilePath());
        newMaterial.setFileName(oldMaterial.getFileName());
        newMaterial.setLinkUrl(oldMaterial.getLinkUrl());

        try {
            if (MaterialDAO.uploadMaterial(newMaterial)) {
                MaterialDAO.deleteMaterial(oldMaterial.getMaterialId());
                loadData();
                revisionNoteArea.clear();
                rejectedMaterialCombo.setValue(null);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Material resubmitted successfully!\nRevision details: " + revisionNote);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to resubmit: " + e.getMessage());
        }
    }

    private void loadRejectedMaterialsForResubmit(ComboBox<Material> combo) {
        try {
            List<Material> rejected = MaterialDAO.getMaterialsByUploaderAndStatus(currentUser.getUserId(), "rejected");
            combo.getItems().setAll(rejected);
            combo.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Material item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTitle() + " - " + (item.getReviewComment() != null ? item.getReviewComment() : "No comment"));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Notifications ─────────────────────────────────────────────────────────
    private void refreshNotifications() {
        try {
            notificationList.getItems().setAll(NotificationDAO.getNotificationsForUser(currentUser.getUserId()));
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
        for (Notification n : notificationList.getItems()) {
            if (!n.isRead()) markAsRead(n);
        }
    }

    private void setupNotificationClient() {
        notificationClient = new NotificationClient(
            "localhost", 5000, currentUser.getUserId(), "teacher",
            msg -> Platform.runLater(this::loadData)
        );
        new Thread(notificationClient).start();
    }

    public void cleanup() {
        if (notificationClient != null) notificationClient.disconnect();
    }

    // ── Helper methods ────────────────────────────────────────────────────────
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
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

    public BorderPane getView() { return view; }
}