package com.example.medicalappadmin.rest.response;

import com.example.medicalappadmin.Models.Page;

public class InitialisePageRP {
    boolean isNewPage;
    Page page;

    public boolean isNewPage() {
        return isNewPage;
    }

    public void setNewPage(boolean newPage) {
        isNewPage = newPage;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public InitialisePageRP() {
    }
}
