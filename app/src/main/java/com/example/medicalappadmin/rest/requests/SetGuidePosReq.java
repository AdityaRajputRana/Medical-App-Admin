package com.example.medicalappadmin.rest.requests;


public class SetGuidePosReq {
    String guideId;
    int position;

    public void setGuideId(String guideId) {
        this.guideId = guideId;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public SetGuidePosReq() {
    }
}
