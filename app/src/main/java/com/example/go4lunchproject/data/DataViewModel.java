package com.example.go4lunchproject.data;

import androidx.lifecycle.ViewModel;

import com.example.go4lunchproject.model.MyMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class DataViewModel extends ViewModel {
    private String url;
    private GoogleMap googleMap;
    private RestaurantListManager listManager;
    private boolean isSet = false;
    private ArrayList<MyMarker> markerList = new ArrayList<>();



    public String getUrl() {
        isSet = true;
        return url;
    }

    public void setUrl(String url) {
        isSet = true;
        this.url = url;
    }

    public GoogleMap getGoogleMap() {
        isSet = true;
        return googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        isSet = true;
        this.googleMap = googleMap;
    }

    public RestaurantListManager getListManager() {
        isSet = true;
        return listManager;
    }

    public void setListManager(RestaurantListManager listManager) {
        isSet = true;
        this.listManager = listManager;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean set) {
        isSet = set;
    }

    public ArrayList<MyMarker> getMarkerList() {
        return markerList;
    }

    public void setMarkerList(ArrayList<MyMarker> markerList) {
        this.markerList = markerList;
    }
}
