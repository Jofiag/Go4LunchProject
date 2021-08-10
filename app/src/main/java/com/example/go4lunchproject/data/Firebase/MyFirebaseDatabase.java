package com.example.go4lunchproject.data.Firebase;

import android.util.Log;

import androidx.annotation.NonNull;

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
    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://go4lunchproject-6c727-default-rtdb.europe-west1.firebasedatabase.app");
    private final DatabaseReference userDataRef = database.getReference(Constants.USER_DATA_REF);
    private static MyFirebaseDatabase INSTANCE;

    public MyFirebaseDatabase() {
    }

    public static MyFirebaseDatabase getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MyFirebaseDatabase();
        return INSTANCE;
    }

    public void saveUser(User user){
        userDataRef.child(user.getId()).setValue(user)
                .addOnSuccessListener(unused -> Log.d("SAVING", "onSuccess: User saved with success!!!"))
                .addOnFailureListener(e -> Log.d("SAVING", "onFailure: " + e.getMessage()));
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

}
