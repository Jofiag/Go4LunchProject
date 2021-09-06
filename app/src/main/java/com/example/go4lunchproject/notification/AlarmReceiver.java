package com.example.go4lunchproject.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.go4lunchproject.R;
import com.example.go4lunchproject.controller.HomepageActivity;
import com.example.go4lunchproject.data.api.RestaurantSelectedApi;
import com.example.go4lunchproject.data.firebase.FirebaseCloudDatabase;
import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.model.UserSettings;
import com.example.go4lunchproject.model.Workmate;
import com.example.go4lunchproject.util.Constants;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseCloudDatabase firebaseCloudDatabase = FirebaseCloudDatabase.getInstance();
        String userId = firebaseCloudDatabase.getCurrentUserName() + "_" + firebaseCloudDatabase.getCurrentFirebaseUser().getUid();

        createNotificationChannelIfNeeded(context);

        firebaseCloudDatabase.listenToUser(userId, singleUser -> {
            if (singleUser != null){
                Restaurant restaurant = singleUser.getRestaurantChosen();
                if (restaurant != null){
                    String contentText = getNotificationContent(restaurant);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setContentTitle(Constants.RESTAURANT_NOTIFICATION_TITLE)
                            .setContentText(contentText)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                            .setPriority(NotificationCompat.PRIORITY_MAX);

                    setTapActionIntent(context, builder, restaurant);

                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

                    UserSettings userSettings = singleUser.getUserSettings();
                    if (userSettings != null){
                        if (userSettings.isNotificationOn())
                            notificationManagerCompat.notify(Constants.RESTAURANT_NOTIFICATION_ID, builder.build());

                    }
                }
            }

        });

    }

    private void createNotificationChannelIfNeeded(Context context){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(Constants.CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private String getNotificationContent(Restaurant restaurant){
        StringBuilder notificationContent = new StringBuilder();

        if (restaurant != null){
            notificationContent.append(restaurant.getName()).append("\n")
                    .append(restaurant.getAddress()).append("\n");

            List<Workmate> list = restaurant.getWorkmateList();

            if (list != null && !list.isEmpty()) {
                StringBuilder workmatesName = new StringBuilder();

                for (Workmate workmate : list)
                    workmatesName.append(workmate.getName()).append(" is joining").append("\n");

                notificationContent.append(workmatesName);
            }
        }

        return notificationContent.toString();
    }

    private void setTapActionIntent(Context context, NotificationCompat.Builder builder, Restaurant restaurant){
        RestaurantSelectedApi.getInstance().setRestaurantSelected(restaurant);

        Intent tapActionIntent = new Intent(context.getApplicationContext(), HomepageActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        tapActionIntent.putExtra("from notification",true);
        stackBuilder.addNextIntentWithParentStack(tapActionIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);    // true : The notification will go when the user clicks on it
    }


}
