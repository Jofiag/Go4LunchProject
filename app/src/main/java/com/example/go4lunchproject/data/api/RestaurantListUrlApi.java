package com.example.go4lunchproject.data.api;

import static com.example.go4lunchproject.util.Constants.NEARBY_SEARCH_URL;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.go4lunchproject.R;
import com.example.go4lunchproject.model.MyPositionObject;
import com.example.go4lunchproject.util.Constants;

public class RestaurantListUrlApi {
    private String url;
    private MyPositionObject position;
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

    private void setPosition() {
        position = LocationApi.getInstance(context).getPosition();
    }

    private void setUrl(){
        if (position != null){
            url = NEARBY_SEARCH_URL +
                    "location=" + position.getLatitude() + "," + position.getLongitude() +
//                    "&rankby=" + Constants.PROMINENCE +
                    "&radius=" + Constants.PROXIMITY_RADIUS +
                    "&type=" + Constants.RESTAURANT +
                    "&sensor=true" +
                    "&key=" + context.getString(R.string.google_maps_key);
        }
    }

    public String getUrlThroughDeviceLocation() {
        setPosition();
        setUrl();
        return url;
    }

}
