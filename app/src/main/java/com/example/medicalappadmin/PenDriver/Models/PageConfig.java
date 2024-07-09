package com.example.medicalappadmin.PenDriver.Models;

import java.util.ArrayList;

public class PageConfig {
    public String layout;
    public String pageBg;
    public Dimensions pageSize;
    public ArrayList<PageConfigSymbol> symbols;



    public static class Dimensions {
        public float height;
        public float width;
    }

    public static class PageConfigSymbol {
        public String name;
        public int id;
        public Bounds bounds;
        public String type;
    }

    public static class Bounds {
        public float xmin, ymin, xmax, ymax;
    }

    public PageConfig() {
    }
}
