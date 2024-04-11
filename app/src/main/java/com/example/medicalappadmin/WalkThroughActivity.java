package com.example.medicalappadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.medicalappadmin.Tools.Const;
import com.example.medicalappadmin.databinding.ActivityWalkThroughBinding;

public class WalkThroughActivity extends AppCompatActivity {

    ActivityWalkThroughBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWalkThroughBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.getStartedBtn.setOnClickListener(view -> startLoginActivity());
    }

    private void startLoginActivity() {
        this.getSharedPreferences("MY_PREF", MODE_PRIVATE)
                .edit()
                .putBoolean(Const.isWalkThroughComplete, true)
                .apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}