package com.example.medicalappadmin.rest.api;

import android.content.Context;
import android.util.Base64;

import com.example.medicalappadmin.rest.requests.App.AppRequest;
import com.example.medicalappadmin.rest.requests.App.InputRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class HashUtils {
    private final static String APP_SECRET = "QuasarLabsAndGuruSolutions";
    private static final boolean isHashingEnabled = false;
    private static  final boolean isEncryptionEnabled = false;
    private static String getRandomSalt() {
        Random random = new Random();
        return String.valueOf(random.nextInt(900));
    }

    public static String getHashedData(Object obj, Context context, String endpoint){
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        if (isHashingEnabled) {
            String salt = getRandomSalt();
            long timestamp = System.currentTimeMillis();
            String input = salt + gson.toJson(obj) + String.valueOf(timestamp) + APP_SECRET;
            String hash = md5(input);

            InputRequest inputRequest = new InputRequest(salt, obj, timestamp, hash, context, endpoint);
            String inputReqStr = gson.toJson(inputRequest);
            String encodedInput = toBase64(inputReqStr);
            AppRequest appRequest = new AppRequest(encodedInput);
            String requestData = gson.toJson(new AppRequest(encodedInput));
            return requestData;
        } else {
            return gson.toJson(obj);
        }
    }

    public static AppRequest getHashedDataObject(Object obj, Context context, String endpoint){
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        if (isHashingEnabled) {
            String salt = getRandomSalt();
            long timestamp = System.currentTimeMillis();
            String input = salt + gson.toJson(obj) + String.valueOf(timestamp) + APP_SECRET;
            String hash = md5(input);

            InputRequest inputRequest = new InputRequest(salt, obj, timestamp, hash, context, endpoint);
            String inputReqStr = gson.toJson(inputRequest);
            String encodedInput = toBase64(inputReqStr);
            AppRequest appRequest = new AppRequest(encodedInput);
            return appRequest;
        } else {
            return new AppRequest(gson.toJson(obj));
        }
    }

    private static String md5(String input) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(String.format("%02x", messageDigest[i]));
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toBase64(String input) {
        if (!isEncryptionEnabled)
            return input;
        try {
            String encodeValue = Base64.encodeToString(input.getBytes("UTF-8"), Base64.NO_WRAP);
            return encodeValue;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";

    }

    public static String decode(String input) {
        if (!isEncryptionEnabled)
            return input;
        try {
            byte[] data = Base64.decode(input, Base64.NO_PADDING);
            return new String(data);
        }catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


}
