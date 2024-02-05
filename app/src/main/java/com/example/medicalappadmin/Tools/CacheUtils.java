package com.example.medicalappadmin.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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

    private static final String CACHE_FOLDER_NAME = "canvas_cache";
    public static void saveCanvasBitmap(Context context, Bitmap bitmap, int pageNumber) {
        if (bitmap == null || context == null) {
            return;
        }

        String fileName = generateFileName(pageNumber);
        File cacheFolder = getOrCreateCacheFolder(context);

        if (cacheFolder != null) {
            File file = new File(cacheFolder, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap loadCanvasBitmapFromStorage(Context context, int pageNumber) {
        String fileName = generateFileName(pageNumber);
        File cacheFolder = getCacheFolder(context);

        if (cacheFolder != null) {
            File file = new File(cacheFolder, fileName);
            if (file.exists()) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    fis.close();
                    return bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static void clearCache(Context context) {
        File cacheFolder = getCacheFolder(context);
        if (cacheFolder != null) {
            File[] files = cacheFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    private static File getOrCreateCacheFolder(Context context) {
        File cacheFolder = new File(context.getFilesDir(), CACHE_FOLDER_NAME);
        if (!cacheFolder.exists()) {
            cacheFolder.mkdirs();
        }
        return cacheFolder;
    }

    private static File getCacheFolder(Context context) {
        return new File(context.getFilesDir(), CACHE_FOLDER_NAME);
    }

    private static String generateFileName(int pageNumber) {
        return "page_" + pageNumber + "_cache.png";
    }

}
