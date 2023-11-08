package com.example.medicalappadmin.rest.requests;

import com.example.medicalappadmin.Models.Hospital;
import com.example.medicalappadmin.Models.User;

public class SignupReq {
    User user;

    public SignupReq(User user) {
        this.user = user;
    }
}
