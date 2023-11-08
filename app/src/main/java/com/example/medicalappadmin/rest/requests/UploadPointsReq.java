package com.example.medicalappadmin.rest.requests;


import com.example.medicalappadmin.Models.Point;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class UploadPointsReq {

    String pageId;
    ArrayList<Point> pointsToAdd;

    public UploadPointsReq(String pageId, ArrayList<Point> pointsToAdd) {

        this.pageId = pageId;
        this.pointsToAdd = pointsToAdd;
    }
}
