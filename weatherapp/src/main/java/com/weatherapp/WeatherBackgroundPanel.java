package com.weatherapp;

import javax.swing.*;
import java.awt.*;

public class WeatherBackgroundPanel extends JPanel {

    private Image backgroundImage;
    // Fallback gradient colors used when image fails to load
    private Color topColor    = new Color(70, 130, 180);
    private Color bottomColor = new Color(30,  80, 120);

    public WeatherBackgroundPanel() {
        setWeatherBackground("clear", true);
    }

    /**
     * Picks the right background image based on weather condition and day/night.
     * Available images: sun.jpg, cloud.jpg, rain.jpg, fog.jpg, night.jpg
     *
     * @param condition weather description from API
     * @param isDay     true if it is currently daytime at the location
     */
    public void setWeatherBackground(String condition, boolean isDay) {

        if (condition == null) condition = "clear";
        condition = condition.toLowerCase();

        String imageName;

        if (!isDay) {
            // Always use night image after sunset
            imageName = "night.jpg";
            topColor    = new Color(10, 10, 50);
            bottomColor = new Color(25, 25, 80);
        } else if (condition.contains("rain") || condition.contains("drizzle")) {
            imageName = "rain.jpg";
            topColor    = new Color(80,  90, 110);
            bottomColor = new Color(50,  60,  80);
        } else if (condition.contains("cloud")) {
            imageName = "cloud.jpg";
            topColor    = new Color(140, 160, 190);
            bottomColor = new Color(100, 120, 160);
        } else if (condition.contains("mist") || condition.contains("fog") || condition.contains("haze")) {
            imageName = "fog.jpg";
            topColor    = new Color(180, 185, 195);
            bottomColor = new Color(130, 140, 155);
        } else {
            // Clear / sunny / storm — default to sun
            imageName = "sun.jpg";
            topColor    = new Color(100, 180, 255);
            bottomColor = new Color(30,  120, 210);
        }

        // ================= LOAD IMAGE FROM CLASSPATH =================
        try {
            java.net.URL imgUrl = getClass().getResource("/images/" + imageName);
            if (imgUrl != null) {
                backgroundImage = new ImageIcon(imgUrl).getImage();
            } else {
                System.out.println("Image not found on classpath: /images/" + imageName);
                backgroundImage = null;
            }
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
            backgroundImage = null;
        }

        repaint();
    }

    // ================= DRAW BACKGROUND =================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            // Draw the image stretched to fill the panel
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

            // Dark overlay so text stays readable
            g.setColor(new Color(0, 0, 0, 80));
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            // Gradient fallback when image is unavailable
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setPaint(new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
