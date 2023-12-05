package com.example.medicalappadmin.PenDriver;

import kr.neolab.sdk.metadata.structure.Symbol;

public interface SmartPenListener {
    void onPermissionsDenied();
    void onPermissionsResult(boolean granted);
    void error(String message);
    void onConnection(boolean establised);
    void disconnected();
    void message(String s, String message);
    void drawEvent(float x, float y, int pageId, int actionType);
    void onPaperButtonPress(int id, String name);
}
