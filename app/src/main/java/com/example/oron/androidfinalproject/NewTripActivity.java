package com.example.oron.androidfinalproject;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class NewTripActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        NewTripFragment newTripFragment = new NewTripFragment();
        FragmentTransaction ftr = getFragmentManager().beginTransaction();
        ftr.add(R.id.newTripContainer, newTripFragment);
        ftr.commit();
    }
}
