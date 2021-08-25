package com.example.go4lunchproject.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Workmate implements Parcelable {
    private String name;
    private String imageUri;
    private Restaurant restaurantChosen;
    private List<Restaurant> restaurantLikedList;

    public Workmate() {
    }

    protected Workmate(Parcel in) {
        name = in.readString();
        imageUri = in.readString();
        restaurantChosen = in.readParcelable(Restaurant.class.getClassLoader());
        restaurantLikedList = in.createTypedArrayList(Restaurant.CREATOR);
    }

    public static final Creator<Workmate> CREATOR = new Creator<Workmate>() {
        @Override
        public Workmate createFromParcel(Parcel in) {
            return new Workmate(in);
        }

        @Override
        public Workmate[] newArray(int size) {
            return new Workmate[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUrl(String imageUri) {
        this.imageUri = imageUri;
    }

    public Restaurant getRestaurantChosen() {
        return restaurantChosen;
    }

    public void setRestaurantChosen(Restaurant restaurantChosen) {
        this.restaurantChosen = restaurantChosen;
    }

    public List<Restaurant> getRestaurantLikedList() {
        return restaurantLikedList;
    }

    public void setRestaurantLikedList(List<Restaurant> restaurantLikedList) {
        this.restaurantLikedList = restaurantLikedList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(imageUri);
        dest.writeParcelable(restaurantChosen, flags);
        dest.writeTypedList(restaurantLikedList);
    }
}
