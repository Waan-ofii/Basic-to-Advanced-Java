package com.university.client.ui;

import com.university.client.network.ServerConnection;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginScreen extends Application {
    private TextField studentIdField;
    private PasswordField passwordField;
    private ServerConnection serverConnection;
    
    @Override
    public void start(Stage primaryStage) {
        serverConnection = new ServerConnection("localhost", 8888);
        
        // Create UI components
        Label titleLabel = new Label("University Material Sharing System");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label studentIdLabel = new Label("Student ID:");
        studentIdField = new TextField();
        studentIdField.setPromptText("Enter your student ID");
        
        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px;");
        loginButton.setOnAction(e -> handleLogin(primaryStage));
        
        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px;");
        registerButton.setOnAction(e -> showRegistrationScreen());
        
        // Layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30));
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.add(studentIdLabel, 0, 0);
        grid.add(studentIdField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        
        HBox buttonBox = new HBox(10, loginButton, registerButton);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 1, 2);
        
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(titleLabel, grid);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #ecf0f1, #bdc3c7);");
        
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("University Material Sharing System - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void handleLogin(Stage stage) {
        String studentId = studentIdField.getText();
        String password = passwordField.getText();
        
        if (studentId.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both Student ID and Password", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            boolean success = serverConnection.login(studentId, password);
            
            if (success) {
                showAlert("Success", "Login successful!", Alert.AlertType.INFORMATION);
                DashboardScreen dashboard = new DashboardScreen(serverConnection);
                dashboard.start(stage);
            } else {
                showAlert("Error", "Invalid credentials. Please try again.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Error", "Connection failed: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void showRegistrationScreen() {
        // Implement registration screen
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration");
        alert.setHeaderText("Registration Feature");
        alert.setContentText("Please contact your department administrator for registration.");
        alert.showAndWait();
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}