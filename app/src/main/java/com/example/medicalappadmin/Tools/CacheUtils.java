package com.example.medicalappadmin.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class CacheUtils {
    //static cache function with key value and expiryTime with context as arguments and store it in local storage ttl in hours
    public static void cache(Context context, String key, String value, int ttl){
        Log.i("cache", "cache: " + key + " :" + value + ":" + ttl);
        SharedPreferences sharedPreferences = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.putLong(key + "_exp", System.currentTimeMillis() + ttl * 60 * 60 * 1000);
        editor.apply();
    }

    public static String getCached(Context context, String key){
        Log.i("cache", "getCached: " + key);
        SharedPreferences sharedPreferences = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
        long ttl = sharedPreferences.getLong(key + "_exp", 0);
        if (ttl > System.currentTimeMillis()){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(key);
            editor.remove(key + "_ttl");
            editor.apply();
            return null;
        }
        return sharedPreferences.getString(key, null);
    }


}
