package com.example.medicalappadmin.PenDriver.LiveData;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.medicalappadmin.PenDriver.Models.PenStateData;

public class PenStatusLiveData {

    private static PenStatusLiveData penStatusLiveData;
    public static PenStatusLiveData getPenStatusLiveData(){
        if (penStatusLiveData == null)
            penStatusLiveData = new PenStatusLiveData();
        return penStatusLiveData;
    }




    private MutableLiveData<Boolean> isConnected;

    public LiveData<Boolean> getIsConnected() {
        if (isConnected == null)
            isConnected = new MutableLiveData<Boolean>(false);
        return isConnected;
    }

    public void setIsConnected(boolean connected) {
        if (isConnected == null)
            isConnected = new MutableLiveData<Boolean>(connected);
        isConnected.setValue(connected);
    }





}
