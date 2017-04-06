package com.example.oron.androidfinalproject;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.oron.androidfinalproject.Model.Model;
import com.example.oron.androidfinalproject.Model.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    TripsListFragment tripsListFragment = new TripsListFragment();
    private static MenuItem refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add the trip details fragment
        FragmentTransaction ftr = getFragmentManager().beginTransaction();
        ftr.add(R.id.mainFirstContainer, tripsListFragment);
        ftr.commit();

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        // Set authorize listener to handle change in the user state
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                // Get the current user
                FirebaseUser user = firebaseAuth.getCurrentUser();

                // If user chose to log out
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // New trip has been created
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                // Refresh the trips list
                refreshTripsList();
            }
        }

        // Trip has been deleted
        else if (requestCode == 2) {
            if (resultCode == Activity.RESULT_FIRST_USER) {
                // Refresh the trips list
                refreshTripsList();
            }

            // Trip has been edited
            else if (resultCode == Activity.RESULT_OK) {
                // Refresh the trips list
                refreshTripsList();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu from the XML
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);

        // Save reference to the refresh button for further processing
        refreshButton = menu.findItem(R.id.refresh_button);

        if (!CheckNetwork.isInternetAvailable(MyApplication.getAppContext())) {
            refreshButton.setVisible(false);
            menu.findItem(R.id.new_trip_button).setVisible(false);
        }

        // Set the icon of the refresh button to "no updates"
        changeRefreshButtonIcon(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle menu action according to item id
        switch (item.getItemId()) {

            // Handle click on new trip button
            case R.id.new_trip_button:

                // Start the new trip activity
                Intent intent = new Intent(getApplicationContext(), NewTripActivity.class);
                startActivityForResult(intent, 1);

                return true;

            // Handle click on refresh button
            case R.id.refresh_button:

                // Refresh the trips list
                refreshTripsList();

                // Show relevant message
                Toast.makeText(this, R.string.refresh_button_message, Toast.LENGTH_LONG).show();

                // Set the icon of the refresh button to "no updates"
                changeRefreshButtonIcon(false);

                return true;

            // If user chose to sign out
            case R.id.sign_out_button:

                // Sign out using firebase api
                auth.signOut();

                return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    public static void changeRefreshButtonIcon(boolean isNoUpToDate) {
        // Change the icon of the refresh button (black - "updates" / white - "no updates") according to the parameter
        if (isNoUpToDate) {
            refreshButton.setIcon(R.drawable.ic_refresh_black_24dp);
        } else {
            refreshButton.setIcon(R.drawable.ic_refresh_white_24dp);
        }
    }

    private void refreshTripsList() {
        // Refresh the trips list
        tripsListFragment.setTripsList(Model.getInstance().refreshTripsList());
        tripsListFragment.getAdapter().notifyDataSetChanged();

        // Set the currently selected item to the first item
        tripsListFragment.getList().setSelection(0);
    }
}
