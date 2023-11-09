package com.example.medicalappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.medicalappadmin.adapters.CaseHistoryRVAdapter;
import com.example.medicalappadmin.databinding.ActivityCaseHistoryBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.CaseHistoryRP;

public class ActivityCaseHistory extends AppCompatActivity {

    int currentPage = 1;
    int totalPages=Integer.MAX_VALUE;
    ActivityCaseHistoryBinding binding;
    private CaseHistoryRVAdapter adapter;
    LinearLayoutManager manager;

    private CaseHistoryRP caseHistoryRP;
    private ItemTouchHelper.Callback callback;
    private ItemTouchHelper itemTouchHelper;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityCaseHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        manager = new LinearLayoutManager(ActivityCaseHistory.this);
        binding.rcvCaseHistory.setVisibility(View.GONE);

        loadCases(currentPage,totalPages);



        binding.ivBackBtn.setOnClickListener(view -> {
            finish();
        });

//        binding.nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
//                    currentPage++;
//                    binding.pbCaseHistory.setVisibility(View.VISIBLE);
//                    loadCases(currentPage, totalPages);
//                }
//            }
//        });

        binding.rcvCaseHistory.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                        if (manager.findFirstCompletelyVisibleItemPosition() == 0){
                            if (binding.llTopStrip.getVisibility() == View.VISIBLE){
                                binding.llTopStrip.setVisibility(View.INVISIBLE);
                            }
                        }

                        if (manager.findFirstCompletelyVisibleItemPosition() == 1){
                            if (binding.llTopStrip.getVisibility() != View.VISIBLE){
                                binding.llTopStrip.setVisibility(View.VISIBLE);
                            }
                        }

                        if (manager.findLastCompletelyVisibleItemPosition() == loadedCases-1){
                            if (binding.pbCaseHistory.getVisibility() != View.VISIBLE) {
                                currentPage++;
                                loadCases(currentPage, totalPages);
                            }
                        }
            }
        });


        //TODO: implement merge cases














    }


    private boolean shouldMergeItems(int fromPosition, int toPosition) {
        // Get the bounds of the items being moved
        Rect fromBounds = new Rect();
        binding.rcvCaseHistory.getLayoutManager().findViewByPosition(fromPosition).getGlobalVisibleRect(fromBounds);

        Rect toBounds = new Rect();
        binding.rcvCaseHistory.getLayoutManager().findViewByPosition(toPosition).getGlobalVisibleRect(toBounds);

        // Check if the bounds overlap vertically
        return Rect.intersects(fromBounds, toBounds);
    }

    private int loadedCases = -1;

    private void loadCases(int cPage, int tPages) {
        if(cPage > tPages){
            binding.pbCaseHistory.setVisibility(View.GONE);
            Toast.makeText(this, "That's all", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.pbCaseHistory.setVisibility(View.VISIBLE);
        if (caseHistoryRP != null)
            binding.rcvCaseHistory.smoothScrollToPosition(caseHistoryRP.getCases().size() -1);
        APIMethods.loadCaseHistory(this, cPage, new APIResponseListener<CaseHistoryRP>() {
            @Override
            public void success(CaseHistoryRP response) {


                if (caseHistoryRP == null){
                    caseHistoryRP = response;
                } else {
                    caseHistoryRP.getCases().addAll(response.getCases());
                    caseHistoryRP.setTotalPages(response.getTotalPages());
                    caseHistoryRP.setCurrentPage(response.getCurrentPage());
                }

                if (loadedCases == -1){
                    loadedCases = 0;
                }
                loadedCases += response.getCases().size();

                binding.rcvCaseHistory.setVisibility(View.VISIBLE);
                totalPages = response.getTotalPages();

                if (adapter == null) {
                    adapter = new CaseHistoryRVAdapter(response, ActivityCaseHistory.this);
                    binding.rcvCaseHistory.setLayoutManager(manager);
                    binding.rcvCaseHistory.setAdapter(adapter);
                    callback = new ItemTouchHelperCallback(binding.rcvCaseHistory, response, ActivityCaseHistory.this);
                    itemTouchHelper = new ItemTouchHelper(callback);
                    itemTouchHelper.attachToRecyclerView(binding.rcvCaseHistory);
                } else {
                    adapter.notifyItemRangeInserted(caseHistoryRP.getCases().size() - response.getCases().size() + 1, response.getCases().size());
                    itemTouchHelper.attachToRecyclerView(binding.rcvCaseHistory);
                }
                binding.pbCaseHistory.setVisibility(View.GONE);
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {

            }
        });
    }

}