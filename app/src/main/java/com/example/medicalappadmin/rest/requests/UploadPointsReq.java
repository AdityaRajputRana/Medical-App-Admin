package com.example.medicalappadmin.rest.requests;


import com.example.medicalappadmin.Models.Point;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class UploadPointsReq {

    int pageNumber;
    ArrayList<Point> pointsToAdd;

    public UploadPointsReq(int pageNumber, ArrayList<Point> pointsToAdd) {

        this.pageNumber = pageNumber;
        this.pointsToAdd = pointsToAdd;
    }
}
