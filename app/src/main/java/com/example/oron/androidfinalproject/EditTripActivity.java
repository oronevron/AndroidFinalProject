package com.example.oron.androidfinalproject;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

public class EditTripActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        // Get trip details
        Intent intent = getIntent();
        String index = intent.getExtras().get("tripIndex").toString();

        // Prepare the parameter for the fragment
        Bundle bundle = new Bundle();
        bundle.putString("tripIndex", index);

        // Create edit trip fragment and call it with the parameter
        EditTripFragment editTripFragment = new EditTripFragment();
        editTripFragment.setArguments(bundle);
        FragmentTransaction ftr = getFragmentManager().beginTransaction();
        ftr.add(R.id.editTripContainer, editTripFragment);
        ftr.commit();
    }
}
