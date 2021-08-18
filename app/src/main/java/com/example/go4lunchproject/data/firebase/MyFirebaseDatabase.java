package com.example.go4lunchproject.data.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.go4lunchproject.data.api.UserApi;
import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.model.User;
import com.example.go4lunchproject.util.Constants;
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
    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://go4lunchproject-6c727-default-rtdb.europe-west1.firebasedatabase.app");
    private final DatabaseReference userDataRef = database.getReference(Constants.USER_DATA_REF);
    private final DatabaseReference restaurantChosenRef = database.getReference(Constants.RESTAURANT_CHOSEN_REFERENCE);
    private static MyFirebaseDatabase INSTANCE;

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

                }
            });
        }
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

    }

}
