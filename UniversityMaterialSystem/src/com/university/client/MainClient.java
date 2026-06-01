package com.university.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class MainClient extends Application {
    
    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        
        // Show login screen
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.start(stage);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}