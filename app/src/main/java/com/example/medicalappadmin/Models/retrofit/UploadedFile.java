package com.example.medicalappadmin.Models.retrofit;

public class UploadedFile {
    String public_url;

    MetaData metaData;

    Details details;

    public String getPublicUrl() {
        return public_url;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public Details getDetails() {
        return details;
    }
}
