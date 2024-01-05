package com.example.medicalappadmin.rest.api.interfaces;


import android.os.Handler;
import android.os.Looper;

public interface APIResponseListener<K> {
    default void sendFail(String code, String message, String redirectLink, boolean retry, boolean cancellabl){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(()->fail(code, message, redirectLink, retry, cancellabl));
    }

    default void sendSuccess(K response){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(()->success(response));
    }
    void success(K response);
    default void convertData(Object data){
        Handler handler = new Handler(Looper.getMainLooper());
        try {
            if (data == null){
                handler.post(()->success(null));
            } else {
                K tData = (K) data;
                handler.post(()->success(tData));
            }
        } catch (Exception e){
            e.printStackTrace();
            handler.post(()->fail("-2", "Not Convertible: " + e.getMessage(), "", true, true));
        }
    }


    void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable);
}
