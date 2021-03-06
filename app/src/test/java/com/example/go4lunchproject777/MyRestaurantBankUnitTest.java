package com.example.go4lunchproject777;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import com.example.go4lunchproject777.data.api.LocationApi;
import com.example.go4lunchproject777.data.api.RestaurantListUrlApi;
import com.example.go4lunchproject777.data.googleplace.RestaurantNearbyBank2;
import com.example.go4lunchproject777.model.MyPositionObject;
import com.example.go4lunchproject777.model.Restaurant;
import com.example.go4lunchproject777.util.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class MyRestaurantBankUnitTest {
    private String url;

    @Mock
    Context mockContext;

    @Before
    public void initializing(){
        initializeUrlForNearbyRestaurant();
    }

    @Test
    public void testRestaurantBank(){
        assertNotNull(mockContext);
        assertNotNull(url);

        RestaurantNearbyBank2 bank = mock(RestaurantNearbyBank2.class);
        RestaurantNearbyBank2.OnRestaurantListCallback callback = mock(RestaurantNearbyBank2.OnRestaurantListCallback.class);

        ArrayList<Restaurant> restaurants = Constants.getRestaurantList();
        callback.onListReady(restaurants);

        when(bank).thenAnswer((Answer<ArrayList<Restaurant>>) invocation -> invocation.getArgument(0));

        bank.getRestaurantList(url, callback);

        verify(bank).getRestaurantList(url, callback);

        assertEquals(callback.onListReady(restaurants), restaurants);

    }

    private void initializeUrlForNearbyRestaurant(){
        //Setting a fake device position needed to get the url that is going to be used to have the restaurants nearby
        MyPositionObject fakeDevicePosition = new MyPositionObject(45.7772, 3.0870);

        //Saving that device position in our LocationApi, because we're getting the position from that api to have the url required for nearby restaurant
        LocationApi.getInstance(mockContext).setPosition(fakeDevicePosition);

        //Finally, we can get the url required using the device position
        url = RestaurantListUrlApi.getInstance(mockContext).getUrlThroughDeviceLocation();
    }
}
