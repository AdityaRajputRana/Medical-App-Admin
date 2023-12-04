package com.example.medicalappadmin.rest.api;

import android.content.Context;

import com.example.medicalappadmin.Models.Point;
import com.example.medicalappadmin.Models.User;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.requests.AddDetailsReq;
import com.example.medicalappadmin.rest.requests.AddMobileNoReq;
import com.example.medicalappadmin.rest.requests.CaseHistoryReq;
import com.example.medicalappadmin.rest.requests.CaseSubmitReq;
import com.example.medicalappadmin.rest.requests.InitialisePageReq;
import com.example.medicalappadmin.rest.requests.LinkPageReq;
import com.example.medicalappadmin.rest.requests.LoginReq;
import com.example.medicalappadmin.rest.requests.MergeCasesReq;
import com.example.medicalappadmin.rest.requests.SignupReq;
import com.example.medicalappadmin.rest.requests.UploadPointsReq;
import com.example.medicalappadmin.rest.requests.ViewCaseReq;
import com.example.medicalappadmin.rest.response.AddDetailsRP;
import com.example.medicalappadmin.rest.response.AddMobileNoRP;
import com.example.medicalappadmin.rest.response.CaseHistoryRP;
import com.example.medicalappadmin.rest.response.CaseSubmitRP;
import com.example.medicalappadmin.rest.response.EmptyRP;
import com.example.medicalappadmin.rest.response.InitialisePageRP;
import com.example.medicalappadmin.rest.response.LinkPageRP;
import com.example.medicalappadmin.rest.response.LoginRP;
import com.example.medicalappadmin.rest.response.SignupRP;
import com.example.medicalappadmin.rest.response.ViewCaseRP;

import java.util.ArrayList;


public class APIMethods {

    //login method
    public static void loginWithEmailAndPassword(Context context, String email, String password, APIResponseListener<LoginRP> listener) {
        LoginReq req = new LoginReq(email, password);
        API.postData(listener, req, EndPoints.login, LoginRP.class, context);
    }

    //signup method for staff
    public static void signUpStaff(Context context, User user, APIResponseListener<SignupRP> listener) {
        SignupReq signupReq = new SignupReq(user);
        API.postData(listener, signupReq, EndPoints.signupStaff, SignupRP.class, context);
    }


    public static void initialisePage(Context context, int pageNumber, APIResponseListener<InitialisePageRP> listener) {
        InitialisePageReq req = new InitialisePageReq(pageNumber);
        API.postData(listener, req, EndPoints.initialisePage, InitialisePageRP.class, context);
    }


    public static void uploadPoints(Context context, int pageNumber, ArrayList<Point> points, APIResponseListener<EmptyRP> listener) {
        UploadPointsReq req = new UploadPointsReq(pageNumber, points);
        API.postData(listener, req, EndPoints.uploadPoints, EmptyRP.class, context);

    }

    //Upload details

    public static void addDetails(Context context, AddDetailsReq req, APIResponseListener<AddDetailsRP> listener) {
        API.postData(listener, req, EndPoints.addDetails, AddDetailsRP.class, context);
    }


    //Load cases history
    public static void loadCaseHistory(Context context, int pageNumber, APIResponseListener<CaseHistoryRP> listener) {
        CaseHistoryReq req = new CaseHistoryReq(pageNumber);
        API.postData(listener, req, EndPoints.caseHistory, CaseHistoryRP.class, context);
    }


    //merge cases
    public static void mergeCasesHistory(Context context, String fromCaseId, String toCaseId, APIResponseListener<EmptyRP> listener) {
        MergeCasesReq req = new MergeCasesReq(fromCaseId, toCaseId);
        API.postData(listener, req, EndPoints.mergeCases, EmptyRP.class, context);
    }

    //submit case

    public static void submitCase(Context context, String caseId, APIResponseListener<CaseSubmitRP> listener) {
        CaseSubmitReq req = new CaseSubmitReq(caseId);
        API.postData(listener, req, EndPoints.submitCase, CaseSubmitRP.class, context);
    }

    //view case
    public static void viewCase(Context context, String caseId, APIResponseListener<ViewCaseRP> listener) {
        ViewCaseReq req = new ViewCaseReq(caseId);
        API.postData(listener, req, EndPoints.viewCase, ViewCaseRP.class, context);

    }

    public static void addMobileNumber(Context context, AddMobileNoReq req, APIResponseListener<AddMobileNoRP> listener) {
        API.postData(listener, req, EndPoints.addMobileNo, AddMobileNoRP.class, context);
    }

    public  static void linkPage(Context context, LinkPageReq req,APIResponseListener<LinkPageRP> listener){
        API.postData(listener,req,EndPoints.linkPage, LinkPageRP.class,context);
    }


}