package com.example.oron.androidfinalproject;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.oron.androidfinalproject.Model.Model;
import com.example.oron.androidfinalproject.Model.Trip;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditTripFragment extends Fragment {

    private Trip previousTripDetails;


    public EditTripFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_edit_trip, container, false);

//        final int index = this.getArguments().getInt("tripIndex");
//        Trip trip = Model.getInstance().getTripByIndex(index);

        final String index = this.getArguments().getString("tripIndex");
        final Trip trip = Model.getInstance().getTripById(index);

        EditText nameEt = (EditText) view.findViewById(R.id.edit_trip_name);
        nameEt.setText(trip.getName());
        EditText idEt = (EditText) view.findViewById(R.id.edit_trip_id);
        idEt.setText(trip.getId());
        EditText typeEt = (EditText) view.findViewById(R.id.edit_trip_type);
        typeEt.setText(trip.getType());
        EditText difficultyEt = (EditText) view.findViewById(R.id.edit_trip_difficulty);
        difficultyEt.setText(Integer.toString(trip.getDifficulty()));

        if (trip.getImageName() != null) {
            final ImageView image = (ImageView) view.findViewById(R.id.edit_trip_image_view);
            final ProgressBar progress = (ProgressBar) view.findViewById(R.id.edit_trip_image_progress_bar);
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


//        CheckBox checkedCb = (CheckBox) view.findViewById(R.id.edit_trip_checked);
//        checkedCb.setChecked(trip.getChecked());
//        InputDateTextView birthDateIdtv = (InputDateTextView) view.findViewById(R.id.edit_trip_birth_date);
//        birthDateIdtv.setText(trip.getDayOfMonth() + "/" + trip.getMonthOfYear() + "/" + trip.getYear());
//        InputTimeTextView birthTimeIdtv = (InputTimeTextView) view.findViewById(R.id.edit_trip_birth_time);
//        birthTimeIdtv.setText(trip.getHourOfDay() + ":" + trip.getMinute());


        // Save the initial trip details in temporary variable
        previousTripDetails = new Trip(nameEt.getText().toString(), idEt.getText().toString(),typeEt.getText().toString(), 0, Integer.parseInt(difficultyEt.getText().toString()), null);//, checkedCb.isChecked(), trip.getYear(), trip.getMonthOfYear(), trip.getDayOfMonth(), trip.getHourOfDay(), trip.getMinute());

        // Handle click on cancel button
        Button cancelBtn = (Button) view.findViewById(R.id.edit_trip_cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new MessagesAlertDialog();
                Bundle args = new Bundle();
                args.putInt("resultCode", Activity.RESULT_CANCELED);
                args.putInt("messageCode", R.string.action_canceled_message);
                args.putBoolean("isMessageOnly", false);
                args.putBoolean("isTripIndexSent", false);
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), "TAG");
            }
        });

        // Handle click on delete button
        Button deleteBtn = (Button) view.findViewById(R.id.edit_trip_delete_button);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
