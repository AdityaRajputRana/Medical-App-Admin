package com.example.medicalappadmin.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.medicalappadmin.LoginActivity;
import com.example.medicalappadmin.Models.User;
import com.example.medicalappadmin.PenDriver.LiveData.PenStatusLiveData;
import com.example.medicalappadmin.R;
import com.example.medicalappadmin.Tools.CacheUtils;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.Transformations.CircleTransformation;
import com.example.medicalappadmin.VideoSettingsActivity;
import com.example.medicalappadmin.databinding.FragmentProfileBinding;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    User user;
    FragmentProfileBinding binding;
    Boolean isPenConnected = false;

    public interface CallBacksListener{
        void startConnectionRoutine();
        void disconnectFromPen();
    }

    private CallBacksListener listener;


    public ProfileFragment() {
    }

    public ProfileFragment(CallBacksListener listener) {
        this.listener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding == null){
            binding = FragmentProfileBinding.inflate(inflater);
            loadData();
            loadUI();
            setListeners();
        }
        return binding.getRoot();
    }

    private void loadData() {
        user = new Gson().fromJson(
                getActivity().getSharedPreferences("MY_PREF", MODE_PRIVATE).getString("MY_USER", "{}"),
                User.class
        );
    }

    private void setListeners() {
        binding.logOutBtn.setOnClickListener(view -> confirmLogout());
        PenStatusLiveData.getPenStatusLiveData().getIsConnected()
                .observe(getViewLifecycleOwner(), isConnected->{
                    isPenConnected = isConnected;
                    updatePenConnectionStatus();
                });

        binding.penActionBtn.setOnClickListener(view->{
            if (isPenConnected)
                listener.disconnectFromPen();
            else
                listener.startConnectionRoutine();
        });

        binding.videoSettingsBtn.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), VideoSettingsActivity.class));
        });
    }

    private void updatePenConnectionStatus() {
        isPenConnected = PenStatusLiveData.getPenStatusLiveData().getIsConnected().getValue();
        int colorId = isPenConnected ? R.color.white : R.color.error700;
        int bgColor = isPenConnected ? R.color.success500 : R.color.error50;
        binding.penActionBtn.setBackgroundColor(getActivity().getColor(bgColor));
        binding.penActionBtn.setForegroundTintList(ColorStateList.valueOf(getActivity().getColor(bgColor)));
    }

    private void loadUI() {
        binding.nameTxt.setText(user.getName());
        binding.staffTypeTxt.setText(user.getType());

        updatePenConnectionStatus();
    }

    private void confirmLogout(){
        new AlertDialog.Builder(getActivity())
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to log out of the app?")
                .setPositiveButton("Logout", (dialogInterface, i) -> logout())
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .setCancelable(true)
                .show();
    }

    private void logout() {
        SharedPreferences.Editor editor = getActivity()
                .getSharedPreferences("MY_PREF", MODE_PRIVATE)
                .edit();
        editor.remove("JWT_TOKEN");
        editor.remove("MY_USER");
        editor.commit();
        CacheUtils.clearCache(getActivity());

        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }
}