package com.example.go4lunchproject.services;

import android.app.job.JobParameters;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.go4lunchproject.data.api.RestaurantListUrlApi;
import com.example.go4lunchproject.data.googleplace.RestaurantNearbyBank2;
import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.util.Constants;
import com.example.go4lunchproject.util.UtilMethods;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MyJobService extends android.app.job.JobService {
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("JOB0", "onStartJob: " + params.getJobId());
        doBackgroundTask(params);

        //false => complete the Job in the current thread; true => complete the job in a different thread
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        //onStopJob is called when the job is needed to be stop before the calling of jobFinished() method

        Log.i("JOB0", "onStopJob: " + params.getJobId());
        jobCancelled = true;

        // false => we don't want to reschedule; true => we do
        return true;
    }

    private void doBackgroundTask(JobParameters parameters){
        Runnable  runnable = () -> {
            getRestaurantNearbyListTask();
            jobFinished(parameters, false);
            Log.i("JOB0", "run: Job done !");
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void getRestaurantNearbyListTask(){
        if (jobCancelled)
            return;

        String url = RestaurantListUrlApi.getInstance(getApplicationContext()).getUrlThroughDeviceLocation();
        RestaurantNearbyBank2 bank = RestaurantNearbyBank2.getInstance(getApplication());
        bank.getRestaurantList(url, this::sendListToTheMainThread);
    }

    private void sendListToTheMainThread(ArrayList<Restaurant> restaurantArrayList){
        Intent listIntent = new Intent(Constants.SEND_LIST_ACTION);
        listIntent.putParcelableArrayListExtra(Constants.LIST, UtilMethods.removeRedundantRestaurant(restaurantArrayList));

        //Sending using BroadcastReceiver
        sendBroadcast(listIntent);
    }



}