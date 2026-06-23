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
    
    public void start(Stage stage) {
        this.serverConnection = new ServerConnection("localhost", 8888);
        
        // Create UI components
        Label titleLabel = new Label("📚 University Material Sharing System");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label subtitleLabel = new Label("Login to access learning materials");
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
        
        Label studentIdLabel = new Label("Student ID:");
        studentIdLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        studentIdField = new TextField();
        studentIdField.setPromptText("Enter your student ID");
        studentIdField.setPrefWidth(280);
        studentIdField.setStyle("-fx-padding: 10; -fx-font-size: 14px;");
        
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefWidth(280);
        passwordField.setStyle("-fx-padding: 10; -fx-font-size: 14px;");
        
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 15px; -fx-padding: 10 30; -fx-cursor: hand;");
        loginButton.setOnAction(e -> handleLogin(stage));
        
        passwordField.setOnAction(e -> handleLogin(stage));
        
        // Loading indicator (hidden initially)
        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(false);
        loadingIndicator.setMaxSize(40, 40);
        
        // Layout
        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.getChildren().addAll(
            studentIdLabel, studentIdField,
            passwordLabel, passwordField,
            loginButton
        );
        
        VBox mainBox = new VBox(25);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setPadding(new Insets(40));
        mainBox.getChildren().addAll(titleLabel, subtitleLabel, formBox, loadingIndicator);
        mainBox.setStyle("-fx-background-color: linear-gradient(to bottom, #ecf0f1, #bdc3c7);");
        
        Scene scene = new Scene(mainBox, 900, 600);
        stage.setTitle("Login - University Material System");
        stage.setScene(scene);
        stage.show();
    }
    
    private void handleLogin(Stage stage) {
        String studentId = studentIdField.getText().trim();
        String password = passwordField.getText();
        
        if (studentId.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both Student ID and Password", Alert.AlertType.ERROR);
            return;
        }
        
        // Disable login button and show loading
        Button loginBtn = (Button) ((VBox) stage.getScene().getRoot()).getChildren().get(2);
        ProgressIndicator loadingIndicator = (ProgressIndicator) ((VBox) stage.getScene().getRoot()).getChildren().get(3);
        
        loginBtn.setDisable(true);
        loginBtn.setText("Logging in...");
        loadingIndicator.setVisible(true);
        
        new Thread(() -> {
            try {
                User user = serverConnection.login(studentId, password);
                
                Platform.runLater(() -> {
                    loginBtn.setDisable(false);
                    loginBtn.setText("Login");
                    loadingIndicator.setVisible(false);
                    
                    if (user != null) {
                        showAlert("Success", "Welcome " + user.getName() + "!", Alert.AlertType.INFORMATION);
                        
                        if (user.isAdmin()) {
                            AdminDashboard dashboard = new AdminDashboard(serverConnection, user);
                            dashboard.start(stage);
                        } else {
                            StudentDashboard dashboard = new StudentDashboard(serverConnection, user);
                            dashboard.start(stage);
                        }
                    } else {
                        showAlert("Login Failed", "Invalid student ID or password", Alert.AlertType.ERROR);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    loginBtn.setDisable(false);
                    loginBtn.setText("Login");
                    loadingIndicator.setVisible(false);
                    showAlert("Connection Error", "Cannot connect to server: " + e.getMessage(), Alert.AlertType.ERROR);
                });
            }
        }).start();
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}