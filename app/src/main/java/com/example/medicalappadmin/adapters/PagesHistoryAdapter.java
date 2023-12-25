package com.example.medicalappadmin.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.R;
import com.example.medicalappadmin.canvas.PageViewRV;
import com.example.medicalappadmin.rest.response.ViewCaseRP;

import java.util.ArrayList;

public class PagesHistoryAdapter extends RecyclerView.Adapter<PagesHistoryAdapter.MyViewHolder> {
    ViewCaseRP viewCaseRP;
    Context context;
    PageListener listener;




    public PagesHistoryAdapter(ViewCaseRP viewCaseRP, Context context, PageListener listener) {
        this.viewCaseRP = viewCaseRP;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pages_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.notepadView.addCoordinates(viewCaseRP.getPages().get(position).getPoints());
        holder.pageCount.setText("Page no: " + viewCaseRP.getPages().get(position).getPageNumber());
        holder.llPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("lol", "onClick: " + viewCaseRP.getPages().get(holder.getAdapterPosition()).getPageNumber());
                Log.i("lol", "onClick pos: " + holder.getAdapterPosition());
                listener.onPageClicked(viewCaseRP.getPages(),holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return viewCaseRP.getPages().size();
    }

    public interface PageListener {
        default void onPageClicked(ArrayList<Page> pages, int currentposition) {


        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        PageViewRV notepadView;
        TextView pageCount;
        LinearLayout llPage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.notepadView = itemView.findViewById(R.id.rcvNotepadView);
            this.pageCount = itemView.findViewById(R.id.tvPageCount);
            this.llPage = itemView.findViewById(R.id.llPage);
        }
    }
}
