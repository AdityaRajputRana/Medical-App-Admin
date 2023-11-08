package com.example.medicalappadmin.rest.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.api.interfaces.FileTransferResponseListener;
import com.example.medicalappadmin.rest.requests.App.AppRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

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
                                    listener.fail("2", request.getString("message"), "", true, true);
                                }
                            } catch (Exception e) {
                                listener.fail("1", "The received response is not good", "", true, true);
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
                                listener.fail(String.valueOf(error.networkResponse.statusCode), message, "", true, true);
                            }
                        }
                    }){

            };

            VolleyClient.getRequestQueue().add(jsonObjectRequest);

        } catch (Exception e){
            e.printStackTrace();
        }



    }
    public static void postData(APIResponseListener listener, Object rawData, String endpoint, Class klass, Context context){
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
                            try {
                                Log.i("eta Response", response.toString());
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
                                        Log.i("eta rsp data", data);
                                        if (klass == String.class)
                                            listener.success(data);
                                        else
                                            listener.convertData(new Gson().fromJson(data, klass));
                                    } else {
                                        listener.convertData(null);
                                    }
                                } else {
                                    if (response.getString("message").contains("LOGOUT")) {
                                        Methods.showForceLogOutDialog((Activity) context);
                                    }
                                    listener.fail("2", response.getString("message"), "", true, true);
                                }
                            } catch (Exception e) {
                                Log.i("Lesson Response", response.toString());
                                listener.fail("1", "Response Conversion Error: " + e.getMessage().toString(), "", true, true);
                                e.printStackTrace();
                            }
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
                                    listener.fail(String.valueOf(error.networkResponse.statusCode), message, "", true, true);
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
                    }
                    return params;
                }

            };

            VolleyClient.getRequestQueue().add(jsonObjectRequest);

        } catch (Exception e){
            e.printStackTrace();
        }



    }

    public static void postFile(Context context, FileTransferResponseListener listener, Object rawData, String endpoint, Class klass,
                                String name, String fileType, byte[] file){
        try {
            String encodedData = "";
            AppRequest request = HashUtils.getHashedDataObject(rawData, context, endpoint);
            Log.i("encodedData", request.getData());
            if (request != null){
                encodedData = request.getData();
            }
            String finalEncodedData = encodedData;

            String url = VolleyClient.getBaseUrl() + endpoint + VolleyClient.suffix;
            Log.i("eta url", url);

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse rsp) {
                           onNetWorkResponse(listener, rsp, klass);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("eta eror", new Gson().toJson(error));
                            if (error.networkResponse != null && error.networkResponse.data != null){
                                Log.i("Eta network data", String.valueOf(error.networkResponse.data));
                                onNetWorkResponse(listener, error.networkResponse, klass);
                            } else {
                                listener.fail("VE-1", String.valueOf(error) + " : " + error.getMessage(), "", false, true );
                            }
                        }
                    }, listener) {

                long totalSize = file.length;

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("data", finalEncodedData);
                    return params;
                }
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    long imagename = System.currentTimeMillis();
                    totalSize = file.length;
                    params.put("image", new DataPart(imagename + ".png", file));
                    return params;
                }

                long transferred = 0;

                @Override
                protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
                    transferred += (256*1024);
                    Log.i("Eta Transferred Bytes", String.valueOf(transferred));
                    int progress = (int) ((transferred * 100f)/file.length);
                    listener.onProgress(progress);
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
