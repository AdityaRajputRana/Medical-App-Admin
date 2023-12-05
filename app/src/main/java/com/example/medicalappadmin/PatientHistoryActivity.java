package com.example.medicalappadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.adapters.CaseHistoryRVAdapter;
import com.example.medicalappadmin.adapters.PatientListAdapter;
import com.example.medicalappadmin.databinding.ActivityPatientHistoryBinding;
import com.example.medicalappadmin.rest.api.API;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.CaseHistoryRP;
import com.example.medicalappadmin.rest.response.PatientListRP;

public class PatientHistoryActivity extends AppCompatActivity {
    private ActivityPatientHistoryBinding binding;

    int currentPage = 1;
    int totalPages = Integer.MAX_VALUE;

    private PatientListRP patientListRP;

    private PatientListAdapter adapter;
    LinearLayoutManager manager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatientHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.pbPatientHistory.setVisibility(View.GONE);


        manager = new LinearLayoutManager(PatientHistoryActivity.this);
        binding.rcvPatients.setVisibility(View.GONE);

        loadPatientsList(currentPage,totalPages);



        binding.rcvPatients.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (manager.findFirstCompletelyVisibleItemPosition() == 0) {
                    if (binding.llTopStrip.getVisibility() == View.VISIBLE) {
                        binding.llTopStrip.setVisibility(View.INVISIBLE);
                    }
                }

                if (manager.findFirstCompletelyVisibleItemPosition() == 1) {
                    if (binding.llTopStrip.getVisibility() != View.VISIBLE) {
                        binding.llTopStrip.setVisibility(View.VISIBLE);
                    }
                }

                if (manager.findLastCompletelyVisibleItemPosition() == loadedPatients - 1) {
                    if (binding.pbPatientHistory.getVisibility() != View.VISIBLE) {
                        currentPage++;
                        loadPatientsList(currentPage, totalPages);
                    }
                }
            }
        });



        binding.ibPatientBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.tvTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PatientHistoryActivity.this, PatientDetailedHistoryActivity.class);
                startActivity(i);
                finish();
            }
        });


    }


    int loadedPatients = -1;

    private void loadPatientsList(int cPage, int tPages) {
            if (cPage > tPages) {
                binding.pbPatientHistory.setVisibility(View.GONE);
                return;
            }

            binding.pbPatientHistory.setVisibility(View.VISIBLE);
            APIMethods.loadPatientsList(this, cPage, new APIResponseListener<PatientListRP>() {
                @Override
                public void success(PatientListRP response) {

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
                            public void onPatientClicked() {
                                //TODO open patient details
                                PatientListAdapter.PatientListener.super.onPatientClicked();
                            }

                            @Override
                            public void onShareClicked() {
                                //TODO share patient

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