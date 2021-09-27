package com.example.go4lunchproject777.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunchproject777.R;
import com.example.go4lunchproject777.adapter.RestaurantRecyclerViewAdapter;
import com.example.go4lunchproject777.data.api.RestaurantListUrlApi;
import com.example.go4lunchproject777.data.firebase.FirebaseCloudDatabase;
import com.example.go4lunchproject777.data.googleplace.RestaurantNearbyBank2;
import com.example.go4lunchproject777.model.Restaurant;
import com.example.go4lunchproject777.model.UserSettings;
import com.example.go4lunchproject777.util.Constants;
import com.example.go4lunchproject777.util.UtilMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@RequiresApi(api = Build.VERSION_CODES.O)
public class RestaurantListViewFragment extends Fragment{
    private String url;
    private Activity activity;
    private RecyclerView recyclerView;
    private RestaurantRecyclerViewAdapter restaurantAdapter;
    private RestaurantNearbyBank2 restaurantNearbyBank;
    private final FirebaseCloudDatabase firebaseCloudDatabase = FirebaseCloudDatabase.getInstance();

    public RestaurantListViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        activity = getActivity();
        url = RestaurantListUrlApi.getInstance(getActivity()).getUrlThroughDeviceLocation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_restaurant_list_view, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.restaurant_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        url = RestaurantListUrlApi.getInstance(getActivity()).getUrlThroughDeviceLocation();

        restaurantNearbyBank  = new RestaurantNearbyBank2(getContext());
        restaurantNearbyBank.getRestaurantList(url, restaurantList -> {
            if (restaurantList.isEmpty())
                Toast.makeText(activity, "No restaurant found !!!", Toast.LENGTH_SHORT).show();

            sortListDependingOnUserOptionSelected(restaurantList);

            return restaurantList;

        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_view_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_item);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint(Constants.SEARCH_RESTAURANTS_TEXT);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Action after user validate his search text
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Real time action
                /*if (restaurantAdapter != null)
                    restaurantAdapter.getFilter().filter(newText);*/

                filterList(newText);

                return false;
            }
        });

    }


    @SuppressLint("NotifyDataSetChanged")
    private void filterList(String query){

        restaurantNearbyBank = new RestaurantNearbyBank2(getContext());
        restaurantNearbyBank.getRestaurantList(url, restaurantList -> {
            ArrayList<Restaurant> listFiltered = new ArrayList<>();

            for (Restaurant restaurant : restaurantList) {
                if (restaurant.getName().toLowerCase().contains(query.toLowerCase()))
                    listFiltered.add(restaurant);
            }

            sortListDependingOnUserOptionSelected(listFiltered);

            return restaurantList;

        });

        /*RestaurantNearbyBank.getInstance(activity, null).getRestaurantNearbyList(url, restaurantList -> {
            List<Restaurant> listFiltered = new ArrayList<>();

            for (Restaurant restaurant : restaurantList)
                if (restaurant.getName().toLowerCase().contains(query.toLowerCase()))
                    listFiltered.add(restaurant);

            restaurantAdapter = new RestaurantRecyclerViewAdapter(activity, listFiltered);
            recyclerView.setAdapter(restaurantAdapter);
            restaurantAdapter.notifyDataSetChanged();

        });*/
    }

    private void sortListDependingOnUserOptionSelected(ArrayList<Restaurant> restaurantList){
        firebaseCloudDatabase.listenToUser(singleUser -> {
            if (singleUser != null){
                UserSettings userSettings = singleUser.getUserSettings();

                if (userSettings != null){
                    String optionSelected = userSettings.getSortListOption();
                    if (optionSelected == null){
                        optionSelected = Constants.SORT_BY_NAME;
                    }

                    switch (optionSelected){
                        case Constants.SORT_BY_NAME:
                            Collections.sort(restaurantList, Comparator.comparing(Restaurant::getName));
                            break;
                        case Constants.SORT_BY_RATING:
                            Collections.sort(restaurantList, Comparator.comparingInt(Restaurant::getFavorableOpinion));
                            break;
                        case Constants.SORT_BY_PROXIMITY:
                            Collections.sort(restaurantList, Comparator.comparingInt(Restaurant::getProximity));
                            break;
                        case Constants.SORT_BY_WORKMATES_INTERESTED:
                            Collections.sort(restaurantList, Comparator.comparingInt(Restaurant::getNumberOfInterestedWorkmate));
                            break;
                    }
                }



                restaurantAdapter = new RestaurantRecyclerViewAdapter(activity, UtilMethods.removeRedundantRestaurant(restaurantList));
                recyclerView.swapAdapter(restaurantAdapter, false);

            }
        });
    }

    /*private void initializePlaces(){
        if (!Places.isInitialized())
            Places.initialize(Objects.requireNonNull(getContext()), getString(R.string.google_maps_key));

        placesClient = Places.createClient(Objects.requireNonNull(getContext()));
    }
    private void initializePredictionRequestAndPlaceFields(){
        sessionToken = AutocompleteSessionToken.newInstance();
        bounds = RectangularBounds.newInstance(LatLngBounds.builder().include(new LatLng(45.7757747, 3.0804423)).build());

        predictionRequest = FindAutocompletePredictionsRequest.builder()
                .setCountry("fr")
                .setLocationBias(bounds)
                .setTypeFilter(TypeFilter.GEOCODE)
                .setSessionToken(sessionToken)
                .build();

        placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG,
                Place.Field.PHOTO_METADATAS, Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.TYPES);
    }*/
}