package com.example.go4lunchproject777.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunchproject777.R;
import com.example.go4lunchproject777.model.Workmate;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class WorkmateAdapterForRestaurantDetails extends RecyclerView.Adapter<WorkmateAdapterForRestaurantDetails.MyViewHolder>{

    private final List<Workmate> workmateList;

    public WorkmateAdapterForRestaurantDetails(List<Workmate> workmateList) {
        this.workmateList = workmateList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workmate_restaurant_details_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Workmate workmate = workmateList.get(position);

        holder.workmateNameTextView.setText(workmate.getName());

        if (workmate.getImageUri() != null) {
            Uri uri = Uri.parse(workmate.getImageUri());
            Picasso.get().load(uri)
                    .placeholder(android.R.drawable.stat_sys_download)
                    .error(android.R.drawable.stat_notify_error)
//                    .resize(154, 154)
                    .into(holder.circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        if (workmateList != null)
            return workmateList.size();
        else
            return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView workmateNameTextView;
        private CircleImageView circleImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            setReferences(itemView);
        }

        private void setReferences(View itemView) {
            circleImageView = itemView.findViewById(R.id.workmate_image_for_restaurant_details);
            workmateNameTextView = itemView.findViewById(R.id.workmate_name_text_for_restaurant_details);
        }
    }
}
