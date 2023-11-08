package com.example.medicalappadmin.rest.api;

import android.content.Context;

import com.example.medicalappadmin.Models.Hospital;
import com.example.medicalappadmin.Models.Point;
import com.example.medicalappadmin.Models.User;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.requests.HomeReq;
import com.example.medicalappadmin.rest.requests.InitialisePageReq;
import com.example.medicalappadmin.rest.requests.LoginReq;
import com.example.medicalappadmin.rest.requests.SignupReq;
import com.example.medicalappadmin.rest.requests.UploadPointsReq;
import com.example.medicalappadmin.rest.response.DashboardRP;
import com.example.medicalappadmin.rest.response.InitialisePageRP;
import com.example.medicalappadmin.rest.response.LoginRP;
import com.example.medicalappadmin.rest.response.SignupRP;
import com.example.medicalappadmin.rest.response.UploadPointsRP;

import java.util.ArrayList;


public class APIMethods {

    //login method
    public static void loginWithEmailAndPassword(Context context, String email, String password, APIResponseListener<LoginRP> listener){
        LoginReq req = new LoginReq(email, password);
        API.postData(listener, req, EndPoints.login, LoginRP.class, context);
    }

    //signup method for staff
    public static void signUpStaff(Context context, User user, APIResponseListener<SignupRP> listener){
        SignupReq signupReq = new SignupReq(user);
        API.postData(listener,signupReq,EndPoints.signupStaff, SignupRP.class,context);
    }


    public static void initialisePage(Context context, int pageNumber,APIResponseListener<InitialisePageRP> listener){
        InitialisePageReq req = new InitialisePageReq(pageNumber);
         API.postData(listener,req,EndPoints.initialisePage, InitialisePageRP.class,context);
    }


    public static void uploadPoints(Context context, String pageID, ArrayList<Point> points, APIResponseListener<UploadPointsRP> listener){
        UploadPointsReq req = new UploadPointsReq(pageID,points);
         API.postData(listener,req,EndPoints.uploadPoints, UploadPointsReq.class,context);

    }






}