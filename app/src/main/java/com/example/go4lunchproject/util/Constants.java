package com.example.go4lunchproject.util;

import android.Manifest;

import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.model.Workmate;
import com.example.go4lunchproject.model.Workmate;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static final String H_TEXT = "h";
    public static final String OPEN_UNTIL_TEXT = "open until ";
    public static final String IM_HUNGRY_TITLE_TEXT = "I'm hungry!";
    public static final String CLOSE_AND_OPEN_AT_TEXT = "Closed. Open at ";
    public static final String WORKMATE_SELECTED_CODE = "workmate selected";
    public static final String RESTAURANT_SELECTED_CODE = "restaurant selected";
    public static final String AVAILABLE_WORKMATES_TITLE_TEXT = "Available workmates";
    public static final String HAS_NOT_DECIDED_YET = " hasn't decided yet";
    public static final String NO_RESTAURANT_TO_SHOW_TEXT = "No restaurant to show!";
    public static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final CharSequence SEARCH_RESTAURANTS_TEXT = "Search restaurants";
    public static final CharSequence SEARCH_WORKMATES_TEXT = "Search workmates";
    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String NAME = "name";
    public static final String ADDRESS = "address";
    public static final String RESTAURANT_CLICKED_POSITION = "position";
    public static final String RESTAURANT = "restaurant";
    public static final String NEARBY_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    public static final String PLACE_PHOTO_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    public static final String PLACE_DETAILS_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
    public static final int PROXIMITY_RADIUS = 10000;

    //JSON FILE
    public static final String PLACE_NAME = "place_name";
    public static final String VICINITY = "vicinity";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String REFERENCE = "reference";
    public static final String GEOMETRY = "geometry";
    public static final String LOCATION = "location";
    public static final String RESULTS = "results";
    public static final String RESTAURANT_ON_MARKER_CODE = "restaurant on marker";
    public static final String URL_KEY = "url";
    public static final String FOOD = "food";
    public static final String FAST_FOOD = "fast food";
    public static final String TYPE = "types";
    public static final String LODGING = "lodging";
    public static final String ESTABLISHMENT = "establishment";
    public static final String DISTANCE = "distance";
    public static final String PROMINENCE = "prominence";
    public static final String PHOTOS = "photos";
    public static final String RATING = "rating";
    public static final String PHOTO_REFERENCE = "photo_reference";
    public static final String PLACE_ID = "place_id";
    public static final int PHOTO_MAX_HEIGHT = 1600;
    public static final int PHOTO_MAX_WIDTH = 1600;
    public static final String OPENING_HOURS = "opening_hours";
    public static final String RESULT = "result";
    public static final String CLOSED = "Closed";
    public static final String CLOSING_SOON = "Closing soon";
    public static final String CLOSED_TODAY = "Closed today";
    public static final String DEVICE_POSITION = "My position";
    public static final String FRAGMENT_KEY = "Fragment";
    public static final String RESTAURANT_LIST_FRAGMENT = "Restaurant listview fragment";
    public static final String WORKMATE_LIST_FRAGMENT = "Workmate listview fragment";
    public static final String RESTAURANT_MAP_VIEW_FRAGMENT = "Restaurant map view fragment";
    public static final int JOB_ID = 111;
    public static final String INFO_FROM_OTHER_THREAD = "DATA_FROM_BACKGROUND";
    public static final String INFO = "INFO";
    public static final String LIST = "Restaurant list";
    public static final String SEND_LIST_ACTION = "Send list action";
    public static final String USER_LIST_FIREBASE_PATH = "User_list";
    public static final String USER_DATA_REF = "Users_data_ref";
    public static final String SAVE_RESTAURANT_ACTION = "Save restaurant";
    public static final String REMOVE_RESTAURANT_ACTION = "Remove restaurant";
    public static final String RESTAURANT_CHOSEN_REFERENCE = "Restaurant_chosen_ref";
    public static final String REMOVE_ACTUAL_WORKMATE = "Remove actual workmate";
    public static final String ADD_ACTUAL_WORKMATE = "Add actual workmate";
    public static final String RESTAURANT_NOTIFICATION_TITLE = "Lunch reminder";
    public static final String CHANNEL_ID = "channel for restaurant chosen notification";
    public static final String CHANNEL_NAME = "Restaurant chosen notification";
    public static final String CHANNEL_DESCRIPTION = "Show in a notification the name of the restaurant chosen by the user, it's address and workmate that chose the same restaurant";
    public static final int RESTAURANT_NOTIFICATION_ID = 7;

    public static List<Restaurant> getRestaurantList(){
        List<Restaurant> restaurantList = new ArrayList<>();
        restaurantList.add(createCRestaurant("Safari", "French", "1 Rue Faubourg"));
        restaurantList.add(createCRestaurant("Flunch", "Italian", "67 Rue Vincent"));
        restaurantList.add(createCRestaurant("La mama", "French", "13 Ter Richard Mille "));
        restaurantList.add(createCRestaurant("O'tacos", "French", "51 Avenue de la Liberation"));
        restaurantList.add(createCRestaurant("Oc pizza", "French", "83 Rue Strasbourg"));
        restaurantList.add(createCRestaurant("O'tantik", "French", "21 Boulevard François Mitterand"));
        restaurantList.add(createCRestaurant("McDonald", "American", "43 Rue Jean Jacques Jores"));
        restaurantList.add(createCRestaurant("Panorama", "French", "31 Rue des filletes"));
        restaurantList.add(createCRestaurant("Maman africa", "French", "71 Avenue des Paulines"));
        restaurantList.add(createCRestaurant("Original tacos", "French", "97 Boulevard Resgistre"));

        return restaurantList;
    }

    private static Restaurant createCRestaurant(String name, String foodCountry, String address){
        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setFoodCountry(foodCountry);
        restaurant.setAddress(address);

        return restaurant;
    }

    public static List<Workmate> getWorkmateList(){
        List<Restaurant> restaurantList = getRestaurantList();

        List<Workmate> workmateList = new ArrayList<>();
        workmateList.add(createWorkmate("Angela", restaurantList.get(0)));
        workmateList.add(createWorkmate("Bilikiss", restaurantList.get(1)));
        workmateList.add(createWorkmate("Carole", restaurantList.get(2)));
        workmateList.add(createWorkmate("Dorianne", restaurantList.get(3)));
        workmateList.add(createWorkmate("Elizabet", restaurantList.get(4)));
        workmateList.add(createWorkmate("Florian", restaurantList.get(5)));
        workmateList.add(createWorkmate("Gisele", restaurantList.get(6)));
        workmateList.add(createWorkmate("Hodette", null));
        workmateList.add(createWorkmate("Imen", null));
        workmateList.add(createWorkmate("Jocelyn", null));

        return workmateList;
    }

    private static Workmate createWorkmate(String name, Restaurant restaurant){
        Workmate workmate = new Workmate();
        workmate.setName(name);
        workmate.setRestaurantChosen(restaurant);
        return workmate;
    }
}
