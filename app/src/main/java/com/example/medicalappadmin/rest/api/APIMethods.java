package com.example.medicalappadmin.rest.api;

import android.content.Context;

import com.example.medicalappadmin.Models.Hospital;
import com.example.medicalappadmin.Models.User;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.requests.HomeReq;
import com.example.medicalappadmin.rest.requests.LoginReq;
import com.example.medicalappadmin.rest.requests.SignupReq;
import com.example.medicalappadmin.rest.response.DashboardRP;
import com.example.medicalappadmin.rest.response.LoginRP;
import com.example.medicalappadmin.rest.response.SignupRP;


public class APIMethods {

    //login method
    public static void loginWithEmailAndPassword(Context context, String email, String password, APIResponseListener<LoginRP> listener){
        LoginReq req = new LoginReq(email, password);
        API.postData(listener, req, EndPoints.login, LoginRP.class, context);
    }

    //signup method for staff
    public static void signUpStaff(Context context, User user, Hospital hospital, APIResponseListener<SignupRP> listener){
        SignupReq signupReq = new SignupReq(user,hospital);
        API.postData(listener,signupReq,EndPoints.signupStaff, SignupRP.class,context);
    }


}