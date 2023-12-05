package com.example.medicalappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.adapters.ViewPatientCasesAdapter;
import com.example.medicalappadmin.databinding.ActivityViewPatientBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.ViewPatientRP;

public class ActivityViewPatient extends AppCompatActivity {


    ActivityViewPatientBinding binding;
    ViewPatientCasesAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewPatientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String patientId = getIntent().getStringExtra("PATIENT_ID");
        binding.rcvVPCases.setLayoutManager(new LinearLayoutManager(this));
        loadPatientDetails(patientId);

        binding.ivVPBackBtn.setOnClickListener(view -> finish());

//        binding.rcvVPCases.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                if (dy > 0) {
//                    // Scrolling up, hide the upper layout
//                    binding.appBarLayout.setVisibility(View.GONE);
//                    binding.appBarLayout.setExpanded(false, true);
//                } else {
//                    binding.appBarLayout.setVisibility(View.VISIBLE);
//                    binding.appBarLayout.setExpanded(true, true);
//                }
//            }
//        });



    }

    private void loadPatientDetails(String patientId) {
        binding.pbVP.setVisibility(View.VISIBLE);

        APIMethods.viewPatient(ActivityViewPatient.this, patientId, new APIResponseListener<ViewPatientRP>() {
            @Override
            public void success(ViewPatientRP response) {
                setDetailsInUI(response);

                //todo set up recyclerview
                if(adapter == null){
                    adapter = new ViewPatientCasesAdapter(response, ActivityViewPatient.this, new ViewPatientCasesAdapter.VPCaseListener() {
                        @Override
                        public void caseClicked(String caseId) {
                            Intent i = new Intent(ActivityViewPatient.this,PatientDetailedHistoryActivity.class);
                            i.putExtra("CASE_ID",caseId);
                            startActivity(i);
                        }
                    });
                }

                binding.rcvVPCases.setAdapter(adapter);
                binding.pbVP.setVisibility(View.GONE);


            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                Methods.showError(ActivityViewPatient.this,message,cancellable);
                binding.pbVP.setVisibility(View.GONE);

            }
        });

    }

    private void setDetailsInUI(ViewPatientRP response) {
        binding.tvVPName.setText(response.getPatientDetails().getFullName());
        binding.tvVPGender.setText(response.getPatientDetails().getGender());
        binding.tvVPNo.setText(String.valueOf(response.getPatientDetails().getMobileNumber()));
    }
}