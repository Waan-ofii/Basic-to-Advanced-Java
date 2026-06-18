package views;

import application.CampusShareApp;
import models.User;
import models.Department;
import dao.UserDAO;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import java.util.List;

public class RegisterView {
    private CampusShareApp app;
    private VBox view;
    private TextField usernameField, fullNameField, emailField;
    private PasswordField passwordField, confirmPasswordField;
    private ComboBox<String> deptCombo;
    private ComboBox<String> roleCombo;
    private Label messageLabel;
    private List<Department> departments;

    public RegisterView(CampusShareApp app) {
        this.app = app;
        try {
            departments = UserDAO.getAllDepartments();
        } catch (Exception e) {
            e.printStackTrace();
        }
        createView();
    }

    private void createView() {
        view = new VBox(20);
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(40));
        view.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #3498db);");

        // Title
        Label titleLabel = new Label("Create Account");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);

        // Registration Card
        VBox card = new VBox(15);
        card.setMaxWidth(450);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 30;");

        // Form fields
        usernameField = new TextField();
        usernameField.setPromptText("Username*");
        usernameField.setPrefHeight(40);

        fullNameField = new TextField();
        fullNameField.setPromptText("Full Name*");
        fullNameField.setPrefHeight(40);

        emailField = new TextField();
        emailField.setPromptText("Email*");
        emailField.setPrefHeight(40);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password*");
        passwordField.setPrefHeight(40);

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password*");
        confirmPasswordField.setPrefHeight(40);

        // Department ComboBox
        deptCombo = new ComboBox<>();
        deptCombo.setPromptText("Select Department*");
        for (Department dept : departments) {
            deptCombo.getItems().add(dept.getDeptName());
        }
        deptCombo.setPrefHeight(40);

        // Role ComboBox
        roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("student", "teacher");
        roleCombo.setValue("student");
        roleCombo.setPrefHeight(40);

        messageLabel = new Label();
        messageLabel.setTextFill(Color.RED);

        // Register button
        Button registerBtn = new Button("REGISTER");
        registerBtn.setPrefHeight(40);
        registerBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        registerBtn.setOnAction(e -> handleRegister());

        // Back to login link
        HBox loginBox = new HBox(10);
        loginBox.setAlignment(Pos.CENTER);
        Label haveAccountLabel = new Label("Already have an account?");
        Hyperlink loginLink = new Hyperlink("Login Here");
        loginLink.setOnAction(e -> app.showLoginScreen());
        loginBox.getChildren().addAll(haveAccountLabel, loginLink);

        card.getChildren().addAll(usernameField, fullNameField, emailField,
                passwordField, confirmPasswordField,
                deptCombo, roleCombo, messageLabel, registerBtn, loginBox);
        view.getChildren().addAll(titleLabel, card);
    }

    private void handleRegister() {
        // Validation
        if (usernameField.getText().trim().isEmpty() ||
                fullNameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                passwordField.getText().isEmpty() ||
                deptCombo.getValue() == null) {
            messageLabel.setText("Please fill all required fields");
            return;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            messageLabel.setText("Passwords do not match");
            return;
        }

        // Get department ID
        int deptId = 0;
        for (Department dept : departments) {
            if (dept.getDeptName().equals(deptCombo.getValue())) {
                deptId = dept.getDeptId();
                break;
            }
        }

        User user = new User();
        user.setUsername(usernameField.getText().trim());
        user.setFullName(fullNameField.getText().trim());
        user.setEmail(emailField.getText().trim());
        user.setPassword(passwordField.getText());
        user.setRole(roleCombo.getValue());
        user.setDeptId(deptId);

        try {
            if (UserDAO.registerUser(user)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registration Successful");
                alert.setHeaderText(null);
                alert.setContentText("Account created! Please login.");
                alert.showAndWait();
                app.showLoginScreen();
            } else {
                messageLabel.setText("Registration failed. Username may already exist.");
            }
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    public VBox getView() {
        return view;
    }
}