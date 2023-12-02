package com.example.medicalappadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;

public class PatientDetailedHistoryActivity extends AppCompatActivity {

    private ActivityPatientDetailedHistoryBinding binding;
    String caseId;
    PagesHistoryAdapter adapter;
    GridLayoutManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatientDetailedHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        caseId = getIntent().getStringExtra("CASE_ID");

        if (caseId != null) {
            downloadCase(caseId);
        }
        manager = new GridLayoutManager(PatientDetailedHistoryActivity.this, 2);



        binding.rcvPages.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (manager.findFirstCompletelyVisibleItemPosition() == 0) {
                    if (binding.llStrip.getVisibility() == View.VISIBLE) {
                        binding.llStrip.setVisibility(View.INVISIBLE);
                    }
                }

                if (manager.findFirstCompletelyVisibleItemPosition() == 1) {
                    if (binding.llStrip.getVisibility() != View.VISIBLE) {
                        binding.llStrip.setVisibility(View.VISIBLE);
                    }
                }
            }
        });





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

        binding.rcvPages.setLayoutManager(manager);
//        binding.rcvPages.setLayoutManager(new LinearLayoutManager(PatientDetailedHistoryActivity.this));
        if (adapter == null) {
            adapter = new PagesHistoryAdapter(response, PatientDetailedHistoryActivity.this, new PagesHistoryAdapter.PageListener() {
                @Override
                public void onPageClicked(ArrayList<Page> pages, int currentPosition) {
                    Intent i = new Intent(PatientDetailedHistoryActivity.this,DetailedPageViewActivity.class);
                    String response = new Gson().toJson(pages);
//                    i.putExtra("PAGES_LIST", response );
                    i.putExtra("CASE_ID", caseId);
                    i.putExtra("CURRENT_PAGE_NUMBER", String.valueOf(currentPosition) );
                    Log.i("lol", "sent: " + currentPosition);
                    startActivity(i);
//                    Toast.makeText(PatientDetailedHistoryActivity.this, "Clicked " + pageNumber, Toast.LENGTH_SHORT).show();
                }
            });
        }
        binding.rcvPages.setAdapter(adapter);


    }
}