package com.example.go4lunchproject.data.firebase;

import android.util.Log;

import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.model.User;
import com.example.go4lunchproject.model.Workmate;
import com.example.go4lunchproject.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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
    private final CollectionReference restaurantCollectionRef = cloudDatabase.collection(Constants.RESTAURANT_CHOSEN_REFERENCE);

    private String currentUserName;
    private static FirebaseCloudDatabase INSTANCE;
    private final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public FirebaseCloudDatabase() {
    }

    public static FirebaseCloudDatabase getInstance() {
        if (INSTANCE == null)
            INSTANCE = new FirebaseCloudDatabase();
        return INSTANCE;
    }

    public void saveUser(User user){
        userCollectionRef.document(user.getId())
                .set(user)
                .addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: Saving user succeed"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e));
    }

    public void getUser(String userId, SingleUserFromFirebase callback){
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

    public void updateUser(User newUser){
        userCollectionRef.document(newUser.getId()).set(newUser)
                .addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: Updating user SUCCEED"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure" + e.getMessage()));
    }

    public void updateUserRestaurantChosen(String userId, Restaurant newRestaurant){
        userCollectionRef.document(userId).update("restaurantChosen", newRestaurant)
                .addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: Updating user restaurant chosen SUCCEED"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure" + e.getMessage()));
    }

    public void deleteUser(String userId){
        userCollectionRef.document(userId)
                .delete()
                .addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: deleting user " + userId + " succeed"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure" + e.getMessage()));

    }



    public void saveRestaurant(Restaurant restaurant){
        restaurantCollectionRef.document(restaurant.getRestaurantId())
                .set(restaurant)
                .addOnSuccessListener(unused -> Log.d(TAG, "saveRestaurant: Saving restaurant SUCCEED"))
                .addOnFailureListener(e -> Log.d(TAG, "saveRestaurant: " + e.getMessage()));
    }

    public void getRestaurant(String restaurantId, RestaurantFromFirebase callback){
        restaurantCollectionRef.document(restaurantId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                    if (callback != null)
                        callback.onRestaurantGotten(restaurant);
                })
                .addOnFailureListener(e -> Log.d(TAG, "getRestaurant: " + e));
    }

    public void getAllRestaurant(RestaurantListFromFirebase callback){
        List<Restaurant> restaurantList = new ArrayList<>();

        restaurantCollectionRef.get()
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
                .addOnFailureListener(e -> Log.d(TAG, "getAllUsers: " + e));
    }

    public void updateRestaurant(Restaurant newRestaurant){
        restaurantCollectionRef.document(newRestaurant.getRestaurantId()).set(newRestaurant)
                .addOnSuccessListener(unused -> Log.d(TAG, "updateRestaurant: Updating restaurant SUCCEED"))
                .addOnFailureListener(e -> Log.d(TAG, "updateRestaurant: " + e.getMessage()));
    }

    public void updateRestaurantWorkmateList(String restaurantId, List<Workmate> workmateList){
        restaurantCollectionRef.document(restaurantId).update("workmateList", workmateList)
                .addOnSuccessListener(unused -> Log.d(TAG, "updateRestaurantWorkmateList: Updating restaurant workmate list SUCCEED"))
                .addOnFailureListener(e -> Log.d(TAG, "updateRestaurantWorkmateList: " + e.getMessage()));
    }

    public void deleteRestaurant(String restaurantId){
        restaurantCollectionRef.document(restaurantId)
                .delete()
                .addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: deleting user " + restaurantId + " succeed"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure" + e.getMessage()));

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
