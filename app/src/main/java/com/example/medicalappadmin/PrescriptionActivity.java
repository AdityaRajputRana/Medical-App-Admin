package com.example.medicalappadmin;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.medicalappadmin.Models.FileMetadata;
import com.example.medicalappadmin.Models.Guide;
import com.example.medicalappadmin.Models.LinkedPatient;
import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.Models.Point;
import com.example.medicalappadmin.PenDriver.ConnectionsHandler;
import com.example.medicalappadmin.PenDriver.LiveData.DrawLiveDataBuffer;
import com.example.medicalappadmin.PenDriver.LiveData.PenStatusLiveData;
import com.example.medicalappadmin.PenDriver.Models.SmartPen;
import com.example.medicalappadmin.PenDriver.SmartPenDriver;
import com.example.medicalappadmin.PenDriver.SmartPenListener;
import com.example.medicalappadmin.Tools.Const;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.adapters.OtherGuidesAdapterBS;
import com.example.medicalappadmin.adapters.PagesHistoryAdapter;
import com.example.medicalappadmin.adapters.RelativePreviousCasesAdapter;
import com.example.medicalappadmin.components.LoginSheet;
import com.example.medicalappadmin.components.WebVideoPlayer;
import com.example.medicalappadmin.components.YTVideoPlayer;
import com.example.medicalappadmin.databinding.ActivityPrescriptionBinding;
import com.example.medicalappadmin.databinding.BsheetAddMobileNoBinding;
import com.example.medicalappadmin.databinding.DialogPenBinding;
import com.example.medicalappadmin.databinding.DialogViewCaseBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.api.interfaces.FileTransferResponseListener;
import com.example.medicalappadmin.rest.requests.AddDetailsReq;
import com.example.medicalappadmin.rest.requests.AddMobileNoReq;
import com.example.medicalappadmin.rest.requests.EmptyReq;
import com.example.medicalappadmin.rest.requests.LinkGuideReq;
import com.example.medicalappadmin.rest.requests.LinkPageReq;
import com.example.medicalappadmin.rest.response.AddDetailsRP;
import com.example.medicalappadmin.rest.response.AddMobileNoRP;
import com.example.medicalappadmin.rest.response.ConfigurePageRP;
import com.example.medicalappadmin.rest.response.EmptyRP;
import com.example.medicalappadmin.rest.response.InitialisePageRP;
import com.example.medicalappadmin.rest.response.LinkGuideRP;
import com.example.medicalappadmin.rest.response.LinkPageRP;
import com.example.medicalappadmin.rest.response.SubmitCaseRP;
import com.example.medicalappadmin.rest.response.UploadVoiceRP;
import com.example.medicalappadmin.rest.response.ViewCaseRP;
import com.example.medicalappadmin.rest.response.ViewPatientRP;
import com.google.android.material.bottomsheet.BottomSheetDialog;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;


public class PrescriptionActivity extends AppCompatActivity implements SmartPenListener {

    private static final int PATIENT_ID_LINK_REQUEST = 459;
    String TAG = "pres";
    boolean isPenSearchRunning = false;
    ActivityPrescriptionBinding binding;
    DialogPenBinding dialogPenBinding;
    AlertDialog dialog;
    SmartPen selectedPen;
    ArrayList<SmartPen> smartPens;
    ArrayList<Point> pointsArrayList;
    ViewTreeObserver viewTreeObserver;
    int currentPageNumber = -1;
    ArrayList<Point> pendingPoints = new ArrayList<>();
    Page currentPage;
    InitialisePageRP currentInitPageResponse;
    ActionBarDrawerToggle actionBarDrawerToggle;



    String gender = "M";
    SmartPenDriver driver = SmartPenDriver.getInstance(this); //driverStep1


    //Bottom Sheet attach audio views
    TextView bsAVActionText;
    AppCompatButton btnBSAVAttach;
    AppCompatButton btnBSAVStop;
    AppCompatButton btnBSAVStart;

