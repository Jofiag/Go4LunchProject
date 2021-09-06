package com.example.go4lunchproject.controller;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
import com.example.go4lunchproject.data.firebase.FirebaseCloudDatabase;
import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.model.User;
import com.example.go4lunchproject.model.Workmate;
import com.example.go4lunchproject.notification.AlarmReceiver;
import com.example.go4lunchproject.util.Constants;
import com.example.go4lunchproject.util.UtilMethods;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

@RequiresApi(api = Build.VERSION_CODES.O)
public class RestaurantDetailsActivity extends AppCompatActivity {

    public interface AccessToRestaurantUpdated {
        void onResponse(Restaurant restaurantUpdated);
    }

    public static final String CALL_PERMISSION = Manifest.permission.CALL_PHONE;
    public static final int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;

    private ImageView yellowStar;
    private ImageView callImageView;
    private ImageView starImageView;
    private ImageView globeImageView;
    private RecyclerView recyclerView;
    private ImageView restaurantImageView;
    private TextView restaurantNameTextView;
    private CircleImageView chosenImageView;
    private TextView RestaurantFoodCountryAndRestaurantAddress;

    private String userId;
    private Restaurant restaurantActuallyShowed;

    private WorkmateAdapterForRestaurantDetails adapter;
    private final FirebaseCloudDatabase firebaseCloudDatabase = FirebaseCloudDatabase.getInstance();


    private Drawable greenCheck;
    private Drawable redUncheck;

    private Workmate actualWorkmate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        setReferences();

        initializeUserAndRestaurantShowed();

        setYellowStarVisibility();
        setChosenImageVisibility();

        showRestaurantImageNameAndAddress();
        setCallRestaurantFunction();
        setLikeRestaurantFunction();
        setGoToRestaurantWebsiteFunction();

        indicateIfRestaurantIsChosenByWorkmate();
        setRecyclerView();
        retrieveActualWorkmateFromHisLastRestaurantChosen();
        deleteRestaurantThatAreNotChosenAnymore();

//        setNotification();
//        alarmNotification();
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

    private void initializeUserAndRestaurantShowed(){
        userId = UserApi.getInstance().getUserId();

        User user = getUserWithNameAndPhotoUrlOnly();
        actualWorkmate = UtilMethods.setWorkmateCorresponding(user);
        restaurantActuallyShowed = RestaurantSelectedApi.getInstance().getRestaurantSelected();
    }

    private User getUserConnected(){
        return UserApi.getInstance().getUser();
    }

