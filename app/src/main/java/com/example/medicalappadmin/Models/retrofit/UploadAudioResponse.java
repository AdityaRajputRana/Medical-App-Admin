package com.example.medicalappadmin.Models.retrofit;

public class UploadAudioResponse {

    boolean success;
    String message;
    UploadData data;


    public UploadAudioResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public UploadData getData() {
        return data;
    }
}

