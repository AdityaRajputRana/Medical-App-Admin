package com.example.medicalappadmin;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.medicalappadmin.Models.Additional;
import com.example.medicalappadmin.Models.MetaData;
import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.adapters.AdditionalsRVAdapter;
import com.example.medicalappadmin.adapters.PagesHistoryAdapter;
import com.example.medicalappadmin.components.AudioPlayer;
import com.example.medicalappadmin.components.WebVideoPlayer;
import com.example.medicalappadmin.components.YTVideoPlayer;
import com.example.medicalappadmin.fragments.CaseDetails.CaseInfoFragment;
import com.example.medicalappadmin.fragments.CaseDetails.PrescriptionFragment;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.ViewCaseRP;
import com.example.medicalappadmin.databinding.ActivityCaseDetailsBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

public class CaseDetailsActivity extends AppCompatActivity {

    private ActivityCaseDetailsBinding binding;
    String caseId;
    PrescriptionFragment prescriptionFragment;
    CaseInfoFragment caseInfoFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCaseDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpUI();

        caseId = getIntent().getStringExtra("CASE_ID");

        if (caseId != null) {
            downloadCase(caseId);
        }

    }

    private void setUpUI() {
        prescriptionFragment = new PrescriptionFragment();
        caseInfoFragment = new CaseInfoFragment();
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        binding.viewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.backBtn.setOnClickListener(view->CaseDetailsActivity.this.finish());
    }

    private void downloadCase(String caseId) {

        APIMethods.viewCase(CaseDetailsActivity.this, caseId, new APIResponseListener<ViewCaseRP>() {
            @Override
            public void success(ViewCaseRP response) {
                updateUI(response);
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                Methods.showError(CaseDetailsActivity.this, message, false);
            }
        });


    }

    private void updateUI(ViewCaseRP response) {
        prescriptionFragment.setUI(response);
    }


    private class PagerAdapter extends FragmentPagerAdapter{

        public PagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return prescriptionFragment;
                case 1:
                    return caseInfoFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Prescription";
                case 1:
                    return "Case Info";
            }
            return super.getPageTitle(position);
        }
    }
}