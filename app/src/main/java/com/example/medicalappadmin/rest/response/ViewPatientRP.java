package com.example.medicalappadmin.rest.response;

import com.example.medicalappadmin.Models.Case;
import com.example.medicalappadmin.Models.Document;
import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.Models.Patient;

import java.util.ArrayList;

public class ViewPatientRP {

    Patient patientDetails;
    ArrayList<Case> patientCases;

    public Patient getPatientDetails() {
        return patientDetails;
    }

    public void setPatientDetails(Patient patientDetails) {
        this.patientDetails = patientDetails;
    }

    public ArrayList<Case> getPatientCases() {
        return patientCases;
    }

    public void setPatientCases(ArrayList<Case> patientCases) {
        this.patientCases = patientCases;
    }

    public ViewPatientRP() {
    }
}
