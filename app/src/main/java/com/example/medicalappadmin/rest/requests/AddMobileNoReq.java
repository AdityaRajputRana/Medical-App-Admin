package com.example.medicalappadmin.rest.requests;

public class AddMobileNoReq {

    long mobileNumber;
    int pageNumber;

    public AddMobileNoReq() {
    }

    public AddMobileNoReq(long mobileNumber, int pageNumber) {
        this.mobileNumber = mobileNumber;
        this.pageNumber = pageNumber;
    }

    public void setMobileNumber(long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }


    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}


