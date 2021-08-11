package com.example.go4lunchproject.data.viewmodel;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchproject.fragment.RestaurantListViewFragment;
import com.example.go4lunchproject.fragment.RestaurantMapViewFragment;
import com.example.go4lunchproject.fragment.WorkmateListViewFragment;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FragmentViewModel extends ViewModel {
    private Fragment restaurantMapViewFragment = new RestaurantMapViewFragment();
    private Fragment restaurantListViewFragment = new RestaurantListViewFragment();
    private Fragment workmateListViewFragment = new WorkmateListViewFragment();
    private boolean isSet = false;



    public Fragment getRestaurantMapViewFragment() {
        isSet = true;
        return restaurantMapViewFragment;
    }

    public void setRestaurantMapViewFragment(RestaurantMapViewFragment restaurantMapViewFragment) {
        this.restaurantMapViewFragment = restaurantMapViewFragment;
    }

    public Fragment getRestaurantListViewFragment() {
        isSet = true;
        return restaurantListViewFragment;
    }

    public void setRestaurantListViewFragment(RestaurantListViewFragment restaurantListViewFragment) {
        this.restaurantListViewFragment = restaurantListViewFragment;
    }

    public Fragment getWorkmateListViewFragment() {
        isSet = true;
        return workmateListViewFragment;
    }

    public void setWorkmateListViewFragment(WorkmateListViewFragment workmateListViewFragment) {
        this.workmateListViewFragment = workmateListViewFragment;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean set) {
        isSet = set;
    }
}
