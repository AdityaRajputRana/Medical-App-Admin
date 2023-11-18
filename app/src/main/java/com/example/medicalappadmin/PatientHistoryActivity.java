package com.example.medicalappadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.medicalappadmin.databinding.ActivityPatientHistoryBinding;

public class PatientHistoryActivity extends AppCompatActivity {
    private ActivityPatientHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatientHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.pbPatientHistory.setVisibility(View.GONE);


        //TODO : Create Adapter for rcv and integrate apis


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
}