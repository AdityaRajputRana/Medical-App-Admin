package com.example.medicalappadmin.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.medicalappadmin.ActivityCaseHistory;
import com.example.medicalappadmin.Models.User;
import com.example.medicalappadmin.PatientHistoryActivity;
import com.example.medicalappadmin.PenDriver.LiveData.PenStatusLiveData;
import com.example.medicalappadmin.PrescriptionActivity;
import com.example.medicalappadmin.R;
import com.example.medicalappadmin.Transformations.CircleTransformation;
import com.example.medicalappadmin.databinding.ActivityPatientHistoryBinding;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;


public class HomeFragment extends Fragment {

    public interface CallBacksListener{
        void onPenIconClicked();
    }

    private CallBacksListener listener;
    private LinearLayout btnAddNewPatient;
    private LinearLayout btnCaseHistory;
    private LinearLayout btnPatientHistory;
    private ImageButton penButton;
    private TextView tvGreeting;
    private TextView tvDoctorName;
    private ImageView ivProfilePic;
    private User user;



    public HomeFragment(Context context) {
        if (context instanceof  CallBacksListener){
            listener = (CallBacksListener) context;
        }
    }
    public HomeFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        btnAddNewPatient  = view.findViewById(R.id.btnAddNewPatient);
        btnCaseHistory  = view.findViewById(R.id.btnCaseHistory);
        btnPatientHistory  = view.findViewById(R.id.btnPatientHistory);
        penButton = view.findViewById(R.id.penStatusBtn);
        tvDoctorName = view.findViewById(R.id.tvDoctorName);
        tvGreeting = view.findViewById(R.id.tvGreeting);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);

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

        btnPatientHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getContext(), PatientHistoryActivity.class);
                startActivity(i);
            }
        });

        penButton.setOnClickListener(v->{
            if (listener != null){
                listener.onPenIconClicked();
            }
        });

        PenStatusLiveData.getPenStatusLiveData().getIsConnected()
                .observe(getViewLifecycleOwner(), isConnected->{
                    int colorCodeID = isConnected?R.color.colorActiveGreen:R.color.colorInactiveGray;
                    penButton.setColorFilter(getActivity().getColor(colorCodeID));
                });
        loadData();



    }


    private void loadData() {
        user = new Gson().fromJson(
                getActivity().getSharedPreferences("MY_PREF", MODE_PRIVATE).getString("MY_USER", "{}"),
                User.class
        );
        if(user != null){
            tvDoctorName.setText("Dr. " + user.getName());
            if(!user.getDisplayPicture().equals("") || user.getDisplayPicture() != null){
                Picasso.get().load(user.getDisplayPicture()).transform(new CircleTransformation()).into(ivProfilePic);
            }
        } else {
            tvDoctorName.setVisibility(View.GONE);
        }
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        Log.i("time", "loadData:hours "+ hours);
        String greeting = null;
        if(hours>=1 && hours<12){
            greeting = "Good Morning,";
        } else if(hours>=12 && hours<16){
            greeting = "Good Afternoon,";
        } else if(hours>=16 && hours<21){
            greeting = "Good Evening,";
        } else if(hours>=21 && hours<=24){
            greeting = "Good Night,";
        } else {
            greeting = "Hello";
        }
        Log.i("time", "loadData: gre "+greeting);
        tvGreeting.setText(greeting);
    }

}