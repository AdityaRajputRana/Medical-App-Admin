package com.example.medicalappadmin.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappadmin.Models.Case;
import com.example.medicalappadmin.R;
import com.example.medicalappadmin.rest.response.CaseHistoryRP;

import org.w3c.dom.Text;

public class CaseHistoryRVAdapter extends RecyclerView.Adapter<CaseHistoryRVAdapter.ViewHolder>{

    private CaseHistoryRP caseHistoryRP;
    private Context context;

    TextView tvName;
    TextView tvMobileNumber;
    TextView tvPages;

    public CaseHistoryRVAdapter(CaseHistoryRP caseHistoryRP, Context context) {
        this.caseHistoryRP = caseHistoryRP;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.case_history_item,parent,false);
        tvName = view.findViewById(R.id.tvPatientName);
        tvMobileNumber = view.findViewById(R.id.tvPatientMobileNumber);
        tvPages = view.findViewById(R.id.tvPages);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String TAG = "ADi rcv";
            for(int i=0; i<caseHistoryRP.getCases().size(); i++){
                Log.i(TAG, "id " + caseHistoryRP.getCases().get(i).get_id());
                Log.i(TAG, "pages "+caseHistoryRP.getCases().get(i).getPageCount());
                Log.i(TAG, "updated at "+ caseHistoryRP.getCases().get(i).getUpdatedAt());
                tvPages.setText(String.valueOf(caseHistoryRP.getCases().get(i).getPageCount()));
                tvName.setText(caseHistoryRP.getCases().get(i).get_id());
                tvMobileNumber.setText(caseHistoryRP.getCases().get(i).getUpdatedAt());
            }
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
