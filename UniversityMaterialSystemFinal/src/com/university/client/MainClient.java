package com.university.client;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainClient extends Application {

    @Override
    public void start(Stage stage) {
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}