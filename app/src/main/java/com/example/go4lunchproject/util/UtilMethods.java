package com.example.go4lunchproject.util;

import android.util.Log;

import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.model.User;
import com.example.go4lunchproject.model.Workmate;

import java.util.ArrayList;

public class UtilMethods {
    public static ArrayList<Restaurant> removeRedundantRestaurant(ArrayList<Restaurant> list){
        ArrayList<Restaurant> newList = new ArrayList<>();

        for (Restaurant restaurant : list) {
            if (!newList.contains(restaurant)) {
                newList.add(restaurant);
            }
        }
        for (Restaurant restaurant : newList)
            Log.d("REDUNDANT", "removeRedundantRestaurant: " + restaurant.getName() + "\n");

        Log.d("REDUNDANT", "removeRedundantRestaurant: \n\n\n");


        return newList;

        ///////////////////////////////////////////
        /*Set<Restaurant> set = new HashSet<>(list);
        list.clear();
        list.addAll(set);

        for (Restaurant restaurant : list) {
            Log.d("REDUNDANT", "removeRedundantRestaurant: " + restaurant.getName());
        }
        return list;
        */

        /////////////////////////////////////
        /*
        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Restaurant restaurantC = list.get(i);

            for (int y = 0; y < list.size(); y++) {
                Restaurant restaurantO = list.get(i);
                if (i != y){
                    if (restaurantC == restaurantO)
                        indexList.add(y);
                }
            }
        }

        if (!indexList.isEmpty()) {
            for (Integer integer : indexList) {
                if (integer < list.size()){
                    Restaurant restaurantR = list.get(integer);
                    list.remove(restaurantR);
                }
            }
        }
        return list
        */
    }
    public static Workmate setWorkmateCorresponding(User user){
        Workmate workmate = new Workmate();
        workmate.setName(user.getName());
        workmate.setImageUri(user.getImageUri());
        workmate.setRestaurantChosen(user.getRestaurantChosen());

        return workmate;
    }
}
