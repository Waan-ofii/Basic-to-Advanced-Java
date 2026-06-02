package com.university.client;

import com.university.shared.User;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginScreen {

    private TextField studentIdField;
    private PasswordField passwordField;
    private ServerConnection serverConnection;
    private Button loginButton;  // ← MOVED TO CLASS LEVEL (FIX)
    private Stage primaryStage;

    public void start(Stage stage) {
        this.primaryStage = stage;
        this.serverConnection = new ServerConnection("localhost", 8888);

        Label titleLabel = new Label("📚 University Material Sharing System");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitleLabel = new Label("Login to access learning materials");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        Label studentIdLabel = new Label("Student ID:");
        studentIdField = new TextField();
        studentIdField.setPromptText("Enter your student ID");
        studentIdField.setPrefWidth(250);

        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefWidth(250);

        loginButton = new Button("Login");  // ← NOW A CLASS FIELD
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        loginButton.setOnAction(e -> handleLogin());

        passwordField.setOnAction(e -> handleLogin());

        // Loading indicator
        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(false);
        loadingIndicator.setMaxSize(40, 40);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30));
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.add(studentIdLabel, 0, 0);
        grid.add(studentIdField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(loginButton, 1, 2);

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getChildren().addAll(titleLabel, subtitleLabel, grid, loadingIndicator);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #ecf0f1, #bdc3c7);");

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Login - University Material System");
        stage.setScene(scene);
        stage.show();
    }

    private void handleLogin() {
        String studentId = studentIdField.getText().trim();
        String password = passwordField.getText();

        if (studentId.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both Student ID and Password", Alert.AlertType.ERROR);
            return;
        }

        // Disable login button and show loading
        loginButton.setDisable(true);
        loginButton.setText("Logging in...");

        // Get loading indicator from scene
        VBox root = (VBox) primaryStage.getScene().getRoot();
        ProgressIndicator loadingIndicator = (ProgressIndicator) root.getChildren().get(3);
        loadingIndicator.setVisible(true);

        new Thread(() -> {
            try {
                User user = serverConnection.login(studentId, password);

                Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    loginButton.setText("Login");
                    loadingIndicator.setVisible(false);

                    if (user != null) {
                        showAlert("Success", "Welcome " + user.getName() + "!", Alert.AlertType.INFORMATION);
                        showDashboard(user);
                    } else {
                        showAlert("Login Failed", "Invalid student ID or password", Alert.AlertType.ERROR);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    loginButton.setText("Login");
                    loadingIndicator.setVisible(false);
                    showAlert("Connection Error", "Cannot connect to server: " + e.getMessage(), Alert.AlertType.ERROR);
                });
            }
        }).start();
    }

    private void showDashboard(User user) {
        // Simple dashboard for now
        Label welcomeLabel = new Label("Welcome, " + user.getName() + "!");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label infoLabel = new Label("Role: " + user.getRole() + " | Department: " + user.getDepartment() + " | Year: " + user.getBatch());
        infoLabel.setStyle("-fx-font-size: 14px;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> {
            serverConnection.logout();
            start(primaryStage);
        });

        VBox root = new VBox(20, welcomeLabel, infoLabel, logoutBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dashboard");
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}