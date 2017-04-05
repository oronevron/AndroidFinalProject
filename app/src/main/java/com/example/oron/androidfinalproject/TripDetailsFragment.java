package com.example.oron.androidfinalproject;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oron.androidfinalproject.Model.Model;
import com.example.oron.androidfinalproject.Model.Trip;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripDetailsFragment extends Fragment {


    public TripDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the view from the XML
        View view = inflater.inflate(R.layout.fragment_trip_details, container, false);

        // Get the id of the required trip
        String index = this.getArguments().getString("tripIndex");

        // Get all the details of the required trip
        Trip trip = Model.getInstance().getTripById(index);

        // Set the trip details in the relevant fields in the view
        TextView nameTv = (TextView) view.findViewById(R.id.trip_details_name);
        nameTv.setText("Name: " + trip.getName());
        TextView typeTv = (TextView) view.findViewById(R.id.trip_details_type);
        typeTv.setText("Type: " + trip.getType());
        TextView difficultyTv = (TextView) view.findViewById(R.id.trip_details_difficulty);
        int difficulty = trip.getDifficulty();
        String diffDesc = null;

        // Set text in the difficulty field according to the value
        switch (difficulty) {
            case 0: diffDesc = getResources().getString(R.string.very_easy);
                    break;
            case 1: diffDesc = getResources().getString(R.string.easy);
                    break;
            case 2: diffDesc = getResources().getString(R.string.medium);
                    break;
            case 3: diffDesc = getResources().getString(R.string.hard);
                    break;
            case 4: diffDesc = getResources().getString(R.string.very_hard);
                    break;
        }
        difficultyTv.setText("Difficulty: " + diffDesc);
        TextView descriptionTv = (TextView) view.findViewById(R.id.trip_details_description);
        descriptionTv.setText("Description: " + trip.getDescription());

        // If there is an image for the trip
        if (trip.getImageName() != null) {

            // Get the image view of the view
            final ImageView image = (ImageView) view.findViewById(R.id.trip_details_imageview);

            // Show progress bar
            final ProgressBar progress = (ProgressBar) view.findViewById(R.id.trip_details_image_progress_bar);
            progress.setVisibility(View.VISIBLE);

            // Load the trip's image
            Model.getInstance().loadImage(trip.getImageName(), new Model.GetImageListener() {
                @Override
                public void onSuccess(Bitmap imagebtmp) {
                    if (imagebtmp != null) {

                        // Set the loaded image in the view
                        image.setImageBitmap(imagebtmp);

                        // Hide progress bar
                        progress.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFail() {
                    // Hide progress bar
                    progress.setVisibility(View.GONE);

                    // Show relevant message
                    Toast.makeText(getActivity(), getString(R.string.image_loading_failed), Toast.LENGTH_LONG);
                }
            });
        }

        // Inflate the layout for this fragment
        return view;
    }
}
