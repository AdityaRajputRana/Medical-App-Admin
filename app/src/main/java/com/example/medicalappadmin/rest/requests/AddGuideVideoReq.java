package com.example.medicalappadmin.rest.requests;

public class AddGuideVideoReq {

    String name;
    String description;
    String url;

    public AddGuideVideoReq(String name, String description, String url) {
        this.name = name;
        this.description = description;
        this.url = url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AddGuideVideoReq() {
    }
}
