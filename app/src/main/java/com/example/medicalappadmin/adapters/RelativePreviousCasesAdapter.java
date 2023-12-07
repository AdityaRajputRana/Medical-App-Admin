package com.example.medicalappadmin.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappadmin.R;
import com.example.medicalappadmin.rest.response.ViewPatientRP;

public class RelativePreviousCasesAdapter extends RecyclerView.Adapter<RelativePreviousCasesAdapter.ViewHolder> {


    Context context;
    ViewPatientRP viewPatientRP;
    TextView tvRelCaseDetails;
    TextView tvRelCaseName;
    TextView tvRelNoOfPages;
    LinearLayout llRelClick;
    SelectCaseListener listener;


    public RelativePreviousCasesAdapter(ViewPatientRP response,Context context, SelectCaseListener listener) {
        this.context =context;
        this.viewPatientRP = response;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rel_prev_cases,parent,false);
        llRelClick = view.findViewById(R.id.llRelClick);
        tvRelCaseDetails = view.findViewById(R.id.tvRelCaseDetails);
        tvRelCaseName = view.findViewById(R.id.tvRelCaseName);
        tvRelNoOfPages = view.findViewById(R.id.tvRelNoOfPages);
        return new ViewHolder(view);
    }

    public interface SelectCaseListener{
        default void onCaseSelected(String caseId){}
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        tvRelCaseName.setText("Untitled case");

        tvRelNoOfPages.setText(String.valueOf(viewPatientRP.getPatientCases().get(position).getPageCount()));
        tvRelCaseDetails.setText(viewPatientRP.getPatientCases().get(position).get_id());
        llRelClick.setOnClickListener(view -> {
            listener.onCaseSelected(viewPatientRP.getPatientCases().get(holder.getAdapterPosition()).get_id());
        });
    }

    @Override
    public int getItemCount() {
        return viewPatientRP.getPatientCases().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
