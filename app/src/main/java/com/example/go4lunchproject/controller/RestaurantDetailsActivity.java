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
import com.example.go4lunchproject.adapter.WorkmateAdapterForRestaurantDetails;
import com.example.go4lunchproject.data.api.RestaurantSelectedApi;
import com.example.go4lunchproject.data.api.UserApi;
import com.example.go4lunchproject.data.firebase.MyFirebaseDatabase;
import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.model.User;
import com.example.go4lunchproject.model.Workmate;
import com.example.go4lunchproject.util.Constants;
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
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(RestaurantDetailsActivity.this));

        MyFirebaseDatabase.getInstance().getAllUsers(userList -> {
            if (userList != null) {
                for (User user : userList) {
                    Restaurant restaurantChosenFromFirebase = user.getRestaurantChosen();

                    if (restaurantChosenFromFirebase != null && restaurantChosenFromFirebase.getAddress().equals(restaurantActuallyShowed.getAddress())){
                        List<Workmate> workmateListFromFirebase = restaurantChosenFromFirebase.getWorkmateList();
                        if (workmateListFromFirebase != null){
                            for (Workmate workmate : workmateListFromFirebase) {
                                if (workmateList != null && !workmateList.contains(workmate))
                                    workmateList.add(workmate);
                            }
                        }
                    }
                }
            }

            adapter = new WorkmateAdapterForRestaurantDetails(workmateList);
            recyclerView.setAdapter(adapter);
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
        MyFirebaseDatabase.getInstance().getUser(userId, singleUser -> {
            Restaurant restaurantChosenFromFirebase = singleUser.getRestaurantChosen();
            if (restaurantChosenFromFirebase != null){
                if (restaurantChosenFromFirebase.getAddress().equals(restaurantActuallyShowed.getAddress()))
                    chosenImageView.setImageResource(R.mipmap.green_check_round);
                else
                    chosenImageView.setImageResource(R.mipmap.red_unchecked);
            }
            else
                chosenImageView.setImageResource(R.mipmap.red_unchecked);
        });
    }

    private void updateRestaurantWorkmateList(Workmate workmate, boolean removeWorkmate){

        if (workmateList == null)
            workmateList = new ArrayList<>();

        if (removeWorkmate)
            if (workmateList.contains(workmate))
                workmateList.remove(workmate);
        else
            if (!workmateList.contains(workmate))
                workmateList.add(workmate);

        restaurantActuallyShowed.setWorkmateList(workmateList);
    }
    private void indicateIfRestaurantIsChosenByWorkmate(){
        /*
        *   Show the list of users that choose the actual restaurant
        *       get the list of restaurant that chosen by all users from firebase
        *           check if that list contains the one actually showed.
        *               if it's the case then set the list of that restaurant to the recycler view.
        *               if it's not the case that means the restaurant actually showed has never be chosen by any user, so we do nothing.
        *
        *   When the user clicks on the restaurant :
        *       if the actual restaurant is the one he has chosen.
        *           we remove the actual user from the actual restaurant's workmate list.
        *           then we make the actual restaurant to be not chosen anymore.
        *           (we update the restaurant in firebase.)
        *           we update the user in  firebase.
        *
        *       if the actual restaurant is not the one the user has chosen
        *           we add the user to the workmate list of the actual restaurant.
        *           then we make the actual restaurant the one the user chose.
        *           (we save that restaurant in firebase, or update it if it was already saved.)
        *           we update the user in firebase.
        *
        *
        *
        * */
        //If workmate connected has chosen actual restaurant, set fab visibility to VISIBLE
        /*MyFirebaseDatabase.getInstance().getUser(userId, singleUser ->
                chosenImageView.setOnClickListener(view -> {
                    Restaurant restoChosenFromFirebase = singleUser.getRestaurantChosen();
                    boolean deleteWorkmateFromList = false;

                    if (restoChosenFromFirebase != null){
                        if (!restoChosenFromFirebase.getAddress().equals(restaurantActuallyShowed.getAddress())){
                            List<Workmate> list = restaurantActuallyShowed.getWorkmateList();
                            Workmate actualWorkmate = UtilMethods.setWorkmateCorresponding(singleUser);
                            if (list != null && !list.contains(actualWorkmate))
                                list.add(actualWorkmate);
                            singleUser.setRestaurantChosen(restaurantActuallyShowed);
                            chosenImageView.setImageResource(R.mipmap.green_check_round);
                        }
                        else{
                            deleteWorkmateFromList = true;
                            singleUser.setRestaurantChosen(null);
                            chosenImageView.setImageResource(R.mipmap.red_unchecked);
                        }
                    }
                    else{
                        singleUser.setRestaurantChosen(restaurantActuallyShowed);
                        chosenImageView.setImageResource(R.mipmap.green_check_round);
                    }

                    //updateRestaurantWorkmateList(ActualWorkmateApi.getInstance().getWorkmate(), false);
                    UserApi.getInstance().setUser(singleUser);
                    MyFirebaseDatabase.getInstance().updateUser(singleUser);
                    updateRestaurantWorkmateList(UtilMethods.setWorkmateCorresponding(singleUser), deleteWorkmateFromList);
                }));*/
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