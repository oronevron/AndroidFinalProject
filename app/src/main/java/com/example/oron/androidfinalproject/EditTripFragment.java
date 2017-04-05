package com.example.oron.androidfinalproject;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.oron.androidfinalproject.Model.Model;
import com.example.oron.androidfinalproject.Model.Trip;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditTripFragment extends Fragment {

    ImageView imageView = null;
    Bitmap imageBitmap = null;
    private Trip previousTripDetails;
    int difficulty = 0;
    boolean imageChanged = false;

    public EditTripFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_edit_trip, container, false);

        // Sets difficulty slider
        setDifficultySlider(view);

        // Set touch handler for the image
        imageView = (ImageView) view.findViewById(R.id.edit_trip_image_view);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takingPicture();
            }
        });

        final String index = this.getArguments().getString("tripIndex");
        final Trip trip = Model.getInstance().getTripById(index);

        // Populate values in the types spinner
        setTypesDropDown(view,trip);

        EditText nameEt = (EditText) view.findViewById(R.id.edit_trip_name);
        nameEt.setText(trip.getName());
        SeekBar difficultySb = (SeekBar) view.findViewById(R.id.edit_trip_difficulty);
        difficultySb.setProgress(trip.getDifficulty());
        difficulty = trip.getDifficulty();
        TextView difficultyTv = (TextView) view.findViewById(R.id.edit_trip_difficulty_value);
        EditText descEt = (EditText) view.findViewById(R.id.edit_trip_description);
        descEt.setText(trip.getDescription());

        switch (trip.getDifficulty()) {
            case 0: difficultyTv.setText(getResources().getString(R.string.very_easy));
                    break;
            case 1: difficultyTv.setText(getResources().getString(R.string.easy));
                    break;
            case 2: difficultyTv.setText(getResources().getString(R.string.medium));
                    break;
            case 3: difficultyTv.setText(getResources().getString(R.string.hard));
                    break;
            case 4: difficultyTv.setText(getResources().getString(R.string.very_hard));
                    break;
        }

        if (trip.getImageName() != null) {

            final ProgressBar progress = (ProgressBar) view.findViewById(R.id.edit_trip_image_progress_bar);
            progress.setVisibility(View.VISIBLE);
            Model.getInstance().loadImage(trip.getImageName(), new Model.GetImageListener() {
                @Override
                public void onSuccess(Bitmap imagebtmp) {
                    if (imagebtmp != null) {
                        imageView.setImageBitmap(imagebtmp);
                        progress.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFail() {
                    progress.setVisibility(View.GONE);
                }
            });
        }

        // Save the initial trip details in temporary variable
        previousTripDetails = new Trip(trip.getName(), trip.getType(), trip.getDescription(), trip.getDifficulty());//, checkedCb.isChecked(), trip.getYear(), trip.getMonthOfYear(), trip.getDayOfMonth(), trip.getHourOfDay(), trip.getMinute());

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
                String type;
                String description;

                EditText nameEt = (EditText) view.findViewById(R.id.edit_trip_name);
                name = nameEt.getText().toString();
                Spinner spinner = (Spinner) view.findViewById(R.id.types_spinner);
                type = spinner.getSelectedItem().toString();
                EditText descEt = (EditText) view.findViewById(R.id.edit_trip_description);
                description = descEt.getText().toString();

                // Check that there is no empty field
                if (name != null && !name.isEmpty()) {

                    // Check if there is at least one change in trip details
                    if (!name.equals(previousTripDetails.getName()) ||
                            !type.equals(previousTripDetails.getType()) ||
                            difficulty != previousTripDetails.getDifficulty() ||
                            !description.equals(previousTripDetails.getDescription()) ||
                            imageChanged == true)
                            {// || !isChecked.equals(previousTripDetails.getChecked()) ||
//                    year != previousTripDetails.getYear() || monthOfYear != previousTripDetails.getMonthOfYear() || dayOfMonth != previousTripDetails.getDayOfMonth() || hourOfDay != previousTripDetails.getHourOfDay() || minute != previousTripDetails.getMinute())  {

                        Trip tripToEdit = new Trip(name,type,description,difficulty);
                        tripToEdit.setId(trip.getId());
                        tripToEdit.setImageName(trip.getImageName());
                        tripToEdit.setUser_id(trip.getUser_id());

                        Model.getInstance().editTrip(tripToEdit, imageBitmap, new Model.EditTripListener() {
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

    private void setTypesDropDown( final View view, final Trip trip) {

        // Populate values in the types spinner
        Spinner spinner = (Spinner) view.findViewById(R.id.types_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.types_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // Set initial value in the spinner
        spinner.setSelection(adapter.getPosition(trip.getType()));
    }

    private void setDifficultySlider( final View view) {

        // Handle seek bar change event
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.edit_trip_difficulty);

        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {


                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {

                        // Get the trip difficulty text view
                        TextView textView = (TextView) view.findViewById(R.id.edit_trip_difficulty_value);

                        switch (progresValue) {
                            case 0: textView.setText(getResources().getString(R.string.very_easy));
                                difficulty = 0;
                                break;
                            case 1: textView.setText(getResources().getString(R.string.easy));
                                difficulty = 1;
                                break;
                            case 2: textView.setText(getResources().getString(R.string.medium));
                                difficulty = 2;
                                break;
                            case 3: textView.setText(getResources().getString(R.string.hard));
                                difficulty = 3;
                                break;
                            case 4: textView.setText(getResources().getString(R.string.very_hard));
                                difficulty = 4;
                                break;
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void takingPicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            imageChanged = true;
        }
    }
}
