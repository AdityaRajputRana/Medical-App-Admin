package com.example.medicalappadmin.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.medicalappadmin.Models.Guide;
import com.example.medicalappadmin.PrescriptionActivity;
import com.example.medicalappadmin.R;
import com.example.medicalappadmin.components.WebVideoPlayer;
import com.example.medicalappadmin.components.YTVideoPlayer;
import com.example.medicalappadmin.rest.response.GuidesVideosRP;
import com.squareup.picasso.Picasso;

public class GuidesAdapter extends RecyclerView.Adapter<GuidesAdapter.ViewHolder> {

    GuidesVideosRP guidesList;
    Context context;
    TextView tvGuideDesc;
    TextView tvGuideName;
    ImageView ivPositionGuides;
    RepositionListener listener;
    LinearLayout llGuide;


    public GuidesAdapter(GuidesVideosRP guidesList, Context context,RepositionListener listener) {
        this.guidesList = guidesList;
        this.context = context;
        this.listener =listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.guides_item, parent, false);
        tvGuideName = view.findViewById(R.id.tvGuideName);
        tvGuideDesc = view.findViewById(R.id.tvGuideDesc);
        ivPositionGuides = view.findViewById(R.id.ivPosition);
        llGuide= view.findViewById(R.id.llGuide);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        tvGuideName.setText(guidesList.getAllGuides().get(position).getName());
        tvGuideDesc.setText(guidesList.getAllGuides().get(position).getDescription());

        ivPositionGuides.setOnClickListener(view -> {
            showPopupMenu(view, guidesList.getAllGuides().get(position));
        });

        llGuide.setOnClickListener(view -> {
            playGuideVideo(guidesList.getAllGuides().get(position));
        });

        String link = guidesList.getAllGuides().get(position).getUrl();
        String mime = guidesList.getAllGuides().get(position).getMime();
        String type = guidesList.getAllGuides().get(position).getType();
        if (type.equals("Link") && mime.equals("link/youtube")) {
            Uri uri = Uri.parse(link);
            String videoId = uri.getQueryParameter("v");
            if (videoId == null || videoId.isEmpty()){
                videoId = link.substring(link.lastIndexOf("/") + 1);
                videoId = videoId.substring(0, videoId.indexOf("?"));
            }
            String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/0.jpg";
            Log.i("thumbnailUrl", thumbnailUrl);
            Picasso.get()
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.default_care_guide_thumb)
                    .into(holder.thumbnailView);
        } else {
            Picasso.get().load(R.drawable.default_care_guide_thumb).into(holder.thumbnailView);
        }

    }
    private  YTVideoPlayer ytVideoPlayer;
    private  WebVideoPlayer webVideoPlayer;
    private void playGuideVideo(Guide guide) {

        Toast.makeText(context,"Loading Video...", Toast.LENGTH_SHORT).show();
        if(guide.getMime().equals("link/youtube")){
            ytVideoPlayer = new YTVideoPlayer(context);
            ytVideoPlayer.playVideo(guide.getUrl());
        } else {
            webVideoPlayer = new WebVideoPlayer(context);
            webVideoPlayer.playVideo(guide.getUrl());
        }
    }

    @Override
    public int getItemCount() {
        return guidesList.getAllGuides().size();
    }

    private void showPopupMenu(View view, Guide guide) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.popup_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.setAs1) {
                    listener.onRepositionClicked(1,guide.get_id());
                } else {
                    listener.onRepositionClicked(2,guide.get_id());
                }
                return true;
            }
        });

        popupMenu.show();
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public interface RepositionListener {
        void onRepositionClicked(int position, String guideId);
//        void playGuideClicked(Guide guide);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailView = itemView.findViewById(R.id.thumbnailView);
        }
    }
}
