package com.weatherapp;

import javax.swing.*;
import java.awt.*;

public class WeatherBackgroundPanel extends JPanel {

    private Image backgroundImage;

    public void setWeather(String condition) {

        if (condition == null) return;

        condition = condition.toLowerCase();

        String path;

        if (condition.contains("rain")) {
            path = "src/main/resources/images/rain.jpg";
        }
        else if (condition.contains("cloud")) {
            path = "src/main/resources/images/cloud.jpg";
        }
        else if (condition.contains("mist") || condition.contains("fog")) {
            path = "src/main/resources/images/fog.jpg";
        }
        else {
            path = "src/main/resources/images/sunny.jpg";
        }

        backgroundImage = new ImageIcon(path).getImage();

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (backgroundImage != null) {

            g.drawImage(backgroundImage,
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    this);

        } else {

            g.setColor(new Color(135, 206, 235));
            g.fillRect(0, 0, getWidth(), getHeight());

        }
    }
}