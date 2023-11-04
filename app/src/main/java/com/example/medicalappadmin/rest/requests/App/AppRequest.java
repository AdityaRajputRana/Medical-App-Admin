package com.example.medicalappadmin.rest.requests.App;

public class AppRequest {
    String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public AppRequest(String data) {
        this.data = data;
    }
}
