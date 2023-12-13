package com.example.medicalappadmin.rest.response;


import com.example.medicalappadmin.Models.Guide;

public class SetGuidePosRP {

    Guide demotedGuide;
    Guide promotedGuide;

    public Guide getDemotedGuide() {
        return demotedGuide;
    }

    public void setDemotedGuide(Guide demotedGuide) {
        this.demotedGuide = demotedGuide;
    }

    public Guide getPromotedGuide() {
        return promotedGuide;
    }

    public void setPromotedGuide(Guide promotedGuide) {
        this.promotedGuide = promotedGuide;
    }

    public SetGuidePosRP() {
    }
}