//                Model.getInstance().deleteTrip(index);


                Model.getInstance().deleteTrip(index, new Model.DeleteTripListener() {
                    @Override
                    public void onResult(String id) {
                        DialogFragment dialog = new MessagesAlertDialog();
                        Bundle args = new Bundle();
                        args.putInt("resultCode", Activity.RESULT_FIRST_USER);
                        args.putInt("messageCode", R.string.edit_trip_delete_message);
                        args.putBoolean("isMessageOnly", false);
                        args.putBoolean("isTripIndexSent", false);
                        dialog.setArguments(args);
                        dialog.show(getFragmentManager(), "TAG");
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });

        // Handle click on save button
        Button saveBtn = (Button) view.findViewById(R.id.edit_trip_save_button);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name;
                String id;
                String type;
                int difficulty;
                Boolean isChecked;
                int year;
                int monthOfYear;
                int dayOfMonth;
                int hourOfDay;
                int minute;

                EditText nameEt = (EditText) view.findViewById(R.id.edit_trip_name);
                name = nameEt.getText().toString();
                EditText idEt = (EditText) view.findViewById(R.id.edit_trip_id);
                id = idEt.getText().toString();
                EditText typeEt = (EditText) view.findViewById(R.id.edit_trip_type);
                type = typeEt.getText().toString();
                EditText difficultyEt = (EditText) view.findViewById(R.id.edit_trip_difficulty);
                difficulty = Integer.parseInt(difficultyEt.getText().toString());
//                try {
//                    difficulty = Integer.parseInt(difficultyEt.getText().toString());
//                } catch (NumberFormatException e) {
//                    difficulty = 0;
//                }
//                CheckBox checkedCb = (CheckBox) view.findViewById(R.id.edit_trip_checked);
//                isChecked = checkedCb.isChecked();
//                InputDateTextView birthDateIdtv = (InputDateTextView) view.findViewById(R.id.edit_trip_birth_date);
//                year = birthDateIdtv.getYear();
//                monthOfYear = birthDateIdtv.getMonthOfYear();
//                dayOfMonth = birthDateIdtv.getDayOfMonth();
//                InputTimeTextView birthTimeIdtv = (InputTimeTextView) view.findViewById(R.id.edit_trip_birth_time);
//                hourOfDay = birthTimeIdtv.getHourOfDay();
//                minute = birthTimeIdtv.getMinute();

                // Check that there is no empty field
                if (name != null && !name.isEmpty() && id != null && !id.isEmpty() && type != null && !type.isEmpty()) {

                    // Check if there is at least one change in trip details
                    if (!name.equals(previousTripDetails.getName()) || !id.equals(previousTripDetails.getId()) || !type.equals(previousTripDetails.getType()) || difficulty != previousTripDetails.getDifficulty()) {// || !isChecked.equals(previousTripDetails.getChecked()) ||
//                    year != previousTripDetails.getYear() || monthOfYear != previousTripDetails.getMonthOfYear() || dayOfMonth != previousTripDetails.getDayOfMonth() || hourOfDay != previousTripDetails.getHourOfDay() || minute != previousTripDetails.getMinute())  {

                        //TODO
//                        Model.getInstance().editTrip(new Trip(name, id, type, 0, difficulty, null), index);//, isChecked, year, monthOfYear, dayOfMonth, hourOfDay, minute), index);


                        Model.getInstance().editTrip(new Trip(name, id, type, 0, difficulty, null), new Model.EditTripListener() {
                            @Override
                            public void onResult() {
                                Intent intent = new Intent();
                                intent.putExtra("tripIndex", index);

                                DialogFragment dialog = new MessagesAlertDialog();
                                Bundle args = new Bundle();
                                args.putInt("resultCode", Activity.RESULT_OK);
                                args.putInt("messageCode", R.string.edit_trip_success_message);
                                args.putBoolean("isMessageOnly", false);
                                args.putBoolean("isTripIndexSent", true);
//                        args.putInt("tripIndex", index);
                                args.putString("tripIndex", index);
                                dialog.setArguments(args);
                                dialog.show(getFragmentManager(), "TAG");
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }
                    else {
                        DialogFragment dialog = new MessagesAlertDialog();
                        Bundle args = new Bundle();
                        args.putInt("messageCode", R.string.edit_trip_error_message);
                        args.putBoolean("isMessageOnly", true);
                        dialog.setArguments(args);
                        dialog.show(getFragmentManager(), "TAG");
                    }
                }
                else {
                    DialogFragment dialog = new MessagesAlertDialog();
                    Bundle args = new Bundle();
                    args.putInt("messageCode", R.string.new_or_edit_trip_error_message);
                    args.putBoolean("isMessageOnly", true);
                    dialog.setArguments(args);
                    dialog.show(getFragmentManager(), "TAG");
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
