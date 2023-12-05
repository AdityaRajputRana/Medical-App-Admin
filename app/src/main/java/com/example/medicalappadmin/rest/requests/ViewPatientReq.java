package com.example.medicalappadmin.rest.requests;


public class ViewPatientReq {
   String patientId;

    public ViewPatientReq(String patientId) {
        this.patientId = patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
