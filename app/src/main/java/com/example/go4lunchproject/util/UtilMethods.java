package com.example.go4lunchproject.util;

import android.util.Log;

import com.example.go4lunchproject.model.Restaurant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UtilMethods {
    public static ArrayList<Restaurant> removeRedundantRestaurant(ArrayList<Restaurant> list){
        List<Integer> indexList = new ArrayList<>();

        Set<Restaurant> set = new HashSet<>(list);
        list.clear();
        list.addAll(set);

        Log.d("REDUNDANT", "removeRedundantRestaurant: " + list);
        //return list;

        /*for (int i = 0; i < list.size(); i++) {
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
        }*/

        return list;
    }
}
