package com.example.medicalappadmin;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappadmin.Models.Additional;
import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.adapters.AdditionalsRVAdapter;
import com.example.medicalappadmin.adapters.PagesHistoryAdapter;
import com.example.medicalappadmin.databinding.ActivityPatientDetailedHistoryBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.ViewCaseRP;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class PatientDetailedHistoryActivity extends AppCompatActivity {

    private ActivityPatientDetailedHistoryBinding binding;
    String caseId;
    PagesHistoryAdapter adapter;
    GridLayoutManager manager;
    private boolean isFirstItemVisible = true;
    private MediaPlayer mediaPlayer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatientDetailedHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        caseId = getIntent().getStringExtra("CASE_ID");

        if (caseId != null) {
            downloadCase(caseId);
        }
        manager = new GridLayoutManager(PatientDetailedHistoryActivity.this, 2);

        binding.rcvPages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && binding.appBarLayout.getHeight() > 0) {
                    binding.appBarLayout.setExpanded(false, true);
                } else if (dy < 0 && binding.appBarLayout.getHeight() == 0) {
                    binding.appBarLayout.setExpanded(true, true);


                }
                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
                if (firstVisibleItemPosition == 0) {
                    if (!isFirstItemVisible) {
                        binding.appBarLayout.setExpanded(true, true);
                        isFirstItemVisible = true;
                    }
                } else {
                    isFirstItemVisible = false;
                }
            }
        });




        binding.ivPatientDetailsBackBtn.setOnClickListener(view -> {
            finish();
        });

    }

    private void downloadCase(String caseId) {

        Log.i("add", "downloadCase: id "+ caseId);
        APIMethods.viewCase(PatientDetailedHistoryActivity.this, caseId, new APIResponseListener<ViewCaseRP>() {
            @Override
            public void success(ViewCaseRP response) {
                updateUI(response);
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                binding.shimmerContainer.stopShimmer();
                binding.shimmerContainer.hideShimmer();

            }
        });


    }

    private void updateUI(ViewCaseRP response) {

        if (response != null) {
            if (response.getPatient().getFullName() != null) {
                binding.tvPatientsName.setText(response.getPatient().getFullName());
            } else {
                binding.tvPatientsName.setText("Untitled Case");

            }
            binding.tvGender.setText(response.getPatient().getGender());
            binding.tvCaseName.setText(response.getTitle());
            binding.tvDiagnosis.setText(response.getDiagnosis());
            binding.tvLastUpdated.setText(response.getUpdatedAt());
            setUpRCV(response);
            setUpAdditionalsRCV(response.getAdditionals());

        } else {
            finish();
        }
        binding.shimmerContainer.stopShimmer();
        binding.shimmerContainer.hideShimmer();


    }

    private void setUpAdditionalsRCV(ArrayList<Additional> additionalsList) {

        binding.rcvAdditionals.setLayoutManager(new LinearLayoutManager(this));

        binding.rcvAdditionals.setAdapter(new AdditionalsRVAdapter(additionalsList, PatientDetailedHistoryActivity.this, new AdditionalsRVAdapter.AdditionItemListener() {
            @Override
            public void onItemClicked(String type, String url) {
                if(Objects.equals(type, "Voice")){
                   if(!isPlaying){
                       playAudio(url);
                   } else {
                       stopAudio();
                   }
                } else if(Objects.equals(type,"Video")){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "video/*");
                    startActivity(intent);
                }
            }
        }));

    }

    private String TAG = "add";
    private boolean isPlaying = false;

    private void playAudio(String audioUrl) {
        Log.i(TAG, "playAudio: url "+ audioUrl);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Playing..", Toast.LENGTH_SHORT).show();
        isPlaying = true;
    }

    private void stopAudio() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            Toast.makeText(PatientDetailedHistoryActivity.this, "Audio has been stopped", Toast.LENGTH_SHORT).show();
            isPlaying = false;

        } else {
            Toast.makeText(PatientDetailedHistoryActivity.this, "Audio has not played", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpRCV(ViewCaseRP response) {

        binding.rcvPages.setLayoutManager(manager);
//        binding.rcvPages.setLayoutManager(new LinearLayoutManager(PatientDetailedHistoryActivity.this));
        if (adapter == null) {
            adapter = new PagesHistoryAdapter(response, PatientDetailedHistoryActivity.this, new PagesHistoryAdapter.PageListener() {
                @Override
                public void onPageClicked(ArrayList<Page> pages, int currentPosition) {
                    Intent i = new Intent(PatientDetailedHistoryActivity.this,DetailedPageViewActivity.class);
                    String response = new Gson().toJson(pages);
//                    i.putExtra("PAGES_LIST", response );
                    i.putExtra("CASE_ID", caseId);
                    i.putExtra("CURRENT_PAGE_NUMBER", String.valueOf(currentPosition) );
                    Log.i("lol", "sent: " + currentPosition);
                    startActivity(i);
//                    Toast.makeText(PatientDetailedHistoryActivity.this, "Clicked " + pageNumber, Toast.LENGTH_SHORT).show();
                }
            });
        }
        binding.rcvPages.setAdapter(adapter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}