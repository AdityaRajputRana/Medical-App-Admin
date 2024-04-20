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
    TextView tvVPCaseName, detailTxt, lastSeenTxt;
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
        detailTxt = view.findViewById(R.id.caseDetails);
        lastSeenTxt = view.findViewById(R.id.lastSeenTxt);
        ivVPShareCase = view.findViewById(R.id.ivVPShareCase);
        llVPClick = view.findViewById(R.id.llVPClick);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String details = "Pages - ";
        details += String.valueOf(viewPatientRP.getPatientCases().get(position).getPageCount());
        details += " | ";
        details += viewPatientRP.getPatientCases().get(position).isOpen()?"Open":"Close";
        details += " Case";
        detailTxt.setText(details);

        String lastSeen = "Last Updated -  ";
        lastSeen +=  Methods.convertDate(viewPatientRP.getPatientCases().get(position).getUpdatedAt());
        lastSeenTxt.setText(lastSeen);

        tvVPCaseName.setText("Untitled Case");
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
