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
import com.example.medicalappadmin.rest.response.PatientListRP;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.ViewHolder> {

    PatientListRP patientListRP;
    PatientListener listener;
    Context context;

    TextView tvPatientsMobileNumber;
    TextView tvPatientsName;
    LinearLayout llPatientItem;
    ImageView ivSharePatient;


    public PatientListAdapter(PatientListRP patientListRP, Context context, PatientListener listener) {
        this.patientListRP = patientListRP;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.patient_history_item,parent,false);
        tvPatientsMobileNumber = view.findViewById(R.id.tvPatientsLMobileNumber);
        tvPatientsName = view.findViewById(R.id.tvPatientsLName);
        llPatientItem = view.findViewById(R.id.llPatientItem);
        ivSharePatient = view.findViewById(R.id.ivSharePatient);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        tvPatientsName.setText(patientListRP.getPatients().get(position).getFullName());
        tvPatientsMobileNumber.setText(String.valueOf(patientListRP.getPatients().get(position).getMobileNumber()));
        llPatientItem.setOnClickListener(view -> {
            listener.onPatientClicked(patientListRP.getPatients().get(holder.getAdapterPosition()).get_id());
        });

        ivSharePatient.setOnClickListener(view -> {
            listener.onShareClicked();
        });

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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
