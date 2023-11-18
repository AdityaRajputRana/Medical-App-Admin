package com.example.medicalappadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.medicalappadmin.databinding.ActivityPatientDetailedHistoryBinding;
import com.example.medicalappadmin.databinding.ActivityPatientHistoryBinding;

public class PatientDetailedHistoryActivity extends AppCompatActivity {

    private ActivityPatientDetailedHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatientDetailedHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }
}