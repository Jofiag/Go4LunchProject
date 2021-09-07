package com.example.go4lunchproject.data.firebase;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.model.User;
import com.example.go4lunchproject.model.UserSettings;
import com.example.go4lunchproject.model.Workmate;
import com.example.go4lunchproject.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FirebaseCloudDatabase {
    private static final String TAG = "CLOUDDATABASE";

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

    private final FirebaseFirestore cloudDatabase = FirebaseFirestore.getInstance();
    private final CollectionReference userCollectionRef = cloudDatabase.collection(Constants.USER_DATA_REF);
    private final CollectionReference restaurantChosenCollectionRef = cloudDatabase.collection(Constants.RESTAURANT_CHOSEN_REFERENCE);
    private final CollectionReference restaurantNearbyCollectionRef = cloudDatabase.collection(Constants.RESTAURANT_NEARBY_REFERENCE);

    private String currentUserName;
    private static FirebaseCloudDatabase INSTANCE;
    private final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private final String userId = this.getCurrentUserName() + "_" + this.getCurrentFirebaseUser().getUid();



    public FirebaseCloudDatabase() {
    }

    public static FirebaseCloudDatabase getInstance() {
        if (INSTANCE == null)
            INSTANCE = new FirebaseCloudDatabase();
        return INSTANCE;
    }



    public void saveUser(User user){
        if (user.getId() != null) {
            userCollectionRef.document(user.getId()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        User userTemp = documentSnapshot.toObject(User.class);

                        if (userTemp == null){
                            userCollectionRef.document(user.getId())
                                    .set(user)
                                    .addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: Saving user succeed"))
                                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e));
                        }

                    });

        }
        else throw new RuntimeException("The user id must not be null");
    }

    public void getUser(SingleUserFromFirebase callback){
        userCollectionRef.document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (callback != null)
                        callback.onSingleUserGotten(user);
                })
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e));
    }

    public void getAllUsers(UserListFromFirebase callback) {
        List<User> userList = new ArrayList<>();

        userCollectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            User user = queryDocumentSnapshot.toObject(User.class);
                            userList.add(user);
                        }
                    }
                    if (callback != null)
                        callback.onListGotten(userList);
                })
                .addOnFailureListener(e -> Log.d(TAG, "getAllUsers: " + e));
    }

    public void listenToUser(SingleUserFromFirebase callback){
        userCollectionRef.document(userId)
                .addSnapshotListener((value, error) -> {
                    if (value != null){
                        User user = value.toObject(User.class);
                        if (callback != null)
                            callback.onSingleUserGotten(user);
                    }
                    else
                    if (callback != null)
                        callback.onSingleUserGotten(null);
                });
    }

    public void listenToAllUsers(UserListFromFirebase callback){
        userCollectionRef.addSnapshotListener((value, error) -> {
            if (value != null && !value.isEmpty()) {
                List<User> userList = new ArrayList<>();

                for (QueryDocumentSnapshot queryDocumentSnapshot : value) {
                    User user = queryDocumentSnapshot.toObject(User.class);
                    userList.add(user);
                }

                if (callback != null)
                    callback.onListGotten(userList);
            }

        });
    }

    public void updateUser(User newUser){
        if (newUser.getId() != null) {
            userCollectionRef.document(newUser.getId()).set(newUser)
                    .addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: Updating user SUCCEED"))
                    .addOnFailureListener(e -> Log.d(TAG, "onFailure" + e.getMessage()));
        }
        else throw new RuntimeException("User path is null");
    }

    public void updateUserRestaurantChosen(Restaurant newRestaurant){
        userCollectionRef.document(userId).update("restaurantChosen", newRestaurant)
                .addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: Updating user restaurant chosen SUCCEED"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure" + e.getMessage()));
    }

    public void updateUserSettings(UserSettings newSettings){
        userCollectionRef.document(userId).update("userSettings", newSettings)
                .addOnSuccessListener(unused -> Log.d(TAG, "updateUserSettings: Updating user settings SUCCEED"))
                .addOnFailureListener(e -> Log.d(TAG, "updateUserSettings: " + e.getMessage()));
    }

    public void updateUserRestaurantLikedList(List<Restaurant> newRestaurantLikedList){
        userCollectionRef.document(userId)
                .update("restaurantLikedList", newRestaurantLikedList)
                .addOnSuccessListener(unused -> Log.d(TAG, "updateUserRestaurantLikedList: updating restaurant liked list SUCCEED"))
                .addOnFailureListener(e -> Log.d(TAG, "updateUserRestaurantLikedList: " + e.getMessage()));
    }

    public void deleteUser(){
        userCollectionRef.document(userId)
                .delete()
                .addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: deleting user " + userId + " succeed"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure" + e.getMessage()));

    }



    public void saveRestaurantNearbyList(ArrayList<Restaurant> restaurantList){
            if (restaurantList != null && !restaurantList.isEmpty()){
                //We save each restaurant from the list instead of saving the list directly to prevent having redundant restaurant.
                for (Restaurant restaurant : restaurantList) {
                    restaurantNearbyCollectionRef.document(restaurant.getRestaurantId())
                            .set(restaurant)
                            .addOnSuccessListener(unused -> Log.d(TAG, "saveRestaurantNearbyList: Saving restaurant nearby SUCCEED"))
                            .addOnFailureListener(e -> Log.d(TAG, "saveRestaurantNearbyList: " + e.getMessage()));
                }
            }
    }

    public void getRestaurantNearby(String restaurantId,RestaurantFromFirebase callback){
        if (restaurantId != null) {
            restaurantNearbyCollectionRef.document(restaurantId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                        if (callback != null)
                            callback.onRestaurantGotten(restaurant);
                    })
                    .addOnFailureListener(e -> Log.d(TAG, "getRestaurantNearby: " + e));
        }
        else throw new RuntimeException("The restaurant id must not be null");
    }

    public void getAllRestaurantNearby(RestaurantListFromFirebase callback){
        List<Restaurant> restaurantList = new ArrayList<>();

        restaurantNearbyCollectionRef
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            Restaurant restaurant = queryDocumentSnapshot.toObject(Restaurant.class);
                            restaurantList.add(restaurant);
                        }
                    }
                    if (callback != null)
                        callback.onListGotten(restaurantList);
                })
                .addOnFailureListener(e -> Log.d(TAG, "getAllRestaurantNearby: " + e));
    }

    public void updateRestaurantNearby(Restaurant newRestaurant){
        if (newRestaurant != null && newRestaurant.getRestaurantId() != null) {
            restaurantNearbyCollectionRef
                    .document(newRestaurant.getRestaurantId()).set(newRestaurant)
                    .addOnSuccessListener(unused -> Log.d(TAG, "updateRestaurantNearby: Updating restaurant SUCCEED"))
                    .addOnFailureListener(e -> Log.d(TAG, "updateRestaurantNearby: " + e.getMessage()));
        }
        else throw new RuntimeException("The new restaurant and it's id must not be null");
    }

    public void updateRestaurantNearbyWorkmateList(String restaurantId, List<Workmate> workmateList){
        if (restaurantId != null) {
            restaurantNearbyCollectionRef.document(restaurantId)
                    .update("workmateList", workmateList)
                    .addOnSuccessListener(unused -> Log.d(TAG, "updateRestaurantNearbyWorkmateList: Updating restaurant workmate list SUCCEED"))
                    .addOnFailureListener(e -> Log.d(TAG, "updateRestaurantNearbyWorkmateList: " + e.getMessage()));
        }
        else throw new RuntimeException("The restaurant id must not be null");
    }



    public void saveRestaurantChosen(Restaurant restaurant){
        if (restaurant != null && restaurant.getRestaurantId() != null) {
            restaurantChosenCollectionRef.document(restaurant.getRestaurantId())
                    .set(restaurant)
                    .addOnSuccessListener(unused -> Log.d(TAG, "saveRestaurantChosen: Saving restaurant SUCCEED"))
                    .addOnFailureListener(e -> Log.d(TAG, "saveRestaurantChosen: " + e.getMessage()));
        }
        else throw new RuntimeException("The restaurant and it's id must not be null");
    }

    public void getRestaurantChosen(String restaurantId, RestaurantFromFirebase callback){
        if (restaurantId != null) {
            restaurantChosenCollectionRef.document(restaurantId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                        if (callback != null)
                            callback.onRestaurantGotten(restaurant);
                    })
                    .addOnFailureListener(e -> Log.d(TAG, "getRestaurantChosen: " + e));
        }
        else throw new RuntimeException("The restaurant id must not be null");
    }

    public void getAllRestaurantChosen(RestaurantListFromFirebase callback){
        List<Restaurant> restaurantList = new ArrayList<>();

        restaurantChosenCollectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            Restaurant restaurant = queryDocumentSnapshot.toObject(Restaurant.class);
                            restaurantList.add(restaurant);
                        }
                    }
                    if (callback != null)
                        callback.onListGotten(restaurantList);
                })
                .addOnFailureListener(e -> Log.d(TAG, "getAllRestaurantChosen: " + e));
    }

    public void listenToRestaurantChosen(String restaurantId, RestaurantFromFirebase callback){
        restaurantChosenCollectionRef.document(restaurantId)
                .addSnapshotListener((value, error) -> {
                    if (value != null){
                        Restaurant restaurant = value.toObject(Restaurant.class);
                        if (callback != null)
                            callback.onRestaurantGotten(restaurant);
                    }
                    else
                        if (callback != null)
                            callback.onRestaurantGotten(null);
                });
    }

    public void listenToAllRestaurantChosen(RestaurantListFromFirebase callback){
        restaurantChosenCollectionRef.addSnapshotListener((value, error) -> {
            if (value != null && !value.isEmpty()) {
                List<Restaurant> restaurantList = new ArrayList<>();

                for (QueryDocumentSnapshot queryDocumentSnapshot : value) {
                    Restaurant restaurant = queryDocumentSnapshot.toObject(Restaurant.class);
                    restaurantList.add(restaurant);
                }

                if (callback != null)
                    callback.onListGotten(restaurantList);
            }

        });
    }

    public void updateRestaurantChosen(Restaurant newRestaurant){
        if (newRestaurant != null && newRestaurant.getRestaurantId() != null) {
            restaurantChosenCollectionRef.document(newRestaurant.getRestaurantId())
                    .set(newRestaurant)
                    .addOnSuccessListener(unused -> Log.d(TAG, "updateRestaurantChosen: Updating restaurant SUCCEED"))
                    .addOnFailureListener(e -> Log.d(TAG, "updateRestaurantChosen: " + e.getMessage()));
        }
        else throw new RuntimeException("The new restaurant and it's id must not be null");
    }

    public void updateRestaurantChosenWorkmateList(String restaurantId, List<Workmate> workmateList){
        if (restaurantId != null) {
            restaurantChosenCollectionRef.document(restaurantId)
                    .update("workmateList", workmateList)
                    .addOnSuccessListener(unused -> Log.d(TAG, "updateRestaurantChosenWorkmateList: Updating restaurant workmate list SUCCEED"))
                    .addOnFailureListener(e -> Log.d(TAG, "updateRestaurantChosenWorkmateList: " + e.getMessage()));
        }
        else throw new RuntimeException("The restaurant id must not be null");
    }

    public void deleteRestaurantChosen(String restaurantId){
        if (restaurantId != null) {
            restaurantChosenCollectionRef.document(restaurantId)
                    .delete()
                    .addOnSuccessListener(unused -> Log.d(TAG, "deleteRestaurantChosen: deleting user " + restaurantId + " succeed"))
                    .addOnFailureListener(e -> Log.d(TAG, "deleteRestaurantChosen" + e.getMessage()));
        }
        else throw new RuntimeException("The restaurant id must not be null");

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

    public String getUserId() {
        return userId;
    }
}
