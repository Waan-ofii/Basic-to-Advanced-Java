package com.weatherapp;

import javax.swing.*;
import java.awt.*;

public class WeatherUI extends JFrame {

    private JTextField cityField;
    private JLabel weatherInfo;

    private WeatherService service;
    private WeatherBackgroundPanel backgroundPanel;

    public WeatherUI() {

        service = new WeatherService();

        // ================= WINDOW =================
        setTitle("Modern Weather App");
        setSize(550, 700);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        // ================= BACKGROUND =================
        backgroundPanel = new WeatherBackgroundPanel();

        backgroundPanel.setLayout(null);

        setContentPane(backgroundPanel);

        // ================= TITLE =================
        JLabel title = new JLabel("Weather Forecast");

        title.setBounds(120, 20, 400, 40);

        title.setFont(new Font("Arial", Font.BOLD, 30));

        title.setForeground(Color.WHITE);

        backgroundPanel.add(title);

        // ================= SEARCH FIELD =================
        cityField = new JTextField();

        cityField.setBounds(90, 90, 250, 40);

        cityField.setFont(new Font("Arial", Font.BOLD, 16));

        cityField.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        backgroundPanel.add(cityField);

        // ================= SEARCH BUTTON =================
        JButton searchButton = new JButton("Search");

        searchButton.setBounds(360, 90, 100, 40);

        searchButton.setFont(new Font("Arial", Font.BOLD, 15));

        searchButton.setBackground(new Color(52, 152, 219));

        searchButton.setForeground(Color.WHITE);

        searchButton.setFocusPainted(false);

        backgroundPanel.add(searchButton);

        // ================= WEATHER CARD =================
        JPanel card = new JPanel();

        card.setLayout(null);

        card.setBounds(50, 180, 430, 350);

        card.setBackground(new Color(0,0,0,170));

        backgroundPanel.add(card);

        // ================= WEATHER INFO =================
        weatherInfo = new JLabel();

        weatherInfo.setBounds(20, 20, 380, 300);

        weatherInfo.setFont(new Font("Arial", Font.BOLD, 18));

        weatherInfo.setForeground(Color.WHITE);

        card.add(weatherInfo);

        // ================= BUTTON ACTION =================
        searchButton.addActionListener(e -> {

            String city = cityField.getText().trim();

            if (city.isEmpty()) {

                weatherInfo.setText(
                        "<html>Please enter city name.</html>"
                );

                return;
            }

            WeatherData data = service.getWeather(city);

            if (data != null) {

                // CHANGE BACKGROUND
                backgroundPanel.setWeather(data.getDescription());

                // UPDATE WEATHER INFO
                weatherInfo.setText(
                        "<html>" +

                                "<h1 style='color:white;'>" +
                                data.getCity() +
                                "</h1>" +

                                "<p style='font-size:18px;'>" +

                                "🌡 Temperature: <b>" +
                                data.getTemperature() +
                                " °C</b><br><br>" +

                                "💧 Humidity: <b>" +
                                data.getHumidity() +
                                "%</b><br><br>" +

                                "🌥 Condition: <b>" +
                                data.getDescription() +
                                "</b><br><br>" +

                                "💨 Wind Speed: <b>" +
                                data.getWindSpeed() +
                                " m/s</b>" +

                                "</p>" +

                                "</html>"
                );

            } else {

                weatherInfo.setText(
                        "<html>City not found or API error.</html>"
                );

            }

        });

        // IMPORTANT
        setVisible(true);
    }
}