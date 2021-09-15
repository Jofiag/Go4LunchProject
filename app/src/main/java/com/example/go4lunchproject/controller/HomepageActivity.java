package com.example.go4lunchproject.controller;

import static com.example.go4lunchproject.util.Constants.FINE_LOCATION;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunchproject.MainActivity;
import com.example.go4lunchproject.R;
import com.example.go4lunchproject.adapter.WorkmateRecyclerViewAdapter;
import com.example.go4lunchproject.data.api.LocationApi;
import com.example.go4lunchproject.data.api.RestaurantSelectedApi;
import com.example.go4lunchproject.data.api.UserApi;
import com.example.go4lunchproject.data.firebase.FirebaseCloudDatabase;
import com.example.go4lunchproject.data.viewmodel.FragmentViewModel;
import com.example.go4lunchproject.model.MyPositionObject;
import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.model.Workmate;
import com.example.go4lunchproject.notification.AlarmReceiver;
import com.example.go4lunchproject.util.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HomepageActivity extends AppCompatActivity
        implements WorkmateRecyclerViewAdapter.OnWorkmateClickListener {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location deviceLocation;

    private Toolbar myToolbar;
    private DrawerLayout myDrawerLayout;
    private NavigationView myNavigationView;
    private BottomNavigationView bottomNavigationView;

    private final FragmentManager fragmentManager = getSupportFragmentManager();

    private Fragment fragmentToShow;
    private Fragment activeFragment;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FragmentViewModel fragmentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homepage);
        setReferences();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        fragmentViewModel = new ViewModelProvider(this).get(FragmentViewModel.class);

        setLocationManagerAndListener();
        requestLocationIfPermissionIsGranted();

        setBottomNavigationView();
        setMyToolbarAsAppBar();
        setMyDrawerLayout();
        setMyNavigationView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getRestaurantChosenByUser();
    }

    private void getRestaurantChosenByUser() {
        Restaurant restaurantChosen = UserApi.getInstance().getUser().getRestaurantChosen();
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), result -> {
                if (result)
                    requestLocationIfPermissionIsGranted();
                else
                    requestPermissionWithinDialog();
            }
    );

    private void setLocationManagerAndListener(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                deviceLocation = location;
                MyPositionObject positionObject = new MyPositionObject(location.getLatitude(), location.getLongitude());
                LocationApi.getInstance(HomepageActivity.this).setPosition(positionObject);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    private void requestLocationIfPermissionIsGranted() {
        if (checkSelfPermission(FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 100, locationListener);
            deviceLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (deviceLocation != null) {
                MyPositionObject positionObject = new MyPositionObject(deviceLocation.getLatitude(), deviceLocation.getLongitude());
                LocationApi.getInstance(this).setPosition(positionObject);
                addFragments();
                alarmNotification();
            }
            else
                Toast.makeText(this, "Location unavailable", Toast.LENGTH_SHORT).show();

        }
        else{
            if (shouldShowRequestPermissionRationale(FINE_LOCATION))
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();

            requestPermissionLauncher.launch(FINE_LOCATION);
        }

    }
    private void requestPermissionWithinDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Location permission disable")
                .setMessage("You denied the location permission. It is required to show your location. Do you want to grant the permission")
                .setPositiveButton("YES", (dialog, which) -> requestLocationIfPermissionIsGranted())
                .setNegativeButton("NO", null)
                .create()
                .show();
    }
    private void setReferences() {
        myToolbar = findViewById(R.id.my_toolbar);
        myDrawerLayout = findViewById(R.id.my_drawer);
        myNavigationView = findViewById(R.id.my_navigation_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);


    }
    private void addFragments(){
        activeFragment = fragmentViewModel.getRestaurantMapViewFragment();

        if (!fragmentViewModel.getRestaurantMapViewFragment().isAdded()) {
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container_homepage_activity, fragmentViewModel.getWorkmateListViewFragment(), Constants.WORKMATE_LIST_FRAGMENT)
                    .hide(fragmentViewModel.getWorkmateListViewFragment())
                    .commit();
        }

        if (!fragmentViewModel.getRestaurantListViewFragment().isAdded()) {
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container_homepage_activity, fragmentViewModel.getRestaurantListViewFragment(), Constants.RESTAURANT_LIST_FRAGMENT)
                    .hide(fragmentViewModel.getRestaurantListViewFragment())
                    .commit();
        }

        if (!fragmentViewModel.getWorkmateListViewFragment().isAdded()) {
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container_homepage_activity, activeFragment, Constants.RESTAURANT_MAP_VIEW_FRAGMENT)
                    .commit();
        }
    }
    private void showFragment(){
        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(fragmentToShow)
                .commit();

        activeFragment = fragmentToShow;
    }
    private void setBottomNavigationView(){
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.restaurant_map_view_item){
                myToolbar.setTitle(Constants.IM_HUNGRY_TITLE_TEXT);
                fragmentToShow = fragmentViewModel.getRestaurantMapViewFragment();
            }
            else if (id == R.id.restaurant_list_view_item){
                myToolbar.setTitle(Constants.IM_HUNGRY_TITLE_TEXT);
                fragmentToShow = fragmentViewModel.getRestaurantListViewFragment();
            }
            else if (id == R.id.workmate_list_view_item){
                myToolbar.setTitle(Constants.AVAILABLE_WORKMATES_TITLE_TEXT);
                fragmentToShow = fragmentViewModel.getWorkmateListViewFragment();
            }

            showFragment();

            return true;
        });
    }
    private void setMyToolbarAsAppBar(){
        setSupportActionBar(myToolbar);
    }
    private void setMyDrawerLayout(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(HomepageActivity.this,
                myDrawerLayout,
                myToolbar,
                R.string.open_navigation_drawer_description_text, R.string.close_navigation_drawer_description_text);

        myDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }
    private void setMyNavigationView(){
        showUserProfileInNavHeader();

        myNavigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.your_lunch_item)
                startRestaurantDetailsActivity();

            else if (id == R.id.settings_item)
                startActivity(new Intent(this, SettingsActivity.class));

            else if (id == R.id.logout_item)
                logoutUserAndBackToMainActivity();

            return true;
        });
    }
    private void logoutUserAndBackToMainActivity(){
        new AlertDialog.Builder(HomepageActivity.this)
                .setTitle("SIGNING OUT")
                .setMessage("Are you sure you want to sign out ?")
                .setPositiveButton("YES", (dialog, which) -> {
                    mAuth.signOut();
                    startActivity(new Intent(HomepageActivity.this, MainActivity.class));
                    finish();
                })
                .setNegativeButton("NO", null)
                .create()
                .show();

    }
    private void showUserProfileInNavHeader(){
        View navHeaderView = myNavigationView.getHeaderView(0);
        TextView userNameTextView = navHeaderView.findViewById(R.id.name_text_nav_header);
        TextView userEmailTextView = navHeaderView.findViewById(R.id.email_text_nav_header);
        CircleImageView profileCircleImageView = navHeaderView.findViewById(R.id.profile_circle_image_view_nav_header);

        if (currentUser != null){
            Picasso.get().load(currentUser.getPhotoUrl())
                    .placeholder(android.R.drawable.stat_sys_download)
                    .error(android.R.drawable.stat_notify_error)
//                    .resize(154, 154)
                    .into(profileCircleImageView);

            userNameTextView.setText(currentUser.getDisplayName());
            userEmailTextView.setText(currentUser.getEmail());
        }
    }
    private void startRestaurantDetailsActivity(){
        FirebaseCloudDatabase.getInstance().getCurrentUser(singleUser -> {
            if (singleUser != null){
                Restaurant restaurant = singleUser.getRestaurantChosen();
                if (restaurant != null){
                    RestaurantSelectedApi.getInstance().setRestaurantSelected(restaurant);
                    startActivity(new Intent(HomepageActivity.this, RestaurantDetailsActivity.class));
                }
                else
                    Toast.makeText(this, "You don't chose any restaurant yet!", Toast.LENGTH_SHORT).show();
            }
        });

        Log.d("SELECTED", "startRestaurantDetailsActivity: " + RestaurantSelectedApi.getInstance().getRestaurantSelected());

    }

    private void alarmNotification(){
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 31);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

    }

    @Override
    public void onBackPressed() {
        //Make sure we close the DrawerLayout when the user click on back button
        if (myDrawerLayout.isEnabled())
            myDrawerLayout.closeDrawers();

        //If the actual fragment is the mapview one, close the app
        if (activeFragment == fragmentViewModel.getRestaurantMapViewFragment())
            finish();
        else{
            //Go to mapview fragment if it's not the actual fragment
            fragmentToShow = fragmentViewModel.getRestaurantMapViewFragment();
            showFragment();
        }
    }

    private void startRestaurantDetailsActivity(Parcelable parcelable){
        Intent intent = new Intent(HomepageActivity.this, RestaurantDetailsActivity.class);
        intent.putExtra(Constants.WORKMATE_SELECTED_CODE, parcelable);
        startActivity(intent);
    }

    @Override
    public void onWorkmateSelected(Workmate workmate) {
        if (workmate.getRestaurantChosen() != null)
            startRestaurantDetailsActivity(workmate);
        else
            Toast.makeText(this, Constants.NO_RESTAURANT_TO_SHOW_TEXT, Toast.LENGTH_SHORT).show();
    }
}