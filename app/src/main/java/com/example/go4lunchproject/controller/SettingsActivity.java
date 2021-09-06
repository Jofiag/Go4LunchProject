package com.example.go4lunchproject.controller;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.go4lunchproject.R;
import com.example.go4lunchproject.data.api.NotificationSettingApi;

public class SettingsActivity extends AppCompatActivity {
    private SwitchCompat switchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setReferences();

        manageNotifications();
    }

    private void setReferences() {
        switchButton = findViewById(R.id.notification_switch_button);
    }

    private void manageNotifications(){
        NotificationSettingApi.getInstance().setOn(switchButton.isChecked());
    }
}