package com.example.medicalappadmin.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.medicalappadmin.ActivityCaseHistory;
import com.example.medicalappadmin.Models.User;
import com.example.medicalappadmin.PatientHistoryActivity;
import com.example.medicalappadmin.PenDriver.LiveData.PenStatusLiveData;
import com.example.medicalappadmin.PrescriptionActivity;
import com.example.medicalappadmin.R;
import com.example.medicalappadmin.Tools.Const;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.HomePageRP;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.Calendar;


public class HomeFragment extends Fragment {

    private CallBacksListener listener;
    private LinearLayout btnAddNewPatient;
    private LinearLayout btnCaseHistory;
    private LinearLayout btnPatientHistory;
    private ImageButton penButton;
    private TextView tvGreeting;
    private TextView tvDoctorName;
    private HomePageRP homePageRP;
    private ShimmerFrameLayout shimmerContainer;

    private View searchPatientBtn;
    public HomeFragment(Context context) {
        if (context instanceof CallBacksListener) {
            listener = (CallBacksListener) context;
        }
    }


    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        btnAddNewPatient = view.findViewById(R.id.btnAddNewPatient);
        btnCaseHistory = view.findViewById(R.id.btnCaseHistory);
        btnPatientHistory = view.findViewById(R.id.btnPatientHistory);
        penButton = view.findViewById(R.id.penStatusBtn);
        tvDoctorName = view.findViewById(R.id.tvDoctorName);
        tvGreeting = view.findViewById(R.id.tvGreeting);
        shimmerContainer = view.findViewById(R.id.shimmerContainer);

        searchPatientBtn = view.findViewById(R.id.searchPatientBtn);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListeners();

        if (homePageRP == null) {
            loadHomePage();
        } else {
            setUpUI();
        }

    }

    private void setListeners() {
        searchPatientBtn.setOnClickListener(btn->{
            Intent i = new Intent(getActivity(), PatientHistoryActivity.class);
            i.putExtra(Const.patientFilter, Const.patientFilterSearch);
            getActivity().startActivity(i);
        });



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
                Intent i = new Intent(getContext(), ActivityCaseHistory.class);
                startActivity(i);
            }
        });

        btnPatientHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), PatientHistoryActivity.class);
                startActivity(i);
            }
        });

        penButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPenIconClicked();
            }
        });

        PenStatusLiveData.getPenStatusLiveData().getIsConnected()
                .observe(getViewLifecycleOwner(), isConnected -> {
                    int colorCodeID = isConnected ? R.color.colorActiveGreen : R.color.colorInactiveGray;
                    penButton.setColorFilter(getActivity().getColor(colorCodeID));
                });
    }

    private void setGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        Log.i("time", "loadData:hours " + hours);
        String greeting = null;
        if (hours >= 1 && hours < 12) {
            greeting = "Good morning,";
        } else if (hours >= 12 && hours < 16) {
            greeting = "Good afternoon,";
        } else if (hours >= 16 && hours < 21) {
            greeting = "Good evening,";
        } else if (hours >= 21 && hours <= 24) {
            greeting = "Good night,";
        } else {
            greeting = "Hello";
        }
        Log.i("time", "loadData: gre " + greeting);
        tvGreeting.setText(greeting);
    }

    private void loadHomePage() {
        APIMethods.homePage(getContext(), new APIResponseListener<HomePageRP>() {
            @Override
            public void success(HomePageRP response) {
                homePageRP = response;
                setUpUI();
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                tvDoctorName.setVisibility(View.GONE);
                setGreeting();
                Log.i("Home", "fail: " + message);
            }
        });
    }

    private void setUpUI() {

        setGreeting();
        if (homePageRP != null) {
            shimmerContainer.stopShimmer();
            shimmerContainer.hideShimmer();
            tvDoctorName.setText(homePageRP.getStaffDetails().getFullName());
            }
    }

    public interface CallBacksListener {
        void onPenIconClicked();
    }

}