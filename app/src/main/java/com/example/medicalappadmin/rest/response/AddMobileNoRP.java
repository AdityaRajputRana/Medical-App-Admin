package com.example.medicalappadmin.rest.response;

import com.example.medicalappadmin.Models.LinkedPatient;

import java.util.ArrayList;

public class AddMobileNoRP {
    ArrayList<LinkedPatient> patients;

    public AddMobileNoRP() {
    }

    public ArrayList<LinkedPatient> getPatients() {
        return patients;
    }
}
