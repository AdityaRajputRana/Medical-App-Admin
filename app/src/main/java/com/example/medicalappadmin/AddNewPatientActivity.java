package com.example.medicalappadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.medicalappadmin.Models.Patient;
import com.example.medicalappadmin.Tools.Const;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.databinding.ActivityAddNewPatientBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.ViewPatientRP;

public class AddNewPatientActivity extends AppCompatActivity {

    ActivityAddNewPatientBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewPatientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //Todo: Make it perform actions and pre inflate data
        processFilter();
        setListeners();
    }

    boolean isPagePatientLinkEnabled = false;
    private void processFilter() {
        String filter = getIntent().getStringExtra(Const.patientFilter);
        if (filter == null) return;

        if (filter.contains(Const.patientFilterLinkPageAndPatient)){
            isPagePatientLinkEnabled = true;
        }
    }

    private void setListeners() {
        binding.addPatientBtn.setOnClickListener(view->addPatient());
        binding.backBtn.setOnClickListener(view -> finish());
    }

    private void addPatient() {
        if (checkMissingDetails()){
            Toast.makeText(this, "Please Fill the missing details!", Toast.LENGTH_SHORT).show();
            return;
        }

        Patient patient = new Patient();
        patient.setFullName(binding.etPatientName.getText().toString());
        patient.setMobileNumber(Long.valueOf(binding.etMobileNumber.getText().toString()));
        patient.setGender(binding.genderRadioGroup.getCheckedRadioButtonId() == R.id.genderMaleBtn? "M":"F");
        patient.setFullName(binding.etPatientName.getText().toString());
        patient.setAge(Integer.valueOf(binding.etPatientAge.getText().toString()));

        uploadPatientData(patient);
    }

    private void startProgress(){
        binding.addPatientBtn.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void stopProgress(){
        binding.addPatientBtn.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void uploadPatientData(Patient patient) {
        startProgress();
        APIMethods.addNewPatient(this, patient, new APIResponseListener<ViewPatientRP>() {
            @Override
            public void success(ViewPatientRP response) {
                stopProgress();
                Toast.makeText(AddNewPatientActivity.this, "Patient Added Successfully", Toast.LENGTH_SHORT).show();
                if (isPagePatientLinkEnabled){
                    Intent intent = new Intent();
                    intent.putExtra("SELECTED_PATIENT_ID", response.getPatientDetails().get_id());
                    setResult(RESULT_OK, intent);
                    finish();
                    return;
                }
                Intent intent = new Intent(AddNewPatientActivity.this, ActivityPatientDetails.class);
                intent.putExtra("PATIENT_ID", response.getPatientDetails().get_id());
                AddNewPatientActivity.this.startActivity(intent);
                AddNewPatientActivity.this.finish();
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                stopProgress();
                Methods.showError(AddNewPatientActivity.this, message, true);
            }
        });
    }

    private boolean checkMissingDetails() {
        boolean areDetailsMissing = false;
        if (binding.etMobileNumber.getText().toString().length() != 10){
            binding.etMobileNumber.setError("Mobile Numbers Must be 10 digits");
            areDetailsMissing = true;
        } else {
            binding.etMobileNumber.setError(null);
        }

        if (binding.etPatientName.getText().toString().isEmpty()){
            binding.etPatientName.setError("Name of Patient is Required");
            areDetailsMissing = true;
        } else {
            binding.etPatientName.setError(null);
        }

        if (binding.etPatientAge.getText().toString().isEmpty()){
            binding.etPatientAge.setError("Age of Patient isRequired");
            areDetailsMissing = true;
        } else {
            binding.etPatientAge.setError(null);
        }

        return areDetailsMissing;
    }
}