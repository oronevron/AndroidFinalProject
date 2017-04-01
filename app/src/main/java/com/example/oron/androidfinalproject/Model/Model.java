package com.example.oron.androidfinalproject.Model;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import com.example.oron.androidfinalproject.MyApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class Model {
    private final static Model instance = new Model();
    ModelFirebase modelFirebase;
    ModelSql modelSql;
    private List<Trip> tripsData = new LinkedList<Trip>();

    private Model() {

        // Add initial trips data
//        for (int i = 0; i < 10; i++){
//            Trip trip = new Trip("id" + i, "trip" + i, "type" + i, i, i + 5, i + 1);
//            addTrip(trip);
//        }

        modelFirebase = new ModelFirebase();
        modelSql = new ModelSql(MyApplication.getAppContext());
    }

    public static Model getInstance() {
        return instance;
    }

    public List<Trip> getAllTrips() {
        return tripsData;
    }

    public void addTrip(Trip trip){
        tripsData.add(trip);
    }

    public void deleteTrip(int index){
        tripsData.remove(index);
    }

    public void editTrip(Trip trip, int index){
        tripsData.set(index, trip);
    }

    public Trip getTripByIndex(int index) {
        return tripsData.get(index);
    }

    public interface GetTripsListener{
        public void onResult(List<Trip> trips);
        public void onCancel();
    }

    public void getAllTripsAsynch(final GetTripsListener listener){
        //1. get the last update date
        final double lastUpdateDate = TripSql.getLastUpdateDate(modelSql.getReadbleDB());

        //2. get all trips records that where updated since last update date
        modelFirebase.getAllTripsAsynch(lastUpdateDate, new GetTripsListener() {
            @Override
            public void onResult(List<Trip> trips) {
                if(trips != null && trips.size() > 0) {
                    //3. update the local DB
                    double recentUpdate = lastUpdateDate;
                    for (Trip trip : trips) {
                        TripSql.add(modelSql.getWritableDB(), trip);
                        if (trip.getLastUpdated() > recentUpdate) {
                            recentUpdate = trip.getLastUpdated();
                        }
                        Log.d("TAG","updating: " + trip.toString());
                    }
                    TripSql.setLastUpdateDate(modelSql.getWritableDB(), recentUpdate);


//                        tripsData.add(s);
                    }

                //return the complete trip list to the caller
                List<Trip> res = TripSql.getAllTrips(modelSql.getReadbleDB());
                listener.onResult(res);
            }

            @Override
            public void onCancel() {
                listener.onCancel();
            }
        });
    }

    public interface GetTrip{
        public void onResult(Trip trip);
        public void onCancel();
    }

    public void getTripById(String id, GetTrip listener){
        modelFirebase.getTripById(id, listener);
    }

    public void add(Trip trip){
        modelFirebase.add(trip);
    }

    private String getLocalImageFileName(String url) {
        String name = URLUtil.guessFileName(url, null, null);
        return name;
    }

    public interface SaveImageListener{
        void complete(String url);
        void fail();
    }

    public void saveImage(final Bitmap imageBitmap, String name, final SaveImageListener listener) {
        //1. save the image remotly
        modelFirebase.saveImage(imageBitmap, name, new SaveImageListener() {
            @Override
            public void complete(String url) {
                // 2. saving the file localy
                String localName = getLocalImageFileName(url);
                Log.d("TAG","cach image: " + localName);
                saveImageToFile(imageBitmap, localName); // synchronously save image locally
                listener.complete(url);
            }
            @Override
            public void fail() {
                listener.fail();
            }
        });
    }

    private void addPicureToGallery(File imageFile){
        //add the picture to the gallery so we dont need to manage the cache size
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        MyApplication.getAppContext().sendBroadcast(mediaScanIntent);
    }

    private void saveImageToFile(Bitmap imageBitmap, String imageFileName){
        try {
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File imageFile = new File(dir,imageFileName);
            imageFile.createNewFile();

            OutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

            addPicureToGallery(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
