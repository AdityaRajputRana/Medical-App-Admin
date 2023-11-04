package com.example.medicalappadmin.rest.api.interfaces;

public interface APIResponseListener<K> {
    void success(K response);
    default void convertData(Object data){
        try {
            if (data == null){
                success(null);
            } else {
                K tData = (K) data;
                success(tData);
            }
        } catch (Exception e){
            e.printStackTrace();
            fail("-2", "Not Convertible: " + e.getMessage(), "", true, true);
        }
    }


    void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable);
}
