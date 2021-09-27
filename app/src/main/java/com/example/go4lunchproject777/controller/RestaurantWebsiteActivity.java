package com.example.go4lunchproject777.controller;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.go4lunchproject777.R;
import com.example.go4lunchproject777.data.api.RestaurantSelectedApi;

public class RestaurantWebsiteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_website);

        /*WebView myWebView = findViewById(R.id.my_web_view);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String restaurantWebsiteUrl = String.valueOf(RestaurantSelectedApi.getInstance().getRestaurantSelected().getWebsiteUrl());
        myWebView.loadUrl(restaurantWebsiteUrl);*/

        WebView myWebView = new WebView(this);
        setContentView(myWebView);

        String restaurantWebsiteUrl = RestaurantSelectedApi.getInstance().getRestaurantSelected().getWebsiteUrl();
        myWebView.loadUrl(restaurantWebsiteUrl);
    }
}