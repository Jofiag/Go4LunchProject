package com.example.go4lunchproject777.model;

public class UserSettings {
    private boolean isNotificationOn;
    private String sortListOption;

    public UserSettings() {
    }

    public UserSettings(boolean isNotificationOn) {
        this.isNotificationOn = isNotificationOn;
    }

    public UserSettings(boolean isNotificationOn, String sortListOption) {
        this.isNotificationOn = isNotificationOn;
        this.sortListOption = sortListOption;
    }

    public boolean isNotificationOn() {
        return isNotificationOn;
    }
    public void setNotificationOn(boolean notificationSate) {
        this.isNotificationOn = notificationSate;
    }

    public String getSortListOption() {
        return sortListOption;
    }
    public void setSortListOption(String sortListBy) {
        this.sortListOption = sortListBy;
    }
}
