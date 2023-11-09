package com.example.medicalappadmin.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.medicalappadmin.ActivityCaseHistory;
import com.example.medicalappadmin.PrescriptionActivity;
import com.example.medicalappadmin.R;


public class HomeFragment extends Fragment {
    LinearLayout btnAddNewPatient;
    LinearLayout btnCaseHistory;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        btnAddNewPatient  = view.findViewById(R.id.btnAddNewPatient);
        btnCaseHistory  = view.findViewById(R.id.btnCaseHistory);
        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnAddNewPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getContext(), PrescriptionActivity.class);
                startActivity(i);
            }
        });

        btnCaseHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getContext(), ActivityCaseHistory.class);
                startActivity(i);
            }
        });
    }
}