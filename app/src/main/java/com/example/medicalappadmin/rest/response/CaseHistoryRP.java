package com.example.medicalappadmin.rest.response;

import com.example.medicalappadmin.Models.Case;

import java.util.ArrayList;

public class CaseHistoryRP {

    ArrayList<Case> cases;
    int currentPage;
    int totalPages;

    public CaseHistoryRP() {
    }

    public ArrayList<Case> getCases() {
        return cases;
    }

    public void setCases(ArrayList<Case> cases) {
        this.cases = cases;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }




}
