package application;

import views.*;
import models.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CampusShareApp extends Application {
    
    private Stage primaryStage;
    private Scene loginScene;
    private Scene registerScene;
    private Scene studentDashboardScene;
    private Scene teacherDashboardScene;
    private Scene adminDashboardScene;
    
    private User currentUser;
    
    @Override
    public void start(Stage primaryStage) {
        services.NotificationServer.startServer();
        this.primaryStage = primaryStage;
        primaryStage.setTitle("CampusShare - Departmental Material Sharing System");
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(768);
        
        // Show login screen first
        showLoginScreen();
        
        primaryStage.show();
    }
    
    private void applyStylesheet(Scene scene) {
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("Could not load CSS: " + e.getMessage());
        }
    }

    public void showLoginScreen() {
        LoginView loginView = new LoginView(this);
        loginScene = new Scene(loginView.getView(), 1024, 768);
        applyStylesheet(loginScene);
        primaryStage.setScene(loginScene);
    }
    
    public void showRegisterScreen() {
        RegisterView registerView = new RegisterView(this);
        registerScene = new Scene(registerView.getView(), 1024, 768);
        applyStylesheet(registerScene);
        primaryStage.setScene(registerScene);
    }
    
    public void showBrowseMaterials(views.BrowseMaterialsView browseMaterials) {
        Scene browseScene = new Scene(browseMaterials.getView(), 1024, 768);
        applyStylesheet(browseScene);
        primaryStage.setScene(browseScene);
    }
    
    public void loginSuccess(User user) {
        this.currentUser = user;
        switch (user.getRole()) {
            case "student":
                showStudentDashboard();
                break;
            case "teacher":
                showTeacherDashboard();
                break;
            case "admin":
                showAdminDashboard();
                break;
        }
    }
    
    private void showStudentDashboard() {
        StudentDashboardView dashboard = new StudentDashboardView(this, currentUser);
        studentDashboardScene = new Scene(dashboard.getView(), 1280, 800);
        applyStylesheet(studentDashboardScene);
        primaryStage.setScene(studentDashboardScene);
    }
    
    private void showTeacherDashboard() {
        TeacherDashboardView dashboard = new TeacherDashboardView(this, currentUser);
        teacherDashboardScene = new Scene(dashboard.getView(), 1280, 800);
        applyStylesheet(teacherDashboardScene);
        primaryStage.setScene(teacherDashboardScene);
    }
    
    private void showAdminDashboard() {
        AdminDashboardView dashboard = new AdminDashboardView(this, currentUser);
        adminDashboardScene = new Scene(dashboard.getView(), 1280, 800);
        applyStylesheet(adminDashboardScene);
        primaryStage.setScene(adminDashboardScene);
    }
    
    public void logout() {
        currentUser = null;
        showLoginScreen();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void stop() throws Exception {
        services.NotificationServer.stopServer();
        super.stop();
    }
}