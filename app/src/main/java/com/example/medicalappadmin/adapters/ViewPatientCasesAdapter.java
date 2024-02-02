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
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.rest.response.ViewPatientRP;

public class ViewPatientCasesAdapter extends RecyclerView.Adapter<ViewPatientCasesAdapter.ViewHolder> {

    ViewPatientRP viewPatientRP;
    Context context;

    TextView tvVPNoOfCases;
    TextView tvVPStatus;
    TextView tvVPDetails;
    TextView tvVPCaseName;
    ImageView ivVPShareCase;
    LinearLayout llVPClick;
    VPCaseListener listener;

    public ViewPatientCasesAdapter(ViewPatientRP viewPatientRP, Context context,VPCaseListener listener) {
        this.viewPatientRP = viewPatientRP;
        this.context = context;
        this.listener = listener;
    }

    public interface VPCaseListener{
        default void caseClicked(String caseId){}
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
        llVPClick = view.findViewById(R.id.llVPClick);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        tvVPNoOfCases.setText(String.valueOf(viewPatientRP.getPatientCases().get(position).getPageCount()));
        tvVPCaseName.setText("Untitled Case");
        tvVPDetails.setText(Methods.convertDate(viewPatientRP.getPatientCases().get(position).getUpdatedAt()));
        if (viewPatientRP.getPatientCases().get(position).isOpen()) {
            tvVPStatus.setText("Open");
            tvVPStatus.setTextColor(context.getColor(R.color.colorActiveGreen));
        } else {
            tvVPStatus.setText("Closed");
            tvVPStatus.setTextColor(context.getColor(R.color.colorDanger));
        }

        llVPClick.setOnClickListener(view -> {
            listener.caseClicked(viewPatientRP.getPatientCases().get(position).get_id());
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
