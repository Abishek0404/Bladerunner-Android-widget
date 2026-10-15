package com.example.bladerunnerwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BladeRunnerWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "BladeRunnerWidget";
    private static final String ACTION_UPDATE_TIME = "com.example.bladerunnerwidget.ACTION_UPDATE_TIME";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate called");
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
        scheduleNextUpdate(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "onReceive: " + intent.getAction());
        if (ACTION_UPDATE_TIME.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, BladeRunnerWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            for (int appWidgetId : appWidgetIds) {
                updateWidget(context, appWidgetManager, appWidgetId);
            }
            scheduleNextUpdate(context);
        }
    }

    private void updateWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_blade_runner);
        final ComponentName thisWidget = new ComponentName(context, BladeRunnerWidgetProvider.class);

        // Date update
        SimpleDateFormat dateFmt = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
        views.setTextViewText(R.id.widget_date, dateFmt.format(new Date()).toUpperCase());

        // Time update (Digital Clock) - 12 hour format (without AM/PM)
        SimpleDateFormat timeFmt = new SimpleDateFormat("hh:mm", Locale.US);
        String timeString = timeFmt.format(new Date());
        views.setImageViewBitmap(R.id.widget_time_image, createFontBitmap(context, timeString, 120, "#1A1A1A"));

        // Location hardcoded
        views.setTextViewText(R.id.location_text, "MANAPAKKAM");

        // Refresh Button Intent
        Intent refreshIntent = new Intent(context, BladeRunnerWidgetProvider.class);
        refreshIntent.setAction(ACTION_UPDATE_TIME);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, flags);
        views.setOnClickPendingIntent(R.id.btn_refresh, pendingIntent);

        // Weather update
        WeatherFetcher.fetchTemperature(new WeatherFetcher.WeatherCallback() {
            @Override
            public void onWeatherFetched(String temp, String condition) {
                Log.d(TAG, "Weather callback: " + temp + " (" + condition + ")");
                
                // 1. Digital Temperature Bitmap
                views.setImageViewBitmap(R.id.temp_image, createFontBitmap(context, temp, 80, "#FF3B30"));
                
                // 2. Location (Double-ensure)
                views.setTextViewText(R.id.location_text, "MANAPAKKAM");
                
                // 3. Custom Weather Icon
                views.setImageViewResource(R.id.weather_icon, getWeatherIcon(condition));
                
                appWidgetManager.updateAppWidget(thisWidget, views);
            }
        });

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private int getWeatherIcon(String condition) {
        switch (condition) {
            case "Rain":
            case "Drizzle":
            case "Thunderstorm":
                return R.drawable.ic_weather_rain_lines;
            case "Clouds":
            case "Mist":
            case "Smoke":
            case "Haze":
            case "Dust":
            case "Fog":
                return R.drawable.ic_weather_clouds;
            default: // Clear and others
                return R.drawable.ic_weather_spinner;
        }
    }

    private Bitmap createFontBitmap(Context context, String text, int size, String colorHex) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        try {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/ds_digital_italic.ttf");
            paint.setTypeface(tf);
        } catch (Exception e) {
            Log.e(TAG, "Could not load font", e);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
        }
        
        paint.setTextSize(size); 
        paint.setColor(Color.parseColor(colorHex));
        paint.setTextAlign(Paint.Align.LEFT);

        float baseline = -paint.ascent();
        int width = (int) (paint.measureText(text) + 0.5f);
        int height = (int) (baseline + paint.descent() + 0.5f);

        if (width <= 0) width = 100;
        if (height <= 0) height = 100;

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private void scheduleNextUpdate(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BladeRunnerWidgetProvider.class);
        intent.setAction(ACTION_UPDATE_TIME);
        
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, flags);

        long nextTick = (System.currentTimeMillis() / 60000 + 1) * 60000;
        Log.d(TAG, "Scheduling next update at " + new Date(nextTick));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTick, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextTick, pendingIntent);
        }
    }
}
