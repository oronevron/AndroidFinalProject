package com.example.oron.androidfinalproject;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

        View view = inflater.inflate(R.layout.fragment_trip_details, container, false);

//        int index = this.getArguments().getInt("tripIndex");
//        Trip trip = Model.getInstance().getTripByIndex(index);




        String index = this.getArguments().getString("tripIndex");
        Trip trip = Model.getInstance().getTripById(index);




        TextView nameTv = (TextView) view.findViewById(R.id.trip_details_name);
        nameTv.setText("name: " + trip.getName());
        TextView idTv = (TextView) view.findViewById(R.id.trip_details_id);
        idTv.setText("id: " + trip.getId());
        TextView typeTv = (TextView) view.findViewById(R.id.trip_details_type);
        typeTv.setText("type: " + trip.getType());
        TextView difficultyTv = (TextView) view.findViewById(R.id.trip_details_difficulty);
        difficultyTv.setText("difficulty: " + trip.getDifficulty());

        if (trip.getImageName() != null) {
            final ImageView image = (ImageView) view.findViewById(R.id.trip_details_imageview);
            final ProgressBar progress = (ProgressBar) view.findViewById(R.id.trip_details_image_progress_bar);
            progress.setVisibility(View.VISIBLE);
            Model.getInstance().loadImage(trip.getImageName(), new Model.GetImageListener() {
                @Override
                public void onSuccess(Bitmap imagebtmp) {
//                if (imagebtmp != null && ((Integer)cb.getTag() == position)) {
                    if (imagebtmp != null) {
                        image.setImageBitmap(imagebtmp);
                        progress.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFail() {
                    progress.setVisibility(View.GONE);
                }
            });
        }


//        CheckBox checkedCb = (CheckBox) view.findViewById(R.id.trip_details_checked);
//        checkedCb.setChecked(trip.getChecked());
//        TextView birthDateTv = (TextView) view.findViewById(R.id.trip_details_birth_date);
//        birthDateTv.setText("birth date: " + trip.getDayOfMonth() + "/" + trip.getMonthOfYear() + "/" + trip.getYear());
//        TextView birthTimeTv = (TextView) view.findViewById(R.id.trip_details_birth_time);
//        birthTimeTv.setText("birth time: " + trip.getHourOfDay() + ":" + trip.getMinute());

        // Inflate the layout for this fragment
        return view;
    }

}
