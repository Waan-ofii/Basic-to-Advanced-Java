package com.university.client;

/**
 * Separate launcher class that does NOT extend Application.
 * This is required for Java 11+ to avoid the JavaFX runtime bootstrap issue.
 * Set this as the Main Class in your run configuration, not MainClient.
 */
public class Launcher {
    public static void main(String[] args) {
        MainClient.main(args);
    }
}
