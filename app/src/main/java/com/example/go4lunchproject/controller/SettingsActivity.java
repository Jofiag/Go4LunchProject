package com.example.go4lunchproject.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.go4lunchproject.R;
import com.example.go4lunchproject.data.firebase.FirebaseCloudDatabase;
import com.example.go4lunchproject.model.UserSettings;
import com.example.go4lunchproject.util.Constants;

public class SettingsActivity extends AppCompatActivity {
    private Spinner sortingSpinner;
    private SwitchCompat switchButton;

    private final FirebaseCloudDatabase firebaseCloudDatabase = FirebaseCloudDatabase.getInstance();
    private final String userId = firebaseCloudDatabase.getUserId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setReferences();

        setSwitchButtonState();
        manageNotifications();

        setSortingSpinner();
        responseToUserSpinnerSelection();
    }

    private void setReferences() {
        switchButton = findViewById(R.id.notification_switch_button);
        sortingSpinner = findViewById(R.id.sorting_spinner);
    }

    private void setSwitchButtonState(){
        firebaseCloudDatabase.getUser(singleUser -> {
            if (singleUser != null){
                UserSettings userSettings = singleUser.getUserSettings();
                if (userSettings != null)
                    switchButton.setChecked(userSettings.isNotificationOn());
            }
        });

    }
    private void manageNotifications(){
        Bundle bundle = new Bundle();

        firebaseCloudDatabase.listenToUser(singleUser -> switchButton.setOnClickListener(view -> {
            if (singleUser != null) {
                UserSettings userSettings = singleUser.getUserSettings();
                if (userSettings == null)
                    userSettings = new UserSettings(true, Constants.SORT_BY_NAME);

                userSettings.setNotificationOn(switchButton.isChecked());
                firebaseCloudDatabase.updateUserSettings(userSettings);
            }

        }));
    }

    private void setSortingSpinner(){
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.sorting_list_options_array, android.R.layout.simple_spinner_item);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sortingSpinner.setAdapter(arrayAdapter);

        //Set the spinner selection on the last user option selected.
        firebaseCloudDatabase.getUser(singleUser -> {
            if (singleUser != null){
                UserSettings userSettings = singleUser.getUserSettings();
                if (userSettings != null){
                    String optionSelected = userSettings.getSortListOption();
                    if (optionSelected == null)
                        optionSelected = Constants.SORT_BY_NAME;

                    switch (optionSelected){
                        case Constants.SORT_BY_NAME:
                            sortingSpinner.setSelection(0);
                            break;
                        case Constants.SORT_BY_RATING:
                            sortingSpinner.setSelection(1);
                            break;
                        case Constants.SORT_BY_PROXIMITY:
                            sortingSpinner.setSelection(2);
                            break;
                        case Constants.SORT_BY_WORKMATES_INTERESTED:
                            sortingSpinner.setSelection(3);
                            break;
                    }
                }
            }
        });
    }
    private void responseToUserSpinnerSelection(){
        sortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String sortOptionSelected = (String) adapterView.getItemAtPosition(position);
                saveSortOptionSelectedInFirebase(sortOptionSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //set the first option (Name) by default
                saveSortOptionSelectedInFirebase((String) adapterView.getItemAtPosition(0));
            }
        });
    }
    private void saveSortOptionSelectedInFirebase(String option){
        firebaseCloudDatabase.getUser(singleUser -> {
            if (singleUser != null){
                UserSettings userSettings = singleUser.getUserSettings();
                if (userSettings == null)
                    userSettings = new UserSettings(true, Constants.SORT_BY_NAME);

                userSettings.setSortListOption(option);

                firebaseCloudDatabase.updateUserSettings(userSettings);
            }
        });
    }
}