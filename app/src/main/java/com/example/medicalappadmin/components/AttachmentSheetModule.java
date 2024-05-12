package com.example.medicalappadmin.components;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medicalappadmin.Models.FileMetadata;
import com.example.medicalappadmin.PrescriptionActivity;
import com.example.medicalappadmin.databinding.BsheetAttachmentBinding;
import com.example.medicalappadmin.databinding.BsheetSourceSelectorBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.FileTransferResponseListener;
import com.example.medicalappadmin.rest.response.UploadVoiceRP;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class AttachmentSheetModule {
    private String caseId;
    private int pageNumber = -1;
    private static AttachmentSheetModule instance;
    private BottomSheetDialog bottomSheetDialog;
    private BsheetAttachmentBinding attachmentBinding;
    private BsheetSourceSelectorBinding sourceSelectorBinding;
    private BottomSheetDialog sourceDialog;
    private AppCompatActivity context;
    public static final int ATTACHMENT_PICKER_REQ_CODE = 7945;
    private AttachmentInterface attachmentInterface;
    private int processSemaphores = 0;
    private int currentUploadProcesses = 1;

    public void gotFilePickResult(int reqCode, int resCode, Intent data) {
        if (reqCode != ATTACHMENT_PICKER_REQ_CODE || resCode != RESULT_OK) return;
        if (bottomSheetDialog.isShowing()) bottomSheetDialog.dismiss();
        if (sourceDialog.isShowing()) sourceDialog.dismiss();
        handleCameraImageResult(resCode, data);
    }


    private void handleCameraImageResult(int resultCode, Intent data) {
        if (pageNumber == -1 ||  caseId == null || caseId.isEmpty()){
            if (attachmentInterface != null) attachmentInterface.onAttachmentFail("Error while capturing image, page lost");
            return;
        }

        if (context == null) {
            if (attachmentInterface != null) attachmentInterface.onAttachmentFail("Null Context Error");
            return;
        }

        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        Random randomGenerator = new Random();
        int random = randomGenerator.nextInt(9999);
        String newimagename= "CAM_IMG_" + String.valueOf(random);

        try {
            File file = File.createTempFile(newimagename, ".png", context.getCacheDir());
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            uploadAttachment(pageNumber, file, newimagename, ".png", "image/png");
        } catch (IOException e) {
            Log.e("AdiErrLogs", "Error while processing attachment: " + e.getMessage());
            if (attachmentInterface != null) attachmentInterface.onAttachmentFail(e.getMessage());
            return;
        }
    }

    private void uploadAttachment(final int pageNum, File file, String fileNane, String ext, String mime) {
        if (attachmentInterface != null) attachmentInterface.setUploadProgress(0);
        FileMetadata metadata = new FileMetadata(ext, mime, "IMAGE", fileNane);
        metadata.description = "Clicked Live";
        APIMethods.uploadAttachment(context, file, pageNum, metadata, new FileTransferResponseListener<UploadVoiceRP>() {
            @Override
            public void success(UploadVoiceRP response) {
                if (attachmentInterface != null) attachmentInterface.successUpload();
            }

            @Override
            public void onProgress(int percent) {
                if (attachmentInterface != null) attachmentInterface.setUploadProgress(percent);
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                 if (attachmentInterface != null) attachmentInterface.onAttachmentFail("Attachment upload failed: " + message);
            }
        });
    }



    private enum AttachmentType{PRES, REPORT, VIDEO, VOICE};


    public interface AttachmentInterface{
        void setUploadProgress(int progress);
        void successUpload();
        void onAttachmentFail(String err);
    }

    private AttachmentSheetModule(){
        Log.i("Toda", "New Attachement Module initialized");
    }

    public static AttachmentSheetModule getInstance(){
        if (instance == null){
            instance = new AttachmentSheetModule();
        }
        return instance;
    }

    public void showAttachmentOptions(String caseId, AppCompatActivity activity, int pageNumber){
        if (caseId == null || activity == null) return;
        this.caseId = caseId;
        this.context = activity;
        this.pageNumber = pageNumber;
        if (activity instanceof AttachmentInterface) this.attachmentInterface = (AttachmentInterface) activity;

        Log.i("Toda", "New Attachement Views Inflated!");
        bottomSheetDialog = new BottomSheetDialog(context);
        attachmentBinding = BsheetAttachmentBinding.inflate(activity.getLayoutInflater());
        bottomSheetDialog.setContentView(attachmentBinding.getRoot());

        sourceSelectorBinding = BsheetSourceSelectorBinding.inflate(activity.getLayoutInflater());
        sourceDialog = new BottomSheetDialog(context);
        sourceDialog.setContentView(sourceSelectorBinding.getRoot());
        setListeners();

        if (!bottomSheetDialog.isShowing()){
            bottomSheetDialog.show();
        }
    }

    private void setListeners() {
        attachmentBinding.oldPrescriptionBtn.setOnClickListener(view->confirmSource(AttachmentType.PRES));


        sourceSelectorBinding.cameraBtn.setOnClickListener(view->{
            Intent attachmentIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            context.startActivityForResult(attachmentIntent, ATTACHMENT_PICKER_REQ_CODE);
        });

        sourceSelectorBinding.galleryBtn.setOnClickListener(view->{
            Intent attachmentIntent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R && android.os.ext.SdkExtensions.getExtensionVersion(android.os.Build.VERSION_CODES.R) >= 2) {
                attachmentIntent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            } else {
                attachmentIntent = new Intent(Intent.ACTION_GET_CONTENT);
                attachmentIntent.setType("image/*");
            }
            context.startActivityForResult(attachmentIntent, ATTACHMENT_PICKER_REQ_CODE);
        });

        sourceSelectorBinding.docsBtn.setOnClickListener(view->{
            Intent attachmentIntent = new Intent(Intent.ACTION_GET_CONTENT);
            attachmentIntent.setType("application/pdf");

            context.startActivityForResult(attachmentIntent, ATTACHMENT_PICKER_REQ_CODE);
        });
    }

    private AttachmentType currentAttachmentType;
    private void confirmSource(AttachmentType attachmentType) {

        sourceSelectorBinding.cameraBtn.setVisibility(View.VISIBLE);
        sourceSelectorBinding.galleryBtn.setVisibility(View.VISIBLE);
        sourceSelectorBinding.docsBtn.setVisibility(View.VISIBLE);


        bottomSheetDialog.dismiss();
        sourceDialog.show();
    }

}
