package com.example.medicalappadmin.fragments.CaseDetails;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.medicalappadmin.R;
import com.example.medicalappadmin.databinding.FragmentCaseInfoBinding;


public class CaseInfoFragment extends Fragment {

    FragmentCaseInfoBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding == null){
            binding = FragmentCaseInfoBinding.inflate(inflater);
        }
        return binding.getRoot();
    }
}