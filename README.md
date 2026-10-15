<img width="300" alt="Screenshot_20261016_012244" src="https://github.com/user-attachments/assets/a0774564-9f73-405f-bfaa-abd9969d14f5" />
# Blade Runner Android Widget

A sleek, minimalist Android home screen widget inspired by the aesthetics of Blade Runner. It features a digital clock, the current date, and real-time weather updates with custom icons.

## Features
*   **Digital Clock:** Displays the current time in a 12-hour format using a custom digital font (`ds_digital_italic.ttf`).
*   **Live Weather:** Fetches real-time temperature and weather conditions (Clear, Rain, Clouds, etc.) using the OpenWeatherMap API.
*   **Custom Weather Icons:** Dynamically updates a minimalist icon based on the current weather condition.
*   **Manual Refresh:** Includes a built-in refresh button on the widget to force a manual time and weather update.
*   **Auto-Update:** Uses `AlarmManager` to ensure the time stays synchronized every minute.

## Prerequisites

To build and run this project, you will need:
*   Android Studio (latest version recommended)
*   An OpenWeatherMap API Key

## Setup Instructions

**1. Clone the repository**
```bash
git clone https://github.com/Abishek0404/Bladerunner-Android-widget.git
```

**2. Add your API Key**
For security reasons, the OpenWeatherMap API key is not included in the source code. You must add it to your local environment.
*   Open the project in Android Studio.
*   Locate or create a file named `local.properties` in the root directory of the project.
*   Add the following line to `local.properties`, replacing the placeholder with your actual API key:
```properties
WEATHER_API_KEY=your_openweathermap_api_key_here
```
*   Sync the project with Gradle files.

**3. Build and Run**
*   Connect your Android device or start an emulator.
*   Click the "Run" button in Android Studio.

**4. Add the Widget to your Home Screen**
*   Long press on an empty space on your Android home screen.
*   Select "Widgets".
*   Scroll down to find "BladeRunnerWidget".
*   Drag and drop the widget onto your home screen.

## Technologies Used
*   Java
*   Android AppWidgets (RemoteViews)
*   HttpURLConnection (for API calls)
*   AlarmManager (for background scheduling)
