package com.example.medicalappadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String jwt = SplashScreen.this.getSharedPreferences("MY_PREF", MODE_PRIVATE)
                .getString("JWT_TOKEN","");

        if(!jwt.equals("")){
            startActivity(new Intent(this, DashboardActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();

    }
}