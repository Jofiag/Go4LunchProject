package com.example.go4lunchproject.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunchproject.R;
import com.example.go4lunchproject.model.Restaurant;
import com.example.go4lunchproject.model.Workmate;
import com.example.go4lunchproject.util.Constants;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class WorkmateRecyclerViewAdapter extends RecyclerView.Adapter<WorkmateRecyclerViewAdapter.MyViewHolder>
    implements Filterable {

    public interface OnWorkmateClickListener {
        void onWorkmateSelected(Workmate workmate);
    }

    private final List<Workmate> workmateList;
    private OnWorkmateClickListener mCallback;

    public WorkmateRecyclerViewAdapter(Context context, List<Workmate> workmateList) {
        this.workmateList = workmateList;
        this.mCallback = (OnWorkmateClickListener) context;
    }

    public WorkmateRecyclerViewAdapter(List<Workmate> workmateList) {
        this.workmateList = workmateList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workmate_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Workmate workmate = workmateList.get(position);

        Restaurant restaurant = workmate.getRestaurantChosen();
        if (restaurant != null){
            holder.foodCountryTextView.setText(restaurant.getFoodCountry());
            holder.restaurantNameTextView.setText(MessageFormat.format("({0})", restaurant.getName()));
            holder.workmateNameTextView.setText(workmate.getName());
        }
        else {
            holder.isEatingTextView.setVisibility(View.GONE);
            holder.foodCountryTextView.setVisibility(View.GONE);
            holder.restaurantNameTextView.setVisibility(View.GONE);
            holder.workmateNameTextView.setVisibility(View.GONE);
            holder.hasNotDecidedTextView.setText(String.format("%s%s", workmate.getName(), Constants.HAS_NOT_DECIDED_YET));
        }

        if (workmate.getImageUri() != null) {
            Uri uri = Uri.parse(workmate.getImageUri());
            holder.circleImageView.setImageURI(uri);
        }


        holder.itemView.setOnClickListener(v -> mCallback.onWorkmateSelected(workmate));
    }

    @Override
    public int getItemCount() {
        if (workmateList != null)
            return workmateList.size();
        else
            return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView foodCountryTextView;
        private TextView workmateNameTextView;
        private TextView restaurantNameTextView;
        private TextView isEatingTextView;
        private TextView hasNotDecidedTextView;
        private CircleImageView circleImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            setReferences(itemView);
        }

        private void setReferences(View itemView) {
            circleImageView = itemView.findViewById(R.id.workmate_image_view);
            workmateNameTextView = itemView.findViewById(R.id.workmate_name_text_view);
            foodCountryTextView = itemView.findViewById(R.id.food_country_text_view);
            restaurantNameTextView = itemView.findViewById(R.id.restaurant_name_workmate_row);
            isEatingTextView = itemView.findViewById(R.id.is_eating);
            hasNotDecidedTextView = itemView.findViewById(R.id.has_not_decided_text_view);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Workmate> workmateListFiltered = new ArrayList<>();
                List<Workmate> workmateListToFilter = Constants.getWorkmateList();

                if (constraint == null || constraint.length() == 0)
                    workmateListFiltered.addAll(workmateListToFilter);
                else{
                    String searchText = constraint.toString().toLowerCase().trim();

                    for (Workmate workmate : workmateListToFilter) {
                        Restaurant restaurantChosen = workmate.getRestaurantChosen();
                        if (restaurantChosen != null){
                            if (restaurantChosen.getName().toLowerCase().contains(searchText))
                                workmateListFiltered.add(workmate);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = workmateListFiltered;

                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                workmateList.clear();
                workmateList.addAll((List<Workmate>) results.values);

                notifyDataSetChanged();
            }
        };
    }
}
