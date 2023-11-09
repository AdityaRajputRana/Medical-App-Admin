package com.example.medicalappadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.medicalappadmin.adapters.CaseHistoryRVAdapter;
import com.example.medicalappadmin.databinding.ActivityCaseHistoryBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.CaseHistoryRP;
import com.example.medicalappadmin.rest.response.EmptyRP;

public class ActivityCaseHistory extends AppCompatActivity {

    int currentPage = 1;
    int totalPages=1;
    ActivityCaseHistoryBinding binding;
    private CaseHistoryRVAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityCaseHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadCases(currentPage,totalPages);

        binding.nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    currentPage++;
                    binding.pbCaseHistory.setVisibility(View.VISIBLE);
                    loadCases(currentPage, totalPages);
                }
            }
        });






    }

    private void loadCases(int cPage, int tPages) {
        if(cPage > tPages){
            binding.pbCaseHistory.setVisibility(View.GONE);
            Toast.makeText(this, "That's all", Toast.LENGTH_SHORT).show();
            return;
        }
        APIMethods.loadCaseHistory(this, cPage, new APIResponseListener<CaseHistoryRP>() {
            @Override
            public void success(CaseHistoryRP response) {
                totalPages = response.getTotalPages();
                if(currentPage == totalPages){
                    binding.pbCaseHistory.setVisibility(View.GONE);
                    Toast.makeText(ActivityCaseHistory.this, "That's all", Toast.LENGTH_SHORT).show();
                }
                adapter = new CaseHistoryRVAdapter(response,ActivityCaseHistory.this);
                binding.rcvCaseHistory.setLayoutManager(new LinearLayoutManager(ActivityCaseHistory.this));
                binding.rcvCaseHistory.setAdapter(adapter);
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {

            }
        });
    }

}