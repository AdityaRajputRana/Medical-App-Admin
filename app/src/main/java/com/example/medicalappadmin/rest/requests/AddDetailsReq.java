package com.example.medicalappadmin.rest.requests;

public class AddDetailsReq {

    int pageNumber;
    String fullName;
    String gender;
    //TODO add age
    Long mobileNumber;

    public int getPageNumber() {
        return pageNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getGender() {
        return gender;
    }

    public Long getMobileNumber() {
        return mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    String email;

    public AddDetailsReq() {
    }

    public AddDetailsReq(int pageNumber, String fullName, String gender, Long mobileNumber, String email) {
        this.pageNumber = pageNumber;
        this.fullName = fullName;
        this.gender = gender;
        this.mobileNumber = mobileNumber;
        this.email = email;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setMobileNumber(Long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
