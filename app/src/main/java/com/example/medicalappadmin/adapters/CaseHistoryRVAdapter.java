package com.example.medicalappadmin.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.medicalappadmin.CaseDetailsActivity;
import com.example.medicalappadmin.DetailedPageViewActivity;
import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.R;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.databinding.BsheetPreviewCaseBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.CaseHistoryRP;
import com.example.medicalappadmin.rest.response.ViewCaseRP;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.util.ArrayList;


public class CaseHistoryRVAdapter extends RecyclerView.Adapter<CaseHistoryRVAdapter.ViewHolder> {

    private CaseHistoryRP caseHistoryRP;
    private Context context;
    private CaseListener listener;
    private boolean isSelectorMode;



    public CaseHistoryRVAdapter(CaseHistoryRP caseHistoryRP, Context context, CaseListener listener, Boolean isSelectorMode) {
        this.caseHistoryRP = caseHistoryRP;
        this.context = context;
        this.listener = listener;
        this.isSelectorMode = isSelectorMode;
    }

    public CaseHistoryRVAdapter(CaseHistoryRP caseHistoryRP, Context context, CaseListener listener) {
        this.caseHistoryRP = caseHistoryRP;
        this.context = context;
        this.listener = listener;
        isSelectorMode = false;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.case_history_item, parent, false);
        return new ViewHolder(view);

    }

    public interface CaseListener {
        default void onShareClicked(String caseId) {

        }

        default void openCase(String caseId) {

        }

        void submitCaseToPatient(String caseId);

        default void onCaseSelected(String caseId){}
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvPages.setText(String.valueOf(caseHistoryRP.getCases().get(position).getPageCount()));
        holder.tvName.setText(caseHistoryRP.getCases().get(position).getFullName());
        holder.tvMobileNumber.setText(caseHistoryRP.getCases().get(position).getMobileNumber());

        if (isSelectorMode){
            holder.tvName.setText("Last Update: " + Methods.convertDate(caseHistoryRP.getCases().get(position).getUpdatedAt()));
            holder.tvMobileNumber.setText("Case ID: " + caseHistoryRP.getCases().get(position).get_id());
            holder.submitCaseBtn.setImageDrawable(context.getDrawable(R.drawable.baseline_remove_red_eye_24));
            holder.submitCaseBtn.setOnClickListener(view -> previewCase(caseHistoryRP.getCases().get(holder.getBindingAdapterPosition()).get_id()));
            holder.sharePDFBtn.setImageDrawable(context.getDrawable(R.drawable.baseline_open_in_new_24));
            holder.sharePDFBtn.setOnClickListener(view->listener.openCase(caseHistoryRP.getCases().get(holder.getBindingAdapterPosition()).get_id()));
            holder.itemView.setOnClickListener(view->listener.onCaseSelected(caseHistoryRP.getCases().get(holder.getBindingAdapterPosition()).get_id()));
        } else {
            holder.submitCaseBtn.setOnClickListener(view ->listener.submitCaseToPatient((caseHistoryRP.getCases().get(holder.getBindingAdapterPosition()).get_id())));
            holder.sharePDFBtn.setOnClickListener(v->listener.onShareClicked(caseHistoryRP.getCases().get(holder.getBindingAdapterPosition()).get_id()));
            holder.llCaseItem.setOnClickListener(view -> listener.openCase(caseHistoryRP.getCases().get(holder.getBindingAdapterPosition()).get_id()));
        }
    }

    private void previewCase(String caseId) {
        BottomSheetDialog previewCaseDialog = new BottomSheetDialog(context);
        Activity activity = (Activity) context;
        BsheetPreviewCaseBinding previewCaseBinding = BsheetPreviewCaseBinding.inflate(activity.getLayoutInflater());
        previewCaseDialog.setContentView(previewCaseBinding.getRoot());
        previewCaseDialog.show();
        APIMethods.viewCase(context, caseId, new APIResponseListener<ViewCaseRP>() {
            @Override
            public void success(ViewCaseRP viewCaseRP) {
               previewCaseBinding.progressBar.setVisibility(View.GONE);
                LinearLayoutManager manager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                PagesHistoryAdapter adapter = new PagesHistoryAdapter(viewCaseRP, activity, new PagesHistoryAdapter.PageListener() {
                    @Override
                    public void onPageClicked(ArrayList<Page> pages, int currentposition) {
                        Intent i = new Intent(activity, DetailedPageViewActivity.class);
                        String caseRP = new Gson().toJson(viewCaseRP);
                        i.putExtra("CASE_ID", viewCaseRP.get_id());
                        i.putExtra("CASE", caseRP);
                        i.putExtra("CURRENT_PAGE_NUMBER", String.valueOf(currentposition));
                        activity.startActivity(i);
                    }
                }, true);
                previewCaseBinding.pagesRV.setLayoutManager(manager);
                previewCaseBinding.pagesRV.setAdapter(adapter);
                SnapHelper snapHelper = new LinearSnapHelper();
                snapHelper.attachToRecyclerView(previewCaseBinding.pagesRV);
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                Methods.showError((Activity) context, message, false);
                previewCaseDialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return caseHistoryRP.getCases().size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvMobileNumber;
        TextView tvPages;
        LinearLayout llCaseItem;
        ImageView sharePDFBtn;
        ImageView submitCaseBtn;

        public ViewHolder(@NonNull View view) {
            super(view);
            tvName = view.findViewById(R.id.tvPatientName);
            tvMobileNumber = view.findViewById(R.id.tvPatientMobileNumber);
            tvPages = view.findViewById(R.id.tvPages);
            sharePDFBtn = view.findViewById(R.id.sharePDFBtn);
            submitCaseBtn = view.findViewById(R.id.submitCasePDF);
            llCaseItem = view.findViewById(R.id.llCaseItem);
        }
    }


}
