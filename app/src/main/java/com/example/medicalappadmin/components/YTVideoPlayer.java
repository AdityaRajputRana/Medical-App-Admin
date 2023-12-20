package com.example.medicalappadmin.components;


import com.example.medicalappadmin.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class YTVideoPlayer {

    private Dialog youtubePopupDialog;
    private YouTubePlayerView youTubePlayerView;
    private TextView tvError;
    private Context context;

    public YTVideoPlayer(Context context) {
        this.context = context;
        youtubePopupDialog = new Dialog(context);
        youtubePopupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        youtubePopupDialog.setContentView(R.layout.yt_video_popup_layout);
        youtubePopupDialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );

        youTubePlayerView = youtubePopupDialog.findViewById(R.id.youtubePlayerView);
        tvError = youtubePopupDialog.findViewById(R.id.tvError);
        ImageButton btnClose = youtubePopupDialog.findViewById(R.id.btnClose);

        // Set a listener to close the popup when the close button is clicked
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopup();
            }
        });
    }

    private String extractVideoId(String youtubeUrl) {
        String videoId = null;

        if (youtubeUrl.contains("youtu.be/")) {
            int startIndex = youtubeUrl.indexOf("youtu.be/") + 9;
            int endIndex = youtubeUrl.indexOf("?", startIndex);
            if (endIndex == -1) {
                endIndex = youtubeUrl.length();
            }
            videoId = youtubeUrl.substring(startIndex, endIndex);
        } else if (youtubeUrl.contains("v=")) {
            int startIndex = youtubeUrl.indexOf("v=") + 2;
            int endIndex = youtubeUrl.indexOf("&", startIndex);
            if (endIndex == -1) {
                endIndex = youtubeUrl.length();
            }
            videoId = youtubeUrl.substring(startIndex, endIndex);
        }

        return videoId;
    }

    public void playVideo(String videoUrl) {
        String videoId = extractVideoId(videoUrl);

        if (videoId != null) {
            youTubePlayerView.setVisibility(View.VISIBLE);
            tvError.setVisibility(View.GONE);
            youTubePlayerView.getYouTubePlayerWhenReady(youTubePlayer -> {
                youTubePlayer.loadVideo(videoId, 0);
                youtubePopupDialog.show();
            });
        } else {
            // Handle invalid URL or unable to extract video ID
            youTubePlayerView.setVisibility(View.INVISIBLE);

            tvError.setVisibility(View.VISIBLE);
            youtubePopupDialog.show();
        }
    }

    public void dismissPopup() {
        if (youtubePopupDialog != null && youtubePopupDialog.isShowing()) {
            youtubePopupDialog.dismiss();
        }
    }
}
