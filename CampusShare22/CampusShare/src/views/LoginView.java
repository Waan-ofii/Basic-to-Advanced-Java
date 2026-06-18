package views;

import application.CampusShareApp;
import models.User;
import dao.UserDAO;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.util.Duration;

public class LoginView {
    private CampusShareApp app;
    private VBox view;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label messageLabel;
    private Button loginBtn;

    public LoginView(CampusShareApp app) {
        this.app = app;
        createView();
    }

    private void createView() {
        // ── Outer container with gradient background ──
        view = new VBox();
        view.setAlignment(Pos.CENTER);
        view.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #0f0c29, #302b63, #24243e);"
        );

        // ── Decorative accent circles ──
        StackPane canvas = new StackPane();
        canvas.setAlignment(Pos.CENTER);

        Circle c1 = decorCircle(320, Color.web("#6366f1", 0.18));
        Circle c2 = decorCircle(200, Color.web("#818cf8", 0.14));
        StackPane.setAlignment(c1, Pos.TOP_LEFT);
        StackPane.setAlignment(c2, Pos.BOTTOM_RIGHT);
        c1.setTranslateX(-80); c1.setTranslateY(-80);
        c2.setTranslateX(80);  c2.setTranslateY(80);

        // ── Central content column ──
        VBox center = new VBox(32);
        center.setAlignment(Pos.CENTER);
        center.setMaxWidth(460);

        // Logo / title area
        VBox titleBox = new VBox(6);
        titleBox.setAlignment(Pos.CENTER);

        Label icon = new Label("🎓");
        icon.setFont(Font.font(48));
        icon.setStyle("-fx-text-fill: white;");

        Label title = new Label("CampusShare");
        title.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 42));
        title.setStyle("-fx-text-fill: white; -fx-font-weight: 800;");

        Label sub = new Label("Departmental Material Sharing System");
        sub.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 15));
        sub.setStyle("-fx-text-fill: #c7d2fe;");

        titleBox.getChildren().addAll(icon, title, sub);

        // ── Login Card ──
        VBox card = new VBox(18);
        card.setMaxWidth(420);
        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.07);" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: rgba(255,255,255,0.15);" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 36 32 36 32;"
        );
        GaussianBlur blur = new GaussianBlur(0);
        card.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.web("#000000", 0.35), 30, 0, 0, 8));

        Label cardTitle = new Label("Sign in to your account");
        cardTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        cardTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        // Username
        VBox userGroup = fieldGroup("👤  Username", usernameField = styledField("e.g. john_student", false));

        // Password
        passwordField = new PasswordField();
        passwordField.setPromptText("••••••••");
        passwordField.setPrefHeight(44);
        passwordField.setStyle(inputStyle());
        VBox passGroup = fieldGroup("🔒  Password", passwordField);

        // Error label
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: #fca5a5; -fx-font-size: 13px;");
        messageLabel.setWrapText(true);

        // Login button
        loginBtn = new Button("SIGN IN");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(46);
        loginBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #6366f1, #818cf8);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(99,102,241,0.45), 10, 0, 0, 4);"
        );
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #4f46e5, #6366f1);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(99,102,241,0.60), 14, 0, 0, 5);"
        ));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #6366f1, #818cf8);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(99,102,241,0.45), 10, 0, 0, 4);"
        ));
        loginBtn.setOnAction(e -> handleLogin());

        // Allow Enter key on password field
        passwordField.setOnAction(e -> handleLogin());
        usernameField.setOnAction(e -> passwordField.requestFocus());

        // Register link
        HBox registerRow = new HBox(6);
        registerRow.setAlignment(Pos.CENTER);
        Label noAcct = new Label("Don't have an account?");
        noAcct.setStyle("-fx-text-fill: #94a3b8;");
        Hyperlink reg = new Hyperlink("Create one →");
        reg.setStyle("-fx-text-fill: #a5b4fc; -fx-underline: false; -fx-cursor: hand; -fx-font-weight: bold; -fx-border-width: 0;");
        reg.setOnAction(e -> app.showRegisterScreen());
        registerRow.getChildren().addAll(noAcct, reg);

        card.getChildren().addAll(cardTitle, userGroup, passGroup, messageLabel, loginBtn, registerRow);
        center.getChildren().addAll(titleBox, card);
        canvas.getChildren().addAll(c1, c2, center);

        VBox.setVgrow(canvas, Priority.ALWAYS);
        view.getChildren().add(canvas);

        // Entrance fade animation
        center.setOpacity(0);
        center.setTranslateY(20);
        FadeTransition ft = new FadeTransition(Duration.millis(600), center);
        ft.setFromValue(0); ft.setToValue(1);
        TranslateTransition tt = new TranslateTransition(Duration.millis(600), center);
        tt.setFromY(20); tt.setToY(0);
        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.setDelay(Duration.millis(100));
        pt.play();
    }

    // ── Helpers ─────────────────────────────────────────────────────────────
    private Circle decorCircle(double r, Color fill) {
        Circle c = new Circle(r, fill);
        c.setEffect(new GaussianBlur(60));
        return c;
    }

    private TextField styledField(String prompt, boolean isPassword) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setPrefHeight(44);
        f.setStyle(inputStyle());
        return f;
    }

    private VBox fieldGroup(String labelText, Control input) {
        VBox g = new VBox(6);
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-text-fill: #cbd5e1; -fx-font-weight: bold; -fx-font-size: 12px;");
        g.getChildren().addAll(lbl, input);
        return g;
    }

    private String inputStyle() {
        return  "-fx-background-color: rgba(255,255,255,0.10);" +
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: rgba(255,255,255,0.40);" +
                "-fx-border-color: rgba(255,255,255,0.18);" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-border-width: 1.5;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 10 14 10 14;";
    }

    // ── Login logic ──────────────────────────────────────────────────────────
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            shake(loginBtn);
            messageLabel.setText("⚠  Please enter your username and password.");
            return;
        }

        messageLabel.setText("");
        try {
            User user = UserDAO.validateLogin(username, password);
            if (user != null) {
                app.loginSuccess(user);
            } else {
                shake(loginBtn);
                messageLabel.setText("✕  Invalid username or password.");
            }
        } catch (Exception ex) {
            messageLabel.setText("✕  Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /** Brief horizontal shake animation for error feedback */
    private void shake(javafx.scene.Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(60), node);
        tt.setByX(8); tt.setCycleCount(6); tt.setAutoReverse(true);
        tt.play();
    }

    public VBox getView() { return view; }
}