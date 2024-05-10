package com.example.medicalappadmin.components;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.medicalappadmin.databinding.BsheetAttachmentBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class AttachmentSheetModule {
    private String caseId;
    private static AttachmentSheetModule instance;
    private BottomSheetDialog bottomSheetDialog;
    private BsheetAttachmentBinding attachmentBinding;
    private Context context;

    private AttachmentSheetModule(){
    }

    public static AttachmentSheetModule getInstance(){
        if (instance == null){
            instance = new AttachmentSheetModule();
        }
        return instance;
    }

    public void showAttachmentOptions(String caseId, Activity activity){
        if (caseId == null || activity == null) return;
        this.caseId = caseId;
        this.context = activity;

        if (bottomSheetDialog == null || attachmentBinding == null){
            bottomSheetDialog = new BottomSheetDialog(context);
            attachmentBinding = BsheetAttachmentBinding.inflate(activity.getLayoutInflater());
            bottomSheetDialog.setContentView(attachmentBinding.getRoot());
            setListeners();
        }
        if (!bottomSheetDialog.isShowing()){
            bottomSheetDialog.show();
        }
    }

    private void setListeners() {
        attachmentBinding.oldPrescriptionBtn.setOnClickListener(view-> Toast.makeText(context, "Pres Clicked", Toast.LENGTH_SHORT).show());
    }

}
