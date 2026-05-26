package com.weatherapp;

public class WeatherData {

    private String city;
    private double temperature;
    private int humidity;
    private String description;
    private double windSpeed;
    // Unix timestamps (UTC seconds) for sunrise/sunset
    private long sunrise;
    private long sunset;
    // Current time at location (Unix UTC seconds)
    private long currentTime;

    // Constructor
    public WeatherData(String city,
                       double temperature,
                       int humidity,
                       String description,
                       double windSpeed,
                       long sunrise,
                       long sunset,
                       long currentTime) {

        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.description = description;
        this.windSpeed = windSpeed;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.currentTime = currentTime;
    }

    // Getters
    public String getCity() { return city; }
    public double getTemperature() { return temperature; }
    public int getHumidity() { return humidity; }
    public String getDescription() { return description; }
    public double getWindSpeed() { return windSpeed; }
    public long getSunrise() { return sunrise; }
    public long getSunset() { return sunset; }
    public long getCurrentTime() { return currentTime; }

    /** Returns true if it is currently daytime at the location. */
    public boolean isDay() {
        return currentTime >= sunrise && currentTime < sunset;
    }
}