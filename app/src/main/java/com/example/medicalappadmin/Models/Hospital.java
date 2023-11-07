package com.example.medicalappadmin.Models;

public class Hospital {
    String _id;
    String name;

    public Hospital() {
    }

    public Hospital(String _id, String name) {
        this._id = _id;
        this.name = name;
    }

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }
}
