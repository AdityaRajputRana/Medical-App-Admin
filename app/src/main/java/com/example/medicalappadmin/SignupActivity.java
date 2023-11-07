package com.example.medicalappadmin;

import static com.example.medicalappadmin.Tools.Methods.showToast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import com.example.medicalappadmin.Models.Hospital;
import com.example.medicalappadmin.Models.User;
import com.example.medicalappadmin.databinding.ActivitySignupBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.SignupRP;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(initialChecks()){
                    showToast(SignupActivity.this, "Fields are correct");
                    String fullName = binding.etFNameSignup.getText().toString()+" "+ binding.etLNameSignup.getText().toString();
                    Hospital hospital = new Hospital();
                    User user = new User(
                            fullName,
                            binding.etFNameSignup.getText().toString(),
                            binding.etLNameSignup.getText().toString(),
                            binding.etMobileSignup.getText().toString(),
                            binding.etEmailSignup.getText().toString(),
                            binding.etPasswordSignup.getText().toString(),
                            //dp
                            binding.etFNameSignup.getText().toString(),
                            //type
                            binding.etFNameSignup.getText().toString(),
                            //title
                            binding.etFNameSignup.getText().toString(),
                            hospital
                    );

                    //signup

//                    APIMethods.signUpStaff(SignupActivity.this, user, hospital, new APIResponseListener<SignupRP>() {
//                        @Override
//                        public void success(SignupRP response) {
//                            launchDashboardActivity();
//                        }
//
//                        @Override
//                        public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
//
//                        }
//                    });
                }
            }
        });


        binding.tvToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchLoginActivity();
            }
        });

    }

    private boolean initialChecks() {
        boolean isAllCorrect = true;
        if(binding.etFNameSignup.length() == 0){
            binding.etFNameSignup.setError("Can't be empty");
            isAllCorrect = false;
        }
        if(binding.etLNameSignup.length() == 0){
            binding.etLNameSignup.setError("Can't be empty");
            isAllCorrect = false;


        }
        if(binding.etEmailSignup.length() == 0){
            binding.etEmailSignup.setError("Can't be empty");
            isAllCorrect = false;

        }
        if(binding.etPasswordSignup.length() == 0){
            binding.etPasswordSignup.setError("Can't be empty");
            isAllCorrect = false;

        }
        if(binding.etRePasswordSignup.length() == 0){
            binding.etRePasswordSignup.setError("Can't be empty");
            isAllCorrect = false;

        }
        if(binding.etMobileSignup.length() == 0){
            binding.etMobileSignup.setError("Can't be empty");
            isAllCorrect = false;

        }
        if(isAllCorrect){
            if(!isValidEmail(binding.etEmailSignup.getText().toString())){
                showToast(this,"Please enter a valid email..");
                return false;
            }
            if(binding.etMobileSignup.length() != 10){
                showToast(this, "Invalid mobile number..");
                return false;
            }
            if(binding.etPasswordSignup.getText() != binding.etRePasswordSignup.getText()){
                showToast(this,"Passwords don't match..");
                return false;
            }

            return true;
        }
        else{
            return false;
        }
    }
    private boolean isValidEmail(String email) {
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return true;
        }
        else{
            return false;
        }
    }


    private void launchLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
    private void launchDashboardActivity() {
        Intent i = new Intent(this, DashboardActivity.class);
        startActivity(i);
        finish();
    }
}