package com.example.medicalappadmin.PenDriver.Models;

import com.example.medicalappadmin.PenDriver.LiveData.PenStatusLiveData;


public class SmartPen {
    String id;
    String name;
    String macAddress;
    int rssi = -100;

    String sppAddress = "";
    String leAddress = "";
    String deviceName = "";
    boolean isLe = false;
    int colorCode = 0;
    int productCode = 0;
    int companyCode = 0;

    boolean connected = false;
    boolean authorized = false;

    String password = "0000";

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        PenStatusLiveData.getPenStatusLiveData().setIsConnected(connected);
        this.connected = connected;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void setId(String id) {
        this.id = id;
        this.macAddress = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAddress() {
        return macAddress;
    }


    public String getSppAddress() {
        return sppAddress;
    }

    public void setSppAddress(String sppAddress) {
        this.sppAddress = sppAddress;
    }

    public String getLeAddress() {
        return leAddress;
    }

    public void setLeAddress(String leAddress) {
        this.leAddress = leAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isLe() {
        return isLe;
    }

    public void setLe(boolean le) {
        isLe = le;
    }


    public int getColorCode() {
        return colorCode;
    }

    public void setColorCode(int colorCode) {
        this.colorCode = colorCode;
    }

    public int getProductCode() {
        return productCode;
    }

    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }

    public int getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(int companyCode) {
        this.companyCode = companyCode;
    }

    public SmartPen(String id, String name, String macAddress) {
        this.id = id;
        this.name = name;
        this.macAddress = macAddress;
    }

    public SmartPen(int rssi, String name, String macAddress) {
        this.rssi = rssi;
        this.name = name;
        this.macAddress = macAddress;
    }

    public int getRssi() {
        return rssi;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
