package com.example.medicalappadmin.Tools;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    public static String getImageMimeType(Uri uri, Context context) {
        String mimeType = null;
        if (uri != null) {
            ContentResolver resolver = context.getContentResolver();
            mimeType = resolver.getType(uri);
        }
        return mimeType;
    }
    public static String getImageName(Uri uri, Context context) {
        String imageName = null;
        if (uri != null) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    imageName = cursor.getString(nameIndex);
                }
                cursor.close();
            }
        }
        return imageName;
    }
    public static File getImageFile(Uri uri, Context context) {
        File imageFile = null;
        if (uri != null) {
            String imagePath = null;
            if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    imagePath = cursor.getString(columnIndex);
                    cursor.close();
                }
            } else if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
                imagePath = uri.getPath();
            }

            if (imagePath != null) {
                imageFile = new File(imagePath);
            }
        }
        return imageFile;
    }
    public static String getFileExtension(Uri uri, Context context) {
        String extension = null;
        if (uri != null) {
            ContentResolver resolver = context.getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String type = resolver.getType(uri);
            extension = mime.getExtensionFromMimeType(type);
            if (extension == null) {
                String path = uri.getPath();
                if (path != null) {
                    int lastDotIndex = path.lastIndexOf(".");
                    if (lastDotIndex != -1 && lastDotIndex < path.length() - 1) {
                        extension = path.substring(lastDotIndex + 1);
                    }
                }
            }
        }
        return extension;
    }

    public static File getFile(Context context, Uri uri) {
        File file = null;
        if (uri != null) {
            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    String fileName = getImageName(uri, context);
                    String fileExtension = getFileExtension(uri, context);
                    if (fileName != null && fileExtension != null) {
                        file = File.createTempFile(fileName, "." + fileExtension, context.getCacheDir());
                        outputStream = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }


    private static String getFilePath(Context context, Uri uri) {
        String filePath = null;
        if (context != null && uri != null) {
            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                if (columnIndex != -1) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
            }
        }
        return filePath;
    }
}
