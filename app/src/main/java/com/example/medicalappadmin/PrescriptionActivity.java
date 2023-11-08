package com.example.medicalappadmin;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.Models.Point;
import com.example.medicalappadmin.PenDriver.ConnectionsHandler;
import com.example.medicalappadmin.PenDriver.Models.SmartPen;
import com.example.medicalappadmin.PenDriver.SmartPenDriver;
import com.example.medicalappadmin.PenDriver.SmartPenListener;
import com.example.medicalappadmin.databinding.ActivityPrescriptionBinding;
import com.example.medicalappadmin.databinding.DialogPenBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.InitialisePageRP;
import com.example.medicalappadmin.rest.response.UploadPointsRP;

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



    SmartPenDriver driver = SmartPenDriver.getInstance(this); //driverStep1




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intialiseControls();

    }

    private void intialiseControls() {
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

        driver.setListener(this);
        SmartPenDriver.CONNECT_MESSAGE message = driver.initialize();//driverStep3

        if(message == SmartPenDriver.CONNECT_MESSAGE.CONFIG_SUCCESS)
            searchPens();
        else if(message == SmartPenDriver.CONNECT_MESSAGE.REQUESTING_PERMS){
            dialogPenBinding.progressBar.setVisibility(View.VISIBLE);
            dialogPenBinding.bodyTxt.setText("Searching");
        } else
            showError(String.valueOf(message), null);
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
    public void drawEvent(float x, float y, int pageId) {
        if(pageId != currentPageNumber){
            currentPageNumber = pageId;
            APIMethods.initialisePage(this, currentPageNumber, new APIResponseListener<InitialisePageRP>() {
                @Override
                public void success(InitialisePageRP response) {
                    if(!response.isNewPage()){
                        Log.i("ADI", "success: points received " + response.getPage().getPoints().toString());
                        for(Point p:response.getPage().getPoints()){
                            binding.canvasView.addCoordinate(p.getX(),p.getY());
                        }
                    }
                    currentPage = response.getPage();
                    setTimelyUploads();
                    Toast.makeText(PrescriptionActivity.this, "Page initialised", Toast.LENGTH_LONG).show();
                }

                @Override
                public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                    Toast.makeText(PrescriptionActivity.this, "Page initialised failed" + message, Toast.LENGTH_LONG).show();
                }
            });
        }
        binding.canvasView.addCoordinate(x, y);
        pendingPoints.add(new Point(x, y));
//        Log.i("Adi", x +" y->"+ y);
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
                if (pendingPoints.size() > 0) {
                    ArrayList<Point> uploadPoints = new ArrayList<>();
                    uploadPoints.addAll(pendingPoints);

                    pendingPoints = new ArrayList<>();

                    //call upload points

                    Log.i("ADI ", "page id " + currentPage.get_id());

                    APIMethods.uploadPoints(PrescriptionActivity.this, currentPage.get_id(), uploadPoints, new APIResponseListener<UploadPointsRP>() {
                        @Override
                        public void success(UploadPointsRP response) {
                            
                        }

                        @Override
                        public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                            Toast.makeText(PrescriptionActivity.this, "Some error occurred while uploading points" + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                handler.postDelayed(runnable, 15000);
            }
        };

        handler.postDelayed(runnable, 15000);
    }


    private void showError(String message, View.OnClickListener listener){

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
}