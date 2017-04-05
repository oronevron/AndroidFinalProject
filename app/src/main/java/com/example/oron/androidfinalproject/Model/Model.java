package com.example.oron.androidfinalproject.Model;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.webkit.URLUtil;

import com.example.oron.androidfinalproject.MyApplication;

import java.io.File;
import java.io.FileInputStream;
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

        // Init Firebase model
        modelFirebase = new ModelFirebase();

        // Register for data changes in Firebase in order to implement real-time change support
        modelFirebase.handleDatabaseChanges();

        // Init local database model
        modelSql = new ModelSql(MyApplication.getAppContext());
    }

    public static Model getInstance() {
        return instance;
    }

    public interface GetTripsListener{
        public void onResult(List<Trip> trips, List<Trip> tripsToDelete);
        public void onCancel();
    }

    public void getAllTripsAsynch(final GetTripsListener listener){

        // Get the last update time
        final double lastUpdateDate = TripSql.getLastUpdateDate(modelSql.getReadbleDB());

        // Get all trips records from Firebase that where updated since local last update time
        modelFirebase.getAllTripsAsynch(lastUpdateDate, new GetTripsListener() {
            @Override
            public void onResult(List<Trip> trips, List<Trip> tripsToDelete) {

                // Check if there is trips that added/updated since local last update time
                if(trips != null && trips.size() > 0) {

                    // Init the variable that will contain the maximum last update time with the value of the current local last update time
                    double recentUpdate = lastUpdateDate;

                    // Loop over all these trips
                    for (Trip trip : trips) {

                        // Add the current trip to the local database
                        TripSql.addTrip(modelSql.getWritableDB(), trip);

                        // Get the maximum last update time
                        if (trip.getLastUpdated() > recentUpdate) {
                            recentUpdate = trip.getLastUpdated();
                        }
                    }

                    // Set the last update time as the maximum we find
                    TripSql.setLastUpdateDate(modelSql.getWritableDB(), recentUpdate);
                }

                // Check if there is trips that deleted since local last update time
                if(tripsToDelete != null && tripsToDelete.size() > 0) {

                    // Loop over all these trips
                    for (Trip tripToDelete : tripsToDelete) {

                        // Remove trip's image from the device
                        removeImageFromDevice(tripToDelete.getImageName());

                        // Delete the trip from the local database
                        TripSql.deleteTrip(modelSql.getWritableDB(), tripToDelete.getId());
                    }
                }

                // Return the complete trip list (ordered by trip name) to the caller
                List<Trip> tripsList = TripSql.getAllTrips(modelSql.getReadbleDB());
                listener.onResult(tripsList, null);
            }

            @Override
            public void onCancel() {

                // Call listener's onCancel method
                listener.onCancel();
            }
        });
    }

    public List<Trip> refreshTripsList() {

        // Return all trips (ordered by trip name) from the local database
        return TripSql.getAllTrips(modelSql.getReadbleDB());
    }

    public Trip getTripById(String id){

        // Return the trip by the required id if it exists
        // Otherwise - return null
        return TripSql.getTripById(modelSql.getReadbleDB(), id);
    }

    public interface AddTripListener{
        public void onResult();
        public void onCancel();
    }

    public void addTrip(final Trip trip, final Bitmap imageBitmap, final AddTripListener listener){

        // Get new key from Firebase
        trip.setId(modelFirebase.getNewKey());

        // If we need to save image
        if (imageBitmap != null) {

            // Set the image name
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String imName = "image_" + trip.getId() + "_" + timeStamp + ".jpg";

            // Save image to Firebase and local storage
            saveImage(imageBitmap, imName, new Model.SaveImageListener() {
                @Override
                public void complete(String url) {

                    // Set the trip's image name as the url from Firebase
                    trip.setImageName(url);

                    // Add trip to both Firebase and local database
                    saveTrip(trip, listener);
                }

                @Override
                public void fail() {

                    // Add trip to both Firebase and local database
                    saveTrip(trip, listener);
                }
            });
        }
        // If we don't need to save image
        else {

            // Add trip to both Firebase and local database
            saveTrip(trip, listener);
        }
    }

    private void saveTrip(final Trip trip, final AddTripListener listener) {

        // Add trip to Firebase
        modelFirebase.addTrip(trip, new AddTripListener() {
            @Override
            public void onResult() {

                // Add the trip to the local database
                TripSql.addTrip(Model.getInstance().modelSql.getWritableDB(), trip);

                // Call listener's onResult method
                listener.onResult();
            }

            @Override
            public void onCancel() {

                // Call listener's onCancel method
                listener.onCancel();
            }
        });
    }

    public interface DeleteTripListener{
        public void onResult(String id);
        public void onCancel();
    }

    public void deleteTrip(String id, final DeleteTripListener listener){

        // Delete trip from Firebase
        modelFirebase.deleteTrip(id, new DeleteTripListener() {
            @Override
            public void onResult(String id) {

                // Remove trip's image from the device
                removeImageFromDevice(TripSql.getTripById(modelSql.getReadbleDB(), id).getImageName());

                // Delete the trip from the local database
                TripSql.deleteTrip(modelSql.getReadbleDB(), id);

                // Call listener's onResult method with the id
                listener.onResult(id);
            }

            @Override
            public void onCancel() {

                // Call listener's onCancel method
                listener.onCancel();
            }
        });
    }

    public interface EditTripListener{
        public void onResult();
        public void onCancel();
    }

    public void editTrip(final Trip trip, final Bitmap imageBitmap, final EditTripListener listener) {

        // If we need to save image
        if (imageBitmap != null) {

            // Set the image name
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String imName = "image_" + trip.getId() + "_" + timeStamp + ".jpg";

            // Save image to Firebase and local storage
            saveImage(imageBitmap, imName, new Model.SaveImageListener() {
                @Override
                public void complete(String url) {

                    // Set the trip's image name as the url from Firebase
                    trip.setImageName(url);

                    // Edit trip on Firebase
                    modelFirebase.editTrip(trip, new EditTripListener() {
                        @Override
                        public void onResult() {

                            // Update the current trip on the local database
                            TripSql.editTrip(modelSql.getReadbleDB(), trip);

                            // Call listener's onResult method
                            listener.onResult();
                        }

                        @Override
                        public void onCancel() {

                            // Call listener's onCancel method
                            listener.onCancel();
                        }
                    });
                }

                @Override
                public void fail() {

                    // Update the current trip on the local database
                    TripSql.editTrip(modelSql.getReadbleDB(), trip);
                }
            });
        }
        // If we don't need to save image
        else {

            // Edit trip on Firebase
            modelFirebase.editTrip(trip, new EditTripListener() {
                @Override
                public void onResult() {

                    // Update the current trip on the local database
                    TripSql.editTrip(modelSql.getReadbleDB(), trip);

                    // Call listener's onResult method
                    listener.onResult();
                }

                @Override
                public void onCancel() {

                    // Call listener's onCancel method
                    listener.onCancel();
                }
            });
        }
    }

    private String getLocalImageFileName(String url) {

        // Get image name by url
        String name = URLUtil.guessFileName(url, null, null);
        return name;
    }

    public interface SaveImageListener{
        void complete(String url);
        void fail();
    }

    public void saveImage(final Bitmap imageBitmap, String name, final SaveImageListener listener) {

        // Save the image on Firebase
        modelFirebase.saveImage(imageBitmap, name, new SaveImageListener() {
            @Override
            public void complete(String url) {

                // Get image name by url
                String localName = getLocalImageFileName(url);

                // Synchronously save image locally
                saveImageToFile(imageBitmap, localName);

                // Call listener's complete method with the url
                listener.complete(url);
            }
            @Override
            public void fail() {

                // Call listener's fail method
                listener.fail();
            }
        });
    }

    private void addPicureToGallery(File imageFile){

        // Add the picture to the gallery
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        MyApplication.getAppContext().sendBroadcast(mediaScanIntent);
    }

    private void refreshGallery(){

        // Refresh the gallery in order to remove deleted images from there
        MediaScannerConnection.scanFile(MyApplication.getAppContext(),
                new String[] { Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() },
                null, null);
    }

    private void saveImageToFile(Bitmap imageBitmap, String imageFileName){
        try {

            // Get the storage directory on the device
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);

            // If the directory not exists - create it
            if (!dir.exists()) {
                dir.mkdir();
            }

            // Create new image file with the relevant name
            File imageFile = new File(dir, imageFileName);
            imageFile.createNewFile();

            // Save the image to the file
            OutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

            // Add the picture to the gallery
            addPicureToGallery(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeImageFromDevice(String imageFileName) {

        // Get image name by url
        String localFileName = getLocalImageFileName(imageFileName);

        // Get the storage directory on the device
        File dir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        // Create file object with the relevant image path
        File fdelete = new File(dir + "/" + localFileName);

        // If the image exists
        if (fdelete.exists()) {

            // If the image deleetd successfully
            if (fdelete.delete()) {

                // Refresh the gallery in order to remove deleted images from there
                refreshGallery();
            }
        }
    }

    public interface GetImageListener{
        void onSuccess(Bitmap image);
        void onFail();
    }

    public void loadImage(final String url, final GetImageListener listener) {

        // First try to find the image on the device
        final String localFileName = getLocalImageFileName(url);
        Bitmap image = loadImageFromFile(localFileName);

        // If image not found - try downloading it from Firebase
        if (image == null) {

            // Get image from Firebase
            modelFirebase.getImage(url, new GetImageListener() {
                @Override
                public void onSuccess(Bitmap image) {

                    // Save the image locally
                    saveImageToFile(image, localFileName);

                    // Return the image using the listener
                    listener.onSuccess(image);
                }

                @Override
                public void onFail() {

                    // Call listener's onFail method
                    listener.onFail();
                }
            });
        }
        // If image found locally
        else {

            // Return the image using the listener
            listener.onSuccess(image);
        }
    }

    private Bitmap loadImageFromFile(String imageFileName){
        Bitmap bitmap = null;
        try {

            // Get the storage directory on the device
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            // Create new image file with the relevant name
            File imageFile = new File(dir, imageFileName);

            // Load the image from the file and decode it to bitmap
            InputStream inputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the image bitmap
        return bitmap;
    }
}