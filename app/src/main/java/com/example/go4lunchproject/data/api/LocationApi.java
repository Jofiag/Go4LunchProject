package com.example.go4lunchproject.data.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.example.go4lunchproject.model.MyPositionObject;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class LocationApi {
    private MyPositionObject position;
    private final Geocoder geocoder;
    @SuppressLint("StaticFieldLeak")
    private static LocationApi INSTANCE;


    public LocationApi(Context context) {
        geocoder = new Geocoder(context);
    }

    public static LocationApi getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = new LocationApi(context);

        return INSTANCE;
    }

    public MyPositionObject getPosition() {
        return position;
    }

    public void setPosition(MyPositionObject position) {
        this.position = position;
    }

    public LatLng getPositionFromLocation(){
        LatLng position = null;

        if (this.position != null)
            position = new LatLng(this.position.getLatitude(), this.position.getLongitude());

        return position;
    }

    public String getStreetAddressFromPositions() {
        LatLng position = getPositionFromLocation();

        String address = "";

        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocation(position.latitude, position.longitude, 1);
            address = addressList.get(0).getAddressLine(0);
        } catch (IOException e) {
            Log.d("ADDRESS", "getStreetAddressFromPositions: " + e.getMessage());
        }

        return address;
    }
}