    private void setYellowStarVisibility(){
        firebaseCloudDatabase.getUser(userId, singleUser -> {
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

        // Get the list of restaurant that chosen by all users from firebase each time it changes.
        firebaseCloudDatabase.listenToAllRestaurant(restaurantList -> {
            //if that list is not null and not empty
            if (restaurantList != null && !restaurantList.isEmpty()){
                //then we check if that list contains the one actually showed.
                for (Restaurant restaurant : restaurantList) {
                    //if it's the case then set the list of that restaurant to the recycler view.
                    if (restaurant != null && restaurant.getAddress() != null) {
                        if (restaurant.getAddress().equals(restaurantActuallyShowed.getAddress())) {
                            adapter = new WorkmateAdapterForRestaurantDetails(restaurant.getWorkmateList());
                            recyclerView.setAdapter(adapter);
                            break;
                        }
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
        firebaseCloudDatabase.updateUser(user);
        Toast.makeText(this, restaurantActuallyShowed.getName() + status, Toast.LENGTH_SHORT).show();
    }

    private void setLikeRestaurantFunction(){
        firebaseCloudDatabase.getUser(userId, singleUser -> starImageView.setOnClickListener(v -> {

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

        firebaseCloudDatabase.getUser(userId, singleUser -> {
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
        firebaseCloudDatabase.getUser(userId, singleUser ->
                chosenImageView.setOnClickListener(view -> {
                    Restaurant restaurantChosen = singleUser.getRestaurantChosen();
                    if (restaurantChosen != null && restaurantChosen.getAddress().equals(restaurantActuallyShowed.getAddress())){

                        //Remove the actual workmate to the actual restaurant and update that restaurant
                        updateActualRestaurantAfterDeleteWorkmate(restaurantUpdated -> {
                            firebaseCloudDatabase.updateRestaurant(restaurantUpdated);
                            //Set the restaurant chosen by the actual workmate as null if the actual restaurant was his/her choice and update the user in firebase.
                            Restaurant lastRestaurantChosen = singleUser.getRestaurantChosen();
                            if (lastRestaurantChosen != null && lastRestaurantChosen.getAddress().equals(restaurantActuallyShowed.getAddress()))
                                singleUser.setRestaurantChosen(null);

                            firebaseCloudDatabase.updateUser(singleUser);

                            chosenImageView.setImageDrawable(redUncheck);


                        });
                    }
                    else if (restaurantChosen == null || !restaurantChosen.getAddress().equals(restaurantActuallyShowed.getAddress())){
                        //Add the actual workmate to the actual restaurant and update that restaurant
                        updateActualRestaurantAfterAddWorkmate(restaurantUpdated -> {
                            //Set the restaurant updated as the on chosen by the actual workmate and update the user in firebase.
                            singleUser.setRestaurantChosen(restaurantUpdated);
                            firebaseCloudDatabase.updateUser(singleUser);

                            chosenImageView.setImageDrawable(greenCheck);
                        });
                    }

                }));
    }

    private User getUserWithNameAndPhotoUrlOnly(){
        User userWithNameAndImageUriOnly = new User();
        userWithNameAndImageUriOnly.setName(firebaseCloudDatabase.getCurrentUserName());

        if (firebaseCloudDatabase.getCurrentFirebaseUser().getPhotoUrl() != null)
            userWithNameAndImageUriOnly.setImageUrl(firebaseCloudDatabase.getCurrentFirebaseUser().getPhotoUrl().toString());

        return userWithNameAndImageUriOnly;
    }

    private List<Workmate> getWorkmateListFromARestaurant(Restaurant restaurant){
        List<Workmate> workmates = restaurant.getWorkmateList();

        if (workmates == null)
            workmates = new ArrayList<>();

        return workmates;
    }

    private Restaurant updateRestaurantListAndRestaurant(List<Workmate> workmates, Restaurant restaurant){
        restaurant.setWorkmateList(workmates);
//        firebaseCloudDatabase.updateRestaurant(restaurant);
        firebaseCloudDatabase.updateRestaurantWorkmateList(restaurant.getRestaurantId(), workmates);

        return restaurant;
    }

    private boolean containsWorkmate(List<Workmate> workmates, Workmate workmate){
        boolean contains = false;

        for (Workmate workmate1 : workmates) {
            if (workmate1.getName().equals(workmate.getName())){
                contains = true;
                break;
            }
        }

        return contains;
    }

    private void addWorkmate(List<Workmate> workmates, Workmate workmate){
        if (!containsWorkmate(workmates, workmate))
            workmates.add(workmate);
    }

    private List<Workmate> removeWorkmate(List<Workmate> workmates, Workmate workmate){
        if (workmates != null && !workmates.isEmpty()) {
            for (Workmate workmate1 : workmates) {
                if (workmate1.getName().equals(workmate.getName())) {
                    workmates.remove(workmate1);
                    break;
                }
            }
        }

        return workmates;
    }

    private void updateActualRestaurantAfterAddWorkmate(AccessToRestaurantUpdated callback){
        User user = getUserWithNameAndPhotoUrlOnly();
        Workmate actualWorkmate = UtilMethods.setWorkmateCorresponding(user);

        //Add or remove the actual workmate to the actual restaurant showed workmate list.
        firebaseCloudDatabase.getRestaurant(restaurantActuallyShowed.getRestaurantId(), restaurant -> {
            Restaurant restaurantTemp;

            if (restaurant != null)
                restaurantTemp = restaurant;
            else
                restaurantTemp = restaurantActuallyShowed;

            List<Workmate> workmates = getWorkmateListFromARestaurant(restaurantTemp);

            addWorkmate(workmates, actualWorkmate);
            chosenImageView.setImageDrawable(greenCheck);

            restaurantTemp.setWorkmateList(workmates);

            //update the restaurant in firebase
            firebaseCloudDatabase.updateRestaurant(restaurantTemp);

            if (callback != null)
                callback.onResponse(restaurantTemp);
        });

    }

    private void updateActualRestaurantAfterDeleteWorkmate(AccessToRestaurantUpdated callback){


        //Remove the actual workmate to the actual restaurant showed workmate list.
        firebaseCloudDatabase.getRestaurant(restaurantActuallyShowed.getRestaurantId(), restaurant -> {
            Restaurant restaurantTemp;

            if (restaurant != null)
                restaurantTemp = restaurant;
            else
                restaurantTemp = restaurantActuallyShowed;

            List<Workmate> workmates = getWorkmateListFromARestaurant(restaurantTemp);

            removeWorkmate(workmates, actualWorkmate);
            chosenImageView.setImageDrawable(redUncheck);

            restaurantTemp.setWorkmateList(workmates);

            //update the restaurant in firebase
            firebaseCloudDatabase.updateRestaurant(restaurantTemp);

            if (callback != null)
                callback.onResponse(restaurantTemp);
        });

    }

    private void retrieveActualWorkmateFromHisLastRestaurantChosen(){
        firebaseCloudDatabase.listenToAllRestaurant(restaurantList -> {
            if (restaurantList != null && !restaurantList.isEmpty()){

                for (Restaurant restaurant : restaurantList) {
                    //We check if the actual restaurant of the list contains in it's workmate list the actual workmate
                    boolean containsActualWorkmate = containsWorkmate(restaurant.getWorkmateList(), actualWorkmate);

                    //We get the actual restaurant chosen by the actual workmate
                    firebaseCloudDatabase.getUser(userId, singleUser -> {
                        Restaurant userRestaurant = singleUser.getRestaurantChosen();

                        if (userRestaurant != null){
                            // We check if the restaurant chosen by the actual workmate is the same with the actual restaurant of the list
                            boolean isTheSame = userRestaurant.getAddress().equals(restaurant.getAddress());

                            //if the actual restaurant of the list contains in it's workmate list the actual workmate AND
                            //the that restaurant is not the same with the one chosen by the workmate
                            //then we remove the actual workmate from the list of the restaurant and update it in firebase.
                            if (containsActualWorkmate && !isTheSame) {
                                List<Workmate> list = removeWorkmate(restaurant.getWorkmateList(), actualWorkmate);
                                firebaseCloudDatabase.updateRestaurantWorkmateList(restaurant.getRestaurantId(), list);
                            }
                        }
                    });
                }
            }
        });

        /*firebaseCloudDatabase.getAllRestaurant(restaurantList -> {
            if (restaurantList != null && !restaurantList.isEmpty()){

                for (Restaurant restaurant : restaurantList) {
                    //We check if the actual restaurant of the list contains in it's workmate list the actual workmate
                    boolean containsActualWorkmate = containsWorkmate(restaurant.getWorkmateList(), actualWorkmate);

                    //We get the actual restaurant chosen by the actual workmate
                    firebaseCloudDatabase.getUser(userId, singleUser -> {
                        Restaurant userRestaurant = singleUser.getRestaurantChosen();

                        if (userRestaurant != null){
                            // We check if the restaurant chosen by the actual workmate is the same with the actual restaurant of the list
                            boolean isTheSame = userRestaurant.getAddress().equals(restaurant.getAddress());

                            //if the actual restaurant of the list contains in it's workmate list the actual workmate AND
                            //the that restaurant is not the same with the one chosen by the workmate
                            //then we remove the actual workmate from the list of the restaurant and update it in firebase.
                            if (containsActualWorkmate && !isTheSame) {
                                List<Workmate> list = removeWorkmate(restaurant.getWorkmateList(), actualWorkmate);
                                firebaseCloudDatabase.updateRestaurantWorkmateList(restaurant.getRestaurantId(), list);
                            }
                        }
                    });
                }
            }
        });*/
    }

    private void deleteRestaurantThatAreNotChosenAnymore(){
        firebaseCloudDatabase.listenToAllRestaurant(restaurantList -> {
            if (restaurantList != null && !restaurantList.isEmpty()){
                for (Restaurant restaurant : restaurantList) {
                    List<Workmate> workmateList = restaurant.getWorkmateList();

                    if (workmateList == null || workmateList.isEmpty())
                        firebaseCloudDatabase.deleteRestaurant(restaurant.getRestaurantId());
                }
            }
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


    //Notification
    private void alarmNotification(){
        Intent intent = new Intent(RestaurantDetailsActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(RestaurantDetailsActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 20);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2000, AlarmManager.INTERVAL_DAY, pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ 10000, pendingIntent);


        /*firebaseCloudDatabase.listenToUser(userId, singleUser -> {
            if (singleUser != null){
                Restaurant restaurant = singleUser.getRestaurantChosen();
                if (restaurant != null){
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2000, AlarmManager.INTERVAL_DAY, pendingIntent);
//                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                }
            }
        });*/

    }

    /*private void setNotification(){
        Notification notification = new Notification(this);

        notification.setMoreThanOneLine(true);
        notification.setPriority(NotificationCompat.PRIORITY_MAX);
        notification.setTitle(Constants.RESTAURANT_NOTIFICATION_TITLE);
        notification.setSmallIconResourceId(R.mipmap.ic_launcher_round);
        notification.setTapActionIntent(new Intent(getApplicationContext(), RestaurantDetailsActivity.class));

        //Notification channel is needed for API 26 and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(Constants.CHANNEL_ID);
            notification.setChannelName(Constants.CHANNEL_NAME);
            notification.setChannelDescription(Constants.CHANNEL_DESCRIPTION);
            notification.setChannelImportance(NotificationManager.IMPORTANCE_MAX);
        }

        firebaseCloudDatabase.listenToUser(userId, singleUser -> {
            if (singleUser != null){
                Restaurant restaurant = singleUser.getRestaurantChosen();
                if (restaurant != null && itIs12()){
                    notification.setContentText(getNotificationContent(restaurant));
                    notification.initializeNotification();
                    notification.showNotification(Constants.RESTAURANT_NOTIFICATION_ID);
                }
            }
        });

    }

    private String getNotificationContent(Restaurant restaurant){
        StringBuilder notificationContent = new StringBuilder();

        if (restaurant != null){
            notificationContent.append(restaurant.getName()).append("\n")
                    .append(restaurant.getAddress()).append("\n");

            List<Workmate> list = restaurant.getWorkmateList();

            if (list != null && !list.isEmpty()) {
                StringBuilder workmatesName = new StringBuilder();

                for (Workmate workmate : list)
                    workmatesName.append(workmate.getName()).append(" is joining").append("\n");

                notificationContent.append(workmatesName);
            }
        }

        return notificationContent.toString();
    }

    private boolean itIs12(){
        boolean itIs12 = false;

        if (LocalTime.now().getHour() == 12)
            itIs12 = true;

        return itIs12;
    }*/
}