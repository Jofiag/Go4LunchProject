package com.example.go4lunchproject.data.api;

public class NotificationSettingApi {
    private boolean isOn = false;
    private static NotificationSettingApi INSTANCE;

    public static NotificationSettingApi getInstance(){
        if (INSTANCE == null)
            INSTANCE = new NotificationSettingApi();

        return INSTANCE;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }
}