    private MediaRecorder mediaRecorder;
    private LottieAnimationView voiceAnimation;
    private ImageView ivDeleteAudio;
    private String outputFile;


    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE){
            handleCameraImageResult(resultCode, data);
        }
        if (requestCode == PATIENT_ID_LINK_REQUEST){
            if (resultCode == RESULT_OK && data != null && data.hasExtra("SELECTED_PATIENT_ID")){
                String selectedPatientID = data.getStringExtra("SELECTED_PATIENT_ID");
                if (selectedPatientID != null && !selectedPatientID.isEmpty()){
                    linkPageToPatient(selectedPatientID);
                }
            }
            return;
        }
    }

    private static final int CAMERA_REQUEST_CODE = 345689;
    private int attachRequestPageNumber = -1;

    private void attachCameraImage() {
        attachRequestPageNumber = currentPageNumber;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
    }

    private void handleCameraImageResult(int resultCode, Intent data) {
        if (resultCode != RESULT_OK){
            Toast.makeText(this, "Error while capturing image, please try again.", Toast.LENGTH_SHORT).show();
            attachRequestPageNumber = -1;
            return;
        }

        if (attachRequestPageNumber == -1){
            Toast.makeText(this, "Error while capturing image, page lost", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        Random randomGenerator = new Random();
        int random = randomGenerator.nextInt(9999);
        String newimagename= "CAM_IMG_" + String.valueOf(random);

        try {
            File file = File.createTempFile(newimagename, ".png", getCacheDir());
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            uploadAttachment(attachRequestPageNumber, file, newimagename, ".png", "image/png");
            attachRequestPageNumber = -1;
        } catch (IOException e) {
            Toast.makeText(this, "Error while Processing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }


    }

    private void uploadAttachment(int pageNum, File file, String fileNane, String ext, String mime) {
        setUploadProgress(0);
        FileMetadata metadata = new FileMetadata(ext, mime, "IMAGE", fileNane);
        metadata.description = "Clicked Live";
        APIMethods.uploadAttachment(this, file, pageNum, metadata, new FileTransferResponseListener<UploadVoiceRP>() {
            @Override
            public void success(UploadVoiceRP response) {
                successUpload();
            }

            @Override
            public void onProgress(int percent) {
                setUploadProgress(percent);
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                Toast.makeText(PrescriptionActivity.this, "Attachment upload failed: " + message, Toast.LENGTH_SHORT).show();
                binding.pbVoiceUpload.setVisibility(View.GONE);
            }
        });
    }

    private void linkMobileNumber(final long mobileNo) {
        if (currentPageNumber == -1) {
            Toast.makeText(PrescriptionActivity.this, "Please touch your page with pen", Toast.LENGTH_SHORT).show();
            return;
        }

        AddMobileNoReq req = new AddMobileNoReq();

        req.setPageNumber(currentPageNumber);
        req.setMobileNumber(mobileNo);

        Log.i(TAG, "req set mobile" + req.toString());
        final int pageNo =  currentPageNumber;

        APIMethods.addMobileNumber(PrescriptionActivity.this, req, new APIResponseListener<AddMobileNoRP>() {
            @Override
            public void success(AddMobileNoRP response) {

                tempMobileNumber = "";
                binding.tempMobileIndetProgressBar.setVisibility(View.GONE);
                binding.tempMobileNumberTxt.setText("");

                if (currentInitPageResponse.getPage().getPageNumber() == pageNo){
                    currentInitPageResponse.getPage().setMobileNumber(mobileNo);
                    showLinkedPatientInformation();
                }
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                showError(message, null);
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadDoctorConfigurations();

        handleGender();
        setListeners();
        intialiseControls();
    }


    private Handler patientDetailsblinkHandler = new Handler();
    private Runnable patientDetailsBlinkRunnable = new Runnable() {
        @Override
        public void run() {
            binding.patientDetailsSection.animate()
                    .alpha(binding.patientDetailsSection.getAlpha()==0?0.4f:0)
                    .setDuration(750)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
            patientDetailsblinkHandler.postDelayed(this, 900);
        }
    };

    private void hidePatientDetailsLayout() {
        patientDetailsblinkHandler.post(patientDetailsBlinkRunnable);
    }

    private void showPatientDetailsLayout(){
        patientDetailsblinkHandler.removeCallbacks(patientDetailsBlinkRunnable);
        binding.patientDetailsLayout.setVisibility(View.VISIBLE);
        binding.patientDetailsSection.animate()
                .alpha(1)
                .setDuration(300)
                .setInterpolator(new AnticipateInterpolator())
                .start();
    }

    private void setListeners() {
        //initialise page


        //MyListeners
        binding.backBtn.setOnClickListener(view -> onBackPressed());
        binding.linkPatientBtn.setOnClickListener(view->{
            if (currentPageNumber != -1 && currentInitPageResponse != null){
                Intent intent = new Intent(PrescriptionActivity.this, PatientHistoryActivity.class);
                intent.putExtra(Const.patientFilter, Const.patientFilterLinkPageAndPatient);
                if (currentInitPageResponse.getPage().getMobileNumber() != 0){
                    intent.putExtra("FILTER_PHONE", String.valueOf(currentInitPageResponse.getPage().getMobileNumber()));
                }
                PrescriptionActivity.this.startActivityForResult(intent, PATIENT_ID_LINK_REQUEST);
            }
        });
    }



    @Override
    protected void onPostResume() {
        super.onPostResume();
        offlineData();
    }





    //Todo: Preview Case before addition: Move to Patient Selector Activity
    private void showCaseDetailsDialog(String caseId) {

        Dialog viewCaseDialog = new Dialog(this);
        DialogViewCaseBinding dialogViewCaseBinding = DialogViewCaseBinding.inflate(getLayoutInflater());
        viewCaseDialog.setContentView(dialogViewCaseBinding.getRoot());
        Window window = viewCaseDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }
        viewCaseDialog.show();

        dialogViewCaseBinding.ivCloseDialog.setOnClickListener(view -> viewCaseDialog.dismiss());
        dialogViewCaseBinding.tvOpenCaseInDetail.setOnClickListener(view -> {
            Intent intent = new Intent(PrescriptionActivity.this, CaseDetailsActivity.class);
            intent.putExtra("CASE_ID",caseId);
            startActivity(intent);
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(PrescriptionActivity.this,2);
        APIMethods.viewCase(PrescriptionActivity.this, caseId, new APIResponseListener<ViewCaseRP>() {
            @Override
            public void success(ViewCaseRP response) {
                dialogViewCaseBinding.pbRelViewCase.setVisibility(View.GONE);
                dialogViewCaseBinding.llCaseData.setVisibility(View.VISIBLE);
                dialogViewCaseBinding.tvOpenCaseInDetail.setVisibility(View.VISIBLE);
                dialogViewCaseBinding.tvOpenCaseInDetail.setEnabled(true);
                dialogViewCaseBinding.tvRelCaseName.setText(response.getTitle());
                dialogViewCaseBinding.tvRelLastUpdated.setText(response.getUpdatedAt());
                dialogViewCaseBinding.rcvRelCasePages.setLayoutManager(gridLayoutManager);
                dialogViewCaseBinding.rcvRelCasePages.setAdapter(new PagesHistoryAdapter(response, PrescriptionActivity.this, new PagesHistoryAdapter.PageListener() {
                    @Override
                    public void onPageClicked(ArrayList<Page> pages, int currentposition) {
                        Log.i(TAG, "onPageClicked: rel case" + currentposition);
                    }
                }));
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                viewCaseDialog.dismiss();
                Toast.makeText(PrescriptionActivity.this, "Error encountered. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideAllLayouts(){
        hidePatientDetailsLayout();
        LoginSheet.hideSheet();
        if(recordVoiceDialog != null && recordVoiceDialog.isShowing()){
            Toast.makeText(this, "Voice Recording Cancelled due to page change!", Toast.LENGTH_SHORT).show();
            recordVoiceDialog.dismiss();
        }
    }



    private void linkPageToCase(String caseId, String relativeId, LinkedPatient selectedRelative) {
        LinkPageReq req = new LinkPageReq();
        req.setPageNumber(currentPageNumber);
        req.setPatientId(relativeId);
        req.setCaseId(caseId);
        final int pageNo =currentPageNumber;
        APIMethods.linkPage(PrescriptionActivity.this, req, new APIResponseListener<LinkPageRP>() {
            @Override
            public void success(LinkPageRP response) {

                Toast.makeText(PrescriptionActivity.this, "Page is linked to " + selectedRelative.getFullName(), Toast.LENGTH_SHORT).show();
//                TODO: binding.toolbar.setSubtitle("Page is linked to " + selectedRelative.getFullName());
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                showError(message, null);
            }
        });
    }

    private void linkPageToPatient(String patientId) {
        LinkPageReq req = new LinkPageReq();
        if (patientId == null) {
            return;
        }

        req.setPatientId(patientId);
        req.setPageNumber(currentPageNumber);
        final int pageNo = currentPageNumber;

        binding.pbPrescription.setVisibility(View.VISIBLE);
        APIMethods.linkPage(PrescriptionActivity.this, req, new APIResponseListener<LinkPageRP>() {
            @Override
            public void success(LinkPageRP response) {
                if (currentPageNumber == pageNo) {
                    binding.pbPrescription.setVisibility(View.GONE);
                    binding.pageStatusTxt.setText("Page is linked to " + response.getPatient().getFullName());
                    currentInitPageResponse.setPatient(response.getPatient());
                    showLinkedPatientInformation();
                }
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {

                showError(message, null);
                Log.i(TAG, "fail: link to page " + message);
            }
        });
    }



    private void handleGender() {
        //Todo: Implement Handling Gender
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void intialiseControls() {

        driver.setListener(this);
        driver.observeBuffer(this, buff->{
            ArrayList<DrawLiveDataBuffer.DrawAction> actions = DrawLiveDataBuffer.emptyBuffer();
            if (actions != null && actions.size() > 0){
                if (actions.size() == 1){
                    Log.i("lv-data", "single val");
                    handleSingleDraw(actions.get(0));
                } else {
                    //Todo: Optimize this
                    Log.i("lv-data", "mult val");
                    for (DrawLiveDataBuffer.DrawAction action: actions){
                        handleSingleDraw(action);
                    }
                }
            }
        });

        if (PenStatusLiveData.getPenStatusLiveData().getIsConnected().getValue() != null && PenStatusLiveData.getPenStatusLiveData().getIsConnected().getValue()) {
            showDrawView();
        } else {
            dialogPenBinding = DialogPenBinding.inflate(getLayoutInflater());
            dialog = new AlertDialog.Builder(this).setView(dialogPenBinding.getRoot()).create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            dialog.setCancelable(false);
            dialogPenBinding.hideBtn.setOnClickListener(view -> {
                dialog.dismiss();
                dialogPenBinding = null;
                onBackPressed();
            });
            dialogPenBinding.hideBtn.setText("Go Back");
            dialogPenBinding.hideBtn.setVisibility(View.VISIBLE);
            dialog.show();

            dialogPenBinding.progressBar.setVisibility(View.VISIBLE);
            dialogPenBinding.titleTxt.setVisibility(View.VISIBLE);
            dialogPenBinding.titleTxt.setText("Intialising Pen Driver");
            SmartPenDriver.CONNECT_MESSAGE message = driver.initialize();//driverStep3
            if (message == SmartPenDriver.CONNECT_MESSAGE.CONFIG_SUCCESS) searchPens();
            else if (message == SmartPenDriver.CONNECT_MESSAGE.REQUESTING_PERMS) {
                dialogPenBinding.bodyTxt.setText("Requesting Permissions");
            } else
                showError(String.valueOf(message), null);
        }
    }

    private void offlineData(){
        if (driver != null && driver.isOfflineDataAvailable()){
            //Todo: Show UI for offline Data
            new AlertDialog.Builder(PrescriptionActivity.this).setTitle("Offline data found. Want to transfer?")
                    .setPositiveButton("YES",(dialogInterface, i) -> driver.transferOfflineData())
                    .setNegativeButton("NO",(dialogInterface,i) -> dialogInterface.dismiss()).show();
            Log.i("pen-msg-log", "transferring offline data");
        }
    }

    private void searchPens() {
        //TODO remove it
//        dialog.dismiss();
        /////

        dialogPenBinding.progressBar.setVisibility(View.VISIBLE);
        dialogPenBinding.bodyTxt.setVisibility(View.VISIBLE);
        dialogPenBinding.titleTxt.setVisibility(View.VISIBLE);
        dialogPenBinding.imageView.setVisibility(View.GONE);
        dialogPenBinding.bodyTxt.setText("Pen Driver intialized successfully");
        dialogPenBinding.titleTxt.setText("Searching for pens");




        isPenSearchRunning = true;
        driver.getSmartPenList(new ConnectionsHandler.PenConnectionsListener() {
            @Override
            public void onSmartPens(ArrayList<SmartPen> smartPens) {
                if (isPenSearchRunning && dialogPenBinding != null) {
                    dialogPenBinding.progressBar.setVisibility(View.GONE);
                    if (smartPens.size() > 0)
                        dialogPenBinding.titleTxt.setText("Found all nearby pens");
                    else
                        showError("No Nearby pens found! Try refreshing bluetooth or turning on the power of smart pen", null);
                }
                isPenSearchRunning = false;
            }

            @Override
            public void onSmartPen(SmartPen smartPen) {

            }
        });
        SmartPenDriver.observeSmartPens(dialog, new Observer<ArrayList<SmartPen>>() {
            @Override
            public void onChanged(ArrayList<SmartPen> mPens) {
                Log.i("MedPA", "on Smart Pens changed: " + mPens.size());
                for (SmartPen smartPen: mPens){
                    if (smartPens == null) {
                        smartPens = new ArrayList<>();
                        dialogPenBinding.actionBtn.setOnClickListener(view -> {
                            isPenSearchRunning = false;
                            connectToSmartPen(selectedPen);
                        });
                    }
                    dialogPenBinding.radioSelector.setVisibility(View.VISIBLE);
                    dialogPenBinding.actionBtn.setVisibility(View.VISIBLE);
                    RadioButton button = new RadioButton(PrescriptionActivity.this);
                    button.setText(smartPen.getName());
                    button.setTag(String.valueOf(smartPens.size()));
                    dialogPenBinding.radioSelector.addView(button);
                    button.setChecked(true);
                    selectedPen = smartPen;
                    button.setOnCheckedChangeListener((compoundButton, b) -> {
                        if (b) {
                            selectedPen = smartPen;
                        }
                    });
                    smartPens.add(smartPen);
                }
            }
        });

    }

    private void connectToSmartPen(SmartPen selectedPen) {

        dialogPenBinding.progressBar.setVisibility(View.VISIBLE);
        dialogPenBinding.titleTxt.setVisibility(View.VISIBLE);
        dialogPenBinding.bodyTxt.setVisibility(View.VISIBLE);
        dialogPenBinding.imageView.setVisibility(View.GONE);
        dialogPenBinding.actionBtn.setVisibility(View.GONE);
        dialogPenBinding.radioSelector.setVisibility(View.GONE);

        dialogPenBinding.titleTxt.setText("Connecting to selected pen");
        dialogPenBinding.bodyTxt.setText("Please wait");

        driver.connectToPen(selectedPen);


    }

    @Override
    public void onPermissionsDenied() {

    }

    @Override
    public void onPermissionsResult(boolean granted) {
        Log.i("Connections", "Perm Result:" + String.valueOf(granted));
        if (granted) {
            searchPens();
        } else {
            showError("Permissions Denied", null);
        }
    }

    @Override
    public void error(String message) {
        Log.i("MedPA", "onError()");
        showError(message, null);
    }

    @Override
    public void onConnection(boolean establised) {

        if (establised) {
            dialog.dismiss();
            dialogPenBinding = null;
            Toast.makeText(this, "Connected pen successfully", Toast.LENGTH_SHORT).show();

            showDrawView();
            offlineData();

        } else showError("Connection failed", null);


    }

    private void showDrawView() {
        binding.canvasView.setVisibility(View.VISIBLE);
        viewTreeObserver = binding.canvasView.getViewTreeObserver();


    }

    @Override
    public void disconnected() {
        showError("Pen Disconnected", null);
    }

    @Override
    public void message(String s, String message) {
        showMessage(s, message);
    }

    private void handleSingleDraw(DrawLiveDataBuffer.DrawAction drawAction){
        float x = drawAction.x;
        float y = drawAction.y;
        int pageId = drawAction.pageId;
        int actionType = drawAction.actionType;

        if (pageId != currentPageNumber) {
            startPageChangeProcedure(pageId);
        }
        binding.canvasView.addCoordinate(x, y, actionType);
        pendingPoints.add(new Point(x, y, actionType));
    }

    private void startPageChangeProcedure(int pageId) {
        uploadPoints();
        binding.tempMobileLayout.setVisibility(View.GONE);
        binding.tempMobileDetProgress.setVisibility(View.GONE);
        binding.tempMobileIndetProgressBar.setVisibility(View.GONE);
        binding.tempMobileNumberTxt.setText("");
        binding.patientDetailsLayout.setVisibility(View.VISIBLE);
        tempMobileNumber = "";
        tempMobileCountDownHandler.removeCallbacks(tempMobileCountDownRunnable);

        //UploadPointsHandlerRemoval
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
            runnable = null;
        }
        binding.canvasView.clearDrawing(pageId);
        currentPageNumber = pageId;
        binding.pageNumberTxt.setText("Page " + currentPageNumber);
        binding.pageStatusTxt.setText("Initialising Page");
        showPB();
        final int pageNo = currentPageNumber;
        hideAllLayouts();
        currentInitPageResponse = null;
        APIMethods.initialisePage(this, currentPageNumber, new APIResponseListener<InitialisePageRP>() {
            @Override
            public void success(InitialisePageRP response) {
                currentInitPageResponse = response;
                currentPage = response.getPage();
                showLinkedPatientInformation();


                if (response.getPage() != null
                        && response.getPage().getPoints() != null
                        && response.getPage().getPoints().size() > 0)
                    binding.canvasView.addCoordinates(response.getPage().getPoints());

                setTimelyUploads();

                binding.pageStatusTxt.setText("Page Initialized");
                hidePB();
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                hidePB();
                Methods.showError(PrescriptionActivity.this, message, true);
            }
        });
    }

    private void showLinkedPatientInformation() {
        if (currentInitPageResponse == null){
            return;
        }

        showPatientDetailsLayout();
        binding.tempMobileLayout.setVisibility(View.GONE);
        binding.linkPatientBtn.setVisibility(
                currentInitPageResponse.getPatient() == null?View.VISIBLE: View.GONE
        );

        String nameTxt = currentInitPageResponse.getPage().getFullName();
        if (currentInitPageResponse.getPatient() != null){
            nameTxt = currentInitPageResponse.getPatient().getFullName();
        }
        binding.patientNameTxt.setText(nameTxt == null || nameTxt.isEmpty() ? "-" : nameTxt);

        Long mobNum = currentInitPageResponse.getPage().getMobileNumber();
        if (currentInitPageResponse.getPatient() != null){
            mobNum = currentInitPageResponse.getPatient().getMobileNumber();
        }
        String mobNumTxt = (mobNum == null || mobNum == 0)?"-":String.valueOf(mobNum);
        binding.patientMobileNumberTxt.setText(mobNumTxt);

        String age = "";
        if (currentInitPageResponse.getPage().getAge() != null)
            age = String.valueOf(currentInitPageResponse.getPage().getAge());
        if (currentInitPageResponse.getPatient() != null){
            age = currentInitPageResponse.getPatient().getAgeText();
        }
        binding.patientAgeTxt.setText(age);
        String gender = currentInitPageResponse.getPage().getGender();
        if (currentInitPageResponse.getPatient() != null){
            gender = currentInitPageResponse.getPatient().getGender();
        }
        if (gender != null && !gender.isEmpty()) {
            gender = "(" + gender + ")";
            binding.patientAgeTxt.setText(age + gender);
        }
    }

    @Override
    public boolean onPaperButtonPress(int id, String name) {

        if (id >= 0 && id <= 10) {
            showLoginSheet(id);
            return true;
        } else if (id == 21 || id == 22 || id == 23) {
            switch (id) {
                case 21: {
                    showRecordVoiceSheet();
                    startRecording();
                    break;
                }
                case 22: {
                    stopRecording();
                    break;
                }
                case 23: {
                    submitRecording();
                    break;
                }
            }
        } else if (id == 51 || id == 52) {

            //todo was saving gender when submit is clicked so is it required here?
            if (id == 51) {
                gender = "M";
            } else {
                gender = "F";
            }
            AddDetailsReq req = new AddDetailsReq();
            req.setGender(gender);
            req.setPageNumber(currentPageNumber);
            binding.pageStatusTxt.setText("Saving Patient Details");
            binding.pbPrescription.setVisibility(View.VISIBLE);

            APIMethods.addDetails(PrescriptionActivity.this, req, new APIResponseListener<AddDetailsRP>() {
                @Override
                public void success(AddDetailsRP response) {
                    binding.pageStatusTxt.setText("Saved Patient Details");
                    if(gender.equals("M")){
                        Toast.makeText(PrescriptionActivity.this, "Patient gender is set as Male", Toast.LENGTH_LONG).show();

                    } else if(gender.equals("F")){
                        Toast.makeText(PrescriptionActivity.this, "Patient gender is set as Female", Toast.LENGTH_LONG).show();

                    }
                    binding.pbPrescription.setVisibility(View.GONE);
                }

                @Override
                public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                    showError(message, null);
                    binding.pbPrescription.setVisibility(View.GONE);
                }
            });

        } else if(id == 31 || id == 32 || id == 33 || id ==34 || id == 38 || id == 39){
            //31 -> play 1,   32 -> share 1,  33 -> play 2, 34 -> share 2

            switch (id) {
                case 31: {
                    playGuideVideo(guidesList.get(0));
                    break;
                }
                case 32: {
                    hideGuideVideo();
                    linkGuideToPatient(guidesList.get(0));
                    break;
                }
                case 33: {
                    playGuideVideo(guidesList.get(0));
                    Log.i(TAG, "onPaperButtonPress: 33");
                    break;
                }
                case 34: {
                    hideGuideVideo();
                    linkGuideToPatient(guidesList.get(1));
                    break;
                }
                case 38:{
                    showOtherGuidesBS();
                    break;
                }
                case 39:{
                    if(selectedOtherGuide == null){
                        Toast.makeText(this, "Select a guide first.", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    hideGuideVideo();
                    linkGuideToPatient(selectedOtherGuide);
                    break;
                }
                default:{
                    Log.i(TAG, "onPaperButtonPress: thala default");
                    break;
                }
            }


        }
        else if (id == 100) {
            if (currentPage == null) {
                Toast.makeText(this, "Please initialise the page before submitting", Toast.LENGTH_SHORT).show();
            } else {
                submitCase(currentPage.getCaseId());
            }
        }
        else if (id <= 69 && id >= 61){
            switch (id){
                case 61:
                    attachCameraImage();
                    break;
            }
        }

        return true;
    }

    String linkMasterPageCaseId;
    Page masterPage;
    int linkMasterPageNumber;
    @Override
    public void startLinkingProcedure(int page) {
        Log.i("ETA - START", "" + page);
        if(currentPage != null){
            masterPage =  currentPage;
            linkMasterPageCaseId = currentPage.getCaseId();
            linkMasterPageNumber = page;
            binding.connectPagesLayout.setVisibility(View.VISIBLE);
        } else {
            //TODO: Add waiting here (5)
            Log.i("ETA - Start", "Current page is null");
        }

    }

    @Override
    public void stopLinking(int masterPage, int currentPage) {
        Log.i("ETA - stop", currentPage + " to " + masterPage);
        linkMasterPageNumber = -1;
        linkMasterPageCaseId = null;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.connectPagesLayout.setVisibility(View.GONE);
            }
        },10000);
    }

    @Override
    public boolean linkPages(int masterPage, int slavePage) {
        Log.i("ETA - LINK", slavePage + " to " + masterPage);
        if(masterPage == linkMasterPageNumber) {
            binding.connectPagesLayout.setVisibility(View.VISIBLE);
            binding.ivConnectPages.setVisibility(View.GONE);
            binding.linkAnimation.setVisibility(View.VISIBLE);

            LinkPageReq req = new LinkPageReq();
            req.setCaseId(linkMasterPageCaseId);
            req.setPageNumber(slavePage);
            req.setPatientId(currentPage.getPatientID());
            APIMethods.linkPage(PrescriptionActivity.this, req, new APIResponseListener<LinkPageRP>() {
                @Override
                public void success(LinkPageRP response) {
                    Toast.makeText(PrescriptionActivity.this, "Page linked successfully", Toast.LENGTH_SHORT).show();
                    binding.linkAnimation.setVisibility(View.GONE);
                    binding.ivConnectPages.setImageDrawable(AppCompatResources.getDrawable(
                            PrescriptionActivity.this,
                            R.drawable.ic_done_bg));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.ivConnectPages.setImageDrawable(AppCompatResources.getDrawable(PrescriptionActivity.this,R.drawable.connection));
                            binding.connectPagesLayout.setVisibility(View.GONE);
                        }
                    },5000);

                }

                @Override
                public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                    Methods.showError(PrescriptionActivity.this,message,false);
//                    showError(message, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//
//                        }
//                    });
                    Toast.makeText(PrescriptionActivity.this, "Page linking failed", Toast.LENGTH_SHORT).show();
                    binding.ivConnectPages.setImageDrawable(AppCompatResources.getDrawable(PrescriptionActivity.this,R.drawable.connection));
                    binding.linkAnimation.setVisibility(View.GONE);
                    binding.connectPagesLayout.setVisibility(View.GONE);
                }
            });
            return true;
        };
        return false;
    }

    boolean isAddingPhoneNumberToPagePossible(){
        if (currentPageNumber == -1){
            Toast.makeText(this, "Touch the paper with pen to initialize page", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (currentInitPageResponse == null){
            Toast.makeText(this, "Please Wait till page is initialized", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (currentInitPageResponse.getPatient() != null){
            Toast.makeText(this, "Page is already linked with a patient. Adding Phone number is not possible right now!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (currentInitPageResponse.getPage().getMobileNumber() != 0){
            Toast.makeText(this, "This page already has phone number attached. Adding Phone number is not possible right now!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    String tempMobileNumber = "";
    Handler tempMobileCountDownHandler = new Handler();
    int i = 0;
    int inc = 50*100/5000;
    Runnable tempMobileCountDownRunnable = new Runnable() {
        @Override
        public void run() {
            i += inc;
            binding.tempMobileDetProgress.setProgress(i);
            if (i >= 100){
                binding.tempMobileIndetProgressBar.setVisibility(View.VISIBLE);
                binding.tempMobileDetProgress.setVisibility(View.GONE);
                binding.tempMobileCancelBtn.setVisibility(View.GONE);
                linkMobileNumber(Long.parseLong(tempMobileNumber));
                tempMobileNumber = "";
            } else {
                tempMobileCountDownHandler.postDelayed(this, 50);
            }
        }
    };
    private void showLoginSheet(int id) {
       if (!isAddingPhoneNumberToPagePossible()){
           return;
       }

       binding.tempMobileLayout.setVisibility(View.VISIBLE);
       binding.patientDetailsLayout.setVisibility(View.GONE);


       if (id == 10){
           if (tempMobileNumber.length() == 0) {
               return;
           };
           if (tempMobileNumber.length() == 10){
               tempMobileCountDownHandler.removeCallbacks(tempMobileCountDownRunnable);
               binding.tempMobileCancelBtn.setVisibility(View.GONE);
               binding.tempMobileDetProgress.setVisibility(View.GONE);
               binding.tempMobileIndetProgressBar.setVisibility(View.GONE);
           }
           tempMobileNumber = tempMobileNumber.substring(0, tempMobileNumber.length()-1);
           binding.tempMobileNumberTxt.setText(tempMobileNumber);
           return;
       }


        if (tempMobileNumber.length() >= 10){
            Toast.makeText(this, "Saving Mobile Number please wait!", Toast.LENGTH_SHORT).show();
            return;
        }
       tempMobileNumber += String.valueOf(id);
       binding.tempMobileNumberTxt.setText(tempMobileNumber);
        binding.tempMobileDetProgress.setVisibility(View.GONE);
        binding.tempMobileIndetProgressBar.setVisibility(View.GONE);
        binding.tempMobileCancelBtn.setVisibility(View.GONE);
       if (tempMobileNumber.length() == 10){
           binding.tempMobileCancelBtn.setVisibility(View.VISIBLE);
           binding.tempMobileIndetProgressBar.setVisibility(View.GONE);
           binding.tempMobileDetProgress.setVisibility(View.VISIBLE);
           i = 0;
           tempMobileCountDownHandler.postDelayed(tempMobileCountDownRunnable, 50);
           binding.tempMobileCancelBtn.setOnClickListener(view->{
               tempMobileCountDownHandler.removeCallbacks(tempMobileCountDownRunnable);
               binding.tempMobileCancelBtn.setVisibility(View.GONE);
               binding.tempMobileIndetProgressBar.setVisibility(View.GONE);
               binding.tempMobileDetProgress.setVisibility(View.GONE);
           });
       }
    }

    private void setPageStatus(String statusTxt){
        if (statusTxt.length() > 36)
            statusTxt = statusTxt.substring(0, 36) + "...";
        binding.pageStatusTxt.setText(statusTxt);
    }



    public void linkGuideToPatient(Guide guide){
        if(otherGuidesBSDialog != null){
            otherGuidesBSDialog.dismiss();
        }

        setPageStatus("Linking guide: "+ guide.getName());
        binding.pbPrescription.setVisibility(View.VISIBLE);
        LinkGuideReq req = new LinkGuideReq(guide.get_id(),currentPageNumber);
        APIMethods.linkAdditionalGuide(PrescriptionActivity.this, req, new APIResponseListener<LinkGuideRP>() {
            @Override
            public void success(LinkGuideRP response) {
                setPageStatus("Added guide: "+ guide.getName() + " to the patient");
                binding.pbPrescription.setVisibility(View.GONE);
                Toast.makeText(PrescriptionActivity.this, guide.getName() + "is attached to the patient.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                setPageStatus("Some error occurred while linking guide.");
                Log.e(TAG, "fail: linking guide "+message);
                binding.pbPrescription.setVisibility(View.GONE);
                showError(message,null);
            }
        });

    }


    private ConfigurePageRP pageConfigurations;
    private ArrayList<Guide> guidesList;
    private ArrayList<Guide> otherGuidesList;
    private void loadDoctorConfigurations() {
        setPageStatus("Loading configurations");
        binding.pbPrescription.setVisibility(View.VISIBLE);

        APIMethods.configurePage(PrescriptionActivity.this, new EmptyReq(), new APIResponseListener<ConfigurePageRP>() {
            @Override
            public void success(ConfigurePageRP response) {
                setPageStatus("Configured page");
                binding.pbPrescription.setVisibility(View.GONE);


                pageConfigurations = response;
                guidesList = response.getGuides();
                Log.i(TAG, "success: guides list size "+ guidesList.size());
                otherGuidesList = new ArrayList<>();
                if(guidesList.size() > 2){
                    otherGuidesList =  new ArrayList<>();
                    for(int i=2; i<guidesList.size(); i++){
                        Log.i(TAG, "success: adding to other guide "+ guidesList.get(i));
                        otherGuidesList.add(guidesList.get(i));
                    }
                }
                Log.i(TAG, "success: other guides list size "+ otherGuidesList.size());

                binding.canvasView.setBackgroundImageUrl(pageConfigurations.getPageDetails().getPageBackground(),
                        pageConfigurations.getPageDetails().getPageWidth(),pageConfigurations.getPageDetails().getPageHeight());


//                binding.canvasView.getPrescriptionBMP()


//                SharedPreferences preferences = getSharedPreferences("PAGE_CONFIGURATION_PREFS", MODE_PRIVATE);
//                SharedPreferences.Editor editor = preferences.edit();
//
//                editor.putString("PAGE_CONFIGS",new Gson().toJson(response));
//
//                editor.commit();

            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
//                Methods.showError(PrescriptionActivity.this,message,cancellable);
                showError(message, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }
        });
    }


    BottomSheetDialog  otherGuidesBSDialog;
     RecyclerView rcvOtherGuides;

     private Guide selectedOtherGuide;

    private void showOtherGuidesBS(){
        Log.i(TAG, "showOtherGuidesBS: guides size "+guidesList.size());
        if(guidesList == null || guidesList.size() <= 2 ){
            Toast.makeText(this, "No other guides available. Add them from settings.", Toast.LENGTH_LONG).show();
            return;
        }

        if(otherGuidesList != null){
            otherGuidesBSDialog = new BottomSheetDialog(PrescriptionActivity.this);
            otherGuidesBSDialog.setContentView(R.layout.bsheet_other_guides);
            rcvOtherGuides = otherGuidesBSDialog.findViewById(R.id.rvOtherGuides);
            rcvOtherGuides.setLayoutManager(new LinearLayoutManager(PrescriptionActivity.this));
            rcvOtherGuides.setAdapter(new OtherGuidesAdapterBS(otherGuidesList, PrescriptionActivity.this, new OtherGuidesAdapterBS.GuideLinkListener() {
                @Override
                public void onLinkGuideClicked(Guide guide) {
//                    linkGuideToPatient(guide);
                    selectedOtherGuide = guide;
                    Log.i(TAG, "onLinkGuideClicked: selectedGuide is "+selectedOtherGuide.getName());
                    playGuideVideo(guide);
                }
            }));

            otherGuidesBSDialog.show();
        } else {
            Toast.makeText(this, "No other guides", Toast.LENGTH_SHORT).show();
        }



    }

    private  YTVideoPlayer ytVideoPlayer;
    private  WebVideoPlayer webVideoPlayer;
    private void playGuideVideo(Guide guide) {
        Toast.makeText(PrescriptionActivity.this, "Loading "+guide.getName()+"'s video. Please wait...", Toast.LENGTH_SHORT).show();
        if(guide.getMime().equals("link/youtube")){
            ytVideoPlayer = new YTVideoPlayer(PrescriptionActivity.this);
            Log.i(TAG, "onLinkGuideClicked: url "+ guide.getUrl());
            ytVideoPlayer.playVideo(guide.getUrl());
        } else {
            webVideoPlayer = new WebVideoPlayer(PrescriptionActivity.this);
            webVideoPlayer.playVideo(guide.getUrl());
        }
    }
    private void hideGuideVideo(){
        if(ytVideoPlayer != null){
            ytVideoPlayer.dismissPopup();
        }
        if(webVideoPlayer != null){
            webVideoPlayer.dismissPopup();
        }
    }


    private void setTimelyUploads() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                uploadPoints();
                handler.postDelayed(runnable, 15000);
            }
        };


        handler.postDelayed(runnable, 15000);
    }

    private void uploadPoints() {
        if (pendingPoints.size() > 0) {
            ArrayList<Point> uploadPoints = new ArrayList<>();
            uploadPoints.addAll(pendingPoints);

            pendingPoints = new ArrayList<>();

            //call upload points
            setPageStatus("Synchronising page");

            APIMethods.uploadPoints(PrescriptionActivity.this, currentPageNumber, uploadPoints, new APIResponseListener<EmptyRP>() {
                @Override
                public void success(EmptyRP response) {
                    setPageStatus("Page synchronised");
                }

                @Override
                public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                    if (active)
                        Methods.showError(PrescriptionActivity.this, message, true);
                    Log.i(TAG, "fail: timely uploads");

                }
            });
        }
    }


    static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    private void showError(String message, View.OnClickListener listener) {

        if (dialogPenBinding == null) {
            dialogPenBinding = DialogPenBinding.inflate(getLayoutInflater());
            dialog = new AlertDialog.Builder(this).setView(dialogPenBinding.getRoot()).create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


        dialogPenBinding.imageView.setVisibility(View.VISIBLE);
        dialogPenBinding.titleTxt.setVisibility(View.VISIBLE);
        dialogPenBinding.bodyTxt.setVisibility(View.VISIBLE);
        dialogPenBinding.actionBtn.setVisibility(View.VISIBLE);
        dialogPenBinding.progressBar.setVisibility(View.GONE);
        dialogPenBinding.radioSelector.setVisibility(View.GONE);

        String actionTxt;
        if (listener != null) {
            message += "\n\nPress button to Retry";
            actionTxt = "Retry";
            dialogPenBinding.actionBtn.setOnClickListener(listener);
        } else {
            message += "\n\nClick button to close screen";
            actionTxt = "End process";
            dialogPenBinding.actionBtn.setOnClickListener(view -> {
//                PrescriptionActivity.this.finish()
                dialog.dismiss();
            });
        }

        dialogPenBinding.titleTxt.setText("Error Occurred");
        dialogPenBinding.bodyTxt.setText(message);
        dialogPenBinding.imageView.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_error_bg));
        dialogPenBinding.actionBtn.setText(actionTxt);
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

    }

    private void showMessage(String ttl, String msg) {

        if (dialogPenBinding == null) {
            dialogPenBinding = DialogPenBinding.inflate(getLayoutInflater());
            dialog = new AlertDialog.Builder(this).setView(dialogPenBinding.getRoot()).create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


        dialogPenBinding.imageView.setVisibility(View.VISIBLE);
        dialogPenBinding.titleTxt.setVisibility(View.VISIBLE);
        dialogPenBinding.bodyTxt.setVisibility(View.VISIBLE);
        dialogPenBinding.actionBtn.setVisibility(View.VISIBLE);
        dialogPenBinding.progressBar.setVisibility(View.GONE);
        dialogPenBinding.radioSelector.setVisibility(View.GONE);

        dialogPenBinding.titleTxt.setText(ttl);
        dialogPenBinding.bodyTxt.setText(msg);
        dialogPenBinding.actionBtn.setText("Okay");
        dialogPenBinding.imageView.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_error_bg));
        dialogPenBinding.actionBtn.setOnClickListener(view -> dialog.dismiss());
    }

    private void showPB() {
        binding.pbPrescription.setVisibility(View.VISIBLE);
    }

    private void hidePB() {
        binding.pbPrescription.setVisibility(View.GONE);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private BottomSheetDialog mobileBSDialog;
    private BsheetAddMobileNoBinding bsheetAddMobileNoBinding;

    private void showMobileBottomSheet() {



        mobileBSDialog.show();
    }
    
    

    private void submitCase(String caseId) {
        
        APIMethods.submitCase(PrescriptionActivity.this, caseId, new APIResponseListener<SubmitCaseRP>() {
            @Override
            public void success(SubmitCaseRP response) {
                Toast.makeText(PrescriptionActivity.this, "Case Submitted and would be shared with patient", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                showError(message, null);
            }
        });
    }
    BottomSheetDialog recordVoiceDialog;

    private void showRecordVoiceSheet() {
        recordVoiceDialog = new BottomSheetDialog(this);
        recordVoiceDialog.setContentView(R.layout.bsheet_attach_voice);

        bsAVActionText = recordVoiceDialog.findViewById(R.id.bsAVActionText);
        btnBSAVAttach = recordVoiceDialog.findViewById(R.id.btnBSAVAttach);
        btnBSAVStart = recordVoiceDialog.findViewById(R.id.btnBSAVStart);
        btnBSAVStop = recordVoiceDialog.findViewById(R.id.btnBSAVStop);
        voiceAnimation = recordVoiceDialog.findViewById(R.id.voiceAnimation);
        ivDeleteAudio = recordVoiceDialog.findViewById(R.id.ivDeleteAudio);


        btnBSAVStart.setOnClickListener(view -> {
            startRecording();
        });

        btnBSAVStop.setOnClickListener(view -> {
            if (outputFile == null) {
                bsAVActionText.setText("Please press start to record a voice");
                bsAVActionText.setTextColor(getColor(R.color.colorDanger));
            } else {
                stopRecording();
            }


        });
        btnBSAVAttach.setOnClickListener(view -> {
            if (outputFile != null) {
                submitRecording();
            } else {
                bsAVActionText.setText("Please record a voice before submitting");
                bsAVActionText.setTextColor(getColor(R.color.colorDanger));
            }
        });
        recordVoiceDialog.show();


    }

    private void startRecording() {
        if (currentPage != null) {
            releaseMediaRecorder();
            Log.i(TAG, "startRecording: " + currentPage.get_id());

            //todo:  make path dynamic
            outputFile = getExternalCacheDir().getAbsolutePath() + "/recordingTest.mp3";
            bsAVActionText.setVisibility(View.VISIBLE);
            bsAVActionText.setTextColor(getColor(R.color.colorCta));
            bsAVActionText.setText("Recording...");
            Log.i(TAG, "recording ");
            voiceAnimation.setVisibility(View.VISIBLE);


            if (mediaRecorder == null) {
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                mediaRecorder.setOutputFile(outputFile);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);


                try {
                    mediaRecorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Some error occurred while preparing voice recorder", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "startRecording: not prepared " + e.getLocalizedMessage());
                }
                mediaRecorder.start();


            }
        } else {
            Toast.makeText(this, "Please initialize the page first", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;

        }
        voiceAnimation.setVisibility(View.GONE);
        bsAVActionText.setText(outputFile);
        ivDeleteAudio.setVisibility(View.VISIBLE);

        ivDeleteAudio.setOnClickListener(view -> {
            deleteRecording();
        });
    }

    private void deleteRecording() {
        stopRecording();
        File recordingFile = new File(outputFile);
        if (recordingFile.exists()) {
            if (recordingFile.delete()) {
                Toast.makeText(this, "Recording deleted", Toast.LENGTH_SHORT).show();
                outputFile = null;
                bsAVActionText.setVisibility(View.GONE);
                ivDeleteAudio.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Failed to delete recording", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Recording file not found", Toast.LENGTH_SHORT).show();
        }
    }


    Handler voiceHandler = new Handler();
    Runnable voiceRunnable = new Runnable() {
        @Override
        public void run() {
            binding.voiceUploadLayout.setVisibility(View.GONE);
            binding.pbVoiceUpload.setProgress(0);
        }
    };

    private void setUploadProgress(int progress){
        binding.voiceUploadLayout.setVisibility(View.VISIBLE);
        binding.pbVoiceUpload.setProgress(progress);
    }

    private void successUpload() {
        binding.pbVoiceUpload.setVisibility(View.INVISIBLE);
        binding.ivVoiceUpload.setImageDrawable(AppCompatResources.getDrawable(PrescriptionActivity.this,R.drawable.ic_done_bg));
        Toast.makeText(PrescriptionActivity.this, "Attached to Page!", Toast.LENGTH_SHORT).show();
        voiceHandler.postDelayed(voiceRunnable,7000);
    }

    private void submitRecording() {
        if(outputFile == null){
            return;
        }
        recordVoiceDialog.dismiss();

        Log.i(TAG, "submitRecording: output file is "+ outputFile);

        File file = new File(outputFile);
        binding.voiceUploadLayout.setVisibility(View.VISIBLE);
        binding.pbVoiceUpload.setProgress(0);
        APIMethods.uploadVoice(this, file, currentPageNumber, new FileTransferResponseListener<UploadVoiceRP>() {
            @Override
            public void success(UploadVoiceRP response) {
                binding.pbVoiceUpload.setVisibility(View.INVISIBLE);
                binding.ivVoiceUpload.setImageDrawable(AppCompatResources.getDrawable(PrescriptionActivity.this,R.drawable.ic_done_bg));
                Toast.makeText(PrescriptionActivity.this, "Voice uploaded successfully", Toast.LENGTH_SHORT).show();
                voiceHandler.postDelayed(voiceRunnable,7000);
            }

            @Override
            public void onProgress(int percent) {
                binding.pbVoiceUpload.setProgress(percent);
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                binding.voiceUploadLayout.setVisibility(View.GONE);
                Toast.makeText(PrescriptionActivity.this, "Failed uploading voice. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    @Override
    public void onBackPressed() {
        if(pendingPoints.size() != 0){
            uploadPoints();
        }
        super.onBackPressed();
    }
}
