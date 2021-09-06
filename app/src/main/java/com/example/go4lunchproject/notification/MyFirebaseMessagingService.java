package com.example.go4lunchproject.notification;

import android.app.AlarmManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public interface AccessDeviceRegistrationToken{
        void onResponse(String token);
    }

    private static final String TAG_ERROR = "TOKEN ERROR";
    private static final String TAG = "TOKEN SUCCEEDED";

    @Override
    public void onNewToken(@NonNull String s) {

        //send the new FCM registration token to the app server.
    }

    public static void getFmcActualToken(AccessDeviceRegistrationToken callback){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    String token = null;

                    if (task.isSuccessful()) {
                        token = task.getResult();
                        Log.d(TAG, "getFmcActualToken: " + token);
                    } else
                        Log.d(TAG_ERROR, "getFmcActualToken: " + task.getException());


                    if (callback != null)
                        callback.onResponse(token);
                }
        );
    }
}
