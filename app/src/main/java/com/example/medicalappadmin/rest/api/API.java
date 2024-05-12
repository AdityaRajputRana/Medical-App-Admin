package com.example.medicalappadmin.rest.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.medicalappadmin.Tools.CacheUtils;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.api.interfaces.FileTransferResponseListener;
import com.example.medicalappadmin.rest.requests.App.AppRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class API {

    public static void getData(APIResponseListener listener, Object rawData, String endpoint, Class klass, Context context){
        try {
            String data = HashUtils.getHashedData(rawData, context, endpoint);
            JSONObject request = new JSONObject(data);
            String url = VolleyClient.getBaseUrl() + endpoint + VolleyClient.suffix;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, request, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Boolean successful = response.getBoolean("success");
                                if (successful) {
                                    String data = response.getJSONObject("data").toString();
//                                    String decodedData = HashUtils.fromBase64(data);
                                    listener.convertData(new Gson().fromJson(data, klass));
                                } else {
                                    listener.sendFail("2", request.getString("message"), "", true, true);
                                }
                            } catch (Exception e) {
                                listener.sendFail("1", "The received response is not good", "", true, true);
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error.networkResponse != null) {
                                String message = "";
                                if (error.networkResponse.data != null) {
                                    try {
                                        NetworkResponse networkResponse = error.networkResponse;
                                        String errorStr = new String(networkResponse.data);
                                        JSONObject jsonObject = new JSONObject(errorStr);
                                        message = message + " " + jsonObject.getString("message");
                                    } catch (Exception e){
                                        e.printStackTrace();
                                        message = message+" Json Conversion error.";
                                    }
                                }
                                if (error.getMessage() != null && !error.getMessage().isEmpty()) {
                                    message = message + " " + error.getMessage();
                                }
                                listener.sendFail(String.valueOf(error.networkResponse.statusCode), message, "", true, true);
                            }
                        }
                    }){

            };

            VolleyClient.getRequestQueue().add(jsonObjectRequest);

        } catch (Exception e){
            e.printStackTrace();
        }



    }

    private static void convertAndSendResponseBack(APIResponseListener listener, JSONObject response, Class klass, Context context, boolean cache, String endpoint){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(()->Log.i("eta: main", "Got RSP"));
        try {
            Boolean successful = response.getBoolean("success");
            if (successful) {
                handler.post(()->Log.i("eta: main", "Success Check pass"));
                String dataStr = response.getString("data");
                if (dataStr != null
                        && !dataStr.isEmpty() && !dataStr.trim().isEmpty()) {
                    handler.post(()->Log.i("eta: main", "Empty Data check pass"));
//                                    String decodedData = HashUtils.fromBase64(data);
                    handler.post(()->Log.i("eta: main","Data part decoded"));
                    Log.i("eta rsp data", dataStr);

                    if (klass == String.class)
                        listener.sendSuccess(dataStr);
                    else {
                        Object rawObj = new Gson().fromJson(dataStr, klass);
                        handler.post(()->Log.i("eta: main","Data converted to object"));
                        listener.convertData(rawObj);

                    }


                    if (cache){
                        CacheUtils.cache(context, endpoint, dataStr, 24*90);
                    }
                } else {
                    listener.convertData(null);
                }
            } else {
                if (response.getString("message").contains("LOGOUT")) {
                    Methods.showForceLogOutDialog((AppCompatActivity) context);
                }
                listener.sendFail("2", response.getString("message"), "", true, true);
            }
        } catch (Exception e) {
            Log.i("Lesson Response", response.toString());
            listener.sendFail("1", "Response Conversion Error: " + e.getMessage().toString(), "", true, true);
            e.printStackTrace();
        }
    }

    public static void postData(APIResponseListener listener, Object rawData, String endpoint, Class klass, Context context){
        postData(listener, rawData, endpoint, klass, context, false);
    }

    public static void postData(APIResponseListener listener, Object rawData, String endpoint, Class klass, Context context, boolean cache, boolean forceCache){
        if (cache && forceCache){
                String cachedValue = CacheUtils.getCached(context, endpoint);
                if (cachedValue != null) {
                    Log.i("eta-response", "Got cached value");
                    listener.success(new Gson().fromJson(cachedValue, klass));
                    return;
                }
        }
        postData(listener, rawData, endpoint, klass, context, cache);
    }

    public static void postData(APIResponseListener listener, Object rawData, String endpoint, Class klass, Context context, boolean cache){
        if (cache) {
            String cachedValue = CacheUtils.getCached(context, endpoint);
            if (cachedValue != null) {
                Log.i("eta-response", "Got cached value");
                listener.success(new Gson().fromJson(cachedValue, klass));
            }
        }
        try {
            String data = HashUtils.getHashedData(rawData, context, endpoint);
            JSONObject request;
            Log.i("eta", data);
            if (data.equals("\"{}\"")){
                request = new JSONObject();
            } else {
                 request = new JSONObject(data);
            }

            String url = VolleyClient.getBaseUrl() + endpoint + VolleyClient.suffix;

            Log.i("eta data", data);
            Log.i("eta url", url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Thread responseConverterThread = new Thread(() -> {
                                convertAndSendResponseBack(listener, response, klass, context, cache, endpoint);
                            });
                            responseConverterThread.start();
                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error != null) {
                                if (error.networkResponse != null) {
                                    String message = "";
                                    if (error.networkResponse.data != null) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(error.networkResponse.data.toString());
                                            message = message+" " +jsonObject.getString("message");
                                        } catch (Exception e){
                                            e.printStackTrace();
                                            message = message+" Json Conversion error.";
                                        }
                                    }
                                    message = message +" " + error.getMessage();
                                    listener.sendFail(String.valueOf(error.networkResponse.statusCode), message, "", true, true);
                                }
                            }
                        }
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    SharedPreferences preferences = context.getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
                    if (preferences.contains("JWT_TOKEN") && !preferences.getString("JWT_TOKEN", "").isEmpty()){
                        params.put("x-access-token",preferences.getString("JWT_TOKEN",""));
                        Log.i("token", "getHeaders:" + preferences.getString("JWT_TOKEN",""));
                    }
                    return params;
                }

            };

            if (EndPoints.generateCasePDF.equals(endpoint) || EndPoints.viewCase.equals(endpoint))
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*7,
                    3,
                    1.5f));

            VolleyClient.getRequestQueue().add(jsonObjectRequest);

        } catch (Exception e){
            e.printStackTrace();
        }



    }

    public static void postFile(Context context, File mFile, Object rawData, String endpoint, Class klass, FileTransferResponseListener fileListener, String name, String ext){
        try {
            String encodedData = "";
            AppRequest request = HashUtils.getHashedDataObject(rawData, context, endpoint);
            if (request != null){
                encodedData = request.getData();
            }
            String finalEncodedData = encodedData;

            String url = VolleyClient.getBaseUrl() + endpoint + VolleyClient.suffix;
            Log.i("eta url", url);

            byte[] file = new byte[(int) mFile.length()];
            FileInputStream inputStream = new FileInputStream(mFile);
            inputStream.read(file);
            inputStream.close();

            Map<String, String> params = new HashMap<>();
            params.put("data", finalEncodedData);

            Map<String, String> headers = new HashMap<String, String>();
            SharedPreferences preferences = context.getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
            if (preferences.contains("JWT_TOKEN") && !preferences.getString("JWT_TOKEN", "").isEmpty()){
                headers.put("x-access-token",preferences.getString("JWT_TOKEN",""));
            }



            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse rsp) {
                            Log.i("eta rsp", "got");
                            if (context instanceof Activity)
                                ((Activity) context).runOnUiThread(()->onNetWorkResponse(fileListener, rsp, klass));
                            else
                                onNetWorkResponse(fileListener, rsp, klass);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("eta eror", new Gson().toJson(error));
                            if (error.networkResponse != null && error.networkResponse.data != null){
                                Log.i("Eta network data", String.valueOf(error.networkResponse.data));
                                if (context instanceof Activity)
                                    ((Activity) context).runOnUiThread(()->onNetWorkResponse(fileListener, error.networkResponse, klass));
                                else
                                    onNetWorkResponse(fileListener, error.networkResponse, klass);
                            } else {
                                if (context instanceof Activity)
                                    ((Activity) context).runOnUiThread(()->fileListener.fail("VE-1", String.valueOf(error) + " : " + error.getMessage(), "", false, true ));
                                else
                                    fileListener.fail("VE-1", String.valueOf(error) + " : " + error.getMessage(), "", false, true );
                            }
                        }
                    }) {

                long totalSize = mFile.length();

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("data", finalEncodedData);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    SharedPreferences preferences = context.getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
                    if (preferences.contains("JWT_TOKEN") && !preferences.getString("JWT_TOKEN", "").isEmpty()){
                        headers.put("x-access-token",preferences.getString("JWT_TOKEN",""));
                    }
                    return headers;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    totalSize = file.length;
                    params.put("file", new DataPart("file", file));
                    return params;
                }

                long transferred = 0;

                @Override
                protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
                    transferred += (256*1024);
                    Log.i("eta progress", transferred + "/" + file.length);
                    int progress = (int) ((transferred * 100f)/file.length);
                    if (context instanceof Activity)
                        ((Activity) context).runOnUiThread(()->fileListener.onProgress(Math.max(100, progress)));
                    else
                        fileListener.onProgress(progress);
                    return super.parseNetworkResponse(response);
                }
            };
            //End

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    180000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    2f));
            VolleyClient.getRequestQueue().add(multipartRequest);




        } catch (Exception e){
            e.printStackTrace();
        }
    }

