package com.example.go4lunchproject777.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MyPositionObject implements Parcelable {
    private double latitude;
    private double longitude;

    public MyPositionObject() {
    }

    public MyPositionObject(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected MyPositionObject(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<MyPositionObject> CREATOR = new Creator<MyPositionObject>() {
        @Override
        public MyPositionObject createFromParcel(Parcel in) {
            return new MyPositionObject(in);
        }

        @Override
        public MyPositionObject[] newArray(int size) {
            return new MyPositionObject[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }
}
