package com.example.go4lunchproject.model;

public class UserSettings {
    private boolean isNotificationOn;
    private String sortListBy;


    public boolean isNotificationOn() {
        return isNotificationOn;
    }
    public void setNotificationOn(boolean notificationSate) {
        this.isNotificationOn = notificationSate;
    }

    public String getSortListBy() {
        return sortListBy;
    }
    public void setSortListOption(String sortListBy) {
        this.sortListBy = sortListBy;
    }
}
