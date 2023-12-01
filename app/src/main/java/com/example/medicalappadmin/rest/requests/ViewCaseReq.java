package com.example.medicalappadmin.rest.requests;


public class ViewCaseReq {
   String caseId;

    public ViewCaseReq(String caseId) {
        this.caseId = caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }
}
