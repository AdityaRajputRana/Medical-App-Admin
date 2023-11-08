package com.example.medicalappadmin.Models;

public class Point {
    double x;
    double y;
    int pageNo;

    public Point() {
    }

    public Point(double x, double y, int pageNo) {
        this.x = x;
        this.y = y;
        this.pageNo = pageNo;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getPageNo() {
        return pageNo;
    }
}
