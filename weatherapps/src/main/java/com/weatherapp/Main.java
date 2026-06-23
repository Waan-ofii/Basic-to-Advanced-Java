package com.weatherapp;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        // Launch GUI safely
        SwingUtilities.invokeLater(() -> {

            new WeatherUI();

        });

    }
}