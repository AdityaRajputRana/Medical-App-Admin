package com.example.medicalappadmin.rest.api;

import android.content.Context;

import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.requests.HomeReq;
import com.example.medicalappadmin.rest.requests.LoginReq;
import com.example.medicalappadmin.rest.response.DashboardRP;
import com.example.medicalappadmin.rest.response.LoginRP;


public class APIMethods {

//    public static void uploadProfilePicture(Uri fileUri, Context context, FileTransferResponseListener<MessageRP> listener){
//        byte[] file = Utils.getImageBytes(fileUri, context);
//        FileReq req = new FileReq();
//        API.postFile(context, listener, req, EndPoints.updateProfilePhoto, MessageRP.class, "profilePhoto", "image/png", file);
//    }


//    public static void getDashboard(Context context, APIResponseListener<DashboardRP> listener) {
//        HomeReq req = new HomeReq();
//        API.postData(listener, req, EndPoints.dashboard, DashboardRP.class, context);
//    }

    public static void loginWithEmailAndPassword(Context context, String email, String password, APIResponseListener<LoginRP> listener){
        LoginReq req = new LoginReq(email, password);
        API.postData(listener, req, EndPoints.login, LoginRP.class, context);
    }

}