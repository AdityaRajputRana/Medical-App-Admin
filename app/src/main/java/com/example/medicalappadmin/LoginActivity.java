package com.example.medicalappadmin;

import static com.example.medicalappadmin.Tools.Methods.showToast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.databinding.ActivityLoginBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.LoginRP;
import com.google.gson.Gson;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Methods.setStatusBarColor(getColor(R.color.colorCta),LoginActivity.this);
        binding.etPasswordLogin.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                binding.passInputLayout.setHint("");
            } else {
                binding.passInputLayout.setHint("Password");
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(initialChecks()){
                    showPB();
                    String email = binding.etEmailLogin.getText().toString();
                    String password = binding.etPasswordLogin.getText().toString();

                    APIMethods.loginWithEmailAndPassword(LoginActivity.this, email, password, new APIResponseListener<LoginRP>() {
                        @Override
                        public void success(LoginRP response) {
                            hidePB();
                            showToast(LoginActivity.this,"Login Successful");
                            SharedPreferences.Editor editor = LoginActivity.this.
                                    getSharedPreferences("MY_PREF", MODE_PRIVATE)
                                    .edit();
                            editor.putString("JWT_TOKEN", response.getJwt());
                            editor.putString("MY_USER", new Gson().toJson(response.getUser()));
                            editor.commit();
                            Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                            hidePB();
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
        finish();
    }

    private void showPB(){
        binding.pbLogin.setVisibility(View.VISIBLE);
    }
    private void hidePB(){
        binding.pbLogin.setVisibility(View.GONE);
    }



}