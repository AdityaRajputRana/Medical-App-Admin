package com.example.medicalappadmin.rest.response;

import java.util.Date;

public class PageConfigMetadataRP {
    String _id;
    String hospitalId;
    String doctorId;
    String configUrl;
    Integer __v;

    public PageConfigMetadataRP() {
    }

    public String get_id() {
        return _id;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getConfigUrl() {
        return configUrl;
    }

    public Integer get__v() {
        return __v;
    }
}
