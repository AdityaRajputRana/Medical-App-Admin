package com.example.medicalappadmin.rest.requests;

public class CaseHistoryReq {
    int pageNumber;
    String doctorId;
    String creatorId;

    public CaseHistoryReq() {
    }

    public CaseHistoryReq(int pageNumber) {
        this.pageNumber = pageNumber;
    }


    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
}
