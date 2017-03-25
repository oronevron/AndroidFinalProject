package com.example.oron.androidfinalproject;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class TripDetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        // Get trip details and show them
        Intent intent = getIntent();
        int index = (int) intent.getExtras().get("tripIndex");
        Bundle bundle = new Bundle();
        bundle.putInt("tripIndex", index);

        TripDetailsFragment tripDetailsFragment = new TripDetailsFragment();
        tripDetailsFragment.setArguments(bundle);
        FragmentTransaction ftr = getFragmentManager().beginTransaction();
        ftr.add(R.id.detailsContainer, tripDetailsFragment);
        ftr.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            // Trip has been deleted
            if (resultCode == Activity.RESULT_FIRST_USER) {
                setResult(Activity.RESULT_FIRST_USER);
                finish();
            }

            // Trip has been edited
            else if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.activity_trip_details_menu, menu);
//
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.edit_trip_button:
//
//                // Handle click on edit button
//                Intent intent = getIntent();
//                int index = (int) intent.getExtras().get("tripIndex");
//                Intent intentNew = new Intent(getApplicationContext(), EditTripActivity.class);
//                intentNew.putExtra("tripIndex", index);
//                startActivityForResult(intentNew, 1);
//
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}
