package com.example.medicalappadmin.rest.api;

import com.android.volley.RequestQueue;
import com.example.medicalappadmin.app.MyApplication;

public class VolleyClient {

    public static String suffix = "";

    static String localUrl = "http://10.0.2.2:8080/";
    static String devUrl = "https://medicalappmangal.onrender.com/";
    static String prodUrl = "";

    static boolean useProd = false;
    static boolean useDev = false;

    public static String getBaseUrl() {
        if (useProd) return prodUrl;
        if (useDev) return devUrl;
        return localUrl;
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    private static RequestQueue requestQueue = MyApplication.getMainRequestQueue();

}
