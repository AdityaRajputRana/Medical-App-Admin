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

    enum VERSION{PROD, DEV, LOCAL}
    static VERSION version = VERSION.DEV;

    public static String getBaseUrl() {
        if (version == VERSION.PROD) return prodUrl;
        if (version == VERSION.DEV) return devUrl;
        return localUrl;
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    private static RequestQueue requestQueue = MyApplication.getMainRequestQueue();

}
