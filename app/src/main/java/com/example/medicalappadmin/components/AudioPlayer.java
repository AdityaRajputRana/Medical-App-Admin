package com.example.medicalappadmin.components;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;

import com.example.medicalappadmin.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class AudioPlayer {

    private Context context;
    private AlertDialog alertDialog;
    private ExoPlayer player;

    public AudioPlayer(Context context) {
        this.context = context;
    }

    public void showAudioPopup(String audioUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.play_voice_popup, null);

        PlayerView playerView = view.findViewById(R.id.playerView);
        ImageButton closeButton = view.findViewById(R.id.closeButton);

        // Initialize ExoPlayer
        player = new ExoPlayer.Builder(context).build();
        playerView.setPlayer(player);

        // Set media source
        MediaItem mediaItem = MediaItem.fromUri(audioUrl);
        player.setMediaItem(mediaItem);
        player.prepare();
//        player.setPlayWhenReady(true);  // Auto-play when ready
        Log.i("add", "showAudioPopup: player ready");
        player.play();

        // Set up close button click listener
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAudioPopup();
            }
        });

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void dismissAudioPopup() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        releasePlayer();
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}

