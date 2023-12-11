package com.example.medicalappadmin.rest.requests;

import com.example.medicalappadmin.Models.FileMetadata;

public class UploadVoiceReq {
    FileMetadata metaData;
    int pageNumber;

    public UploadVoiceReq(FileMetadata metaData, int pageNumber) {
        this.metaData = metaData;
        this.pageNumber = pageNumber;
    }
}
