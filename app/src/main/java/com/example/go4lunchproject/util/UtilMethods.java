package com.example.go4lunchproject.util;

import android.util.Log;

import com.example.go4lunchproject.data.firebase.MyFirebaseDatabase;
import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.model.User;
import com.example.go4lunchproject.model.Workmate;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Objects;

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

    public static User getMyUserFromFirebaseUser(){
        User user = new User();
        FirebaseUser firebaseUser = MyFirebaseDatabase.getInstance().getCurrentFirebaseUser();

        if (firebaseUser != null){
            user.setId(firebaseUser.getDisplayName() + "_" +  firebaseUser.getUid());
            user.setFirebaseId(firebaseUser.getUid());
            user.setUserEmail(firebaseUser.getEmail());
            user.setName(firebaseUser.getDisplayName());
            user.setImageUri(Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString());

            return user;
        }
        else
            return null;
    }

    public static Workmate setWorkmateCorresponding(User user){
        Workmate workmate = new Workmate();
        workmate.setName(user.getName());
        workmate.setImageUri(user.getImageUri());
//        workmate.setRestaurantChosen(user.getRestaurantChosen());

        return workmate;
    }
}
