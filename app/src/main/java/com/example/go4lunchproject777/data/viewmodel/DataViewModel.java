package com.example.go4lunchproject777.data.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchproject777.data.api.LocationApi;
import com.example.go4lunchproject777.data.api.RestaurantListUrlApi;
import com.example.go4lunchproject777.data.googleplace.RestaurantListManager;
import com.example.go4lunchproject777.model.Restaurant;
import com.example.go4lunchproject777.util.Constants;
import com.example.go4lunchproject777.util.UtilMethods;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


@SuppressLint("StaticFieldLeak")
@RequiresApi(api = Build.VERSION_CODES.O)
public class DataViewModel extends ViewModel {

    public interface OnGettingRestaurantNearbyList{
        void gettingRestaurantNearbyList(ArrayList<Restaurant> restaurantList);
    }
    private String url;
    private Context context;
    private GoogleMap googleMap;
    private LatLng devicePosition;
    private RestaurantListManager listManager;



    public void startGettingRestaurantNearbyList(OnGettingRestaurantNearbyList callback){
        listManager = RestaurantListManager.getInstance(context);
        devicePosition = LocationApi.getInstance(context).getPositionFromLocation();
        url = RestaurantListUrlApi.getInstance(context).getUrlThroughDeviceLocation();

        listManager.startGettingListInBackground();
        listManager.receiveRestaurantList(restaurantList -> {
            if (callback != null)
                callback.gettingRestaurantNearbyList(UtilMethods.removeRedundantRestaurant(restaurantList));
        });
    }

    public String getUrl() {
        return url;
    }

    public void registerBroadcastReceiver(){
        listManager.registerBroadcastReceiverFromManager(Constants.SEND_LIST_ACTION);
    }

    public void unregisterBroadcastReceiver(){
        listManager.unregisterBroadcastReceiverFromManager();
    }

    public void stopGettingRestaurantNearbyList(){
        if (listManager != null)
            listManager.stopJobWhenWeGetAllTheRestaurantsFromDb();
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public LatLng getDevicePosition() {
        return devicePosition;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}
