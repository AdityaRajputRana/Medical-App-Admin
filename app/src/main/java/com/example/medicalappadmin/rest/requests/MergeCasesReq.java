package com.example.medicalappadmin.rest.requests;

public class MergeCasesReq {
    String fromCaseId;
    String toCaseId;

    public MergeCasesReq() {
    }

    public void setFromCaseId(String fromCaseId) {
        this.fromCaseId = fromCaseId;
    }

    public void setToCaseId(String toCaseId) {
        this.toCaseId = toCaseId;
    }

    public MergeCasesReq(String fromCaseId, String toCaseId) {
        this.fromCaseId = fromCaseId;
        this.toCaseId = toCaseId;
    }
}
