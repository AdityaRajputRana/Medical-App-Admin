package com.example.medicalappadmin.Models;

public class Additional {
      String public_url;


      MetaData metaData;

      Details details;

      Directory directory;

    public String getPublic_url() {
        return public_url;
    }

    public void setPublic_url(String public_url) {
        this.public_url = public_url;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public Additional() {
    }

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