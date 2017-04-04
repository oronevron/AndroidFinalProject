package com.example.oron.androidfinalproject.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
        DatabaseReference myRef = database.getReference("trips");
        Query query = myRef.orderByChild("lastUpdated").startAt(lastUpdateDate);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Trip> tripList = new LinkedList<Trip>();
                final List<Trip> tripsToDelete = new LinkedList<Trip>();
                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    Trip trip = tripSnapshot.getValue(Trip.class);

                    trip.setId(tripSnapshot.getKey());

                    Log.d("TAG", trip.getName() + " - " + trip.getId());

                    if (!trip.getIsDeleted()) {
                        tripList.add(trip);
                    } else {
                        tripsToDelete.add(trip);
                    }
                }
                listener.onResult(tripList, tripsToDelete);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    public void getTripById(String id, final Model.GetTrip listener) {
//        DatabaseReference myRef = database.getReference("trips").child(id);
//        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                Trip trip = snapshot.getValue(Trip.class);
//                Log.d("TAG", trip.getName() + " - " + trip.getId());
//                listener.onResult(trip);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    public String getNewKey() {
        return database.getReference("trips").push().getKey();
    }

    public void addTrip(Trip trip, Bitmap imageBitmap, final Model.AddTripListener listener) {
        final DatabaseReference myRef = database.getReference("trips");

        myRef.child(trip.getId()).setValue(trip.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    listener.onResult();
                } else {
                    listener.onCancel();
                }
            }
        });
    }

    public void deleteTrip(final String id, final Model.DeleteTripListener listener) {
        final DatabaseReference myRef = database.getReference("trips").child(id);
//        myRef.removeValue(new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                if (databaseError == null) {
//                    listener.onResult(id);
//                } else {
//                    listener.onCancel();
//                }
//            }
//        });

        myRef.child("isDeleted").setValue(true, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    myRef.child("lastUpdated").setValue(ServerValue.TIMESTAMP, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                listener.onResult(id);
                            } else {
                                listener.onCancel();
                            }
                        }
                    });
                } else {
                    listener.onCancel();
                }
            }
        });
    }

    public void editTrip(Trip trip, final Model.EditTripListener listener) {
        DatabaseReference myRef = database.getReference("trips").child(trip.getId());
        myRef.setValue(trip.toMap(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        listener.onResult();
                    } else {
                        listener.onCancel();
                    }
                }
            });
    }

    public void saveImage(Bitmap imageBitmap, String name, final Model.SaveImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imagesRef = storage.getReference().child("images").child(name);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                listener.fail();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                listener.complete(downloadUrl.toString());
            }
        });
    }


    public void getImage(String url, final Model.GetImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(url);
        final long ONE_MEGABYTE = 1024 * 1024;
        httpsReference.getBytes(3* ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                listener.onSuccess(image);
                // Data for "images/island.jpg" is returns, use this as needed
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Log.d("TAG",exception.getMessage());
                listener.onFail();
                // Handle any errors
            }
        });

    }

    public void handleDatabaseChanges() {
        database.getReference("trips").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                System.out.println("onChildAdded: " + dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                System.out.println("onChildChanged: " + dataSnapshot.getKey());
//                Trip test = TripSql.getTripById(Model.getInstance().modelSql.getReadbleDB(), dataSnapshot.getKey());
//                TripSql.editTrip(Model.getInstance().modelSql.getWritableDB(), dataSnapshot.getValue(Trip.class));
//                Trip test2 = TripSql.getTripById(Model.getInstance().modelSql.getReadbleDB(), dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                System.out.println("onChildRemoved: " + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                System.out.println("onChildMoved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("onCancelled");
            }
        });
    }
}

















