package com.example.go4lunchproject777.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Restaurant implements Parcelable {
    private String restaurantId;
    private String name;
    private String address;
    private String websiteUrl;
    private String placeId;
    private String imageUrl;
    private MyPositionObject position;
    private String foodCountry;
    private String phoneNumber;
    private int favorableOpinion;
    private List<Workmate> workmateList;
    private MyOpeningHours myOpeningHours;
    private int numberOfInterestedWorkmate;
    private int distanceFromDeviceLocation;


    public Restaurant() {
    }


    protected Restaurant(Parcel in) {
        restaurantId = in.readString();
        name = in.readString();
        address = in.readString();
        websiteUrl = in.readString();
        placeId = in.readString();
        imageUrl = in.readString();
        position = in.readParcelable(MyPositionObject.class.getClassLoader());
        foodCountry = in.readString();
        phoneNumber = in.readString();
        favorableOpinion = in.readInt();
        workmateList = in.createTypedArrayList(Workmate.CREATOR);
        numberOfInterestedWorkmate = in.readInt();
        distanceFromDeviceLocation = in.readInt();
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public int getProximity() {
        return distanceFromDeviceLocation;
    }

    public void setDistanceFromDeviceLocation(int distanceFromDeviceLocation) {
        this.distanceFromDeviceLocation = distanceFromDeviceLocation;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public MyPositionObject getPosition() {
        return position;
    }

    public void setPosition(MyPositionObject position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFoodCountry() {
        return foodCountry;
    }

    public void setFoodCountry(String foodCountry) {
        this.foodCountry = foodCountry;
    }

    public MyOpeningHours getOpeningHours() {
        return myOpeningHours;
    }

    public void setOpeningHours(MyOpeningHours myOpeningHours) {
        this.myOpeningHours = myOpeningHours;
    }

    public int getFavorableOpinion() {
        return favorableOpinion;
    }

    public void setFavorableOpinion(int favorableOpinion) {
        this.favorableOpinion = favorableOpinion;
    }

    public int getNumberOfInterestedWorkmate() {
        return numberOfInterestedWorkmate;
    }

    public void setNumberOfInterestedWorkmate(int numberOfInterestedWorkmate) {
        this.numberOfInterestedWorkmate = numberOfInterestedWorkmate;
    }

    public List<Workmate> getWorkmateList() {
        return workmateList;
    }

    public void setWorkmateList(List<Workmate> workmateList) {
        this.workmateList = workmateList;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(restaurantId);
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeString(websiteUrl);
        parcel.writeString(placeId);
        parcel.writeString(imageUrl);
        parcel.writeParcelable(position, i);
        parcel.writeString(foodCountry);
        parcel.writeString(phoneNumber);
        parcel.writeInt(favorableOpinion);
        parcel.writeTypedList(workmateList);
        parcel.writeInt(numberOfInterestedWorkmate);
        parcel.writeInt(distanceFromDeviceLocation);
    }

    @NonNull
    @Override
    public String toString() {
        return "Restaurant{" +
                "restaurantId='" + restaurantId + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", position=" + position +
                '}';
    }
}
