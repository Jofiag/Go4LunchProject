package com.example.go4lunchproject.controller;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.go4lunchproject.R;
import com.example.go4lunchproject.data.api.UserApi;
import com.example.go4lunchproject.data.firebase.FirebaseCloudDatabase;
import com.example.go4lunchproject.model.UserSettings;

public class SettingsActivity extends AppCompatActivity {
    private SwitchCompat switchButton;
    private Spinner sortingSpinner;

    private final FirebaseCloudDatabase firebaseCloudDatabase = FirebaseCloudDatabase.getInstance();
    private final String userId = UserApi.getInstance().getUserId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setReferences();

        setSwitchButtonState();
        manageNotifications();

        setSortingSpinner();
    }

    private void setReferences() {
        switchButton = findViewById(R.id.notification_switch_button);
        sortingSpinner = findViewById(R.id.sorting_spinner);
    }

    private void setSwitchButtonState(){
        firebaseCloudDatabase.getUser(userId, singleUser -> {
            if (singleUser != null){
                UserSettings userSettings = singleUser.getUserSettings();
                if (userSettings != null)
                    switchButton.setChecked(userSettings.isNotificationOn());
            }
        });

    }
    private void manageNotifications(){
        Bundle bundle = new Bundle();

        firebaseCloudDatabase.getUser(userId, singleUser -> switchButton.setOnClickListener(view -> {
            UserSettings userSettings = new UserSettings();
            userSettings.setNotificationOn(switchButton.isChecked());
            singleUser.setUserSettings(userSettings);
            firebaseCloudDatabase.updateUser(singleUser);
        }));
    }

    private void setSortingSpinner(){
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.sorting_list_options_array, android.R.layout.simple_spinner_item);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sortingSpinner.setAdapter(arrayAdapter);
    }
}