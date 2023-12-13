package com.example.medicalappadmin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappadmin.Models.Guide;
import com.example.medicalappadmin.R;
import com.example.medicalappadmin.rest.response.GuidesVideosRP;

import java.util.ArrayList;

public class GuidesAdapter extends RecyclerView.Adapter<GuidesAdapter.ViewHolder> {

    GuidesVideosRP guidesList;
    Context context;
    TextView tvGuideDesc;
    TextView tvGuideName;
    TextView tvGuidePosition;

    public GuidesAdapter(GuidesVideosRP guidesList, Context context) {
        this.guidesList = guidesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.guides_item,parent,false);
        tvGuideName = view.findViewById(R.id.tvGuideName);
        tvGuidePosition = view.findViewById(R.id.tvGuidePosition);
        tvGuideDesc = view.findViewById(R.id.tvGuideDesc);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        tvGuidePosition.setText(String.valueOf(guidesList.getAllGuides().get(position).getPosition()));
        tvGuideName.setText(guidesList.getAllGuides().get(position).getName());
        tvGuideDesc.setText(guidesList.getAllGuides().get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return guidesList.getAllGuides().size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
