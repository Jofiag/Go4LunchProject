package com.example.go4lunchproject.controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunchproject.R;
import com.example.go4lunchproject.adapter.WorkmateRecyclerViewAdapter;
import com.example.go4lunchproject.data.Firebase.MyFirebaseDatabase;
import com.example.go4lunchproject.data.RestaurantSelectedApi;
import com.example.go4lunchproject.data.UserApi;
import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

@RequiresApi(api = Build.VERSION_CODES.M)
public class RestaurantDetailsActivity extends AppCompatActivity {
    public static final String CALL_PERMISSION = Manifest.permission.CALL_PHONE;
    public static final int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;

    private ImageView yellowStar;
    private ImageView callImageView;
    private ImageView starImageView;
    private ImageView globeImageView;
    private CircleImageView chosenImageView;
    private RecyclerView recyclerView;
    private ImageView restaurantImageView;
    private TextView restaurantNameTextView;
    private TextView RestaurantFoodCountryAndRestaurantAddress;

    private List<Restaurant> restaurantLikedList = new ArrayList<>();

    private Restaurant restaurantActuallyShowed;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        setReferences();

        user = getUserConnected();
        restaurantLikedList = getRestaurantLikedList();
        restaurantActuallyShowed = RestaurantSelectedApi.getInstance().getRestaurantSelected();

