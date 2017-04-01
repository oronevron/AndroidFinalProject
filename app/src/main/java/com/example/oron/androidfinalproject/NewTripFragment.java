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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.oron.androidfinalproject.Model.Model;
import com.example.oron.androidfinalproject.Model.Trip;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewTripFragment extends Fragment {

    ImageView imageView = null;
    String imageFileName = null;
    Bitmap imageBitmap = null;


    public NewTripFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_new_trip, container, false);
        imageView = (ImageView) view.findViewById(R.id.new_trip_imageview);

        // Handle click on cancel button
        Button cancelBtn = (Button) view.findViewById(R.id.new_trip_cancel_button);
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

        // Handle click on save button
        Button saveBtn = (Button) view.findViewById(R.id.new_trip_save_button);
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

                EditText nameEt = (EditText) view.findViewById(R.id.new_trip_name);
                name = nameEt.getText().toString();
                EditText idEt = (EditText) view.findViewById(R.id.new_trip_id);
                id = idEt.getText().toString();
                EditText typeEt = (EditText) view.findViewById(R.id.new_trip_type);
                type = typeEt.getText().toString();
                EditText difficultyEt = (EditText) view.findViewById(R.id.new_trip_difficulty);
                try {
                    difficulty = Integer.parseInt(difficultyEt.getText().toString());
                } catch (NumberFormatException e) {
                    difficulty = 0;
                }
//                CheckBox checkedCb = (CheckBox) view.findViewById(R.id.new_trip_checked);
//                isChecked = checkedCb.isChecked();
//                InputDateTextView birthDateIdtv = (InputDateTextView) view.findViewById(R.id.new_trip_birth_date);
//                year = birthDateIdtv.getYear();
//                monthOfYear = birthDateIdtv.getMonthOfYear();
//                dayOfMonth = birthDateIdtv.getDayOfMonth();
//                InputTimeTextView birthTimeIdtv = (InputTimeTextView) view.findViewById(R.id.new_trip_birth_time);
//                hourOfDay = birthTimeIdtv.getHourOfDay();
//                minute = birthTimeIdtv.getMinute();

                // Check that there is no empty field
                if (name != null && !name.isEmpty() && id != null && !id.isEmpty() && type != null && !type.isEmpty() && difficulty != 0) {
//                    Model.getInstance().addTrip(new Trip(name, id, phone, address, isChecked, year, monthOfYear, dayOfMonth, hourOfDay, minute));

                    final Trip trip = new Trip(name, id, type, 0, difficulty, null);
                    if(imageBitmap != null){
                        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        String imName = "image_" + id + "_" + timeStamp + ".jpg";
                        Model.getInstance().saveImage(imageBitmap, imName, new Model.SaveImageListener() {
                            @Override
                            public void complete(String url) {
                                trip.setImageName(url);
                                saveTripAndClose(trip);
                            }

                            @Override
                            public void fail() {
                                saveTripAndClose(null);
                            }
                        });
                    }else{
                        saveTripAndClose(trip);
                    }

                    Model.getInstance().addTrip(new Trip(name, id, type, 0, difficulty, null));

//                    Model.getInstance().add(new Trip(name, id, type, 0, 0, difficulty));

                    DialogFragment dialog = new MessagesAlertDialog();
                    Bundle args = new Bundle();
                    args.putInt("resultCode", Activity.RESULT_OK);
                    args.putInt("messageCode", R.string.new_trip_success_message);
                    args.putBoolean("isMessageOnly", false);
                    args.putBoolean("isTripIndexSent", false);
                    dialog.setArguments(args);
                    dialog.show(getFragmentManager(), "TAG");
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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takingPicture();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void saveTripAndClose(Trip trip){
        if (trip != null){
            Model.getInstance().add(trip);
        }

        Intent resultIntent = new Intent();
        getActivity().setResult(Activity.RESULT_OK, resultIntent);
        getActivity().finish();
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
        }
    }
}
