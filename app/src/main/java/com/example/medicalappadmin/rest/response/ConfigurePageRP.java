package com.example.medicalappadmin.rest.response;

import com.example.medicalappadmin.Models.Guide;
import com.example.medicalappadmin.Models.PageDetails;

import java.util.ArrayList;

public class ConfigurePageRP {
    ArrayList<Guide> guides;

    PageDetails pageDetails;

    public ArrayList<Guide> getGuides() {
        return guides;
    }

    public void setGuides(ArrayList<Guide> guides) {
        this.guides = guides;
    }

    public PageDetails getPageDetails() {
        return pageDetails;
    }

    public void setPageDetails(PageDetails pageDetails) {
        this.pageDetails = pageDetails;
    }

    public ConfigurePageRP() {
    }
}