        showRestaurantImageNameAndAddress();
        setRecyclerView();
        setCallRestaurantFunction();
        setLikeRestaurantFunction();
        setGoToRestaurantWebsiteFunction();
        indicateIfRestaurantIsChosenByWorkmate();
        setYellowStarVisibility();
        setChosenImageViewSource();
        setRestaurantChosenByWorkmateMarkerInGreen();

    }

    private void setRestaurantChosenByWorkmateMarkerInGreen() {
        //TODO : Set marker of restaurant chosen by workmate in green
    }

    private void setReferences() {
        chosenImageView = findViewById(R.id.chosenImageView);
        yellowStar = findViewById(R.id.yellow_star);
        globeImageView = findViewById(R.id.globe_image_view);
        starImageView = findViewById(R.id.green_star_image_view);
        callImageView = findViewById(R.id.green_call_image_view);
        recyclerView = findViewById(R.id.restaurant_details_recycler_view);
        restaurantImageView = findViewById(R.id.restaurant_image_view_details);
        restaurantNameTextView = findViewById(R.id.restaurant_name_text_view_details);
        RestaurantFoodCountryAndRestaurantAddress = findViewById(R.id.food_country_and_restaurant_address_details);
    }

    private User getUserConnected(){
        return UserApi.getInstance().getUser();
    }

    public List<Restaurant> getRestaurantLikedList() {
        restaurantLikedList = user.getRestaurantLikedList();

        if (restaurantLikedList == null)
            restaurantLikedList = new ArrayList<>();

        return restaurantLikedList;
    }

    private void setYellowStarVisibility(){
        MyFirebaseDatabase.getInstance().getUser(user.getId(), singleUser -> {
            List<Restaurant> list = singleUser.getRestaurantLikedList();
            boolean isFavorite = false;
            if (list != null){
                for (Restaurant restaurant : list) {
                    if (restaurant.getName().equals(restaurantActuallyShowed.getName())) {
                        isFavorite = true;
                        yellowStar.setVisibility(View.VISIBLE);
                    }
                }
            }

            if (!isFavorite)
                yellowStar.setVisibility(View.GONE);

        });

    }

    private void setChosenImageViewSource(){
        if (user.getRestaurantChosen() == restaurantActuallyShowed)
            chosenImageView.setImageResource(R.mipmap.green_check_round);
        else
            chosenImageView.setImageResource(R.mipmap.red_unchecked);
    }

    private void setRecyclerView(){
        WorkmateRecyclerViewAdapter workmateAdapter;
        if (restaurantActuallyShowed != null)
            workmateAdapter = new WorkmateRecyclerViewAdapter(restaurantActuallyShowed.getWorkmateList());
        else
            workmateAdapter = new WorkmateRecyclerViewAdapter(new ArrayList<>());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(RestaurantDetailsActivity.this));
        recyclerView.setAdapter(workmateAdapter);
    }

    private void showRestaurantImageNameAndAddress(){
        if (restaurantActuallyShowed != null) {
            if (restaurantActuallyShowed.getImageUrl() != null)
                Picasso.get().load(restaurantActuallyShowed.getImageUrl())
                        .placeholder(android.R.drawable.stat_sys_download)
                        .error(android.R.drawable.stat_notify_error)
                        .resize(445, 445)
                        .into(restaurantImageView);

            /*if (restaurant.getFoodCountry() != null || restaurant.getFoodCountry().isEmpty())
                RestaurantFoodCountryAndRestaurantAddress.setText(MessageFormat.format("{0} - {1}", restaurant.getFoodCountry(), restaurant.getAddress()));
            else*/
                RestaurantFoodCountryAndRestaurantAddress.setText(restaurantActuallyShowed.getAddress());

            restaurantNameTextView.setText(restaurantActuallyShowed.getName());
        }
    }

    private void setLikeRestaurantFunction(){

//        MyFirebaseDatabase.getInstance().getUser(user.getId(), singleUser -> {
//            List<Restaurant> list = singleUser.getRestaurantLikedList();
//
//            if (list != null){
//                if (!list.contains(restaurantActuallyShowed))
//                    list.add(restaurantActuallyShowed);
//
//            }
//
//        });

        starImageView.setOnClickListener(v -> {
            //Add actual restaurant to the liked restaurant list of the workmate connected and set yellowStar visibility to VISIBLE
            int visibility = yellowStar.getVisibility();
            String status = "";
            if (visibility != View.VISIBLE) {
                yellowStar.setVisibility(View.VISIBLE);

                if (!restaurantLikedList.contains(restaurantActuallyShowed))
                    restaurantLikedList.add(restaurantActuallyShowed);

                status = " added to liked list.";
            }

            if (visibility == View.VISIBLE){
                yellowStar.setVisibility(View.GONE);
                restaurantLikedList.remove(restaurantActuallyShowed);
                status = " removed from liked list.";
            }

            List<Restaurant> tempList = user.getRestaurantLikedList();
            if (tempList == null)
                tempList = new ArrayList<>(restaurantLikedList);
            else
                tempList.addAll(restaurantLikedList);

            user.setRestaurantLikedList(tempList);
            MyFirebaseDatabase.getInstance().updateUser(user);
            UserApi.getInstance().setUser(user);
            Toast.makeText(this, restaurantActuallyShowed.getName() + status, Toast.LENGTH_SHORT).show();

        });
    }

    private void setGoToRestaurantWebsiteFunction(){
        globeImageView.setOnClickListener(v -> {
            //Go to restaurant website if its available.
            if(restaurantActuallyShowed.getWebsiteUrl() != null)
                startActivity(new Intent(this, RestaurantWebsiteActivity.class));
            else
                Toast.makeText(this, "Website not available for " + restaurantActuallyShowed.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    private void indicateIfRestaurantIsChosenByWorkmate(){
        //If workmate connected has chosen actual restaurant, set fab visibility to VISIBLE
        chosenImageView.setOnClickListener(v -> {
            String status;
            Restaurant restaurantTemp;

            if (user.getRestaurantChosen() == restaurantActuallyShowed){
                chosenImageView.setImageResource(R.mipmap.red_unchecked);
                restaurantTemp = null;
                status = " not chosen anymore.";
            }
            else{
                restaurantTemp = restaurantActuallyShowed;
                chosenImageView.setImageResource(R.mipmap.green_check_round);
                status = " chosen.";
            }

            user.setRestaurantChosen(restaurantTemp);
            Toast.makeText(RestaurantDetailsActivity.this, restaurantActuallyShowed.getName() + status, Toast.LENGTH_SHORT).show();
            //TODO : update user to firebase
        });
    }


    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), result -> {
                if (result)
                    callPhoneNumberIfPermissionGranted();
                else
                    askPermissionWithinDialog();
            }
);
    private void setCallRestaurantFunction(){
        callImageView.setOnClickListener(v -> {
            //Call restaurant if phone number is available
            Toast.makeText(RestaurantDetailsActivity.this, "Call", Toast.LENGTH_SHORT).show();
            callPhoneNumberIfPermissionGranted();
            Log.d("DETAILS", "setCallRestaurantFunction: CALL");
        });
    }

    private void callRestaurant(){
        String dial = "tel:" + restaurantActuallyShowed.getPhoneNumber();
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(dial));
        startActivity(callIntent);
    }

    private void callPhoneNumberIfPermissionGranted(){
        if (checkSelfPermission(CALL_PERMISSION) == PERMISSION_GRANTED)
            callRestaurant();
        else{
            if (shouldShowRequestPermissionRationale(CALL_PERMISSION))
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();

            requestPermissionLauncher.launch(CALL_PERMISSION);
        }
    }

    private void askPermissionWithinDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Call phone permission disable")
                .setMessage("You denied the Call phone permission. It is required to to call the restaurant. Do you want to grant the permission")
                .setPositiveButton("YES", (dialog, which) -> callPhoneNumberIfPermissionGranted())
                .setNegativeButton("NO", null)
                .create()
                .show();
    }
}