package com.example.bladerunnerwidget;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFetcher {
    private static final String TAG = "WeatherFetcher";
    private static final String API_KEY = BuildConfig.WEATHER_API_KEY;
    // Fixed query for Chennai as requested
    private static final String CITY_QUERY = "Chennai,IN"; 
    private static final String URL_STRING = "https://api.openweathermap.org/data/2.5/weather?q=" + CITY_QUERY + "&appid=" + API_KEY + "&units=metric";

    public interface WeatherCallback {
        void onWeatherFetched(String temp, String condition);
    }

    public static void fetchTemperature(final WeatherCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "Fetching weather for: " + CITY_QUERY);
                    URL url = new URL(URL_STRING);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();

                    JSONObject json = new JSONObject(result.toString());
                    
                    // Parse Temperature
                    JSONObject main = json.getJSONObject("main");
                    double temp = main.getDouble("temp");

                    // Parse Condition
                    JSONArray weatherArray = json.getJSONArray("weather");
                    String condition = "Clear";
                    if (weatherArray.length() > 0) {
                        condition = weatherArray.getJSONObject(0).getString("main");
                    }

                    callback.onWeatherFetched(String.format("%.0f°C", temp), condition);

                } catch (Exception e) {
                    Log.e(TAG, "Error fetching weather", e);
                    callback.onWeatherFetched("--°C", "Clear");
                }
            }
        }).start();
    }
}
