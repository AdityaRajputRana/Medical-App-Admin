package com.example.medicalappadmin;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.Models.Point;
import com.example.medicalappadmin.PenDriver.ConnectionsHandler;
import com.example.medicalappadmin.PenDriver.LiveData.PenStatusLiveData;
import com.example.medicalappadmin.PenDriver.Models.SmartPen;
import com.example.medicalappadmin.PenDriver.SmartPenDriver;
import com.example.medicalappadmin.PenDriver.SmartPenListener;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.databinding.ActivityPrescriptionBinding;
import com.example.medicalappadmin.databinding.DialogPenBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.requests.AddDetailsReq;
import com.example.medicalappadmin.rest.response.InitialisePageRP;
import com.example.medicalappadmin.rest.response.EmptyRP;

import java.util.ArrayList;

public class PrescriptionActivity extends AppCompatActivity implements SmartPenListener {

    boolean isPenSearchRunning = false;

    ActivityPrescriptionBinding binding;

    DialogPenBinding dialogPenBinding;

    AlertDialog dialog;
    int state = 0;
    SmartPen selectedPen;
    ArrayList<SmartPen> smartPens;

    ArrayList<Point> pointsArrayList;
    ViewTreeObserver viewTreeObserver;
    int currentPageNumber = -1;

    ArrayList<Point> pendingPoints = new ArrayList<>();
    Page currentPage;

    ActionBarDrawerToggle actionBarDrawerToggle;
    EditText etFullName;
    EditText etMobileNumber;
    EditText etEmail;
    TextView tvMale;
    TextView tvFemale;

    String gender = "M";



    SmartPenDriver driver = SmartPenDriver.getInstance(this); //driverStep1




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,R.string.drawer_open,R.string.drawer_close);
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        etFullName = binding.navView.getHeaderView(0).findViewById(R.id.etFullName);
        etEmail = binding.navView.getHeaderView(0).findViewById(R.id.etEmail);
        etMobileNumber = binding.navView.getHeaderView(0).findViewById(R.id.etMobile);
        tvMale = binding.navView.getHeaderView(0).findViewById(R.id.tvMale);
        tvFemale = binding.navView.getHeaderView(0).findViewById(R.id.tvFemale);

        handleGender();

        binding.navView.getHeaderView(0).findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Adi", "onClick: btn save clicked ");

                if(currentPageNumber == -1){
                    Toast.makeText(PrescriptionActivity.this, "Please touch your page with pen", Toast.LENGTH_SHORT).show();
                    binding.drawerLayout.close();
                    return;
                }
                AddDetailsReq req = new AddDetailsReq();
                req.setPageNumber(currentPageNumber);
                if(etFullName.getText() != null && !etFullName.getText().toString().isEmpty()){
                    req.setFullName(etFullName.getText().toString());
                }
                if(etEmail.getText() != null && !etEmail.getText().toString().isEmpty()){
                    req.setEmail(etEmail.getText().toString());
                }
                if(etMobileNumber.getText() != null && !etMobileNumber.getText().toString().isEmpty()){
                    req.setMobileNumber(Long.valueOf(etMobileNumber.getText().toString()));
                }

                req.setGender(gender);

                Log.i("Adi", "req" + req.toString());

                binding.drawerLayout.close();
                showPB();
                binding.toolbar.setSubtitle("Saving details");
                APIMethods.addDetails(PrescriptionActivity.this, req, new APIResponseListener<EmptyRP>() {
                            @Override
                            public void success(EmptyRP response) {
                                hidePB();
                                binding.toolbar.setSubtitle("Details Saved");
                                Toast.makeText(PrescriptionActivity.this, "Saved details successfully", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                                showError(message,null);
                            }
                        }

                );
            }
        });

