package com.example.musicplayer;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>  {

    public static ArrayList<File> localdataset;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textView);
            // Define click listener for the ViewHolder's View
            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Intent intent = new Intent(view.getContext(), PlaySong.class);
                intent.putExtra("songList", localdataset);
                intent.putExtra("songPosition", position);
                view.getContext().startActivity(intent);
            });

        }

        public TextView getTextView() {
            return textView;
        }

    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public CustomAdapter(ArrayList<File> dataSet) {
        localdataset = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.text_row_item, viewGroup, false);
        return new ViewHolder(view);
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.getTextView().setText(localdataset.get(position).getName().replace(".mp3", ""));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localdataset.size();
    }
}

