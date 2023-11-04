package com.example.medicalappadmin.rest.requests.App;

import android.content.Context;
import android.provider.Settings;


public class InputRequest {
    String salt;
    Object input;
    long timestamp;
    String hash;
    String deviceId;
    String endpoint;
    String uid;

    public InputRequest(String random_string, Object input, long timestamp, String hash, Context context, String endpoint) {
        this.salt = random_string;
        this.input = input;
        this.timestamp = timestamp;
        this.hash = hash;
        this.deviceId = ""+ Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        this.endpoint = endpoint;
//        if (FirebaseAuth.getInstance().getCurrentUser() !=  null){
//            this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        }
    }
}
