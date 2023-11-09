package com.example.medicalappadmin.PenDriver;

public interface SmartPenListener {
    void onPermissionsDenied();
    void onPermissionsResult(boolean granted);
    void error(String message);
    void onConnection(boolean establised);
    void disconnected();
    void message(String s, String message);
    void drawEvent(float x, float y, int pageId, int actionType);
}
