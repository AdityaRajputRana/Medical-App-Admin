package com.example.medicalappadmin.rest.requests;

import com.example.medicalappadmin.Models.Hospital;
import com.example.medicalappadmin.Models.User;

public class SignupReq {
    User user;
    Hospital hospital;

    public SignupReq(User user, Hospital hospital) {
        this.user = user;
        this.hospital = hospital;
    }
}
