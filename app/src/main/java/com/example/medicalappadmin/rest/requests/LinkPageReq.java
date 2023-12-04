package com.example.medicalappadmin.rest.requests;

public class LinkPageReq {
    String patientId;
    int pageNumber;
    String caseId;

    public String getPatientId() {
        return patientId;
    }


    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public LinkPageReq() {
    }
}
