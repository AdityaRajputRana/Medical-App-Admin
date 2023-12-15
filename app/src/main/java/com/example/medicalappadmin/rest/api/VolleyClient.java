package com.example.medicalappadmin.rest.api;

import com.android.volley.RequestQueue;
import com.example.medicalappadmin.app.MyApplication;

public class VolleyClient {

    public static String testURL = "http://10.0.2.2:8080/";
    public static String suffix = "";
    public static String BASE_URL = "https://medicalappmangal.onrender.com/";

    public static String getBaseUrl() {
        if (false)
            return testURL;
        return BASE_URL;
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    private static RequestQueue requestQueue = MyApplication.getMainRequestQueue();

}
