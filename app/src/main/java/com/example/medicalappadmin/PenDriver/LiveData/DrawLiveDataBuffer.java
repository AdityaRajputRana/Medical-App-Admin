package com.example.medicalappadmin.PenDriver.LiveData;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;

public class DrawLiveDataBuffer {

    private static MutableLiveData<ArrayList<DrawAction>> drawBuffer;

    private static MutableLiveData<ArrayList<DrawAction>> getDrawBufferInstance(){
        if (drawBuffer == null)
            drawBuffer = new MutableLiveData<>(new ArrayList<DrawAction>());
        return drawBuffer;
    }

    public static void observeBuffer(@NonNull LifecycleOwner owner, @NonNull Observer<ArrayList<DrawAction>> observer){
        getDrawBufferInstance().observe(owner, observer);
    }
    public static void removeObserver(Observer observer){
        getDrawBufferInstance().removeObserver(observer);
    }

    public static void insertAction(DrawAction action){
        ArrayList<DrawAction> bufferInstance = getDrawBufferInstance().getValue();
        bufferInstance.add(action);
        getDrawBufferInstance().setValue(bufferInstance);

        Log.i("lv-data", "insert val");
        //Todo: setValue
    }

    public static ArrayList<DrawAction> emptyBuffer(){
        ArrayList<DrawAction> res = new ArrayList<>(getDrawBufferInstance().getValue());
        getDrawBufferInstance().getValue().clear();
        return res;
    }

    public static ArrayList<DrawAction> readBuffer(){
        return new ArrayList<>(getDrawBufferInstance().getValue());
    }




    public static class DrawAction {
        public float x, y;
        public int pageId, actionType;
        public boolean isOffline;

        public DrawAction(float x, float y, int pageId, int actionType, boolean isOffline) {
            this.x = x;
            this.y = y;
            this.pageId = pageId;
            this.actionType = actionType;
            this.isOffline = isOffline;
        }
    }
}
