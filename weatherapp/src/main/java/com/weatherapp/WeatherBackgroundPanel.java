package com.weatherapp;

import javax.swing.*;
import java.awt.*;

public class WeatherBackgroundPanel extends JPanel {

    private Image backgroundImage;

    public WeatherBackgroundPanel() {

        setWeatherBackground("clear");

    }

    // ================= CHANGE BACKGROUND =================
    public void setWeatherBackground(String condition) {

        if (condition == null) {
            condition = "clear";
        }

        condition = condition.toLowerCase();

        String imageName;

        // ================= WEATHER CONDITIONS =================

        if (condition.contains("rain")) {

            imageName = "rainy.jpg";

        }
        else if (condition.contains("cloud")) {

            imageName = "cloudy.jpg";

        }
        else if (condition.contains("storm")
                || condition.contains("thunder")) {

            imageName = "storm.jpg";

        }
        else if (condition.contains("mist")
                || condition.contains("fog")
                || condition.contains("haze")) {

            imageName = "fog.jpg";

        }
        else if (condition.contains("night")) {

            imageName = "night.jpg";

        }
        else {

            imageName = "sunny.jpg";

        }

        // ================= LOAD IMAGE =================
        try {

            ImageIcon icon =
                    new ImageIcon(
                            getClass()
                                    .getResource("/images/" + imageName)
                    );

            backgroundImage = icon.getImage();

        }
        catch (Exception e) {

            System.out.println(
                    "Image not found: " + imageName
            );

        }

        repaint();
    }

    // ================= DRAW BACKGROUND =================
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (backgroundImage != null) {

            g.drawImage(
                    backgroundImage,
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    this
            );

            // Dark overlay for readability
            g.setColor(new Color(0, 0, 0, 80));

            g.fillRect(
                    0,
                    0,
                    getWidth(),
                    getHeight()
            );

        } else {

            // fallback background
            g.setColor(new Color(70, 130, 180));

            g.fillRect(
                    0,
                    0,
                    getWidth(),
                    getHeight()
            );

        }
    }
}