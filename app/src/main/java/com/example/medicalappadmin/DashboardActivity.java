package com.example.medicalappadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.medicalappadmin.PenDriver.ConnectionsHandler;
import com.example.medicalappadmin.PenDriver.LiveData.DrawLiveDataBuffer;
import com.example.medicalappadmin.PenDriver.LiveData.PenStatusLiveData;
import com.example.medicalappadmin.PenDriver.Models.SmartPen;
import com.example.medicalappadmin.PenDriver.SmartPenDriver;
import com.example.medicalappadmin.PenDriver.SmartPenListener;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.databinding.ActivityDashboardBinding;
import com.example.medicalappadmin.databinding.DialogPenBinding;
import com.example.medicalappadmin.fragments.CasesHistoryFragment;
import com.example.medicalappadmin.fragments.HomeFragment;
import com.example.medicalappadmin.fragments.NotepadFragment;
import com.example.medicalappadmin.fragments.PatientsFragment;
import com.example.medicalappadmin.fragments.ProfileFragment;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.requests.EmptyReq;
import com.example.medicalappadmin.rest.response.ConfigurePageRP;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;

import java.util.ArrayList;



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
            Log.i("Connections", "Perm Result: " + granted);
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
                    dialog = null;
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
        public boolean onPaperButtonPress(int id, String name) {
            return false;
        }

        @Override
        public void startLinkingProcedure(int page) {

        }

        @Override
        public void stopLinking(int masterPage, int currentPage) {

        }

        @Override
        public boolean linkPages(int masterPage, int slavePage) {
            //return true if command is recognized
            return false;
        }
    };

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
        Log.i("PenConnectionRoutine", "Dash: Searching Pens");
        dialogPenBinding.progressBar.setVisibility(View.VISIBLE);
        dialogPenBinding.bodyTxt.setVisibility(View.VISIBLE);
        dialogPenBinding.titleTxt.setVisibility(View.VISIBLE);
        dialogPenBinding.imageView.setVisibility(View.GONE);
        dialogPenBinding.bodyTxt.setText("Pen Driver intialized successfully");
        dialogPenBinding.titleTxt.setText("Searching for pens");

        isPenSearchRunning = true;
        smartPens = new ArrayList<>();
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
                Log.i("Connections", "Adding to View");

            }
        });

        SmartPenDriver.observeSmartPens(dialog, new Observer<ArrayList<SmartPen>>() {
            @Override
            public void onChanged(ArrayList<SmartPen> mPens) {
                Log.i("PenConnectionRoutine", "Dash: Change in list observed: " + mPens.size());
                dialogPenBinding.radioSelector.removeAllViews();
                for (SmartPen smartPen: mPens){
                    if (smartPens == null) {
                        smartPens = new ArrayList<>();
                    }
                    dialogPenBinding.actionBtn.setOnClickListener(view->{
                        isPenSearchRunning = false;
                        connectToSmartPen(selectedPen);
                    });
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

        boolean success = driver.connectToPen(selectedPen);
        if (success){
            Toast.makeText(this, "Pen Connected!", Toast.LENGTH_SHORT).show();
        } else {
            showError("Connection failed", null);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001){
            if (resultCode == RESULT_OK){
                driver.getSmartPenList(new ConnectionsHandler.PenConnectionsListener() {
                    @Override
                    public void onSmartPens(ArrayList<SmartPen> smartPens) {
                        if (smartPens.size() > 0 )
                            dialogPenBinding.titleTxt.setText("Found all nearby pens");
                        else
                            showError("No Nearby pens found! Try refreshing bluetooth or turning on the power of smart pen", null);
                    }

                    @Override
                    public void onSmartPen(SmartPen smartPen) {

                    }
                });
            }
        }
    }

    private void initialisePenConnectionControls() {
        if (dialog != null || dialogPenBinding != null){
            dialog.dismiss();
            dialog = null;
            dialogPenBinding = null;
        }
        driver.setListener(smartPenListener);
        dialogPenBinding = DialogPenBinding.inflate(getLayoutInflater());
        dialog = new AlertDialog.Builder(this)
                .setView(dialogPenBinding.getRoot())
                .create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setCancelable(false);
        dialogPenBinding.hideBtn.setOnClickListener(view -> dialog.dismiss());
        dialogPenBinding.hideBtn.setVisibility(View.VISIBLE);
        dialog.show();

        dialogPenBinding.progressBar.setVisibility(View.VISIBLE);
        dialogPenBinding.titleTxt.setVisibility(View.VISIBLE);
        dialogPenBinding.titleTxt.setText("Intialisiextenng Pen Driver");

        SmartPenDriver.CONNECT_MESSAGE message = driver.initialize();//driverStep3

        if(message == SmartPenDriver.CONNECT_MESSAGE.CONFIG_SUCCESS)
            searchPens();
        else if(message == SmartPenDriver.CONNECT_MESSAGE.REQUESTING_PERMS){
            dialogPenBinding.progressBar.setVisibility(View.VISIBLE);
            dialogPenBinding.bodyTxt.setText("Requesting Permissions");
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
        smartPens = new ArrayList<>();
        Log.i("ConnHandlerRes", String.valueOf(driver.initialize()));
        driver.getSmartPenList(new ConnectionsHandler.PenConnectionsListener() {
            @Override
            public void onSmartPens(ArrayList<SmartPen> smartPens) {
            }

            @Override
            public void onSmartPen(SmartPen smartPen) {

            }
        });
        SmartPenDriver.observeSmartPens(this, new Observer<ArrayList<SmartPen>>() {
            @Override
            public void onChanged(ArrayList<SmartPen> smartPens) {
                for (SmartPen smartPen: smartPens){
                    if (smartPen.getMacAddress().equals(autoConnMacAdd)){
                        SmartPenDriver.removeSmartPensObserver(this);
                        driver.connectToPen(smartPen);
                        break;
                    }
                }
            }
        });
    }


    private SmartPenDriver driver = SmartPenDriver.getInstance(this);


    @Override
    protected void onResume() {
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
                } else if (item.getItemId() == R.id.patients){
                    changeFragment(2);
                } else if (item.getItemId() == R.id.appointments){
                    changeFragment(3);
                }
                return true;
            }
        });
        activateAutoSmartPenConnect();
        setOnDrawNavigation();
    }

    private void setOnDrawNavigation() {
        driver.observeBuffer(this, buffer->{
            ArrayList< DrawLiveDataBuffer.DrawAction> bufferInstance = DrawLiveDataBuffer.readBuffer();
            if (bufferInstance != null && bufferInstance.size() >0){
                Log.i("lv-data", "NON NULL");
                startDrawActivity();
            }
        });
    }


    private boolean isDrawStarted = false;
    private void startDrawActivity(){
        if (isDrawStarted)
            return;
        isDrawStarted = true;
        Intent i = new Intent(this, PrescriptionActivity.class);
        Log.i("lv-data", "STARTING DRAW");
        startActivity(i);
    }

    @Override
    protected void onPostResume() {
        isDrawStarted = false;
        super.onPostResume();
    }


    private final HomeFragment homeFragment = new HomeFragment(this);
    private final  ProfileFragment profileFragment = new ProfileFragment(this);
    private final PatientsFragment patientsFragment = new PatientsFragment();
    private final CasesHistoryFragment casesHistoryFragment = new CasesHistoryFragment();


    private void changeFragment(int i){
        Fragment fragment = homeFragment;
        switch (i){
            case 1:
                fragment = profileFragment;
                break;
            case 2:
                fragment = patientsFragment;
                break;
            case 3:
                fragment = casesHistoryFragment;
                break;

        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onPenIconClicked() {
        if (Boolean.TRUE.equals(PenStatusLiveData.getPenStatusLiveData().getIsConnected().getValue())) {
            binding.bottomNavigationView.setSelectedItemId(R.id.profile);
        } else {
            initialisePenConnectionControls();
        }
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