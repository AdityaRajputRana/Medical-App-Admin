package com.example.medicalappadmin.rest.response;

import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.Models.Patient;

public class InitialisePageRP {
    boolean isNewPage;
    Page page;

    Patient patient;

    public InitialisePageRP() {
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public boolean isNewPage() {
        return isNewPage;
    }

    public void setNewPage(boolean newPage) {
        isNewPage = newPage;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
