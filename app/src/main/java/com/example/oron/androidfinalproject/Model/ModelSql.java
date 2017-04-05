package com.example.oron.androidfinalproject.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class ModelSql {
    final static int VERSION = 48;

    Helper sqlDb;

    public ModelSql(Context context){
        sqlDb = new Helper(context);
    }

    public SQLiteDatabase getWritableDB(){
        return sqlDb.getWritableDatabase();
    }

    public SQLiteDatabase getReadbleDB(){
        return sqlDb.getReadableDatabase();
    }

    class Helper extends SQLiteOpenHelper {
        public Helper(Context context) {
            super(context, "database.db", null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            // Create both trips and last upadte time tables
            TripSql.create(db);
            LastUpdateSql.create(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            // Drop both trips and last upadte time tables
            TripSql.drop(db);
            LastUpdateSql.drop(db);

            // Recreate both trips and last upadte time tables
            onCreate(db);
        }
    }
}
