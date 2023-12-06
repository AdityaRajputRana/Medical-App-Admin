package com.example.medicalappadmin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappadmin.R;
import com.example.medicalappadmin.rest.response.CaseHistoryRP;


public class CaseHistoryRVAdapter extends RecyclerView.Adapter<CaseHistoryRVAdapter.ViewHolder> {

    private CaseHistoryRP caseHistoryRP;
    private Context context;
    private CaseListener listener;

    TextView tvName;
    TextView tvMobileNumber;
    TextView tvPages;
    LinearLayout llCaseItem;


    ImageView ivShareCase;

    public CaseHistoryRVAdapter(CaseHistoryRP caseHistoryRP, Context context, CaseListener listener) {
        this.caseHistoryRP = caseHistoryRP;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.case_history_item, parent, false);
        tvName = view.findViewById(R.id.tvPatientName);
        tvMobileNumber = view.findViewById(R.id.tvPatientMobileNumber);
        tvPages = view.findViewById(R.id.tvPages);
        ivShareCase = view.findViewById(R.id.ivShareCase);
        llCaseItem = view.findViewById(R.id.llCaseItem);
        return new ViewHolder(view);

    }

    public interface CaseListener {
        default void onShareClicked(String caseId) {

        }

        default void onCaseClicked(String caseId) {

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        tvPages.setText(String.valueOf(caseHistoryRP.getCases().get(position).getPageCount()));
        tvName.setText(caseHistoryRP.getCases().get(position).getFullName());
        tvMobileNumber.setText(caseHistoryRP.getCases().get(position).getMobileNumber());
        ivShareCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onShareClicked((caseHistoryRP.getCases().get(holder.getAdapterPosition()).get_id()));
            }
        });

        llCaseItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCaseClicked(caseHistoryRP.getCases().get(position).get_id());
            }
        });


    }

    @Override
    public int getItemCount() {
        return caseHistoryRP.getCases().size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}
