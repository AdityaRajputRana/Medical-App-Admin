package com.example.medicalappadmin.rest.response;

import com.example.medicalappadmin.Models.Additional;
import com.example.medicalappadmin.Models.Document;
import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.Models.Patient;

import java.util.ArrayList;

public class ViewCaseRP {

    String title;
    String _id;
    String updatedAt;
    String diagnosis;
    Patient patient;
    ArrayList<Document> documents;
    ArrayList<Integer> pageNumbers;
    ArrayList<Page> pages;
    boolean shareRequired;
    ArrayList<Additional> additionals;

    public ArrayList<Integer> getPageNumbers() {
        if (pageNumbers == null)
            pageNumbers = new ArrayList<>();
        return pageNumbers;
    }

    public ViewCaseRP() {
    }

    public String getTitle() {
        if (title == null || title.isEmpty()) return "Regular Visit";
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public ArrayList<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(ArrayList<Document> documents) {
        this.documents = documents;
    }

    public ArrayList<Page> getPages() {
        return pages;
    }

    public void setPages(ArrayList<Page> pages) {
        this.pages = pages;
    }

    public boolean isShareRequired() {
        return shareRequired;
    }

    public void setShareRequired(boolean shareRequired) {
        this.shareRequired = shareRequired;
    }

    public ArrayList<Additional> getAdditionals() {
        return additionals;
    }

    public void setAdditionals(ArrayList<Additional> additionals) {
        this.additionals = additionals;
    }
}
