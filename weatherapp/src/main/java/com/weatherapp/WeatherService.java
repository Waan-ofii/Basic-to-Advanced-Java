package com.weatherapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherService {

    public WeatherData getWeather(String city) {

        try {

            // Build API URL
            String urlString =
                    APIConfig.BASE_URL
                            + "?q=" + city
                            + "&appid=" + APIConfig.API_KEY
                            + "&units=metric";

            // Create URL
            URL url = new URL(urlString);

            // Open connection
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            // Read API response
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream()
                            )
                    );

            StringBuilder response =
                    new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            // Convert to JSON
            JsonObject jsonObject =
                    JsonParser.parseString(
                            response.toString()
                    ).getAsJsonObject();

            // Extract city name
            String cityName =
                    jsonObject.get("name").getAsString();

            // Extract temperature and humidity
            JsonObject mainObject =
                    jsonObject.getAsJsonObject("main");

            double temperature =
                    mainObject.get("temp").getAsDouble();

            int humidity =
                    mainObject.get("humidity").getAsInt();

            // Extract weather description
            String description =
                    jsonObject.getAsJsonArray("weather")
                            .get(0)
                            .getAsJsonObject()
                            .get("description")
                            .getAsString();

            // Extract wind speed
            double windSpeed =
                    jsonObject.getAsJsonObject("wind")
                            .get("speed")
                            .getAsDouble();

            // Return object
            return new WeatherData(
                    cityName,
                    temperature,
                    humidity,
                    description,
                    windSpeed
            );

        } catch (Exception e) {

            System.out.println("Error: "
                    + e.getMessage());

            return null;
        }
    }
}