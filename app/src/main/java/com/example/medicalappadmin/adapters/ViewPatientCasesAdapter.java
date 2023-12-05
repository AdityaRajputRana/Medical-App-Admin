package com.example.medicalappadmin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappadmin.R;
import com.example.medicalappadmin.rest.response.ViewPatientRP;

public class ViewPatientCasesAdapter extends RecyclerView.Adapter<ViewPatientCasesAdapter.ViewHolder> {

    ViewPatientRP viewPatientRP;
    Context context;

    TextView tvVPNoOfCases;
    TextView tvVPStatus;
    TextView tvVPDetails;
    TextView tvVPCaseName;
    ImageView ivVPShareCase;

    public ViewPatientCasesAdapter(ViewPatientRP viewPatientRP, Context context) {
        this.viewPatientRP = viewPatientRP;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_patient_cases_item, parent, false);
        //Get the views

        tvVPCaseName = view.findViewById(R.id.tvVPCaseName);
        tvVPNoOfCases = view.findViewById(R.id.tvVPNoOfCases);
        tvVPStatus = view.findViewById(R.id.tvVPStatus);
        tvVPDetails = view.findViewById(R.id.tvVPDetails);
        ivVPShareCase = view.findViewById(R.id.ivVPShareCase);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        tvVPNoOfCases.setText(String.valueOf(viewPatientRP.getPatientCases().get(position).getPageCount()));
        tvVPCaseName.setText("Untitled Case");
        tvVPDetails.setText(viewPatientRP.getPatientCases().get(position).getUpdatedAt());
        if (viewPatientRP.getPatientCases().get(position).isOpen()) {
            tvVPStatus.setText("Open");
            tvVPStatus.setTextColor(context.getColor(R.color.colorActiveGreen));
        } else {
            tvVPStatus.setText("Closed");
            tvVPStatus.setTextColor(context.getColor(R.color.colorDanger));
        }
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
