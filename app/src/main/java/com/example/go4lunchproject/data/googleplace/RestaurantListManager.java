package com.example.go4lunchproject.data.googleplace;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.example.go4lunchproject.data.api.RestaurantListUrlApi;
import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.services.MyJobService;
import com.example.go4lunchproject.util.Constants;

import java.util.ArrayList;

@SuppressLint("StaticFieldLeak")
@RequiresApi(api = Build.VERSION_CODES.O)
public class RestaurantListManager{

    public interface OnRestaurantListReceive{
        void onResponse(ArrayList<Restaurant> restaurantList);
    }

    private final Context mContext;
    private BroadcastReceiver broadcastReceiver;
    private static RestaurantListManager INSTANCE;
    private ArrayList<Restaurant> restaurantArrayList = new ArrayList<>();
    private final JobScheduler jobScheduler;
    private JobInfo jobInfo;

    public RestaurantListManager(Context mContext) {
        this.mContext = mContext;
        jobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        startGettingListInBackground();
    }

    public static synchronized RestaurantListManager getInstance(Context context){
        if (INSTANCE == null)
            INSTANCE = new RestaurantListManager(context);

        return INSTANCE;
    }

    public void receiveRestaurantList(OnRestaurantListReceive callback){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    restaurantArrayList = bundle.getParcelableArrayList(Constants.LIST);

                    if (callback != null)
                        callback.onResponse(restaurantArrayList);

//                    stopJobWhenWeGetAllTheRestaurantsFromDb();
                }
            }
        };
    }

    private void setJobInfo(){
        //        jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobInfo = new JobInfo.Builder(Constants.JOB_ID, new ComponentName(mContext.getApplicationContext(), MyJobService.class))
//                .setMinimumLatency(0)
//                .setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED) //The action will stop if there is no more internet
//                .setPersisted(true) //Set if the action will continue to be executed even when the device is rebooting
//                .setPeriodic(15 * 60 * 1000)  //Set the interval of time that the action will be executed, here it's every 15 minutes
                .setMinimumLatency(1)
                .build();
    }

    public void startGettingListInBackground(){
        setJobInfo();
        jobScheduler.schedule(jobInfo);
    }

    public void stopJobWhenWeGetAllTheRestaurantsFromDb(){
        String url = RestaurantListUrlApi.getInstance(mContext).getUrlThroughDeviceLocation();
        RestaurantNearbyBank2.getInstance(mContext).getRestaurantList(url, restaurantList -> {
                    if (restaurantArrayList.size() == restaurantList.size())
                        jobScheduler.cancelAll();
        });
    }

    public void registerBroadcastReceiverFromManager(String action){
        mContext.registerReceiver(broadcastReceiver,new IntentFilter(action));
    }

    public void unregisterBroadcastReceiverFromManager(){
        mContext.unregisterReceiver(broadcastReceiver);
    }

    public JobScheduler getJobScheduler() {
        return jobScheduler;
    }
}
