package com.example.medicalappadmin;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappadmin.Models.Additional;
import com.example.medicalappadmin.Models.MetaData;
import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.adapters.AdditionalsRVAdapter;
import com.example.medicalappadmin.adapters.PagesHistoryAdapter;
import com.example.medicalappadmin.components.AudioPlayer;
import com.example.medicalappadmin.components.WebVideoPlayer;
import com.example.medicalappadmin.components.YTVideoPlayer;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.ViewCaseRP;
import com.example.medicalappadmin.databinding.ActivityCaseDetailsBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

public class CaseDetailsActivity extends AppCompatActivity {

    private ActivityCaseDetailsBinding binding;
    String caseId;
    PagesHistoryAdapter adapter;
    GridLayoutManager manager;
    private boolean isFirstItemVisible = true;
    private MediaPlayer mediaPlayer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCaseDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        caseId = getIntent().getStringExtra("CASE_ID");

        if (caseId != null) {
            downloadCase(caseId);
        }
        manager = new GridLayoutManager(CaseDetailsActivity.this, 2);

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
        APIMethods.viewCase(CaseDetailsActivity.this, caseId, new APIResponseListener<ViewCaseRP>() {
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
            if (response.getPatient().getName() != null) {
                binding.tvPatientsName.setText(response.getPatient().getName());
            } else {
                binding.tvPatientsName.setText("Untitled Case");
            }

            binding.tvGender.setText(response.getPatient().getGender());
            binding.tvCaseName.setText(response.getTitle());
            binding.tvDiagnosis.setText(response.getDiagnosis());
            binding.tvLastUpdated.setText(response.getUpdatedAt());
            if(response.getPatient().getMobileNumber() != null && response.getPatient().getMobileNumber() != 0)
                binding.tvMobileNumber.setText(String.valueOf(response.getPatient().getMobileNumber()));
            else binding.tvMobileNumber.setVisibility(View.GONE);
            setUpRCV(response);
            if(response.getAdditionals() != null && response.getAdditionals().size() != 0){
                setUpAdditionalsRCV(response.getAdditionals());
            }

//            binding.llPatient.setOnClickListener(view -> {
//                Intent i = new Intent(this,ActivityViewPatient.class);
//                Log.i(TAG, "updateUI: case id "+ response.get_id());
//                Log.i(TAG, "updateUI: patient id "+ response.getPatient().get_id());
//                i.putExtra("PATIENT_ID",response.get_id());
//                startActivity(i);
//            });

        } else {
            finish();
        }
        binding.shimmerContainer.stopShimmer();
        binding.shimmerContainer.hideShimmer();


    }

    private void setUpAdditionalsRCV(ArrayList<Additional> additionalsList) {
        binding.llAdditionals.setVisibility(View.VISIBLE);
        binding.rcvAdditionals.setLayoutManager(new LinearLayoutManager(this));

        binding.rcvAdditionals.setAdapter(new AdditionalsRVAdapter(additionalsList, CaseDetailsActivity.this, new AdditionalsRVAdapter.AdditionItemListener() {
            @Override
            public void onItemClicked(MetaData metaData,String type, String url) {
                if(Objects.equals(type, "Voice")){
//                   if(!isPlaying){
//                       playAudio(url);
//                   } else {
//                       stopAudio();
//                   }
                    playAudio(url);
                } else if(type.equals("Link")){
                    playVideo(metaData,url);
                }
            }
        }));

    }

    private void playVideo(MetaData metaData, String url) {
        Toast.makeText(CaseDetailsActivity.this, "Loading video. Please wait...", Toast.LENGTH_SHORT).show();
        if(metaData.getMime().equals("link/youtube")){
            YTVideoPlayer ytVideoPlayer = new YTVideoPlayer(CaseDetailsActivity.this);
            Log.i(TAG, "onLinkGuideClicked: url "+url );
            ytVideoPlayer.playVideo(url);
        } else {
            WebVideoPlayer webVideoPlayer = new WebVideoPlayer(CaseDetailsActivity.this);
            webVideoPlayer.playVideo(url);
        }
    }

    private String TAG = "add";

    private void playAudio(String audioUrl) {
        Log.i(TAG, "playAudio: url "+ audioUrl);
//        if(mediaPlayer == null)
//            mediaPlayer = new MediaPlayer();
////        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        try {
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            Log.i(TAG, "playAudio:setting url "+audioUrl);
//            mediaPlayer.setDataSource(audioUrl);
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//            Log.i(TAG, "playAudio: started");
//            Toast.makeText(this, "Playing..", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.i(TAG, "playAudio: error "+ e.getMessage());
//        }
        
//
        AudioPlayer player = new AudioPlayer(CaseDetailsActivity.this);
        player.showAudioPopup(audioUrl);

    }

    public static ViewCaseRP viewCaseRP;

    private void setUpRCV(ViewCaseRP response) {

        binding.rcvPages.setLayoutManager(manager);
        viewCaseRP = response;
//        binding.rcvPages.setLayoutManager(new LinearLayoutManager(PatientDetailedHistoryActivity.this));
        if (adapter == null) {
            adapter = new PagesHistoryAdapter(response, CaseDetailsActivity.this, new PagesHistoryAdapter.PageListener() {
                @Override
                public void onPageClicked(ArrayList<Page> pages, int currentPosition) {
                    Intent i = new Intent(CaseDetailsActivity.this,DetailedPageViewActivity.class);
                    String caseRP = new Gson().toJson(response);
                    i.putExtra("CASE_ID", caseId);
                    i.putExtra("CASE", caseRP);
                    i.putExtra("CURRENT_PAGE_NUMBER", String.valueOf(currentPosition) );
                    startActivity(i);
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