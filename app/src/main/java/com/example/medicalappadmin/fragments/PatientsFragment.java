package com.example.medicalappadmin.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.example.medicalappadmin.ActivityViewPatient;
import com.example.medicalappadmin.PatientHistoryActivity;
import com.example.medicalappadmin.R;
import com.example.medicalappadmin.Tools.Const;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.adapters.PatientListAdapter;
import com.example.medicalappadmin.databinding.FragmentPatientsBinding;
import com.example.medicalappadmin.databinding.FragmentProfileBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.PatientListRP;

public class PatientsFragment extends Fragment {

    FragmentPatientsBinding binding;
    int currentPage = 1;
    int totalPages = Integer.MAX_VALUE;

    private PatientListRP patientListRP;

    private PatientListAdapter adapter;
    LinearLayoutManager manager;


    public PatientsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding == null) {
            binding = FragmentPatientsBinding.inflate(inflater);
            binding.pbPatientHistory.setVisibility(View.GONE);


            manager = new LinearLayoutManager(getActivity());
            binding.rcvPatients.setVisibility(View.GONE);


            loadPatientsList(currentPage, totalPages);
            setListeners();
        }
        return binding.getRoot();
    }

    private void setListeners() {
        binding.rcvPatients.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {

                if (manager.findLastCompletelyVisibleItemPosition() == loadedPatients - 1) {
                    if (binding.pbPatientHistory.getVisibility() != View.VISIBLE) {
                        currentPage++;
                        loadPatientsList(currentPage, totalPages);
                    }
                }

                if (manager.findFirstCompletelyVisibleItemPosition() == 0){
                    binding.headerCard.setCardElevation(0);
                } else {
                    binding.headerCard.setCardElevation(10);
                }
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

    private void performSearch(String searchQuery) {
        Intent intent = new Intent(getActivity(), PatientHistoryActivity.class);
        intent.putExtra(Const.searchQuery, searchQuery);
        startActivity(intent);
    }


    int loadedPatients = -1;
    private void loadPatientsList(int cPage, int tPages) {
        if (cPage > tPages) {
            binding.pbPatientHistory.setVisibility(View.GONE);
            return;
        }

        binding.pbPatientHistory.setVisibility(View.VISIBLE);
        APIMethods.loadPatientsList(getActivity(), cPage, new APIResponseListener<PatientListRP>() {
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
                    adapter = new PatientListAdapter(response, getActivity(), new PatientListAdapter.PatientListener() {
                        @Override
                        public void onPatientClicked(String id) {
                            //TODO open patient details
                            Intent i = new Intent(getActivity(), ActivityViewPatient.class);
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
                Methods.showError(getActivity(),message,cancellable);
            }
        });


    }
}