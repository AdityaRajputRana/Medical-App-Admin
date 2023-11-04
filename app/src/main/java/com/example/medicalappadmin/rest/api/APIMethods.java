package com.example.medicalappadmin.rest.api;

import android.content.Context;

import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.requests.HomeReq;
import com.example.medicalappadmin.rest.requests.LoginReq;
import com.example.medicalappadmin.rest.response.DashboardRP;
import com.example.medicalappadmin.rest.response.LoginRP;


public class APIMethods {

    //login method
    public static void loginWithEmailAndPassword(Context context, String email, String password, APIResponseListener<LoginRP> listener){
        LoginReq req = new LoginReq(email, password);
        API.postData(listener, req, EndPoints.login, LoginRP.class, context);
    }

    //signup method



}