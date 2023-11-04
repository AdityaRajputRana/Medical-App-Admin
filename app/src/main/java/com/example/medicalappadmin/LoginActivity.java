package com.example.medicalappadmin;

import static com.example.medicalappadmin.Tools.Methods.showToast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import com.example.medicalappadmin.databinding.ActivityLoginBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.LoginRP;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(initialChecks()){
                    String email = binding.etEmailLogin.getText().toString();
                    String password = binding.etPasswordLogin.getText().toString();

                    APIMethods.loginWithEmailAndPassword(LoginActivity.this, email, password, new APIResponseListener<LoginRP>() {
                        @Override
                        public void success(LoginRP response) {
                            showToast(LoginActivity.this,"Login Successful");
                            Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(i);
                        }

                        @Override
                        public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                            showToast(LoginActivity.this,message);
                            Log.i("ADI", "fail: "+ message);
                        }
                    });


                }
            }
        });

        binding.tvToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSignupScreen();
            }
        });
    }

    private boolean initialChecks() {
        if(binding.etEmailLogin.getText() == null){
            binding.etEmailLogin.setError("Can't be empty");
            return false;
        }
        else if(binding.etPasswordLogin.getText() == null){
            binding.etEmailLogin.setError("Can't be empty");
            return false;
        }
        else if(binding.etEmailLogin.getText() == null && binding.etPasswordLogin.getText() == null){
            binding.etEmailLogin.setError("Can't be empty");
            binding.etPasswordLogin.setError("Can't be empty");
            return false;

        }
        else if(!isValidEmail(binding.etEmailLogin.getText().toString())){
            showToast(this,"Please enter valid email");
            return false;
        }
        else{
            return true;
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

    private void launchSignupScreen() {
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
    }


}