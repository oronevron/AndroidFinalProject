package com.example.oron.androidfinalproject;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        // Get the application context
        MyApplication.context = getApplicationContext();
    }
    public static Context getAppContext() {
        // Return the application context
        return MyApplication.context;
    }
}
