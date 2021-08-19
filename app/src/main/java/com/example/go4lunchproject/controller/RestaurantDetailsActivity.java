package com.example.go4lunchproject.controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunchproject.R;
import com.example.go4lunchproject.adapter.WorkmateAdapterForRestaurantDetails;
import com.example.go4lunchproject.data.api.RestaurantSelectedApi;
import com.example.go4lunchproject.data.api.UserApi;
import com.example.go4lunchproject.data.firebase.MyFirebaseDatabase;
import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.model.User;
import com.example.go4lunchproject.model.Workmate;
import com.example.go4lunchproject.util.Constants;
import com.example.go4lunchproject.util.UtilMethods;
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

    private Restaurant restaurantActuallyShowed;
    private String userId;

    private WorkmateAdapterForRestaurantDetails adapter;
    private List<Workmate> workmateList = new ArrayList<>();

    private Drawable greenCheck;
    private Drawable redUncheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        setReferences();

        userId = getUserConnected().getId();
        restaurantActuallyShowed = RestaurantSelectedApi.getInstance().getRestaurantSelected();

        setYellowStarVisibility();
        setChosenImageVisibility();

        showRestaurantImageNameAndAddress();
        setCallRestaurantFunction();
        setLikeRestaurantFunction();
        setGoToRestaurantWebsiteFunction();

        indicateIfRestaurantIsChosenByWorkmate();
        setRecyclerView();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
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

    private void setYellowStarVisibility(){
        MyFirebaseDatabase.getInstance().getUser(userId, singleUser -> {
            List<Restaurant> list = singleUser.getRestaurantLikedList();
            boolean isFavorite = false;
            if (list != null){
                for (Restaurant restaurant : list) {
                    if (restaurant.getAddress().equals(restaurantActuallyShowed.getAddress())) {
                        isFavorite = true;
                        yellowStar.setVisibility(View.VISIBLE);
                    }
                }
            }

            if (!isFavorite)
                yellowStar.setVisibility(View.GONE);

        });

    }



    private void setRecyclerView(){
        /*
        *   Show the list of users that choose the actual restaurant
        *       get the list of restaurant that chosen by all users from firebase
        *         if that list is not null and not empty
        *           check if that list contains the one actually showed.
        *               if it's the case then set the list of that restaurant to the recycler view.
        *               if it's not the case that means the restaurant actually showed has never be chosen by any user, so we do nothing.
        * */
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(RestaurantDetailsActivity.this));

        // get the list of restaurant that chosen by all users from firebase
        MyFirebaseDatabase.getInstance().getAllRestaurant(restaurantList -> {
            //if that list is not null and not empty
            if (restaurantList != null && !restaurantList.isEmpty()){
                //then we check if that list contains the one actually showed.
                for (Restaurant restaurant : restaurantList) {
                    //if it's the case then set the list of that restaurant to the recycler view.
                    if (restaurant.getAddress().equals(restaurantActuallyShowed.getAddress())) {
                        adapter = new WorkmateAdapterForRestaurantDetails(restaurant.getWorkmateList());
                        recyclerView.setAdapter(adapter);
                        break;
                    }
                }
            }
        });
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

    private void saveOrRemoveActualRestaurantLikedToFirebase(User user, List<Restaurant> firebaseList, String action){
        String status = "";
        if (action.equals(Constants.SAVE_RESTAURANT_ACTION)){
            if (firebaseList != null){
                boolean contains = false;
                for (Restaurant restaurant : firebaseList) {
                    if (restaurant.getAddress().equals(restaurantActuallyShowed.getAddress())){
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    firebaseList.add(restaurantActuallyShowed);
                    user.setRestaurantLikedList(firebaseList);
                }
            }
            else{
                firebaseList = new ArrayList<>();
                firebaseList.add(restaurantActuallyShowed);
                user.setRestaurantLikedList(firebaseList);
            }
            status = " added to liked list.";
        }
        else if(action.equals(Constants.REMOVE_RESTAURANT_ACTION)){
            if (firebaseList != null) {
                for (Restaurant restaurant : firebaseList) {
                    if (restaurant.getAddress().equals(restaurantActuallyShowed.getAddress())) {
                        firebaseList.remove(restaurant);
                        user.setRestaurantLikedList(firebaseList);
                        break;
                    }
                }
            }

            status = " removed from liked list.";
        }

        UserApi.getInstance().setUser(user);
        MyFirebaseDatabase.getInstance().updateUser(user);
        Toast.makeText(this, restaurantActuallyShowed.getName() + status, Toast.LENGTH_SHORT).show();
    }

    private void setLikeRestaurantFunction(){
        MyFirebaseDatabase.getInstance().getUser(userId, singleUser -> starImageView.setOnClickListener(v -> {

            int visibility = yellowStar.getVisibility();
            List<Restaurant> list = singleUser.getRestaurantLikedList();

            if (visibility != View.VISIBLE) {
                // If actual restaurant IS NOT IN the liked list of the workmate connected
                // Set yellowStar visibility to VISIBLE
                yellowStar.setVisibility(View.VISIBLE);

                //Add actual restaurant to the liked restaurant list of the workmate connected
                saveOrRemoveActualRestaurantLikedToFirebase(singleUser, list, Constants.SAVE_RESTAURANT_ACTION);
            }
            else {
                // If actual restaurant IS IN the liked list of the workmate connected
                // Set yellowStar visibility to GONE
                yellowStar.setVisibility(View.GONE);

                //Remove actual restaurant to the liked restaurant list of the workmate connected
                saveOrRemoveActualRestaurantLikedToFirebase(singleUser, list, Constants.REMOVE_RESTAURANT_ACTION);
            }
        }));
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

    private void setChosenImageVisibility(){
        greenCheck = AppCompatResources.getDrawable(RestaurantDetailsActivity.this, R.mipmap.green_check);
        redUncheck = AppCompatResources.getDrawable(RestaurantDetailsActivity.this, R.mipmap.red_unchecked);

        MyFirebaseDatabase.getInstance().getUser(userId, singleUser -> {
            Restaurant restaurantChosenFromFirebase = singleUser.getRestaurantChosen();
            if (restaurantChosenFromFirebase != null){
                if (restaurantChosenFromFirebase.getAddress().equals(restaurantActuallyShowed.getAddress()))
                    chosenImageView.setImageDrawable(greenCheck);
                else
                    chosenImageView.setImageDrawable(redUncheck);
            }
            else
                chosenImageView.setImageDrawable(redUncheck);
        });
    }

    private void indicateIfRestaurantIsChosenByWorkmate(){
        MyFirebaseDatabase firebaseDatabase = MyFirebaseDatabase.getInstance();
        firebaseDatabase.getUser(userId, singleUser -> chosenImageView.setOnClickListener(view -> {

            Restaurant restaurant = singleUser.getRestaurantChosen();
            Workmate workmateCorresponding = UtilMethods.setWorkmateCorresponding(singleUser);
            if (restaurant != null){
                if (restaurant.getAddress().equals(restaurantActuallyShowed.getAddress())){

                    firebaseDatabase.getRestaurant(restaurant.getRestaurantId(), restaurant1 -> {
                        workmateList = restaurant1.getWorkmateList();
                        if(workmateList == null)
                            workmateList = new ArrayList<>();
                        if (!workmateList.isEmpty()) {
                            for (Workmate workmate : workmateList) {
                                if (workmate.getName().equals(workmateCorresponding.getName())){
                                    workmateList.remove(workmate);

                                    restaurant1.setWorkmateList(workmateList);
                                    firebaseDatabase.updateRestaurant(restaurant1);

                                    singleUser.setRestaurantChosen(null);
                                    firebaseDatabase.updateUser(singleUser);
                                    break;
                                }

                            }

                        }

                    });

                }
                else if (!restaurant.getAddress().equals(restaurantActuallyShowed.getAddress())){
                    //In case the restaurant was already chosen by the current user or any other user
                    firebaseDatabase.getRestaurant(restaurantActuallyShowed.getRestaurantId(), restaurant1 -> {
                        Restaurant restaurant2 = restaurantActuallyShowed;
                        if (restaurant1 != null)
                            restaurant2 = restaurant1;

                        workmateList = restaurant2.getWorkmateList();
                        if (workmateList == null)
                            workmateList = new ArrayList<>();
                        boolean contains = false;
                        for (Workmate workmate : workmateList) {
                            if (workmate.getName().equals(workmateCorresponding.getName())){
                                contains = true;
                                break;
                            }
                        }
                        if (!contains) {
                            workmateList.add(workmateCorresponding);

                            restaurant2.setWorkmateList(workmateList);
                            firebaseDatabase.updateRestaurant(restaurant2);

                            singleUser.setRestaurantChosen(restaurant2);
                            firebaseDatabase.updateUser(singleUser);
                        }


                    });

                    //In case the restaurant was never chosen before
                    firebaseDatabase.getAllRestaurant(restaurantList -> {
                        List<Workmate> listOfLastRestaurantChosen = restaurant.getWorkmateList();
                        for (Workmate workmate : listOfLastRestaurantChosen) {
                            if (workmate.getName().equals(workmateCorresponding.getName())) {
                                listOfLastRestaurantChosen.remove(workmate);
                                restaurant.setWorkmateList(listOfLastRestaurantChosen);
                                firebaseDatabase.updateRestaurant(restaurant);
                                break;
                            }
                        }

                        boolean containsActualRestaurant = false;
                        for (Restaurant restaurant1 : restaurantList) {
                            if (restaurant1.getAddress().equals(restaurantActuallyShowed.getAddress())){
                                containsActualRestaurant = true;
                                break;
                            }
                        }
                        if (!containsActualRestaurant) {
                            if (!workmateList.contains(workmateCorresponding)) {
                                workmateList.add(workmateCorresponding);
                                restaurantActuallyShowed.setWorkmateList(workmateList);
                                singleUser.setRestaurantChosen(restaurantActuallyShowed);
                                firebaseDatabase.updateRestaurant(restaurantActuallyShowed);
                                firebaseDatabase.updateUser(singleUser);
                            }
                        }
                    });
                }

            }
            else {
                firebaseDatabase.getAllRestaurant(restaurantList -> {
                    if (restaurantList.isEmpty()){
                        if (!workmateList.contains(workmateCorresponding)) {
                            workmateList.add(workmateCorresponding);
                            restaurantActuallyShowed.setWorkmateList(workmateList);
                            singleUser.setRestaurantChosen(restaurantActuallyShowed);
                            firebaseDatabase.updateRestaurant(restaurantActuallyShowed);
                            firebaseDatabase.updateUser(singleUser);
                        }
                    }
                    else {
                        boolean containsActualRestaurant = false;
                        for (Restaurant restaurant1 : restaurantList) {
                            if (restaurant1.getAddress().equals(restaurantActuallyShowed.getAddress())){
                                containsActualRestaurant = true;
                                break;
                            }
                        }
                        if (!containsActualRestaurant){
                            if (!workmateList.contains(workmateCorresponding)) {
                                workmateList.add(workmateCorresponding);
                                restaurantActuallyShowed.setWorkmateList(workmateList);
                                singleUser.setRestaurantChosen(restaurantActuallyShowed);
                                firebaseDatabase.updateRestaurant(restaurantActuallyShowed);
                                firebaseDatabase.updateUser(singleUser);
                            }
                        }
                        firebaseDatabase.getRestaurant(restaurantActuallyShowed.getRestaurantId(), restaurant1 -> {
                            if (restaurant1 != null){
                                workmateList = restaurant1.getWorkmateList();
                                if (workmateList == null)
                                    workmateList = new ArrayList<>();

                                boolean contains = false;
                                for (Workmate workmate : workmateList) {
                                    if (workmate.getName().equals(workmateCorresponding.getName())){
                                        contains = true;
                                        break;
                                    }
                                }

                                if (!contains) {
                                    workmateList.add(workmateCorresponding);

                                    restaurant1.setWorkmateList(workmateList);
                                    firebaseDatabase.updateRestaurant(restaurant1);

                                    singleUser.setRestaurantChosen(restaurant1);
                                    firebaseDatabase.updateUser(singleUser);
                                }
                            }
                            else {
                                if (!workmateList.contains(workmateCorresponding)) {
                                    workmateList.add(workmateCorresponding);
                                    restaurantActuallyShowed.setWorkmateList(workmateList);
                                    singleUser.setRestaurantChosen(restaurantActuallyShowed);
                                    firebaseDatabase.updateRestaurant(restaurantActuallyShowed);
                                    firebaseDatabase.updateUser(singleUser);
                                }
                            }
                        });
                    }

                });
            }

        }));

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