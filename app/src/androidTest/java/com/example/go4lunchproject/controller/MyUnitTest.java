package com.example.go4lunchproject.controller;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.go4lunchproject.MainActivity;
import com.example.go4lunchproject.data.api.LocationApi;
import com.example.go4lunchproject.data.api.RestaurantListUrlApi;
import com.example.go4lunchproject.data.firebase.FirebaseCloudDatabase;
import com.example.go4lunchproject.data.googleplace.RestaurantNearbyBank2;
import com.example.go4lunchproject.model.MyPositionObject;
import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.model.User;

import junit.framework.TestCase;

import org.junit.Rule;

import java.util.ArrayList;
import java.util.List;

public class MyUnitTest extends TestCase {
    private Context context;
    private String url;

    private FirebaseCloudDatabase firebaseCloudDatabase;
    private RestaurantNearbyBank2 restaurantNearbyBank;
    private User testingUser;


    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityRules = new ActivityScenarioRule<>(MainActivity.class);

    public void setUp() throws Exception {
        super.setUp();

        initializeRestaurantBankAndFirebaseCloudDb();
        initializeUrlForNearbyRestaurant();
        initializeTestingUser();
    }

    public void tearDown() {
    }

    public void testGettingRestaurantNearby() {
        assertNotNull(context);
        assertNotNull(url);

        List<Restaurant>[] restaurantList = new List[]{new ArrayList<>()};
        assertEquals(restaurantList[0].size(), 0);

        restaurantNearbyBank.getRestaurantList(url, restaurantList1 -> {
            assertNotNull(restaurantList1);

            restaurantList[0] = restaurantList1;
            assertTrue(restaurantList[0].size() != 0);
        });

    }

    public void testSavingUserInFirebase(){
        //Saving the testingUser in firebase
        firebaseCloudDatabase.saveUser(testingUser);

        //Get that user from firebase
        firebaseCloudDatabase.getUser(testingUser.getId(), singleUser -> assertEquals(singleUser.getId(), testingUser.getId()));
    }


    /////PRIVATE FUNCTION
    private void initializeRestaurantBankAndFirebaseCloudDb(){
        context = ApplicationProvider.getApplicationContext();
        restaurantNearbyBank = RestaurantNearbyBank2.getInstance(context);
        firebaseCloudDatabase = FirebaseCloudDatabase.getInstance();
    }
    private void initializeUrlForNearbyRestaurant(){
        //Setting a fake device position needed to get the url that is going to be used to have the restaurants nearby
        MyPositionObject fakeDevicePosition = new MyPositionObject(45.7772, 3.0870);

        //Saving that device position in our LocationApi, because we're getting the position from that api to have the url required for nearby restaurant
        LocationApi.getInstance(context).setPosition(fakeDevicePosition);

        //Finally, we can get the url required using the device position
        url = RestaurantListUrlApi.getInstance(context).getUrlThroughDeviceLocation();
    }
    private void initializeTestingUser(){
        testingUser = new User();
        testingUser.setName("Testing User");
        testingUser.setUserEmail("testing.user@gmail.com");
        testingUser.setFirebaseId("K4pny0E9MtY2cL7RGn8ji8grpcC3");
        testingUser.setId(testingUser.getName() + "_" +  testingUser.getFirebaseId());
        testingUser.setImageUrl("https://graph.facebook.com/101830498902097/picture");
    }

}