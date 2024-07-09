package com.example.medicalappadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.medicalappadmin.PenDriver.SmartPenDriver;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.databinding.ActivityPageConfigBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.VolleyClient;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.PageConfigMetadataRP;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class PageConfigActivity extends AppCompatActivity {

    ActivityPageConfigBinding binding;
    SharedPreferences preferences;
    Integer activeConfigVersion = 0;
    PageConfigMetadataRP pageConfigMetadata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPageConfigBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = this.getSharedPreferences("CONFIG_PREFS", MODE_PRIVATE);
        updateVisibleData();
        setUpListeners();
    }

    private void updateVisibleData() {
        Boolean isCustomConfig = preferences.getBoolean("isCustomPageConfig", false);
        ArrayList<String> details = Lists.newArrayList();
        if (isCustomConfig) {
            String docId = preferences.getString("doctorId", "No Doctor ID Set");
            String configId = preferences.getString("pageConfigId", "No Configuration ID Set");
            activeConfigVersion = preferences.getInt("pageConfigVersion", 0);
            String lastUpdated = preferences.getString("pageConfigLastUpdated", "No Last Updated Date Set");
            details.add("Doctor ID: " + docId);
            details.add("Configuration ID: " + configId);
            details.add("Configuration Version: " + activeConfigVersion);
            details.add("Last Updated: " + lastUpdated);
        } else {
            details.add("No Custom Configuration is Set");
            details.add("Default Configuration is being used");
        }
        details.add("You can press Check for Updates Button to fetch the latest configurations for your account");
        binding.configDetailsBodyTxt.setText(String.join("\n", details));
    }

    private void setUpListeners() {
        binding.actionBtn.setOnClickListener(v -> performAction());
        binding.ivBackBtnVideos.setOnClickListener(v -> finish());
    }

    int state = 0;
    private void performAction() {
        if (state == 0) {
            fetchConfigMetadataFromServer();
        } else {
            downloadAndUpdateConfig();
        }
    }

    private void downloadAndUpdateConfig() {
        if (pageConfigMetadata == null) {
            Toast.makeText(this, "No Config Metadata Found", Toast.LENGTH_SHORT).show();
            state = 1;
            updateVisibleData();
            return;
        }

        startProgress();
        binding.activeProcessTitle.setText("Updating Config");
        binding.activeProcessBody.setText("Downloading Configurations from Server");
        StringRequest downloadRequest = new StringRequest(Request.Method.GET, pageConfigMetadata.getConfigUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        saveJson(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stopProgress();
                binding.configDetailsBodyTxt.setText("Error in Downloading Config: " + error.getMessage());
                Methods.showError(PageConfigActivity.this, "Error in Downloading Config", true);
            }
        });

        VolleyClient.getRequestQueue().add(downloadRequest);

    }

    private void saveJson(String response) {
        binding.activeProcessBody.setText("Download Complete, Saving Configurations");
        File file = new File(this.getFilesDir(), "pageConfig.json");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isSaved = false;
                Exception exception = null;
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(response.getBytes());
                    saveMetadata();
                    isSaved = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    exception =  e;
                }

                boolean finalIsSaved = isSaved;
                Exception finalException = exception;
                runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       stopProgress();
                       if (finalIsSaved) {
                           binding.configTitle.setText("Configuration Updated Successfully, Restart App if changes are not parsed in first go!");
                           binding.actionBtn.setVisibility(View.GONE);
                           updateVisibleData();
                       } else {
                           binding.configDetailsBodyTxt.setText("Error in Saving Configurations: " + finalException.getMessage());
                           Methods.showError(PageConfigActivity.this, "Error in Saving Configurations", true);
                       }
                   }
               });
            }
        });

        thread.start();

    }

    private void saveMetadata() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isCustomPageConfig", true);
        editor.putString("doctorId", pageConfigMetadata.getDoctorId());
        editor.putString("pageConfigId", pageConfigMetadata.get_id());
        editor.putInt("pageConfigVersion", pageConfigMetadata.get__v());
        editor.putString("pageConfigLastUpdated", new Date().toString());
        editor.apply();
    }

    private void fetchConfigMetadataFromServer() {
        startProgress();
        APIMethods.getPageConfigMetadata(this, new APIResponseListener<PageConfigMetadataRP>() {
            @Override
            public void success(PageConfigMetadataRP response) {
                stopProgress();
                if (response.get__v() > activeConfigVersion) {
                    pageConfigMetadata = response;
                    state = 1;
                    binding.actionBtn.setText("Download and Update Config");
                    binding.actionBtn.setIcon(getDrawable(R.drawable.baseline_system_update_alt_24));
                    binding.configTitle.setText("New Configuration Available");
                    binding.configDetailsBodyTxt.setText("New Update is available for your account. Press the button below to download and update your configuration.");
                } else {
                    binding.actionBtn.setVisibility(View.GONE);
                    binding.activeProcessTitle.setText("No New Configuration Available");
                    binding.activeProcessTitle.setVisibility(View.VISIBLE);
                    Toast.makeText(PageConfigActivity.this, "Already Using the latest version", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                stopProgress();
                Methods.showError(PageConfigActivity.this, message, cancellable);
            }
        });
    }

    private void startProgress(){
        binding.actionBtn.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.activeProcessTitle.setVisibility(View.VISIBLE);
        binding.activeProcessBody.setVisibility(View.VISIBLE);
    }

    private void stopProgress(){
        binding.actionBtn.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
        binding.activeProcessTitle.setVisibility(View.GONE);
        binding.activeProcessBody.setVisibility(View.GONE);
    }
}