//    public static void postFile(Context context, FileTransferResponseListener listener, Object rawData, String endpoint, Class klass,
//                                String name, String fileType, byte[] file){
//        try {
//            String encodedData = "";
//            AppRequest request = HashUtils.getHashedDataObject(rawData, context, endpoint);
//            Log.i("encodedData", request.getData());
//            if (request != null){
//                encodedData = request.getData();
//            }
//            String finalEncodedData = encodedData;
//
//            String url = VolleyClient.getBaseUrl() + endpoint + VolleyClient.suffix;
//            Log.i("eta url", url);
//
//            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
//                    new Response.Listener<NetworkResponse>() {
//                        @Override
//                        public void onResponse(NetworkResponse rsp) {
//                           onNetWorkResponse(listener, rsp, klass);
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.i("eta eror", new Gson().toJson(error));
//                            if (error.networkResponse != null && error.networkResponse.data != null){
//                                Log.i("Eta network data", String.valueOf(error.networkResponse.data));
//                                onNetWorkResponse(listener, error.networkResponse, klass);
//                            } else {
//                                listener.sendFail("VE-1", String.valueOf(error) + " : " + error.getMessage(), "", false, true );
//                            }
//                        }
//                    }, listener) {
//
//                long totalSize = file.length;
//
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("data", finalEncodedData);
//                    return params;
//                }
//                @Override
//                protected Map<String, DataPart> getByteData() {
//                    Map<String, DataPart> params = new HashMap<>();
//                    long imagename = System.currentTimeMillis();
//                    totalSize = file.length;
//                    params.put("image", new DataPart(imagename + ".png", file));
//                    return params;
//                }
//
//                long transferred = 0;
//
//                @Override
//                protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
//                    transferred += (256*1024);
//                    Log.i("Eta Transferred Bytes", String.valueOf(transferred));
//                    int progress = (int) ((transferred * 100f)/file.length);
//                    listener.onProgress(progress);
//                    return super.parseNetworkResponse(response);
//                }
//            };
//            //End
//
//            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
//                    180000,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    2f));
//            VolleyClient.getRequestQueue().add(multipartRequest);
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//
//    }

    private static void onNetWorkResponse(FileTransferResponseListener listener, NetworkResponse rsp, Class klass) {
        try {
            String strRp = new String(rsp.data);
            JSONObject response = new JSONObject(strRp);
            Log.i("Lesson Response", response.toString());
            Boolean successful = response.getBoolean("success");
            if (successful) {
                if (response.getString("data") != null
                        && !response.getString("data").isEmpty() && !response.getString("data").trim().isEmpty()) {
                    String data = "";
                    if (klass == ArrayList.class){
                        data = response.getJSONArray("data").toString();
                    } else if (klass ==  String.class){
                        data = response.getString("data");
                    } else {
                        data = response.getJSONObject("data").toString();
                    }
//                                    String decodedData = HashUtils.fromBase64(data);
                    if (klass == String.class)
                        listener.success(data);
                    else
                        listener.convertData(new Gson().fromJson(data, klass));
                } else {
                    listener.convertData(null);
                }
            } else {
                listener.fail("2", response.getString("message"), "", true, true);
            }
        } catch (Exception e) {
            Log.i("Lesson Response", new String(rsp.data));
            listener.fail("1", "Response Conversion Error: " + e.getMessage().toString(), "", true, true);
            e.printStackTrace();
        }
    }






}
