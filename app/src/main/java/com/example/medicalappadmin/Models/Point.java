package com.example.medicalappadmin.Models;

public class Point {
    public  float x;
    public float y;
    public float dx = 0;
    public float dy = 0;
    int pageNo;
    int actionType = -1;  // 1=pendown 2=penup 3=move -1=other


    public int getActionType() {
        return actionType;
    }

    public Point() {
    }

    public Point(float x, float y, int actionType) {
        this.x = x;
        this.y = y;
        this.actionType = actionType;
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getPageNo() {
        return pageNo;
    }
}
