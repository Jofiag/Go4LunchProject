package com.example.go4lunchproject.model;

import com.google.firebase.auth.FirebaseUser;

public class User extends Workmate {
    private String id;
    private String userEmail;
    private FirebaseUser firebaseUser;

    public User() {
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }
    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }
}
