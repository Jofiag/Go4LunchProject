package com.example.go4lunchproject.data;

import static com.example.go4lunchproject.util.Constants.NEARBY_SEARCH_URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import com.example.go4lunchproject.R;
import com.example.go4lunchproject.util.Constants;

public class RestaurantListUrlApi {
    private String url;
    private Location location;
    private final Context context;
    @SuppressLint("StaticFieldLeak")
    private static RestaurantListUrlApi INSTANCE;

    public RestaurantListUrlApi(Context context) {
        this.context = context;
    }

    public static RestaurantListUrlApi getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = new RestaurantListUrlApi(context);

        return INSTANCE;
    }

    private void setLocation() {
        location = LocationApi.getInstance(context).getLocation();
    }

    private void setUrl(){
        if (location != null){
            url = NEARBY_SEARCH_URL +
                    "location=" + location.getLatitude() + "," + location.getLongitude() +
//                    "&rankby=" + Constants.PROMINENCE +
                    "&radius=" + Constants.PROXIMITY_RADIUS +
                    "&type=" + Constants.RESTAURANT +
                    "&sensor=true" +
                    "&key=" + context.getString(R.string.google_maps_key);
        }
    }

    public String getUrlThroughDeviceLocation() {
        setLocation();
        setUrl();
        return url;
    }

}
