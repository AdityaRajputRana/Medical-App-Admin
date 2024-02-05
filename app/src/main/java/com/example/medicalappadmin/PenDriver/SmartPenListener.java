package com.example.medicalappadmin.PenDriver;


public interface SmartPenListener {
    void onPermissionsDenied();
    void onPermissionsResult(boolean granted);
    void error(String message);
    void onConnection(boolean establised);
    void disconnected();
    void message(String s, String message);
    boolean onPaperButtonPress(int id, String name);

    void startLinkingProcedure(int page);
    void stopLinking(int masterPage, int currentPage);
    boolean linkPages(int masterPage, int slavePage);
}
