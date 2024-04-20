package com.example.medicalappadmin.Models;

public class FileMetadata {
    public String ext;
    public String mime;
    public String type;
    public String fileName;
    public String description;
    public String uploader;
    public long uploadedAt;

    public FileMetadata(String ext, String mime, String type) {
        this.ext = ext;
        this.mime = mime;
        this.type = type;
    }

    public FileMetadata(String ext, String mime, String type, String fileName) {
        this.ext = ext;
        this.mime = mime;
        this.type = type;
        this.fileName = fileName;
    }



    public FileMetadata() {
    }
}
