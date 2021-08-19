package com.example.go4lunchproject.data.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.go4lunchproject.data.api.UserApi;
import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.model.User;
import com.example.go4lunchproject.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyFirebaseDatabase {
    public interface UserListFromFirebase{
        void onListGotten(List<User> userList);
    }

    public interface SingleUserFromFirebase{
        void onSingleUserGotten(User singleUser);
    }

    public interface RestaurantFromFirebase{
        void onRestaurantGotten(Restaurant restaurant);
    }

    public interface RestaurantListFromFirebase{
        void onListGotten(List<Restaurant> restaurantList);
    }

    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://go4lunchproject-6c727-default-rtdb.europe-west1.firebasedatabase.app");
    private final DatabaseReference userDataRef = database.getReference(Constants.USER_DATA_REF);
    private final DatabaseReference restaurantChosenRef = database.getReference(Constants.RESTAURANT_CHOSEN_REFERENCE);
    private static MyFirebaseDatabase INSTANCE;
    private final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private String currentUserName;

    public MyFirebaseDatabase() {
    }

    public static MyFirebaseDatabase getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MyFirebaseDatabase();
        return INSTANCE;
    }

    public void saveUser(User user){
        userDataRef.child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    User userFromApi = UserApi.getInstance().getUser();
                    userDataRef.child(user.getId()).setValue(user)
                            .addOnSuccessListener(unused -> Log.d("SAVING", "onSuccess: User saved with success!!!"))
                            .addOnFailureListener(e -> Log.d("SAVING", "onFailure: " + e.getMessage()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getUser(String path, SingleUserFromFirebase callback){
        if (path != null) {
            userDataRef.child(path).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        User user = snapshot.getValue(User.class);
                        if (callback != null)
                            callback.onSingleUserGotten(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("UserE", "onCancelled: " + error.getMessage());
                }
            });
        }
        else throw new RuntimeException("User path is null");
    }

    public void getAllUsers(UserListFromFirebase callback) {
        List<User> userList = new ArrayList<>();

        userDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (!userList.contains(user))
                            userList.add(user);
                    }

                    if (callback != null)
                        callback.onListGotten(userList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("GETTING", "onCancelled: " + error.getMessage());
            }
        });
    }

    public void updateUser(User newUser){
        userDataRef.child(newUser.getId()).setValue(newUser);
    }



    public void saveRestaurant(Restaurant restaurant){
        restaurantChosenRef.child(restaurant.getRestaurantId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                    restaurantChosenRef.child(restaurant.getRestaurantId()).setValue(restaurant)
                            .addOnSuccessListener(unused -> Log.d("SAVING", "onSuccess: Restaurant saved with success!!!"))
                            .addOnFailureListener(e -> Log.d("SAVING", "onFailure: " + e.getMessage()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getRestaurant(String path, RestaurantFromFirebase callback){
        if (path != null){
            restaurantChosenRef.child(path).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        Restaurant restaurant = snapshot.getValue(Restaurant.class);
                        if (callback != null)
                            callback.onRestaurantGotten(restaurant);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("RestoE", "onCancelled: " + error.getMessage());
                }
            });
        }
        else throw new RuntimeException("restaurant path is null");
    }

    public void getAllRestaurant(RestaurantListFromFirebase callback){
        List<Restaurant> restaurantList = new ArrayList<>();

        restaurantChosenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Restaurant restaurant = child.getValue(Restaurant.class);
                        if (!restaurantList.contains(restaurant))
                            restaurantList.add(restaurant);
                    }

                }
                if (callback != null)
                    callback.onListGotten(restaurantList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateRestaurant(Restaurant newRestaurant){
        restaurantChosenRef.child(newRestaurant.getRestaurantId()).setValue(newRestaurant);
    }

    public FirebaseUser getCurrentFirebaseUser() {
        return currentFirebaseUser;
    }

    public String getCurrentUserName(){
        String name = "";
        if (currentFirebaseUser != null)
            name = currentFirebaseUser.getDisplayName();

        return name;
    }

}
