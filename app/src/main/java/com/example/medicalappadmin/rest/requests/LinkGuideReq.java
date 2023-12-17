package com.example.medicalappadmin.rest.requests;

public class LinkGuideReq {
    String guideId;
    int pageNumber;

    public LinkGuideReq() {
    }

    public LinkGuideReq(String guideId, int pageNumber) {
        this.guideId = guideId;
        this.pageNumber = pageNumber;
    }

    public String getGuideId() {
        return guideId;
    }

    public void setGuideId(String guideId) {
        this.guideId = guideId;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
