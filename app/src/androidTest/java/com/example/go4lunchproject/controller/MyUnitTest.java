package com.example.go4lunchproject.controller;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.go4lunchproject.MainActivity;
import com.example.go4lunchproject.data.api.LocationApi;
import com.example.go4lunchproject.data.api.RestaurantListUrlApi;
import com.example.go4lunchproject.data.googleplace.RestaurantNearbyBank2;
import com.example.go4lunchproject.model.MyPositionObject;
import com.example.go4lunchproject.model.Restaurant;

import junit.framework.TestCase;

import org.junit.Rule;

import java.util.ArrayList;
import java.util.List;

public class MyUnitTest extends TestCase {

    private RestaurantNearbyBank2 restaurantNearbyBank;
    private Context context;
    private String url;

    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityRules = new ActivityScenarioRule<>(MainActivity.class);

    public void setUp() throws Exception {
        super.setUp();
        context = ApplicationProvider.getApplicationContext();
        restaurantNearbyBank = RestaurantNearbyBank2.getInstance(context);

        MyPositionObject fakeDevicePosition = new MyPositionObject(45.7772, 3.0870);
        LocationApi.getInstance(context).setPosition(fakeDevicePosition);

        url = RestaurantListUrlApi.getInstance(context).getUrlThroughDeviceLocation();
    }

    public void tearDown() {
    }

    public void testOnCreate() {
    }

    public void testGettingRestaurantNearby() {
        assertNotNull(context);
        assertNotNull(url);

        final List<Restaurant>[] restaurantList = new List[]{new ArrayList<>()};
        assertEquals(restaurantList[0].size(), 0);

        restaurantNearbyBank.getRestaurantList(url, restaurantList1 -> {
            assertNotNull(restaurantList1);

            restaurantList[0] = restaurantList1;
            assertTrue(restaurantList[0].size() != 0);
        });

    }

}