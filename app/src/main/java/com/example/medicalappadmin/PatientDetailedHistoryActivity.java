package com.example.medicalappadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.adapters.PagesHistoryAdapter;
import com.example.medicalappadmin.databinding.ActivityPatientDetailedHistoryBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.ViewCaseRP;
import com.google.gson.Gson;

public class PatientDetailedHistoryActivity extends AppCompatActivity {

    private ActivityPatientDetailedHistoryBinding binding;
    String caseId;
    PagesHistoryAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatientDetailedHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        caseId = getIntent().getStringExtra("CASE_ID");

        if (caseId != null) {
            downloadCase(caseId);
        }


        binding.ivPatientDetailsBackBtn.setOnClickListener(view -> {
            finish();
        });

    }

    private void downloadCase(String caseId) {

        APIMethods.viewCase(PatientDetailedHistoryActivity.this, caseId, new APIResponseListener<ViewCaseRP>() {
            @Override
            public void success(ViewCaseRP response) {
                updateUI(response);
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                binding.shimmerContainer.stopShimmer();
                binding.shimmerContainer.hideShimmer();

            }
        });


    }

    private void updateUI(ViewCaseRP response) {

        if (response != null) {
            if (response.getPatient().getName() != null) {
                binding.tvPatientsName.setText(response.getPatient().getName());
            } else {
                binding.tvPatientsName.setText("Untitled Case");

            }
            binding.tvGender.setText(response.getPatient().getGender());
            binding.tvCaseName.setText(response.getTitle());
            binding.tvDiagnosis.setText(response.getDiagnosis());
            binding.tvLastUpdated.setText(response.getUpdatedAt());
            setUpRCV(response);

        } else {
            finish();
        }
        binding.shimmerContainer.stopShimmer();
        binding.shimmerContainer.hideShimmer();


    }



    //todo click on page to open it on next page, create new canvas view















    private void setUpRCV(ViewCaseRP response) {

        binding.rcvPages.setLayoutManager(new GridLayoutManager(PatientDetailedHistoryActivity.this, 2));
//        binding.rcvPages.setLayoutManager(new LinearLayoutManager(PatientDetailedHistoryActivity.this));
        if (adapter == null) {
            adapter = new PagesHistoryAdapter(response, PatientDetailedHistoryActivity.this, new PagesHistoryAdapter.PageListener() {
                @Override
                public void onPageClicked(Page page) {
                    Intent i = new Intent(PatientDetailedHistoryActivity.this,DetailedPageViewActivity.class);
                    String response = new Gson().toJson(page);
                    i.putExtra("OPEN_PAGE", response );
                    startActivity(i);
//                    Toast.makeText(PatientDetailedHistoryActivity.this, "Clicked " + pageNumber, Toast.LENGTH_SHORT).show();
                }
            });
        }
        binding.rcvPages.setAdapter(adapter);


    }
}