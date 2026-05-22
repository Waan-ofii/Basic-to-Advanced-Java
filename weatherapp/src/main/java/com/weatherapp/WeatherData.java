package com.weatherapp;

public class WeatherData {

    private String city;
    private double temperature;
    private int humidity;
    private String description;
    private double windSpeed;

    // Constructor
    public WeatherData(String city,
                       double temperature,
                       int humidity,
                       String description,
                       double windSpeed) {

        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.description = description;
        this.windSpeed = windSpeed;
    }

    // Getters
    public String getCity() {
        return city;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public String getDescription() {
        return description;
    }

    public double getWindSpeed() {
        return windSpeed;
    }
}