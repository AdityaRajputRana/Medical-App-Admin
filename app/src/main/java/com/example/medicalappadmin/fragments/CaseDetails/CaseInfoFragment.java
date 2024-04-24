package com.example.medicalappadmin.fragments.CaseDetails;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.medicalappadmin.ActivityPatientDetails;
import com.example.medicalappadmin.Models.MetaData;
import com.example.medicalappadmin.R;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.adapters.AdditionalsRVAdapter;
import com.example.medicalappadmin.components.AudioPlayer;
import com.example.medicalappadmin.components.WebVideoPlayer;
import com.example.medicalappadmin.components.YTVideoPlayer;
import com.example.medicalappadmin.databinding.FragmentCaseInfoBinding;
import com.example.medicalappadmin.rest.response.ViewCaseRP;

import java.util.Objects;


public class CaseInfoFragment extends Fragment {

    FragmentCaseInfoBinding binding;
    ViewCaseRP viewCaseRP;


    public void setUI(ViewCaseRP res) {
        viewCaseRP = res;
        updateUI();
    }

    private void updateUI() {
        binding.progressBar.setVisibility(View.GONE);

        String name = viewCaseRP.getPatient().getFullName();
        binding.patientNameTxt.setText(name);

        String details = "";
        details += viewCaseRP.getPatient().getGenderFull();
        details += " | ";
        details += viewCaseRP.getPatient().getAge();
        binding.patientDetailsTxt.setText(details);
        binding.patientDetailsLayout.setVisibility(View.VISIBLE);

        //Case Info
        binding.caseDetailsLayout.setVisibility(View.VISIBLE);
        binding.caseTitleTxt.setText(viewCaseRP.getTitle());
        String[] dateTime = Methods.splitDateTime(viewCaseRP.getUpdatedAt());
        binding.lastUpdateDateTxt.setText(dateTime[0]);
        binding.lastUpdateTime.setText(dateTime[1]);

        //Additional
        binding.additionalLayout.setVisibility(View.VISIBLE);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        AdditionalsRVAdapter adapter = new AdditionalsRVAdapter(viewCaseRP.getAdditionals(), getActivity(), new AdditionalsRVAdapter.AdditionItemListener() {
            @Override
            public void onItemClicked(MetaData metaData, String type, String url) {
                playAdditional(metaData, type, url);
            }
        });
        binding.additionalRcv.setAdapter(adapter);
        binding.additionalRcv.setLayoutManager(manager);
    }

    private void playAdditional(MetaData metaData, String type, String url) {
        if(Objects.equals(type, "Voice")){
            playAudio(url);
        } else if(type.equals("Link")){
            playVideo(metaData,url);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding == null){
            binding = FragmentCaseInfoBinding.inflate(inflater);
            setListeners();
        }
        return binding.getRoot();
    }

    private void setListeners() {
        if (binding == null) return;
        binding.patientDetailsLayout.setOnClickListener(view->{
            if(viewCaseRP == null) return;
            Intent i = new Intent(getActivity(), ActivityPatientDetails.class);
            i.putExtra("PATIENT_ID",viewCaseRP.getPatient().get_id());
            getActivity().startActivity(i);
        });
    }


    private void playVideo(MetaData metaData, String url) {
        Toast.makeText(getActivity(), "Loading video. Please wait...", Toast.LENGTH_SHORT).show();
        if(metaData.getMime().equals("link/youtube")){
            YTVideoPlayer ytVideoPlayer = new YTVideoPlayer(getActivity());
            ytVideoPlayer.playVideo(url);
        } else {
            WebVideoPlayer webVideoPlayer = new WebVideoPlayer(getActivity());
            webVideoPlayer.playVideo(url);
        }
    }

    private void playAudio(String audioUrl) {
        AudioPlayer player = new AudioPlayer(getActivity());
        player.showAudioPopup(audioUrl);
    }

}