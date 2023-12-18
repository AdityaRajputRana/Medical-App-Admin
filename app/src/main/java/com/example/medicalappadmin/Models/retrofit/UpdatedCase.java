package com.example.medicalappadmin.Models.retrofit;


import com.example.medicalappadmin.Models.Additional;

import java.util.ArrayList;

public class UpdatedCase {

    Pdf pdf;

    String _id;

    long updatedAt;

    String hospitalId;

    String doctorId;

    String creatorId;

    int pageCount;

    long createdAt;

    int __v;

    long mobileNumber;

    String email;

    String fullName;

    String gender;

    String hospitalPatientId;

    ArrayList<Additional> additionals;

    public UpdatedCase() {
    }

    public Pdf getPdf() {
        return pdf;
    }

    public String getId() {
        return _id;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public int getPageCount() {
        return pageCount;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public int getVersion() {
        return __v;
    }

    public long getMobileNumber() {
        return mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getGender() {
        return gender;
    }

    public String getHospitalPatientId() {
        return hospitalPatientId;
    }

    public ArrayList<Additional> getAdditionals() {
        return additionals;
    }
}
