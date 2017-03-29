package com.example.oron.androidfinalproject.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Or Natan on 29/03/2017.
 */

public class TripSql {
    final static String TRIP_TABLE = "trips";
    final static String TRIP_TABLE_ID = "_id";
    final static String TRIP_TABLE_NAME = "name";
    final static String TRIP_TABLE_TYPE = "type";
    final static String TRIP_TABLE_AGE_MIN = "age_min";
    final static String TRIP_TABLE_DIFFICULTY = "difficulty";
    final static String TRIP_TABLE_IMAGE_NAME = "image_name";

    static public void create(SQLiteDatabase db) {
        db.execSQL("create table " + TRIP_TABLE + " (" +
                TRIP_TABLE_ID + " TEXT PRIMARY KEY," +
                TRIP_TABLE_NAME + " TEXT," +
                TRIP_TABLE_TYPE + " TEXT," +
                TRIP_TABLE_AGE_MIN + " INTEGER," +
                TRIP_TABLE_DIFFICULTY + " INTEGER," +
                TRIP_TABLE_IMAGE_NAME + " TEXT);");
    }

    public static void drop(SQLiteDatabase db) {
        db.execSQL("drop table " + TRIP_TABLE + ";");
    }

    public static List<Trip> getAllTrips(SQLiteDatabase db) {
        Cursor cursor = db.query(TRIP_TABLE, null, null , null, null, null, null);
        List<Trip> trips = new LinkedList<Trip>();

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(TRIP_TABLE_ID);
            int nameIndex = cursor.getColumnIndex(TRIP_TABLE_NAME);
            int typeIndex = cursor.getColumnIndex(TRIP_TABLE_TYPE);
            int ageMinIndex = cursor.getColumnIndex(TRIP_TABLE_AGE_MIN);
            int difficultyIndex = cursor.getColumnIndex(TRIP_TABLE_DIFFICULTY);
            int imageNameIndex = cursor.getColumnIndex(TRIP_TABLE_IMAGE_NAME);
            do {
                String id = cursor.getString(idIndex);
                String name = cursor.getString(nameIndex);
                String type = cursor.getString(typeIndex);
                Integer age_min = cursor.getInt(ageMinIndex);
                Integer difficulty = cursor.getInt(difficultyIndex);
                String imageName = cursor.getString(imageNameIndex);
                Trip trip = new Trip(name, id, type, age_min, difficulty, imageName);
                trips.add(trip);
            } while (cursor.moveToNext());
        }
        return trips;
    }

    public static Trip getStudentById(SQLiteDatabase db, String id) {
        String where = TRIP_TABLE_ID + " = ?";
        String[] args = {id};
        Cursor cursor = db.query(TRIP_TABLE, null, where, args, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(TRIP_TABLE_ID);
            int nameIndex = cursor.getColumnIndex(TRIP_TABLE_NAME);
            int typeIndex = cursor.getColumnIndex(TRIP_TABLE_TYPE);
            int ageMinIndex = cursor.getColumnIndex(TRIP_TABLE_AGE_MIN);
            int difficultyIndex = cursor.getColumnIndex(TRIP_TABLE_DIFFICULTY);
            int imageNameIndex = cursor.getColumnIndex(TRIP_TABLE_IMAGE_NAME);
            String trip_id = cursor.getString(idIndex);
            String name = cursor.getString(nameIndex);
            String type = cursor.getString(typeIndex);
            Integer age_min = cursor.getInt(ageMinIndex);
            Integer difficulty = cursor.getInt(difficultyIndex);
            String imageName = cursor.getString(imageNameIndex);
            Trip trip = new Trip(name, trip_id, type, age_min, difficulty, imageName);
            return trip;
        }
        return null;
    }

    public static void add(SQLiteDatabase db, Trip st) {
        ContentValues values = new ContentValues();
        values.put(TRIP_TABLE_ID, st.getId());
        values.put(TRIP_TABLE_NAME, st.getName());
        values.put(TRIP_TABLE_TYPE, st.getType());
        values.put(TRIP_TABLE_AGE_MIN, st.getAge_min());
        values.put(TRIP_TABLE_DIFFICULTY, st.getDifficulty());
        values.put(TRIP_TABLE_IMAGE_NAME, st.getImageName());
        db.insertWithOnConflict(TRIP_TABLE, TRIP_TABLE_ID, values,SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static double getLastUpdateDate(SQLiteDatabase db){
        return LastUpdateSql.getLastUpdate(db,TRIP_TABLE);
    }
    public static void setLastUpdateDate(SQLiteDatabase db, double date){
        LastUpdateSql.setLastUpdate(db,TRIP_TABLE, date);
    }
}
