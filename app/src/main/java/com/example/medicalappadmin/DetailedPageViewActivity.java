package com.example.medicalappadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.databinding.ActivityDetailedPageViewBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.ViewCaseRP;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class DetailedPageViewActivity extends AppCompatActivity {
    ActivityDetailedPageViewBinding binding;
    Page page;
    int finalCurrPageNo;

    ArrayList<Page> pages;

    ViewCaseRP viewCaseRP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailedPageViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String caseID = getIntent().getStringExtra("CASE_ID");
        String pageNumber = getIntent().getStringExtra("CURRENT_PAGE_NUMBER");
        finalCurrPageNo = Integer.parseInt(pageNumber);
        loadPage(finalCurrPageNo,caseID);

        binding.backBtn.setOnClickListener(view -> finish());

    }

    private void loadPage(int finalCurrPageNo, String caseID) {
        APIMethods.viewCase(DetailedPageViewActivity.this, caseID, new APIResponseListener<ViewCaseRP>() {
            @Override
            public void success(ViewCaseRP response) {
                viewCaseRP = response;
                page = response.getPages().get(finalCurrPageNo);
                updateUI(page);
                setListeners();
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                binding.pbPage.setVisibility(View.GONE);
                Log.i("lol", "fail: " + message);
                Methods.showError(DetailedPageViewActivity.this,message,true);
            }
        });

    }

    private void setListeners() {
        binding.nextPage.setOnClickListener(view -> {
            finalCurrPageNo ++;
            if(finalCurrPageNo < viewCaseRP.getPages().size()){
                Log.i("lol", "setListeners: next " + finalCurrPageNo );
                Page nextPage = viewCaseRP.getPages().get(finalCurrPageNo);
                Log.i("lol", "setListeners:next  page " + nextPage.toString());
                updateUI(nextPage);
            }
            else{
                Toast.makeText(this, "End of pages", Toast.LENGTH_SHORT).show();
            }
        });
        binding.prevPage.setOnClickListener(view -> {
            finalCurrPageNo --;
            if(finalCurrPageNo >=0){
                Page prevPage = viewCaseRP.getPages().get(finalCurrPageNo);
                updateUI(prevPage);
            }
            else{
                Toast.makeText(this, "First Page", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateUI(Page page) {
        binding.detailedPage.clearDrawing();
        binding.detailedPage.addCoordinates(page.getPoints());
        binding.tvPageNumber.setText("Page No: "+page.getPageNumber());
        binding.pbPage.setVisibility(View.GONE);

    }
}