package com.example.medicalappadmin.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.medicalappadmin.ItemTouchHelperCallback;
import com.example.medicalappadmin.CaseDetailsActivity;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.adapters.CaseHistoryRVAdapter;
import com.example.medicalappadmin.databinding.FragmentCaseBinding;
import com.example.medicalappadmin.databinding.LoadingDialogBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.CaseHistoryRP;
import com.example.medicalappadmin.rest.response.GeneratePDFLinkRP;
import com.example.medicalappadmin.rest.response.SubmitCaseRP;

public class CasesHistoryFragment extends Fragment {

    FragmentCaseBinding binding;
    int currentPage = 1;
    int totalPages = Integer.MAX_VALUE;
    private CaseHistoryRVAdapter adapter;
    LinearLayoutManager manager;

    private CaseHistoryRP caseHistoryRP;
    private ItemTouchHelper.Callback callback;
    private ItemTouchHelper itemTouchHelper;
    private Activity activity;


    public CasesHistoryFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding == null){
            binding = FragmentCaseBinding.inflate(inflater);
            manager = new LinearLayoutManager(getActivity());
            binding.rcvCaseHistory.setVisibility(View.GONE);
            activity = getActivity();

            loadCases(currentPage, totalPages);


            binding.rcvCaseHistory.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {

                    if (manager.findFirstCompletelyVisibleItemPosition() == 0){
                        binding.titleCardView.setCardElevation(0);
                    } else {
                        binding.titleCardView.setCardElevation(10);
                    }
                    if (manager.findLastCompletelyVisibleItemPosition() == loadedCases - 1) {
                        if (binding.pbCaseHistory.getVisibility() != View.VISIBLE) {
                            currentPage++;
                            loadCases(currentPage, totalPages);
                        }
                    }
                }
            });


            setListeners();
        }
        return binding.getRoot();
    }


    private void setListeners() {
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

    private void saveToClipBoard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("CASE LINK", text);
        clipboard.setPrimaryClip(clip);
    }

    private void loadCases(int cPage, int tPages) {
        if (cPage > tPages) {
            binding.pbCaseHistory.setVisibility(View.GONE);
            return;
        }

        binding.pbCaseHistory.setVisibility(View.VISIBLE);
        if (caseHistoryRP != null)
            binding.rcvCaseHistory.smoothScrollToPosition(caseHistoryRP.getCases().size() - 1);
        APIMethods.loadCaseHistory(getActivity(), cPage, new APIResponseListener<CaseHistoryRP>() {
            @Override
            public void success(CaseHistoryRP response) {
                if(response.getCases().size() == 0){
                    binding.llData.setVisibility(View.GONE);
                    binding.llNoData.setVisibility(View.VISIBLE);
                }

                if (caseHistoryRP == null) {
                    caseHistoryRP = response;
                } else {
                    caseHistoryRP.getCases().addAll(response.getCases());
                    caseHistoryRP.setTotalPages(response.getTotalPages());
                    caseHistoryRP.setCurrentPage(response.getCurrentPage());
                }

                if (loadedCases == -1) {
                    loadedCases = 0;
                }
                loadedCases += response.getCases().size();

                binding.rcvCaseHistory.setVisibility(View.VISIBLE);
                totalPages = response.getTotalPages();

                if (adapter == null) {
                    adapter = new CaseHistoryRVAdapter(response, getActivity() == null? activity :getActivity(), new CaseHistoryRVAdapter.CaseListener() {
                        @Override
                        public void onShareClicked(String caseId) {
                            generateAndSharePDFLink(caseId);
                        }

                        @Override
                        public void openCase(String caseId) {
                            Intent i = new Intent(getActivity(), CaseDetailsActivity.class);
                            i.putExtra("CASE_ID", caseId);
                            startActivity(i);
                        }

                        @Override
                        public void submitCaseToPatient(String caseId) {
                            shareCaseToPatient(caseId);
                        }
                    });
                    binding.rcvCaseHistory.setLayoutManager(manager);
                    binding.rcvCaseHistory.setAdapter(adapter);
                    callback = new ItemTouchHelperCallback(binding.rcvCaseHistory, response, getActivity());
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


    private void generateAndSharePDFLink(String caseId) {
        AlertDialog dialog;
        LoadingDialogBinding loadingDialogBinding;
        loadingDialogBinding = LoadingDialogBinding.inflate(getLayoutInflater(), null, false);
        loadingDialogBinding.tvTitleAD.setText("Generating link. Please wait");
        dialog = new AlertDialog.Builder(getActivity())
                .setView(loadingDialogBinding.getRoot())
                .show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        APIMethods.generatePDFonServer(getActivity(), caseId, new APIResponseListener<GeneratePDFLinkRP>() {
            @Override
            public void success(GeneratePDFLinkRP response) {
                dialog.dismiss();
                saveToClipBoard(getActivity(), response.getPdfUrl());
                Toast.makeText(getActivity(), "Link copied to clipboard", Toast.LENGTH_SHORT).show();
                Methods.shareText(getActivity(), "Hi here is your prescription. Download चिकित्सा नुस्खे पर्ची App for detailed reports.\n\n"+response.getPdfUrl());
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Some error occurred while fetching case, Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shareCaseToPatient(String caseId) {
        AlertDialog dialog;
        LoadingDialogBinding loadingDialogBinding;
        loadingDialogBinding = LoadingDialogBinding.inflate(getLayoutInflater(), null, false);
        loadingDialogBinding.tvTitleAD.setText("Requesting to Shared with patient");
        dialog = new AlertDialog.Builder(getActivity())
                .setView(loadingDialogBinding.getRoot())
                .show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        APIMethods.submitCase(getActivity(), caseId, new APIResponseListener<SubmitCaseRP>() {
            @Override
            public void success(SubmitCaseRP response) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Prescription will be shared with patient soon!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Some error sharing prescription, " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading() {

    }
}