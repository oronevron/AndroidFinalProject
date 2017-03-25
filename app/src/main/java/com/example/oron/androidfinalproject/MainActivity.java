package com.example.oron.androidfinalproject;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.oron.androidfinalproject.Model.Model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    TripsListFragment tripsListFragment = new TripsListFragment();

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

        // This piece of code will be used in the future to receive user details!
        // Do not delete it for now!!
//        FirebaseUser userCool = auth.getCurrentUser();
//
//        String displayName = userCool.getDisplayName();
//        String userId = userCool.getUid();
//        String email = userCool.getEmail();

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
                tripsListFragment.getAdapter().notifyDataSetChanged();
                tripsListFragment.getList().setSelection(Model.getInstance().getAllTrips().size());
            }
        }

//        // Trip has been deleted
//        else if (requestCode == 2) {
//            if (resultCode == Activity.RESULT_FIRST_USER) {
//                tripsListFragment.getAdapter().notifyDataSetChanged();
//                tripsListFragment.getList().setSelection(0);
//            }
//
//            // Trip has been edited
//            else if (resultCode == Activity.RESULT_OK) {
//                tripsListFragment.getAdapter().notifyDataSetChanged();
//                tripsListFragment.getList().setSelection((int)data.getExtras().get("tripIndex"));
//            }
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle menu action according to item id
        switch (item.getItemId()) {

            // Handle click on new trip button
            case R.id.new_trip_button:

                Intent intent = new Intent(getApplicationContext(), NewTripActivity.class);
                startActivityForResult(intent, 1);

                return true;

            // If user chose to sign out
            case R.id.sign_out_button:

                // Sign out using firebase api
                auth.signOut();

                return true;

//            default:
//                return super.onOptionsItemSelected(item);
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

}
