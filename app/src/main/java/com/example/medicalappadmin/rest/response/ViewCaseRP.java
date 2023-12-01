package com.example.medicalappadmin.rest.response;

import com.example.medicalappadmin.Models.Document;
import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.Models.Patient;

import java.util.ArrayList;

public class ViewCaseRP {

    String title;
    String _id;
    String updatedAt;
    String diagnosis;

    public String getTitle() {
        return title;
    }

    public String get_id() {
        return _id;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public Patient getPatient() {
        return patient;
    }

    public ArrayList<Document> getDocuments() {
        return documents;
    }

    public ArrayList<Page> getPages() {
        return pages;
    }

    public boolean isShareRequired() {
        return shareRequired;
    }

    public ArrayList<String> getAdditionals() {
        return additionals;
    }

    Patient patient;
    ArrayList<Document> documents;

    ArrayList<Page> pages;
    boolean shareRequired;
    ArrayList<String> additionals;


    public ViewCaseRP() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setDocuments(ArrayList<Document> documents) {
        this.documents = documents;
    }

    public void setPages(ArrayList<Page> pages) {
        this.pages = pages;
    }

    public void setShareRequired(boolean shareRequired) {
        this.shareRequired = shareRequired;
    }

    public void setAdditionals(ArrayList<String> additionals) {
        this.additionals = additionals;
    }
}