//        binding.btnOpenDrawer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                binding.drawerLayout.openDrawer(GravityCompat.END);
//            }
//        });
        intialiseControls();

    }

    private void handleGender() {
        tvMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = "M";
                tvMale.setBackgroundColor(getColor(R.color.blue_bg));
                tvFemale.setBackgroundColor(getColor(R.color.white));
                tvMale.setTextColor(getColor(R.color.white));
                tvFemale.setTextColor(getColor(R.color.black));
            }
        });
        tvFemale.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                gender = "F";
                tvFemale.setBackgroundColor(getColor(R.color.blue_bg));
                tvMale.setBackgroundColor(getColor(R.color.white));
                tvFemale.setTextColor(getColor(R.color.white));
                tvMale.setTextColor(getColor(R.color.black));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void intialiseControls() {

        driver.setListener(this);

        if (PenStatusLiveData.getPenStatusLiveData().getIsConnected().getValue() != null
            && PenStatusLiveData.getPenStatusLiveData().getIsConnected().getValue()){
            showDrawView();
        } else {
            dialogPenBinding = DialogPenBinding.inflate(getLayoutInflater());
            dialog = new AlertDialog.Builder(this)
                    .setView(dialogPenBinding.getRoot())
                    .create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.show();

            dialogPenBinding.progressBar.setVisibility(View.VISIBLE);
            dialogPenBinding.titleTxt.setVisibility(View.VISIBLE);
            dialogPenBinding.titleTxt.setText("Intialising Pen Driver");
            SmartPenDriver.CONNECT_MESSAGE message = driver.initialize();//driverStep3
            if (message == SmartPenDriver.CONNECT_MESSAGE.CONFIG_SUCCESS)
                searchPens();
            else if (message == SmartPenDriver.CONNECT_MESSAGE.REQUESTING_PERMS) {
                dialogPenBinding.bodyTxt.setText("Requesting Permissions");
            } else
                showError(String.valueOf(message), null);
        }
    }

    private void searchPens() {

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
                if (isPenSearchRunning && dialogPenBinding != null){
                    dialogPenBinding.progressBar.setVisibility(View.GONE);
                    if (smartPens.size() > 0 )
                        dialogPenBinding.titleTxt.setText("Found all nearby pens");
                    else
                        showError("No Nearby pens found! Try refreshing bluetooth or turning on the power of smart pen", null);
                }
                isPenSearchRunning =false;
            }

            @Override
            public void onSmartPen(SmartPen smartPen) {
                if (smartPens == null) {
                    smartPens = new ArrayList<>();
                    dialogPenBinding.actionBtn.setOnClickListener(view->{
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
                    if (b){
                        selectedPen = smartPen;
                    }
                });
                smartPens.add(smartPen);
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
        if(granted){
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

        if(establised){
            dialog.dismiss();
            dialogPenBinding = null;
            Toast.makeText(this, "Connected pen successfully", Toast.LENGTH_SHORT).show();

            showDrawView();


        } else
            showError("Connection failed", null);


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
        showMessage(s,message);
    }

    @Override
    public void drawEvent(float x, float y, int pageId, int actionType) {
        if(pageId != currentPageNumber){
            uploadPoints();
            if (handler != null){
                handler.removeCallbacks(runnable);
                handler = null;
                runnable = null;
            }
            binding.canvasView.clearDrawing();
            currentPageNumber = pageId;
            binding.toolbar.setTitle("Page : " + currentPageNumber);
            binding.toolbar.setSubtitle("Initialising Page");
            showPB();

            Log.i("Adi", "drawEvent: " + pageId);
            Log.i("Adi", "drawEvent: currentpage " + currentPageNumber);
            APIMethods.initialisePage(this, currentPageNumber, new APIResponseListener<InitialisePageRP>() {
                @Override
                public void success(InitialisePageRP response) {
                    if(!response.isNewPage()){
                        if(response.getPage().getEmail() != null && !response.getPage().getGender().isEmpty()){
                            if(response.getPage().getGender().equals("M")){
                                gender = "M";
                                tvMale.setBackgroundColor(getColor(R.color.blue_bg));
                                tvFemale.setBackgroundColor(getColor(R.color.white));
                                tvMale.setTextColor(getColor(R.color.white));
                                tvFemale.setTextColor(getColor(R.color.black));
                            }
                            else{
                                gender = "F";
                                tvFemale.setBackgroundColor(getColor(R.color.blue_bg));
                                tvMale.setBackgroundColor(getColor(R.color.white));
                                tvFemale.setTextColor(getColor(R.color.white));
                                tvMale.setTextColor(getColor(R.color.black));
                            }
                        }

                        if(response.getPage().getEmail() != null && !response.getPage().getEmail().isEmpty() && !response.getPage().getEmail().equals("")){
                            etEmail.setText(response.getPage().getEmail().toString());
                        }
                        if(response.getPage().getEmail() != null &&!response.getPage().getFullName().isEmpty() && !response.getPage().getFullName().equals("")){
                            etFullName.setText(response.getPage().getFullName().toString());
                        }
                        if(response.getPage().getMobileNumber() != null &&response.getPage().getMobileNumber() != 0){
                            etMobileNumber.setText(String.valueOf(response.getPage().getMobileNumber()));
                        }

                        Log.i("ADI", "success: points received " + response.getPage().getPoints().toString());

                        binding.canvasView.addCoordinates(response.getPage().getPoints());
                    }
                    hidePB();
                    currentPage = response.getPage();
                    setTimelyUploads();
                    binding.toolbar.setSubtitle("Page initialised successfully");
                }

                @Override
                public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                    hidePB();
                    Methods.showError(PrescriptionActivity.this,message,true);
                }
            });
        }
        binding.canvasView.addCoordinate(x, y,actionType);
        pendingPoints.add(new Point(x, y,actionType));
    }


    private Handler handler;
    private Runnable runnable;
    private void setTimelyUploads() {
        if (handler != null && runnable != null){
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

    private void uploadPoints(){
        if (pendingPoints.size() > 0) {
            ArrayList<Point> uploadPoints = new ArrayList<>();
            uploadPoints.addAll(pendingPoints);

            pendingPoints = new ArrayList<>();

            //call upload points
            binding.toolbar.setSubtitle("Synchronising page");

            APIMethods.uploadPoints(PrescriptionActivity.this, currentPageNumber, uploadPoints, new APIResponseListener<EmptyRP>() {
                @Override
                public void success(EmptyRP response) {
                    binding.toolbar.setSubtitle("Page synchronised");
                }

                @Override
                public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                    Methods.showError(PrescriptionActivity.this,message,true);

                }
            });
        }
    }


    private void showError(String message, View.OnClickListener listener){

        Log.i("MedPA", "onShowError("+ message + ")");

        if (dialogPenBinding == null){
            dialogPenBinding = DialogPenBinding.inflate(getLayoutInflater());
            dialog = new AlertDialog.Builder(this)
                    .setView(dialogPenBinding.getRoot())
                    .create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


        dialogPenBinding.imageView.setVisibility(View.VISIBLE);
        dialogPenBinding.titleTxt.setVisibility(View.VISIBLE);
        dialogPenBinding.bodyTxt.setVisibility(View.VISIBLE);
        dialogPenBinding.actionBtn.setVisibility(View.VISIBLE);
        dialogPenBinding.progressBar.setVisibility(View.GONE);
        dialogPenBinding.radioSelector.setVisibility(View.GONE);

        String actionTxt;
        if (listener != null){
            message += "\n\nPress button to Retry";
            actionTxt = "Retry";
            dialogPenBinding.actionBtn.setOnClickListener(listener);
        } else {
            message += "\n\nClick button to close screen";
            actionTxt = "End process";
            dialogPenBinding.actionBtn.setOnClickListener(view->PrescriptionActivity.this.finish());
        }

        dialogPenBinding.titleTxt.setText("Error Occurred");
        dialogPenBinding.bodyTxt.setText(message);
        dialogPenBinding.imageView.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.ic_error_bg));
        dialogPenBinding.actionBtn.setText(actionTxt);
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

    }
    private void showMessage(String ttl, String msg){

        if (dialogPenBinding == null){
            dialogPenBinding = DialogPenBinding.inflate(getLayoutInflater());
            dialog = new AlertDialog.Builder(this)
                    .setView(dialogPenBinding.getRoot())
                    .create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
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
        dialogPenBinding.imageView.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.ic_error_bg));
        dialogPenBinding.actionBtn.setOnClickListener(view->dialog.dismiss());
    }

    private void showPB(){
        binding.pbPrescription.setVisibility(View.VISIBLE);
    }
    private void hidePB(){
        binding.pbPrescription.setVisibility(View.GONE);
    }
}