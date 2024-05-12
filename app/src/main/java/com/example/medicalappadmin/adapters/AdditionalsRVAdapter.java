package com.example.medicalappadmin.adapters;


import android.content.Context;
import android.content.Intent;
import android.health.connect.datatypes.Metadata;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappadmin.Models.Additional;
import com.example.medicalappadmin.Models.MetaData;
import com.example.medicalappadmin.R;

import java.util.ArrayList;
import java.util.Objects;

public class AdditionalsRVAdapter extends RecyclerView.Adapter<AdditionalsRVAdapter.ViewHolder> {

    Context context;
    AdditionItemListener listener;
    TextView tvAdditionalHeading;
    TextView tvAdditionalDescription;
    ImageView ivAdditionalIcon;
    LinearLayout llAdditional;
    ArrayList<Additional> response;

    public AdditionalsRVAdapter(ArrayList<Additional> response, Context context, AdditionItemListener listener) {
        this.response = response;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.additional_item, parent, false);
        tvAdditionalHeading = view.findViewById(R.id.tvHeadingOfAdditional);
        tvAdditionalDescription = view.findViewById(R.id.tvDescOfAdditional);
        llAdditional = view.findViewById(R.id.llAdditional);
        ivAdditionalIcon = view.findViewById(R.id.ivAdditionalIcon);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        tvAdditionalHeading.setText(response.get(position).getDetails().getName());
        Log.i("add", "onBindViewHolder: " + response.get(position).getMetaData().getType());
        if (Objects.equals(response.get(position).getMetaData().getType(), "Voice")) {
            ivAdditionalIcon.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.baseline_audiotrack_24));
            tvAdditionalDescription.setText("VOICE");
        } else if (Objects.equals(response.get(position).getMetaData().getType(), "Link")) {
            tvAdditionalDescription.setText("VIDEO");
            ivAdditionalIcon.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.baseline_videocam_24));
        } else if (response.get(position).getMetaData().getMime().startsWith("image")){
            tvAdditionalDescription.setText("IMAGE");
            ivAdditionalIcon.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.baseline_photo_24));
        } else if (response.get(position).getMetaData().getMime().equals("application/pdf")){
            tvAdditionalDescription.setText("PDF");
            ivAdditionalIcon.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.baseline_picture_as_pdf_24));
        } else {
            ivAdditionalIcon.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.baseline_info_24));
        }

        tvAdditionalDescription.setText(response.get(position).getMetaData().getType().toUpperCase());



        llAdditional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(response.get(position).getMetaData().getType(), "Voice")) {
                    listener.onItemClicked(response.get(position).getMetaData(),"Voice", response.get(position).getPublicUrl());
                } else if (Objects.equals(response.get(position).getMetaData().getType(), "Link")) {
                    listener.onItemClicked(response.get(position).getMetaData(),"Link", response.get(position).getPublicUrl());
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(response.get(position).getPublic_url()), response.get(position).getMetaData().getMime());

                    context.startActivity(intent);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return response.size();
    }

    public interface AdditionItemListener {
        default void onItemClicked(MetaData metaData,String type, String url) {
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
