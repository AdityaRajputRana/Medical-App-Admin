package com.example.medicalappadmin.rest.response;


import com.example.medicalappadmin.Models.Company;

import java.util.ArrayList;

public class DashboardRP {
    ArrayList<Company> recommendedCompanies;

    public DashboardRP() {
    }

    public ArrayList<Company> getRecommendedCompanies() {
        if (recommendedCompanies == null)
            recommendedCompanies = new ArrayList<Company>();
        return recommendedCompanies;
    }
}
