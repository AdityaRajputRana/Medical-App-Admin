package com.example.medicalappadmin.components;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicalappadmin.databinding.DialogPenBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class PenConnectionFlow {
    private static PenConnectionFlow instance;
    private AppCompatActivity activity;
    public static PenConnectionFlow getInstance(AppCompatActivity activity) {
        if (instance == null || instance.activity != activity)
            instance = new PenConnectionFlow(activity);
        return instance;
    }

    public void startPenConnectionFlow() {

    }

    public void stopPenConnectionFlow() {

    }


    private BottomSheetDialog bottomSheetDialog;
    private DialogPenBinding binding;
    public PenConnectionFlow(AppCompatActivity activity) {
        this.activity = activity;
        bottomSheetDialog = new BottomSheetDialog(activity);
        binding = DialogPenBinding.inflate(activity.getLayoutInflater());
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.setContentView(binding.getRoot());
        bottomSheetDialog.setCancelable(true);
    }
}
