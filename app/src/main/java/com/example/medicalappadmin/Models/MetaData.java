package com.example.medicalappadmin.Models;

public class MetaData {
    String type;
    String ext;
    String mime;

    String uploader;

    long uploadedAt;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public void setUploadedAt(long uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public MetaData() {
    }

    public String getUploader() {
        return uploader;
    }

    public long getUploadedAt() {
        return uploadedAt;
    }
}
