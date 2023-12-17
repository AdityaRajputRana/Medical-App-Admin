package com.example.medicalappadmin.PenDriver.Models;

import java.util.ArrayList;

public class NoteModel {
    public int noteId;
    public int sectionId;
    public int ownerId;
    public ArrayList<Integer> pages;

    public NoteModel(int sectionId, int ownerId, int noteId, ArrayList<Integer> pages) {
        this.noteId = noteId;
        this.sectionId = sectionId;
        this.ownerId = ownerId;
        this.pages = pages;
    }

    public int[] getPagesArray(){
        int[] res = new int[pages.size()];
        for (int i = 0; i < pages.size(); i++) {
            res[i] = pages.get(i);
        }
        return res;
    }
}
