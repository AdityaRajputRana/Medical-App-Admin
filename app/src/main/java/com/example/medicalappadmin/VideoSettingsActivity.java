package com.example.medicalappadmin;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.adapters.GuidesAdapter;
import com.example.medicalappadmin.databinding.ActivityVideoSettingsBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.GuidesVideosRP;

public class VideoSettingsActivity extends AppCompatActivity {

    private ActivityVideoSettingsBinding binding;
    LinearLayoutManager manager;
    GuidesAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        manager = new LinearLayoutManager(this);
        setListeners();
        loadGuides();




    }

    private void setListeners() {
        binding.ivBackBtnVideos.setOnClickListener(view -> finish());
        binding.addGuideBtn.setOnClickListener(view -> {

        });
    }

    private void loadGuides() {
        APIMethods.listGuidesVideos(VideoSettingsActivity.this, new APIResponseListener<GuidesVideosRP>() {
            @Override
            public void success(GuidesVideosRP response) {
                setUpRV(response);
                binding.pbGuides.setVisibility(View.GONE);
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                Methods.showError(VideoSettingsActivity.this,message,cancellable);
                binding.pbGuides.setVisibility(View.GONE);

            }
        });
    }

    private void setUpRV(GuidesVideosRP response) {

        binding.rcvGuides.setLayoutManager(manager);
        if(adapter == null){
            adapter = new GuidesAdapter(response,VideoSettingsActivity.this);
        }
        binding.rcvGuides.setAdapter(adapter);

    }
}