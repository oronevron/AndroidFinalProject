package com.example.oron.androidfinalproject.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.oron.androidfinalproject.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

public class ModelFirebase {
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public void getAllTripsAsynch(double lastUpdateDate, final Model.GetTripsListener listener) {

        // Get reference for the trips node
        DatabaseReference myRef = database.getReference("trips");

        // Get all the trips whose last update time is equal or greater than the local last update time
        Query query = myRef.orderByChild("lastUpdated").startAt(lastUpdateDate);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Create trips list and trips to delete list
                final List<Trip> tripList = new LinkedList<Trip>();
                final List<Trip> tripsToDelete = new LinkedList<Trip>();

                // Loop over all fetched trips
                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {

                    // Create a trip object for the current snapshot
                    Trip trip = tripSnapshot.getValue(Trip.class);

                    // Set the id of the current trip
                    trip.setId(tripSnapshot.getKey());

                    // If the trip is no deleted - add it to the trips list
                    // Otherwise - add it to the trips to delete list
                    if (!trip.getIsDeleted()) {
                        tripList.add(trip);
                    } else {
                        tripsToDelete.add(trip);
                    }
                }

                // Call listener's onResult method with both lists
                listener.onResult(tripList, tripsToDelete);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Call listener's onCancel method
                listener.onCancel();
            }
        });
    }

    public String getNewKey() {

        // Push a new child node to the trips node and return it's id
        return database.getReference("trips").push().getKey();
    }

    public void addTrip(Trip trip, final Model.AddTripListener listener) {

        // Get reference for the trips node
        final DatabaseReference myRef = database.getReference("trips");

        // Add the trip
        myRef.child(trip.getId()).setValue(trip.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                // If there isn't an error
                if (databaseError == null) {

                    // Call listener's onResult method
                    listener.onResult();
                }
                // If there is an error
                else {

                    // Call listener's onCancel method
                    listener.onCancel();
                }
            }
        });
    }

    public void deleteTrip(final String id, final Model.DeleteTripListener listener) {

        // Get reference for the trips node
        final DatabaseReference myRef = database.getReference("trips").child(id);

        // Mark the trip as deleted
        myRef.child("isDeleted").setValue(true, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                // If there isn't an error
                if (databaseError == null) {

                    // Update the trip's last update time
                    myRef.child("lastUpdated").setValue(ServerValue.TIMESTAMP, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            // If there isn't an error
                            if (databaseError == null) {

                                // Call listener's onResult method with the id
                                listener.onResult(id);
                            }
                            // If there is an error
                            else {

                                // Call listener's onCancel method
                                listener.onCancel();
                            }
                        }
                    });
                }
                // If there is an error
                else {

                    // Call listener's onCancel method
                    listener.onCancel();
                }
            }
        });
    }

    public void editTrip(Trip trip, final Model.EditTripListener listener) {

        // Get reference for the requierd trip's node
        DatabaseReference myRef = database.getReference("trips").child(trip.getId());

        // Update the trip
        myRef.setValue(trip.toMap(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    // If there isn't an error
                    if (databaseError == null) {

                        // Call listener's onResult method
                        listener.onResult();
                    }
                    // If there is an error
                    else {

                        // Call listener's onCancel method
                        listener.onCancel();
                    }
                }
            });
    }

    public void saveImage(Bitmap imageBitmap, String name, final Model.SaveImageListener listener){

        // Get reference to the images node of Firebase storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imagesRef = storage.getReference().child("images").child(name);

        // Process the bitmap in order to get byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload the image
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {

                // Call listener's fail method
                listener.fail();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // Get the url of the uploaded image
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                // Call listener's complete method with the url
                listener.complete(downloadUrl.toString());
            }
        });
    }

    public void getImage(String url, final Model.GetImageListener listener){

        // Get reference to Firebase storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(url);

        // Get the image
        final long ONE_MEGABYTE = 1024 * 1024;
        httpsReference.getBytes(3* ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                // Decode the fetched byte array to bitmap
                Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                // Call listener's onSuccess method with the image bitmap
                listener.onSuccess(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {

                // Call listener's onFail method
                listener.onFail();
            }
        });
    }

    public void handleDatabaseChanges() {

        // Register for any change within the trips node
        database.getReference("trips").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                // Create a trip object for the current snapshot
                Trip trip = dataSnapshot.getValue(Trip.class);

                // Set the id of the current trip
                trip.setId(dataSnapshot.getKey());

                // If the trip isn't deleted
                if (!trip.getIsDeleted()) {

                    // If the trip not exists in the local database
                    if (Model.getInstance().getTripById(trip.getId()) == null) {

                        // Get the current user and check if it is different than the user that created the trip
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (!user.getUid().equals(trip.getUser_id())) {

                            // Set the icon of the refresh button to "updates"
                            MainActivity.changeRefreshButtonIcon(true);
                        }
                    }

                    // Add the trip to the local database
                    TripSql.addTrip(Model.getInstance().modelSql.getWritableDB(), trip);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {


                // Get the current user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // Create a trip object for the current snapshot
                Trip trip = dataSnapshot.getValue(Trip.class);

                // Set the id of the current trip
                trip.setId(dataSnapshot.getKey());

                // If the trip isn't deleted
                if (!trip.getIsDeleted()) {

                    // Update the trip in the local database
                    TripSql.editTrip(Model.getInstance().modelSql.getWritableDB(), trip);
                }
                // If the trip is deleted and the current user is different than the user that created the trip
                else if (!user.getUid().equals(trip.getUser_id())) {

                    // Remove trip's image from the device
                    Model.getInstance().removeImageFromDevice(trip.getImageName());

                    // Delete the trip from the local database
                    TripSql.deleteTrip(Model.getInstance().modelSql.getWritableDB(), trip.getId());
                }

                // If the current user is different than the user that created the trip
                if (!user.getUid().equals(trip.getUser_id())) {

                    // Set the icon of the refresh button to "updates"
                    MainActivity.changeRefreshButtonIcon(true);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // For future features
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // For future features
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // For future features
            }
        });
    }
}