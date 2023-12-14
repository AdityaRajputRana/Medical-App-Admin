package com.example.medicalappadmin.rest.requests;

public class AddDetailsReq {

    int pageNumber;
    String fullName;
    String gender;
    //TODO add age
    Long mobileNumber;
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

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(Long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
