package com.example.medicalappadmin.Models;

import android.util.Log;

public class Case {
    String _id;
    String updatedAt;
    String hospitalId;
    int pageCount;
    Long createdAt;
    String fullName;
    long mobileNumber;
    String gender;
    String email;

    public String getFullName() {
        if(fullName == null || fullName.isEmpty()){
            return "Untitled Case";
        }
        return fullName;
    }

    public String getMobileNumber() {
        if(mobileNumber == 0 ){
            return  "Case Id: " + get_id();
        }
        return "Mobile Number: " + mobileNumber;
    }

    public Case() {
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

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
