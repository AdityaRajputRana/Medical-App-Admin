package com.example.medicalappadmin.rest.response;

import com.example.medicalappadmin.Models.Patient;

import java.util.ArrayList;

public class PatientListRP {

    int totalPages;
    int currentPage;
    ArrayList<Patient> patients;

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public ArrayList<Patient> getPatients() {
        return patients;
    }

    public void setPatients(ArrayList<Patient> patients) {
        this.patients = patients;
    }

    public PatientListRP() {
    }


}
