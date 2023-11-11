package com.example.medicalappadmin.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.medicalappadmin.ActivityCaseHistory;
import com.example.medicalappadmin.PenDriver.LiveData.PenStatusLiveData;
import com.example.medicalappadmin.PrescriptionActivity;
import com.example.medicalappadmin.R;


public class HomeFragment extends Fragment {

    public interface CallBacksListener{
        void onPenIconClicked();
    }

    private CallBacksListener listener;
    LinearLayout btnAddNewPatient;
    LinearLayout btnCaseHistory;
    ImageButton penButton;

    public HomeFragment(Context context) {
        if (context instanceof  CallBacksListener){
            listener = (CallBacksListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        btnAddNewPatient  = view.findViewById(R.id.btnAddNewPatient);
        btnCaseHistory  = view.findViewById(R.id.btnCaseHistory);
        penButton = view.findViewById(R.id.penStatusBtn);
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


    }
}