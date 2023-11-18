package com.example.medicalappadmin.adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappadmin.Models.Case;
import com.example.medicalappadmin.R;
import com.example.medicalappadmin.rest.response.CaseHistoryRP;


public class CaseHistoryRVAdapter extends RecyclerView.Adapter<CaseHistoryRVAdapter.ViewHolder>{

    private CaseHistoryRP caseHistoryRP;
    private Context context;
    private  Listener listener;

    TextView tvName;
    TextView tvMobileNumber;
    TextView tvPages;

    ImageView ivShareCase;

    public CaseHistoryRVAdapter(CaseHistoryRP caseHistoryRP, Context context, Listener listener) {
        this.caseHistoryRP = caseHistoryRP;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.case_history_item,parent,false);
        tvName = view.findViewById(R.id.tvPatientName);
        tvMobileNumber = view.findViewById(R.id.tvPatientMobileNumber);
        tvPages = view.findViewById(R.id.tvPages);
        ivShareCase = view.findViewById(R.id.ivShareCase);
        return new ViewHolder(view);

    }

    public interface  Listener {
        default void onShareClicked(String caseId) {

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
    }

    @Override
    public int getItemCount() {
        return caseHistoryRP.getCases().size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}
