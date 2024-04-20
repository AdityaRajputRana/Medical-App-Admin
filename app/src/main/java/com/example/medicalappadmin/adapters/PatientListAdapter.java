package com.example.medicalappadmin.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappadmin.R;
import com.example.medicalappadmin.rest.response.PatientListRP;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.ViewHolder> {

    PatientListRP patientListRP;
    PatientListener listener;
    Context context;


    public PatientListAdapter(PatientListRP patientListRP, Context context, PatientListener listener) {
        this.patientListRP = patientListRP;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.patient_history_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tvPatientsName.setText(patientListRP.getPatients().get(position).getFullName());
        holder.tvPatientsMobileNumber.setText(String.valueOf(patientListRP.getPatients().get(position).getMobileNumber()));
        holder.itemView.setOnClickListener(view ->
            listener.onPatientClicked(patientListRP.getPatients().get(position).get_id())
        );

    }

    @Override
    public int getItemCount() {
        return patientListRP.getPatients().size();
    }


    public interface PatientListener {
        default void onPatientClicked(String id) {
        }
        default void onShareClicked() {
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientsMobileNumber;
        TextView tvPatientsName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientsMobileNumber = itemView.findViewById(R.id.tvPatientsLMobileNumber);
            tvPatientsName = itemView.findViewById(R.id.tvPatientsLName);
        }
    }
}
