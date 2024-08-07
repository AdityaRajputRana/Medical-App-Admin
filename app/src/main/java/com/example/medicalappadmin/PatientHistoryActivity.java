package com.example.medicalappadmin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.example.medicalappadmin.Tools.Const;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.adapters.PatientListAdapter;
import com.example.medicalappadmin.databinding.ActivityPatientListBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.PatientListRP;

public class PatientHistoryActivity extends AppCompatActivity {
    private ActivityPatientListBinding binding;

    int currentPage = 1;
    int totalPages = Integer.MAX_VALUE;

    private PatientListRP patientListRP;

    private PatientListAdapter adapter;
    LinearLayoutManager manager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatientListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        preProcessUI();
        processFilter();
        binding.pbPatientHistory.setVisibility(View.GONE);


        manager = new LinearLayoutManager(PatientHistoryActivity.this);
        binding.rcvPatients.setVisibility(View.GONE);

        loadPatientsList(currentPage,totalPages);

        setListeners();



        binding.rcvPatients.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {

                if (manager.findLastCompletelyVisibleItemPosition() == loadedPatients - 1) {
                    if (binding.pbPatientHistory.getVisibility() != View.VISIBLE) {
                        currentPage++;
                        loadPatientsList(currentPage, totalPages);
                    }
                }
            }
        });



        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void preProcessUI() {
        String query = getIntent().getStringExtra(Const.searchQuery);
        if (query != null && !query.isEmpty()){
            binding.searchPatientEt.setText(query);
        }
    }

    boolean isPagePatientLinkEnabled = false;
    private void processFilter() {
        String filter = getIntent().getStringExtra(Const.patientFilter);
        if (filter == null) return;
        
        if (filter.contains(Const.patientFilterSearch)){
            binding.searchPatientEt.requestFocus();
            Methods.showKeyboardOnLaunch(this);
        }
        if (filter.contains(Const.patientFilterLinkPageAndPatient)){
            isPagePatientLinkEnabled = true;
        }
    }

    private void setListeners() {
        binding.btnGoBack.setOnClickListener(view -> {
            finish();
        });

        binding.addPatientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientHistoryActivity.this, AddNewPatientActivity.class);
                if (isPagePatientLinkEnabled){
                    intent.putExtra(Const.patientFilter, Const.patientFilterLinkPageAndPatient);
                    intent.putExtra("FILTER_PHONE", getIntent().getStringExtra("FILTER_PHONE"));
                    startActivityForResult(intent, 1);
                    return;
                }
                startActivity(intent);
            }
        });

        binding.searchPatientEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void selectCaseOfThePatient(String patientID){
        Intent intent = new Intent(this, ActivityCaseHistory.class);
        intent.putExtra("CASE_SELECTOR_FILTER_EN", true);
        intent.putExtra("PATIENT_ID", patientID);
        startActivityForResult(intent, 1);
    }

    private void performSearch(String searchQuery) {
        Intent intent = new Intent(this, PatientHistoryActivity.class);
        intent.putExtra(Const.searchQuery, searchQuery);
        if (isPagePatientLinkEnabled){
            intent.putExtra(Const.patientFilter, Const.patientFilterLinkPageAndPatient);
            intent.putExtra("FILTER_PHONE", getIntent().getStringExtra("FILTER_PHONE"));
            startActivityForResult(intent, 1);
            return;
        }
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && isPagePatientLinkEnabled && resultCode == RESULT_OK && data != null){
            setResult(RESULT_OK, data);
            finish();
        }
    }

    int loadedPatients = -1;

    private void loadPatientsList(int cPage, int tPages) {
            if (cPage > tPages) {
                binding.pbPatientHistory.setVisibility(View.GONE);
                return;
            }

            binding.pbPatientHistory.setVisibility(View.VISIBLE);
            APIMethods.loadPatientsList(this, cPage, getIntent().getStringExtra(Const.searchQuery), new APIResponseListener<PatientListRP>() {
                @Override
                public void success(PatientListRP response) {

                    if(response.getPatients().size() == 0) {
                        binding.llData.setVisibility(View.GONE);
                        binding.llNoData.setVisibility(View.VISIBLE);

                    }

                    if (patientListRP == null) {
                        patientListRP = response;
                    } else {
                        patientListRP.getPatients().addAll(response.getPatients());
                        patientListRP.setTotalPages(response.getTotalPages());
                        patientListRP.setCurrentPage(response.getCurrentPage());
                    }

                    if (loadedPatients == -1) {
                        loadedPatients = 0;
                    }
                    loadedPatients += response.getPatients().size();

                    binding.rcvPatients.setVisibility(View.VISIBLE);
                    totalPages = response.getTotalPages();

                    if (adapter == null) {
                        adapter = new PatientListAdapter(response, PatientHistoryActivity.this, new PatientListAdapter.PatientListener() {
                            @Override
                            public void onPatientClicked(String id) {
                                //TODO open patient details
                                if (isPagePatientLinkEnabled){
                                    selectCaseOfThePatient(id);
                                    return;
                                }
                                Intent i = new Intent(PatientHistoryActivity.this, ActivityPatientDetails.class);
                                i.putExtra("PATIENT_ID", id);
                                startActivity(i);
                            }

                            @Override
                            public void onShareClicked() {
                                PatientListAdapter.PatientListener.super.onShareClicked();
                            }
                        });
                        binding.rcvPatients.setLayoutManager(manager);
                        binding.rcvPatients.setAdapter(adapter);
                    } else {
                        adapter.notifyItemRangeInserted(patientListRP.getPatients().size() - response.getPatients().size() + 1, response.getPatients().size());

                    }
                    binding.pbPatientHistory.setVisibility(View.GONE);
                }

                @Override
                public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                    Methods.showError(PatientHistoryActivity.this,message,cancellable);
                }
            });


    }
}