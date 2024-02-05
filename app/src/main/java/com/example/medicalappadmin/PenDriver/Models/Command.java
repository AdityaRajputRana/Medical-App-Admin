package com.example.medicalappadmin.PenDriver.Models;

public class Command {
    int type;
    String id;
    int pageNumber;

    int masterPage;
    int slavePage;

    public Command(int type, String id, int pageNumber, int masterPage, int slavePage) {
        this.type = type;
        this.id = id;
        this.pageNumber = pageNumber;
        this.masterPage = masterPage;
        this.slavePage = slavePage;
    }
}
