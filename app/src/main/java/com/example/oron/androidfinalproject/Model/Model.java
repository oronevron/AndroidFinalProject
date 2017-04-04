package com.example.oron.androidfinalproject.Model;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import com.example.oron.androidfinalproject.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        modelFirebase.handleDatabaseChanges();
        modelSql = new ModelSql(MyApplication.getAppContext());
    }

    public static Model getInstance() {
        return instance;
    }

    public List<Trip> getAllTrips() {
        return tripsData;
    }

//    public void addTrip(Trip trip){
//        tripsData.add(trip);
//    }

//    public void deleteTrip(int index){
//        tripsData.remove(index);
//    }

//    public void editTrip(Trip trip, int index){
//        tripsData.set(index, trip);
//    }

//    public Trip getTripByIndex(int index) {
//        return tripsData.get(index);
//    }

    public interface GetTripsListener{
        public void onResult(List<Trip> trips, List<Trip> tripsToDelete);
        public void onCancel();
    }

    public void getAllTripsAsynch(final GetTripsListener listener){
        //1. get the last update date
        final double lastUpdateDate = TripSql.getLastUpdateDate(modelSql.getReadbleDB());

        //2. get all trips records that where updated since last update date
        modelFirebase.getAllTripsAsynch(lastUpdateDate, new GetTripsListener() {
            @Override
            public void onResult(List<Trip> trips, List<Trip> tripsToDelete) {
                if(trips != null && trips.size() > 0) {
                    //3. update the local DB
                    double recentUpdate = lastUpdateDate;
                    for (Trip trip : trips) {
                        TripSql.addTrip(modelSql.getWritableDB(), trip);
                        if (trip.getLastUpdated() > recentUpdate) {
                            recentUpdate = trip.getLastUpdated();
                        }
                        Log.d("TAG","updating: " + trip.toString());
                    }
                    TripSql.setLastUpdateDate(modelSql.getWritableDB(), recentUpdate);


//                        tripsData.add(s);
                }

                if(tripsToDelete != null && tripsToDelete.size() > 0) {
                    for (Trip tripToDelete : tripsToDelete) {
                        TripSql.deleteTrip(modelSql.getWritableDB(), tripToDelete.getId());
                    }
                }

                //return the complete trip list to the caller
                List<Trip> res = TripSql.getAllTrips(modelSql.getReadbleDB());
                listener.onResult(res, null);
            }

            @Override
            public void onCancel() {
                listener.onCancel();
            }
        });
    }

    public List<Trip> refreshTripsList() {
        return TripSql.getAllTrips(modelSql.getReadbleDB());
    }

//    public interface GetTrip{
//        public void onResult(Trip trip);
//        public void onCancel();
//    }

//    public void getTripById(String id, GetTrip listener){
    public Trip getTripById(String id){
//        modelFirebase.getTripById(id, listener);

        return TripSql.getTripById(modelSql.getReadbleDB(), id);
    }

    public interface AddTripListener{
        public void onResult();
        public void onCancel();
    }

    public void addTrip(final Trip trip, final Bitmap imageBitmap, final AddTripListener listener){
        // Get new key from Firebase
        trip.setId(modelFirebase.getNewKey());

        // Check if we need to save image or not and act accordingly
        if (imageBitmap != null) {
            // Set the image name
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String imName = "image_" + trip.getId() + "_" + timeStamp + ".jpg";

            // Add image to firebase and local storage
            saveImage(imageBitmap, imName, new Model.SaveImageListener() {
                @Override
                public void complete(String url) {
                    trip.setImageName(url);
                    saveTrip(trip, imageBitmap, listener);
                }

                @Override
                public void fail() {
                    saveTrip(trip, imageBitmap, listener);
                }
            });
        } else {
            saveTrip(trip, imageBitmap, listener);
        }
    }

    private void saveTrip(final Trip trip, final Bitmap imageBitmap, final AddTripListener listener) {
        // Add trip to both firebase and local sql db
        modelFirebase.addTrip(trip, imageBitmap, new AddTripListener() {
            @Override
            public void onResult() {
                TripSql.addTrip(Model.getInstance().modelSql.getWritableDB(), trip);
                listener.onResult();
            }

            @Override
            public void onCancel() {

            }
        });
    }

    public interface DeleteTripListener{
        public void onResult(String id);
        public void onCancel();
    }

    public void deleteTrip(String id, final DeleteTripListener listener){
        modelFirebase.deleteTrip(id, new DeleteTripListener() {
            @Override
            public void onResult(String id) {
                TripSql.deleteTrip(modelSql.getReadbleDB(), id);
                listener.onResult(id);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    public interface EditTripListener{
        public void onResult();
        public void onCancel();
    }

    public void editTrip(final Trip trip, final EditTripListener listener){
        modelFirebase.editTrip(trip, new EditTripListener() {
            @Override
            public void onResult() {
                TripSql.editTrip(modelSql.getReadbleDB(), trip);
                listener.onResult();
            }

            @Override
            public void onCancel() {

            }
        });
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

    public interface GetImageListener{
        void onSuccess(Bitmap image);
        void onFail();
    }

    public void loadImage(final String url, final GetImageListener listener) {
        //1. first try to find the image on the device
        final String localFileName = getLocalImageFileName(url);
        Bitmap image = loadImageFromFile(localFileName);
        if (image == null) {                                      //if image not found - try downloading it from firebase
            Log.d("TAG","fail reading cache image: " + localFileName);

            modelFirebase.getImage(url, new GetImageListener() {
                @Override
                public void onSuccess(Bitmap image) {
                    //2.  save the image localy
//                    String localFileName = getLocalImageFileName(url);
                    Log.d("TAG","save image to cache: " + localFileName);
                    saveImageToFile(image, localFileName);
                    //3. return the image using the listener
                    listener.onSuccess(image);
                }

                @Override
                public void onFail() {
                    listener.onFail();
                }
            });
        } else {
            Log.d("TAG","OK reading cache image: " + localFileName);
            listener.onSuccess(image);
        }
    }

    private Bitmap loadImageFromFile(String imageFileName){
        Bitmap bitmap = null;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir, imageFileName);

            //File dir = context.getExternalFilesDir(null);
            InputStream inputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(inputStream);
            Log.d("tag","got image from cache: " + imageFileName);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}