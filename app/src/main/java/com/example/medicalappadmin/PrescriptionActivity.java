package com.example.medicalappadmin;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
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
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;

import com.example.medicalappadmin.Models.LinkedPatient;
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
import com.example.medicalappadmin.rest.requests.AddMobileNoReq;
import com.example.medicalappadmin.rest.requests.LinkPageReq;
import com.example.medicalappadmin.rest.response.AddDetailsRP;
import com.example.medicalappadmin.rest.response.AddMobileNoRP;
import com.example.medicalappadmin.rest.response.EmptyRP;
import com.example.medicalappadmin.rest.response.InitialisePageRP;
import com.example.medicalappadmin.rest.response.LinkPageRP;

import java.util.ArrayList;
import java.util.Objects;

public class PrescriptionActivity extends AppCompatActivity implements SmartPenListener {


    String TAG = "pres";

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
    EditText etPageNumber;
    TextView tvMale;
    TextView tvFemale;
    LinearLayout llPrevPatientList;
    LinearLayout llNewPatient;
    LinearLayout llAddMobileNumber;
    LinearLayout llExistingPatientDetails;
    AppCompatButton btnCheckRelatives;
    AppCompatButton btnNext;
    AppCompatButton btnSave;
    AppCompatButton btnSyncPage;
    TextView tvEPatientsNo;
    TextView tvEPGender;
    TextView tvEPatientName;
    ImageView ivEPatientsDp;
    ProgressBar pbAddMobile;
    ProgressBar pbSyncPage;
    ProgressBar pbSelectRelative;
    ProgressBar pbSaveNewPatient;
    RadioGroup relativeRadioSelector;
    String gender = "M";
    ArrayList<LinkedPatient> relatives;
    SmartPenDriver driver = SmartPenDriver.getInstance(this); //driverStep1
    long tempMobile = 0;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Methods.setStatusBarColor(getColor(R.color.colorCta), PrescriptionActivity.this);


        setSupportActionBar(binding.toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.drawer_open, R.string.drawer_close);
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        //LL Sync page
        btnSyncPage = binding.navView.getHeaderView(0).findViewById(R.id.btnLinkPage);
        pbSyncPage = binding.navView.getHeaderView(0).findViewById(R.id.pbLinkPage);

        //LL Existing patient  details
        llExistingPatientDetails = binding.navView.getHeaderView(0).findViewById(R.id.llExistingPatientDetails);
        tvEPatientName = binding.navView.getHeaderView(0).findViewById(R.id.tvEPatientName);
        tvEPatientsNo = binding.navView.getHeaderView(0).findViewById(R.id.tvEPatientsNo);
        tvEPGender = binding.navView.getHeaderView(0).findViewById(R.id.tvEPGender);
        ivEPatientsDp = binding.navView.getHeaderView(0).findViewById(R.id.ivEPatientsDp);

        //LL add mobile number
        llAddMobileNumber = binding.navView.getHeaderView(0).findViewById(R.id.llAddMobileNumber);
        etMobileNumber = binding.navView.getHeaderView(0).findViewById(R.id.etMobile);
        etPageNumber = binding.navView.getHeaderView(0).findViewById(R.id.etPageNumber);
        btnCheckRelatives = binding.navView.getHeaderView(0).findViewById(R.id.btnCheckRelatives);
        pbAddMobile = binding.navView.getHeaderView(0).findViewById(R.id.pbAddMobile);


        //LL check relatives
        llPrevPatientList = binding.navView.getHeaderView(0).findViewById(R.id.llPrevPatientList);
        relativeRadioSelector = binding.navView.getHeaderView(0).findViewById(R.id.relativeRadioSelector);
        btnNext = binding.navView.getHeaderView(0).findViewById(R.id.btnNext);
        pbSelectRelative = binding.navView.getHeaderView(0).findViewById(R.id.pbSelectRelative);

        //LL new patient
        llNewPatient = binding.navView.getHeaderView(0).findViewById(R.id.llNewPatient);
        etFullName = binding.navView.getHeaderView(0).findViewById(R.id.etFullName);
        etEmail = binding.navView.getHeaderView(0).findViewById(R.id.etEmail);
        tvMale = binding.navView.getHeaderView(0).findViewById(R.id.tvMale);
        tvFemale = binding.navView.getHeaderView(0).findViewById(R.id.tvFemale);
        btnSave = binding.navView.getHeaderView(0).findViewById(R.id.btnSave);
        pbSaveNewPatient = binding.navView.getHeaderView(0).findViewById(R.id.pbSaveNewPatient);
        handleGender();


