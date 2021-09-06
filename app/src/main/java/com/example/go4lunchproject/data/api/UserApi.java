package com.example.go4lunchproject.data.api;

import com.example.go4lunchproject.data.firebase.FirebaseCloudDatabase;
import com.example.go4lunchproject.model.User;

public class UserApi {
    private User user;
    private static UserApi INSTANCE;
    private final String userId = FirebaseCloudDatabase.getInstance().getCurrentUserName() + "_" + FirebaseCloudDatabase.getInstance().getCurrentFirebaseUser().getUid();

    public UserApi() {
        user = new User();
    }

    public static UserApi getInstance() {
        if (INSTANCE == null)
            INSTANCE = new UserApi();

        return INSTANCE;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }
}
