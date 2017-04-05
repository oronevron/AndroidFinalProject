package com.example.oron.androidfinalproject.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

public class TripSql {
    final static String TRIP_TABLE = "trips";
    final static String TRIP_TABLE_ID = "_id";
    final static String TRIP_TABLE_NAME = "name";
    final static String TRIP_TABLE_TYPE = "type";
    final static String TRIP_TABLE_DESCRIPTION = "description";
    final static String TRIP_TABLE_DIFFICULTY = "difficulty";
    final static String TRIP_TABLE_IMAGE_NAME = "image_name";
    final static String TRIP_TABLE_USER_ID = "user_id";

    static public void create(SQLiteDatabase db) {
        // Create the trips table
        db.execSQL("create table " + TRIP_TABLE + " (" +
                TRIP_TABLE_ID + " TEXT PRIMARY KEY," +
                TRIP_TABLE_NAME + " TEXT," +
                TRIP_TABLE_TYPE + " TEXT," +
                TRIP_TABLE_DESCRIPTION + " TEXT," +
                TRIP_TABLE_DIFFICULTY + " INTEGER," +
                TRIP_TABLE_IMAGE_NAME + " TEXT," +
                TRIP_TABLE_USER_ID + " TEXT);");
    }

    public static void drop(SQLiteDatabase db) {

        // Drop the trips table
        db.execSQL("drop table " + TRIP_TABLE + ";");
    }

    public static List<Trip> getAllTrips(SQLiteDatabase db) {

        // Get all trips (ordered by trip name)
        Cursor cursor = db.query(TRIP_TABLE, null, null , null, null, null, TRIP_TABLE_NAME);
        List<Trip> trips = new LinkedList<Trip>();

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(TRIP_TABLE_ID);
            int nameIndex = cursor.getColumnIndex(TRIP_TABLE_NAME);
            int typeIndex = cursor.getColumnIndex(TRIP_TABLE_TYPE);
            int descriptionIndex = cursor.getColumnIndex(TRIP_TABLE_DESCRIPTION);
            int difficultyIndex = cursor.getColumnIndex(TRIP_TABLE_DIFFICULTY);
            int imageNameIndex = cursor.getColumnIndex(TRIP_TABLE_IMAGE_NAME);
            int userIdIndex = cursor.getColumnIndex(TRIP_TABLE_USER_ID);
            do {
                String id = cursor.getString(idIndex);
                String name = cursor.getString(nameIndex);
                String type = cursor.getString(typeIndex);
                String description = cursor.getString(descriptionIndex);
                Integer difficulty = cursor.getInt(difficultyIndex);
                String imageName = cursor.getString(imageNameIndex);
                String user_id = cursor.getString(userIdIndex);
                Trip trip = new Trip(name, type, description, difficulty);
                trip.setId(id);
                trip.setImageName(imageName);
                trip.setUser_id(user_id);
                trips.add(trip);
            } while (cursor.moveToNext());
        }
        return trips;
    }

    public static Trip getTripById(SQLiteDatabase db, String id) {

        // Return the trip by the required id if it exists
        // Otherwise - return null
        String where = TRIP_TABLE_ID + " = ?";
        String[] args = {id};
        Cursor cursor = db.query(TRIP_TABLE, null, where, args, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(TRIP_TABLE_ID);
            int nameIndex = cursor.getColumnIndex(TRIP_TABLE_NAME);
            int typeIndex = cursor.getColumnIndex(TRIP_TABLE_TYPE);
            int descriptionIndex = cursor.getColumnIndex(TRIP_TABLE_DESCRIPTION);
            int difficultyIndex = cursor.getColumnIndex(TRIP_TABLE_DIFFICULTY);
            int imageNameIndex = cursor.getColumnIndex(TRIP_TABLE_IMAGE_NAME);
            int userIdIndex = cursor.getColumnIndex(TRIP_TABLE_USER_ID);
            String trip_id = cursor.getString(idIndex);
            String name = cursor.getString(nameIndex);
            String type = cursor.getString(typeIndex);
            String description = cursor.getString(descriptionIndex);
            Integer difficulty = cursor.getInt(difficultyIndex);
            String imageName = cursor.getString(imageNameIndex);
            String user_id = cursor.getString(userIdIndex);
            Trip trip = new Trip(name, type, description, difficulty);
            trip.setId(trip_id);
            trip.setImageName(imageName);
            trip.setUser_id(user_id);
            return trip;
        }
        return null;
    }

    public static void addTrip(SQLiteDatabase db, Trip trip) {

        // Add trip to trips table if there is no already trip with the same id
        // Otherwise - update the trip
        ContentValues values = new ContentValues();
        values.put(TRIP_TABLE_ID, trip.getId());
        values.put(TRIP_TABLE_NAME, trip.getName());
        values.put(TRIP_TABLE_TYPE, trip.getType());
        values.put(TRIP_TABLE_DESCRIPTION, trip.getDescription());
        values.put(TRIP_TABLE_DIFFICULTY, trip.getDifficulty());
        values.put(TRIP_TABLE_IMAGE_NAME, trip.getImageName());
        values.put(TRIP_TABLE_USER_ID, trip.getUser_id());
        db.insertWithOnConflict(TRIP_TABLE, TRIP_TABLE_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static void deleteTrip(SQLiteDatabase db, String id) {

        // Delete the trip from trips table
        String where = TRIP_TABLE_ID + " = ?";
        String[] args = {id};
        db.delete(TRIP_TABLE, where, args);
    }

    public static void editTrip(SQLiteDatabase db, Trip trip) {
        addTrip(db, trip);
    }

    public static double getLastUpdateDate(SQLiteDatabase db){

        // Get the last update time
        return LastUpdateSql.getLastUpdate(db, TRIP_TABLE);
    }
    public static void setLastUpdateDate(SQLiteDatabase db, double date){

        // Set the last update time
        LastUpdateSql.setLastUpdate(db, TRIP_TABLE, date);
    }
}
