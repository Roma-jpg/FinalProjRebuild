package ru.romeo558.myprojectrebuild;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Load the saved settings
        loadSettings();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save the current settings when the activity is paused
        saveSettings();
    }

    private void loadSettings() {
        // Load the saved settings from SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Example code to load a boolean setting
        boolean isNotificationEnabled = prefs.getBoolean("notification_enabled", true);
        // Example code to load a string setting
        String userName = prefs.getString("user_name", "");
        // Update the UI with the loaded settings
        // ...
    }

    private void saveSettings() {
        // Save the current settings to SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        // Example code to save a boolean setting
        boolean isNotificationEnabled = true;
        editor.putBoolean("notification_enabled", isNotificationEnabled);
        // Example code to save a string setting
        String userName = "John";
        editor.putString("user_name", userName);
        // Commit the changes
        editor.apply();
    }
}
