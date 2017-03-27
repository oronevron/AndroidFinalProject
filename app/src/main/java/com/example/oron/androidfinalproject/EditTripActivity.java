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

        // Get trip details and show them
        Intent intent = getIntent();
        int index = (int) intent.getExtras().get("tripIndex");
        Bundle bundle = new Bundle();
        bundle.putInt("tripIndex", index);

        EditTripFragment editTripFragment = new EditTripFragment();
        editTripFragment.setArguments(bundle);
        FragmentTransaction ftr = getFragmentManager().beginTransaction();
        ftr.add(R.id.editTripContainer, editTripFragment);
        ftr.commit();
    }
}
