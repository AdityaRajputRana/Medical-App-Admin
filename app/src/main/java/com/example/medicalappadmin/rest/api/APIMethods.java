package com.example.medicalappadmin.rest.api;

import android.content.Context;

import com.example.medicalappadmin.Models.Case;
import com.example.medicalappadmin.Models.Point;
import com.example.medicalappadmin.Models.User;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.requests.AddDetailsReq;
import com.example.medicalappadmin.rest.requests.CaseHistoryReq;
import com.example.medicalappadmin.rest.requests.InitialisePageReq;
import com.example.medicalappadmin.rest.requests.LoginReq;
import com.example.medicalappadmin.rest.requests.MergeCasesReq;
import com.example.medicalappadmin.rest.requests.SignupReq;
import com.example.medicalappadmin.rest.requests.UploadPointsReq;
import com.example.medicalappadmin.rest.response.CaseHistoryRP;
import com.example.medicalappadmin.rest.response.InitialisePageRP;
import com.example.medicalappadmin.rest.response.LoginRP;
import com.example.medicalappadmin.rest.response.SignupRP;
import com.example.medicalappadmin.rest.response.EmptyRP;

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


    public static void uploadPoints(Context context, int pageNumber, ArrayList<Point> points, APIResponseListener<EmptyRP> listener){
        UploadPointsReq req = new UploadPointsReq(pageNumber,points);
         API.postData(listener,req,EndPoints.uploadPoints, EmptyRP.class,context);

    }

    //Upload details

    public  static  void addDetails(Context context, AddDetailsReq req,
                                    APIResponseListener<EmptyRP> listener){
        API.postData(listener,req,EndPoints.addDetails, EmptyRP.class,context);
    }


    //Load cases history
    public  static  void loadCaseHistory(Context context, int pageNumber,
                                    APIResponseListener<CaseHistoryRP> listener){
        CaseHistoryReq req = new CaseHistoryReq(pageNumber);
        API.postData(listener,req,EndPoints.caseHistory, CaseHistoryRP.class,context);
    }








}