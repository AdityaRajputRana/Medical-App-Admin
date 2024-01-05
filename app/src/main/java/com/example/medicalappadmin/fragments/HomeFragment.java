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
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.HomePageRP;
import com.facebook.shimmer.ShimmerFrameLayout;

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
    private TextView tvTotal;
    private TextView tvTotalMale;
    private TextView tvTotalFemale;
    private TextView tvTodayTotal;
    private TextView tvTodayMale;
    private TextView tvTodayFemale;
    private ImageView ivProfilePic;
    private PieChart pieChartTotal;
    private PieChart pieChartToday;
    private User user;
    private ConstraintLayout clHome;
    private LinearLayout llAnalytics;
    private LinearLayout llToday;
    private HomePageRP homePageRP;
    private ShimmerFrameLayout shimmerContainer;
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
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        pieChartTotal = view.findViewById(R.id.pieChartTotal);
        tvTotal = view.findViewById(R.id.tvTotal);
        tvTotalMale = view.findViewById(R.id.tvTotalMale);
        tvTotalFemale = view.findViewById(R.id.tvTotalFemale);
        tvTodayTotal = view.findViewById(R.id.tvTotalToday);
        tvTodayMale = view.findViewById(R.id.tvTodayMale);
        tvTodayFemale = view.findViewById(R.id.tvTodayFemale);
        pieChartToday = view.findViewById(R.id.pieChartToday);
        clHome = view.findViewById(R.id.clHome);
        shimmerContainer = view.findViewById(R.id.shimmerContainer);
        llAnalytics = view.findViewById(R.id.llAnalytics);
        llToday = view.findViewById(R.id.llTodayAnalytics);

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
                llAnalytics.setVisibility(View.GONE);
                Log.i("Home", "fail: " + message);
            }
        });
    }

    private void setUpUI() {

        setGreeting();
        if (homePageRP != null) {
            shimmerContainer.stopShimmer();
            shimmerContainer.hideShimmer();
            tvDoctorName.setText("Dr. "+homePageRP.getStaffDetails().getFullName());
            tvTotal.setText("Total Patients : " + homePageRP.getAnalytics().getTotal().getCount());
            tvTotalMale.setText("Males : " + homePageRP.getAnalytics().getTotal().getMale());
            tvTotalFemale.setText("Females : " + homePageRP.getAnalytics().getTotal().getFemale());
            pieChartTotal.addPieSlice(
                    new PieModel(
                            "Male", homePageRP.getAnalytics().getTotal().getMale(), Color.parseColor("#90ee90")
                    )
            );
            pieChartTotal.addPieSlice(
                    new PieModel(
                            "Female", homePageRP.getAnalytics().getTotal().getFemale(), Color.parseColor("#FFB6C1")
                    )
            );
            pieChartTotal.animate();
            if (getActivity() != null)
                pieChartTotal.setInnerPaddingColor(getActivity().getColor(R.color.colorBackground));

            if (homePageRP.getAnalytics().getTodaySoFar().getCount() != 0) {
                llToday.setVisibility(View.VISIBLE);
                tvTodayTotal.setText("Total Patients : " + homePageRP.getAnalytics().getTodaySoFar().getCount());
                tvTodayMale.setText("Males : " + homePageRP.getAnalytics().getTodaySoFar().getMale());
                tvTodayFemale.setText("Females : " + homePageRP.getAnalytics().getTodaySoFar().getFemale());
                pieChartToday.addPieSlice(
                        new PieModel(
                                "Male", homePageRP.getAnalytics().getTodaySoFar().getMale(), Color.parseColor("#90ee90")
                        )
                );
                pieChartToday.addPieSlice(
                        new PieModel(
                                "Female", homePageRP.getAnalytics().getTodaySoFar().getFemale(), Color.parseColor("#FFB6C1")
                        )
                );
                pieChartTotal.animate();
                pieChartTotal.setInnerPaddingColor(getActivity().getColor(R.color.colorBackground));

            }
        } else {
            llAnalytics.setVisibility(View.GONE);
        }
    }

    public interface CallBacksListener {
        void onPenIconClicked();
    }

}