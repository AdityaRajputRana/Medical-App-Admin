package com.example.medicalappadmin.app;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyApplication extends Application {
    private static RequestQueue mainRequestQueue;
    private Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
//        setupActivityListener();
        mainRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }



    public static RequestQueue getMainRequestQueue() {
        return mainRequestQueue;
    }

}
