package com.example.medicalappadmin.rest.requests;

public class LoginReq {
    String email, password;

    public LoginReq(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
