package com.example.medicalappadmin.adapters;

import android.content.Context;
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

import com.example.medicalappadmin.Models.Guide;
import com.example.medicalappadmin.R;
import com.example.medicalappadmin.rest.response.GuidesVideosRP;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class OtherGuidesAdapterBS extends RecyclerView.Adapter<OtherGuidesAdapterBS.ViewHolder> {

    ArrayList<Guide> guidesList;
    Context context;
    TextView tvBSGuideDesc;
    TextView tvBSGuideName;
    LinearLayout llBSOtherGuide;
    GuideLinkListener listener;

    public OtherGuidesAdapterBS(ArrayList<Guide> guidesList, Context context, GuideLinkListener listener) {
        this.guidesList = guidesList;
        this.context = context;
        this.listener =listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bs_guides_item, parent, false);
        tvBSGuideName = view.findViewById(R.id.tvBSGuideName);
        tvBSGuideDesc = view.findViewById(R.id.tvBSGuideDesc);
        llBSOtherGuide = view.findViewById(R.id.llBSOtherGuide);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        tvBSGuideName.setText(guidesList.get(position).getName());
        tvBSGuideDesc.setText(guidesList.get(position).getDescription());

        llBSOtherGuide.setOnClickListener(view -> {
            listener.onLinkGuideClicked(guidesList.get(position));
        });


    }

    @Override
    public int getItemCount() {
        return guidesList.size();
    }



    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public interface GuideLinkListener {
        default void onLinkGuideClicked(Guide guide) {
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
