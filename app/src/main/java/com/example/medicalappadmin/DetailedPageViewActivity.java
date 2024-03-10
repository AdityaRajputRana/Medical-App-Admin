package com.example.medicalappadmin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.adapters.PageAdapter;
import com.example.medicalappadmin.databinding.ActivityDetailedPageViewBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.requests.EmptyReq;
import com.example.medicalappadmin.rest.response.ConfigurePageRP;
import com.example.medicalappadmin.rest.response.ViewCaseRP;
import com.google.gson.Gson;

import java.util.ArrayList;

public class DetailedPageViewActivity extends AppCompatActivity {
    ActivityDetailedPageViewBinding binding;
    Page page;
    int finalCurrPageNo;


    ViewCaseRP viewCaseRP;

    PageAdapter adapter;
    private ConfigurePageRP pageConfigurations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailedPageViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(view -> finish());

        String caseID = getIntent().getStringExtra("CASE_ID");
        String pageNumber = getIntent().getStringExtra("CURRENT_PAGE_NUMBER");
        String caseRP = getIntent().getStringExtra("CASE");

        viewCaseRP = new Gson().fromJson(caseRP, ViewCaseRP.class);
        finalCurrPageNo = Integer.parseInt(pageNumber);

        loadPage(finalCurrPageNo, caseID);
        setListeners();
    }


    private void loadPage(int finalCurrPageNo, String caseID) {
        if (viewCaseRP != null && viewCaseRP.get_id().equals(caseID)) {
            updateUI(finalCurrPageNo);
            return;
        }

        APIMethods.viewCase(DetailedPageViewActivity.this, caseID, new APIResponseListener<ViewCaseRP>() {
            @Override
            public void success(ViewCaseRP response) {
                viewCaseRP = response;
                updateUI(finalCurrPageNo);
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                binding.pbPage.setVisibility(View.GONE);
                Methods.showError(DetailedPageViewActivity.this, message, true);
            }
        });

    }

    private void setListeners() {
        binding.nextPage.setOnClickListener(view -> {
            if (finalCurrPageNo < viewCaseRP.getPageNumbers().size()-1) {
                finalCurrPageNo++;
                binding.viewPager.setCurrentItem(finalCurrPageNo);
            } else {
                Toast.makeText(this, "End of pages", Toast.LENGTH_SHORT).show();
            }
        });
        binding.prevPage.setOnClickListener(view -> {
            if (finalCurrPageNo > 0) {
                finalCurrPageNo--;
                binding.viewPager.setCurrentItem(finalCurrPageNo);
            } else {
                Toast.makeText(this, "First Page", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateUI(int currPageNo) {
        if (currPageNo != 0) {
            binding.viewPager.setCurrentItem(currPageNo);
        }
        if (adapter == null) {
            adapter = new PageAdapter(DetailedPageViewActivity.this, viewCaseRP, currPageNo);
        }
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                finalCurrPageNo = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.viewPager.setCurrentItem(currPageNo);

        binding.pbPage.setVisibility(View.GONE);
    }


}