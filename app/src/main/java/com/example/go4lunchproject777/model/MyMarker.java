package com.example.go4lunchproject777.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.Marker;

public class MyMarker implements Parcelable {
    private Marker marker;

    public MyMarker(Marker marker) {
        this.marker = marker;
    }

    protected MyMarker(Parcel in) {
    }

    public static final Creator<MyMarker> CREATOR = new Creator<MyMarker>() {
        @Override
        public MyMarker createFromParcel(Parcel in) {
            return new MyMarker(in);
        }

        @Override
        public MyMarker[] newArray(int size) {
            return new MyMarker[size];
        }
    };

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
