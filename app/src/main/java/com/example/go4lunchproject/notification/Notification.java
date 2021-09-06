package com.example.go4lunchproject.notification;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notification {
    private int priority;
    private String title;
    private Context context;
    private String contentText;
    private int smallIconResourceId;
    private boolean isMoreThanOneLine;

    private NotificationCompat.Builder builder;

    private String channelId;
    private String channelName;
    private int channelImportance;
    private String channelDescription;

    private Intent tapActionIntent;

    public Notification() {
    }

    public Notification(Context context) {
        this.context = context;
    }

    public Notification(Context context, int smallIconResourceId, String title, String contentText, int priority, boolean isMoreThanOneLine, Intent tapActionIntent) {
        this.title = title;
        this.context = context;
        this.priority = priority;
        this.contentText = contentText;
        this.tapActionIntent = tapActionIntent;
        this.isMoreThanOneLine = isMoreThanOneLine;
        this.smallIconResourceId = smallIconResourceId;

    }

    public void initializeNotification(){
        if (context != null) {
            setBuilder();
            setTapAction();
            createNotificationChannel();
        }

    }

    public void showNotification(int notificationId){
        if (context != null && builder != null) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(notificationId, builder.build());
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void setTapAction(){
        if (tapActionIntent != null) {
            tapActionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
                pendingIntent = PendingIntent.getActivity(context, 0, tapActionIntent, PendingIntent.FLAG_IMMUTABLE);
            else
                pendingIntent = PendingIntent.getActivity(context, 0, tapActionIntent, 0);

            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);    //The notification will go when the user clicks on it
        }

    }


    //// PRIVATE FUNCTION
    private void setBuilder(){
        if (context != null && title != null && contentText != null) {
            builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(smallIconResourceId)
                    .setContentTitle(title)
                    .setContentText(contentText);

            if (isMoreThanOneLine)
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(contentText));

            builder.setPriority(priority);
        }
    }

    private void createNotificationChannel(){
        //Notification channel is needed for API 26 and above.
        if (channelId != null && channelName != null && channelDescription != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName, channelImportance);
                channel.setDescription(channelDescription);

                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }

    }

    //// NOTIFICATION BUILDER GETTER AND SETTER
    public int getSmallIconResourceId() {
        return smallIconResourceId;
    }
    public void setSmallIconResourceId(int smallIconResourceId) {
        this.smallIconResourceId = smallIconResourceId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentText() {
        return contentText;
    }
    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isMoreThanOneLine() {
        return isMoreThanOneLine;
    }
    public void setMoreThanOneLine(boolean moreThanOneLine) {
        isMoreThanOneLine = moreThanOneLine;
    }

    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }

    public Intent getTapActionIntent() {
        return tapActionIntent;
    }
    public void setTapActionIntent(Intent tapActionIntent) {
        this.tapActionIntent = tapActionIntent;
    }

    //// NOTIFICATION CHANNEL GETTER AND SETTER
    public String getChannelId() {
        return channelId;
    }
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }
    public void setChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
    }

    public int getChannelImportance() {
        return channelImportance;
    }
    public void setChannelImportance(int channelImportance) {
        this.channelImportance = channelImportance;
    }
}
