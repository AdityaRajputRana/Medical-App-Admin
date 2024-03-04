package com.example.medicalappadmin.PenDriver.Models;

public class Symbol {
    int id;
    String name;
    float xmin;
    float ymin;
    float ymax;
    float xmax;


    public boolean isApplicable(float x, float y){
        return (xmin <= x && x <= xmax && ymin <= y && y <= ymax);
    }

    public boolean isApplicable(float x, float y, float scaleFactor){
        x *= scaleFactor;
        y *= scaleFactor;
        return (xmin <= x && x <= xmax && ymin <= y && y <= ymax);
    }

    public Symbol(int id, String name, float xmin, float ymin, float xmax, float ymax) {
        this.id = id;
        this.name = name;
        this.xmin = xmin;
        this.ymin = ymin;
        this.ymax = ymax;
        this.xmax = xmax;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
