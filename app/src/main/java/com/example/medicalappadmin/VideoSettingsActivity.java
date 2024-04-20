package com.example.medicalappadmin;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.medicalappadmin.Models.Guide;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.adapters.GuidesAdapter;
import com.example.medicalappadmin.components.WebVideoPlayer;
import com.example.medicalappadmin.components.YTVideoPlayer;
import com.example.medicalappadmin.databinding.ActivityVideoSettingsBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.requests.AddGuideVideoReq;
import com.example.medicalappadmin.rest.requests.SetGuidePosReq;
import com.example.medicalappadmin.rest.response.AddGuideVideoRP;
import com.example.medicalappadmin.rest.response.GuidesVideosRP;
import com.example.medicalappadmin.rest.response.SetGuidePosRP;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class VideoSettingsActivity extends AppCompatActivity {

    private ActivityVideoSettingsBinding binding;
    LinearLayoutManager manager;
    GuidesAdapter adapter;
    GuidesVideosRP guidesVideosRP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        manager = new LinearLayoutManager(this);
        setListeners();
        loadGuides();




    }

    private void setListeners() {
        binding.ivBackBtnVideos.setOnClickListener(view -> finish());
        binding.addGuideBtn.setOnClickListener(view -> {

            showAddVideoBS();


        });
    }

    TextInputEditText etVideoLink;
    TextInputEditText etVideoName;
    TextInputEditText etVideoDesc;
    TextView tvAGActionText;
    AppCompatButton btnBSAGNSubmit;
    ProgressBar pbBSAG;
    BottomSheetDialog addVideoDialog;
    private void showAddVideoBS() {
        addVideoDialog= new BottomSheetDialog(this);
        addVideoDialog.setContentView(R.layout.bsheet_add_guide_video);
        etVideoLink = addVideoDialog.findViewById(R.id.etVideoLink);
        etVideoName = addVideoDialog.findViewById(R.id.etVideoName);
        etVideoDesc = addVideoDialog.findViewById(R.id.etVideoDesc);
        tvAGActionText = addVideoDialog.findViewById(R.id.tvAGActionText);
        btnBSAGNSubmit = addVideoDialog.findViewById(R.id.btnBSAGNSubmit);
        pbBSAG = addVideoDialog.findViewById(R.id.pbBSAG);

        btnBSAGNSubmit.setOnClickListener(view -> {
            if(etVideoName.getText().toString().isEmpty() || etVideoName.getText() == null){
                etVideoName.setError("Required");
                tvAGActionText.setText("All fields are required");
            }
            if(etVideoDesc.getText().toString().isEmpty() || etVideoDesc.getText() == null){
                etVideoDesc.setError("Required");
                tvAGActionText.setText("All fields are required");
            }
            if(etVideoLink.getText().toString().isEmpty() || etVideoLink.getText() == null){
                etVideoLink.setError("Required");
                tvAGActionText.setText("All fields are required");
            }
            if(!etVideoName.getText().toString().isEmpty() && etVideoName.getText() != null
            && !etVideoDesc.getText().toString().isEmpty() && etVideoDesc.getText() != null
            && !etVideoLink.getText().toString().isEmpty() && etVideoLink.getText() != null){
                saveGuide(etVideoName.getText().toString(),etVideoDesc.getText().toString(),etVideoLink.getText().toString());
            }

        });
        addVideoDialog.show();





    }

    private void saveGuide(String name, String desc, String link) {
        AddGuideVideoReq req = new AddGuideVideoReq(name, desc,link);
        pbBSAG.setVisibility(View.VISIBLE);
        tvAGActionText.setText("Saving video...");
        tvAGActionText.setTextColor(getColor(R.color.colorCta));


        APIMethods.addGuideVideo(VideoSettingsActivity.this, req, new APIResponseListener<AddGuideVideoRP>() {
            @Override
            public void success(AddGuideVideoRP response) {
                Toast.makeText(VideoSettingsActivity.this, "Video saved successfully", Toast.LENGTH_SHORT).show();
                loadGuides();
                pbBSAG.setVisibility(View.GONE);
                addVideoDialog.dismiss();


            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                pbBSAG.setVisibility(View.GONE);
                Methods.showError(VideoSettingsActivity.this,message,cancellable);
                Log.i(TAG, "fail: add guide failed");

            }
        });


    }
    private String TAG = "Guide";

    private void loadGuides() {
        Log.i(TAG, "loadGuides: called");
        binding.pbGuides.setVisibility(View.VISIBLE);

        APIMethods.listGuidesVideos(VideoSettingsActivity.this, new APIResponseListener<GuidesVideosRP>() {
            @Override
            public void success(GuidesVideosRP response) {
                guidesVideosRP = response;
                Log.i(TAG, "success: size "+ response.getAllGuides().size());
                for(int i= 0; i<response.getAllGuides().size(); i++){
                    Log.i(TAG, "success: list before"+ new Gson().toJson(response.getAllGuides().get(i).getName()));
                }
                if(response.getAllGuides().isEmpty()){
                    Toast.makeText(VideoSettingsActivity.this, "Add some guides by clicking on + button.", Toast.LENGTH_SHORT).show();
                }
                else{
                    setUI();
                    if(response.getAllGuides().size() > 0){
                        for(int i= 0; i<response.getAllGuides().size(); i++){
                            Log.i(TAG, "success: "+ new Gson().toJson(guidesVideosRP.getAllGuides().get(i).getName()));
                        }
                        setUpRV(guidesVideosRP);
                    }
                }
                Log.i(TAG, "success: loading guides");
                binding.pbGuides.setVisibility(View.GONE);
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                Methods.showError(VideoSettingsActivity.this,message,cancellable);
                binding.pbGuides.setVisibility(View.GONE);
                Log.i(TAG, "fail: load guides failed");

            }
        });
    }

    GuidesVideosRP updatedRP;

    private String getThumbnailUrl(Guide guide){
        String link = guide.getUrl();
        String mime = guide.getMime();
        String type = guide.getType();
        if (type.equals("Link") && mime.equals("link/youtube")) {
            Uri uri = Uri.parse(link);
            String videoId = uri.getQueryParameter("v");
            if (videoId == null || videoId.isEmpty()){
                videoId = link.substring(link.lastIndexOf("/") + 1);
                videoId = videoId.substring(0, videoId.indexOf("?"));
            }
            String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/0.jpg";
            Log.i("thumbUrl", thumbnailUrl);
            return thumbnailUrl;
        }

        return "";

    }


    private void setUI() {

        binding.llFirstGuide.setVisibility(View.VISIBLE);
        binding.tvFirstGuideName.setText(guidesVideosRP.getAllGuides().get(0).getName());
        binding.tvFirstGuideDesc.setText(guidesVideosRP.getAllGuides().get(0).getDescription());
        binding.llFirstGuide.setOnClickListener(view -> playGuideVideo(guidesVideosRP.getAllGuides().get(0)));
        Picasso.get()
                .load(getThumbnailUrl(guidesVideosRP.getAllGuides().get(0)))
                .placeholder(R.drawable.default_care_guide_thumb)
                .into(binding.thumbnailView1);

        if(guidesVideosRP.getAllGuides().size() > 1){
            binding.llSecondGuide.setVisibility(View.VISIBLE);
            binding.tvSecondGuideName.setText(guidesVideosRP.getAllGuides().get(1).getName());
            binding.tvSecondGuideDesc.setText(guidesVideosRP.getAllGuides().get(1).getDescription());
            binding.llSecondGuide.setOnClickListener(view -> {
                Log.i("Size of List", String.valueOf(guidesVideosRP.getAllGuides().size()));
                playGuideVideo(guidesVideosRP.getAllGuides().get(1));
            });
            Picasso.get()
                    .load(getThumbnailUrl(guidesVideosRP.getAllGuides().get(1)))
                    .placeholder(R.drawable.default_care_guide_thumb)
                    .into(binding.thumbnailView2);
        } else {
            binding.llSecondGuide.setVisibility(View.GONE);
        }


        if(guidesVideosRP.getAllGuides().size() > 1){
            Log.i(TAG, "setUI: removing "+ guidesVideosRP.getAllGuides().get(0).getName());
            guidesVideosRP.getAllGuides().remove(0);
            Log.i(TAG, "setUI: remaining size "+ guidesVideosRP.getAllGuides().size());
            Log.i(TAG, "setUI: remaining first element  "+ guidesVideosRP.getAllGuides().get(0).getName());
        }

    }

    private void setGuidePosition(SetGuidePosReq req){
        APIMethods.setGuideVideoPosition(VideoSettingsActivity.this, req, new APIResponseListener<SetGuidePosRP>() {
            @Override
            public void success(SetGuidePosRP response) {
                loadGuides();

            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                Methods.showError(VideoSettingsActivity.this,message,cancellable);
                Log.i(TAG, "fail: set guide position failed" + message);
            }
        });
    }



    private void setUpRV(GuidesVideosRP response) {

        binding.llRCV.setVisibility(View.VISIBLE);
        binding.rcvGuides.setLayoutManager(manager);
        adapter = new GuidesAdapter(response, VideoSettingsActivity.this, new GuidesAdapter.RepositionListener() {
            @Override
            public void onRepositionClicked(int position, String guideId) {
                SetGuidePosReq req = new SetGuidePosReq(guideId,position);
                setGuidePosition(req);
            }
        });
        binding.rcvGuides.setAdapter(adapter);

    }
    private  YTVideoPlayer ytVideoPlayer;
    private  WebVideoPlayer webVideoPlayer;
    private void playGuideVideo(Guide guide) {

        Toast.makeText(VideoSettingsActivity.this,"Loading Video...", Toast.LENGTH_SHORT).show();
        if(guide.getMime().equals("link/youtube")){
            ytVideoPlayer = new YTVideoPlayer(VideoSettingsActivity.this);
            ytVideoPlayer.playVideo(guide.getUrl());
        } else {
            webVideoPlayer = new WebVideoPlayer(VideoSettingsActivity.this);
            webVideoPlayer.playVideo(guide.getUrl());
        }
    }
}