package com.example.medicalappadmin.rest.response;

import com.example.medicalappadmin.Models.Guide;

import java.util.ArrayList;

public class GuidesVideosRP {
    ArrayList<Guide> allGuides;

    public GuidesVideosRP() {
    }

    public ArrayList<Guide> getAllGuides() {
        return allGuides;
    }
}
