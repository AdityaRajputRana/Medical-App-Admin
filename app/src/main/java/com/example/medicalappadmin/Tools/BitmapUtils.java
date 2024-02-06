package com.example.medicalappadmin.Tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.example.medicalappadmin.R;

public class BitmapUtils {

    public static void inspectBitmap(Context context, Bitmap bitmap){
        inspectBitmap(context, bitmap, "No title");
    }
    public static void inspectBitmap(Context context, Bitmap bitmap, String title) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showBmp(context, bitmap, title);
            }
        });
    }

    private static void showBmp(Context context, Bitmap bitmap, String title){
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_image_preview, null);

        // Find the ImageView in the layout
        ImageView imageView = dialogView.findViewById(R.id.imageView);

        // Set the bitmap to the ImageView
        imageView.setImageBitmap(bitmap);

        // Create and show the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setTitle(title)
                .setPositiveButton("OK", null) // You can add buttons or customize as needed
                .show();
    }
}
