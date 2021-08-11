package com.example.go4lunchproject.data.api;

import com.example.go4lunchproject.model.Restaurant;

public class RestaurantSelectedApi {
    private static RestaurantSelectedApi INSTANCE;
    private Restaurant restaurant;


    public static RestaurantSelectedApi getInstance(){
        if (INSTANCE == null)
            INSTANCE = new RestaurantSelectedApi();

        return INSTANCE;
    }

    public void setRestaurantSelected(Restaurant restaurant){
        this.restaurant = restaurant;
    }

    public Restaurant getRestaurantSelected(){
        return restaurant;
    }
}
