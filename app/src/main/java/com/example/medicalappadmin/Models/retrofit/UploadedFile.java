package com.example.medicalappadmin.Models.retrofit;

import com.example.medicalappadmin.Models.Details;
import com.example.medicalappadmin.Models.MetaData;

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
