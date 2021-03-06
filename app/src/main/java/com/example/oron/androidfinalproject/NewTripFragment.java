package com.example.oron.androidfinalproject;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oron.androidfinalproject.Model.Model;
import com.example.oron.androidfinalproject.Model.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewTripFragment extends Fragment {

    ImageView imageView = null;
    String imageFileName = null;
    Bitmap imageBitmap = null;
    private ProgressBar progressBar;
    int difficulty = 0;

    public NewTripFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the view from the XML
        final View view = inflater.inflate(R.layout.fragment_new_trip, container, false);

        // Get the image view of the view
        imageView = (ImageView) view.findViewById(R.id.new_trip_imageview);

        // Get the progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.new_progressBar);

        // Populate values in the types spinner
        setTypesDropDown(view);

        // Sets difficulty slider
        setDifficultySlider(view);

        // Handle click on cancel button
        final Button cancelBtn = (Button) view.findViewById(R.id.new_trip_cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show relevant message
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

        // Handle click on save button
        final Button saveBtn = (Button) view.findViewById(R.id.new_trip_save_button);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name;
                String id;
                String type;
                String description;

                // Get the values of the view's fields
                EditText nameEt = (EditText) view.findViewById(R.id.new_trip_name);
                name = nameEt.getText().toString();
                Spinner spinner = (Spinner) view.findViewById(R.id.types_spinner);
                type = spinner.getSelectedItem().toString();
                EditText descEt = (EditText) view.findViewById(R.id.new_trip_description);
                description = descEt.getText().toString();

                // Check that there is no empty field
                if ((name != null && !name.trim().isEmpty()) &&
                    (description != null && !description.trim().isEmpty())) {

                    // Create new trip and set the relevant values
                    final Trip trip = new Trip(name, type, description, difficulty);

                    // Get the current user
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    // Set the user as the trip's creator
                    // This will be used later to allow only this user to edit or delete the trip
                    trip.setUser_id(user.getUid());

                    progressBar.setVisibility(View.VISIBLE);
                    saveBtn.setVisibility(View.GONE);
                    cancelBtn.setVisibility(View.GONE);

                    // Add the trip in databases
                    Model.getInstance().addTrip(trip, imageBitmap, new Model.AddTripListener() {
                        @Override
                        public void onResult() {

                            progressBar.setVisibility(View.GONE);

                            // Show relevant message
                            DialogFragment dialog = new MessagesAlertDialog();
                            Bundle args = new Bundle();
                            args.putInt("resultCode", Activity.RESULT_OK);
                            args.putInt("messageCode", R.string.new_trip_success_message);
                            args.putBoolean("isMessageOnly", false);
                            args.putBoolean("isTripIndexSent", false);
                            dialog.setArguments(args);
                            dialog.show(getFragmentManager(), "TAG");
                        }

                        @Override
                        public void onCancel() {

                            // Show relevant message
                            Toast.makeText(getActivity(), getString(R.string.add_trip_failed), Toast.LENGTH_LONG);
                        }
                    });
                }
                // If there is a empty field
                else {
                    // Show relevant message
                    DialogFragment dialog = new MessagesAlertDialog();
                    Bundle args = new Bundle();
                    args.putInt("messageCode", R.string.new_or_edit_trip_error_message);
                    args.putBoolean("isMessageOnly", true);
                    dialog.setArguments(args);
                    dialog.show(getFragmentManager(), "TAG");
                }
            }
        });

        // Set click handler for the image view
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Take picture from the user
                takingPicture();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void takingPicture(){
        // Start the camera in order to take picture from the user
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Handle the processing of the image from the user
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void setTypesDropDown( final View view) {

        // Populate values in the types spinner
        Spinner spinner = (Spinner) view.findViewById(R.id.types_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.types_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private void setDifficultySlider( final View view) {

        // Handle seek bar change event
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.new_trip_difficulty);
        seekBar.setOnSeekBarChangeListener(
                new OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {

                        // Get the trip difficulty text view
                        TextView textView = (TextView) view.findViewById(R.id.new_trip_difficulty_value);

                        // Set text in the difficulty field according to the value
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
}
