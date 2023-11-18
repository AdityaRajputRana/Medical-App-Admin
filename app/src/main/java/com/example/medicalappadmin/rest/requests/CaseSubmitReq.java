package com.example.medicalappadmin.rest.requests;

public class CaseSubmitReq {
    String caseId;

    public CaseSubmitReq() {
    }

    public CaseSubmitReq(String caseId) {
        this.caseId = caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }
}
