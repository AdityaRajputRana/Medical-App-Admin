package com.example.medicalappadmin.rest.response;

import com.example.medicalappadmin.Models.Analytics;
import com.example.medicalappadmin.Models.StaffDetails;

public class HomePageRP {
    StaffDetails staffDetails;
    Analytics analytics;

    public StaffDetails getStaffDetails() {
        return staffDetails;
    }

    public void setStaffDetails(StaffDetails staffDetails) {
        this.staffDetails = staffDetails;
    }

    public Analytics getAnalytics() {
        return analytics;
    }

    public void setAnalytics(Analytics analytics) {
        this.analytics = analytics;
    }

    public HomePageRP() {
    }
}