        //initialise page
        btnSyncPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pbSyncPage.setVisibility(View.VISIBLE);
                hideKeyboard(view);
                Log.i(TAG, "onClick: current page no " + currentPageNumber);
                drawEvent(0, 0, Integer.parseInt(etPageNumber.getText().toString()), 0);
            }
        });

        btnCheckRelatives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                tempMobile = Long.parseLong(etMobileNumber.getText().toString());
                linkMobileNumber();
            }
        });
        intialiseControls();

    }

    private void linkMobileNumber() {
        if (currentPageNumber == -1) {
            Toast.makeText(PrescriptionActivity.this, "Please touch your page with pen", Toast.LENGTH_SHORT).show();
            binding.drawerLayout.close();
            return;
        }

        AddMobileNoReq req = new AddMobileNoReq();
        if (etMobileNumber.getText() == null || etMobileNumber.getText().toString().isEmpty() || etMobileNumber.getText().toString().equals("")) {
            etMobileNumber.setError("Required");
            return;
        }
        req.setPageNumber(Integer.parseInt(etPageNumber.getText().toString()));
        req.setMobileNumber(Long.parseLong(etMobileNumber.getText().toString()));

        Log.i(TAG, "req set mobile" + req.toString());
        pbAddMobile.setVisibility(View.VISIBLE);

        APIMethods.addMobileNumber(PrescriptionActivity.this, req, new APIResponseListener<AddMobileNoRP>() {
            @Override
            public void success(AddMobileNoRP response) {
                pbAddMobile.setVisibility(View.GONE);
                Log.i(TAG, "success: size of relatives " + response.getPatients().size());

                if (response.getPatients().size() != 0) {
                    //relatives exist
                    Log.i(TAG, "success: relative exists");

                    showPreviousPatientsLayout();

                    relatives = new ArrayList<>();

                    relatives = response.getPatients();
                    relativeRadioSelector.clearCheck();
                    relativeRadioSelector.removeAllViews();

                    for (int i = 0; i < response.getPatients().size(); i++) {
                        Log.i(TAG, "success: relative " + i + " = " + response.getPatients().get(i).getFullName());
                        Log.i(TAG, "success: relative id " + i + " = " + response.getPatients().get(i).get_id());
                        RadioButton button = new RadioButton(PrescriptionActivity.this);
                        button.setText(relatives.get(i).getFullName());
                        button.setTextSize(16);
                        button.setTextColor(getColor(R.color.colorPrimTxt));
                        button.setId(i);
                        relativeRadioSelector.addView(button);
                        Log.i(TAG, "success: iteration " + i + 1);
                    }

                    //RadioButton for new patient
                    RadioButton button = new RadioButton(PrescriptionActivity.this);
                    button.setText("Other");
                    button.setTextSize(16);
                    button.setTextColor(getColor(R.color.colorPrimTxt));
                    button.setId(relatives.size());
                    relativeRadioSelector.addView(button);

                    btnNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hideKeyboard(view);
                            int selectedRelativeId = relativeRadioSelector.getCheckedRadioButtonId();
                            Log.i(TAG, "onCheckedChanged: selected relative " + selectedRelativeId);
                            if (selectedRelativeId != -1) {
                                showRelativeDetails(selectedRelativeId);
                            } else {
                                Toast.makeText(PrescriptionActivity.this, "Select a relative first", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } else {
                    //new patient
                    Log.i(TAG, "success: relative does not exist, new patient");

                    llAddMobileNumber.setVisibility(View.GONE);
                    llPrevPatientList.setVisibility(View.GONE);
                    llNewPatient.setVisibility(View.VISIBLE);
                    setBtnSaveListener();

                }
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                pbAddMobile.setVisibility(View.GONE);
                showError(message, null);
                Log.i("Adi", "fail: add mobile " + message);
            }
        });
    }

    private void showPreviousPatientsLayout() {
        llAddMobileNumber.setVisibility(View.GONE);
        llNewPatient.setVisibility(View.GONE);
        llPrevPatientList.setVisibility(View.VISIBLE);
        llExistingPatientDetails.setVisibility(View.GONE);
    }

    private void setBtnSaveListener() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                saveNewPatient();
            }
        });

    }

    private void showRelativeDetails(int selectedRelativeId) {
        if (selectedRelativeId == relatives.size()) {
            showAddNewPatientLayout();
            setBtnSaveListener();
        } else if (selectedRelativeId < relatives.size()) {
            LinkedPatient selectedRelative = relatives.get(selectedRelativeId);

            showRelativePrevCases(selectedRelative);
            linkPageToPatient(selectedRelative);
        } else {
            Log.i(TAG, "showRelativeDetails: relatives null");

        }


    }

    private void showRelativePrevCases(LinkedPatient selectedRelative) {

        //TODO show previous cases rcv and set listeners + new case button


    }

    private void showAddNewPatientLayout() {
        llAddMobileNumber.setVisibility(View.GONE);
        llPrevPatientList.setVisibility(View.GONE);
        llNewPatient.setVisibility(View.VISIBLE);
        llExistingPatientDetails.setVisibility(View.GONE);
    }

    private void linkPageToPatient(LinkedPatient selectedRelative) {

        LinkPageReq req = new LinkPageReq();
        if (selectedRelative == null) {
            return;
        }

        Log.i(TAG, "linkPageToPatient: selected relative id " + selectedRelative.get_id());
        req.setPatientId(selectedRelative.get_id());
        req.setPageNumber(currentPageNumber);
        pbSelectRelative.setVisibility(View.VISIBLE);
        APIMethods.linkPage(PrescriptionActivity.this, req, new APIResponseListener<LinkPageRP>() {
            @Override
            public void success(LinkPageRP response) {
                Toast.makeText(PrescriptionActivity.this, "Page is linked to " + selectedRelative.getFullName(), Toast.LENGTH_SHORT).show();
                pbSelectRelative.setVisibility(View.GONE);
                binding.toolbar.setSubtitle("Page is linked to " + selectedRelative.getFullName());
                showExistingPatientLayout();
                Log.i(TAG, "success: linking" + etFullName.getText().toString());
                tvEPatientName.setText(etFullName.getText().toString());
                if (Objects.equals(gender, "M")) {
                    tvEPGender.setText("Male");
                } else {
                    tvEPGender.setText("Female");
                }
                tvEPatientsNo.setText(etMobileNumber.getText().toString());

                clearAllCache();

                binding.drawerLayout.close();

            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                pbSelectRelative.setVisibility(View.GONE);

                showError(message, null);
                Log.i(TAG, "fail: link to page " + message);
            }
        });
    }

    private void clearAllCache() {
        etFullName.setText("");
        etEmail.setText("");
        etMobileNumber.setText("");
    }

    private void saveNewPatient() {
        AddDetailsReq req = new AddDetailsReq();
        if (etFullName.getText().toString().isEmpty()) {
            etFullName.setError("Required");
            return;
        }
        if (etFullName.getText() != null && !etFullName.getText().toString().isEmpty()) {
            req.setFullName(etFullName.getText().toString());
        }
        if (etEmail.getText() != null && !etEmail.getText().toString().isEmpty()) {
            req.setEmail(etEmail.getText().toString());
        }

        req.setGender(gender);
        req.setPageNumber(currentPageNumber);
        req.setMobileNumber(Long.parseLong(etMobileNumber.getText().toString()));

        pbSaveNewPatient.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        APIMethods.addDetails(PrescriptionActivity.this, req, new APIResponseListener<AddDetailsRP>() {
            @Override
            public void success(AddDetailsRP response) {
                pbSaveNewPatient.setVisibility(View.GONE);
                showExistingPatientLayout();
                tvEPatientName.setText(response.getPatient().getFullName());
                tvEPatientsNo.setText(String.valueOf(response.getPatient().getMobileNumber()));
                if (response.getPatient().getGender().equals("M")) {
                    tvEPGender.setText("Male");
                } else if (response.getPatient().getGender().equals("F")) {
                    tvEPGender.setText("Female");
                }
                clearAllCache();

                binding.toolbar.setSubtitle("Details saved successfully");
                Toast.makeText(PrescriptionActivity.this, "Details saved successfully", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                binding.drawerLayout.close();

            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                pbSaveNewPatient.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                showError(message, null);
                binding.drawerLayout.close();
                Log.i("Adi", "fail: saving new patient " + message);
            }
        });
    }

    private void showAddMobileNoLayout() {
        llExistingPatientDetails.setVisibility(View.GONE);
        llNewPatient.setVisibility(View.GONE);
        llAddMobileNumber.setVisibility(View.VISIBLE);
        llNewPatient.setVisibility(View.GONE);
    }


    private void handleGender() {
        tvMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = "M";
                tvMale.setBackgroundColor(getColor(R.color.blue_bg));
                tvFemale.setBackgroundColor(getColor(R.color.colorBackground));
                tvMale.setTextColor(getColor(R.color.white));
                tvFemale.setTextColor(getColor(R.color.colorPrimTxt));
            }
        });
        tvFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = "F";
                tvFemale.setBackgroundColor(getColor(R.color.blue_bg));
                tvMale.setBackgroundColor(getColor(R.color.colorBackground));
                tvFemale.setTextColor(getColor(R.color.white));
                tvMale.setTextColor(getColor(R.color.colorPrimTxt));
            }
        });
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

        if (PenStatusLiveData.getPenStatusLiveData().getIsConnected().getValue() != null && PenStatusLiveData.getPenStatusLiveData().getIsConnected().getValue()) {
            showDrawView();
        } else {
            dialogPenBinding = DialogPenBinding.inflate(getLayoutInflater());
            dialog = new AlertDialog.Builder(this).setView(dialogPenBinding.getRoot()).create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
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

    private void searchPens() {


        //TODO remove it
        dialog.dismiss();
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

    @Override
    public void drawEvent(float x, float y, int pageId, int actionType) {
        if (pageId != currentPageNumber) {
            uploadPoints();
            if (handler != null) {
                handler.removeCallbacks(runnable);
                handler = null;
                runnable = null;
            }
            binding.canvasView.clearDrawing();
            currentPageNumber = pageId;
            binding.toolbar.setTitle("Page : " + currentPageNumber);
            binding.toolbar.setSubtitle("Initialising Page");
            showPB();

            APIMethods.initialisePage(this, currentPageNumber, new APIResponseListener<InitialisePageRP>() {
                @Override
                public void success(InitialisePageRP response) {
                    pbSyncPage.setVisibility(View.GONE);

                    if (!response.isNewPage()) {
                        if (response.getPage().getHospitalPatientId() != null) {
                            showExistingPatientLayout();
                            Log.i(TAG, "success: initialised page " + response.getPatient().getGender());
                            if (response.getPatient().getGender() != null && !response.getPatient().getGender().isEmpty()) {
                                if (response.getPatient().getGender().equals("M")) {
                                    tvEPGender.setText("Male");
                                } else {
                                    tvEPGender.setText("Female");
                                }
                            } else {
                                tvEPGender.setVisibility(View.GONE);
                            }
                            if (response.getPatient().getFullName() != null && !response.getPatient().getFullName().isEmpty() && !response.getPatient().getFullName().equals("")) {
                                tvEPatientName.setText(response.getPatient().getFullName());
                            }
                            if (response.getPatient().getMobileNumber() != null && response.getPatient().getMobileNumber() != 0) {
                                tvEPatientsNo.setText(String.valueOf(response.getPatient().getMobileNumber()));
                            }

                        } else {
                            showAddMobileNoLayout();
                        }

                        binding.canvasView.addCoordinates(response.getPage().getPoints());
                    } else {
                        showAddMobileNoLayout();
                    }
                    hidePB();
                    currentPage = response.getPage();
                    setTimelyUploads();
                    binding.drawerLayout.close();
                    binding.toolbar.setSubtitle("Page initialised successfully");

                }

                @Override
                public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                    hidePB();
                    Methods.showError(PrescriptionActivity.this, message, true);
                    pbSyncPage.setVisibility(View.GONE);

                }
            });
        }
        binding.canvasView.addCoordinate(x, y, actionType);
        pendingPoints.add(new Point(x, y, actionType));
    }

    private void showExistingPatientLayout() {
        llExistingPatientDetails.setVisibility(View.VISIBLE);
        llAddMobileNumber.setVisibility(View.GONE);
        llPrevPatientList.setVisibility(View.GONE);
        llNewPatient.setVisibility(View.GONE);
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
            binding.toolbar.setSubtitle("Synchronising page");

            APIMethods.uploadPoints(PrescriptionActivity.this, currentPageNumber, uploadPoints, new APIResponseListener<EmptyRP>() {
                @Override
                public void success(EmptyRP response) {
                    binding.toolbar.setSubtitle("Page synchronised");
                }

                @Override
                public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                    Methods.showError(PrescriptionActivity.this, message, true);

                }
            });
        }
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
}
