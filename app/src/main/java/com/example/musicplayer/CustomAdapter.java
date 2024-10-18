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
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            textView = (TextView) view.findViewById(R.id.textView);


        }

        public TextView getTextView() {
            return textView;
        }

        @Override
        public void onClick(View view) {
            int position = this.getAdapterPosition();
            String name = localdataset.get(position).getName().replace(".mp3", "");
            Intent intent = new Intent(view.getContext(), PlaySong.class);
            intent.putExtra("com.example.musicplayer.CustomAdapter.SongName",name);
            intent.putExtra("com.example.musicplayer.CustomAdapter.SongList", localdataset);
            intent.putExtra("com.example.musicplayer.CustomAdapter.SongPosition", position);
            view.getContext().startActivity(intent);
            //Toast.makeText(view.getContext(), "Position is " + Integer.toString(position), Toast.LENGTH_SHORT).show();
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
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {


        viewHolder.getTextView().setText(localdataset.get(position).getName());
    }


    @Override
    public int getItemCount() {
        return localdataset.size();
    }
}

