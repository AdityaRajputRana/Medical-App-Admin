package com.example.medicalappadmin.rest.response.App;

public class StandardResponse {
    String data;
    Boolean success;
    String message;

    public StandardResponse() {
    }

    public String getData() {
        return data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
