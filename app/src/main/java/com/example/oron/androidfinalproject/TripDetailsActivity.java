package com.example.oron.androidfinalproject;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.oron.androidfinalproject.Model.Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TripDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        // Get trip details and show them
        Intent intent = getIntent();
//        int index = (int) intent.getExtras().get("tripIndex");
        String index = intent.getExtras().get("tripIndex").toString();
        Bundle bundle = new Bundle();
//        bundle.putInt("tripIndex", index);
        bundle.putString("tripIndex", index);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_trip_details_menu, menu);

        // Hide the "edit" menu item depending on wether user has authority to edit or not
        MenuItem editItem = menu.findItem(R.id.edit_trip_button);

        // Get the current trip's user creator
        Intent intent = getIntent();
        String index = intent.getExtras().get("tripIndex").toString();
        String tripCreatorId = Model.getInstance().getTripById(index).getUser_id();

        // Get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        // If the user isn't the trip's creator, hide the edit button
        if(!userId.equals(tripCreatorId))
        {
            editItem.setVisible(false);
        }
        else
        {
            editItem.setVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_trip_button:

                // Handle click on edit button
                Intent intent = getIntent();
//                int index = (int) intent.getExtras().get("tripIndex");
                String index = intent.getExtras().get("tripIndex").toString();
                Intent intentNew = new Intent(getApplicationContext(), EditTripActivity.class);
                intentNew.putExtra("tripIndex", index);
                startActivityForResult(intentNew, 1);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
