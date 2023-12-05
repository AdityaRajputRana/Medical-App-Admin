package com.example.medicalappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.medicalappadmin.PenDriver.ConnectionsHandler;
import com.example.medicalappadmin.PenDriver.Models.SmartPen;
import com.example.medicalappadmin.PenDriver.SmartPenDriver;
import com.example.medicalappadmin.PenDriver.SmartPenListener;
import com.example.medicalappadmin.databinding.ActivityDashboardBinding;
import com.example.medicalappadmin.databinding.DialogPenBinding;
import com.example.medicalappadmin.fragments.HomeFragment;
import com.example.medicalappadmin.fragments.NotepadFragment;
import com.example.medicalappadmin.fragments.ProfileFragment;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

import kr.neolab.sdk.metadata.structure.Symbol;

public class DashboardActivity extends AppCompatActivity implements HomeFragment.CallBacksListener, ProfileFragment.CallBacksListener {
    private ActivityDashboardBinding binding;

    DialogPenBinding dialogPenBinding;
    AlertDialog dialog;
    int state = 0;
    SmartPen selectedPen;
    ArrayList<SmartPen> smartPens;

    private SmartPenListener smartPenListener = new SmartPenListener() {
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
            if (dialog != null && dialogPenBinding != null) {
                if (establised) {
                    dialog.dismiss();
                    dialogPenBinding = null;
                    Toast.makeText(DashboardActivity.this, "Pen Connected", Toast.LENGTH_SHORT).show();
                } else {
                    showError("Connection failed", null);
                }
            }
        }

        @Override
        public void disconnected() {

        }

        @Override
        public void message(String s, String message) {

        }

        @Override
        public void drawEvent(float x, float y, int pageId, int actionType) {
            if (!presActStarted) {
                presActStarted = true;
                Intent i = new Intent(DashboardActivity.this, PrescriptionActivity.class);
                DashboardActivity.this.startActivity(i);
            }
        }

        @Override
        public void onPaperButtonPress(int id, String name) {

        }
    };

    private boolean presActStarted = false;
    private boolean isPenSearchRunning;

    private void showError(String message, View.OnClickListener listener){

        if (dialogPenBinding == null){
            dialogPenBinding = DialogPenBinding.inflate(getLayoutInflater());
            dialog = new AlertDialog.Builder(DashboardActivity.this)
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
            dialogPenBinding.actionBtn.setOnClickListener(view->dialog.dismiss());
        }

        dialogPenBinding.titleTxt.setText("Error Occurred");
        dialogPenBinding.bodyTxt.setText(message);
        dialogPenBinding.imageView.setImageDrawable(AppCompatResources.getDrawable(DashboardActivity.this,R.drawable.ic_error_bg));
        dialogPenBinding.actionBtn.setText(actionTxt);

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
                RadioButton button = new RadioButton(DashboardActivity.this);
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

    private void initialisePenConnectionControls() {
        driver.setListener(smartPenListener);
        driver.initialize();
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

        if(message == SmartPenDriver.CONNECT_MESSAGE.CONFIG_SUCCESS)
            searchPens();
        else if(message == SmartPenDriver.CONNECT_MESSAGE.REQUESTING_PERMS){
            dialogPenBinding.progressBar.setVisibility(View.VISIBLE);
            dialogPenBinding.bodyTxt.setText("Searching");
        } else
            showError(String.valueOf(message), null);
    }

    String autoConnMacAdd;
    private void activateAutoSmartPenConnect(){
        SharedPreferences preferences = this.getSharedPreferences("PEN_CONFIG", MODE_PRIVATE);
        boolean isPenSaved = preferences.getBoolean("isPenSaved", false);
        boolean isAutoConnectAllowed = preferences.getBoolean("isAutoConnectAllowed", true);

        if (isPenSaved && isAutoConnectAllowed){
            autoConnMacAdd = preferences.getString("SavePenMac", "");
            searchPensInBg();
        }
    }

    private void searchPensInBg() {
        driver.setListener(smartPenListener);
        Log.i("ConnHandlerRes", String.valueOf(driver.initialize()));
        driver.getSmartPenList(new ConnectionsHandler.PenConnectionsListener() {
            @Override
            public void onSmartPens(ArrayList<SmartPen> smartPens) {
            }

            @Override
            public void onSmartPen(SmartPen smartPen) {
                if (smartPen.getMacAddress().equals(autoConnMacAdd)){
                    driver.connectToPen(smartPen);
                }
            }
        });
    }


    private SmartPenDriver driver = SmartPenDriver.getInstance(this);


    @Override
    protected void onResume() {
        presActStarted = false;
        driver.setListener(smartPenListener);
        super.onResume();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomNavigationView.setItemIconTintList(null);
        changeFragment(0);
        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.home){
                    changeFragment(0);
                }
                else if(item.getItemId() == R.id.profile){
                    changeFragment(1);
                }
                return true;
            }
        });


        activateAutoSmartPenConnect();
    }

    private final HomeFragment homeFragment = new HomeFragment(this);
    private final  ProfileFragment profileFragment = new ProfileFragment(this);


    private void changeFragment(int i){
        Fragment fragment = homeFragment;
        switch (i){
            case 1:
                fragment = profileFragment;
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onPenIconClicked() {
        changeFragment(1);
    }



    @Override
    public void startConnectionRoutine() {
        initialisePenConnectionControls();
    }

    @Override
    public void disconnectFromPen() {
        driver.destroyConnection();
    }
}