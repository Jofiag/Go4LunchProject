package com.example.go4lunchproject.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunchproject.R;
import com.example.go4lunchproject.adapter.WorkmateRecyclerViewAdapter;
import com.example.go4lunchproject.data.firebase.FirebaseCloudDatabase;
import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.model.User;
import com.example.go4lunchproject.model.Workmate;
import com.example.go4lunchproject.util.Constants;
import com.example.go4lunchproject.util.UtilMethods;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class WorkmateListViewFragment extends Fragment {
    private interface AccessToAllWorkmate{
        void onResponse(List<Workmate> workmateList);
    }

    private RecyclerView recyclerView;

    private WorkmateRecyclerViewAdapter workmateAdapter;
    private final FirebaseCloudDatabase firebaseCloudDatabase = FirebaseCloudDatabase.getInstance();
//    private final List<Workmate> workmateList = Constants.getWorkmateList();

    public WorkmateListViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmate_list_view, container, false);

        recyclerView = view.findViewById(R.id.workmate_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        accessToAllWorkmate(workmateList -> {
            workmateAdapter = new WorkmateRecyclerViewAdapter(getActivity(), workmateList);
            recyclerView.setAdapter(workmateAdapter);
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_view_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_item);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint(Constants.SEARCH_WORKMATES_TEXT);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Action after user validate his search text
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Real time action
//                workmateAdapter.getFilter().filter(newText);
                filterList(newText);
                return false;
            }
        });

    }

    private void accessToAllWorkmate(AccessToAllWorkmate callback){
        firebaseCloudDatabase.listenToAllUsers(userList -> {
            if (userList != null && !userList.isEmpty()){
                List<Workmate> workmateList = new ArrayList<>();

                for (User user : userList)
                    workmateList.add(UtilMethods.setWorkmateCorresponding(user));

                if (callback != null)
                    callback.onResponse(workmateList);
            }
            else
                if (callback != null)
                    callback.onResponse(null);
        });
    }

    private void filterList(String query){
        List<Workmate> listFiltered = new ArrayList<>();

        firebaseCloudDatabase.listenToAllUsers(userList -> {
            if (userList != null && !userList.isEmpty()){
                List<Workmate> workmateList = new ArrayList<>();

                for (User user : userList)
                    workmateList.add(UtilMethods.setWorkmateCorresponding(user));

                if (!workmateList.isEmpty()){
                    for (Workmate workmate : workmateList) {
                        Restaurant restaurantChosen = workmate.getRestaurantChosen();
                        if (restaurantChosen != null){
                            String restaurantChosenName = restaurantChosen.getName();
                            if (restaurantChosenName.toLowerCase().contains(query.toLowerCase()))
                                listFiltered.add(workmate);
                        }
                    }

                    workmateAdapter = new WorkmateRecyclerViewAdapter(getContext(), listFiltered);
                    recyclerView.setAdapter(workmateAdapter);
                }

            }
        });

//        accessToAllWorkmate(workmateList -> {
//            if (workmateList != null){
//                for (Workmate workmate : workmateList) {
//                    Restaurant restaurantChosen = workmate.getRestaurantChosen();
//                    if (restaurantChosen != null){
//                        String restaurantChosenName = restaurantChosen.getName();
//                        if (restaurantChosenName.toLowerCase().contains(query.toLowerCase()))
//                            listFiltered.add(workmate);
//                    }
//                }
//
//                workmateAdapter = new WorkmateRecyclerViewAdapter(getContext(), listFiltered);
//                recyclerView.setAdapter(workmateAdapter);
//            }
//        });


    }
}