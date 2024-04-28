package com.example.medicalappadmin.fragments.CaseDetails;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.medicalappadmin.ActivityPatientDetails;
import com.example.medicalappadmin.DetailedPageViewActivity;
import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.adapters.PagesHistoryAdapter;
import com.example.medicalappadmin.databinding.FragmentPrescriptionBinding;
import com.example.medicalappadmin.rest.response.ViewCaseRP;
import com.google.gson.Gson;

import java.util.ArrayList;

public class PrescriptionFragment extends Fragment {
    FragmentPrescriptionBinding binding;
    ViewCaseRP viewCaseRP;

    public void setUI(ViewCaseRP res){
        viewCaseRP = res;
        updateUI();
    }

    private void updateUI() {
        binding.progressBar.setVisibility(View.GONE);

        String name = viewCaseRP.getPatient().getFullName();
        binding.patientNameTxt.setText(name);

        String details = "";
        details += viewCaseRP.getPatient().getGenderFull();
        details += " | ";
        details += viewCaseRP.getPatient().getAgeText();
        binding.patientDetailsTxt.setText(details);
        binding.patientDetailsLayout.setVisibility(View.VISIBLE);

        //Pages
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        PagesHistoryAdapter adapter = new PagesHistoryAdapter(viewCaseRP, getActivity(), new PagesHistoryAdapter.PageListener() {
            @Override
            public void onPageClicked(ArrayList<Page> pages, int currentposition) {
                Intent i = new Intent(getActivity(), DetailedPageViewActivity.class);
                String caseRP = new Gson().toJson(viewCaseRP);
                i.putExtra("CASE_ID", viewCaseRP.get_id());
                i.putExtra("CASE", caseRP);
                i.putExtra("CURRENT_PAGE_NUMBER", String.valueOf(currentposition));
                startActivity(i);
            }
        });
        binding.rcvPages.setLayoutManager(manager);
        binding.rcvPages.setAdapter(adapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.rcvPages);
        binding.rcvPages.setVisibility(View.VISIBLE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding == null){
            binding = FragmentPrescriptionBinding.inflate(inflater);
            setListeners();
        }
        return binding.getRoot();
    }

    private void setListeners() {
        if (binding == null) return;
        binding.patientDetailsLayout.setOnClickListener(view->{
            if(viewCaseRP == null) return;
            Intent i = new Intent(getActivity(), ActivityPatientDetails.class);
            i.putExtra("PATIENT_ID",viewCaseRP.getPatient().get_id());
            getActivity().startActivity(i);
        });
    }
}