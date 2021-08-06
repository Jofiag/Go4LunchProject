package com.example.go4lunchproject.controller;

import static com.example.go4lunchproject.util.Constants.FINE_LOCATION;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunchproject.R;
import com.example.go4lunchproject.adapter.WorkmateRecyclerViewAdapter;
import com.example.go4lunchproject.data.FragmentViewModel;
import com.example.go4lunchproject.data.LocationApi;
import com.example.go4lunchproject.data.RestaurantSelectedApi;
import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.model.User;
import com.example.go4lunchproject.model.Workmate;
import com.example.go4lunchproject.util.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

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

    private Restaurant restaurantChosen;

    private FragmentViewModel fragmentViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homepage);
        setReferences();

        fragmentViewModel = new ViewModelProvider(this).get(FragmentViewModel.class);

        setLocationManagerAndListener();
        requestLocationIfPermissionIsGranted();

        User user = new User(); //TODO : get user connected from firebase

        restaurantChosen = user.getRestaurantChosen();

        setBottomNavigationView();

        setMyToolbarAsAppBar();

        setMyDrawerLayout();

        setMyNavigationView();
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
                LocationApi.getInstance(HomepageActivity.this).setLocation(location);
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
            LocationApi.getInstance(this).setLocation(deviceLocation);
            if (deviceLocation != null)
                addFragments();
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
        myNavigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.your_lunch_item) {
                //Attach fragment corresponding

                if (restaurantChosen != null){
                    RestaurantSelectedApi.getInstance().setRestaurantSelected(restaurantChosen);
                    myDrawerLayout.closeDrawers(); // OR myDrawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(new Intent(HomepageActivity.this, RestaurantDetailsActivity.class));
                }
                else
                    Toast.makeText(this, "You don't chose any restaurant yet!", Toast.LENGTH_SHORT).show();

                return true;
            }
            else if (id == R.id.settings_item) {
                //Attach fragment corresponding
                myDrawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            else if (id == R.id.logout_item) {
                //Attach fragment corresponding
                //TODO : LOGOUT USER FROM FIREBASE AND GO BACK TO MAIN ACTIVITY
                myDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }


            return true;
        });
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