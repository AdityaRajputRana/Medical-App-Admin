package com.example.medicalappadmin.rest.response;

import com.example.medicalappadmin.Models.User;

public class LoginRP {
    String jwt;
    long expiresAt;
    String uid;
    User user;
}
